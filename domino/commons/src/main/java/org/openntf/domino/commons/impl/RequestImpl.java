package org.openntf.domino.commons.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.IRequest;

import com.ibm.icu.util.TimeZone;

public class RequestImpl implements IRequest {

	private String userName_;
	private String url_;
	private Locale locale_;
	private TimeZone timeZone_;

	private Map<Class, Object> objects;

	public RequestImpl(final String userName, final String url, final Locale locale, final TimeZone timeZone) {
		userName_ = userName;
		url_ = url;
		locale_ = locale;
		timeZone_ = timeZone;
	}

	@Override
	public String getUserName() {
		return userName_;
	}

	@Override
	public Locale getLocale() {
		return locale_;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone_;
	}

	@Override
	public String getRequestURL() {
		return url_;
	}

	@Override
	public <T> T get(final Class<? extends T> key) {
		if (objects == null) {
			return null;
		}
		return (T) objects.get(key);
	}

	@Override
	public <T> void put(final Class<? extends T> key, final T value) {
		if (objects == null) {
			objects = new HashMap<Class, Object>();
		}
		objects.put(key, value);

	}

}
