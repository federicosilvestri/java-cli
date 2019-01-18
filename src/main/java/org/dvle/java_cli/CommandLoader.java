package org.dvle.java_cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class is used to dynamically load classes that represents a command
 * description.
 * 
 * @author federicosilvestri
 *
 */
public class CommandLoader {

	/**
	 * A pattern that must match to consider a class a potential subclass of
	 * CommandDescription.
	 */
	public static final String CLASS_NAME_PATTERN = "(.)*Command\\.class";

	/**
	 * The instance of terminal.
	 */
	private final Terminal terminal;

	/**
	 * Default class loader.
	 */
	private final ClassLoader classLoader;

	/**
	 * A list of loaded classes.
	 */
	private final LinkedList<String> loadedClasses;

	/**
	 * Create a new instance of command loader.
	 */
	public CommandLoader(Terminal terminal) {
		if (terminal == null) {
			throw new NullPointerException();
		}

		this.terminal = terminal;
		this.classLoader = Thread.currentThread().getContextClassLoader();
		this.loadedClasses = new LinkedList<>();
	}

	/**
	 * This method loads all the classes with name that matches with
	 * CLASS_NAME_PATTERN into terminal instance.
	 * 
	 * @param packageName the name of the package to search
	 * @throws NoSuchMethodException     if the default constructor does not exists
	 * @throws InvocationTargetException if the target of invocation is not
	 *                                   acceptable
	 * @throws IllegalArgumentException  if the argument of constructor are not
	 *                                   valid
	 * @throws IllegalAccessException    if the access to a class property is not
	 *                                   legal
	 * @throws InstantiationException    if we can't instantiate the command
	 *                                   description
	 */
	public void loadFromPackage(String packageName)
			throws FileNotFoundException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (packageName == null) {
			throw new NullPointerException();
		}

		// sanitize the string
		String slashedPackageName = convertPackageName(packageName);
		// get the class URL
		URL resource = classLoader.getResource(slashedPackageName);

		if (resource.toString().startsWith("jar:")) {
			try {
				JarFile jarFile = new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
				loadFromJar(jarFile, packageName);
			} catch (IOException e) {
				throw new RuntimeException("Cannot load resource from Jar file", e);
			}
		} else {
			loadFromFile(new File(resource.getFile()), packageName);
		}
	}

	/**
	 * This method loads all the classes if files are not inside jar.
	 * 
	 * @param packageName the name of the package to search
	 * @throws NoSuchMethodException     if the default constructor does not exists
	 * @throws InvocationTargetException if the target of invocation is not
	 *                                   acceptable
	 * @throws IllegalArgumentException  if the argument of constructor are not
	 *                                   valid
	 * @throws IllegalAccessException    if the access to a class property is not
	 *                                   legal
	 * @throws InstantiationException    if we can't instantiate the command
	 *                                   description
	 */
	private void loadFromFile(File classDir, String packageName)
			throws FileNotFoundException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (!classDir.exists() || !classDir.isDirectory()) {
			throw new FileNotFoundException("Assertions failed during loading of terminal commands");
		}

		for (File file : classDir.listFiles()) {
			String fileName = file.getName();
			if (!file.isFile()) {
				continue;
			}

			if (fileName.matches(CLASS_NAME_PATTERN)) {
				// load it
				String sanitizedClassName = sanitizeClassName(fileName, packageName);
				loadClass(sanitizedClassName);
			}
		}

	}

	/**
	 * This method loads all the classes if files are inside jar file.
	 * 
	 * @param packageName the name of the package to search
	 * @throws NoSuchMethodException     if the default constructor does not exists
	 * @throws InvocationTargetException if the target of invocation is not
	 *                                   acceptable
	 * @throws IllegalArgumentException  if the argument of constructor are not
	 *                                   valid
	 * @throws IllegalAccessException    if the access to a class property is not
	 *                                   legal
	 * @throws InstantiationException    if we can't instantiate the command
	 *                                   description
	 */
	private void loadFromJar(JarFile jarFile, String packageName)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Enumeration<JarEntry> entries = jarFile.entries();
		String slashedNotation = convertPackageName(packageName);

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();

			if (!entryName.startsWith(slashedNotation)) {
				continue;
			}

			if (entryName.matches(CLASS_NAME_PATTERN)) {
				// load it
				String packageString = convertToPackageName(entryName);
				loadClass(packageString);
			}
		}
	}

	/**
	 * This method only returns a valid check package name with slash notation.
	 * 
	 * @param packageName the name of package
	 * @return sanitized package
	 */
	private String convertPackageName(String packageName) {
		if (packageName.length() < 1) {
			throw new IllegalArgumentException("Package name must be != \"\"");
		}

		/*
		 * first convert dot notation to slash notation
		 */
		return packageName.replaceAll("\\.", "/");
	}

	/**
	 * This method only returns a valid package name with dot notation.
	 * 
	 * @param packageName the name of package
	 * @return sanitized package
	 */
	private String convertToPackageName(String packageName) {
		if (packageName.length() < 1) {
			throw new IllegalArgumentException("Package name must be != \"\"");
		}

		if (packageName.endsWith(".class")) {
			packageName = packageName.replace(".class", "");
		}

		/*
		 * first convert dot notation to slash notation
		 */
		return packageName.replaceAll("\\/", "\\.");
	}

	/**
	 * This method sanitizes and prepares a class name to be loaded.
	 * 
	 * @param className   the class name (ending with .class extension)
	 * @param packageName name of the package in dot notation
	 * @return a sanitized string
	 */
	private String sanitizeClassName(String className, String packageName) {
		assert (className != null && packageName != null);
		assert (className.length() > 0 && packageName.length() > 0);

		if (className.endsWith(".class")) {
			className = className.replace(".class", "");
		}

		return packageName + "." + className;
	}

	/**
	 * Load the command inside terminal.
	 * 
	 * @param className the class name to instantiate and load into terminal
	 * @throws ClassNotFoundException    in case of class is not found
	 * @throws SecurityException         in case of constructor of command is not
	 *                                   accessible
	 * @throws NoSuchMethodException     if the default constructor does not exists
	 * @throws InvocationTargetException if the target of invocation is not
	 *                                   acceptable
	 * @throws IllegalArgumentException  if the argument of constructor are not
	 *                                   valid
	 * @throws IllegalAccessException    if the access to a class property is not
	 *                                   legal
	 * @throws InstantiationException    if we can't instantiate the command
	 *                                   description
	 */
	private void loadClass(String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		assert (className != null);
		assert (className.length() > 0);

		Class<?> clazz = Class.forName(className);
		Constructor<?> constructor = clazz.getConstructor();
		Object instance = constructor.newInstance();

		/*
		 * casting
		 */
		if (!(instance instanceof CommandDescription)) {
			throw new ClassCastException();
		}
		CommandDescription cd = (CommandDescription) instance;

		// for info purposes
		loadedClasses.add(className);

		/*
		 * Adding to terminal
		 */
		terminal.addCommand(cd);
	}

	/**
	 * Returns a list of loaded classes, useful for debugging.
	 * 
	 * @return a list of loaded classes.
	 */
	public List<String> getLoadedClasses() {
		return new LinkedList<>(this.loadedClasses);
	}

}
