/*
 * Â© Copyright FOCONIS AG, 2014
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
package org.openntf.domino.commons.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openntf.domino.commons.AsDocMap;
import org.openntf.domino.commons.IFormula;
import org.openntf.domino.commons.IFormulaContext;
import org.openntf.domino.commons.IFormulaService;
import org.openntf.domino.commons.ServiceLocator;

/**
 * Extends <code>MessageProviderAbstract</code> to handle formulas in text. You can use &lt;! {@literal @}Formula... !&gt; to inline
 * formulas in your .properties or &lt;#1#&gt;..&lt;#9#&gt; to access the optional parameters in getString/getInternalString. If the last
 * optional parameter is a Map&lt;String, Object&gt;, this Map is treated as contextMap in the formula engine.
 * 
 * 
 * @author Roland Praml, FOCONIS AG
 * 
 */
public abstract class FormulaMessageProviderAbstract extends MessageProviderAbstract {
	private static final Logger log_ = Logger.getLogger(FormulaMessageProviderAbstract.class.getName());

	@Override
	protected String getDefaultString(final String bundleName, final String key, final Locale loc) {
		return "[&]Invalid TextID '" + bundleName + "/" + key + "'\t<#1#>\t<#2#>\t<#3#>\t<#4#>\t<#5#>\t<#6#>\t<#7#>\t<#8#>\t<#9#>";
	}

	/** If the raw text contains a formula, this method cooks the text by solving the formula using the optional arguments. */
	@SuppressWarnings("unchecked")
	@Override
	protected String getCookedText(final boolean retDefIfNotAvail, final String bundleName, final String key, final Locale loc,
			final Object... args) {
		String msgString = super.getCookedText(retDefIfNotAvail, bundleName, key, loc, args);
		if (msgString == null) {
			if (retDefIfNotAvail)
				throw new IllegalStateException("getCookedText(true, ...) returned null");
			return null;
		}
		if (!msgString.contains("<!") && !msgString.contains("<#"))
			return msgString;
		int numParams = args.length;
		Map<String, Object> map = null;
		if (numParams != 0) {
			Object o = args[numParams - 1];
			if (o instanceof Map)
				map = (Map<String, Object>) o;
			else if (o instanceof AsDocMap)
				map = ((AsDocMap) o).asDocMap();
			if (map != null)
				numParams--;
		}
		try {
			IFormulaService formulaService = ServiceLocator.findApplicationService(IFormulaService.class);
			if (formulaService == null)
				throw new IllegalStateException("No formula service found");
			IFormula formula = formulaService.parse(msgString, true);
			IFormulaContext ctx = formulaService.createContext(map, loc);
			for (int i = 0; i < numParams; i++)
				ctx.setParam(Integer.toString(i + 1), args[i]);
			List<?> resultList = formula.solve(ctx);
			if (resultList != null && !resultList.isEmpty()) {
				Object o = resultList.get(0);
				if (o instanceof String)
					return (String) o;
			}
			throw new ClassCastException("Formula.solve gave invalid ResultList " + resultList);
		} catch (Throwable t) {
			String ret = "[&]Formula  error in TextID " + key + ": " + t.getMessage() + " (RawText=" + msgString + ")";
			log_.log(Level.SEVERE, ret, t);
			// t.printStackTrace();
			return ret;
		}
	}

}
