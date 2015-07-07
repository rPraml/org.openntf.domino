package org.openntf.domino.commons.i18n;

import java.util.Locale;

import org.openntf.domino.commons.IPriority;
import org.openntf.domino.commons.ServiceLocator;

/**
 * Base class for delivering "raw" message texts. One subclass relying on resource bundles will probably always be used (see
 * {@link RawMessageProviderResourceBundle}).
 */
public abstract class RawMessageProviderAbstract implements IPriority {

	public abstract String getRawText(final String bundleName, final String key, final Locale loc);

	/**
	 * The lower the RawMessageProvider's priority, the earlier will it be asked to yield a message text (see
	 * {@link MessageProviderAbstract#findRawMessageProviders()} and {link {@link ServiceLocator#findApplicationServices(Class)}).
	 */
	@Override
	public int getPriority() {
		return 50;
	}

	public void resetCache() {
	}
}
