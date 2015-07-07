package org.openntf.domino.commons.i18n;

import java.util.Iterator;
import java.util.Locale;

import org.openntf.domino.commons.utils.IStringMap;
import org.openntf.domino.commons.utils.StringMap;

/**
 * While <code>RawMessageProviderResourceBundle</code>'s message cache is managed by the resource bundle, there are cases, where that cache
 * must be handled by the provider class itself, e.g. when there are client specific message texts, which are to be read from some database.
 * The class <code>RawMessageProviderCacheAbstract</code> implements the base mechanisms for such a cache.
 */
public abstract class RawMessageProviderCacheAbstract extends RawMessageProviderAbstract {

	/**
	 * Message cache, given by 3 nested <code>Map</code>s. Structure is:<br/>
	 * <br/>
	 * 
	 * <table border="1">
	 * <tr>
	 * <th align="left">Bundle name</th>
	 * <th align="left">Locale</th>
	 * <th align="left">Key</th>
	 * <th align="left">Text</th>
	 * </tr>
	 * <tbody>
	 * <tr>
	 * <td>org.openntf.i18n.test.messages</td>
	 * <td>en</td>
	 * <td>Button.saveclose</td>
	 * <td>Save and close</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	private IStringMap<IStringMap<IStringMap<String>>> _msgCache;

	/**
	 * Provides a raw message text from the cache. If the cache isn't yet initialized, this will be done first.
	 */
	@Override
	public String getRawText(final String bundleName, final String key, final Locale loc) {
		if (_msgCache == null)
			buildMsgCache();
		IStringMap<IStringMap<String>> cmBundle = _msgCache.get(bundleName);
		if (cmBundle == null)
			return null;
		String localeString = loc.toString();
		if (localeString.length() > 5)
			localeString = localeString.substring(0, 5);
		IStringMap<String> map4Loc;
		int lh;
		while ((lh = localeString.length()) >= 2) {
			String ret;
			if ((map4Loc = cmBundle.get(localeString)) != null && (ret = map4Loc.get(key)) != null)
				return ret;
			if (lh <= 2)
				break;
			localeString = localeString.substring(0, 2);
		}
		return ((map4Loc = cmBundle.get(getDefaultLocaleString())) == null) ? null : map4Loc.get(key);
	}

	/** Inner class holding the parameter for one message text. */
	protected class OneMsg {
		private String _bundle;
		private String _key;
		private String _lang;
		private String _text;

		public OneMsg(final String bundle, final String key, final String lang, final String text) {
			_bundle = bundle;
			_key = key;
			_lang = lang;
			_text = text;
		}
	}

	/**
	 * Initializes the message cache, relying on a <code>getMessagesIterator</code> method, which subclasses have to implement.
	 */
	private void buildMsgCache() {
		synchronized (this) {
			if (_msgCache != null)
				return;
			_msgCache = new StringMap<IStringMap<IStringMap<String>>>();
			Iterator<OneMsg> omi = getMessagesIterator();
			while (omi.hasNext())
				addOneMsg(omi.next());
		}
	}

	/** Inserts one message to the cache. */
	private void addOneMsg(final OneMsg om) {
		String lang = om._lang;
		int lh = lang.length();
		if (lh >= 5)
			lang = lang.substring(0, 2) + "_" + lang.substring(3, 5);
		else if (lh >= 2)
			lang = lang.substring(0, 2);
		IStringMap<IStringMap<String>> cmBundle = _msgCache.get(om._bundle);
		if (cmBundle == null)
			_msgCache.put(om._bundle, cmBundle = new StringMap<IStringMap<String>>());
		IStringMap<String> cmLang = cmBundle.get(lang);
		if (cmLang == null)
			cmBundle.put(lang, cmLang = new StringMap<String>());
		cmLang.put(om._key, om._text);
	}

	/** The cache is invalidated. */
	@Override
	public void resetCache() {
		_msgCache = null;
	}

	/** Gives the string, which represents the default locale e.g. in the database; in the Notes-DB case, we use "*". */
	protected abstract String getDefaultLocaleString();

	protected abstract Iterator<OneMsg> getMessagesIterator();

}
