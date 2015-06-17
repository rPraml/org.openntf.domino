package org.openntf.domino.commons.logging;

import java.util.Map;

public interface LogFormulaCondHandlerIF {

	public boolean checkAgainstContext(Map<String, Object> context) throws Exception;
}
