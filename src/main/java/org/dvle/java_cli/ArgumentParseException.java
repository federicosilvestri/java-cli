package org.dvle.java_cli;

/**
 * This exception is thrown if an argument cannot be parsed.
 * 
 * @author federicosilvestri
 *
 */
public class ArgumentParseException extends Exception {

	/**
	 * Default serial UID.
	 */
	private static final long serialVersionUID = 4781197874725664257L;

	/**
	 * Create a new exception with a localized message.
	 * 
	 * @param message message of error
	 */
	public ArgumentParseException(String message) {
		super(message);
	}

}
