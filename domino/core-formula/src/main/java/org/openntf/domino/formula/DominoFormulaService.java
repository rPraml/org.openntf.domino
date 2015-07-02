package org.openntf.domino.formula;

import java.util.Map;

import org.openntf.domino.commons.IFormulaContext;
import org.openntf.formula.Formatter;
import org.openntf.formula.FormulaParser;
import org.openntf.formula.FormulaService;

public class DominoFormulaService extends FormulaService {

	@Override
	protected IFormulaContext newFormulaContext(final Map<String, Object> document, final Formatter formatter, final FormulaParser parser) {
		return new FormulaContextNotes(document, formatter, parser);
	}
}
