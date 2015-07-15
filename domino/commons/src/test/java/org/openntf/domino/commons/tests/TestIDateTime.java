package org.openntf.domino.commons.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;

import org.junit.Test;
import org.openntf.domino.commons.IDateTime;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

public class TestIDateTime {
	@Test
	public void testFactory() {
		Calendar icuCal = Calendar.getInstance();
		java.util.Calendar javaCal = java.util.Calendar.getInstance();
		Date date = new Date();
		assertFalse(IDateTime.$.create() == null);
		assertFalse(IDateTime.$.create(icuCal) == null);
		assertFalse(IDateTime.$.create(javaCal) == null);
		assertFalse(IDateTime.$.create(date) == null);

		IDateTime iDate = IDateTime.$.create(1979, 8, 17);

		DateFormat format = new SimpleDateFormat("yyyy;MM;dd");
		assertEquals("1979;08;17", iDate.toString(format));

		format = new SimpleDateFormat("yyyy;MM;dd HH*mm*ss-SSS");
		assertEquals("1979;08;17 00*00*00-000", iDate.toString(format));

		iDate = IDateTime.$.create(1979, 8, 16, 26, 80, 90, 212);
		assertEquals("1979;08;17 03*21*32-120", iDate.toString(format));

		iDate = IDateTime.$.parse("1979-08-17T03:21:32.120+0100", null, false);
		assertEquals("1979;08;17 03*21*32-120", iDate.toString(format));

		iDate = IDateTime.$.parse("1979-08-17T03:21:32", null, false);
		assertEquals("1979;08;17 03*21*32-000", iDate.toString(format));

		iDate = IDateTime.$.parse("1979-08-17T03:21", null, false);
		assertEquals("1979;08;17 03*21*00-000", iDate.toString(format));

		iDate = IDateTime.$.parse("1979-08-17", null, false);
		assertEquals("1979;08;17 00*00*00-000", iDate.toString(format));

		format = new SimpleDateFormat("yyyy;MM;dd HH*mm*ss-SSS");

		iDate = IDateTime.$.parse("3:21", null, false);
		assertEquals("1970;01;01 03*21*00-000", iDate.toString(format));
		iDate = IDateTime.$.parse("3:21:40", null, false);
		assertEquals("1970;01;01 03*21*40-000", iDate.toString(format));

		iDate = IDateTime.$.parse("03:21:40.123", null, false);
		assertEquals("1970;01;01 03*21*40-123", iDate.toString(format));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseEx1() {
		IDateTime.$.parse("jetzt", null, false);
	}
}
