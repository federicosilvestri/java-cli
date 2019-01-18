package org.dvle.java_cli;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

/**
 * This class represents the immutable settings of terminal.
 * 
 * @author federicosilvestri
 *
 */
final class Settings {
	/**
	 * Default prompt ANSI string.
	 */
	public static final Ansi PROMPT = Ansi.ansi().fg(Color.BLUE).a("[TERM]:").reset();

	/**
	 * Default command not found ANSI string.
	 */
	public static final Ansi COMMAND_NOT_FOUND = Ansi.ansi().fg(Color.RED).a("Command not found").reset();

	/**
	 * Default startup text ANSI string.
	 */
	public static final String STARTUP_TEXT = ":x.       ,x:            \n" + "'Nk      .KK.            \n"
			+ " :WkoooooOW,             \n" + "  dMo'''xMl              \n" + "   00. .Xk               \n"
			+ "   .Nd kK.   ;xxxkkkxxxx,\n" + "    ;NKN'        'W0.    \n" + "     :d,         .NO     \n"
			+ "                 .NO     \n" + "                 .NO     \n" + "                 .NO     \n"
			+ "                 .NO     \n" + "                 .xl     ";
	/**
	 * Default stop text ANSI string.
	 */
	public static final Ansi STOP_TEXT = Ansi.ansi().fg(Color.BLUE).a("Goodbye by Terminal");
}
