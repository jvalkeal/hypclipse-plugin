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
package org.hyperic.hypclipse.internal.wizards.project;

import org.eclipse.ui.IWorkingSet;
import org.hyperic.hypclipse.plugin.IFieldData;

public abstract class AbstractFieldData implements IFieldData {

	private String fId;
//	private String fVersion;
	private String fName;
	private String fPackageName;
//	private String fProvider;
	private boolean fLegacy;
	private String fLibraryName;
	private String fSourceFolderName;
	private String fOutputFolderName;
//	private boolean fHasBundleStructure;
//	private boolean fSimple;
	private String fTargetVersion = "3.1"; //$NON-NLS-1$
	private String fFramework;
	private IWorkingSet[] fWorkingSets;
	private String fExecutionEnvironment;
	private String fHQExecutionEnvironment;

	public String getId() {
		return fId;
	}

//	public String getVersion() {
//		return fVersion;
//	}

	public String getName() {
		return fName;
	}

//	public String getProvider() {
//		return fProvider;
//	}

	public boolean isLegacy() {
		return fLegacy;
	}

	public String getLibraryName() {
		return fLibraryName;
	}

	public String getSourceFolderName() {
		return fSourceFolderName;
	}

	public String getOutputFolderName() {
		return fOutputFolderName;
	}

//	public boolean hasBundleStructure() {
//		return fHasBundleStructure;
//	}

//	public boolean isSimple() {
//		return fSimple;
//	}

	public void setId(String id) {
		fId = id;
	}

	public void setName(String name) {
		fName = name;
	}

//	public void setProvider(String provider) {
//		fProvider = provider;
//	}
//
//	public void setVersion(String version) {
//		fVersion = version;
//	}

	public void setLegacy(boolean isLegacy) {
		fLegacy = isLegacy;
	}

	public void setLibraryName(String name) {
		fLibraryName = name;
	}

	public void setSourceFolderName(String name) {
		fSourceFolderName = name;
	}

	public void setOutputFolderName(String name) {
		fOutputFolderName = name;
	}

//	public void setHasBundleStructure(boolean isBundle) {
//		fHasBundleStructure = isBundle;
//	}

//	public void setSimple(boolean simple) {
//		fSimple = simple;
//	}

	public String getTargetVersion() {
		return fTargetVersion;
	}

	public void setTargetVersion(String version) {
		fTargetVersion = version;
	}

	public String getOSGiFramework() {
		return fFramework;
	}

	public void setOSGiFramework(String framework) {
		fFramework = framework;
	}

	public IWorkingSet[] getWorkingSets() {
		return fWorkingSets;
	}

	public void setWorkingSets(IWorkingSet[] workingSets) {
		fWorkingSets = workingSets;
	}

	public void setExecutionEnvironment(String executionEnvironment) {
		fExecutionEnvironment = executionEnvironment;
	}

	public String getExecutionEnvironment() {
		return fExecutionEnvironment;
	}

	public void setHQExecutionEnvironment(String hQExecutionEnvironment) {
		fHQExecutionEnvironment = hQExecutionEnvironment;
	}

	public String getHQExecutionEnvironment() {
		return fHQExecutionEnvironment;
	}

	public void setPackageName(String packageName) {
		fPackageName = packageName;
	}

	public String getPackageName() {
		return fPackageName;
	}
}
