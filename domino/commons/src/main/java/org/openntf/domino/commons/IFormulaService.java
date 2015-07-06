package org.openntf.domino.commons;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;

public interface IFormulaService {

	IFormulaService INSTANCE = ServiceLocator.findApplicationService(IFormulaService.class);

	/**
	 * Evaluates the given formula
	 * 
	 * @param formula
	 *            the formula-string
	 * @param parseLocale
	 *            the locale to parse the formula. This locale is used to parse datetimes/numbers in the formula string.
	 * @param solveLocale
	 *            the locale that is used to solve that formula. This locale is used when working with @TextToTime functions etc.
	 * @param map
	 *            the context map
	 * @return the result of the solved formula
	 */
	public List<Object> evaluate(String formula, Locale parseLocale, Locale solveLocale, Map<String, Object> map)
			throws FormulaParseException, EvaluateException;

	/**
	 * Evaluates the given formula (without a context-map)
	 */
	public List<Object> evaluate(String string, Locale parseLocale, Locale solveLocale) throws FormulaParseException, EvaluateException;

	/**
	 * Creates a context for subsequent solving formulas. The locale is used for @TextToTime functions etc.
	 */
	public IFormulaContext createContext(Locale locale, Map<String, Object> map);

	/**
	 * Creates a context for subsequent solving formulas. (without a context-map)
	 */
	public IFormulaContext createContext(Locale locale);

	/**
	 * Parses the formula by using the given locale
	 */
	public IFormula parse(String formula, Locale formulaLocale) throws FormulaParseException;

	/**
	 * Parses the formula by using the given locale and optional support for inline-formulas (you can inline formulas in text with &lt;!..
	 * your formula .. !&gt;
	 */
	public IFormula parse(String formula, Locale formulaLocale, boolean useInlineFormulas) throws FormulaParseException;

}
