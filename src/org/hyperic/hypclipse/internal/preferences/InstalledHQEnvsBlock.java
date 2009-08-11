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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.hyperic.hypclipse.internal.HQDEMessages;
import org.hyperic.hypclipse.internal.HQDEPlugin;
import org.hyperic.hypclipse.internal.SWTFactory;

/**
 * A composite that displays installed HQ agents. Agent bundles
 * as base for development environment. Agents can be added,
 * removed and edited.
 *
 */
public class InstalledHQEnvsBlock {
	
	/** Control handle */
	private Composite fControl;
	
	/** Stored and displayed agents */
	private List<HQEnvInfo> fHQs = new ArrayList<HQEnvInfo>();
	
	/** Control to list */
	private CheckboxTableViewer fHQList;

	/** Handle to swt table */
    private Table fTable;

	// Handles to buttons
	private Button fAddButton;
	private Button fRemoveButton;
//	private Button fEditButton;

	// index of column used for sorting
	private int fSortColumn = 0;

	private ISelection fPrevSelection = new StructuredSelection();

	public void createControl(Composite ancestor) {
		Font font = ancestor.getFont();
		Composite parent= SWTFactory.createComposite(ancestor, font, 2, 1, GridData.FILL_BOTH);
		fControl = parent;	

		SWTFactory.createLabel(parent, HQDEMessages.HQEnvsPreferencePage_ttitle, 2);

		fTable= new Table(parent, SWT.CHECK | SWT.RADIO | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 350;
		fTable.setLayoutData(gd);
		fTable.setFont(font);
		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);	

		TableColumn column = new TableColumn(fTable, SWT.NULL);
		column.setText("Name"); 
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO removed sorting
				//sortByName();
			}
		});

		int defaultwidth = 350/2 +1;
		column.setWidth(defaultwidth);

		column = new TableColumn(fTable, SWT.NULL);
		column.setText("Location"); 
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO removed sorting
				//sortByLocation();
			}
		});
		column.setWidth(defaultwidth);

		fHQList = new CheckboxTableViewer(fTable);			
		fHQList.setLabelProvider(new HQEnvLabelProvider());
		fHQList.setContentProvider(new HQsContentProvider());
		// by default, sort by name
		// TODO removed sorting
		//sortByName();
		
		fHQList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});
		
		fHQList.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					setCheckedHQEnv((HQEnvInfo)event.getElement());
				} else {
					setCheckedHQEnv(null);
				}

			}
		});

		fTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					if (fRemoveButton.isEnabled()){
						// TODO removed function
						//removeVMs();
					}
				}
			}
		});	

		Composite buttons = SWTFactory.createComposite(parent, font, 1, 1, GridData.VERTICAL_ALIGN_BEGINNING, 0, 0);
		fAddButton = SWTFactory.createPushButton(buttons, "Add", null); 
		fAddButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				addHQEnv();
			}
		});
		
//		fEditButton= SWTFactory.createPushButton(buttons, "Edit", null); 
//		fEditButton.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event evt) {
//			}
//		});

		fRemoveButton= SWTFactory.createPushButton(buttons, "Remove", null); 
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				removeHQEnvs();
			}
		});

		SWTFactory.createVerticalSpacer(parent, 1);

		enableButtons();

		fillWithHQEnvironments();
	}
	
	/**
	 * Enables the buttons based on selected items counts in the viewer
	 */
	private void enableButtons() {
		IStructuredSelection selection = (IStructuredSelection) fHQList.getSelection();
		int selectionCount= selection.size();
//		fEditButton.setEnabled(selectionCount == 1);
		if (selectionCount > 0 && selectionCount < fHQList.getTable().getItemCount()) {
			fRemoveButton.setEnabled(true);
		} else {
			fRemoveButton.setEnabled(false);
		}
	}	

	/**
	 * Returns this block's control
	 * 
	 * @return control
	 */
	public Control getControl() {
		return fControl;
	}

	
	/**
	 * Restore table settings from the given dialog store using the
	 * given key.
	 * 
	 * @param settings dialog settings store
	 * @param qualifier key to restore settings from
	 */
	public void restoreColumnSettings(IDialogSettings settings, String qualifier) {
		fHQList.getTable().layout(true);
        restoreColumnWidths(settings, qualifier);
		try {
			fSortColumn = settings.getInt(qualifier + ".sortColumn"); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			fSortColumn = 1;
		}
		switch (fSortColumn) {
			case 1:
//				sortByName();
				break;
			case 2:
//				sortByLocation();
				break;
		}
	}
	
	/**
	 * Restores the column widths from dialog settings
	 * @param settings
	 * @param qualifier
	 */
	private void restoreColumnWidths(IDialogSettings settings, String qualifier) {
        int columnCount = fTable.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            int width = -1;
            try {
                width = settings.getInt(qualifier + ".columnWidth" + i); //$NON-NLS-1$
            } catch (NumberFormatException e) {}
            
            if ((width <= 0) || (i == fTable.getColumnCount() - 1)) {
                fTable.getColumn(i).pack();
            } else {
                fTable.getColumn(i).setWidth(width);
            }
        }
	}
	
	/**
	 * Utility method to setting checked environment.
	 * @param env
	 */
	private void setCheckedHQEnv(HQEnvInfo env) {
		if (env == null) {
			setSelection(new StructuredSelection());
		} else {
			setSelection(new StructuredSelection(env));
		}
		savePrefs();
	}

	/**
	 * Checks selected element and unchecks others.
	 * @param selection
	 */
	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			if (!selection.equals(fPrevSelection)) {
				fPrevSelection = selection;
				Object env = ((IStructuredSelection)selection).getFirstElement();
				if (env == null) {
					fHQList.setCheckedElements(new Object[0]);
				} else {
					fHQList.setCheckedElements(new Object[]{env});
					fHQList.reveal(env);
				}
//				fireSelectionChanged();
			}
		}
	}
	
	private String[] getNames() {
		ArrayList<String> names = new ArrayList<String>();
		ListIterator<HQEnvInfo> iterator = fHQs.listIterator();
		while(iterator.hasNext())
			names.add(iterator.next().getName());
		return names.toArray(new String[0]);
	}


	/**
	 * Opens dialog to allow user to enter info
	 * for new agent bundles.
	 */
	private void addHQEnv() {
		HQEnvWizard wizard = new HQEnvWizard(getNames());
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() == Window.OK) {
			HQEnvInfo result = wizard.getResult();
			if (result != null) {
				fHQs.add(result);
				fHQList.refresh();
				// if this is first insert to table
				// mark it as default
				if(fHQs.size() == 1) {
					result.setDefault(true);
					fHQList.setChecked(result, true);
				}

//				fHQList.setSelection(new StructuredSelection(result));
				savePrefs();
			}
		}

	}

	/**
	 * Removes selected entries.
	 */
	private void removeHQEnvs() {
		IStructuredSelection selection = (IStructuredSelection) fHQList.getSelection();
		int selectionCount= selection.size();
		Iterator iterator = selection.iterator();
		while(iterator.hasNext()) {
			HQEnvInfo env = (HQEnvInfo) iterator.next();
			fHQs.remove(env);
			fHQList.refresh();
			savePrefs();			
		}
	}

	/**
	 * Populates table from stored environments.
	 */
	private void fillWithHQEnvironments() {
		HQEnvInfo[] envs = PreferencesManager.getInstance().getEnvironments();
		if(envs == null) {
			setEnvs(new HQEnvInfo[0]);
		} else {			
			setEnvs(envs);
		}
	}
	
	/**
	 * Stores environments to manager.
	 */
	private void savePrefs() {
		HQEnvInfo[] x = (HQEnvInfo[]) fHQs.toArray(new HQEnvInfo[0]);
		PreferencesManager m = PreferencesManager.getInstance();
		
		Object[] selected = fHQList.getCheckedElements();
		if(selected != null && selected.length > 0) {
			m.setEnvironments(x,(HQEnvInfo)selected[0]);						
		} else {
			m.setEnvironments(x);			
		}
		
	}

	/**
	 * Returns shell from attached control.
	 * 
	 * @return Shell
	 */
	protected Shell getShell() {
		return getControl().getShell();
	}
	
	/**
	 * Attaching list of object to table.
	 * 
	 * @param envs List of HQEnvInfo object to attach to table
	 */
	protected void setEnvs(HQEnvInfo[] envs) {
		PreferencesManager mm = PreferencesManager.getInstance();
		HQEnvInfo defaultEnvironment = null;
		fHQs.clear();
		for (int i = 0; i < envs.length; i++) {
			fHQs.add(envs[i]);
			if(envs[i].isDefault()) {
				defaultEnvironment = envs[i];
			}
		}
		fHQList.setInput(fHQs);
		if(defaultEnvironment != null)
			fHQList.setChecked(defaultEnvironment, true);
		fHQList.refresh();
		
	}

	/**
	 * Label provider for installed HQ Agents.
	 */
	class HQEnvLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof HQEnvInfo) {
				HQEnvInfo i = (HQEnvInfo)element;
				switch(columnIndex) {
					case 0:
						return i.getName();
					case 1:
						return i.getLocation();
				}
			}
			return null;
		}
		
	}

	/**
	 * Content provider for list of agents.
	 */
	class HQsContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return fHQs.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}		
		
	}

	
}
