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

package org.eclipse.dltk.javascript.formatter.internal.nodes;

import org.eclipse.dltk.formatter.IFormatterDocument;
import org.eclipse.dltk.javascript.formatter.JavaScriptFormatterConstants;

public class ExpressionParensConfiguration extends AbstractParensConfiguration
		implements IParensConfiguration {

	public ExpressionParensConfiguration(IFormatterDocument document) {
		super(document);

		spaceBeforeLpName = JavaScriptFormatterConstants.INSERT_SPACE_BEFORE_LP_EXPRESSION;
		spaceAfterLpName = JavaScriptFormatterConstants.INSERT_SPACE_AFTER_LP_EXPRESSION;
		spaceBeforeRpName = JavaScriptFormatterConstants.INSERT_SPACE_BEFORE_RP_EXPRESSION;
	}

}
