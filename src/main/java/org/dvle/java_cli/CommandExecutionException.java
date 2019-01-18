package org.dvle.java_cli;

/**
 * This class represents an exception that can be thrown during the execution of
 * a command.
 * 
 * @author federicosilvestri
 *
 */
public class CommandExecutionException extends Exception {

	/**
	 * Default serial ID.
	 */
	private static final long serialVersionUID = -569678662758753896L;

	/**
	 * Create a new command execution exception
	 * 
	 * @param reason a string that represents the reason of exception
	 * @param cause  the cause of this exception
	 */
	public CommandExecutionException(String reason, Throwable cause) {
		super(reason, cause);
	}

	/**
	 * Create a new command execution exception
	 * 
	 * @param reason a string that represents the reason of exception
	 */
	public CommandExecutionException(String reason) {
		super(reason);
	}
}
