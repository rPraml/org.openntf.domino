package org.openntf.domino.thread;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.Factory.ThreadConfig;

import com.ibm.icu.util.TimeZone;

public class DominoRequest implements IRequest {

	protected String userName = null;
	protected Locale locale = null;
	protected String requestUrl = null;
	protected ThreadConfig tc;
	protected String urlSuffix;
	protected Map<Class<?>, Object> objects = null;

	public DominoRequest(final ThreadConfig tc, final String urlSuffix) {
		this.tc = tc;
		this.urlSuffix = urlSuffix;
	}

	public DominoRequest(final ThreadConfig tc, final String urlSuffix, final Locale locale) {
		this(tc, urlSuffix);
		this.locale = locale;
	}

	@Override
	public String getUserName() {
		if (userName == null) {
			userName = Factory.getSession(SessionType.CURRENT).getEffectiveUserName();
		}
		return userName;
	}

	@Override
	public Locale getLocale() {
		if (locale == null) {
			locale = Factory.getSession(SessionType.CURRENT).getCurrentDatabase().getLocale();
		}
		return locale;
	}

	@Override
	public TimeZone getTimeZone() {
		// There is no standard implementation
		return TimeZone.getDefault();
	}

	@Override
	public String getRequestURL() {
		if (requestUrl == null) {
			requestUrl = Factory.getSession(SessionType.CURRENT).getCurrentDatabase().getNotesURL();
			requestUrl += urlSuffix;
		}
		return null;
	}

	@Override
	public <T> T get(final Class<? extends T> key) {
		if (key == ThreadConfig.class)
			return (T) tc;
		if (objects == null) {
			return null;
		}
		return (T) objects.get(key);
	}

	@Override
	public <T> void put(final Class<? extends T> key, final T value) {
		if (objects == null) {
			objects = new HashMap<Class<?>, Object>();
		}
		objects.put(key, value);

	}

}
