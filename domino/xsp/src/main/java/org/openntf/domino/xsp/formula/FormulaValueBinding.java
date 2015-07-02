/*
 * © Copyright FOCONIS AG, 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.domino.xsp.formula;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;

import org.openntf.domino.commons.IFormulaASTNode;
import org.openntf.domino.commons.IFormulaService;
import org.openntf.domino.commons.ServiceLocator;
import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.domino.commons.exception.FormulaParseException;
import org.openntf.domino.xsp.model.DominoDocumentMapAdapter;

import com.ibm.xsp.binding.ValueBindingEx;
import com.ibm.xsp.exception.EvaluationExceptionEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.ValueBindingUtil;

/**
 * Creates a ValueBinding for a formula
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public class FormulaValueBinding extends ValueBindingEx {

	private String formulaStr;
	private transient SoftReference<IFormulaASTNode> FormulaASTNodeCache;

	/**
	 * Constructor
	 * 
	 * @param str
	 *            the formula (without #{...} delimiters)
	 */
	public FormulaValueBinding(final String str) {
		this.formulaStr = (str != null ? str.intern() : null);
	}

	/**
	 * Trivial Constructor (needed for restoreState)
	 */
	public FormulaValueBinding() {
		this.formulaStr = null;
	}

	/**
	 * returns the expected type
	 */
	@Override
	public Class<?> getType(final FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
		return getExpectedType();
	}

	/**
	 * returns the value after the formula was evaluated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(final FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
		boolean rootWasNull = false;
		if (ctx.getViewRoot() == null) {
			rootWasNull = true;
			ctx.setViewRoot(FacesUtil.getViewRoot(getComponent()));
		}

		final DominoDocument dominoDoc = (DominoDocument) ExtLibUtil.resolveVariable(ctx, "currentDocument");
		Map<String, Object> dataMap = null;
		if (dominoDoc instanceof Map) {
			dataMap = (Map<String, Object>) dominoDoc;
		} else {
			dataMap = new DominoDocumentMapAdapter(dominoDoc);
		}
		List<Object> ret = null;
		try {
			IFormulaService service = ServiceLocator.findApplicationService(IFormulaService.class);
			FormulaContextXsp fctx = (FormulaContextXsp) service.createContext(dataMap);
			fctx.init(this.getComponent(), ctx);
			ret = getFormulaASTNode().solve(fctx);
		} catch (EvaluateException e) {
			throw new EvaluationException(e);
		} catch (FormulaParseException e) {
			throw new EvaluationException(e);
		} finally {
			if (rootWasNull) {
				ctx.setViewRoot(null);
			}
		}
		Object firstValue = ret.size() >= 1 ? ret.get(0) : null;

		// RPr: this is similar to the javascript binding
		Class<?> expectedType = getExpectedType();
		if (expectedType != null) {
			if (expectedType == String.class) {
				if (ret.isEmpty()) {
					return null;
				}
				if (ret.size() == 1) {
					return ret.get(0).toString();
				}
				return ret.toString();
			}
			if ((expectedType == Boolean.class) || (expectedType == Boolean.TYPE)) {
				return firstValue == null ? Boolean.FALSE : (Boolean) firstValue;
			}
			if ((expectedType == Character.class) || (expectedType == Character.TYPE)) {
				String str = firstValue == null ? "" : firstValue.toString();
				if (str.length() > 0) {
					return new Character(str.charAt(0));
				}
			}
			if ((expectedType.isPrimitive()) || (Number.class.isAssignableFrom(expectedType))) {
				if ((expectedType == Double.class) || (expectedType == Double.TYPE)) {
					return firstValue == null ? 0d : ((Number) firstValue).doubleValue();
				}
				if ((expectedType == Integer.class) || (expectedType == Integer.TYPE)) {
					return firstValue == null ? 0 : ((Number) firstValue).intValue();

				}
				if ((expectedType == Long.class) || (expectedType == Long.TYPE)) {
					return firstValue == null ? 0l : ((Number) firstValue).longValue();
				}
				if ((expectedType == Byte.class) || (expectedType == Byte.TYPE)) {
					return firstValue == null ? (byte) 0 : ((Number) firstValue).byteValue();
				}
				if ((expectedType == Short.class) || (expectedType == Short.TYPE)) {
					return firstValue == null ? (short) 0 : ((Number) firstValue).shortValue();
				}
				if ((expectedType == Float.class) || (expectedType == Float.TYPE)) {
					return firstValue == null ? 0f : ((Number) firstValue).floatValue();
				}
			}
		}

		return convertToExpectedType(ctx, ret);
	}

	/**
	 * Our valuebindings are read only
	 * 
	 * @return always <code>TRUE</code>
	 */
	@Override
	public boolean isReadOnly(final FacesContext arg0) throws EvaluationException, PropertyNotFoundException {
		return true;
	}

	/**
	 * This is not supported here
	 */
	@Override
	public void setValue(final FacesContext arg0, final Object arg1) throws EvaluationException, PropertyNotFoundException {
		throw new EvaluationExceptionEx("FormulaValueBinding is read-only", this);
	}

	/**
	 * Returns the corresponding {@link IFormulaASTNode} for the formula
	 * 
	 * @return {@link IFormulaASTNode}
	 * @throws FormulaParseException
	 *             if the formula was invalid
	 */
	protected IFormulaASTNode getFormulaASTNode() throws FormulaParseException {
		if (FormulaASTNodeCache != null) {
			IFormulaASTNode node = FormulaASTNodeCache.get();
			if (node != null)
				return node;
		}
		IFormulaService service = ServiceLocator.findApplicationService(IFormulaService.class);
		IFormulaASTNode node = service.parse(formulaStr);
		FormulaASTNodeCache = new SoftReference<IFormulaASTNode>(node);
		return node;

	}

	// --- some methods we have to overwrite
	@Override
	public String getExpressionString() {
		return ValueBindingUtil.getExpressionString(FormulaBindingFactory.FORMULA, this.formulaStr, ValueBindingUtil.RUNTIME_EXPRESSION);
	}

	@Override
	public Object saveState(final FacesContext ctx) {
		Object[] arr = new Object[2];
		arr[0] = super.saveState(ctx);
		arr[1] = this.formulaStr;
		return arr;
	}

	@Override
	public void restoreState(final FacesContext context, final Object obj) {
		Object[] arr = (Object[]) obj;
		super.restoreState(context, arr[0]);
		this.formulaStr = ((String) arr[1]);
	}
}
