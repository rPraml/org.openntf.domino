package org.openntf.domino.commons.i18n;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/*----------------------------------------------------------------------------*/
public abstract class RawMessageProviderCacheAbstract extends RawMessageProviderAbstract {

	private Map<String, Map<String, Map<String, String>>> _msgCache;

	// Bundle          - Locale - Key          - Text
	// test.openntf    - en     - Common.text1 - "Some text"

	@Override
	public String getRawText(final String bundleName, final String key, final Locale loc) {
		if (_msgCache == null)
			buildMsgCache();
		Map<String, Map<String, String>> cmBundle = _msgCache.get(bundleName);
		if (cmBundle == null)
			return null;
		String localeString = loc.toString();
		if (localeString.length() > 5)
			localeString = localeString.substring(0, 5);
		Map<String, String> map4Loc;
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

	private void buildMsgCache() {
		_msgCache = new TreeMap<String, Map<String, Map<String, String>>>();
		Iterator<OneMsg> omi = getMessagesIterator();
		while (omi.hasNext())
			addOneMsg(omi.next());
	}

	private void addOneMsg(final OneMsg om) {
		String lang = om._lang;
		int lh = lang.length();
		if (lh >= 5)
			lang = lang.substring(0, 2) + "_" + lang.substring(3, 5);
		else if (lh >= 2)
			lang = lang.substring(0, 2);
		Map<String, Map<String, String>> cmBundle = _msgCache.get(om._bundle);
		if (cmBundle == null)
			_msgCache.put(om._bundle, cmBundle = new TreeMap<String, Map<String, String>>());
		Map<String, String> cmLang = cmBundle.get(lang);
		if (cmLang == null)
			cmBundle.put(lang, cmLang = new TreeMap<String, String>());
		cmLang.put(om._key, om._text);
	}

	@Override
	public void resetCache() {
		_msgCache = null;
	}

	protected abstract String getDefaultLocaleString();	// e.g. "*"

	protected abstract Iterator<OneMsg> getMessagesIterator();

}
