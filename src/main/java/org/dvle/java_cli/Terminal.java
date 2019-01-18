package org.dvle.java_cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * This class is an implementation of a mechanism for user communication.
 * 
 * @author federicosilvestri
 */

public class Terminal implements Runnable {

	/**
	 * A set that contains all listeners.
	 */
	private final Set<CommandListener> listeners;

	/**
	 * Input stream of terminal. It is used when an user input is required.
	 */
	private final InputStream input;

	/**
	 * Output stream of terminal. It is used when we need to send information in the
	 * terminal.
	 */
	private final PrintStream output;

	/**
	 * Variable that indicates the stop of terminal.
	 */
	private boolean stop;

	/**
	 * Exit command description.
	 */
	private final ExitCommandDescription exitCommand;

	/**
	 * Prompt ansi code.
	 */
	private Ansi prompt;

	/**
	 * A map between the command name and the command description.
	 */
	private final Map<String, CommandDescription> commands;

	/**
	 * Create a new terminal.
	 * 
	 * @param input  the input stream
	 * @param output the output stream
	 * @param ecd    the exit command description
	 */
	public Terminal(InputStream input, PrintStream output, ExitCommandDescription ecd) {
		listeners = new TreeSet<CommandListener>();

		if (input == null || output == null || ecd == null) {
			throw new NullPointerException();
		}

		this.input = input;
		this.output = AnsiConsole.wrapSystemOut(output);
		this.exitCommand = ecd;
		this.commands = new TreeMap<>();
		this.prompt = Settings.PROMPT;
	}

	/**
	 * Create a new terminal specifying the exit command
	 * 
	 * @param ecd    the exit command description
	 */
	protected Terminal(ExitCommandDescription ecd) {
		this(System.in, System.out, ecd);
	}

	/**
	 * Create a new terminal with standard I/O parameters.
	 */
	public Terminal() {
		this(System.in, System.out, ExitCommandDescription.DEFAULT);
	}

	/**
	 * Register a command listener object to this terminal.
	 * 
	 * @param cl command listener instance
	 */
	public void addListener(CommandListener cl) {
		if (cl == null) {
			throw new NullPointerException();
		}

		listeners.add(cl);
	}

	/**
	 * Remove a command listener object from this terminal.
	 * 
	 * @param cl command listener instance
	 */
	public void removeListener(CommandListener cl) {
		if (cl == null) {
			throw new NullPointerException();
		}

		if (!listeners.contains(cl)) {
			throw new RuntimeException("The object you have requested to deregister is not registered!");
		}

		listeners.remove(cl);
	}

	/**
	 * Add a command to this terminal.
	 * 
	 * @param cd command description
	 */
	public void addCommand(CommandDescription cd) {
		if (cd == null) {
			throw new NullPointerException();
		}

		if (commands.containsKey(cd.name)) {
			throw new RuntimeException("You cannot add more command with the same name!");
		}

		commands.put(cd.name, cd);
	}

	/**
	 * Run the terminal.
	 */
	@Override
	public void run() {
		stop = false;
		Scanner inputScanner = new Scanner(input);

		// printing the startup text
		output.println(Settings.STARTUP_TEXT);

		// CLI iteration
		do {
			output.print(prompt);
			String line = inputScanner.nextLine();

			if (line == null || line.length() < 1) {
				continue;
			}

			try {
				parseLine(line);
			} catch (CommandExecutionException e) {
				e.printStackTrace();
			}

		} while (!stop);

		// say goodbye to user
		output.println(Settings.STOP_TEXT);

		// close the scanner to avoid resource leak
		inputScanner.close();
	}

	private void parseLine(String line) throws CommandExecutionException {
		assert (line != null);
		assert (line.length() > 0);

		// sanitizing the line
		line = line.trim();

		// first detect the command
		CommandInvocation commandInvocation;
		try {
			commandInvocation = detectCommand(line);
		} catch (CommandSyntaxError e) {
			// syntax error
			output.println(e.getLocalizedMessage());
			return;
		} catch (ArgumentParseException e) {
			// error during parsing
			output.println(e.getLocalizedMessage());
			return;
		}

		// check if command is found or not
		if (commandInvocation == null) {
			output.println(Settings.COMMAND_NOT_FOUND);
			return;
		}

		if (commandInvocation.command instanceof ExitCommandDescription) {
			// check if the stop command is received
			stop = true;
		}

		// prepare the execution environment
		ExecutionEnvironment exe = new ExecutionEnvironment(this, commandInvocation, output, input);

		/*
		 * Now we have all object to start the execution
		 */
		commandInvocation.command.runCommand(commandInvocation.arguments, exe);
	}

	/**
	 * This method detects the command and returns the related CommandInvocation.
	 * 
	 * @param line the line where executes the search
	 * @return if method finds the command returns the command invocation object,
	 *         else returns null
	 * @throws CommandSyntaxError     in case of during argument parsing any errors
	 *                                occur
	 * @throws ArgumentParseException in case of exception during parsing
	 */
	private CommandInvocation detectCommand(String line) throws CommandSyntaxError, ArgumentParseException {
		assert (line != null);
		assert (line.length() > 0);

		// extract the command from the line
		String command = extractCommand(line);
		CommandDescription commandDescription = null;

		// check if is the exit command
		if (exitCommand.isCalled(line)) {
			commandDescription = exitCommand;
		} else {
			/*
			 * search command on map, if the map fails to find the command, get method will
			 * return null.
			 */
			commandDescription = commands.get(command);
		}

		if (commandDescription == null) {
			return null;
		}

		CommandInvocation commandInvocation = new CommandInvocation(commandDescription);

		/*
		 * Checking the arguments of the command
		 */
		// extract the argument string from line
		String argumentString = line.replaceFirst(command, "");
		// parse argument (not eval it) (it can throws exception due to bad syntax)
		Map<String, String> argumentMap = extractArguments(argumentString);
		// check if command has arguments
		if (commandDescription.arguments.size() > 0) {
			// we need to parse it
			parseArguments(argumentMap, commandInvocation);
		} else {
			if (argumentMap.size() > 0) {
				/*
				 * user has passed arguments, but command does not accept it
				 */
				throw new CommandSyntaxError("This command does not accept arguments!");
			}
		}

		return commandInvocation;
	}

	/**
	 * This method tries to extract the command from a line. This method must not
	 * fail, because the line is != null and the {@literal length is > 0}. It returns the
	 * command name without the space.
	 * 
	 * For example if the line parameter is {@code ln -ls /dev/null /dev/sda}, the
	 * return value of this method is {@code ln}. Another example is that if line
	 * parameter is {@code ls}, the return value is {@code ln}
	 * 
	 * @param line the line of terminal
	 * @return a string != null that is the name of the command
	 */
	private String extractCommand(String line) {
		assert (line != null);
		assert (line.length() > 0);

		// if no spaces
		String extractedCommand = line;
		boolean extracted = false;

		for (int i = 0; i < line.length() && !extracted; i++) {
			if (line.charAt(i) == ' ') {
				// space detected
				extracted = true;
				extractedCommand = line.substring(0, i);
			}
		}

		assert (extractedCommand != null);
		assert (!extractedCommand.contains("\\s"));
		assert (extractedCommand.length() > 0);

		return extractedCommand;
	}

	/**
	 * This method extract the arguments of a command, and put it inside command
	 * invocation class.
	 * 
	 * @param argumentString the string, without command name, to parse
	 * @throws CommandSyntaxError if an error during parsing occurs
	 * @return Map a map between argument name and values
	 */
	private Map<String, String> extractArguments(String argumentString) throws CommandSyntaxError {
		assert (argumentString != null);

		Map<String, String> map = new HashMap<>();
		// check is valid
		if (argumentString.length() < 1) {
			// no argument to process
			return map;
		}

		/*
		 * before process argument string, we need to know if string is well composed.
		 * Split it and analyze pieces by pieces.
		 */
		String splitted[] = argumentString.split("\\s--");

		for (String cs : splitted) {
			if (cs.length() == 0) {
				continue;
			}

			/*
			 * check the syntax of a single argument.
			 */
			if (cs.matches("[^;:,=]+=[^;:,=]+")) {
				// value binded
				String[] csSplit = cs.split("=");
				String argName = csSplit[0];
				String argValue = csSplit[1];
				map.put(argName, argValue);
			} else if (cs.matches("[^;:,=]+")) {
				// single
				map.put(cs, null);
			} else {
				throw new CommandSyntaxError("You inserted too many characters");
			}
		}

		return map;
	}

	/**
	 * It parses the argument of a command.
	 * 
	 * @param map map between argument.
	 * @param commandInvocation the command invocation object
	 * @throws ArgumentParseException if any error during parsing occurs
	 * @throws CommandSyntaxError if there is an error during parsing the command
	 */
	private void parseArguments(Map<String, String> map, CommandInvocation commandInvocation)
			throws ArgumentParseException, CommandSyntaxError {
		assert (commandInvocation != null);
		assert (commandInvocation.command.arguments.size() > 0);

		// for each argument
		for (String argName : map.keySet()) {
			String argValue = map.get(argName);
			ArgumentDescription argumentDescription;
			argumentDescription = commandInvocation.command.arguments.get(argName);

			if (argumentDescription == null) {
				// this argument does not have this parameter name
				throw new CommandSyntaxError("The parameter \"" + argName + "\" is not accepted by this command");
			}

			assert (argValue != null);

			Object parsedValue;
			if (argValue != null) {
				// check if argument is really value binded
				if (argumentDescription.single) {
					throw new CommandSyntaxError("The parameter \"" + argName + "\" does not accept values!");
				}
				// we have the argument description the argument value. let's parse it.
				parsedValue = argumentDescription.parseValue(argValue);
			} else {
				// single argument
				// check if argument is really single
				if (!argumentDescription.single) {
					throw new CommandSyntaxError("The parameter \"" + argName + "\" must accept values!");
				}
				parsedValue = null;
			}

			// now add it to command invocation
			commandInvocation.arguments.put(argumentDescription, parsedValue);
		}

		/*
		 * Now we need to check if all mandatory parameters are set.
		 */
		for (ArgumentDescription argumentDescription : commandInvocation.command.getMandatoryArguments()) {
			if (!commandInvocation.arguments.containsKey(argumentDescription)) {
				throw new CommandSyntaxError(
						"You have missed the mandatory parameter \"" + argumentDescription.name + "\"");
			}
		}

	}

	/**
	 * Propagate a command event.
	 * 
	 * @param commandEvent event to propagate.
	 */
	public void propagateCommand(CommandEvent commandEvent) {
		for (CommandListener cl : listeners) {
			cl.commandRequest(commandEvent);

			if (commandEvent.consumed) {
				break;
			}
		}
	}

	/**
	 * Set the prompt of terminal.
	 * 
	 * @param ansi Ansi string
	 */
	protected void setPrompt(Ansi ansi) {
		if (ansi == null) {
			throw new NullPointerException();
		}

		this.prompt = ansi;
	}
}
