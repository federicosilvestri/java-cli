package org.dvle.java_cli;

/**
 * 
 * @author federicosilvestri
 *
 */
public interface CommandListener {

	/**
	 * Receives the command.
	 * 
	 * @param cmdEvt command event to wrap
	 */
	public void commandRequest(CommandEvent cmdEvt);

	/**
	 * Receives the stop event.
	 * 
	 * @param cmdEvt stop event to wrap
	 */
	public void stopRequest(CommandEvent cmdEvt);
}
