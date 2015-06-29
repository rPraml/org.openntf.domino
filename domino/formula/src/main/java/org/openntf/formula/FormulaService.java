package org.openntf.formula;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.IFormulaASTNode;
import org.openntf.domino.commons.IFormulaContext;
import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;
import org.openntf.formula.parse.AtFormulaParserImpl;

/**
 * This is the general factory
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class FormulaService implements org.openntf.domino.commons.IFormulaService {

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

	public FormulaParser getParser() {
		FormulaParser parser = parserCache.get();
		ClassLoader current = Thread.currentThread().getContextClassLoader();
		if (parser == null) {
			parser = getParser(Formatter.getFormatter(), FunctionFactory.createInstance());
			parserCache.set(parser);
		}
		return parser;
	}

	//	public static FormulaParser getMinimalParser() {
	//		return getParser(getFormatter(), FunctionFactory.getMinimalFF());
	//	}
	@Override
	public IFormulaContext createContext(final Map<String, Object> document) {
		return createContext(document, getParser());
	}

	@Override
	public IFormulaContext createContext(final Map<String, Object> document, final Locale locale) {
		return createContext(document, Formatter.getFormatter(locale), getParser());
	}

	public IFormulaContext createContext(final Map<String, Object> document, final FormulaParser parser) {
		return createContext(document, parser == null ? null : parser.getFormatter(), parser);
	}

	public IFormulaContext createContext(final Map<String, Object> document, final Formatter formatter, final FormulaParser parser) {
		return new FormulaContext(document, formatter, parser);
	}

	@Override
	public List<Object> evaluate(final String formula, final Map<String, Object> map) throws FormulaParseException, EvaluateException {
		return parse(formula).solve(map);
	}

	@Override
	public List<Object> evaluate(final String formula) throws FormulaParseException, EvaluateException {
		return evaluate(formula, null);
	}

	@Override
	public IFormulaASTNode parse(final String formula) throws FormulaParseException {
		return getParser().parse(formula);
	}

	@Override
	public IFormulaASTNode parse(final String formula, final boolean useFocFormula) throws FormulaParseException {
		return getParser().parse(formula, useFocFormula);
	}
}
