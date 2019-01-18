/**
 * 
 */
package org.dvle.java_cli;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * NOTE: this test is not a valid test, because I haven't enough time to write
 * test and it's not so important for the application purpose.
 * 
 * @author federicosilvestri
 *
 */
class TerminalTest {

	/**
	 * Default exit command
	 */
	private static ExitCommandDescription exitCommandDescription;

	private static final String TEST_COMMAND_NAME = "test1";

	private Terminal terminal;
	private Thread terminalThread;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		exitCommandDescription = ExitCommandDescription.DEFAULT;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		// create an input stream with command exit written
		InputStream terminalInput = new ByteArrayInputStream(exitCommandDescription.name.getBytes());
		PrintStream terminalOutput = new PrintStream(new ByteArrayOutputStream());

		terminal = new Terminal(terminalInput, terminalOutput, exitCommandDescription);
		terminalThread = new Thread(terminal);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link unipisa.cd.rcl.turing.com.term.Terminal#Terminal(java.io.InputStream, java.io.PrintStream, unipisa.cd.rcl.turing.com.term.ExitCommandDescription)}.
	 * The test consists in the creation of a terminal with customized input/output
	 * and try to exit from it.
	 */
	@Test
	void testTerminalInputStreamPrintStreamExitCommandDescription() {
		// starting the thread
		terminalThread.start();

		// terminal should exit immediately
		try {
			terminalThread.join();
		} catch (InterruptedException ie) {
			fail("Terminal not stopped");
		}
	}

	/**
	 * Test method for
	 * {@link unipisa.cd.rcl.turing.com.term.Terminal#addListener(unipisa.cd.rcl.turing.com.term.CommandListener)}.
	 */
	@Test
	void testAddListener() {
	}

	/**
	 * Test method for
	 * {@link unipisa.cd.rcl.turing.com.term.Terminal#removeListener(unipisa.cd.rcl.turing.com.term.CommandListener)}.
	 */
	@Test
	void testRemoveListener() {
	}

	/**
	 * Test method for
	 * {@link unipisa.cd.rcl.turing.com.term.Terminal#addCommand(unipisa.cd.rcl.turing.com.term.CommandDescription)}.
	 */
	@Test
	void testAddCommand() {
		// if I add a command two time, I expect an exception
		CommandDescription c1 = new CommandDescription(TEST_COMMAND_NAME) {

			@Override
			public void runCommand(Map<ArgumentDescription, Object> arguments, ExecutionEnvironment exe)
					throws CommandExecutionException {
				// do nothing

			}

		};

		terminal.addCommand(c1);
		assertThrows(RuntimeException.class, () -> terminal.addCommand(c1));
		CommandDescription c2 = new CommandDescription(TEST_COMMAND_NAME) {

			@Override
			public void runCommand(Map<ArgumentDescription, Object> arguments, ExecutionEnvironment exe)
					throws CommandExecutionException {
				// do nothing

			}

		};
		assertThrows(RuntimeException.class, () -> terminal.addCommand(c2));
	}
}
