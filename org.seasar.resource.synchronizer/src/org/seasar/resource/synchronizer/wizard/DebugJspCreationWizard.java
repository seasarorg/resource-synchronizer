/*
 * Copyright 2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.resource.synchronizer.wizard;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.osgi.framework.Bundle;
import org.seasar.eclipse.common.resource.ResourceUtil;
import org.seasar.resource.synchronizer.Activator;
import org.seasar.resource.synchronizer.nls.Strings;

/**
 * @author taichi
 */
public class DebugJspCreationWizard extends BasicNewResourceWizard implements
		IWizard {

	public static final String NAME = DebugJspCreationWizard.class.getName();

	protected WizardNewFolderMainPage folderMainPage;

	public DebugJspCreationWizard() {
		setDialogSettings(Activator.getSettings());
	}

	@Override
	public void addPages() {
		this.folderMainPage = new WizardNewFolderMainPage(
				Strings.MSG_NEW_DEBUG_JSP, getSelection());
		this.folderMainPage.setDescription(""); // TODO 説明書き。
		addPage(this.folderMainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setWindowTitle(Strings.TITLE_NEW_DEBUG_JSP);
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final IFolder folder = this.folderMainPage.createNewFolder();
		if (folder == null) {
			return false;
		}
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@SuppressWarnings("unchecked")
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("", 0);
				Bundle bundle = Activator.getDefault().getBundle();
				for (Enumeration e = bundle
						.findEntries("debug-info", "*", true); e
						.hasMoreElements();) {
					URL url = (URL) e.nextElement();
					String path = url.getPath();
					if (path.contains(".svn") == false) {
						IPath p = new Path(path).removeFirstSegments(1);
						if (path.endsWith("/")) {
							ResourceUtil.createDir(folder, p.toString());
						} else {
							IFile file = folder.getFile(p);
							if (file.exists() == false) {
								try {
									InputStream stream = new BufferedInputStream(
											url.openStream());
									try {
										file.create(stream, true, monitor);
									} finally {
										stream.close();
									}
								} catch (Exception ex) {
									throw new InvocationTargetException(ex);
								}
							}
						}
					}
				}
			}
		};
		try {
			getContainer().run(true, false, op);
			return true;
		} catch (Exception e) {
			Activator.log(e);
		}
		return false;
	}

}
