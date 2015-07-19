package org.openntf.domino.commons.tests;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.openntf.domino.commons.IDateTime;
import org.openntf.domino.commons.impl.DateTimeImpl;

import com.ibm.icu.text.DateFormat;

public class TestDateTimeImpl {
	IDateTime dateTime = new DateTimeImpl();
	IDateTime dateOnly = new DateTimeImpl();
	IDateTime timeOnly = new DateTimeImpl();

	@Before
	public void setUp() throws Exception {
		dateTime.setLocalDate(1979, 8, 17);
		dateTime.setLocalTime(21, 50, 40, 66);

		dateOnly.setLocalDate(1979, 8, 17);
		dateOnly.setAnyTime();

		timeOnly.setAnyDate();
		timeOnly.setLocalTime(21, 50, 40, 0);
	}

	@Test
	public void testBasics() {
		DateTimeImpl ndt = new DateTimeImpl();
		ndt.setNow();
		ndt.setLocalDate(1979, 8, 17);
		ndt.setLocalTime(21, 50, 40, 66);
		System.out.println(ndt);
		ndt.setLocalDate(1982, 8, 17);
		System.out.println(ndt);
		ndt.setLocalDate(1982, 3, 28);
		ndt.setLocalTime(1, 50, 40, 66);
		System.out.println(ndt);
		ndt.adjustHour(2);
		System.out.println(ndt);
		System.out.println("Date1 " + dateTime);
		System.out.println("Date2 " + dateOnly);
		System.out.println("Date3 " + timeOnly);

		dateTime.setNow();
		dateTime.parse("8/17/79 11:00:00 AM", Locale.US, true);
		dateTime.parse("17.08.1979 21:50:40", Locale.GERMAN, false);

		System.out.println("Date1 " + dateTime.toString(Locale.US, DateFormat.LONG, DateFormat.LONG));
		System.out.println("Date1 " + dateTime.toString(Locale.US, DateFormat.MEDIUM, DateFormat.MEDIUM));
		System.out.println("Date1 " + dateTime.toString(Locale.US, DateFormat.SHORT, DateFormat.SHORT));
		dateTime.setNow();

		dateTime.parse("1982-03-28T01:50:40.660+0100", Locale.US, false);
		System.out.println("Parse8 " + dateTime);
		dateTime.parse("August 17, 1979 9:50:40 PM GMT+01:00", Locale.US, true);
		System.out.println("Parse1 " + dateTime);
		dateTime.parse("Aug 17, 1979 9:50:40 PM", Locale.US, true);
		System.out.println("Parse2 " + dateTime);
		dateTime.parse("8/17/79 9:50 PM", Locale.US, true);
		System.out.println("Parse3 " + dateTime);
	}

	//	@Test
	//	public void testAdjustDay() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testAdjustHour() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testAdjustMinute() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testAdjustMonth() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testAdjustSecond() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testAdjustYear() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetDateOnly() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testGetTimeOnly() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testToStringDateFormatArray() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testIsAnyDate() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testIsAnyTime() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetAnyDate() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetAnyTime() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetLocalDate() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetLocalTimeCalendar() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetLocalTimeCalendar1() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetLocalTimeDate() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetLocalTimeIntIntIntInt() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testParseStringLocaleBoolean() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testParseStringDateFormatBoolean() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testSetNow() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testTimeDifferenceMilli() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testToJavaDate() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testToJavaCal() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testToString() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testCompare() {
	//		fail("Not yet implemented");
	//	}
	//
	//	@Test
	//	public void testEqualsObject() {
	//		fail("Not yet implemented");
	//	}

}
