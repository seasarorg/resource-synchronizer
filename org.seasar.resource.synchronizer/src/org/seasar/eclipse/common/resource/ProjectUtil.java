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
package org.seasar.eclipse.common.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author taichi
 * 
 */
public class ProjectUtil {

	private static List<ICommand> getCommands(IProjectDescription desc,
			String[] ignore) throws CoreException {
		ICommand[] commands = desc.getBuildSpec();
		List<ICommand> newCommands = new ArrayList<ICommand>();
		for (int i = 0; i < commands.length; i++) {
			boolean flag = true;
			for (int k = 0; k < ignore.length; k++) {
				if (commands[i].getBuilderName().equals(ignore[k])) {
					flag = false;
					break;
				}
			}
			if (flag) {
				newCommands.add(commands[i]);
			} else {
				flag = true;
			}
		}
		return newCommands;
	}

	private static void setCommands(IProjectDescription desc,
			List<ICommand> newCommands) {
		desc
				.setBuildSpec(newCommands.toArray(new ICommand[newCommands
						.size()]));
	}

	public static void addBuilders(IProject project, String[] id)
			throws CoreException {
		IProjectDescription desc = project.getDescription();
		List<ICommand> newCommands = getCommands(desc, id);
		for (int i = 0; i < id.length; i++) {
			ICommand command = desc.newCommand();
			command.setBuilderName(id[i]);
			newCommands.add(command);
		}
		setCommands(desc, newCommands);
		project.setDescription(desc, null);
	}

	public static void removeBuilders(IProject project, String[] id)
			throws CoreException {
		IProjectDescription desc = project.getDescription();
		List<ICommand> newCommands = getCommands(desc, id);
		setCommands(desc, newCommands);
		project.setDescription(desc, null);
	}

	public static void addNature(IProject project, String natureID)
			throws CoreException {
		if ((project != null) && project.isAccessible()) {
			IProjectDescription desc = project.getDescription();
			String[] natureIDs = desc.getNatureIds();
			int length = natureIDs.length;
			String[] newIDs = new String[length + 1];
			for (int i = 0; i < length; i++) {
				if (natureIDs[i].equals(natureID)) {
					return;
				}
				newIDs[i] = natureIDs[i];
			}
			newIDs[length] = natureID;
			desc.setNatureIds(newIDs);
			project.setDescription(desc, null);
		}
	}

	public static void removeNature(IProject project, String natureID)
			throws CoreException {
		if ((project != null) && project.isAccessible()) {
			IProjectDescription desc = project.getDescription();
			String[] natureIDs = desc.getNatureIds();
			int length = natureIDs.length;
			for (int i = 0; i < length; i++) {
				if (natureIDs[i].equals(natureID)) {
					String[] newIDs = new String[length - 1];
					System.arraycopy(natureIDs, 0, newIDs, 0, i);
					System.arraycopy(natureIDs, i + 1, newIDs, i, length - i
							- 1);
					desc.setNatureIds(newIDs);
					project.setDescription(desc, null);
					return;
				}
			}
		}
	}

	public static IProjectNature getNature(IProject project, String natureID)
			throws CoreException {
		if ((project != null) && (project.isOpen())) {
			return project.getNature(natureID);
		}
		return null;
	}

	public static boolean hasNature(IProject project, String natureID) {
		try {
			return getNature(project, natureID) != null;
		} catch (CoreException e) {
			return false;
		}
	}

	public static String[] getNatureIds(IProject project) {
		try {
			return project.getDescription().getNatureIds();
		} catch (CoreException e) {
			return new String[0];
		}
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}

	public static IProject[] getAllProjects() {
		return getWorkspaceRoot().getProjects();
	}

	public static IProject getProject(String projectName) {
		return getWorkspaceRoot().getProject(projectName);
	}

}
