package org.openntf.domino.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public enum IO {
	;

	/**
	 * Printer class (will be modified by XSP-environment), so that the Factory prints directly to Console (so no "HTTP JVM" Prefix is
	 * there)
	 * 
	 * @author Roland Praml, FOCONIS AG
	 * 
	 */
	public static class Printer {
		public String getDomain() {
			return "ODA";
		}

		public void println(final String s) {
			System.out.println(s);
		}
	}

	private static Printer printer = new Printer();
	public static boolean DEBUG = true;

	public static void setPrinter(final Printer printer) {
		IO.printer = printer;
	}

	public static void println(final Object x) {
		println(null, String.valueOf(x));
	}

	public static void println(final Object source, final Object x) {
		if (source == null) {
			println(null, String.valueOf(x));
		} else {
			String prefix;
			if (source instanceof String) {
				prefix = (String) source;
			} else if (source instanceof Class) {
				prefix = ((Class<?>) source).getSimpleName();
			} else {
				prefix = source.getClass().getSimpleName();
			}
			println(prefix, String.valueOf(x));
		}
	}

	public static void println(String prefix, final String lines) {
		BufferedReader reader = new BufferedReader(new StringReader(lines));
		String line;
		try {
			if (Strings.isBlankString(prefix)) {
				prefix = "[" + printer.getDomain() + "] ";
			} else {
				prefix = "[" + printer.getDomain() + "::" + prefix + "] ";
			}
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0)
					printer.println(prefix + line);
			}
		} catch (IOException ioex) {

		}
	}

	public static void printDbg(final Object o) {
		if (DEBUG) {
			println("DEBUG", o);
		}
	}
}
