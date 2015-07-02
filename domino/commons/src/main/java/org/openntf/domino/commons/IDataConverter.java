package org.openntf.domino.commons;

public interface IDataConverter<T> {
	T convertTo(Object o);

	Class<? extends T> getType();
}
