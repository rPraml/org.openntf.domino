/* Generated By:JJTree: Do not edit this line. ASTSelect.java Version 4.3 */
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

import java.util.Set;

import org.openntf.domino.commons.exception.EvaluateException;
import org.openntf.formula.FormulaContext;
import org.openntf.formula.ValueHolder;
import org.openntf.formula.parse.AtFormulaParserImpl;

public class ASTSelect extends SimpleNode {

	public ASTSelect(final AtFormulaParserImpl p, final int id) {
		super(p, id);
	}

	@Override
	public ValueHolder evaluate(final FormulaContext ctx) {
		try {
			throw new UnsupportedOperationException("SELECT keyword not yet supported. (What should it do?)");
		} catch (RuntimeException cause) {
			return ValueHolder.valueOf(new EvaluateException(codeLine, codeColumn, cause));
		}
	}

	@Override
	protected void analyzeThis(final Set<String> readFields, final Set<String> modifiedFields, final Set<String> variables,
			final Set<String> functions) {
		functions.add("SELECT");
	}
}
/* JavaCC - OriginalChecksum=3cb38fd681e06c11e6004b6246dce786 (do not edit this line) */
