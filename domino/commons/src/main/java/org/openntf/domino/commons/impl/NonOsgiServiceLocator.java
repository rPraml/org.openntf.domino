package org.openntf.domino.commons.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.openntf.domino.commons.ServiceLocator;

public class NonOsgiServiceLocator extends ServiceLocator {
	@SuppressWarnings("rawtypes")
	private static Map<Class, List> cache = new ConcurrentHashMap<Class, List>(); // in a NON-OSGI-Enviroment we may cache this

	@Override
	public <T> List<T> locate(final Class<T> serviceClazz) {
		List<T> ret = cache.get(serviceClazz);
		if (ret == null) {
			ret = new ArrayList<T>();
			cache.put(serviceClazz, ret);
			final List<T> fret = ret;

			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				@Override
				public Object run() {
					ClassLoader cl = Thread.currentThread().getContextClassLoader();
					if (cl != null) {
						ServiceLoader<T> loader = ServiceLoader.load(serviceClazz, cl);
						Iterator<T> it = loader.iterator();
						while (it.hasNext()) {
							fret.add(it.next());
						}
					}
					if (Comparable.class.isAssignableFrom(serviceClazz)) {
						Collections.sort((List<? extends Comparable>) fret);
					}
					return null;
				}
			});

		}
		return ret;
	}

}
