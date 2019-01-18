package org.dvle.java_cli;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * This class represents the execution environment of a command.
 * 
 * @author federicosilvestri
 *
 */
public class ExecutionEnvironment {

	/**
	 * A terminal hook for command propagation
	 */
	private final Terminal terminal;

	/**
	 * The invocation of command
	 */
	private final CommandInvocation commandInvocation;

	/**
	 * A stream to print out.
	 */
	public final PrintStream out;

	/**
	 * A stream to read data.
	 */
	public final InputStream in;

	/**
	 * Create a new execution environment
	 * 
	 * @param out the output stream
	 * @param in  the input stream
	 */
	ExecutionEnvironment(Terminal t, CommandInvocation ci, PrintStream out, InputStream in) {
		super();
		if (t == null || out == null || in == null || ci == null) {
			throw new NullPointerException();
		}

		this.out = out;
		this.in = in;
		this.terminal = t;
		this.commandInvocation = ci;
	}

	/**
	 * Send a command to handlers registered to terminal.
	 * 
	 * @param data an useful data object to be inserted, null if not desired
	 */
	public void sendCommandToHandlers(Object data) {
		// creating event
		CommandEvent ce = new CommandEvent(commandInvocation);
		ce.data = data;
		terminal.propagateCommand(ce);
	}

}
