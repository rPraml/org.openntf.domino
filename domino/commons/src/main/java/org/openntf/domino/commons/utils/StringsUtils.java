package org.openntf.domino.commons.utils;

import java.util.Collection;

/**
 * Small collection of simple utilities for Strings
 * 
 * @author Steinsiek
 */
public enum StringsUtils {
	;

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

	public static final String _emptyString = "";

	public static String null2Empty(final String s) {
		return (s == null) ? _emptyString : s;
	}

	/**
	 * Joins elements into a String using a specified delimiter.
	 */
	public static String join(final Collection<?> source, final String delimiter) {
		if (source == null || source.isEmpty())
			return _emptyString;
		StringBuilder sb = new StringBuilder();
		for (Object o : source) {
			if (sb.length() != 0)
				sb.append(delimiter);
			sb.append(o.toString());
		}
		return sb.toString();
	}

	/**
	 * Concat strings into one String using a specified delimiter.
	 */
	public static String concatStrings(final String[] what, final char delimiter, final boolean trim) {
		if (what == null || what.length == 0)
			return _emptyString;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < what.length; i++) {
			if (i != 0)
				sb.append(delimiter);
			sb.append(trim ? what[i].trim() : what[i]);
		}
		return sb.toString();
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
	public static String[] splitSimple(final String whatToSplit, final char splitter, final boolean trimSplits) {
		return splitSimple(new SplitSimple(whatToSplit, splitter), trimSplits);
	}

	/** Cf. {@link StringsUtils#splitSimple(String, char, boolean)} */
	public static String[] splitSimple(final String whatToSplit, final String splitter, final boolean trimSplits) {
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
	 * Cf. {@link StringsUtils#splitSimple(String, char, boolean)}
	 * 
	 * @param whatToSplit
	 *            Clear
	 * @param specialSplit
	 *            Allowed: {@link SpecialSplit#BlankOrTab} or {@link SpecialSplit#AnyWhiteSpace}
	 */
	public static String[] splitSimple(final String whatToSplit, final SpecialSplit specialSplit, final boolean trimSplits) {
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
			if (!StringsUtils.isEmptyString(whatToSplit))
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
}
