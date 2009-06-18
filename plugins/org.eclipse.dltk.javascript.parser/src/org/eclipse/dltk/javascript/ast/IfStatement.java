/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Vladimir Belov)
 *******************************************************************************/

package org.eclipse.dltk.javascript.ast;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ast.ASTNode;

public class IfStatement extends Statement {

	private Keyword ifKeyword;
	private Keyword elseKeyword;
	private Expression condition;
	private Statement thenStatement;
	private Statement elseStatement = null;
	private int LP = -1;
	private int RP = -1;

	public IfStatement(ASTNode parent) {
		super(parent);
	}

	public Expression getCondition() {
		return this.condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public Statement getThenStatement() {
		return this.thenStatement;
	}

	public void setThenStatement(Statement thenStatement) {
		this.thenStatement = thenStatement;
	}

	public Statement getElseStatement() {
		return this.elseStatement;
	}

	public void setElseStatement(Statement elseStatement) {
		this.elseStatement = elseStatement;
	}

	public Keyword getIfKeyword() {
		return this.ifKeyword;
	}

	public void setIfKeyword(Keyword keyword) {
		this.ifKeyword = keyword;
	}

	public Keyword getElseKeyword() {
		return this.elseKeyword;
	}

	public void setElseKeyword(Keyword keyword) {
		this.elseKeyword = keyword;
	}

	public int getLP() {
		return this.LP;
	}

	public void setLP(int LP) {
		this.LP = LP;
	}

	public int getRP() {
		return this.RP;
	}

	public void setRP(int RP) {
		this.RP = RP;
	}

	public String toSourceString(String indentationString) {

		Assert.isTrue(sourceStart() >= 0);
		Assert.isTrue(sourceEnd() > 0);
		Assert.isTrue(LP > 0);
		Assert.isTrue(RP > 0);

		StringBuffer buffer = new StringBuffer();

		buffer.append(indentationString);
		buffer.append(Keywords.IF);
		buffer.append(" (");
		buffer.append(getCondition().toSourceString(indentationString));
		buffer.append(")\n");

		buffer.append(getThenStatement().toSourceString(
				getThenStatement().isBlock() ? indentationString
						: indentationString + INDENT));

		if (getElseStatement() != null) {
			buffer.append(indentationString);
			buffer.append(Keywords.ELSE);
			buffer.append("\n");
			buffer.append(getElseStatement().toSourceString(
					getElseStatement().isBlock() ? indentationString
							: indentationString + INDENT));
		}

		return buffer.toString();
	}

	public boolean isBlock() {
		return true;
	}

}
