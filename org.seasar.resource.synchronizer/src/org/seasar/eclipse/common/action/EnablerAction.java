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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * @author taichi
 * 
 */
public abstract class EnablerAction implements IWorkbenchWindowActionDelegate,
		IActionDelegate2, IEditorActionDelegate {

	protected IAction delegate;

	protected IDebugEventSetListener debugListener = new IDebugEventSetListener() {
		public void handleDebugEvents(DebugEvent[] events) {
			for (int i = 0; i < events.length; i++) {
				DebugEvent event = events[i];
				if (((DebugEvent.CREATE | DebugEvent.TERMINATE) & event
						.getKind()) != 0) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(
							new Runnable() {
								public void run() {
									maybeEnabled();
								}
							});
				}
			}
		}
	};

	protected abstract boolean checkEnabled();

	protected void maybeEnabled() {
		if (this.delegate == null) {
			return;
		}
		delegate.setEnabled(checkEnabled());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		window.getActivePage().addPartListener(new IPartListener2() {

			public void partActivated(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partDeactivated(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partOpened(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partClosed(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partVisible(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partHidden(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partInputChanged(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}

			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				maybeEnabled();
			}
		});
		window.getWorkbench().addWindowListener(new IWindowListener() {

			public void windowActivated(IWorkbenchWindow window) {
				maybeEnabled();
			}

			public void windowClosed(IWorkbenchWindow window) {
				maybeEnabled();
			}

			public void windowDeactivated(IWorkbenchWindow window) {
				maybeEnabled();
			}

			public void windowOpened(IWorkbenchWindow window) {
				maybeEnabled();
			}

		});

		DebugPlugin.getDefault().addDebugEventListener(debugListener);
		maybeEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		DebugPlugin.getDefault().removeDebugEventListener(debugListener);
		this.delegate = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action) {
		this.delegate = action;
		maybeEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction,
	 *      org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		maybeEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.seasar.eclipse.common.action.AbstractProjectAction#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		maybeEnabled();
	}
}
