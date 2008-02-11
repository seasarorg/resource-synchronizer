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
package org.seasar.resource.synchronizer.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.eclipse.common.resource.ResourceUtil;
import org.seasar.eclipse.common.runtime.AdaptableUtil;
import org.seasar.resource.synchronizer.Activator;
import org.seasar.resource.synchronizer.util.JavascriptUtil;

/**
 * @author taichi
 * 
 */
public class SelectionServlet extends HttpServlet {

	private static final long serialVersionUID = -6726229074449503493L;

	private static final String NOT_FOUND_CODE = "not found source code";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		boolean scriptTag = false;
		String cb = req.getParameter("callback");

		String classname = req.getParameter("classname");
		final int line = toLine(req.getParameter("line"));
		final int size = toLine(req.getParameter("size"), 5);
		final boolean openInEditor = Boolean.valueOf(req
				.getParameter("openInEditor"));

		int firstline = line;
		String code = NOT_FOUND_CODE;

		try {
			IProject project = ResourceUtil.getProject(req
					.getParameter("project"));
			if (project != null) {
				IJavaProject javap = JavaCore.create(project);
				final IType type = javap.findType(classname);
				if (type != null) {
					IDocument document = toDocument(type);

					if (document != null) {
						int begin = 0;
						int end = line;
						if (size < line) {
							firstline = line - size;
							begin = document.getLineOffset(firstline - 1);
						}
						if (line + 5 < document.getNumberOfLines()) {
							IRegion r = document
									.getLineInformation(line + size);
							end = r.getOffset() + r.getLength();
						} else {
							end = document.getLength() - 1;
						}
						code = document.get(begin, end - begin);
						if (openInEditor) {
							openInEditor(line, type);
						}
					}
				}
			}
		} catch (Exception e) {
			Activator.log(e);
		}

		if (cb != null) {
			scriptTag = true;
			resp.setContentType("text/javascript; charset=utf-8");
		} else {
			resp.setContentType("application/x-json; charset=utf-8");
		}

		PrintWriter out = resp.getWriter();
		if (scriptTag) {
			out.write(cb + "(");
		}
		out.print("{ 'results': 1 ,'codes': [" + "{ 'firstline': '" + firstline
				+ "', 'code': '" + JavascriptUtil.escapeJavaScript(code)
				+ "' } ]" + "}");
		if (scriptTag) {
			out.write(");");
		}
	}

	private IDocument toDocument(final IType type) throws JavaModelException {
		IDocument document = null;
		if (type.isBinary()) {
			IPackageFragmentRoot root = (IPackageFragmentRoot) type
					.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			IPath path = root.getSourceAttachmentPath();
			if (path != null) {
				IOpenable o = type.getOpenable();
				IBuffer buf = o.getBuffer();
				if (buf != null) {
					document = new Document(buf.getContents());
				}
			}
		} else {
			document = new Document(type.getCompilationUnit().getSource());
		}
		return document;
	}

	private void openInEditor(final int line, final IType type) {
		new WorkbenchJob("") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					monitor.beginTask("", 0);
					IEditorPart ep = JavaUI.openInEditor(type);
					if (ep != null) {
						ITextEditor editor = AdaptableUtil.to(ep,
								ITextEditor.class);
						if (editor != null) {
							editor.selectAndReveal(line, 0);
						}
					}
				} catch (Exception e) {
					Activator.log(e);
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	private static final Pattern isNumeric = Pattern.compile("\\d*");

	private static int toLine(String line) {
		return toLine(line, 0);
	}

	private static int toLine(String line, int dv) {
		if (line != null && isNumeric.matcher(line).matches()) {
			return Integer.parseInt(line);
		}
		return dv;
	}
}
