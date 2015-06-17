package org.openntf.formula.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openntf.domino.commons.FormulaService;
import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;

public class TestBasic {

	FormulaService service = ServiceLocator.findApplicationService(FormulaService.class);

	@Test
	public void testNow() throws FormulaParseException, EvaluateException {
		System.out.println(service.evaluate("@Now"));
	}

	@Test
	public void testAritmetic() throws FormulaParseException, EvaluateException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("val1", 13);
		data.put("val2", 42);
		List<Object> result = service.evaluate("@Abs(val1-val2)", data);
		assertEquals(Integer.valueOf(29), result.get(0));
	}
}
