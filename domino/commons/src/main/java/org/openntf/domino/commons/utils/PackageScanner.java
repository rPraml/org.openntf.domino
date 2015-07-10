package org.openntf.domino.commons.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Util-Class that can scan packages and find all classes in the package and its sub-packages.
 * 
 * @author Alexander Wagner, FOCONIS AG
 *
 */
public class PackageScanner {

	private final static char DOT = '.';
	private final static char SLASH = '/';
	private final static char BACKSLASH = '\\';
	private final static String CLASS_SUFFIX = ".class";
	private final static String PACKAGE_ERROR_MSG = "Unable to get resources from path '%s'."
			+ " It seams that the package '%s' does not exists.";

	/**
	 * Delivers all classes in the given package and its sub-packages.
	 * 
	 * @param packageName
	 *            The Name of the given package
	 * @return the classes as list
	 */
	public final static List<Class<?>> findClasses(final String packageName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String pathName = packageName.replace(DOT, SLASH);
		try {
			Enumeration<URL> resources = classLoader.getResources(pathName);
			List<Class<?>> classes = new LinkedList<Class<?>>();

			while (resources.hasMoreElements()) {
				File file = new File(resources.nextElement().getFile());
				classes.addAll(find(file, packageName));
			}
			return classes;
		} catch (IOException e) {
			throw new IllegalArgumentException(String.format(PACKAGE_ERROR_MSG, pathName, packageName), e);
		}
	}

	/**
	 * Helper method that returns the class of the current file in the package and the classes of its sub-packages recursively.
	 * 
	 * @param file
	 *            The current file, that is propably a .class file
	 * @param packageName
	 *            The name of the given package
	 * @return the classes as list
	 */
	protected final static List<Class<?>> find(final File file, final String packageName) {
		List<Class<?>> classes = new LinkedList<Class<?>>();

		if (file.isDirectory()) {
			for (File nestedFile : file.listFiles()) {
				classes.addAll(find(nestedFile, packageName));
			}
			//we are not interested in inner classes
		} else if (file.getName().endsWith(CLASS_SUFFIX) && !file.getName().contains("$")) {

			String className = file.getAbsolutePath().replace(BACKSLASH, DOT);
			int beginIndex = className.indexOf(packageName);
			className = className.substring(beginIndex);
			int endIndex = className.length() - CLASS_SUFFIX.length();
			try {
				String resource = className.substring(0, endIndex);
				classes.add(Class.forName(resource));
			} catch (ClassNotFoundException e) {
				//ignore exception
			}
		}
		return classes;
	}
}
