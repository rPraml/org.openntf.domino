package org.openntf.domino.commons;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;

public interface IFormulaService {

	public List<Object> evaluate(String formula, Map<String, Object> map) throws FormulaParseException, EvaluateException;

	public List<Object> evaluate(String string) throws FormulaParseException, EvaluateException;

	public IFormulaContext createContext(Map<String, Object> map);

	public IFormulaContext createContext(Map<String, Object> map, Locale locale);

	public IFormula parse(String formula) throws FormulaParseException;

	public IFormula parse(String formula, boolean useFocFormula) throws FormulaParseException;

}
