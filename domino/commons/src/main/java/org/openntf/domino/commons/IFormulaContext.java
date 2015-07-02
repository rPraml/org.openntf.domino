package org.openntf.domino.commons;

/**
 * Represents the FormulaContext under which a given formula is executed. A FormulaContext is created by
 * 
 * <pre>
 * IFormulaService service = ServiceLocator.findApplicationService(IFormulaService.class);
 * IFormulaContext ctx = service.createContext(map);
 * </pre>
 * 
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface IFormulaContext {

	void setParam(String string, Object object);

}
