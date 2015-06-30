package org.openntf.domino.commons.utils;

import java.util.Collection;

public enum TypeUtils {
	;
	public static final String[] DEFAULT_STR_ARRAY = { "" };

	public static <T> T objectToClass(final Object o, final Class<T> type) {
		return null;//convertToTarget(o, type);
	}

	public static <T> T collectionToClass(final Collection<?> v, final Class<T> type) {
		return collectionToClass(v, type);
	}
}
