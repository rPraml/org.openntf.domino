package org.openntf.formula;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.IFormula;
import org.openntf.domino.commons.IFormulaContext;
import org.openntf.domino.commons.IRequest;
import org.openntf.domino.commons.IRequestLifeCycle;
import org.openntf.domino.commons.StandardLifeCycle;
import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;
import org.openntf.formula.parse.AtFormulaParserImpl;

/**
 * This is the general factory for Formula engine
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class FormulaService extends StandardLifeCycle implements org.openntf.domino.commons.IFormulaService, IRequestLifeCycle {

	private final ThreadLocal<FormulaParser> parserCache = new ThreadLocal<FormulaParser>();

	/**
	 * This function returns a preconfigured default instance
	 */
	public FormulaParser getParser(final Formatter formatter, final FunctionFactory factory) {
		AtFormulaParserImpl parser = new AtFormulaParserImpl(new java.io.StringReader(""));
		parser.reset();
		parser.formatter = formatter;
		parser.functionFactory = factory;

		return parser;
	}

	protected FormulaParser getParserCached(final Formatter fmt) {
		FormulaParser parser = parserCache.get();
		if (parser == null || parser.getFormatter() != fmt) {
			parser = getParser(fmt, FunctionFactory.createInstance());
			parserCache.set(parser);
		}
		return parser;
	}

	protected FormulaParser getParserCached(final Locale parseLocale) {
		return getParserCached(Formatter.getFormatter(parseLocale));
	}

	@Override
	public List<Object> evaluate(final String formula, final Locale parseLocale, final Locale solveLocale, final Map<String, Object> map)
			throws FormulaParseException, EvaluateException {
		return getParserCached(parseLocale).parse(formula).solve(createContext(solveLocale, map));
	}

	@Override
	public List<Object> evaluate(final String formula, final Locale parseLocale, final Locale solveLocale) throws FormulaParseException,
	EvaluateException {
		return getParserCached(parseLocale).parse(formula).solve(createContext(solveLocale));
	}

	@Override
	public IFormulaContext createContext(final Locale locale, final Map<String, Object> map) {
		return newFormulaContext(map, Formatter.getFormatter(locale), getParserCached(locale)); // parser is used for @Eval e.g.
	}

	@Override
	public IFormulaContext createContext(final Locale locale) {
		return newFormulaContext(null, Formatter.getFormatter(locale), getParserCached(locale)); // parser is used for @Eval e.g.

	}

	protected IFormulaContext newFormulaContext(final Map<String, Object> map, final Formatter formatter, final FormulaParser parser) {
		return new FormulaContext(map, formatter, parser);
	}

	@Override
	public IFormula parse(final String formula, final Locale formulaLocale) throws FormulaParseException {
		return getParserCached(formulaLocale).parse(formula);
	}

	@Override
	public IFormula parse(final String formula, final Locale formulaLocale, final boolean useInlineFormulas) throws FormulaParseException {
		return getParserCached(formulaLocale).parse(formula, useInlineFormulas);
	}

	@Override
	public void beforeRequest(final IRequest request) {

	}

	@Override
	public void afterRequest() {
		parserCache.set(null);
	}

}
