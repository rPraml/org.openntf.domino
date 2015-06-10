package org.openntf.domino.commons.utils;

public enum SimpleStringUtils {
	;

	public static boolean startsWithIgnoreCase(final CharSequence what, final CharSequence prefix) {
		if (null == what || null == prefix)
			return false;
		int sz = prefix.length();
		if (sz > what.length())
			return false;
		for (int i = 0; i < sz; i++)
			if (Character.toLowerCase(what.charAt(i)) != Character.toLowerCase(prefix.charAt(i)))
				return false;
		return true;
	}

	public static boolean isEmptyString(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean isBlankString(final CharSequence cs) {
		if (cs == null)
			return true;
		for (int i = cs.length() - 1; i >= 0; i--)
			if (!Character.isWhitespace(cs.charAt(i)))
				return false;
		return true;
	}

	public static final String _emptyString = "";

	public static String null2Empty(final String s) {
		return (s == null) ? _emptyString : s;
	}

}
