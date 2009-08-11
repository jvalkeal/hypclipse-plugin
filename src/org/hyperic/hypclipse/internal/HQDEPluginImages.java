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
package org.hyperic.hypclipse.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

public class HQDEPluginImages {

	private static final String NAME_PREFIX = HQDEPlugin.getPluginId() + ".";

	private static ImageRegistry PLUGIN_REGISTRY;

	public final static String ICONS_PATH = "icons/";

	private static final String PATH_OBJ = ICONS_PATH + "obj16/";
	private static final String PATH_OVR = ICONS_PATH + "ovr16/";
	private static final String PATH_WIZBAN = ICONS_PATH + "wizban/";
	private static final String PATH_LCL = ICONS_PATH + "elcl16/";
	private static final String PATH_TOOL = ICONS_PATH + "etool16/";
	private static final String PATH_FUGUE = ICONS_PATH + "fugue/";

	public static final ImageDescriptor DESC_TREE_SERVER = create(PATH_FUGUE, "server.png");
	public static final ImageDescriptor DESC_TREE_METRIC = create(PATH_FUGUE, "monitor.png");
	public static final ImageDescriptor DESC_TREE_METRICS = create(PATH_FUGUE, "dashboard.png");
	public static final ImageDescriptor DESC_TREE_SERVICE = create(PATH_FUGUE, "equalizer.png");
	public static final ImageDescriptor DESC_TREE_HELP = create(PATH_FUGUE, "lifebuoy.png");
	public static final ImageDescriptor DESC_TREE_PLUGIN = create(PATH_FUGUE, "plug.png");
	public static final ImageDescriptor DESC_TREE_CONFIG = create(PATH_FUGUE, "cards.png");
	public static final ImageDescriptor DESC_TREE_OPTION = create(PATH_FUGUE, "card.png");
	public static final ImageDescriptor DESC_TREE_SCAN = create(PATH_FUGUE, "eye.png");
	public static final ImageDescriptor DESC_TREE_INCLUDE = create(PATH_FUGUE, "gear.png");
	public static final ImageDescriptor DESC_TREE_PROPERTY = create(PATH_FUGUE, "tag.png");
	public static final ImageDescriptor DESC_TREE_FILTER = create(PATH_FUGUE, "funnel.png");
	public static final ImageDescriptor DESC_TREE_PROPERTIES = create(PATH_FUGUE, "tags.png");
	public static final ImageDescriptor DESC_TREE_ACTIONS = create(PATH_FUGUE, "clapperboard.png");
	public static final ImageDescriptor DESC_TREE_CLASSPATH = create(PATH_FUGUE, "direction.png");
	public static final ImageDescriptor DESC_TREE_PLATFORM = create(PATH_FUGUE, "building.png");
	public static final ImageDescriptor DESC_TREE_SCRIPT = create(PATH_FUGUE, "script.png");

	public static final ImageDescriptor DESC_GENERIC_XML_OBJ = create(PATH_OBJ, "generic_xml_obj.gif");

	public static final ImageDescriptor DESC_HELP = create(PATH_LCL, "help.gif");
	
	public static final ImageDescriptor DESC_DOC_CO = create(PATH_OVR, "doc_co.gif");
	public static final ImageDescriptor DESC_WARNING_CO = create(PATH_OVR, "warning_co.gif");
	public static final ImageDescriptor DESC_ERROR_CO = create(PATH_OVR, "error_co.gif");
	public static final ImageDescriptor DESC_EXPORT_CO = create(PATH_OVR, "export_co.gif");
	public static final ImageDescriptor DESC_EXTERNAL_CO = create(PATH_OVR, "external_co.gif");
	public static final ImageDescriptor DESC_BINARY_CO = create(PATH_OVR, "binary_co.gif");
	public static final ImageDescriptor DESC_JAVA_CO = create(PATH_OVR, "java_co.gif");
	public static final ImageDescriptor DESC_JAR_CO = create(PATH_OVR, "jar_co.gif");
	public static final ImageDescriptor DESC_PROJECT_CO = create(PATH_OVR, "project_co.gif");
	public static final ImageDescriptor DESC_OPTIONAL_CO = create(PATH_OVR, "optional_co.gif");
	public static final ImageDescriptor DESC_INTERNAL_CO = create(PATH_OVR, "internal_co.gif");
	public static final ImageDescriptor DESC_FRIEND_CO = create(PATH_OVR, "friend_co.gif");

	public static final ImageDescriptor DESC_PLUGIN_EXPORT_WIZ = create(PATH_WIZBAN, "exp_deployplug_wiz.png");

	public static final ImageDescriptor DESC_NEWEX_WIZ = create(PATH_WIZBAN, "newex_wiz.png");
	
	public static final ImageDescriptor DESC_BUILD_VAR_OBJ = create(PATH_OBJ, "build_var_obj.gif");

	public static final ImageDescriptor DESC_EXPORT_PLUGIN_TOOL = create(PATH_TOOL, "exp_deployplug.gif");

	public static final ImageDescriptor DESC_PAGE_OBJ = create(PATH_OBJ, "page_obj.gif");
	public static final ImageDescriptor DESC_JAVA_LIB_OBJ = create(PATH_OBJ, "java_lib_obj.gif");

	private static ImageDescriptor create(String prefix, String name) {
		return ImageDescriptor.createFromURL(makeImageURL(prefix, name));
	}

	private static URL makeImageURL(String prefix, String name) {
		String path = "$nl$/" + prefix + name;
		return FileLocator.find(HQDEPlugin.getDefault().getBundle(), new Path(path), null);
	}


}
