package org.openntf.domino.commons.i18n;

import java.util.Locale;

/** Yields external (browser) and internal (db/os) Locales */
public interface IExtIntLocaleProvider {

	public Locale getExternalLocale();

	public Locale getInternalLocale();

}
