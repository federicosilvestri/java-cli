package org.dvle.java_cli;

import java.util.Map;

/**
 * This class represents the command description of exit command. Each shell
 * must have a mechanism to quit, and this class represent it.
 * 
 * @author federicosilvestri
 *
 */
public class ExitCommandDescription extends CommandDescription {

	/**
	 * Create a new ExitCommandDescription named exit
	 */
	public static final ExitCommandDescription DEFAULT = new ExitCommandDescription("exit");

	/**
	 * Create the exit command, specifying the name, for example exit
	 * 
	 * @param name the name of the exit command
	 */
	protected ExitCommandDescription(String name) {
		super(name);
	}

	@Override
	public void runCommand(Map<ArgumentDescription, Object> arguments, ExecutionEnvironment exe)
			throws CommandExecutionException {
	}
}
