package org.openntf.domino.commons;

import java.util.Locale;

import com.ibm.icu.util.TimeZone;

public interface IRequest {
	/**
	 * Returns the UserName that is performing the current request. (i.e. the authenticated HTTP User)
	 */
	String getUserName();

	/**
	 * Returns the locale for which the current request is performed. (i.e. the locale of the user's browser)
	 */
	Locale getLocale();

	/**
	 * Returns the locale for which the current request is performed. (i.e. the timezone of the user's browser)
	 * 
	 * <b>Note:</b> There is no header (yet) to determine the TimeZone from the browser. The best way is to ask the user. It depends on your
	 * Request-Implementation, how to get the correct timezone (A standard implementation may return the Server's timezone which is
	 * sufficient for 99% of the cases)
	 */
	TimeZone getTimeZone();

	/**
	 * On a HTTP request, this returns the whole request URL that was entered in the browser
	 */
	String getRequestURL();

	/**
	 * Gets a certain element from the Request (has to be stored before)
	 */
	public <T> T get(Class<? extends T> key);

	/**
	 * Stores a certain element in this request
	 */
	public <T> void put(Class<? extends T> key, T value);

}
