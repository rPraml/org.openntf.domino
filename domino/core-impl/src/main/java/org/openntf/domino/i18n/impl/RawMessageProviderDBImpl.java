package org.openntf.domino.i18n.impl;

import java.util.Iterator;
import java.util.Vector;

import org.openntf.domino.Database;
import org.openntf.domino.Session;
import org.openntf.domino.View;
import org.openntf.domino.ViewEntry;
import org.openntf.domino.ViewNavigator;
import org.openntf.domino.commons.i18n.RawMessageProviderCacheAbstract;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;

/**
 * Reads raw message texts from a Notes lookup view, and these texts are cached by the superclass.
 * 
 * @author Steinsiek
 */
public class RawMessageProviderDBImpl extends RawMessageProviderCacheAbstract {

	private String _dbServer;
	private String _dbPath;
	private String _lookupView;
	private int _colBundle;
	private int _colKey;
	private int _colLang;
	private int _colText;

	/** Constructor: The necessary parameters for inspecting the lookup view mentioned above are passed. */
	protected RawMessageProviderDBImpl(final Database db, final String lookupView, final int colBundle, final int colKey,
			final int colLang, final int colText) {
		_dbServer = db.getServer();
		_dbPath = db.getFilePath();
		_lookupView = lookupView;
		_colBundle = colBundle;
		_colKey = colKey;
		_colLang = colLang;
		_colText = colText;
	}

	@Override
	protected String getDefaultLocaleString() {
		return "*";
	}

	private ViewNavigator _nav;
	private OneMsg _nextOM;

	@Override
	protected Iterator<OneMsg> getMessagesIterator() {
		openViewNav();
		_nextOM = getNextOM(true);
		return new Iterator<OneMsg>() {

			@Override
			public boolean hasNext() {
				return _nextOM != null;
			}

			@Override
			public OneMsg next() {
				OneMsg ret = _nextOM;
				_nextOM = getNextOM(false);
				return ret;
			}

			@Override
			public void remove() {
			}
		};
	}

	private void openViewNav() {
		_nav = null;
		try {
			Database db;
			Session s = Factory.getSession_unchecked(SessionType.CURRENT);
			if (s == null || (db = s.getDatabase(_dbServer, _dbPath)) == null) {
				System.err.println("RawMessageProviderDBImpl: Can't open DB " + _dbServer + "!!" + _dbPath);
				return;
			}
			View v = db.getView(_lookupView);
			if (v == null) {
				System.err.println("RawMessageProviderDBImpl: Can't open View " + _lookupView);
				return;
			}
			v.setAutoUpdate(false);
			_nav = v.createViewNav();
		} catch (Throwable t) {
			t.printStackTrace();
			_nav = null;
		}
	}

	private OneMsg getNextOM(final boolean first) {
		if (_nav == null)
			return null;
		try {
			for (ViewEntry ve = first ? _nav.getFirst() : _nav.getNext(); ve != null; ve = _nav.getNext()) {
				Vector<Object> cols = ve.getColumnValues();
				String bundle, lang, key, text;
				if ((bundle = getVEString(cols, _colBundle)) != null && (lang = getVEString(cols, _colLang)) != null
						&& (key = getVEString(cols, _colKey)) != null && (text = getVEString(cols, _colText)) != null)
					return new OneMsg(bundle, key, lang, text);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		_nav = null;
		return null;
	}

	private String getVEString(final Vector<Object> cols, final int viewColNum) {
		Object o = cols.get(viewColNum);
		return o instanceof String ? (String) o : null;
	}

}
