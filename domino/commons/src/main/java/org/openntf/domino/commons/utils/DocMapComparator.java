/**
 * 
 */
package org.openntf.domino.commons.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author nfreeman, refactored by Roland Praml (replaced Document by Map<String, Object>
 * 
 *         Copyright Michael Zischeck and licensed under Apache License 2.0 from http://in-mood.blogspot.com/
 * 
 * 
 */
public class DocMapComparator implements Comparator<Map<String, Object>>, Serializable {
	@SuppressWarnings("unused")
	private static final Logger log_ = Logger.getLogger(DocMapComparator.class.getName());
	private static final long serialVersionUID = 1L;

	public static class ValuesNotComparableException extends RuntimeException {
		private static final long serialVersionUID = 4918210702121980155L;

		public ValuesNotComparableException(final Object o1, final Object o2) {
			super("Cannot compare objects of type " + (o1 == null ? "null" : o1.getClass().getName()) + " and "
					+ (o2 == null ? "null" : o2.getClass().getName()));
		}
	}

	String[] sortKeys = null;

	public DocMapComparator(final String... sortKeys) {
		this.sortKeys = sortKeys;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int compareObj(final Object o1, final Object o2) {
		if (o1 instanceof Comparable && o2 instanceof Comparable) {
			return ((Comparable) o1).compareTo(o2);
		} else {
			throw new ValuesNotComparableException(o1, o2);
		}
	}

	@Override
	public int compare(final Map<String, Object> doc1, final Map<String, Object> doc2) {
		// loop all sortFields
		for (String key : sortKeys) {
			Object obj1 = doc1.get(key);
			Object obj2 = doc2.get(key);
			if (obj1 instanceof List && obj2 instanceof List) {
				List<?> l1 = (List<?>) obj1;
				List<?> l2 = (List<?>) obj2;
				int min = l1.size() < l2.size() ? l1.size() : l2.size();
				int fallback = Integer.valueOf(l1.size()).compareTo(l2.size());	// if all values are the same up to the end of the smaller
				// list, the smaller list wins
				for (int i = 0; i < min; i++) {
					int result = compareObj(l1.get(i), l2.get(i));
					if (result != 0)
						return result;
				}
				return fallback;
			} else {
				return compareObj(obj1, obj2);
			}
		}
		return 0;
	}
}
