package org.openntf.domino.xsp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.openntf.domino.commons.IRequest;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.Factory.ThreadConfig;

import com.ibm.icu.util.TimeZone;
import com.ibm.xsp.context.FacesContextEx;

public class XspRequest implements IRequest {

	private String userName = null;
	private Locale locale = null;
	private String requestUrl = null;
	private ThreadConfig tc;
	private FacesContextEx fc;
	private Map<Class<?>, Object> objects = null;

	public XspRequest(final ThreadConfig tc, final FacesContextEx fc) {
		this.tc = tc;
		this.fc = fc;
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
		if (locale != null)
			return locale;

		locale = fc.getSessionData().getLocale();
		if (locale != null)
			return locale;

		Iterator<Locale> locIt = fc.getExternalContext().getRequestLocales();
		if (locIt.hasNext()) {
			locale = locIt.next();
		}
		if (locale != null)
			return locale;
		locale = fc.getExternalContext().getRequestLocale();
		if (locale != null)
			return locale;
		locale = fc.getApplication().getDefaultLocale();
		if (locale != null)
			return locale;

		locale = Locale.getDefault();

		return locale;
	}

	@Override
	public TimeZone getTimeZone() {
		// There is no standard implementation
		return TimeZone.getDefault();
	}

	@Override
	public String getRequestURL() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return req.getRequestURL().toString();
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
