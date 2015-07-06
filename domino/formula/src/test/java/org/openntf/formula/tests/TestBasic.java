package org.openntf.formula.tests;

import static org.junit.Assert.assertEquals;

import java.text.FieldPosition;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.openntf.domino.commons.IFormulaService;
import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;

public class TestBasic {

	IFormulaService service = ServiceLocator.findApplicationService(IFormulaService.class);

	@Test
	public void testNow() throws FormulaParseException, EvaluateException, ParseException {
		//		System.out.println("Should be ISO: " + service.evaluate("@Now", Locale.GERMAN, Locale.GERMAN));
		//		System.out.println("Should be ISO: " + service.evaluate("@Now", Locale.US, Locale.US));
		//		System.out.println("Should be GERMAN: " + service.evaluate("@Text(@Now)", Locale.GERMAN, Locale.GERMAN));
		//		System.out.println("Should be US: " + service.evaluate("@Text(@Now)", Locale.US, Locale.US));
		//		System.out.println("Should be ISO: " + service.evaluate("1.5", Locale.GERMAN, Locale.GERMAN));
		//		System.out.println("Should be ISO: " + service.evaluate("1.5", Locale.US, Locale.GERMAN));
		//		System.out.println("Should be ISO: " + service.evaluate("@Text(1.5)", Locale.US, Locale.GERMAN));
		//
		//		System.out.println("Date1: " + service.evaluate("@TextToTime({03.07.2015 15:27:47})", Locale.US, Locale.GERMAN));
		//		System.out.println("Date2a: " + service.evaluate("[Jul 3, 2015 3:30:44 PM]", Locale.US, Locale.GERMAN));
		//		System.out.println("Date2b: " + service.evaluate("[03.07.2015 15:30:44]", Locale.GERMAN, Locale.GERMAN));
		//		System.out.println("Delta: "
		//				+ service.evaluate("@TextToTime({03.07.2015 15:27:47}) - [Jul 3, 2015 3:30:44 PM]", Locale.US, Locale.GERMAN));

		//System.out.println(service.evaluate("[10:00]", Locale.GERMAN, Locale.GERMAN));
		System.out.println(service.evaluate("[140:00:21]", Locale.GERMAN, Locale.GERMAN));
		System.out.println("Date 13:00: " + service.evaluate("[2015-07-03] + [13:00]", Locale.GERMAN, Locale.GERMAN));
		System.out.println("Date 12_45: " + service.evaluate("[2015-07-03] + [12:45]", Locale.GERMAN, Locale.GERMAN));
		System.out.println("Date 00:00: " + service.evaluate("[2015-07-03] + [00:00]", Locale.GERMAN, Locale.GERMAN));

		System.out.println("Delta: " + service.evaluate("@TextToTime({2015-07-030})" + //
				"- [14:00]", Locale.US, Locale.GERMAN));
		Locale loc = Locale.US;
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, loc);
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		df.setCalendar(c);
		String s = df.format(c, new StringBuffer(), new FieldPosition(0)).toString();
		System.out.println("Date formatted: " + s);

		System.out.println("Back parsed: " + df.parse(s));
	}

	@Test
	public void testAritmetic() throws FormulaParseException, EvaluateException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("val1", 13);
		data.put("val2", 42);
		List<Object> result = service.evaluate("@Abs(val1-val2)", Locale.GERMAN, Locale.GERMAN, data);
		assertEquals(Integer.valueOf(29), result.get(0));
	}
}
