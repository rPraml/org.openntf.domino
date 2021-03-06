package org.openntf.domino.commons;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Small collection of simple utilities for Strings
 * 
 * @author Steinsiek
 */
public enum Strings {
	;
	public static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");

	public static final String EMPTY_STRING = "";

	/**
	 * Case-insensitive variant of {@link String#startsWith(String)}
	 */
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

	/**
	 * Returns true iff the "String" to test is either null or empty
	 */
	public static boolean isEmptyString(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * Same as {@link String#valueOf(Object)}, but the String for NULL may be chosen arbitrarily (instead of "null")
	 */
	public static String toString0(final Object o, final String nullValue) {
		return o == null ? nullValue : o.toString();
	}

	/**
	 * Convenience method - same as {@link #toString0(Object, String)}, with <code>nullValue=""</code>
	 */
	public static String toString0(final Object o) {
		return toString0(o, EMPTY_STRING);
	}

	/**
	 * Returns true iff the "String" to test is null or empty or consists of "white spaces" only (cf. {@link Character#isWhitespace(char)})
	 */
	public static boolean isBlankString(final CharSequence cs) {
		if (cs == null)
			return true;
		for (int i = cs.length() - 1; i >= 0; i--)
			if (!Character.isWhitespace(cs.charAt(i)))
				return false;
		return true;
	}

	/**
	 * Converts a "null" string to empty string
	 */
	public static String null2Empty(final String s) {
		return (s == null) ? EMPTY_STRING : s;
	}

	/**
	 * Joins elements into a String using a specified delimiter.
	 */

	public static String join(final Object source, final String delimiter) {
		if (source == null)
			return EMPTY_STRING;
		StringBuilder sb = new StringBuilder();
		Class<? extends Object> cls = source.getClass();
		if (cls.isArray()) {
			int len = Array.getLength(source);
			for (int i = 0; i < len; i++) {
				if (sb.length() != 0)
					sb.append(delimiter);
				sb.append(toString(Array.get(source, i)));
			}
		} else {
			for (Object o : (Iterable) source) {
				if (sb.length() != 0)
					sb.append(delimiter);
				sb.append(toString(o));
			}
		}
		return sb.toString();
	}

	/**
	 * Concat strings into one String using a specified delimiter.
	 */
	public static String concatStrings(final String[] what, final char delimiter, final boolean trim) {
		if (what == null || what.length == 0)
			return EMPTY_STRING;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < what.length; i++) {
			if (i != 0)
				sb.append(delimiter);
			sb.append(trim ? what[i].trim() : what[i]);
		}
		return sb.toString();
	}

	/** trim is false: Cf. {@link Strings#split(String, char, boolean)} */
	public static String[] split(final String whatToSplit, final char splitter) {
		/** smallest memory footprint implementation */
		int parts = 1;
		for (int i = 0; i < whatToSplit.length(); i++) {
			if (whatToSplit.charAt(i) == splitter)
				parts++;
		}
		String[] ret = new String[parts];
		parts = 0;
		int prev = 0;
		for (int i = 0; i < whatToSplit.length(); i++) {
			if (whatToSplit.charAt(i) == splitter) {
				ret[parts++] = whatToSplit.substring(prev, i);
				prev = i + 1;
			}
		}
		ret[parts++] = whatToSplit.substring(prev);
		return ret;
	}

	/** trim is false: Cf. {@link Strings#split(String, char, boolean)} */
	public static String[] split(final String whatToSplit, final String splitter) {
		return splitSimple(new SplitSimple(whatToSplit, splitter), false);
	}

	/**
	 * In most cases, the "normal" split method (cf. {@link String#split(String)}) is too powerful: You need to split strings along
	 * characters, substrings, blank/tab or white spaces, but very rarely you'll need "real" regular expressions to split along.
	 * <p/>
	 * Even worse: Since <code>String.split</code> expects a regular expression as parameter, you have to be very careful: If you want to
	 * split e.g. a Java package name, <code>"org.openntf.domino".split(".")</code> won't do the job. Instead, you have to write
	 * <code>"org.openntf.domino".split("\\.")</code>.
	 * <p/>
	 * Therefore, we give three variants of a "simple split", which cover 99% of the practice. - Since frequently the split parts are needed
	 * "trimmed", this may be controlled by the parameter <code>trimSplits</code>.
	 */
	public static String[] split(final String whatToSplit, final char splitter, final boolean trimSplits) {
		return splitSimple(new SplitSimple(whatToSplit, splitter), trimSplits);
	}

	/** Cf. {@link Strings#split(String, char, boolean)} */
	public static String[] split(final String whatToSplit, final String splitter, final boolean trimSplits) {
		return splitSimple(new SplitSimple(whatToSplit, splitter), trimSplits);
	}

	/**
	 * In practice, you often need a split along blank/tab (like <code>awk</code>) or even any "white space" (like <code>xml</code>). This
	 * <code>enum</code> contains the corresponding values <code>BlankOrTab</code> resp. <code>AnyWhiteSpace</code>.
	 */
	public enum SpecialSplit {
		/** Split along blank or tab */
		BlankOrTab,
		/** Split along any "white space" (cf. {@link Character#isWhitespace(char)}) */
		AnyWhiteSpace,
		/** No special split; mustn't be passed as parameter */
		None;
	}

	/**
	 * Cf. {@link Strings#split(String, char, boolean)}
	 * 
	 * @param whatToSplit
	 *            Clear
	 * @param specialSplit
	 *            Allowed: {@link SpecialSplit#BlankOrTab} or {@link SpecialSplit#AnyWhiteSpace}
	 */
	public static String[] split(final String whatToSplit, final SpecialSplit specialSplit, final boolean trimSplits) {
		return splitSimple(new SplitSimple(whatToSplit, specialSplit), trimSplits);
	}

	private static String[] splitSimple(final SplitSimple splitSimple, final boolean trimSplits) {
		int n = splitSimple.split();
		String[] ret = new String[n];
		for (int i = 0; i < n; i++)
			ret[i] = splitSimple.getSplitN(i, trimSplits);
		return ret;
	}

	/**
	 * Slim implementation of a simple split mechanism.
	 */
	public static class SplitSimple {

		private char[] _whatToSplit = null;
		private char _splitterC;
		private String _splitterS = null;
		private SpecialSplit _specialSplitter = SpecialSplit.None;
		private int _splitterLh;

		public SplitSimple(final String whatToSplit, final char splitter) {
			setWhatToSplit(whatToSplit);
			_splitterC = splitter;
			_splitterLh = 1;
		}

		private void setWhatToSplit(final String whatToSplit) {
			if (!Strings.isEmptyString(whatToSplit))
				_whatToSplit = whatToSplit.toCharArray();
		}

		public SplitSimple(final String whatToSplit, final String splitter) {
			if (splitter == null || (_splitterLh = splitter.length()) == 0)
				throw new IllegalArgumentException("Splitter mustn't be NULL or EMPTY");
			setWhatToSplit(whatToSplit);
			_splitterS = splitter;
			_splitterC = splitter.charAt(0);
		}

		public SplitSimple(final String whatToSplit, final SpecialSplit specialSplit) {
			if (specialSplit == SpecialSplit.None)
				throw new IllegalArgumentException("Invalid SpecialSplit");
			setWhatToSplit(whatToSplit);
			_specialSplitter = specialSplit;
			_splitterLh = 1;
		}

		private int _numSplits;
		private int[] _splitBegs;
		private int[] _splitEnds;

		public int split() {
			if (_whatToSplit == null)
				return 0;
			int i = _whatToSplit.length >>> 2;
			if (i < 4)
				i = 4;
			else if (i > 1024)
				i = 1024;
			_splitBegs = new int[i];
			_splitEnds = new int[i];
			_splitBegs[0] = 0;
			_numSplits = 0;
			for (i = _splitBegs[_numSplits]; i < _whatToSplit.length; i++) {
				if (_specialSplitter == SpecialSplit.None) {
					if (_whatToSplit[i] != _splitterC)
						continue;
					if (_splitterLh > 1 && !testVsSplitterS(i))
						continue;
				} else if (_specialSplitter == SpecialSplit.BlankOrTab) {
					if (_whatToSplit[i] != ' ' && _whatToSplit[i] != '\t')
						continue;
				} else { // if (_specialSplitter == SpecialSplit.AnyWhiteSpace
					if (!Character.isWhitespace(_whatToSplit[i]))
						continue;
				}
				if (_numSplits + 1 >= _splitBegs.length)
					reallocBegEnd();
				_splitEnds[_numSplits++] = i;
				_splitBegs[_numSplits] = i + _splitterLh;
				i = _splitBegs[_numSplits] - 1;
			}
			_splitEnds[_numSplits++] = i;
			return _numSplits;
		}

		private boolean testVsSplitterS(final int i) {
			for (int j = 1; j < _splitterLh; j++)
				if (i + j >= _whatToSplit.length || _whatToSplit[i + j] != _splitterS.charAt(j))
					return false;
			return true;
		}

		private void reallocBegEnd() {
			int[] newBegs = new int[_numSplits << 1];
			int[] newEnds = new int[_numSplits << 1];
			System.arraycopy(_splitBegs, 0, newBegs, 0, _numSplits + 1);
			_splitBegs = newBegs;
			System.arraycopy(_splitEnds, 0, newEnds, 0, _numSplits + 1);
			_splitEnds = newEnds;
		}

		public String getSplitN(final int n) {
			return getSplitN(n, false);
		}

		public String getSplitN(final int n, final boolean trimIt) {
			if (n >= _numSplits || n < 0)
				return "";
			int beg = _splitBegs[n];
			int end = _splitEnds[n];
			if (trimIt) {
				for (; beg < end; beg++)
					if (!Character.isWhitespace(_whatToSplit[beg]))
						break;
				if (beg < end) {
					for (end--; Character.isWhitespace(_whatToSplit[end]); end--)
						;
					end++;
				}
			}
			return new String(_whatToSplit, beg, end - beg);
		}

	}

	/**
	 * Generates a String filled with a specific character.
	 * 
	 * @param length
	 *            The length of the String to return. Negative values will be treated as positive.
	 * 
	 * @param c
	 *            Character with which to populate the result.
	 * 
	 * @return String consisting c characters repeated length times.
	 */
	public static String getFilledString(final int length, final char c) {
		if (0 == length) {
			return EMPTY_STRING;
		}

		final int n = Math.abs(length);
		char[] chars = new char[n];
		Arrays.fill(chars, c);
		return new String(chars);

	}

	/**
	 * Checks if is hex.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is hex
	 */
	public static boolean isHex(final CharSequence value) {
		if (value == null)
			return false;
		String chk = value.toString().trim().toLowerCase();
		for (int i = 0; i < chk.length(); i++) {
			char c = chk.charAt(i);
			boolean isHexDigit = Character.isDigit(c) || Character.isWhitespace(c) || c == 'a' || c == 'b' || c == 'c' || c == 'd'
					|| c == 'e' || c == 'f';

			if (!isHexDigit) {
				return false;
			}

		}
		return true;
	}

	/**
	 * Checks if is number. (in US/English format!)
	 * 
	 * @param value
	 *            the value
	 * @return true, if is number
	 */
	public static boolean isNumber(final CharSequence value) {
		boolean seenDot = false;
		boolean seenExp = false;
		boolean justSeenExp = false;
		boolean seenDigit = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c >= '0' && c <= '9') {
				seenDigit = true;
				continue;
			}
			if ((c == '-' || c == '+') && (i == 0 || justSeenExp)) {
				continue;
			}
			if (c == '.' && !seenDot) {
				seenDot = true;
				continue;
			}
			justSeenExp = false;
			if ((c == 'e' || c == 'E') && !seenExp) {
				seenExp = true;
				justSeenExp = true;
				continue;
			}
			return false;
		}
		if (!seenDigit) {
			return false;
		}
		try {
			Double.parseDouble(value.toString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Returns the String-representation of the given object. In
	 * 
	 * @param object
	 * @return
	 */
	public static String toString(final java.lang.Object object) {
		if (object == null)
			return null;
		if (object instanceof String) {
			return (String) object;
		} else if (object instanceof Iterable) {
			return join(object, ", ");
		} else if (object.getClass().isArray()) {
			return join(object, ", ");
			// 2015-08-06/RPr: We use the deprecated "toString" of IDateTime here.
			// This returns an (ugly readable) Date in ISO format, so you should immediately
			// notice, that something is going wrong if you see the value in the UI.

			//		} else if (object instanceof org.openntf.domino.commons.IDateTime) {
			//			throw new UnsupportedOperationException("Explain me your use-case");
			//			// 2015-06-30/RPr: How should we convert DateTime-Values?
			//			// - getGMTTime is available only in lotus - and heavily depends on your locale settings
			//			// - getGMTTime is not "user readable", because it is computed with the wrong timezone
			//			// - toString() is "readable", but not intended to write in a field
			//			// - if you want to write a "stable" date, we should use an ISO-format (e.g. ISO-8601)
			//			//return ((org.openntf.domino.commons.IDateTime) object).getGMTTime();
		} else {
			return String.valueOf(object);
		}
	}

	public static String[] toStrings(final Collection<?> coll) {
		if (coll == null || coll.isEmpty())
			return new String[0];

		String[] strings = new String[coll.size()];
		int i = 0;
		for (Object o : coll) {
			strings[i++] = toString(o);
		}
		return strings;
	}

	public static String[] toStrings(final Object[] arr) {
		if (arr == null || arr.length == 0)
			return new String[0];

		Class<?> cType = arr.getClass().getComponentType();
		if (String.class.isAssignableFrom(cType))
			return (String[]) arr;

		String[] strings = new String[arr.length];
		int i = 0;
		for (Object o : arr) {
			strings[i++] = toString(o);
		}
		return strings;
	}

	public static String[] toStrings(final Object o) {
		if (o == null) {
			return new String[0];
		}
		Class<? extends Object> type = o.getClass();
		if (type.isArray()) {
			if (type.getComponentType().isPrimitive()) {
				// cast (Object[]) wont work!
				String[] strings = new String[Array.getLength(o)];
				for (int i = 0; i < strings.length; i++) {
					strings[i] = toString(Array.get(o, i));
				}
				return strings;
			}
			return toStrings((Object[]) o);
		}
		if (o instanceof Collection) {
			return toStrings((Collection<?>) o);
		}
		if (o instanceof Iterable) {
			List<Object> li = new ArrayList<Object>();
			for (Object elem : (Iterable<?>) o) {
				li.add(elem);
			}
			return toStrings(li);
		}
		return new String[] { toString(o) };
	}
}
