package org.openntf.formula.impl;

import static com.ibm.icu.util.Calendar.DAY_OF_MONTH;
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
import java.util.Date;
import java.util.Locale;

import org.openntf.domino.commons.IDateTime;
import org.openntf.formula.Formatter;

import com.ibm.icu.util.Calendar;

public class DateTimeImpl implements IDateTime, Externalizable {
	private Locale _locale;
	private Calendar _cal;
	private boolean _noDate = false;
	private boolean _noTime = false;

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(20150211);
		out.writeObject(_locale);
		out.writeLong(_cal.getTimeInMillis());
		out.writeBoolean(_noDate);
		out.writeBoolean(_noTime);
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		in.readInt();
		_locale = (Locale) in.readObject();
		_cal = Calendar.getInstance(_locale);
		_cal.setTimeInMillis(in.readLong());
		_noDate = in.readBoolean();
		_noTime = in.readBoolean();
	}

	/**
	 * @deprecated needed for {@link Externalizable} - do not use!
	 */
	@Deprecated
	public DateTimeImpl() {
	}

	DateTimeImpl(final Locale loc) {
		_locale = loc;
		_cal = Calendar.getInstance(loc);
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
	public void adjustMonth(final int n) {
		if (!_noDate)
			_cal.add(MONTH, n);
	}

	@Override
	public void adjustSecond(final int n) {
		if (!_noTime)
			_cal.add(SECOND, n);
	}

	@Override
	public void adjustYear(final int n) {
		if (!_noDate)
			_cal.add(YEAR, n);
	}

	@Override
	public void convertToZone(final int zone, final boolean isDST) {
		//		int unused;
		// TODO MSt: Timezone support (Really needed?)
	}

	@Override
	public String getDateOnly() {
		if (_noDate)
			return "";
		return Formatter.getFormatter(_locale).formatCalDateOnly(_cal);
	}

	@Override
	public String getLocalTime() {
		if (_noDate)
			return getTimeOnly();
		if (_noTime)
			return getDateOnly();
		return Formatter.getFormatter(_locale).formatCalDateTime(_cal);
	}

	@Override
	public String getTimeOnly() {
		if (_noTime)
			return "";
		return Formatter.getFormatter(_locale).formatCalTimeOnly(_cal);
	}

	@Override
	public int getTimeZone() {
		//		int unused;
		// TODO MSt: Timezone support
		return 0;
	}

	@Override
	public String getZoneTime() {
		//		int unused;
		// TODO MSt: Timezone support
		return null;
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
	public boolean isDST() {
		//		int unused;
		// TODO MSt: Timezone support
		return false;
	}

	@Override
	public void setAnyDate() {
		_noDate = true;
	}

	@Override
	public void setAnyTime() {
		_noTime = true;
	}

	@Override
	public void setLocalDate(final int year, final int month, final int day) {
		_cal.set(YEAR, year);
		_cal.set(MONTH, month - 1);
		_cal.set(DAY_OF_MONTH, day);
		_noDate = false;
	}

	@Override
	public void setLocalTime(final java.util.Calendar otherCal) {
		setLocalTime(otherCal.getTime());
	}

	@Override
	public void setLocalTime(final Date date) {
		_cal.setTime(date);
		_noDate = false;
		_noTime = false;
	}

	@Override
	public void setLocalTime(final int hour, final int minute, final int second, final int hundredth) {
		_cal.set(HOUR_OF_DAY, hour);
		_cal.set(MINUTE, minute);
		_cal.set(SECOND, second);
		_cal.set(MILLISECOND, hundredth * 10);
		_noTime = false;
	}

	@Override
	public void setLocalTime(final String time) {
		setLocalTime(time, true);
	}

	@Override
	public void setLocalTime(final String time, final boolean parseLenient) {
		boolean[] noDT = new boolean[2];
		_cal = Formatter.getFormatter(_locale).parseDateToCal(time, noDT, parseLenient);
		_noDate = noDT[0];
		_noTime = noDT[1];
	}

	@Override
	public void setNow() {
		setLocalTime(new Date());
	}

	@Override
	public int timeDifference(final IDateTime dt) {
		return (int) timeDifferenceDouble(dt);
	}

	@Override
	public double timeDifferenceDouble(final IDateTime dt) {
		// What if isAnyDate or isAnyTime for one of these?
		return (_cal.getTimeInMillis() - dt.toJavaCal().getTimeInMillis()) / 1000;
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
	public String toString() {
		return getLocalTime();
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
}
