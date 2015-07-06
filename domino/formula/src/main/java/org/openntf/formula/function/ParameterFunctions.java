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

package org.openntf.formula.function;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.Map;
import java.util.MissingResourceException;

import org.openntf.domino.commons.IDateTime;
import org.openntf.formula.Formatter;
import org.openntf.formula.FormulaContext;
import org.openntf.formula.Function;
import org.openntf.formula.FunctionFactory;
import org.openntf.formula.FunctionSet;
import org.openntf.formula.ValueHolder;
import org.openntf.formula.ValueHolder.DataType;
import org.openntf.formula.annotation.ParamCount;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

/*----------------------------------------------------------------------------*/
public enum ParameterFunctions {

	;

	public static class Functions extends FunctionSet {
		private static final Map<String, Function> functionSet = FunctionFactory.getFunctions(ParameterFunctions.class);

		@Override
		public Map<String, Function> getFunctions() {
			return functionSet;
		}
	}

	/*----------------------------------------------------------------------------*/
	/*
	 * @FocDate
	 */
	/*----------------------------------------------------------------------------*/
	private static int getRelativePart(final FormulaContext ctx, final Object o, final boolean ultimoAllowed, final boolean[] auxRes) {
		if (o instanceof Number) {
			int part = ((Number) o).intValue();
			auxRes[0] = (part > 0);
			return part;
		}
		if (!(o instanceof String))
			throw new IllegalArgumentException("Expected Number or String, got " + o.getClass());
		String s = (String) o;
		if (ultimoAllowed && s.equalsIgnoreCase("ultimo")) {
			auxRes[0] = true;
			auxRes[1] = true;
			return 1;
		}
		int part = ctx.getFormatter().parseNumber(s, false).intValue();
		auxRes[0] = (part > 0 && Character.isDigit(s.charAt(0)));
		return part;
	}

	/*----------------------------------------------------------------------------*/
	@ParamCount(1)
	public static ValueHolder atP(final FormulaContext ctx, final ValueHolder[] params) {
		return ValueHolder.valueOf(ctx.getParam(Integer.toString(params[0].getInt(0))));
	}

	/*----------------------------------------------------------------------------*/
	@ParamCount(0)
	public static ValueHolder atP1(final FormulaContext ctx) {
		return p1To9(ctx, 1);
	}

	@ParamCount(0)
	public static ValueHolder atP2(final FormulaContext ctx) {
		return p1To9(ctx, 2);
	}

	@ParamCount(0)
	public static ValueHolder atP3(final FormulaContext ctx) {
		return p1To9(ctx, 3);
	}

	@ParamCount(0)
	public static ValueHolder atP4(final FormulaContext ctx) {
		return p1To9(ctx, 4);
	}

	@ParamCount(0)
	public static ValueHolder atP5(final FormulaContext ctx) {
		return p1To9(ctx, 5);
	}

	@ParamCount(0)
	public static ValueHolder atP6(final FormulaContext ctx) {
		return p1To9(ctx, 6);
	}

	@ParamCount(0)
	public static ValueHolder atP7(final FormulaContext ctx) {
		return p1To9(ctx, 7);
	}

	@ParamCount(0)
	public static ValueHolder atP8(final FormulaContext ctx) {
		return p1To9(ctx, 8);
	}

	@ParamCount(0)
	public static ValueHolder atP9(final FormulaContext ctx) {
		return p1To9(ctx, 9);
	}

	private static ValueHolder p1To9(final FormulaContext ctx, final int i) {
		return atP(ctx, new ValueHolder[] { ValueHolder.valueOf(i) });
	}

	/*----------------------------------------------------------------------------*/
}
