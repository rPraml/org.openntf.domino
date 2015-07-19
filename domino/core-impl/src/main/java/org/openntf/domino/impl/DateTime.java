/*
 * Copyright 2013
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
 */
package org.openntf.domino.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import lotus.domino.NotesException;

import org.openntf.domino.Session;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.commons.IDateTime;
import org.openntf.domino.commons.utils.SafeCast;
import org.openntf.domino.utils.Factory;
import org.openntf.domino.utils.Factory.SessionType;
import org.openntf.domino.utils.ODAUtils;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;

// TODO: Auto-generated Javadoc
/**
 * The Class DateTime.
 */
public class DateTime extends BaseNonThreadSafe<org.openntf.domino.DateTime, lotus.domino.DateTime, Session> implements
		org.openntf.domino.DateTime {
	private static final Logger log_ = Logger.getLogger(DateTime.class.getName());
	private static final long serialVersionUID = 1L;
	private static final int ANY = -1;
	private static final int INNARD_OFFSET = 0x253D8C; // Unix-Timestamp 0

	private IDateTime idt;

	//	int dateInnard;
	//	int timeInnard;

	private void workDone(final lotus.domino.DateTime worker, final boolean reInit) throws NotesException {
		if (reInit)
			this.initialize(worker);
		worker.recycle();
	}

	/**
	 * Instantiates a new date time.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param parent
	 *            the parent
	 * @param wf
	 *            the wrapperfactory
	 * @param cppId
	 *            the cpp-id
	 */
	protected DateTime(final lotus.domino.DateTime delegate, final Session parent) {
		super(delegate, parent, NOTES_TIME);
		initialize(delegate);
		// TODO: Wrapping recycles the caller's object. This may cause issues.
		Base.s_recycle(delegate);
	}

	/**
	 * Needed for clone
	 * 
	 * @param dateTime
	 */
	protected DateTime(final DateTime orig, final Session sess) {
		super(null, sess, NOTES_TIME);
		idt = orig.idt.clone();
	}

	/**
	 * Clones the DateTime object.
	 */
	@Override
	public org.openntf.domino.DateTime clone() {
		return new DateTime(this, getAncestorSession());
	}

	/*
	 * The returned object MUST get recycled
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.impl.Base#getDelegate()
	 */
	@Override
	protected lotus.domino.DateTime getDelegate() {

		int dateInnard = ANY;
		int timeInnard = ANY;
		long millis = idt.getMillis();
		if (!idt.isAnyDate()) {
			dateInnard = (int) (millis / 86400000L) + INNARD_OFFSET;
		}
		if (!idt.isAnyTime()) {
			timeInnard = (int) ((millis % 86400000L) / 10L);
		}

		return ((org.openntf.domino.impl.Session) parent).createNativeDateTime(dateInnard, timeInnard);
	}

	/**
	 * Initialize.
	 * 
	 * @param delegate
	 *            the delegate
	 */
	private void initialize(final lotus.domino.DateTime delegate) {
		try {

			String innards = ((lotus.domino.local.DateTime) delegate).getReplicaID();
			int dateInnard = Integer.valueOf(innards.substring(0, 8), 16);
			int timeInnard = Integer.valueOf(innards.substring(8, 16), 16);
			idt = IDateTime.$.create();

			long millis = dateInnard == ANY ? 0 : ((dateInnard & 0xFFFFFF) - INNARD_OFFSET) * 86400000L;
			if (timeInnard != ANY) {
				millis = timeInnard * 10L;
			}
			idt.setLocalTime(millis);
			if (dateInnard == ANY)
				idt.setAnyDate();
			if (timeInnard == ANY)
				idt.setAnyTime();
			//			dst_ = delegate.isDST();
			//			notesZone_ = delegate.getTimeZone();
			//			if (notesZone_ == -18000000) {
			//				Throwable t = new Throwable();
			//				t.printStackTrace();
			//			}
			//			String s = delegate.getDateOnly();
			//			isTimeOnly_ = (s == null || s.length() == 0);
			//			s = delegate.getTimeOnly();
			//			isDateOnly_ = (s == null || s.length() == 0);
			//			try {
			//				if (isTimeOnly_ && isDateOnly_) {
			//					date_ = null;
			//				} else {
			//					date_ = delegate.toJavaDate();
			//				}
			//			} catch (NotesException e1) {
			//				// System.out.println("Error attempting to initialize a DateTime: " + delegate.getGMTTime());
			//				throw new RuntimeException(e1);
			//			}

		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustDay(int, boolean)
	 */
	@Override
	public void adjustDay(final int n, final boolean preserveLocalTime) {
		// TODO find out what preserveLocalTime means!
		if (preserveLocalTime) {
			idt.adjustDay(n);
		} else {
			try {
				lotus.domino.DateTime worker = getDelegate();
				worker.adjustDay(n, preserveLocalTime);
				workDone(worker, true);
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustDay(int)
	 */
	@Override
	public void adjustDay(final int n) {
		idt.adjustDay(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustHour(int, boolean)
	 */
	@Override
	public void adjustHour(final int n, final boolean preserveLocalTime) {
		if (preserveLocalTime) {
			try {
				lotus.domino.DateTime worker = getDelegate();
				worker.adjustHour(n, preserveLocalTime);
				workDone(worker, true);
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
			}
		} else {
			idt.adjustHour(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustHour(int)
	 */
	@Override
	public void adjustHour(final int n) {
		idt.adjustHour(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMinute(int, boolean)
	 */
	@Override
	public void adjustMinute(final int n, final boolean preserveLocalTime) {
		if (preserveLocalTime) {
			try {
				lotus.domino.DateTime worker = getDelegate();
				worker.adjustMinute(n, preserveLocalTime);
				workDone(worker, true);
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
			}
		} else {
			idt.adjustMinute(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMinute(int)
	 */
	@Override
	public void adjustMinute(final int n) {
		idt.adjustMinute(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMonth(int, boolean)
	 */
	@Override
	public void adjustMonth(final int n, final boolean preserveLocalTime) {
		if (preserveLocalTime) {
			idt.adjustMonth(n);
		} else {
			try {
				lotus.domino.DateTime worker = getDelegate();
				worker.adjustMonth(n, preserveLocalTime);
				workDone(worker, true);
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMonth(int)
	 */
	@Override
	public void adjustMonth(final int n) {
		idt.adjustMonth(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustSecond(int, boolean)
	 */
	@Override
	public void adjustSecond(final int n, final boolean preserveLocalTime) {
		if (preserveLocalTime) {
			try {
				lotus.domino.DateTime worker = getDelegate();
				worker.adjustSecond(n, preserveLocalTime);
				workDone(worker, true);
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
			}
		} else {
			idt.adjustSecond(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustSecond(int)
	 */
	@Override
	public void adjustSecond(final int n) {
		idt.adjustSecond(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustYear(int, boolean)
	 */
	@Override
	public void adjustYear(final int n, final boolean preserveLocalTime) {
		if (preserveLocalTime) {
			idt.adjustYear(n);
		} else {
			try {
				lotus.domino.DateTime worker = getDelegate();
				worker.adjustYear(n, preserveLocalTime);
				workDone(worker, true);
			} catch (NotesException ne) {
				ODAUtils.handleException(ne);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustYear(int)
	 */
	@Override
	public void adjustYear(final int n) {
		idt.adjustYear(n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#convertToZone(int, boolean)
	 */
	@Override
	public void convertToZone(final int zone, final boolean isDST) {
		try {
			lotus.domino.DateTime worker = getDelegate();
			worker.convertToZone(zone, isDST);
			workDone(worker, true);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
		// TODO NTF - find out what this actually does. The documentation is... vague
		//throw new UnimplementedException("convertToZone is not yet implemented.");
	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.ext.DateTime#equals(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean equals(final IDateTime compareDate) {
		return idt.equals(compareDate);
	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.ext.DateTime#equalsIgnoreDate(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean equalsIgnoreDate(final IDateTime compareDate) {
		Calendar cal = idt.toJavaCal();
		Calendar other = compareDate.toJavaCal();

		return (cal.get(Calendar.HOUR_OF_DAY) == other.get(Calendar.HOUR_OF_DAY) && // 
				cal.get(Calendar.MINUTE) == other.get(Calendar.MINUTE) && //
				cal.get(Calendar.SECOND) == other.get(Calendar.SECOND) && //
		cal.get(Calendar.MILLISECOND) == other.get(Calendar.MILLISECOND));

	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.ext.DateTime#equalsIgnoreTime(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean equalsIgnoreTime(final IDateTime compareDate) {
		Calendar cal = idt.toJavaCal();
		Calendar other = compareDate.toJavaCal();
		return (cal.get(Calendar.YEAR) == other.get(Calendar.YEAR) && // 
				cal.get(Calendar.MONTH) == other.get(Calendar.MONTH) && //
		cal.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getDateOnly()
	 */
	@Override
	@Deprecated
	public String getDateOnly() {
		return idt.getDateOnly(Locale.getDefault());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getGMTTime()
	 */
	@Override
	public String getGMTTime() {
		String ret = null;
		try {
			lotus.domino.DateTime worker = getDelegate();
			ret = worker.getGMTTime();
			workDone(worker, false);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getLocalTime()
	 */
	@Override
	public String getLocalTime() {
		String ret = null;
		try {
			lotus.domino.DateTime worker = getDelegate();
			ret = worker.getLocalTime();
			workDone(worker, false);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.impl.Base#getParent()
	 */
	@Override
	public final Session getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getTimeOnly()
	 */
	@Override
	public String getTimeOnly() {
		return idt.getTimeOnly(Locale.getDefault());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getTimeZone()
	 */
	@Override
	public int getTimeZone() {
		// TODO: are these the upper two bits of innard1
		int ret = 0;
		try {
			lotus.domino.DateTime worker = getDelegate();
			ret = worker.getTimeZone();
			workDone(worker, false);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
		return ret;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getZoneTime()
	 */
	@Override
	public String getZoneTime() {
		// TODO NTF - find out what this really does
		String ret = null;
		try {
			lotus.domino.DateTime worker = getDelegate();
			ret = worker.getZoneTime();
			workDone(worker, false);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
		return ret;
		// throw new UnimplementedException("getZoneTime is not yet implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#isAfter(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean isAfter(final IDateTime compareDate) {
		return idt.isAfter(compareDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#isBefore(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean isBefore(final IDateTime compareDate) {
		return idt.isBefore(compareDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#isDST()
	 */
	@Override
	public boolean isDST() {
		return idt.toJavaCal().get(Calendar.DST_OFFSET) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setAnyDate()
	 */
	@Override
	public void setAnyDate() {
		idt.setAnyDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setAnyTime()
	 */
	@Override
	public void setAnyTime() {
		idt.setAnyTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalDate(int, int, int, boolean)
	 */
	@Override
	public void setLocalDate(final int year, final int month, final int day, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getDelegate();
			worker.setLocalDate(year, month, day, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalDate(int, int, int)
	 */
	@Override
	public void setLocalDate(final int year, final int month, final int day) {
		idt.setLocalDate(year, month, day);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(java.util.Calendar)
	 */
	@Override
	public void setLocalTime(final java.util.Calendar calendar) {
		idt.setLocalTime(calendar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ext.DateTime#setLocalTime(com.ibm.icu.util.Calendar)
	 */
	@Override
	public void setLocalTime(final Calendar calendar) {
		idt.setLocalTime(calendar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(java.util.Date)
	 */
	@Override
	public void setLocalTime(final Date date) {
		idt.setLocalTime(date);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(int, int, int, int)
	 */
	@Override
	public void setLocalTime(final int hour, final int minute, final int second, final int hundredth) {
		idt.setLocalTime(hour, minute, second, hundredth);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(java.lang.String)
	 */
	@Override
	public void setLocalTime(final String time) {
		try {
			idt = null;
			lotus.domino.DateTime worker = getDelegate();
			worker.setLocalTime(time);
			workDone(worker, true);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setNow()
	 */
	@Override
	public void setNow() {
		idt.setNow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#timeDifference(lotus.domino.DateTime)
	 */
	@Override
	public int timeDifference(final lotus.domino.DateTime dt) {
		if (dt instanceof IDateTime) {
			SafeCast.longToInt((getMillis() - ((IDateTime) dt).getMillis()) / 1000L);
		}
		Integer[] res = new Integer[1];
		res[0] = new Integer(0);
		timeDifferenceCommon(dt, res);
		return (res[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#timeDifferenceDouble(lotus.domino.DateTime)
	 */
	@Override
	public double timeDifferenceDouble(final lotus.domino.DateTime dt) {
		if (dt instanceof IDateTime) {
			SafeCast.longToDouble((getMillis() - ((IDateTime) dt).getMillis()) / 1000L);
		}
		Double[] res = new Double[1];
		res[0] = new Double(0);
		timeDifferenceCommon(dt, res);
		return (res[0]);
	}

	private void timeDifferenceCommon(final lotus.domino.DateTime dt, final Object[] res) {
		lotus.domino.DateTime dtLocal = dt;
		lotus.domino.DateTime lotusDTTmp = null;
		try {
			if (dtLocal instanceof org.openntf.domino.impl.DateTime) {
				lotusDTTmp = ((org.openntf.domino.impl.DateTime) dtLocal).getDelegate();
				dtLocal = lotusDTTmp;
			}
			lotus.domino.DateTime worker = getDelegate();
			if (res[0] instanceof Integer) {
				res[0] = worker.timeDifference(dtLocal);
			} else if (res[0] instanceof Double) {
				res[0] = worker.timeDifferenceDouble(dtLocal);
			}
			workDone(worker, false);
		} catch (NotesException ne) {
			ODAUtils.handleException(ne);
		} finally {
			if (lotusDTTmp != null)
				s_recycle(lotusDTTmp);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#toJavaDate()
	 */
	@Override
	public Date toJavaDate() {
		return idt.toJavaDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	@Deprecated
	public String toString() {
		return idt.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.types.SessionDescendant#getAncestorSession()
	 */
	@Override
	public final Session getAncestorSession() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ext.DateTime#isAnyDate()
	 */
	@Override
	public boolean isAnyDate() {
		return idt.isAnyDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ext.DateTime#isAnyTime()
	 */
	@Override
	public boolean isAnyTime() {
		return idt.isAnyTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#toJavaCal()
	 */
	@Override
	public Calendar toJavaCal() {
		return idt.toJavaCal();
	}

	@Override
	public int compareTo(final IDateTime arg0) {
		return idt.compareTo(arg0);
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		idt = (IDateTime) in.readObject();
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeObject(idt);
	}

	/**
	 * @deprecated needed for {@link Externalizable} - do not use!
	 */
	@Deprecated
	public DateTime() {
		// it does not matter which session we use here, so we use the current one!
		super(null, Factory.getSession(SessionType.CURRENT), NOTES_TIME);
	}

	/**
	 * Constructs a new DateTime from the given String
	 * 
	 * @param time
	 *            the time string in a notes readable format
	 * @throws java.text.ParseException
	 *             if the time string does not match
	 */
	protected DateTime(final Session parent) {
		super(null, parent, NOTES_TIME);
	}

	/*
	 * A few tiny methods needed for the org.openntf.domino.formula.DateTime interface
	 */
	//	@Override
	//	public int timeDifference(final IDateTime dt) {
	//		if (dt instanceof lotus.domino.DateTime)
	//			return timeDifference((lotus.domino.DateTime) dt);
	//		return (int) timeDifferenceDouble(dt);
	//	}

	//	@Override
	//	public double timeDifferenceDouble(final IDateTime dt) {
	//		if (dt instanceof lotus.domino.DateTime)
	//			return timeDifferenceDouble((lotus.domino.DateTime) dt);
	//		Calendar thisCal = this.toJavaCal();
	//		Calendar thatCal = dt.toJavaCal();
	//		return (thisCal.getTimeInMillis() - thatCal.getTimeInMillis()) * 1000;
	//	}

	//	@Override
	//	public void setLocalTime(final String time, final boolean parseLenient) {
	//		setLocalTime(time);
	//	}

	public DateTime(final IDateTime value, final Session parent) {
		super(null, parent, NOTES_TIME);
		idt = value;
	}

	@Override
	protected WrapperFactory getFactory() {
		return parent.getFactory();
	}

	@Override
	public void adjustMilli(final long n) {
		idt.adjustMilli(n);

	}

	@Override
	public long getMillis() {
		return idt.getMillis();
	}

	@Override
	public String getDateOnly(final Locale locale) {
		return idt.getDateOnly(locale);
	}

	@Override
	public String getDateOnly(final Locale locale, final int style) {
		return idt.getDateOnly(locale, style);
	}

	@Override
	public String getTimeOnly(final Locale locale) {
		return idt.getTimeOnly(locale);
	}

	@Override
	public String getTimeOnly(final Locale locale, final int style) {
		return idt.getTimeOnly(locale, style);
	}

	@Override
	public String toString(final Locale locale, final int dateStyle, final int timeStyle) {
		return idt.toString(locale, dateStyle, timeStyle);
	}

	@Override
	public String toString(final Locale locale, final int dateStyle) {
		return idt.toString(locale, dateStyle);
	}

	@Override
	public String toString(final Locale locale) {
		return idt.toString(locale);
	}

	@Override
	public String toString(final DateFormat format) {
		return idt.toString(format);
	}

	@Override
	public TimeZone getIcuTimeZone() {
		return idt.getIcuTimeZone();
	}

	@Override
	public void setIcuTimeZone(final TimeZone tc) {
		idt.setIcuTimeZone(tc);
	}

	@Override
	public void parse(final String time, final Locale locale, final boolean parseLenient) {
		idt.parse(time, locale, parseLenient);
	}

	@Override
	public void parse(final String time, final DateFormat dateFormat, final boolean parseLenient) {
		idt.parse(time, dateFormat, parseLenient);
	}

	@Override
	public void setLocalTime(final long timeMillis) {
		idt.setLocalTime(timeMillis);
	}

}
