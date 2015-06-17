package org.openntf.formula;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ServiceLoader;

import org.openntf.domino.commons.EvaluateService;
import org.openntf.formula.parse.AtFormulaParserImpl;

/**
 * This is the general factory
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class FormulaService implements EvaluateService {

	private final ThreadLocal<FormulaParser> parserCache = new ThreadLocal<FormulaParser>();
	private final ThreadLocal<Class<FormulaContext>> contextClassCache = new ThreadLocal<Class<FormulaContext>>();
	private final ThreadLocal<FunctionFactory> functionFactoryCache = new ThreadLocal<FunctionFactory>();

	public void initialize() {
		parserCache.set(null);
		functionFactoryCache.set(null);
	}

	public void terminate() {
		parserCache.set(null);
		functionFactoryCache.set(null);
	}

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
		if (parser == null) {
			parser = getParser(Formatter.getFormatter(), getFunctionFactory());
			parserCache.set(parser);
		}
		return parser;
	}

	public FunctionFactory getFunctionFactory() {
		FunctionFactory functionFactory = functionFactoryCache.get();
		if (functionFactory == null) {
			functionFactory = FunctionFactory.createInstance();
			functionFactoryCache.set(functionFactory);
		}
		return functionFactory;
	}

	//	public static FormulaParser getMinimalParser() {
	//		return getParser(getFormatter(), FunctionFactory.getMinimalFF());
	//	}

	public FormulaContext createContext(final Map<String, Object> document, final FormulaParser parser) {
		return createContext(document, parser == null ? null : parser.getFormatter(), parser);
	}

	@SuppressWarnings("unchecked")
	public FormulaContext createContext(final Map<String, Object> document, final Formatter formatter, final FormulaParser parser) {
		Class<FormulaContext> ctxClass = contextClassCache.get();
		FormulaContext instance = null;

		if (ctxClass == null) {
			ServiceLoader<FormulaContext> loader = ServiceLoader.load(FormulaContext.class);
			Iterator<FormulaContext> it = loader.iterator();
			if (it.hasNext()) {
				instance = it.next();
			} else {
				instance = new FormulaContext();
			}
			ctxClass = (Class<FormulaContext>) instance.getClass();
			contextClassCache.set(ctxClass);
		} else {
			try {
				instance = ctxClass.newInstance();
			} catch (InstantiationException e) {
				throw new MissingResourceException("Can't instantiate context: " + e.getMessage(), ctxClass.getName(),
						"InstantiationException");
			} catch (IllegalAccessException e) {
				throw new MissingResourceException("Can't instantiate context: " + e.getMessage(), ctxClass.getName(),
						"IllegalAccessException");

			}
		}
		instance.init(document, formatter, parser);
		return instance;
	}

	public List<Object> evaluate(final String formula, final Map<String, Object> map) throws FormulaParseException, EvaluateException {
		FormulaParser parser = getParser();
		ASTNode node = parser.parse(formula);
		FormulaContext ctx = createContext(map, parser);
		return node.solve(ctx);
	}
}
