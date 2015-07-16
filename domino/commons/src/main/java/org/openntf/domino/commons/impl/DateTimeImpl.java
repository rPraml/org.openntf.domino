package org.openntf.domino.commons.impl;

import static com.ibm.icu.util.Calendar.DAY_OF_MONTH;
import static com.ibm.icu.util.Calendar.HOUR;
import static com.ibm.icu.util.Calendar.HOUR_OF_DAY;
import static com.ibm.icu.util.Calendar.MILLISECOND;
import static com.ibm.icu.util.Calendar.MINUTE;
import static com.ibm.icu.util.Calendar.MONTH;
import static com.ibm.icu.util.Calendar.SECOND;
import static com.ibm.icu.util.Calendar.YEAR;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

import org.openntf.domino.commons.IDateTime;
import org.openntf.domino.commons.utils.TypeUtils;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;

public class DateTimeImpl implements IDateTime, Externalizable, Cloneable {

	private Calendar _cal;
	private boolean _noDate = false;
	private boolean _noTime = false;

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(20150702);
		out.writeObject(null); // formerly: locale
		out.writeLong(_cal.getTimeInMillis());
		out.writeBoolean(_noDate);
		out.writeBoolean(_noTime);
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		in.readInt();
		in.readObject(); // formerly: locale
		_cal.setTimeInMillis(in.readLong());
		_noDate = in.readBoolean();
		_noTime = in.readBoolean();
	}

	public DateTimeImpl() {
		_cal = getCalendarInstance();
	}

	@Override
	public DateTimeImpl clone() {
		try {
			DateTimeImpl other = (DateTimeImpl) super.clone();
			other._cal = (Calendar) _cal.clone();
			other._noDate = _noDate;
			other._noTime = _noTime;
			return other;
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void adjustYear(final int n) {
		if (!_noDate)
			_cal.add(YEAR, n);
	}

	@Override
	public void adjustMonth(final int n) {
		if (!_noDate)
			_cal.add(MONTH, n);
	}

	@Override
	public void adjustDay(final int n) {
		if (!_noDate)
			_cal.add(DAY_OF_MONTH, n);
	}

	@Override
	public void adjustHour(final int n) {
		if (!_noTime)
			_cal.add(HOUR_OF_DAY, n);
	}

	@Override
	public void adjustMinute(final int n) {
		if (!_noTime)
			_cal.add(MINUTE, n);
	}

	@Override
	public void adjustSecond(final int n) {
		if (!_noTime)
			_cal.add(SECOND, n);
	}

	@Override
	public void adjustMilli(final long n) {
		if (!_noTime) {
			_noTime = false;
			_cal.set(HOUR_OF_DAY, 0);
			_cal.set(MINUTE, 0);
			_cal.set(SECOND, 0);
			_cal.set(MILLISECOND, 0);
		}
		_noTime = false;
		System.out.println("Vorher: " + _cal.getTime() + " " + n);
		_cal.setTimeInMillis(_cal.getTimeInMillis() + n);
		System.out.println("Nachher: " + _cal.getTime());

	}

	@Override
	public long getMillis() {
		return _cal.getTimeInMillis();
	}

	@Override
	public String getDateOnly(final Locale locale, final int style) {
		if (_noDate)
			return "";
		if (locale == null)
			return format(ISO.dateFormat());
		return format(DateFormat.getDateInstance(style, locale));
	}

	@Override
	public String getDateOnly(final Locale locale) {
		return getDateOnly(locale, DateFormat.MEDIUM);
	}

	@Override
	public String getTimeOnly(final Locale locale, final int style) {
		if (_noTime)
			return "";
		// TDOO will not work for times > 23:59
		if (locale == null)
			return format(ISO.timeFormat());
		return format(DateFormat.getTimeInstance(style, locale));
	}

	@Override
	public String getTimeOnly(final Locale locale) {
		return getTimeOnly(locale, DateFormat.MEDIUM);
	}

	@Override
	public String toString(final Locale locale, final int dateStyle, final int timeStyle) {
		if (_noDate)
			return getTimeOnly(locale, timeStyle);
		if (_noTime)
			return getDateOnly(locale, dateStyle);
		if (locale == null)
			return format(ISO.dateTimeFormat());
		return format(DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale));

	}

	@Override
	public String toString(final Locale locale, final int style) {
		return toString(locale, style, style);
	}

	@Override
	public String toString(final Locale locale) {
		return toString(locale, DateFormat.MEDIUM, DateFormat.MEDIUM);
	}

	@Override
	@Deprecated
	public String toString() {
		return toIsoString();
	}

	public String toIsoString() {
		if (_noDate && _noTime)
			return "";
		if (_noDate) {
			// TODO: This will not work for times > 23:59
			return format(ISO.timeFormat());
		}
		if (_noTime)
			return format(ISO.dateFormat());
		return format(ISO.dateTimeFormat());
	}

	//	/**
	//	 * Anydate > 23:59:59
	//	 * 
	//	 * @return
	//	 */
	//	protected String toExtendedDate() {
	//		long mil = _cal.getTimeInMillis();
	//		long sec = mil / 1000;
	//		long min = sec / 60;
	//		long hrs = min / 60;
	//		mil = mil % 1000;
	//		sec = sec % 60;
	//		min = min % 60;
	//		StringBuffer sb = new StringBuffer();
	//		Formatter f = new Formatter(sb);
	//		if (mil > 0) {
	//			f.format("x%d:%02%:%02d.%03%", hrs, min, sec, mil);
	//		} else {
	//			f.format("x%d:%02%:%02d", hrs, min, sec);
	//		}
	//		return sb.toString();
	//	}

	@Override
	public String toString(final DateFormat dfmt) {
		if (dfmt == null)
			return toIsoString();
		if (_noDate && _noTime)
			return "";
		if (_noDate) {
			// TODO will not work for time > 23:59
			return format(dfmt);
		}
		if (_noTime)
			return format(dfmt);
		return format(dfmt);
	}

	protected String format(final DateFormat dfmt) {
		dfmt.setCalendar(_cal);
		return dfmt.format(_cal, new StringBuffer(64), new FieldPosition(0)).toString();
	}

	@Override
	public boolean isAnyDate() {
		return _noDate;
	}

	@Override
	public boolean isAnyTime() {
		return _noTime;
	}

	@Override
	public void setAnyDate() {
		if (_noDate)
			return;
		_noDate = true;
		// do not clear fields twice!
		// CHECME RPr: should we clear these fields?
		//		_cal.set(YEAR, 1900);
		//		_cal.set(MONTH, 0);
		//		_cal.set(DAY_OF_MONTH, 1);
		_cal.clear(YEAR);
		_cal.clear(MONTH);
		_cal.clear(DAY_OF_MONTH);
	}

	@Override
	public void setAnyTime() {
		_noTime = true;
		// CHECME RPr: should we clear these fields?
		//		_cal.set(HOUR_OF_DAY, 0);
		//		_cal.set(MINUTE, 0);
		//		_cal.set(SECOND, 0);
		//		_cal.set(MILLISECONDS_IN_DAY, 0);
		_cal.clear(HOUR_OF_DAY);
		_cal.clear(MINUTE);
		_cal.clear(SECOND);
		_cal.clear(MILLISECOND);
	}

	@Override
	public void setLocalDate(final int year, final int month, final int day) {
		_cal.set(YEAR, year);
		_cal.set(MONTH, month - 1);
		_cal.set(DAY_OF_MONTH, day);
		_noDate = false;
	}

	@Override
	public void setLocalTime(final Calendar calendar) {
		_cal = (Calendar) calendar.clone();
		_noTime = !checkTime(_cal);
		_noDate = !checkDate(_cal);
	}

	@Override
	public void setLocalTime(final java.util.Calendar otherCal) {
		_cal.clear();
		for (int i = 0; i < java.util.Calendar.FIELD_COUNT; i++) {
			if (otherCal.isSet(i))
				_cal.set(i, otherCal.get(i));
		}
		_noTime = !checkTime(_cal);
		_noDate = !checkDate(_cal);
	}

	@Override
	public void setLocalTime(final Date date) {
		_cal.setTime(date);
		_noDate = false;
		_noTime = false;
	}

	/**
	 * Checks if the new calendar contains time information and updates missing values (hour, minute, seconds, millis) with zero
	 * 
	 * @return true if cal contains time information
	 */
	protected static boolean checkTime(final Calendar newCal) {
		if (newCal.isSet(HOUR_OF_DAY) || newCal.isSet(HOUR) || newCal.isSet(MINUTE) || newCal.isSet(SECOND)) {
			//			if (!newCal.isSet(HOUR_OF_DAY))
			//				newCal.set(HOUR_OF_DAY, 0);
			if (!newCal.isSet(MINUTE))
				newCal.set(MINUTE, 0);
			if (!newCal.isSet(SECOND))
				newCal.set(SECOND, 0);
			if (!newCal.isSet(MILLISECOND))
				newCal.set(MILLISECOND, 0);
			return true;
		} else {
			// a calendar containing only MILLISECOND is not recognized as time
			newCal.clear(MILLISECOND);
		}
		return false;
	}

	/**
	 * Checks if the new calendar contains date information. If date information was found, a missing year or missing month is completed
	 * with current year/month. A missing day (don't know if possible) with 1. Otherwise the date information is set to 1900-01-01
	 * 
	 * @param newCal
	 * @return
	 */
	protected static boolean checkDate(final Calendar newCal) {
		if (newCal.isSet(YEAR) || newCal.isSet(MONTH) || newCal.isSet(DAY_OF_MONTH)) {
			// it has date, but is not complete
			if (!newCal.isSet(YEAR) || !newCal.isSet(MONTH) || !newCal.isSet(DAY_OF_MONTH)) {
				Calendar now = (Calendar) newCal.clone();
				now.setTime(new Date());
				newCal.set(YEAR, now.get(YEAR));
				if (!newCal.isSet(MONTH))
					newCal.set(MONTH, now.get(MONTH));
				if (!newCal.isSet(DAY_OF_MONTH))
					newCal.set(DAY_OF_MONTH, 1);
			}
			return true;
		}
		newCal.setLenient(true); // to allow Time only like [40:00:00]
		return false;
	}

	@Override
	public void setLocalTime(final int hour, final int minute, final int second, final int hundredth) {
		_cal.set(HOUR_OF_DAY, hour);
		_cal.set(MINUTE, minute);
		_cal.set(SECOND, second);
		_cal.set(MILLISECOND, hundredth * 10);
		_noTime = false;
	}

	protected static boolean parseInternal(final Calendar newCal, final String image, final ParsePosition p, final Locale locale) {
		newCal.setLenient(false);
		newCal.clear();

		p.setErrorIndex(-1);
		p.setIndex(0);

		/*
		 * then attempt: Take a full date-time format MEDIUM
		 */
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
		df.parse(image, newCal, p);
		if (p.getErrorIndex() < 0)
			return true;

		if (!newCal.isSet(DAY_OF_MONTH) || !newCal.isSet(MONTH)) {
			//Try with SHORT format			
			newCal.clear();
			df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
			p.setIndex(0);
			p.setErrorIndex(-1);
			df.parse(image, newCal, p);
			if (!newCal.isSet(DAY_OF_MONTH) || !newCal.isSet(MONTH)) {	// Give up with date
				newCal.clear();
				p.setErrorIndex(0);
			}
		}
		if (newCal.isSet(MINUTE))
			return true;
		/*
		 * If no time found yet (i.e. at least hour+minute like Lotus), try to fish it
		 */
		newCal.clear();
		p.setIndex(p.getErrorIndex());
		p.setErrorIndex(-1);
		df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
		df.parse(image, newCal, p);
		if (newCal.isSet(MINUTE))
			return true;
		//		if (newCal.isSet(DAY_OF_MONTH)) { // Set back possible hour (in accordance with Lotus)
		//			newCal.clear(HOUR);
		//			newCal.clear(HOUR_OF_DAY);
		//			return true;
		//}
		/*
		 * Left: No date found, no time found
		 */
		return false;

	}

	protected Calendar getCalendarInstance() {
		return new GregorianCalendar();
	}

	@Override
	public void parse(final String text, final Locale locale, final boolean parseLenient) {
		String image = text.trim();
		if (image.length() == 0) {
			// Should an empty string lead to a IDateTime with noDate=noTime=true?
			// (Lotus doesn't accept empty strings here.)
			//			_cal.clear();
			//			_noDate = true;
			//			_noTime = true;
			throw new IllegalArgumentException("Illegal date string '" + text + "'");
		}

		// Special NOTES keywords... I don't like them, because they are locale dependent, but....
		char spec = 0;
		if (image.equalsIgnoreCase("TODAY"))
			spec = 'H';
		else if (image.equalsIgnoreCase("TOMORROW"))
			spec = 'M';
		else if (image.equalsIgnoreCase("YESTERDAY"))
			spec = 'G';

		Calendar newCal = getCalendarInstance();
		if (spec != 0) {
			newCal.setTime(new Date());
			if (spec == 'M')
				newCal.add(DAY_OF_MONTH, 1);
			else if (spec == 'G')
				newCal.add(DAY_OF_MONTH, -1);
			_noDate = false;
			_noTime = true;
			_cal = newCal;
			return;
		}
		boolean success = false;
		ParsePosition p = new ParsePosition(0);
		if (locale == null) {
			success = TypeUtils.parseIsoDate(newCal, image, p);
		} else {
			success = parseInternal(newCal, image, p, locale);
			if (!success) {
				// Try if image is in ISO format
				success = TypeUtils.parseIsoDate(newCal, image, p);
			}
		}
		//		System.out.println("Lh=" + image.length() + " Index=" + p.getIndex() + " ErrIndex=" + p.getErrorIndex());
		if (success && !parseLenient) {
			int lh = image.length();
			int errInd = p.getErrorIndex();
			if (errInd >= 0 && errInd < lh) {
				success = false; // there is an error in "image"
			} else if (p.getIndex() < lh) {
				// RPr: Don't agree with that. otherwise, Times like 13:00 cannot be parsed correctly
				// success = false; // image not parsed completely
			}
		}
		if (!success)
			throw new IllegalArgumentException("Illegal date string '" + text + "'. Locale: " + locale);
		boolean hasTime = checkTime(newCal);
		boolean hasDate = checkDate(newCal);
		try {
			newCal.getTime();// verify if it is a valid date
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Illegal date string '" + text + "'. Locale: " + locale + ": " + e.getMessage());
		}
		_cal = newCal;
		_noTime = !hasTime;
		_noDate = !hasDate;
	}

	@Override
	public void parse(final String image, final DateFormat format, final boolean parseLenient) {
		Calendar newCal = getCalendarInstance();
		newCal.setLenient(false);
		ParsePosition p = new ParsePosition(0);
		newCal.clear();
		format.parse(image, newCal, p);
		boolean hasTime = checkTime(newCal);
		boolean hasDate = checkDate(newCal);
		boolean success = hasTime || hasDate;
		if (success && !parseLenient) {
			int lh = image.length();
			int errInd = p.getErrorIndex();
			if (errInd >= 0 && errInd < lh) {
				success = false; // there is an error in "image"
			} else if (p.getIndex() < lh) {
				//success = false; // image not parsed completely
			}
		}
		if (!success)
			throw new IllegalArgumentException("Illegal date string '" + image + "' for format '" + format + "'");
		try {
			newCal.getTime();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Parsing '" + image + "' against '" + format + "' gives Calendar exception: "
					+ e.getMessage());
		}
		_cal = newCal;
		_noDate = !hasDate;
		_noTime = !hasTime;
	}

	@Override
	public void setNow() {
		setLocalTime(new Date());
	}

	@Override
	public Date toJavaDate() {
		return _cal.getTime();
	}

	@Override
	public Calendar toJavaCal() {
		return _cal;
	}

	@Override
	public int compare(final IDateTime sdt1, final IDateTime sdt2) {
		boolean noDate1 = sdt1.isAnyDate();
		boolean noDate2 = sdt2.isAnyDate();
		if (noDate1 != noDate2)
			return (noDate2 ? 1 : -1);
		boolean noTime1 = sdt1.isAnyTime();
		boolean noTime2 = sdt2.isAnyTime();
		if (noDate1) {
			if (noTime1 && noTime2)
				return (0);
			if (noTime1 != noTime2)
				return (noTime2 ? 1 : -1);
		}
		Calendar cal1 = sdt1.toJavaCal();
		Calendar cal2 = sdt2.toJavaCal();
		int i1, i2;
		if (!noDate1) {
			i1 = cal1.get(YEAR);
			i2 = cal2.get(YEAR);
			if (i1 != i2)
				return (i1 > i2 ? 1 : -1);
			i1 = cal1.get(MONTH);
			i2 = cal2.get(MONTH);
			if (i1 != i2)
				return (i1 > i2 ? 1 : -1);
			i1 = cal1.get(DAY_OF_MONTH);
			i2 = cal2.get(DAY_OF_MONTH);
			if (i1 != i2)
				return (i1 > i2 ? 1 : -1);
			if (noTime1 && noTime2)
				return 0;
			if (noTime1 != noTime2)
				return (noTime2 ? 1 : -1);
		}
		i1 = cal1.get(HOUR_OF_DAY);
		i2 = cal2.get(HOUR_OF_DAY);
		if (i1 != i2)
			return (i1 > i2 ? 1 : -1);
		i1 = cal1.get(MINUTE);
		i2 = cal2.get(MINUTE);
		if (i1 != i2)
			return (i1 > i2 ? 1 : -1);
		i1 = cal1.get(SECOND);
		i2 = cal2.get(SECOND);
		if (i1 != i2)
			return (i1 > i2 ? 1 : -1);
		return 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof IDateTime)
			return (compare(this, (IDateTime) o) == 0);
		return super.equals(o);
	}

	@Override
	public TimeZone getIcuTimeZone() {
		return _cal.getTimeZone();
	}

	@Override
	public void setIcuTimeZone(final TimeZone tc) {
		_cal.setTimeZone(tc);
	}

}
