/* Generated By:JJTree: Do not edit this line. ASTSubscript.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
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
 */
package org.openntf.formula.ast;

import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.formula.FormulaContext;
import org.openntf.formula.FormulaReturnException;
import org.openntf.formula.ValueHolder;
import org.openntf.formula.parse.AtFormulaParserImpl;

public class ASTSubscript extends SimpleNode {

	public ASTSubscript(final AtFormulaParserImpl p, final int id) {
		super(p, id);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ValueHolder evaluate(final FormulaContext ctx) throws FormulaReturnException {
		try {
			ValueHolder base = children[0].evaluate(ctx);

			ValueHolder subscript = children[1].evaluate(ctx);

			int idx = subscript.getInt(0); // we need it as INT here
			if (idx < 1 || idx > base.size) {
				throw new IndexOutOfBoundsException("Index " + idx + " not in 1.." + base.size);
			}

			idx--; // Formula is 1 based
			switch (base.dataType) {
			case ERROR:
				return base;

			case DOUBLE:
				return ValueHolder.valueOf(base.getDouble(idx));

			case INTEGER:
				return ValueHolder.valueOf(base.getInt(idx));

			case STRING:
				return ValueHolder.valueOf(base.getString(idx));

			default:
				return ValueHolder.valueOf(base.get(idx));
			}
		} catch (RuntimeException cause) {
			return ValueHolder.valueOf(new EvaluateException(codeLine, codeColumn, cause));
		}
	}

}
/* JavaCC - OriginalChecksum=bf57711d1722e5377ed78d4185bb34a5 (do not edit this line) */
