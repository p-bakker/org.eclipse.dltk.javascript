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

package org.eclipse.dltk.javascript.formatter.preferences;

import java.net.URL;

import org.eclipse.dltk.javascript.formatter.JavaScriptFormatterConstants;
import org.eclipse.dltk.ui.formatter.FormatterModifyTabPage;
import org.eclipse.dltk.ui.formatter.IFormatterControlManager;
import org.eclipse.dltk.ui.formatter.IFormatterModifyDialog;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class JavaScriptNewLinesTabPage extends FormatterModifyTabPage {

	public JavaScriptNewLinesTabPage(IFormatterModifyDialog dialog) {
		super(dialog);
	}

	// private class InitializeListener extends SelectionAdapter implements
	// IFormatterControlManager.IInitializeListener {
	//
	// private final IFormatterControlManager manager;
	//
	// public InitializeListener(IFormatterControlManager manager) {
	// this.manager = manager;
	// }
	//
	// public void initialize() {
	// keepEmptyArray.setEnabled(!manager.getString(
	// JavaScriptFormatterConstants.BRACE_ARRAY).equals(
	// JavaScriptFormatterConstants.BRACE_SAME_LINE));
	// }
	// }

	Combo arrayInitializer;
	Button keepEmptyArray;

	protected void createOptions(IFormatterControlManager manager,
			Composite parent) {

		// Insert new lines

		Group insertNewLinesGroup = SWTFactory.createGroup(parent,
				Messages.JavaScriptNewLinesTabPage_InsertNewLineGroup_name, 1,
				1, GridData.FILL_HORIZONTAL);

		manager.createCheckbox(insertNewLinesGroup,
				JavaScriptFormatterConstants.NEW_LINE_IN_EMPTY_METHOD,
				Messages.JavaScriptBracesTabPage_NewLineInEmptyMethodBody_name);

		manager.createCheckbox(insertNewLinesGroup,
				JavaScriptFormatterConstants.NEW_LINE_IN_EMPTY_BLOCK,
				Messages.JavaScriptBracesTabPage_NewLineInEmptyBlock_name);

		manager.createCheckbox(insertNewLinesGroup,
				JavaScriptFormatterConstants.NEW_LINE_AT_EOF,
				Messages.JavaScriptBracesTabPage_NewLineAtEndOfFile_name);

		// Object initializers

		// Group objectInitializersGroup = SWTFactory
		// .createGroup(
		// parent,
		// Messages.JavaScriptNewLinesTabPage_ObjectInitializersGroup_name,
		// 1, 1, GridData.FILL_HORIZONTAL);
		//
		// // Array initializers
		//
		// Group arrayInitializersGroup = SWTFactory
		// .createGroup(
		// parent,
		// Messages.JavaScriptNewLinesTabPage_ArrayInitializersGroup_name,
		// 1, 1, GridData.FILL_HORIZONTAL);
		//
		// // Object initializers
		//
		// Group emptyStatementsGroup = SWTFactory
		// .createGroup(
		// parent,
		// Messages.JavaScriptNewLinesTabPage_EmptyStatementsGroup_name,
		// 1, 1, GridData.FILL_HORIZONTAL);
	}

	protected URL getPreviewContent() {
		return getClass().getResource("new-lines-preview.js"); //$NON-NLS-1$
	}

}
