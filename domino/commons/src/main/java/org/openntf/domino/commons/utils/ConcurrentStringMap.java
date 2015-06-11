package org.openntf.domino.commons.utils;

/*----------------------------------------------------------------------------*/
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

/*----------------------------------------------------------------------------*/
public class ConcurrentStringMap<V> extends ConcurrentSkipListMap<String, V> {

	private static final long serialVersionUID = 3865136561265883504L;

	protected boolean iCaseInsensitive = false;

	/*----------------------------------------------------------------------------*/
	public ConcurrentStringMap(final boolean caseInsensitive) {
		super(new Comparator<String>() {
			@Override
			public int compare(final String s1, final String s2) {
				return caseInsensitive ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
			}
		});
		iCaseInsensitive = caseInsensitive;
	}

	public ConcurrentStringMap() {
		super();
	}

	/*----------------------------------------------------------------------------*/
	public boolean isCaseInsensitive() {
		return iCaseInsensitive;
	}
	/*----------------------------------------------------------------------------*/
}
