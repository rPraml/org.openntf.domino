package org.openntf.domino.commons.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class PackageScanner {

	private final static char DOT = '.';
	private final static char SLASH = '/';
	private final static char BACKSLASH = '\\';
	private final static String CLASS_SUFFIX = ".class";
	private final static String PACKAGE_ERROR_MSG = "Unable to get resources from path '%s'."
			+ " It seams that the package '%s' does not exists.";

	public final static List<Class<?>> findClasses(final String packageName) {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String pathName = packageName.replace(DOT, SLASH);
		final Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(pathName);
		} catch (IOException e) {
			throw new IllegalArgumentException(String.format(PACKAGE_ERROR_MSG, pathName, packageName), e);
		}
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		while (resources.hasMoreElements()) {
			final File file = new File(resources.nextElement().getFile());
			classes.addAll(find(file, packageName));
		}
		return classes;
	}

	protected final static List<Class<?>> find(final File file, final String packageName) {
		final List<Class<?>> classes = new LinkedList<Class<?>>();
		if (file.isDirectory()) {
			for (File nestedFile : file.listFiles()) {
				classes.addAll(find(nestedFile, packageName));
			}
			//we are not interested in inner classes
		} else if (file.getName().endsWith(CLASS_SUFFIX) && !file.getName().contains("$")) {

			String className = file.getAbsolutePath().replace(BACKSLASH, DOT);
			final int beginIndex = className.indexOf(packageName);
			className = className.substring(beginIndex);
			final int endIndex = className.length() - CLASS_SUFFIX.length();
			try {
				final String resource = className.substring(0, endIndex);
				classes.add(Class.forName(resource));
			} catch (ClassNotFoundException ignore) {
			}
		}
		return classes;
	}
}
