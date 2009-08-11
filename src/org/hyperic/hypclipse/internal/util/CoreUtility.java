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
package org.hyperic.hypclipse.internal.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class CoreUtility {

	/**
	 * Read a file from an InputStream and write it to the file system.
	 *
	 * @param in InputStream from which to read.
	 * @param file output file to create.
	 * @exception IOException
	 */
	public static void readFile(InputStream in, File file) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);

			byte buffer[] = new byte[1024];
			int count;
			while ((count = in.read(buffer, 0, buffer.length)) > 0) {
				fos.write(buffer, 0, count);
			}

			fos.close();
			fos = null;

			in.close();
			in = null;
		} catch (IOException e) {
			// close open streams
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ee) {
				}
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException ee) {
				}
			}

			throw e;
		}
	}

	public static void addNatureToProject(IProject proj, String natureId, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, monitor);
	}

	public static void createFolder(IFolder folder) throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder) parent);
			}
			folder.create(true, true, null);
		}
	}

	public static void createProject(IProject project, IPath location, IProgressMonitor monitor) throws CoreException {
		if (!Platform.getLocation().equals(location)) {
			IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
			desc.setLocation(location);
			project.create(desc, monitor);
		} else
			project.create(monitor);
	}

	public static String normalize(String text) {
		if (text == null || text.trim().length() == 0)
			return ""; //$NON-NLS-1$

		text = text.replaceAll("\\r|\\n|\\f|\\t", " "); //$NON-NLS-1$ //$NON-NLS-2$
		return text.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void deleteContent(File curr) {
		if (curr.exists()) {
			if (curr.isDirectory()) {
				File[] children = curr.listFiles();
				if (children != null) {
					for (int i = 0; i < children.length; i++) {
						deleteContent(children[i]);
					}
				}
			}
			curr.delete();
		}
	}

//	public static boolean guessUnpack(BundleDescription bundle) {
//		if (bundle == null)
//			return true;
//
//		if (new File(bundle.getLocation()).isFile())
//			return false;
//
//		IWorkspaceRoot root = PDECore.getWorkspace().getRoot();
//		IContainer container = root.getContainerForLocation(new Path(bundle.getLocation()));
//		if (container == null)
//			return true;
//
//		if (container instanceof IProject) {
//			try {
//				if (!((IProject) container).hasNature(JavaCore.NATURE_ID))
//					return true;
//			} catch (CoreException e) {
//			}
//		}
//
//		IPluginModelBase model = PluginRegistry.findModel(bundle);
//		if (model == null)
//			return true;
//
//		IPluginLibrary[] libraries = model.getPluginBase().getLibraries();
//		if (libraries.length == 0)
//			return false;
//
//		for (int i = 0; i < libraries.length; i++) {
//			if (libraries[i].getName().equals(".")) //$NON-NLS-1$
//				return false;
//		}
//		return true;
//	}

//	public static boolean jarContainsResource(File file, String resource, boolean directory) {
//		ZipFile jarFile = null;
//		try {
//			jarFile = new ZipFile(file, ZipFile.OPEN_READ);
//			ZipEntry resourceEntry = jarFile.getEntry(resource);
//			if (resourceEntry != null)
//				return directory ? resourceEntry.isDirectory() : true;
//		} catch (IOException e) {
//		} catch (FactoryConfigurationError e) {
//		} finally {
//			try {
//				if (jarFile != null)
//					jarFile.close();
//			} catch (IOException e2) {
//			}
//		}
//		return false;
//	}

//	public static void copyFile(IPath originPath, String name, File target) {
//		File source = new File(originPath.toFile(), name);
//		if (source.exists() == false)
//			return;
//		FileInputStream is = null;
//		FileOutputStream os = null;
//		try {
//			is = new FileInputStream(source);
//			os = new FileOutputStream(target);
//			byte[] buf = new byte[1024];
//			long currentLen = 0;
//			int len = is.read(buf);
//			while (len != -1) {
//				currentLen += len;
//				os.write(buf, 0, len);
//				len = is.read(buf);
//			}
//		} catch (IOException e) {
//		} finally {
//			try {
//				if (is != null)
//					is.close();
//				if (os != null)
//					os.close();
//			} catch (IOException e) {
//			}
//		}
//	}

//	public static org.eclipse.jface.text.Document getTextDocument(File bundleLocation, String path) {
//		ZipFile jarFile = null;
//		InputStream stream = null;
//		try {
//			String extension = new Path(bundleLocation.getName()).getFileExtension();
//			if ("jar".equals(extension) && bundleLocation.isFile()) { //$NON-NLS-1$
//				jarFile = new ZipFile(bundleLocation, ZipFile.OPEN_READ);
//				ZipEntry manifestEntry = jarFile.getEntry(path);
//				if (manifestEntry != null) {
//					stream = jarFile.getInputStream(manifestEntry);
//				}
//			} else {
//				File file = new File(bundleLocation, path);
//				if (file.exists())
//					stream = new FileInputStream(file);
//			}
//			return getTextDocument(stream);
//		} catch (IOException e) {
//		} finally {
//			try {
//				if (jarFile != null)
//					jarFile.close();
//			} catch (IOException e) {
//			}
//		}
//		return null;
//	}
//
//	public static org.eclipse.jface.text.Document getTextDocument(InputStream in) {
//		ByteArrayOutputStream output = null;
//		String result = null;
//		try {
//			output = new ByteArrayOutputStream();
//
//			byte buffer[] = new byte[1024];
//			int count;
//			while ((count = in.read(buffer, 0, buffer.length)) > 0) {
//				output.write(buffer, 0, count);
//			}
//
//			result = output.toString("UTF-8"); //$NON-NLS-1$
//			output.close();
//			output = null;
//			in.close();
//			in = null;
//		} catch (IOException e) {
//			// close open streams
//			if (output != null) {
//				try {
//					output.close();
//				} catch (IOException ee) {
//				}
//			}
//
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException ee) {
//				}
//			}
//		}
//		return result == null ? null : new org.eclipse.jface.text.Document(result);
//	}

}
