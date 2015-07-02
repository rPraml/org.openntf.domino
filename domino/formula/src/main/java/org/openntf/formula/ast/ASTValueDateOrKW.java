/* Generated By:JJTree: Do not edit this line. ASTValueDateOrKW.java Version 4.3 */
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

import org.openntf.domino.commons.IDateTime;
import org.openntf.formula.FormulaContext;
import org.openntf.formula.ValueHolder;
import org.openntf.formula.ValueHolder.DataType;
import org.openntf.formula.parse.AtFormulaParserImpl;
import org.openntf.formula.parse.ParseException;

public class ASTValueDateOrKW extends SimpleNode {
	IDateTime dateValue = null;
	String image = null;

	public ASTValueDateOrKW(final AtFormulaParserImpl p, final int id) {
		super(p, id);
	}

	@Override
	public ValueHolder evaluate(final FormulaContext ctx) {
		if (dateValue != null)
			return ValueHolder.valueOf(dateValue);
		ValueHolder vh = ValueHolder.valueOf(image);
		vh.dataType = DataType.KEYWORD_STRING;
		return vh;
	}

	public void init(final String image) throws ParseException {
		String inner = image.substring(1, image.length() - 1); // remove first [ and last ]
		try {
			dateValue = parser.getFormatter().parseDate(inner);
		} catch (IllegalArgumentException e) {
			if (inner.contains(".") || inner.contains("/") || inner.contains("-") || // this MUST be a date
					inner.contains("\\") || inner.contains("\"") || inner.trim().isEmpty()) {
				throw new ParseException(parser, e.getMessage());
			}
		}
		this.image = image; // tried to parse. but this seems to be a Keyword
	}

	@Override
	protected void analyzeThis(final Set<String> readFields, final Set<String> modifiedFields, final Set<String> variables,
			final Set<String> functions) {
	}

}
/* JavaCC - OriginalChecksum=56ca1fdb501387745d81cc4e6f2b1b55 (do not edit this line) */
