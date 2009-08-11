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
package org.hyperic.hypclipse.internal.text.plugin;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.hyperic.hypclipse.internal.plugin.PluginObject;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IPluginElement;
import org.hyperic.hypclipse.plugin.IPluginObject;
import org.hyperic.hypclipse.plugin.IPluginParent;

public abstract class PluginParent extends PluginObject implements IPluginParent {
	
	protected ArrayList<IPluginObject> fChildren = null;

	public PluginParent() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginParent#add(int, org.hyperic.hypclipse.plugin.IPluginObject)
	 */
	public void add(int index, IPluginObject child) throws CoreException {
		ensureModelEditable();
		getChildrenList().add(index, child);
		postAdd(child);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginParent#add(org.hyperic.hypclipse.plugin.IPluginObject)
	 */
	public void add(IPluginObject child) throws CoreException {
		ensureModelEditable();
		getChildrenList().add(child);
		postAdd(child);
	}

	void appendChild(IPluginElement child) {
		getChildrenList().add(child);
	}

	protected void postAdd(IPluginObject child) {
		((PluginObject) child).setInTheModel(true);
		((PluginObject) child).setParent(this);
		fireStructureChanged(child, IModelChangedEvent.INSERT);
	}

	public int getChildCount() {
		return getChildrenList().size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof IPluginParent) {
			IPluginParent target = (IPluginParent) obj;
			if (target.getChildCount() != getChildCount())
				return false;
			IPluginObject[] tchildren = target.getChildren();
			for (int i = 0; i < tchildren.length; i++) {
				IPluginObject tchild = tchildren[i];
				IPluginObject child = (IPluginObject) getChildrenList().get(i);
				if (child == null || child.equals(tchild) == false)
					return false;
			}
			return true;
		}
		return false;
	}

	public int getIndexOf(IPluginObject child) {
		return getChildrenList().indexOf(child);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginParent#swap(org.hyperic.hypclipse.plugin.IPluginObject, org.hyperic.hypclipse.plugin.IPluginObject)
	 */
	public void swap(IPluginObject child1, IPluginObject child2) throws CoreException {
		ensureModelEditable();
		int index1 = getChildrenList().indexOf(child1);
		int index2 = getChildrenList().indexOf(child2);
		if (index1 == -1 || index2 == -1)
			throwCoreException("PDECoreMessages.PluginParent_siblingsNotFoundException");
		getChildrenList().set(index2, child1);
		getChildrenList().set(index1, child2);
		firePropertyChanged(this, P_SIBLING_ORDER, child1, child2);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginParent#getChildren()
	 */
	public IPluginObject[] getChildren() {
		return (IPluginObject[]) getChildrenList().toArray(new IPluginObject[getChildrenList().size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hypclipse.plugin.IPluginParent#remove(org.hyperic.hypclipse.plugin.IPluginObject)
	 */
	public void remove(IPluginObject child) throws CoreException {
		ensureModelEditable();
		getChildrenList().remove(child);
		((PluginObject) child).setInTheModel(false);
		fireStructureChanged(child, IModelChangedEvent.REMOVE);
	}

	protected ArrayList<IPluginObject> getChildrenList() {
		if (fChildren == null)
			fChildren = new ArrayList<IPluginObject>(1);
		return fChildren;
	}

}
