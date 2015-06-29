package org.openntf.domino.formula.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openntf.domino.commons.IFormulaService;
import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;
import org.openntf.domino.junit.DominoJUnitRunner;

@RunWith(DominoJUnitRunner.class)
public class TestBasic {

	IFormulaService service = ServiceLocator.findApplicationService(IFormulaService.class);

	@Test
	public void testEnvironment() throws FormulaParseException, EvaluateException {
		assertEquals("TestValue 1", service.evaluate("@Environment({$TestEnv1})").get(0));
	}

}
