package org.dvle.java_cli;

import java.io.File;

/**
 * This class allow to represent an argument of a command. There are two main
 * types of argument: single and value-binded. For example in the command
 * {@literal get --verbose --file=foo.txt} we have these two types of argument.
 * The first one is a single type argument, because it is not binded to a value,
 * the second one is a value binded argument because it takes as value a string.
 * This class represent both of them.
 * 
 * @author federicosilvestri
 *
 */
public final class ArgumentDescription {
	/**
	 * This internal enumeration is the representation of the argument value type.
	 * 
	 * @author federicosilvestri
	 *
	 */
	public enum ArgumentValueType {
	STRING, INTEGER, LONG, DECIMAL, FILE_PATH, BOOLEAN
	}

	/**
	 * This variable indicates if argument is single or not (then value-binded);
	 */
	final boolean single;

	/**
	 * The name of the argument, for example if you launch the command
	 * {@literal get --verbose} the argument name is {@literal verbose}.
	 */
	final String name;

	/**
	 * The type of the value. It's not necessary to set it up if the argument is of
	 * single type.
	 */
	final ArgumentValueType type;

	/**
	 * Variable to indicates if this argument is mandatory.
	 */
	final boolean mandatory;

	/**
	 * Create a new value-binded argument description.
	 * 
	 * @param name      name of argument
	 * @param avt       argument value type
	 * @param mandatory true if mandatory, false if not mandatory
	 */
	public ArgumentDescription(String name, ArgumentValueType avt, boolean mandatory) {
		this(false, name, avt, mandatory);

		if (avt == null) {
			throw new NullPointerException();
		}
	}

	/**
	 * Create a new single type argument.
	 * 
	 * @param name the name of the command
	 */
	public ArgumentDescription(String name) {
		this(true, name, null, false);
	}

	private ArgumentDescription(boolean single, String name, ArgumentValueType avt, boolean mandatory) {
		if (name == null) {
			throw new NullPointerException();
		}

		if (name.length() == 0) {
			throw new IllegalArgumentException("Argument name cannot be blank!");
		}

		if (single) {
			if (mandatory) {
				throw new IllegalArgumentException("A single-type argument cannot be mandatory!");
			}

			if (avt != null) {
				throw new IllegalArgumentException(
						"A single-type argument cannot have a non-null argument value type!");
			}
		}

		this.single = single;
		this.name = name;
		this.type = avt;
		this.mandatory = mandatory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof ArgumentDescription)) {
			return false;
		}

		ArgumentDescription ag = (ArgumentDescription) obj;
		return ag.name.equals(this.name);
	}

	/**
	 * Parse the value of the argument.
	 * 
	 * @param argValue the value to parse
	 * @return an object that represents the value of the argument
	 * @throws ArgumentParseException in case of any error occurs during parsing
	 */
	public Object parseValue(String argValue) throws ArgumentParseException {
		switch (type) {
		case STRING:
			return argValue;
		case BOOLEAN:
			try {
				return Boolean.parseBoolean(argValue);
			} catch (NumberFormatException ex) {
				throw new ArgumentParseException(ex.getLocalizedMessage());
			}
		case INTEGER:
			try {
				return Integer.parseInt(argValue);
			} catch (NumberFormatException ex) {
				throw new ArgumentParseException(ex.getLocalizedMessage());
			}
		case LONG:
			try {
				return Long.parseLong(argValue);
			} catch (NumberFormatException ex) {
				throw new ArgumentParseException(ex.getLocalizedMessage());
			}
		case DECIMAL:
			try {
				return Double.parseDouble(argValue);
			} catch (NumberFormatException ex) {
				throw new ArgumentParseException(ex.getLocalizedMessage());
			}
		case FILE_PATH:
			return new File(argValue);
		default:
			/*
			 * should not happen
			 */
			throw new RuntimeException("Unsupported argument value!");
		}
	}

}
