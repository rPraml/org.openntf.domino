package org.openntf.domino.commons.utils;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Simple class for Map<String, V>, which may be case-insensitive.
 * 
 * @author Steinsiek
 */
public class StringMap<V> extends TreeMap<String, V> implements IStringMap<V> {

	private static final long serialVersionUID = -5701938083812020294L;

	protected boolean _caseInsensitive = false;

	public StringMap(final boolean caseInsensitive) {
		super(new Comparator<String>() {
			@Override
			public int compare(final String s1, final String s2) {
				return caseInsensitive ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
			}
		});
		_caseInsensitive = caseInsensitive;
	}

	public StringMap() {
		super();
	}

	@Override
	public boolean isCaseInsensitive() {
		return _caseInsensitive;
	}
}
