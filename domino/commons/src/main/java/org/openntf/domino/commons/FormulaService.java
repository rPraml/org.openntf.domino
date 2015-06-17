package org.openntf.domino.commons;

import java.util.List;
import java.util.Map;

import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;

public interface FormulaService {

	public abstract List<Object> evaluate(String formula, Map<String, Object> map) throws FormulaParseException, EvaluateException;

	public abstract List<Object> evaluate(String string) throws FormulaParseException, EvaluateException;

	public abstract Object createContext(Map<String, Object> map);

	public abstract FormulaASTNode parse(String formula) throws FormulaParseException;

}
