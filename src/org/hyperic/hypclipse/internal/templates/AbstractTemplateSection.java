/**********************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Icons under 'icons/fugue' are made by Yusuke Kamiyamane http://www.pinvoke.com/.
 * Licensed under a Creative Commons Attribution 3.0 license.
 * <http://creativecommons.org/licenses/by/3.0/>
 *
 *********************************************************************************/
package org.hyperic.hypclipse.internal.templates;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.wizard.Wizard;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.plugin.IPluginModelBase;
import org.hyperic.hypclipse.plugin.ITemplateSection;

/**
 * Common function for template sections. It is recommended to subclass this
 * class rather than implementing ITemplateSection directly when providing
 * extension templates.
 * 
 */

public abstract class AbstractTemplateSection implements ITemplateSection, IVariableProvider {

	/**
	 * The project handle.
	 */
	protected IProject project;
	
	/**
	 * The plug-in model.
	 */
	protected IPluginModelBase model;
	
	/**
	 * The key for the main plug-in class of the plug-in that the template is
	 * used for (value="pluginClass").  The return value is a fully-qualified class name.
	 */
	public static final String KEY_PLUGIN_CLASS = "pluginClass";

	/**
	 * The key for the plug-in name of the plug-in that the template is used for
	 * (value="pluginName").
	 */
	public static final String KEY_PLUGIN_NAME = "pluginName";

	/**
	 * The key for the process name of the plug-in that the template is used for
	 * (value="processName").
	 */
	public static final String KEY_PROCESS_NAME = "processName";
	
	/**
	 * The key for the package name that will be created by this template
	 * (value="packageName").
	 */
	public static final String KEY_PACKAGE_NAME = "packageName";

	private boolean pagesAdded = false;

	/**
	 * The default implementation of this method provides values of the
	 * following keys: <samp>pluginClass </samp>, <samp>pluginId </samp> and
	 * <samp>pluginName </samp>.
	 * 
	 * @see ITemplateSection#getReplacementString(String,String)
	 */
	public String getReplacementString(String fileName, String key) {
		String result = getKeyValue(key);
		return result != null ? result : key;
	}

	/**
	 * @see IVariableProvider#getValue(String)
	 */
	public Object getValue(String key) {
		return getKeyValue(key);
	}

	private String getKeyValue(String key) {
		if (model == null)
			return null;

		// TODO removed referenced to pde... may got broken
//		if (key.equals(KEY_PLUGIN_CLASS) && model instanceof IPluginModel) {
//			IPlugin plugin = (IPlugin) model.getPluginBase();
//			return plugin.getClassName();
//		}

//		if (key.equals(KEY_ACTIVATOR_SIMPLE) && model instanceof IPluginModel) {
//			IPlugin plugin = (IPlugin) model.getPluginBase();
//			String qualified = plugin.getClassName();
//			if (qualified != null) {
//				int lastDot = qualified.lastIndexOf('.');
//				return (lastDot != -1 && lastDot < qualified.length() - 1) ? qualified.substring(lastDot + 1) : qualified;
//			}
//		}
//		if (key.equals(KEY_PLUGIN_ID)) {
//			IPluginBase plugin = model.getPluginBase();
//			return plugin.getId();
//		}
//		if (key.equals(KEY_PLUGIN_NAME)) {
//			IPluginBase plugin = model.getPluginBase();
//			return plugin.getTranslatedName();
//		}

//		if (key.equals(KEY_PACKAGE_NAME) && model instanceof IPluginModel) {
//			IPlugin plugin = (IPlugin) model.getPluginBase();
//			String qualified = plugin.getClassName();
//			if (qualified != null) {
//				int lastDot = qualified.lastIndexOf('.');
//				return (lastDot != -1) ? qualified.substring(0, lastDot) : qualified;
//			}
//		}
		return null;
	}

	/**
	 * @see ITemplateSection#getTemplateLocation()
	 */
	public URL getTemplateLocation() {
		return null;
	}

	/**
	 * @see ITemplateSection#getDescription()
	 */
	public String getDescription() {
		return "";
	}

	/**
	 * Returns the translated version of the resource string represented by the
	 * provided key.
	 * 
	 * @param key
	 *            the key of the required resource string
	 * @return the translated version of the required resource string
	 * @see #getPluginResourceBundle()
	 */
	public String getPluginResourceString(String key) {
		ResourceBundle bundle = getPluginResourceBundle();
		if (bundle == null)
			return key;
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * An abstract method that returns the resource bundle that corresponds to
	 * the best match of <samp>plugin.properties </samp> file for the current
	 * locale (in case of fragments, the file is <samp>fragment.properties
	 * </samp>).
	 * 
	 * @return resource bundle for plug-in properties file or <samp>null </samp>
	 *         if not found.
	 */
	protected abstract ResourceBundle getPluginResourceBundle();

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.ITemplateSection#addPages(org.eclipse.jface.wizard.Wizard)
	 */
	public void addPages(Wizard wizard) {
	}

	/**
	 * Tests if wizard pages for this template section have been added.
	 * 
	 * @return <code>true</code> if wizard pages for this section have been
	 *         added, <code>false</code> otherwise.
	 */
	public boolean getPagesAdded() {
		return pagesAdded;
	}

	/**
	 * Marks that pages have been added to the wizard by this template. Call
	 * this method in 'addPages'.
	 * 
	 * @see #addPages(Wizard)
	 */
	protected void markPagesAdded() {
		pagesAdded = true;
	}

	/**
	 * The default implementation of the interface method. The returned value is
	 * 1.
	 * 
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
	public int getNumberOfWorkUnits() {
		return 1;
	}

	/**
	 * Returns the folder with Java files in the target project. The default
	 * implementation looks for source folders in the classpath of the target
	 * folders and picks the first one encountered. Subclasses may override this
	 * behaviour.
	 * 
	 * @param monitor
	 *            progress monitor to use
	 * @return source folder that will be used to generate Java files or
	 *         <samp>null </samp> if none found.
	 */

	protected IFolder getSourceFolder(IProgressMonitor monitor) throws CoreException {
		IFolder sourceFolder = null;

		try {
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] classpath = javaProject.getRawClasspath();
			for (int i = 0; i < classpath.length; i++) {
				IClasspathEntry entry = classpath[i];
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IPath path = entry.getPath().removeFirstSegments(1);
					if (path.segmentCount() > 0)
						sourceFolder = project.getFolder(path);
					break;
				}
			}
		} catch (JavaModelException e) {
		}
		return sourceFolder;
	}

	/**
	 * Generates files as part of the template execution. The default
	 * implementation uses template location as a root of the file templates.
	 * {@link #generateFiles(IProgressMonitor monitor, URL locationUrl)} on how
	 * the location gets processed.
	 * 
	 * @param monitor
	 *            progress monitor to use to indicate generation progress
	 */
	protected void generateFiles(IProgressMonitor monitor) throws CoreException {
		generateFiles(monitor, getTemplateLocation());
	}

	/**
	 * Generates files as part of the template execution.
	 * The files found in the location are processed in the following way:
	 * <ul>
	 * <li>Files and folders found in the directory <samp>bin </samp> are
	 * copied into the target project without modification.</li>
	 * <li>Files found in the directory <samp>java </samp> are copied into the
	 * Java source folder by creating the folder structure that corresponds to
	 * the package name (variable <samp>packageName </samp>). Java files are
	 * subject to conditional generation and variable replacement.</li>
	 * <li>All other files and folders are copied directly into the target
	 * folder with the conditional generation and variable replacement for
	 * files. Variable replacement also includes file names.</li>
	 * </ul>
	 * 
	 * @param monitor
	 *            progress monitor to use to indicate generation progress
	 * @param locationUrl a url pointing to a file/directory that will be copied into the template
	 */
	protected void generateFiles(IProgressMonitor monitor, URL locationUrl) throws CoreException {
		monitor.setTaskName(HQDEMessages.AbstractTemplateSection_generating);

		if (locationUrl == null) {
			return;
		}
		try {
			locationUrl = FileLocator.resolve(locationUrl);
			locationUrl = FileLocator.toFileURL(locationUrl);
		} catch (IOException e) {
			return;
		}
		if ("file".equals(locationUrl.getProtocol())) { //$NON-NLS-1$
			File templateDirectory = new File(locationUrl.getFile());
			if (!templateDirectory.exists())
				return;
			generateFiles(templateDirectory, project, true, false, monitor);
		} else if ("jar".equals(locationUrl.getProtocol())) { //$NON-NLS-1$
			String file = locationUrl.getFile();
			int exclamation = file.indexOf('!');
			if (exclamation < 0)
				return;
			URL fileUrl = null;
			try {
				fileUrl = new URL(file.substring(0, exclamation));
			} catch (MalformedURLException mue) {
				return;
			}
			File pluginJar = new File(fileUrl.getFile());
			if (!pluginJar.exists())
				return;
			String templateDirectory = file.substring(exclamation + 1); // "/some/path/"
			IPath path = new Path(templateDirectory);
			ZipFile zipFile = null;
			try {
				zipFile = new ZipFile(pluginJar);
				generateFiles(zipFile, path, project, true, false, monitor);
			} catch (ZipException ze) {
			} catch (IOException ioe) {
			} finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException e) {
					}
				}
			}

		}
		monitor.subTask(""); //$NON-NLS-1$
		monitor.worked(1);
	}

	/**
	 * Tests if the folder found in the template location should be created in
	 * the target project. Subclasses may use this method to conditionally block
	 * the creation of entire directories (subject to user choices).
	 * 
	 * @param sourceFolder
	 *            the folder that is tested
	 * @return <code>true</code> if the provided folder should be created in
	 *         the workspace, <code>false</code> if the values of the
	 *         substitution variables indicate otherwise.
	 */
	protected boolean isOkToCreateFolder(File sourceFolder) {
		return true;
	}

	/**
	 * Tests if the file found in the template location should be created in the
	 * target project. Subclasses may use this method to conditionally block
	 * creation of the file (subject to user choices).
	 * 
	 * @param sourceFile
	 *            the file found in the template location that needs to be
	 *            created.
	 * @return <samp>true </samp> if the specified file should be created in the
	 *         project or <samp>false </samp> to skip it. The default
	 *         implementation is <samp>true </samp>.
	 */
	protected boolean isOkToCreateFile(File sourceFile) {
		return true;
	}

	/**
	 * Subclass must implement this method to add the required entries in the
	 * plug-in model.
	 * 
	 * @param monitor
	 *            the progress monitor to be used
	 */
	protected abstract void updateModel(IProgressMonitor monitor) throws CoreException;

	/**
	 * The default implementation of the interface method. It will generate
	 * required files found in the template location and then call
	 * <samp>updateModel </samp> to add the required manifest entires.
	 * 
	 * @see ITemplateSection#execute(IProject, IPluginModelBase,
	 *      IProgressMonitor)
	 */
	public void execute(IProject project, IPluginModelBase model, IProgressMonitor monitor) throws CoreException {
		this.project = project;
		this.model = model;
		generateFiles(monitor);
		updateModel(monitor);
	}

	private void generateFiles(File src, IContainer dst, boolean firstLevel, boolean binary, IProgressMonitor monitor) throws CoreException {
		File[] members = src.listFiles();

		for (int i = 0; i < members.length; i++) {
			File member = members[i];
			if (member.isDirectory()) {
				IContainer dstContainer = null;

				if (firstLevel) {
					binary = false;
					if (!isOkToCreateFolder(member))
						continue;

					if (member.getName().equals("java")) { //$NON-NLS-1$
						IFolder sourceFolder = getSourceFolder(monitor);
						dstContainer = generateJavaSourceFolder(sourceFolder, monitor);
					} else if (member.getName().equals("bin")) { //$NON-NLS-1$
						binary = true;
						dstContainer = dst;
					}
				}
				if (dstContainer == null) {
					if (isOkToCreateFolder(member) == false)
						continue;
					String folderName = getProcessedString(member.getName(), member.getName());
					dstContainer = dst.getFolder(new Path(folderName));
				}
				if (dstContainer instanceof IFolder && !dstContainer.exists())
					((IFolder) dstContainer).create(true, true, monitor);
				generateFiles(member, dstContainer, false, binary, monitor);
			} else {
				if (isOkToCreateFile(member)) {
					if (firstLevel)
						binary = false;
					InputStream in = null;
					try {
						in = new FileInputStream(member);
						copyFile(member.getName(), in, dst, binary, monitor);
					} catch (IOException ioe) {
					} finally {
						if (in != null)
							try {
								in.close();
							} catch (IOException ioe2) {
							}
					}
				}
			}
		}
	}

	private void generateFiles(ZipFile zipFile, IPath path, IContainer dst, boolean firstLevel, boolean binary, IProgressMonitor monitor) throws CoreException {
		int pathLength = path.segmentCount();
		// Immidiate children
		Map<String, ZipEntry> childZipEntries = new HashMap<String, ZipEntry>(); // "dir/" or "dir/file.java"

		for (Enumeration<? extends ZipEntry> zipEntries = zipFile.entries(); zipEntries.hasMoreElements();) {
			ZipEntry zipEntry = zipEntries.nextElement();
			IPath entryPath = new Path(zipEntry.getName());
			if (entryPath.segmentCount() <= pathLength) {
				// ancestor or current directory
				continue;
			}
			if (!path.isPrefixOf(entryPath)) {
				// not a descendant
				continue;
			}
			if (entryPath.segmentCount() == pathLength + 1) {
				childZipEntries.put(zipEntry.getName(), zipEntry);
			} else {
				String name = entryPath.uptoSegment(pathLength + 1).addTrailingSeparator().toString();
				if (!childZipEntries.containsKey(name)) {
					ZipEntry dirEntry = new ZipEntry(name);
					childZipEntries.put(name, dirEntry);
				}
			}
		}

		for (Iterator<ZipEntry> it = childZipEntries.values().iterator(); it.hasNext();) {
			ZipEntry zipEnry = (ZipEntry) it.next();
			String name = new Path(zipEnry.getName()).lastSegment().toString();
			if (zipEnry.isDirectory()) {
				IContainer dstContainer = null;

				if (firstLevel) {
					binary = false;
					if (name.equals("java")) { //$NON-NLS-1$
						IFolder sourceFolder = getSourceFolder(monitor);
						dstContainer = generateJavaSourceFolder(sourceFolder, monitor);
					} else if (name.equals("bin")) { //$NON-NLS-1$
						binary = true;
						dstContainer = dst;
					}
				}
				if (dstContainer == null) {
					if (isOkToCreateFolder(new File(path.toFile(), name)) == false)
						continue;
					String folderName = getProcessedString(name, name);
					dstContainer = dst.getFolder(new Path(folderName));
				}
				if (dstContainer instanceof IFolder && !dstContainer.exists())
					((IFolder) dstContainer).create(true, true, monitor);
				generateFiles(zipFile, path.append(name), dstContainer, false, binary, monitor);
			} else {
				if (isOkToCreateFile(new File(path.toFile(), name))) {
					if (firstLevel)
						binary = false;
					InputStream in = null;
					try {
						in = zipFile.getInputStream(zipEnry);
						copyFile(name, in, dst, binary, monitor);
					} catch (IOException ioe) {
					} finally {
						if (in != null)
							try {
								in.close();
							} catch (IOException ioe2) {
							}
					}
				}
			}
		}
	}

	private IFolder generateJavaSourceFolder(IFolder sourceFolder, IProgressMonitor monitor) throws CoreException {
		Object packageValue = getValue(KEY_PACKAGE_NAME);
		String packageName = packageValue != null ? packageValue.toString() : null;
//		if (packageName == null)
//			packageName = model.getPluginBase().getId();
		if (packageName == null)
			packageName = model.getPluginBase().getPackage();
		IPath path = new Path(packageName.replace('.', File.separatorChar));
		if (sourceFolder != null)
			path = sourceFolder.getProjectRelativePath().append(path);

		for (int i = 1; i <= path.segmentCount(); i++) {
			IPath subpath = path.uptoSegment(i);
			IFolder subfolder = project.getFolder(subpath);
			if (subfolder.exists() == false)
				subfolder.create(true, true, monitor);
		}
		return project.getFolder(path);
	}

	private void copyFile(String fileName, InputStream input, IContainer dst, boolean binary, IProgressMonitor monitor) throws CoreException {
		String targetFileName = getProcessedString(fileName, fileName);

		monitor.subTask(targetFileName);
		IFile dstFile = dst.getFile(new Path(targetFileName));

		try {
			InputStream stream = getProcessedStream(fileName, input, binary);
			if (dstFile.exists()) {
				dstFile.setContents(stream, true, true, monitor);
			} else {
				dstFile.create(stream, true, monitor);
			}
			stream.close();

		} catch (IOException e) {
		}
	}

	private String getProcessedString(String fileName, String source) {
		if (source.indexOf('$') == -1)
			return source;
		int loc = -1;
		StringBuffer buffer = new StringBuffer();
		boolean replacementMode = false;
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '$') {
				if (replacementMode) {
					String key = source.substring(loc, i);
					String value = key.length() == 0 ? "$" //$NON-NLS-1$
							: getReplacementString(fileName, key);
					buffer.append(value);
					replacementMode = false;
				} else {
					replacementMode = true;
					loc = i + 1;
					continue;
				}
			} else if (!replacementMode)
				buffer.append(c);
		}
		return buffer.toString();
	}

	private InputStream getProcessedStream(String fileName, InputStream stream, boolean binary) throws IOException, CoreException {
		if (binary)
			return stream;

		InputStreamReader reader = new InputStreamReader(stream);
		int bufsize = 1024;
		char[] cbuffer = new char[bufsize];
		int read = 0;
		StringBuffer keyBuffer = new StringBuffer();
		StringBuffer outBuffer = new StringBuffer();
		StringBuffer preBuffer = new StringBuffer();
		boolean newLine = true;
		ControlStack preStack = new ControlStack();
		preStack.setValueProvider(this);

		boolean replacementMode = false;
		boolean preprocessorMode = false;
		boolean escape = false;
		while (read != -1) {
			read = reader.read(cbuffer);
			for (int i = 0; i < read; i++) {
				char c = cbuffer[i];

				if (escape) {
					StringBuffer buf = preprocessorMode ? preBuffer : outBuffer;
					buf.append(c);
					escape = false;
					continue;
				}

				if (newLine && c == '%') {
					// preprocessor line
					preprocessorMode = true;
					preBuffer.delete(0, preBuffer.length());
					continue;
				}
				if (preprocessorMode) {
					if (c == '\\') {
						escape = true;
						continue;
					}
					if (c == '\n') {
						// handle line
						preprocessorMode = false;
						newLine = true;
						String line = preBuffer.toString().trim();
						preStack.processLine(line);
						continue;
					}
					preBuffer.append(c);

					continue;
				}

				if (preStack.getCurrentState() == false) {
					continue;
				}

				if (c == '$') {
					if (replacementMode) {
						replacementMode = false;
						String key = keyBuffer.toString();
						String value = key.length() == 0 ? "$" //$NON-NLS-1$
								: getReplacementString(fileName, key);
						outBuffer.append(value);
						keyBuffer.delete(0, keyBuffer.length());
					} else {
						replacementMode = true;
					}
				} else {
					if (replacementMode)
						keyBuffer.append(c);
					else {
						outBuffer.append(c);
						if (c == '\n') {
							newLine = true;
						} else
							newLine = false;
					}
				}
			}
		}
		return new ByteArrayInputStream(outBuffer.toString().getBytes(project.getDefaultCharset()));
	}

}
