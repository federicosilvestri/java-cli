package org.dvle.java_cli;

/**
 * This class represents the exception given by terminal if a syntax error
 * occurs.
 * 
 * @author federicosilvestri
 *
 */
class CommandSyntaxError extends Exception {

	/**
	 * Default serial UID.
	 */
	private static final long serialVersionUID = 8687318370716591185L;

	/**
	 * Create a new exception.
	 * 
	 * @param message message to visualize
	 */
	public CommandSyntaxError(String message) {
		super(message);
	}
}
