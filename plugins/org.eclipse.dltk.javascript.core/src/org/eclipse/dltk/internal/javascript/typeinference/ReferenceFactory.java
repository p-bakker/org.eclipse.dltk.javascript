/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.javascript.typeinference;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.internal.javascript.reference.resolvers.ReferenceResolverContext;
import org.eclipse.dltk.javascript.typeinference.ITypeProvider;

public class ReferenceFactory {

	private static ITypeProvider[] providers;

	static {
		initProviders();
	}

	private static void initProviders() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(
						"org.eclipse.dltk.javascript.core.customtype");
		IExtension[] extensions = extensionPoint.getExtensions();
		ArrayList providerList = new ArrayList();
		for (int a = 0; a < extensions.length; a++) {
			IConfigurationElement[] configurationElements = extensions[a]
					.getConfigurationElements();
			for (int b = 0; b < configurationElements.length; b++) {

				IConfigurationElement configurationElement = configurationElements[b];
				try {
					Object createExecutableExtension = configurationElement
							.createExecutableExtension("class");
					if (createExecutableExtension instanceof ITypeProvider) {
						providerList.add(createExecutableExtension);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
				// System.out.println(configurationElement.getName());
			}
		}
		ITypeProvider[] pr = new ITypeProvider[providerList.size()];
		providerList.toArray(pr);
		providers = pr;
	}

	public static UnknownReference createNumberReference(String name) {
		return new NativeNumberReference(name);
	}

	public static UnknownReference createStringReference(String name) {
		return new NativeStringReference(name);
	}

	public static UnknownReference createBooleanReference(String name) {
		return new NativeBooleanReference(name);
	}

	public static UnknownReference createArrayReference(String name) {
		return new NativeArrayReference(name);
	}

	public static UnknownReference createDateReference(String name) {
		return new NativeDateReference(name);
	}

	public static UnknownReference createXMLReference(String name) {
		return new NativeXMLReference(name);
	}

	/**
	 * @param paramOrVarName
	 * @param typeLowerCase
	 * @return
	 */
	public static IReference createTypeReference(String paramOrVarName,
			String type, ReferenceResolverContext rrc) {
		if (type != null) {
			String typeLowerCase = type.toLowerCase();
			if ("boolean".equals(typeLowerCase)) {
				return createBooleanReference(paramOrVarName);
			}
			if ("number".equals(typeLowerCase)) {
				return createNumberReference(paramOrVarName);
			}
			if ("string".equals(typeLowerCase)) {
				return createStringReference(paramOrVarName);
			}
			if ("date".equals(typeLowerCase)) {
				return createDateReference(paramOrVarName);
			}
			if ("array".equals(typeLowerCase)) {
				return createArrayReference(paramOrVarName);
			}
			if ("xml".equals(typeLowerCase)) {
				return createXMLReference(paramOrVarName);
			}

			if (providers != null) {
				for (int i = 0; i < providers.length; i++) {
					ITypeProvider element = (ITypeProvider) providers[i];
					IReference ref = element.createTypeReference(
							paramOrVarName, type, rrc);
					if (ref != null)
						return ref;
				}
			}

		}
		return new UnknownReference(paramOrVarName, false);
	}

}
