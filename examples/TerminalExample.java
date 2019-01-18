package mypackage.cli;

import org.dvle.java_cli.*;

/**
 * This class is the class that represents the implementation of client terminal
 * 
 * @author federicosilvestri
 *
 */
public class TerminalExample extends Terminal {
	
	/**
	 * Location package of the commands.
	 */
	private static final String COMMANDS_PACKAGE = "mypackage.cli";

	/**
	 * Create a new instance of terminal.
	 */
	public ClientTerminal() {
		setupCommands();
	}

	/**
	 * This method loads command from package with command loader
	 */
	private void setupCommands() {
		CommandLoader commandLoader = new CommandLoader(this);
		
		try {
			commandLoader.loadFromPackage(COMMANDS_PACKAGE);
		} catch (FileNotFoundException | ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("Cannot load commands of terminal!", e);
		}
	}

}