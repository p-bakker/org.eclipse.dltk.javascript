/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.javascript.ti;

public class ChildReference extends AbstractReference {

	private final IValueProvider parent;
	private final String name;

	public ChildReference(IValueProvider parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public boolean isReference() {
		if (FUNCTION_OP.equals(name)) {
			return false;
		}
		return parent.isReference();
	}

	@Override
	public IValue getValue() {
		if (parent instanceof IValueCollection) {
			return findChild((IValueCollection) parent, name);
		}
		IValue parentValue = parent.getValue();
		if (parentValue != null) {
			return parentValue.getChild(name);
		}
		return null;
	}

	private static IValue findChild(IValueCollection collection, String name) {
		while (collection != null) {
			final IValue childValue = ((IValueProvider) collection).getValue()
					.getChild(name);
			if (childValue != null) {
				return childValue;
			}
			collection = collection.getParent();
		}
		return null;
	}

	@Override
	public IValue createValue() {
		if (parent instanceof IValueCollection) {
			IValue childValue = findChild((IValueCollection) parent, name);
			if (childValue != null) {
				return childValue;
			}
		}
		IValue parentValue = parent.createValue();
		if (parentValue != null) {
			return parentValue.createChild(name);
		} else {
			return null;
		}
	}

	public ITypeInferenceContext getContext() {
		return parent.getContext();
	}

	public IValueReference getParent() {
		if (parent instanceof IValueReference) {
			return (IValueReference) parent;
		} else {
			return null;
		}
	}

	public void delete() {
		final IValue value = parent.getValue();
		if (value != null) {
			value.deleteChild(name);
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return parent.toString() + "." + name;
	}

}
