package org.openntf.domino.commons;

public interface IDataConverter {
	<T> T convertTo(Object o, Class<T> targetType);
}
