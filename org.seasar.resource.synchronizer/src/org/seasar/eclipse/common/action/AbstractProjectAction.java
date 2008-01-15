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
package org.seasar.eclipse.common.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.seasar.eclipse.common.resource.LogUtil;
import org.seasar.eclipse.common.resource.ResourceUtil;
import org.seasar.eclipse.common.runtime.AdaptableUtil;

/**
 * @author taichi
 * 
 */
public abstract class AbstractProjectAction implements IActionDelegate {

	private IProject project = null;

	/**
	 * 
	 */
	public AbstractProjectAction() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object o = ss.getFirstElement();
			this.project = AdaptableUtil.to(o, IProject.class);
			if (this.project == null) {
				IResource r = AdaptableUtil.to(o, IResource.class);
				if (r != null) {
					this.project = r.getProject();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		try {
			if (this.project == null) {
				this.project = ResourceUtil.getCurrentSelectedProject();
			}
			if (this.project != null) {
				run(action, this.project);
			}
		} catch (CoreException e) {
			LogUtil.log(ResourcesPlugin.getPlugin(), e);
		}
	}

	public abstract void run(IAction action, IProject project)
			throws CoreException;
}
