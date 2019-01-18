package org.dvle.java_cli;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the invocation of a command, so a pair of
 * {@literal <CommandDescription, Map<ArgumentDescription, Object>}.
 * 
 * For example if the line string is {@code ln --file=foo.txt --name=ov.txt},
 * the command invocation is {@literal <CommandDescription(ln),
 * {<ArgumentDescription(file), "foo.txt">,<ArgumentDescription(name),
 * "ov.txt">}>}
 * 
 * NOTE: this class allows to specify only one value per command. For example
 * you cannot parse command like {@code copy --file=foo.txt --file=bar.txt}. To
 * extend this class with multiple arguments you use a Map of
 * {@literal <ArgumentDescription, List<Object>>}.
 * 
 * @author federicosilvestri
 */
final class CommandInvocation {
	/**
	 * The command description
	 */
	final CommandDescription command;

	/**
	 * The map between argument description and the related value.
	 */
	final Map<ArgumentDescription, Object> arguments;

	/**
	 * Create a new invocation of a given command.
	 * 
	 * @param command the command description
	 */
	CommandInvocation(CommandDescription command) {
		super();
		this.command = command;
		this.arguments = new HashMap<>();
	}

}
