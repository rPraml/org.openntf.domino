package org.openntf.domino.commons.impl;

import java.util.Locale;

import org.openntf.domino.commons.IRequest;

import com.ibm.icu.util.TimeZone;

public class RequestImpl implements IRequest, Cloneable {

	protected String userName_;
	protected Locale locale_;
	protected TimeZone timeZone_;
	protected String dbContext_;
	protected String action_;

	protected RequestImpl() {

	}

	public RequestImpl(final String action) {
		action_ = action;
	}

	public RequestImpl(final String userName, final Locale locale, final TimeZone timeZone) {
		userName_ = userName;
		locale_ = locale;
		timeZone_ = timeZone;
	}

	@Override
	public String getUserName() {
		return userName_;
	}

	@Override
	public Locale getLocale() {
		if (locale_ == null) {
			locale_ = Locale.getDefault();
		}
		return locale_;
	}

	@Override
	public TimeZone getTimeZone() {
		if (timeZone_ == null) {
			timeZone_ = TimeZone.getDefault();
		}
		return timeZone_;
	}

	//	@Override
	//	public String getRequestURL() {
	//		return url_;
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	@Override
	//	public <T> T get(final Class<? extends T> key) {
	//		if (objects == null) {
	//			return null;
	//		}
	//		return (T) objects.get(key);
	//	}
	//
	//	@Override
	//	public <T> void put(final Class<? extends T> key, final T value) {
	//		if (objects == null) {
	//			objects = new HashMap<Class<?>, Object>();
	//		}
	//		objects.put(key, value);
	//
	//	}

	@Override
	public IRequest clone(final String nextAction) {
		try {
			RequestImpl ret = (RequestImpl) super.clone();
			ret.action_ = action_ + ", " + nextAction;

			// explicit copy them by invoking the getters!
			ret.dbContext_ = getDatabaseContext();
			ret.locale_ = getLocale();
			ret.timeZone_ = getTimeZone();
			ret.userName_ = getUserName();

			return ret;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String getDatabaseContext() {
		return dbContext_;
	}

	@Override
	public String getAction() {
		return action_;
	}

}
