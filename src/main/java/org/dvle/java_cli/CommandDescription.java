package org.dvle.java_cli;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represent a command that can be given to terminal. For example the
 * linux `ls` or `scp`. A command is defined as a pair of string and a list of
 * arguments. For example the {@literal ls -laR /dev/} can be represented as
 * {@literal <ls , [-l,
 * -a, -R, /dev/null]>}
 * 
 * @author federicosilvestri
 *
 */
public abstract class CommandDescription {
	/**
	 * Name of the command.
	 */
	final String name;

	/**
	 * A map of argument-name, argument-description description.
	 */
	final Map<String, ArgumentDescription> arguments;

	/**
	 * A list of mandatory arguments.
	 */
	private final List<ArgumentDescription> mandatoryArguments;

	/**
	 * Create a new description of a command.
	 * 
	 * @param name the name of the command
	 */
	public CommandDescription(String name) {
		super();

		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("The command name can't be null or blank");
		}

		this.name = name;
		this.arguments = new HashMap<>();
		this.mandatoryArguments = new LinkedList<>();
	}

	/**
	 * Add an argument to this command.
	 * 
	 * @param ad an argument description, that must be != null
	 */
	public void addArgument(ArgumentDescription ad) {
		if (ad == null) {
			throw new NullPointerException();
		}

		if (arguments.containsValue(ad)) {
			throw new RuntimeException("You have already inserted this argument description!");
		}

		arguments.put(ad.name, ad);

		/*
		 * if command is mandatory add it to mandatory list, to speedup checking of
		 * command validity.
		 */
		if (ad.mandatory) {
			mandatoryArguments.add(ad);
		}
	}

	/**
	 * Get a list of required arguments
	 * 
	 * @return a non null value that is representation of a list of
	 *         CommandDescription
	 */
	public List<ArgumentDescription> getMandatoryArguments() {
		LinkedList<ArgumentDescription> returnCopy;
		returnCopy = new LinkedList<>(mandatoryArguments);

		return returnCopy;
	}

	/**
	 * Return true if line can be the invocation of this command.
	 * 
	 * @param line line to process
	 * @return true if the line can be a command invocation, false if not
	 */
	public boolean isCalled(String line) {
		/*
		 * Line is always != null, but we need to assert it.
		 */
		assert (line != null);

		if (line.equals(name)) {
			return true;
		}

		return false;
	}

	/**
	 * Execute the command given the argument.
	 * 
	 * @param arguments map between argument formal description and value. This
	 *                  value can be null if command has no parameters.
	 * @param exe       the execution environment of this command
	 * @throws CommandExecutionException if the execution causes problems
	 */
	public abstract void runCommand(Map<ArgumentDescription, Object> arguments, ExecutionEnvironment exe)
			throws CommandExecutionException;
}
