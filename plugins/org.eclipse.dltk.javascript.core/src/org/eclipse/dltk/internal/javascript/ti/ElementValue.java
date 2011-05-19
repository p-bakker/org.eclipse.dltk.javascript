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

import static org.eclipse.dltk.javascript.typeinfo.ITypeNames.FUNCTION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.javascript.typeinference.IValueReference;
import org.eclipse.dltk.javascript.typeinference.ReferenceKind;
import org.eclipse.dltk.javascript.typeinference.ReferenceLocation;
import org.eclipse.dltk.javascript.typeinfo.JSTypeSet;
import org.eclipse.dltk.javascript.typeinfo.MemberPredicate;
import org.eclipse.dltk.javascript.typeinfo.TypeMemberQuery;
import org.eclipse.dltk.javascript.typeinfo.TypeUtil;
import org.eclipse.dltk.javascript.typeinfo.model.ArrayType;
import org.eclipse.dltk.javascript.typeinfo.model.ClassType;
import org.eclipse.dltk.javascript.typeinfo.model.Element;
import org.eclipse.dltk.javascript.typeinfo.model.JSType;
import org.eclipse.dltk.javascript.typeinfo.model.MapType;
import org.eclipse.dltk.javascript.typeinfo.model.Member;
import org.eclipse.dltk.javascript.typeinfo.model.Method;
import org.eclipse.dltk.javascript.typeinfo.model.Property;
import org.eclipse.dltk.javascript.typeinfo.model.Type;
import org.eclipse.dltk.javascript.typeinfo.model.TypeInfoModelLoader;
import org.eclipse.dltk.javascript.typeinfo.model.UnionType;
import org.eclipse.emf.common.util.EList;

public abstract class ElementValue implements IValue {

	public static ElementValue findMember(JSType type, String name) {
		return findMember(type, name, MemberPredicate.ALWAYS_TRUE);
	}

	public static ElementValue findMember(JSType type, String name,
			MemberPredicate predicate) {
		final Type t = TypeUtil.extractType(type);
		if (t != null) {
			List<Member> selection = findMembers(t, name, predicate);
			if (!selection.isEmpty()) {
				return new MemberValue(selection.toArray(new Member[selection
						.size()]));
			}
		} else if (type instanceof UnionType) {
			EList<JSType> targets = ((UnionType) type).getTargets();
			for (JSType unionTarget : targets) {
				ElementValue member = findMember(unionTarget, name);
				if (member != null)
					return member;
			}
		}
		return null;
	}

	public static List<Member> findMembers(Type type, String name,
			MemberPredicate predicate) {
		final List<Member> selection = new ArrayList<Member>(4);
		for (Member member : new TypeMemberQuery(type, predicate)) {
			if (name.equals(member.getName())) {
				selection.add(member);
			}
		}
		return selection;
	}

	public static ElementValue createFor(Element element,
			ITypeInferenceContext context) {
		if (element instanceof Method) {
			return new MethodValue((Method) element);
		} else if (element instanceof Property) {
			return new PropertyValue((Property) element, context);
		} else {
			assert element instanceof Type;
			return new TypeValue(JSTypeSet.singleton(TypeUtil
					.ref((Type) element)));
		}
	}

	public static ElementValue createClass(Type type) {
		return new ClassValue(JSTypeSet.singleton(TypeUtil.classType(type)));
	}

	private static class TypeValue extends ElementValue implements IValue {

		private Value arrayLookup;
		private final JSTypeSet types;

		public TypeValue(JSTypeSet types) {
			this.types = types;
		}

		@Override
		protected Type[] getElements() {
			return types.toArray();
		}

		@Override
		public IValue resolveValue(ITypeInferenceContext context) {
			return this;
		}

		public IValue getChild(String name, boolean resolve) {
			if (name.equals(IValueReference.ARRAY_OP)) {
				if (arrayLookup == null)
					arrayLookup = new Value();
				return arrayLookup;
			}
			for (JSType type : types) {
				IValue child = findMember(type, name);
				if (child != null)
					return child;
			}
			return null;
		}

		public JSType getDeclaredType() {
			return types.getFirst();
		}

		public JSTypeSet getDeclaredTypes() {
			return types;
		}

		@Override
		public final JSTypeSet getTypes() {
			return types;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + types;
		}
	}

	private static class ClassValue extends ElementValue implements IValue {

		private final JSTypeSet types;

		public ClassValue(JSTypeSet types) {
			this.types = types;
		}

		@Override
		protected Type[] getElements() {
			return types.toArray();
		}

		public IValue getChild(String name, boolean resolve) {
			// just guess that if the child is the function operator it is a new
			// expression of this type. return then the none static type.
			if (name.equals(IValueReference.FUNCTION_OP)) {
				if (types.size() == 1) {
					final JSType type = types.getFirst();
					if (type instanceof ClassType) {
						return new TypeValue(JSTypeSet.singleton(TypeUtil
								.ref(((ClassType) type).getTarget())));
					}
				}
				final JSTypeSet returnTypes = JSTypeSet.create();
				for (JSType type : types) {
					if (type instanceof ClassType) {
						returnTypes.add(TypeUtil.ref(((ClassType) type)
								.getTarget()));
					} else {
						returnTypes.add(type);
					}
				}
				return new TypeValue(returnTypes);
			}
			for (JSType type : types) {
				IValue child = findMember(type, name,
						MemberPredicate.ALWAYS_TRUE);
				if (child != null)
					return child;
			}
			return null;
		}

		public JSType getDeclaredType() {
			return types.getFirst();
		}

		public JSTypeSet getDeclaredTypes() {
			return types;
		}

		@Override
		public ReferenceKind getKind() {
			return ReferenceKind.TYPE;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + types;
		}
	}

	private static class MethodValue extends ElementValue implements IValue {

		private TypeValue functionOperator;
		private final Method method;

		public MethodValue(Method method) {
			this.method = method;
		}

		@Override
		protected Method getElements() {
			return method;
		}

		@Override
		public IValue resolveValue(ITypeInferenceContext context) {
			return context.valueOf(method);
		}

		@Override
		public ReferenceKind getKind() {
			return ReferenceKind.METHOD;
		}

		public IValue getChild(String name, boolean resolve) {
			if (IValueReference.FUNCTION_OP.equals(name)) {
				if (method.getType() != null) {
					if (functionOperator == null) {
						functionOperator = new TypeValue(
								JSTypeSet.singleton(method.getType()));
					}
					return functionOperator;
				}
			}
			return null;
		}

		public JSType getDeclaredType() {
			return null;
		}

		public JSTypeSet getDeclaredTypes() {
			return JSTypeSet.emptySet();
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + '<' + method + '>';
		}
	}

	private static class PropertyValue extends ElementValue implements IValue {

		protected final ITypeInferenceContext context;
		private final Property property;
		private final Map<String, IValue> children = new HashMap<String, IValue>();

		public PropertyValue(Property property, ITypeInferenceContext context) {
			this.property = property;
			this.context = context;
		}

		@Override
		protected Property getElements() {
			return property;
		}

		@Override
		public IValue resolveValue(ITypeInferenceContext context) {
			return context.valueOf(property);
		}

		@Override
		public ReferenceKind getKind() {
			return ReferenceKind.PROPERTY;
		}

		public IValue getChild(String name, boolean resolve) {
			IValue child = children.get(name);
			if (child == null) {
				if (name.equals(IValueReference.ARRAY_OP)
						&& property.getType() != null) {
					Type arrayType = null;
					if (property.getType() instanceof ArrayType)
						arrayType = TypeUtil.extractType(((ArrayType) property
								.getType()).getItemType());
					if (property.getType() instanceof MapType)
						arrayType = TypeUtil.extractType(((MapType) property
								.getType()).getValueType());
					if (arrayType != null) {
						ElementValue arrayOpChild = createFor(arrayType,
								context);
						children.put(name, arrayOpChild);
						return arrayOpChild;
					}
				}
				ElementValue eValue = ElementValue.findMember(
						property.getType(), name);
				if (eValue != null) {
					child = eValue.resolveValue(context);
					children.put(name, child);
				}
			}
			return child;
		}

		public JSType getDeclaredType() {
			return property.getType();
		}

		public JSTypeSet getDeclaredTypes() {
			if (property.getType() != null) {
				return JSTypeSet.singleton(property.getType());
			} else {
				return JSTypeSet.emptySet();
			}
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + '<' + property + '>';
		}
	}

	private static class MemberValue extends ElementValue implements IValue {

		private TypeValue functionOperator;
		private final Member[] members;

		public MemberValue(Member[] members) {
			this.members = members;
		}

		@Override
		protected Member[] getElements() {
			return members;
		}

		@Override
		public IValue resolveValue(ITypeInferenceContext context) {
			if (members.length == 1) {
				IValue value = context.valueOf(members[0]);
				// copy over the properties of this value.
				value.setDeclaredType(getDeclaredType());
				value.setAttribute(IReferenceAttributes.ELEMENT,
						getAttribute(IReferenceAttributes.ELEMENT));
				return value;
			}
			return this;
		}

		@Override
		public ReferenceKind getKind() {
			for (Member member : members) {
				if (member instanceof Method)
					return ReferenceKind.METHOD;
			}
			return ReferenceKind.PROPERTY;
		}

		public IValue getChild(String name, boolean resolve) {
			if (IValueReference.FUNCTION_OP.equals(name)) {
				JSTypeSet types = null;
				for (Member member : members) {
					if (member instanceof Method) {
						final Method method = (Method) member;
						if (method.getType() != null) {
							if (types == null) {
								types = JSTypeSet.create();
							}
							types.add(method.getType());
						}
					}
				}
				if (types != null) {
					if (functionOperator == null) {
						functionOperator = new TypeValue(types);
					}
					return functionOperator;
				}
			}

			JSType type = getDeclaredType();
			if (type != null) {
				final ElementValue child = ElementValue.findMember(type, name);
				if (child != null) {
					return child;
				}
			}
			return null;
		}

		public JSType getDeclaredType() {
			for (Member member : members) {
				if (member instanceof Property) {
					final Property property = (Property) member;
					if (property.getType() != null) {
						return property.getType();
					}
				} else if (member instanceof Method) {
					return TypeUtil.ref(TypeInfoModelLoader.getInstance()
							.getType(FUNCTION));
				}
			}
			return null;
		}

		public JSTypeSet getDeclaredTypes() {
			JSTypeSet types = null;
			for (Member member : members) {
				if (member instanceof Property) {
					final Property property = (Property) member;
					if (property.getType() != null) {
						if (types == null) {
							types = JSTypeSet.create();
						}
						types.add(property.getType());
					}
				} else if (member instanceof Method) {
					if (types == null) {
						types = JSTypeSet.create();
					}
					types.add(TypeUtil.ref(TypeInfoModelLoader.getInstance()
							.getType(FUNCTION)));
				}
			}
			if (types != null) {
				return types;
			} else {
				return JSTypeSet.emptySet();
			}
		}

	}

	protected abstract Object getElements();

	public IValue resolveValue(ITypeInferenceContext context) {
		return this;
	}

	public final void clear() {
	}

	public final void addValue(IValue src) {
	}

	public final void addReference(IValue src) {
	}

	public final Object getAttribute(String key) {
		return getAttribute(key, false);
	}

	public Object getAttribute(String key, boolean includeReferences) {
		if (IReferenceAttributes.ELEMENT.equals(key)) {
			return getElements();
		}
		if (IReferenceAttributes.HIDE_ALLOWED.equals(key)) {
			final Object elements = getElements();
			if (elements instanceof Element) {
				return ((Element) elements).isHideAllowed() ? Boolean.TRUE
						: null;
			}
		}
		return null;
	}

	public final void setAttribute(String key, Object value) {
	}

	public final Set<String> getDirectChildren() {
		return Collections.emptySet();
	}

	public Set<String> getDeletedChildren() {
		return Collections.emptySet();
	}

	public void deleteChild(String name) {
	}

	public final boolean hasChild(String name) {
		return false;
	}

	public final IValue createChild(String name) {
		return getChild(name, true);
		// throw new UnsupportedOperationException();
	}

	public void putChild(String name, IValue value) {
		throw new UnsupportedOperationException();
	}

	public ReferenceKind getKind() {
		return ReferenceKind.UNKNOWN;
	}

	public final ReferenceLocation getLocation() {
		return ReferenceLocation.UNKNOWN;
	}

	public JSTypeSet getTypes() {
		return JSTypeSet.emptySet();
	}

	public final void setDeclaredType(JSType declaredType) {
	}

	public final void setKind(ReferenceKind kind) {
	}

	public final void setLocation(ReferenceLocation location) {
	}
}
