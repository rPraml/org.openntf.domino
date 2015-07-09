package org.openntf.domino.commons.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.openntf.domino.commons.utils.ThreadUtils;

/**
 * Class for providing raw message texts living in a resource bundle.
 */
public class RawMessageProviderResourceBundle extends RawMessageProviderAbstract {

	/**
	 * In the first version, we used the standard-no-fallback-ResourceBundle.Control, given by
	 * ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES). However, when resources sit in an NSF, the
	 * method Control.needsReload returns "true" very often (perhaps always?), and performance gets dreadful. Hence we provide an own
	 * subclass, which always says "needsReload=false".
	 */
	private static class MyNoFallbackControl extends ResourceBundle.Control {
		static final ResourceBundle.Control _instance = new MyNoFallbackControl();
		private List<String> _format;

		public MyNoFallbackControl() {
			(_format = new ArrayList<String>()).add("java.properties");
		}

		@Override
		public List<String> getFormats(final String s) {
			return _format;
		}

		@Override
		public Locale getFallbackLocale(final String s, final Locale loc) {
			return null;
		}

		@Override
		public boolean needsReload(final String s1, final Locale loc, final String s2, final ClassLoader cl, final ResourceBundle rb,
				final long l) {
			return false;
		}
	}

	/** Fetches a the required message text from a resource bundle (cf. {@link ResourceBundle}). */
	@Override
	public String getRawText(final String bundleName, final String key, final Locale loc) {
		try {
			ResourceBundle rb = ResourceBundle.getBundle(bundleName, loc, ThreadUtils.getContextClassLoader(),
					MyNoFallbackControl._instance);
			return rb.getString(key);
		} catch (MissingResourceException mre) {
			return null;
		}
	}

	/** Provider gets a high priority, so it will be the last to be asked */
	@Override
	public int getPriority() {
		return 99;
	}
}
