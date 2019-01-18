package org.dvle.java_cli;

/**
 * This class describes the command received by a terminal.
 * 
 * @author federicosilvestri
 *
 */
public final class CommandEvent {

	/**
	 * This variable indicates that the event must not be forwarded to other
	 * CommandListener instances.
	 */
	boolean consumed;

	/**
	 * The invocation of the command.
	 */
	public final CommandInvocation commandInvocation;

	/**
	 * A generic data for the event. It can be used for information exchanging.
	 */
	public Object data;

	/**
	 * Create a new command event
	 */
	CommandEvent(CommandInvocation ci) {
		if (ci == null) {
			throw new NullPointerException();
		}

		this.consumed = false;
		this.commandInvocation = ci;
		this.data = null;
	}

	/**
	 * Consume the event. If a event is consumed propagation will be stopped as soon
	 * as possible.
	 */
	public void consume() {
		consumed = true;
	}

}
