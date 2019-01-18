package mypackage.cli;

import java.util.Map;

import org.dvle.java_cli.*;

/**
 * Example of implementation of CommandDescription.
 * 
 * @author federicosilvestri
 *
 */
public class CreateCommand extends CommandDescription {
	/**
	 * Argument 0 description.
	 */
	private ArgumentDescription arg0Description;

	/**
	 * Argument 1 description.
	 */
	private ArgumentDescription arg1Description;

	/**
	 * Argument 2 description.
	 */
	private ArgumentDescription arg2Description;

	/**
	 * Argument 3 description.
	 */
	private ArgumentDescription arg3Description;

	/**
	 * Create the command
	 */
	public CreateCommand() {
		super("create");
		// setup the command
		arg0Description = new ArgumentDescription("arg0", ArgumentValueType.STRING, true);
		arg1Description = new ArgumentDescription("arg1", ArgumentValueType.INTEGER, true);
		arg2Description = new ArgumentDescription("arg2", ArgumentValueType.INTEGER, false);
		arg3Description = new ArgumentDescription("arg3");
		this.addArgument(arg0Description);
		this.addArgument(arg1Description);
		this.addArgument(arg2Description);
		this.addArgument(arg3Description);
	}

	@Override
	public void runCommand(Map<ArgumentDescription, Object> arguments, ExecutionEnvironment exe)
			throws CommandExecutionException {

		// getting arguments
		String arg0 = (String) arguments.get(docDescription);
		Integer arg1 = (Integer) arguments.get(sectDescription);
		Integer arg2 = (Integer) arguments.get(sectDescription);
		Boolean arg3 = arguments.containsKey(arg3Description);

		exe.out.println("arg0=" + arg0);
		exe.out.println("arg1=" + arg1);
		exe.out.println("arg2=" + arg1);

		if (arg3) {
			exe.out.println("arg3 is specified");
		}
	}

}
