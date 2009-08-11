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
package org.hyperic.hypclipse.internal.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.hyperic.hypclipse.internal.HQDEPlugin;

/**
 * This class handles global storage for plugin
 * saved preferences. State can be saved to
 * external xml file under plugin metadata
 * directory.
 */
public class PreferencesManager {

	/** Singleton instance */
	private static PreferencesManager instance;
	
	// tag and attribute names
	public static final String TAG_PREFERENCES = "preferences";
	public static final String TAG_HQAGENTS = "agents";
	public static final String TAG_HQAGENT = "agent";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_LOCATION = "location";
	public static final String ATTR_DEFAULT = "default";
	
	private HQEnvInfo[] environments;

	private ArrayList<IHQPreferenceChangedListener> fListeners;
	
	/**
	 * No public access.
	 */
	private PreferencesManager(){
		fListeners = new ArrayList<IHQPreferenceChangedListener>();
		load();
	}
	
	public void dispose() {
		fListeners.clear();
	}
	
	/**
	 * Returns singleton instance of this class.
	 * @return instance
	 */
	public static PreferencesManager getInstance() {
		if(instance == null)
			instance = new PreferencesManager();
		return instance;
	}
	
	/**
	 * Returns environments
	 * @return
	 */
	public HQEnvInfo[] getEnvironments() {
		return environments;
	}
	
	/**
	 * 
	 * @param envs
	 */
	public void setEnvironments(HQEnvInfo[] envs) {
		setEnvironments(envs, null);
	}
	
	/**
	 * 
	 * @param envs
	 * @param selected
	 */
	public void setEnvironments(HQEnvInfo[] envs, HQEnvInfo selected) {
		environments = envs;
		for (int i = 0; i < envs.length; i++) {
			if(envs[i] == selected)
				envs[i].setDefault(true);
			else				
				envs[i].setDefault(false);
		}
	}
	
	public void reload() {
		environments = null;
		load();
		firePreferencesChanged(new HQPreferencesChangedEvent(HQPreferencesChangedEvent.WORLD_CHANGED));
	}
	
	/**
	 * Load preferences from file.
	 */
	private void load() {
		FileReader reader = null;
		try {
			reader = new FileReader(getPreferencesFile());
			loadPreferences(XMLMemento.createReadRoot(reader));
		} catch (FileNotFoundException e) {
			// ignore, file doesn't exist
		} catch (Exception e) {
			HQDEPlugin.logException(e);			
		} finally {
			try {
				if(reader != null)
					reader.close();
			} catch (Exception e) {
				HQDEPlugin.logException(e);							
			}
		}
	}
	
	/**
	 * Handle root node.
	 * @param memento
	 */
	private void loadPreferences(XMLMemento memento) {
		IMemento agents = memento.getChild(TAG_HQAGENTS);
		if(agents != null)
			loadEnvironments(agents);
	}

	/**
	 * Handle agents section.
	 * @param memento
	 */
	private void loadEnvironments(IMemento memento) {		
		IMemento[] children = memento.getChildren(TAG_HQAGENT);
		environments = new HQEnvInfo[children.length];
		for (int i = 0; i < children.length; i++) {
			HQEnvInfo item = new HQEnvInfo(
					children[i].getString(ATTR_NAME),
					children[i].getString(ATTR_LOCATION),
					children[i].getBoolean(ATTR_DEFAULT));
			environments[i] = item;			
		}
	}

	
	
	/**
	 * 
	 * @param envs
	 */
	public void save() {
		XMLMemento memento = XMLMemento.createWriteRoot(TAG_PREFERENCES);
		IMemento agents = memento.createChild(TAG_HQAGENTS);
		saveEnvironments(agents);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getPreferencesFile());
			memento.save(writer);
		} catch (IOException e) {
			HQDEPlugin.logException(e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				HQDEPlugin.logException(e);				
			}
		}
		firePreferencesChanged(new HQPreferencesChangedEvent(HQPreferencesChangedEvent.WORLD_CHANGED));
	}

	/**
	 * 
	 * @param memento
	 * @param envs
	 */
	private void saveEnvironments(IMemento agents) {
		
		for (int i = 0; i < environments.length; i++) {
			IMemento agent = agents.createChild(TAG_HQAGENT);
			agent.putString(ATTR_NAME, environments[i].getName());
			agent.putString(ATTR_LOCATION, environments[i].getLocation());
			agent.putBoolean(ATTR_DEFAULT, environments[i].isDefault());
		}
		
	}
	
	/**
	 * Returns file handle to store file.
	 * @return preferences file
	 */
	private File getPreferencesFile() {
		return HQDEPlugin
			.getDefault()
			.getStateLocation()
			.append("hypclipsepreferences.xml")
			.toFile();
	}
	
	/**
	 * Tries to find matching location compared to given
	 * name. Returns null if nothing found.
	 * 
	 * @param name
	 * @return
	 */
	public String getEnvironmentLocation(String name) {
		for (int i = 0; i < environments.length; i++) {
			if(environments[i].getName().equals(name))
				return environments[i].getLocation();
		}
		return null;
	}

	/**
	 * Returns default environment.
	 * @return
	 */
	public HQEnvInfo getDefaultEnvironment() {
		if(environments == null)
			return null;
		for (int i = 0; i < environments.length; i++) {
			if(environments[i].isDefault())
				return environments[i];
		}		
		return null;
	}
	
	public void addPreferencesChangedListener(IHQPreferenceChangedListener listener) {
		if(!fListeners.contains(listener))
			fListeners.add(listener);
	}
	
	public void removePreferencesChangedListener(IHQPreferenceChangedListener listener) {
		fListeners.remove(listener);
	}
	
	public void firePreferencesChanged(HQPreferencesChangedEvent event) {
		for(int i = 0; i < fListeners.size(); i++) {
			((IHQPreferenceChangedListener)fListeners.get(i)).preferencesChanged(event);
		}
	}
}
