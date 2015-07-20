/*
 * © Copyright FOCONIS AG, 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.domino.commons;

import java.util.Date;
import java.util.Locale;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

/**
 * This is the DateTime interface that is used in formulas. It is very similar to the org.opennft.domino.DateTime interface but this has no
 * dependency to the lotus API, so that the formula engine can be used in a non-notes environment. You should always use this interface
 * instead of the DateTime in the ODA-core API
 */
public interface IDateTime extends Comparable<IDateTime> {

	/**
	 * Factory to create a new instance. In Java 1.8 we can use a static method in the Interface
	 */
	public enum $ {
		;
		private static IDateTime PROTOTYPE = ServiceLocator.findApplicationService(IDateTime.class);

		public static IDateTime create() {
			return PROTOTYPE.clone();
		}

		public static IDateTime parse(final String text, final Locale locale, final boolean parseLenient) {
			IDateTime ret = create();
			ret.parse(text, locale, parseLenient);
			return ret;
		}

		public static IDateTime create(final Date date) {
			IDateTime ret = create();
			ret.setLocalTime(date);
			return ret;
		}

		public static IDateTime create(final java.util.Calendar date) {
			IDateTime ret = create();
			ret.setLocalTime(date);
			return ret;
		}

		public static IDateTime create(final Calendar date) {
			IDateTime ret = create();
			ret.setLocalTime(date);
			return ret;
		}

		public static IDateTime create(final int year, final int month, final int day) {
			IDateTime ret = create();
			ret.setLocalDate(year, month, day);
			ret.setLocalTime(0, 0, 0, 0);
			return ret;
		}

		public static IDateTime create(final int year, final int month, final int day, final int hour, final int minute, final int second,
				final int hundredth) {
			IDateTime ret = create();
			ret.setLocalDate(year, month, day);
			ret.setLocalTime(hour, minute, second, hundredth);
			return ret;
		}
	}

	/**
	 * Formatter are not threadSafe, so we use a ThreadLocal
	 */
	public static enum ISO {
		;
		// We neither set english nor german dates as default.
		// we use something that is machine readable 
		// There is one rule: Never store a Date in a string, so you should NOT use "toString()" to present a date to the user
		private static final String _DATE_PATTERN = "yyyy-MM-dd";
		private static final String _TIME_PATTERN = "HH:mm:ss.SSS";
		private static final String _DATE_TIME_PATTERN = _DATE_PATTERN + "'T'" + _TIME_PATTERN + "Z";
		private static final ThreadLocal<DateFormat[]> _INSTANCE = new ThreadLocal<DateFormat[]>() {
			@Override
			protected DateFormat[] initialValue() {
				DateFormat[] dfmts = new DateFormat[3];
				dfmts[0] = new SimpleDateFormat(_DATE_TIME_PATTERN);
				dfmts[1] = new SimpleDateFormat(_DATE_PATTERN);
				dfmts[2] = new SimpleDateFormat(_TIME_PATTERN);
				return dfmts;
			};
		};

		public static DateFormat dateTimeFormat() {
			return _INSTANCE.get()[0];

		}

		public static DateFormat dateFormat() {
			return _INSTANCE.get()[1];
		}

		public static DateFormat timeFormat() {
			return _INSTANCE.get()[2];
		}

	};

	/**
	 * Increments/decrements a IDateTime by the number of years you specify. This will not change smaller fields. i.e. it preserves
	 * localtime
	 */
	public void adjustYear(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of month you specify. This will not change smaller fields. i.e. it preserves
	 * localtime
	 */
	public void adjustMonth(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of days you specify. This will not change smaller fields. i.e. it preserves localtime
	 */
	public void adjustDay(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of hours you specify. This will not preserve localtime, if you get over a DST-change
	 * event. So adding 24 hours is NOT the same as adding 1 day if you pass a DST boundary.
	 */
	public void adjustHour(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of minutes you specify. This will not preserve local time. See
	 * {@link #adjustHour(int)}
	 */
	public void adjustMinute(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of seconds you specify. This will not preserve local time. See
	 * {@link #adjustHour(int)}
	 */
	public void adjustSecond(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of milliseconds you specify. This will not preserve local time. See
	 * {@link #adjustHour(int)}
	 */
	public void adjustMilli(final long n);

	/**
	 * Returns the Milliseconds since 1970-01-01
	 */
	public long getMillis();

	/**
	 * Return the time difference in milliseconds. It computes <code>this</code> value minus <code>other</code> value.
	 */
	public long timeDifferenceMillis(IDateTime other);

	// Converting date to string without specifying a locale is always a bad idea, so the locale parameter should be applied
	/**
	 * Returns a "Date Only" representation, formatted for the given locale and style {@link DateFormat#MEDIUM}, if <code>locale</code> is
	 * <code>null</code> the date is returned in ISO format (2003-08-23).
	 */
	public String getDateOnly(Locale locale);

	/**
	 * Returns a "Date Only" representation, formatted for the given locale and the given style (see {@link DateFormat}). if
	 * <code>locale</code> is <code>null</code> the date is returned in ISO format (2003-08-23) - style is ignored.
	 */
	public String getDateOnly(Locale locale, int style);

	/**
	 * Returns a "Date Only" representation, formatted for the given locale and style {@link DateFormat#MEDIUM}, if <code>locale</code> is
	 * <code>null</code> the time is returned in ISO format (23:45:12.345).
	 */
	public String getTimeOnly(Locale locale);

	/**
	 * Returns a "Date Only" representation, formatted for the given locale and the given style (see {@link DateFormat}), if
	 * <code>locale</code> is <code>null</code> the time is returned in ISO format (23:45:12.345) - style is ignored
	 */
	public String getTimeOnly(Locale locale, int style);

	/**
	 * Returns the String representation for the given locale and the given styles (see {@link DateFormat}). if <code>locale</code> is
	 * <code>null</code> the date time is returned in ISO format (2003-08-23Z23:45:12.345+0100) - styles are ignored.
	 */
	public String toString(Locale locale, int dateStyle, int timeStyle);

	/**
	 * Returns the String representation for the given locale and {@link DateFormat#MEDIUM}. if <code>locale</code> is <code>null</code> the
	 * date time is returned in ISO format (2003-08-23Z23:45:12.345+0100)
	 */
	public String toString(Locale locale);

	/**
	 * Returns the String in the given format. if <code>format</code> is <code>null</code> the date time is returned in ISO format
	 * (2003-08-23Z23:45:12.345+0100)
	 */
	public String toString(DateFormat format);

	/**
	 * Converts the IDateTime to string by using the ISO format. You should avoid using this mehtod,
	 * 
	 * @deprecated use one of the other toString methods that accepts a {@link Locale} or {@link DateFormat}
	 */
	@Override
	@Deprecated
	public String toString();

	/**
	 * Returns the {@link TimeZone}
	 */
	public TimeZone getIcuTimeZone();

	/**
	 * Sets the {@link TimeZone}
	 */
	public void setIcuTimeZone(TimeZone tc);

	/**
	 * removes the Date component from the IDateTime
	 */
	public void setAnyDate();

	/**
	 * removes the Time component from the IDateTime
	 */
	public void setAnyTime();

	/**
	 * Sets the local date
	 */
	public void setLocalDate(final int year, final int month, final int day);

	/**
	 * Sets the local time.
	 */
	public void setLocalTime(final int hour, final int minute, final int second, final int hundredth);

	/**
	 * Sets the local date and time
	 */
	public void setLocalTime(final Date date);

	/**
	 * Sets the local date and time
	 */
	public void setLocalTime(final java.util.Calendar calendar);

	/**
	 * Sets the local date and time
	 */
	public void setLocalTime(long timeMillis);

	/**
	 * parses the time for the given locale
	 */
	public void parse(final String time, Locale locale, boolean parseLenient);

	/**
	 * parses the time for the given DateFormat
	 */
	public void parse(final String time, DateFormat dateFormat, boolean parseLenient);

	/**
	 * Sets the local date and time
	 */
	public void setNow();

	/**
	 * Returns the Java {@link Date}
	 */
	public Date toJavaDate();

	/**
	 * Clones the IDateTime instance
	 */
	public IDateTime clone();

	/**
	 * Compares current date with another and returns boolean of whether they are the same.
	 * 
	 * @param comparDate
	 *            DateTime to compare to current date
	 * @return boolean, whether or not the two dates are the same
	 * @since org.openntf.domino 1.0.0
	 */
	public boolean equals(final IDateTime compareDate);

	/**
	 * Compares two DateTimes to see if they are the same time (including millisecond), ignoring date element
	 * 
	 * @param comparDate
	 *            DateTime to compare to the current DateTime
	 * @return boolean true if time is the same
	 * @since org.openntf.domino 1.0.0
	 */
	public boolean equalsIgnoreDate(final IDateTime compareDate);

	/**
	 * Compares two DateTimes to see if they are the same date, ignoring the time element
	 * 
	 * @param comparDate
	 *            DateTime to compare to the current DateTime
	 * @return boolean true if date is the same
	 * @since org.openntf.domino 1.0.0
	 */
	public boolean equalsIgnoreTime(final IDateTime compareDate);

	/**
	 * Compares current date with another and returns boolean of whether current date is after parameter.
	 * 
	 * @param comparDate
	 *            DateTime to compare to current date
	 * @return boolean, whether or not current date is after the parameter
	 * @since org.openntf.domino 1.0.0
	 */
	public boolean isAfter(final IDateTime compareDate);

	/**
	 * Checks whether the DateTime is defined as any time, so just a specific Date
	 * 
	 * @return boolean, whether the DateTime is a date-only value (e.g. [1/1/2013])
	 * @since org.openntf.domino 1.0.0
	 */
	public boolean isAnyTime();

	/**
	 * Checks whether the DateTime is defined as any date, so just a specific Time
	 * 
	 * @return boolean, whether the DateTime is a time-only value (e.g. [1:00 PM])
	 * @since org.openntf.domino 1.0.0
	 */
	public boolean isAnyDate();

	/**
	 * Compares current date with another and returns boolean of whether current date is before parameter.
	 * 
	 * @param comparDate
	 *            DateTime to compare to current date
	 * @return boolean, whether or not current date is before the parameter
	 * @since org.openntf.domino 1.0.0
	 */
	public boolean isBefore(final IDateTime compareDate);

	/**
	 * Returns a Java Calendar object for the DateTime object, same as used internally by org.openntf.domino.DateTime class
	 * 
	 * @return Java Calendar object representing the DateTime object
	 * @since org.openntf.domino 1.0.0
	 */
	public Calendar toJavaCal();

	/**
	 * Sets the date and time to the value of a specific Java Calendar instance
	 * 
	 * @param calendar
	 *            Java calendar instance with relevant date and time
	 * @since org.openntf.domino 1.0.0
	 */
	public void setLocalTime(final com.ibm.icu.util.Calendar calendar);

}
