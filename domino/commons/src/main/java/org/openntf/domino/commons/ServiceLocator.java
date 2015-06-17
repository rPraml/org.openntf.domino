package org.openntf.domino.commons;

import java.util.List;

import org.openntf.domino.commons.impl.NonOsgiServiceLocator;

/**
 * Callback interface for the FindService Method
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public abstract class ServiceLocator {
	private static ServiceLocator instance_ = new NonOsgiServiceLocator();

	protected abstract <T> List<T> locate(final Class<T> serviceClazz);

	public static <T> List<T> findApplicationServices(final Class<T> serviceClazz) {
		return instance_.locate(serviceClazz);
	}

	public static <T> T findApplicationService(final Class<T> serviceClazz) {
		List<T> list = instance_.locate(serviceClazz);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);

	}

	public static void setInstance(final ServiceLocator instance) {
		instance_ = instance;
	}

}