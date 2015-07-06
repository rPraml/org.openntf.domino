/* Generated By:JJTree: Do not edit this line. Node.java Version 4.3 */
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

import org.openntf.domino.commons.IFormula;
import org.openntf.formula.FormulaContext;
import org.openntf.formula.FormulaReturnException;
import org.openntf.formula.ValueHolder;

/* All AST nodes must implement this interface.  It provides basic
   machinery for constructing the parent and child relationships
   between nodes. */

public interface Node extends IFormula {

	/**
	 * Create a dump of the AST-Tree to System.out. Useful for debugging
	 * 
	 * @param prefix
	 *            for indention
	 */
	public void dump(final String prefix);

	/**
	 * Hint for formula (if exception occurs)
	 * 
	 */
	public void setFormula(String formula);

	/**
	 * This method is called after the node has been made the current node. It indicates that child nodes can now be added to it.
	 */
	public void jjtOpen();

	public void jjtClose();

	/**
	 * set a new parent to the current node
	 * 
	 */
	public void jjtSetParent(Node n);

	/**
	 * Return the parent of the current node
	 */
	public Node jjtGetParent();

	/**
	 * This method tells the node to add its argument to the node's list of children.
	 */
	public void jjtAddChild(Node n, int i);

	/**
	 * This method returns a child node. The children are numbered from zero, left to right.
	 */
	public Node jjtGetChild(int i);

	/** Return the number of children the node has. */
	public int jjtGetNumChildren();

	public abstract ValueHolder evaluate(FormulaContext ctx) throws FormulaReturnException;

}
/* JavaCC - OriginalChecksum=54dec3b6b2c592c5fbe2fc5be72328d2 (do not edit this line) */
