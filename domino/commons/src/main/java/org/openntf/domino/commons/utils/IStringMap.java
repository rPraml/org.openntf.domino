package org.openntf.domino.commons.utils;

import java.util.Map;

/** Interface, mainly for StringMap. */
public interface IStringMap<V> extends Map<String, V> {

	public boolean isCaseInsensitive();

}
