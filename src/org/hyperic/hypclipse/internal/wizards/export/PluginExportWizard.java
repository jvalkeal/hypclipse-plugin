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
package org.hyperic.hypclipse.internal.wizards.export;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.hyperic.hypclipse.internal.HQDEPluginImages;
import org.hyperic.hypclipse.internal.IConstants;
import org.hyperic.hypclipse.internal.editor.build.WorkspaceBuildModel;
import org.hyperic.hypclipse.internal.export.PluginExportJob;
import org.hyperic.hypclipse.plugin.IBuild;
import org.hyperic.hypclipse.plugin.IBuildEntry;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class PluginExportWizard.
 */
public class PluginExportWizard extends AntGeneratingExportWizard {
	
	/** The Constant STORE_SECTION. */
	private static final String STORE_SECTION = "PluginExportWizard";

	/** The page. */
	private PluginExportWizardPage fPage;
	
	

	/**
	 * Instantiates a new plugin export wizard.
	 */
	public PluginExportWizard() {
		this(null);
	}

	/**
	 * Instantiates a new plugin export wizard.
	 * 
	 * @param fileName the file name
	 */
	public PluginExportWizard(String fileName) {
		fFileName = fileName;
		setDefaultPageImageDescriptor(HQDEPluginImages.DESC_PLUGIN_EXPORT_WIZ);
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.AntGeneratingExportWizard#createPage1()
	 */
	protected BaseExportWizardPage createPage1() {
		fPage = new PluginExportWizardPage(getSelection());
		
		// prefix for exported file name 
		String exportFilePrefix;
		
		// if hint not given, use project name
		if(fFileName != null && fFileName.length() > 0) {
			exportFilePrefix = fFileName;
		} else {
			Object first =  fSelection.getFirstElement();
			IFile file = (IFile) first;
			exportFilePrefix = file.getProject().getName();
		}

		fPage.setFileName(exportFilePrefix);
		return fPage;
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.BaseExportWizard#getSettingsSectionName()
	 */
	protected String getSettingsSectionName() {
		return STORE_SECTION;
	}

	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.BaseExportWizard#scheduleExportJob()
	 */
	protected void scheduleExportJob() {
		
		
		PluginExportJob job = new PluginExportJob("job",fPage.getDestination(), fPage.getFilePath());
		job.setUser(true);
		job.schedule();
//		job.setProperty(IProgressConstants.ICON_PROPERTY, HQDEPluginImages.DESC_PLUGIN_OBJ);
	}



	/* (non-Javadoc)
	 * @see org.hyperic.hypclipse.internal.wizards.export.AntGeneratingExportWizard#generateAntTask()
	 */
	protected Document generateAntTask() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document doc = factory.newDocumentBuilder().newDocument();
			
			Element root = doc.createElement("project");
			root.setAttribute("name", "hqbuild");
			root.setAttribute("default", "package");
			doc.appendChild(root);

			root.appendChild(createClassPath(doc));			
			
			root.appendChild(createPreTarget(doc));
			
			root.appendChild(createCompileTarget(doc));

			root.appendChild(createAssembleTarget(doc));

			root.appendChild(createPackageTarget(doc));

			return doc;
		} catch (DOMException e) {
		} catch (FactoryConfigurationError e) {
		} catch (ParserConfigurationException e) {
		}
		return null;
	}

	/**
	 * Creates the pre target.
	 * 
	 * @param doc the doc
	 * 
	 * @return the element
	 */
	private Element createPreTarget(Document doc) {
		Element targetInit = doc.createElement("target");
		targetInit.setAttribute("name", "init");

		Element deldir1 = doc.createElement("delete");
		deldir1.setAttribute("dir", "${build.dir}");
		targetInit.appendChild(deldir1);

		Element deldir2 = doc.createElement("delete");
		deldir2.setAttribute("dir", "${assemble.dir}");
		targetInit.appendChild(deldir2);

		
		Element mkdir1 = doc.createElement("mkdir");
		mkdir1.setAttribute("dir", "${build.dir}");
		targetInit.appendChild(mkdir1);

		Element mkdir2 = doc.createElement("mkdir");
		mkdir2.setAttribute("dir", "${assemble.dir}/etc");
		targetInit.appendChild(mkdir2);


		return targetInit;
	}
	
	/**
	 * Creates the post target.
	 * 
	 * @param doc the doc
	 * 
	 * @return the element
	 */
	private Element createPostTarget(Document doc) {
		return null;
	}
	
	/**
	 * Creates the class path.
	 * 
	 * @param doc the doc
	 * 
	 * @return the element
	 */
	private Element createClassPath(Document doc) {
		Element path = doc.createElement("path");
		path.setAttribute("id", "hq.classpath");
		
		String paths[] = getPathTokens();
		for (int i = 0; i < paths.length; i++) {
			Element pathelement = doc.createElement("pathelement");
			pathelement.setAttribute("path", paths[i]);
			path.appendChild(pathelement);
		}
		
		return path;
	}

	/**
	 * Creates the package target.
	 * 
	 * @param doc the doc
	 * 
	 * @return the element
	 */
	private Element createPackageTarget(Document doc) {
		Element targetPackage = doc.createElement("target");
		targetPackage.setAttribute("name", "package");
		targetPackage.setAttribute("depends", "assemble");
		
		Element jar = doc.createElement("jar");
		jar.setAttribute("destfile", "${package.file}");
		jar.setAttribute("basedir", "${assemble.dir}");
		targetPackage.appendChild(jar);
		
		return targetPackage;
	}

	/**
	 * Creates the assemble target.
	 * 
	 * @param doc the doc
	 * 
	 * @return the element
	 */
	private Element createAssembleTarget(Document doc) {
		Element targetAssemble = doc.createElement("target");
		targetAssemble.setAttribute("name", "assemble");
		targetAssemble.setAttribute("depends", "compile");
		
		// copy xml
		Object first =  fSelection.getFirstElement();
		IFile file = (IFile) first;
		IContainer dir = file.getParent();
		file = dir.getFile(new Path("hq-plugin.xml"));

		Element xmlCopy = doc.createElement("copy");
		xmlCopy.setAttribute("todir", "${assemble.dir}/etc");
		Element fileset1 = doc.createElement("fileset");
		fileset1.setAttribute("file", file.getLocation().toOSString());
		xmlCopy.appendChild(fileset1);
		targetAssemble.appendChild(xmlCopy);
		
		// copy classes
		Element classCopy = doc.createElement("copy");
		classCopy.setAttribute("todir", "${assemble.dir}");
		Element fileset2 = doc.createElement("fileset");
		fileset2.setAttribute("dir", "${build.dir}");
		classCopy.appendChild(fileset2);
		targetAssemble.appendChild(classCopy);
		
		return targetAssemble;
	}
	
	/**
	 * Gets the path tokens.
	 * 
	 * @return the path tokens
	 */
	private String[] getPathTokens() {
		Object first =  fSelection.getFirstElement();
		IFile file = (IFile) first;
		IContainer dir = file.getParent();
		file = file.getProject().getFile(IConstants.BUILD_FILENAME_DESCRIPTOR);
//		file = dir.getFile(new Path("hqbuild.properties"));
		WorkspaceBuildModel bModel = new WorkspaceBuildModel(file);
		bModel.load();
		IBuild fBuild = bModel.getBuild();
		IBuildEntry srcEntry = fBuild.getEntry("jars.extra.classpath");
		String[] srcTokens = srcEntry.getTokens();
		return srcTokens;
	}

	/**
	 * Gets the source path.
	 * 
	 * @return the source path
	 */
	private String getSourcePath() {
		Object first =  fSelection.getFirstElement();
		IFile file = (IFile) first;
		file = file.getProject().getFile(IConstants.BUILD_FILENAME_DESCRIPTOR);
		IContainer dir = file.getParent();
//		file = dir.getFile(new Path("hqbuild.properties"));
		WorkspaceBuildModel bModel = new WorkspaceBuildModel(file);
		bModel.load();
		IBuild fBuild = bModel.getBuild();
		IBuildEntry srcEntry = fBuild.getEntry("source..");
		String[] srcTokens = srcEntry.getTokens();
		String p = dir.getFile(new Path(srcTokens[0])).getLocation().toOSString();
		return p;
	}
	
	/**
	 * Creates the compile target.
	 * 
	 * @param doc the doc
	 * 
	 * @return the element
	 */
	private Element createCompileTarget(Document doc) {
		Element targetCompile = doc.createElement("target");
		targetCompile.setAttribute("name", "compile");
		targetCompile.setAttribute("depends", "init");

		Element javac = doc.createElement("javac");
		javac.setAttribute("srcdir", getSourcePath());
		javac.setAttribute("destdir", "${build.dir}");
		javac.setAttribute("target", "1.5");
		javac.setAttribute("source", "1.5");
		javac.setAttribute("debug", "on");
		
		// we need to setup fail to false.
		// if it's true, ExportBuildListener wont be notified
		// any messages with error priority
		javac.setAttribute("failonerror", "false");

		Element javacInclude = doc.createElement("include");
		javacInclude.setAttribute("name", "**/*.java");

		Element classpath = doc.createElement("classpath");
		classpath.setAttribute("refid", "hq.classpath");
		
		Element compilerarg = doc.createElement("compilerarg");
//		compilerarg.setAttribute("compiler", "org.eclipse.jdt.core.JDTCompilerAdapter");
//		compilerarg.setAttribute("line", "-log 'c:\\tmp\\foologs\\log.txt'");
		File path = new File(fBuildTempLocation, "build_logs.txt");
		compilerarg.setAttribute("line", "-log '" + path.getAbsolutePath() + "'");
		javac.appendChild(compilerarg);
		
		javac.appendChild(classpath);
		javac.appendChild(javacInclude);
		targetCompile.appendChild(javac);

		return targetCompile;
	}

}
