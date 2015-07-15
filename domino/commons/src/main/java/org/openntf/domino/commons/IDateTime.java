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

import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

/**
 * This is the DateTime interface that is used in formulas. It is very similar to the org.opennft.domino.DateTime interface but this has no
 * dependency to the lotus API, so that the formula engine can be used in a non-notes environmment.
 */
public interface IDateTime extends Comparator<IDateTime> {

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
	 * Increments/decrements a IDateTime by the number of years you specify.
	 */
	public void adjustYear(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of month you specify.
	 */
	public void adjustMonth(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of days you specify.
	 */
	public void adjustDay(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of hours you specify.
	 */
	public void adjustHour(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of minutes you specify.
	 */
	public void adjustMinute(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of seconds you specify.
	 */
	public void adjustSecond(final int n);

	/**
	 * Increments/decrements a IDateTime by the number of milliseconds you specify.
	 */
	public void adjustMilli(final long n);

	/**
	 * Returns the Milliseconds since 1970-01-01
	 * 
	 * @return
	 */
	public long getMillis();

	// Converting date to string without specifying a locale is always a bad idea
	/**
	 * Returns a "Date Only" representation, formatted for the given locale and style {@link DateFormat#MEDIUM}
	 */
	public String getDateOnly(Locale locale);

	/**
	 * Returns a "Date Only" representation, formatted for the given locale and the given style (see {@link DateFormat})
	 */
	public String getDateOnly(Locale locale, int style);

	/**
	 * Returns a "Date Only" representation, formatted for the given locale and style {@link DateFormat#MEDIUM}
	 */
	public String getTimeOnly(Locale locale);

	/**
	 * Returns a "Date Only" representation, formatted for the given locale and the given style (see {@link DateFormat})
	 */
	public String getTimeOnly(Locale locale, int style);

	/**
	 * Returns the String representation for the given locale and the given styles (see {@link DateFormat})
	 */
	public String toString(Locale locale, int dateStyle, int timeStyle);

	/**
	 * Returns the String representation for the given locale and the given styles (see {@link DateFormat})
	 */
	public String toString(Locale locale, int dateStyle);

	/**
	 * Returns the String representation for the given locale and {@link DateFormat#MEDIUM}
	 */
	public String toString(Locale locale);

	/**
	 * Returns the String in the given format
	 */
	public String toString(DateFormat format);

	/**
	 * Converts the IDateTime to string by using the Default system locale. You should avoid using this method.
	 * 
	 * @deprecated use one of the other toString methods that accepts a {@link Locale} or {@link DateFormat}
	 */
	@Override
	@Deprecated
	public String toString();

	// int for timeZone is also a bad idea - the whole TimeZone support seems to be very old in Domino 
	//public int getTimeZone();

	// public void convertToZone(final int zone, final boolean isDST);

	// public String getZoneTime();

	public TimeZone getIcuTimeZone();

	public void setIcuTimeZone(TimeZone tc);

	/**
	 * Returns <code>true</code> if IDateTime does not contain a Date-component (=time-only)
	 */
	public boolean isAnyDate();

	/**
	 * Returns <code>true</code> if IDateTime does not contain a Time-component (=date-only)
	 */
	public boolean isAnyTime();

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
	public void setLocalTime(final com.ibm.icu.util.Calendar calendar);

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
	 * Returns the ICU {@link Calendar}
	 */
	public com.ibm.icu.util.Calendar toJavaCal();

	/**
	 * Clones the IDateTime instance
	 */
	public IDateTime clone();

}
