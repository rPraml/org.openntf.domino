package org.openntf.domino.commons;

import java.util.Collections;
import java.util.Comparator;
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
		List<T> fret = instance_.locate(serviceClazz);
		if (fret.size() > 1) {
			//						if (Comparable.class.isAssignableFrom(serviceClazz)) {
			//							Collections.sort((List<? extends Comparable>) fret);
			//						} else
			if (IPriority.class.isAssignableFrom(serviceClazz)) {
				Collections.sort((List<? extends IPriority>) fret, new Comparator<IPriority>() {

					@Override
					public int compare(final IPriority arg0, final IPriority arg1) {
						int i = arg0.getPriority();
						int j = arg1.getPriority();
						return i == j ? 0 : i < j ? -1 : 1;

					}
				});
			} else {
				IO.println("More than one service found for " + serviceClazz + " but does not implement IPriority.");
			}
		}
		return fret;
	}

	public static <T> T findApplicationService(final Class<T> serviceClazz) {
		List<T> list = findApplicationServices(serviceClazz);
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);

	}

	public static void setInstance(final ServiceLocator instance) {
		instance_ = instance;
	}

}