/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.javascript.typeinference;

public interface IFunctionValueCollection extends IValueCollection {
	/**
	 * Tests if this object represents code like the following:
	 * 
	 * <pre>
	 * (function() { .... })();
	 * </pre>
	 */
	boolean isInlineBlock();

	/**
	 * The name of the function
	 * 
	 * @return String the name of the function
	 */
	String getFunctionName();

}
