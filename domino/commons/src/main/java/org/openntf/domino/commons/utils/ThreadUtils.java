package org.openntf.domino.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.openntf.domino.commons.IO;

public enum ThreadUtils {
	;
	public static class LoaderObjectInputStream extends ObjectInputStream {

		public LoaderObjectInputStream(final InputStream in) throws IOException {
			super(in);
		}

		@Override
		protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			String name = desc.getName();
			Class<?> result = null;
			try {
				result = ThreadUtils.getClass(name);
			} catch (Exception e) {
			}
			if (result == null) {
				result = super.resolveClass(desc);
			}
			return result;
		}
	}

	/**
	 * Get the current context classloader
	 */
	public static ClassLoader getContextClassLoader() {
		try {
			if (System.getSecurityManager() == null)
				return Thread.currentThread().getContextClassLoader();
			return AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
				@Override
				public ClassLoader run() throws Exception {
					return Thread.currentThread().getContextClassLoader();
				}
			});
		} catch (PrivilegedActionException e) {
			IO.println("Cannot get the context classloader. Got a " + e.getMessage());
			return null;
		}
	}

	/**
	 * Set a new classloader and return the previous one. You must restore the previous one, after operation is completed
	 */
	public static ClassLoader setContextClassLoader(final ClassLoader clNew) {
		try {
			if (System.getSecurityManager() == null) {
				ClassLoader ret = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().setContextClassLoader(clNew);
				return ret;
			}
			return AccessController.doPrivileged(new PrivilegedExceptionAction<ClassLoader>() {
				@Override
				public ClassLoader run() throws Exception {
					ClassLoader ret = Thread.currentThread().getContextClassLoader();
					Thread.currentThread().setContextClassLoader(clNew);
					return ret;
				}
			});
		} catch (PrivilegedActionException e) {
			IO.println("Cannot get the context classloader. Got a " + e.getMessage());
			return null;
		}
	}

	/**
	 * Gets a class from the current contextClassLoader
	 * 
	 * @param className
	 *            the className
	 * @return the class
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(final String name) throws ClassNotFoundException {
		Class<?> result = null;

		if (System.getSecurityManager() == null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			return Class.forName(name, true, loader);
		}

		try {
			return AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
				@Override
				public Class<?> run() throws Exception {
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					return Class.forName(name, true, loader);
				}
			});
		} catch (PrivilegedActionException e) {
			if (e.getCause() instanceof ClassNotFoundException) {
				throw (ClassNotFoundException) e.getCause();
			}
			throw new RuntimeException("could not load class: " + name, e);
		}
	}
}
