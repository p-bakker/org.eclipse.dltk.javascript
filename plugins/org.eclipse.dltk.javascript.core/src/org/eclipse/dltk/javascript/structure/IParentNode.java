/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.javascript.structure;

import org.eclipse.dltk.javascript.ast.Identifier;

public interface IParentNode extends IStructureNode {

	void addLocalReference(Identifier node, IDeclaration resolved);

	void addMethodReference(Identifier identifier, int argCount);

	void addFieldReference(Identifier identifier);

	void addToScope(IStructureNode child);

}
