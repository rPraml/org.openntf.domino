package org.openntf.domino.xsp;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;

import org.openntf.domino.commons.ServiceLocator;

import com.ibm.commons.extension.ExtensionManager;
import com.ibm.xsp.application.ApplicationEx;

public class OsgiServiceLocator extends ServiceLocator {

	@Override
	public <T> List<T> locate(final Class<T> serviceClazz) {
		return AccessController.doPrivileged(new PrivilegedAction<List<T>>() {

			@Override
			public List<T> run() {
				return locateAppService(serviceClazz);
			}

		});

	}

	protected <T> List<T> locateAppService(final Class<T> serviceClazz) {
		System.out.println("Loading " + serviceClazz);
		// TODO Auto-generated method stub
		ApplicationFactory aFactory = (ApplicationFactory) FactoryFinder.getFactory("javax.faces.application.ApplicationFactory");
		final ApplicationEx app_ = aFactory == null ? null : (ApplicationEx) aFactory.getApplication();
		if (app_ == null) {
			return (List<T>) ExtensionManager.findApplicationServices(null, Thread.currentThread().getContextClassLoader(),
					serviceClazz.getName());
		} else {
			return app_.findServices(serviceClazz.getName());
		}
	}

}
