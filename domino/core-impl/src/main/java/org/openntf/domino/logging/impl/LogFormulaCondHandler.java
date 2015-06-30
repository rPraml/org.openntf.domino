package org.openntf.domino.logging.impl;

import java.util.Map;

import org.openntf.domino.commons.logging.LogFormulaCondHandlerIF;

public class LogFormulaCondHandler implements LogFormulaCondHandlerIF {

	//	static ThreadLocal<FormulaParser> _myFormulaParser = new ThreadLocal<FormulaParser>() {
	//		@Override
	//		protected FormulaParser initialValue() {
	//			return Formulas.getMinimalParser();
	//		}
	//	};
	//
	//	private ASTNode _parsedCond;
	//
	//	private LogFormulaCondHandler(final ASTNode parsedCond) {
	//		_parsedCond = parsedCond;
	//	}

	public static LogFormulaCondHandler getInstance(final String formulaCondition) throws Exception {
		//		return new LogFormulaCondHandler(_myFormulaParser.get().parse(formulaCondition));
		return new LogFormulaCondHandler();
	}

	@Override
	public boolean checkAgainstContext(final Map<String, Object> context) throws Exception {
		return false;
		//		FormulaContext ctx = Formulas.createContext(context, _myFormulaParser.get());
		//		List<Object> result = _parsedCond.solve(ctx);
		//		if (result == null || result.size() != 1)
		//			return false;
		//		Object o = result.get(0);
		//		return (o instanceof Boolean) && (Boolean) o;
	}

}
