package org.openntf.domino.commons.utils;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Simple class for Set<String>, which may be case-insensitive.
 * 
 * @author Steinsiek
 */
public class StringSet extends TreeSet<String> {

	private static final long serialVersionUID = -5701938083812020299L;

	protected boolean _caseInsensitive = false;

	public StringSet(final boolean caseInsensitive) {
		super(new Comparator<String>() {
			@Override
			public int compare(final String s1, final String s2) {
				return caseInsensitive ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
			}
		});
		_caseInsensitive = caseInsensitive;
	}

	public StringSet() {
		super();
	}

	public boolean isCaseInsensitive() {
		return _caseInsensitive;
	}
}
