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
package org.hyperic.hypclipse.internal.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.hyperic.hypclipse.plugin.IEditingModel;
import org.hyperic.hypclipse.plugin.IModelChangedEvent;
import org.hyperic.hypclipse.plugin.IModelChangedListener;

public abstract class AbstractFoldingStructureProvider implements IFoldingStructureProvider, IModelChangedListener {

	private HQDESourcePage fEditor;
	private IEditingModel fModel;

	public AbstractFoldingStructureProvider(HQDESourcePage editor, IEditingModel model) {
		this.fEditor = editor;
		this.fModel = model;
	}

	public void update() {
		ProjectionAnnotationModel annotationModel = (ProjectionAnnotationModel) fEditor.getAdapter(ProjectionAnnotationModel.class);
		if (annotationModel == null)
			return;

		Set currentRegions = new HashSet();
		try {
			addFoldingRegions(currentRegions, fModel);
			updateFoldingRegions(annotationModel, currentRegions);
		} catch (BadLocationException e) {
		}
	}

	public void updateFoldingRegions(ProjectionAnnotationModel model, Set currentRegions) {
		Annotation[] deletions = computeDifferences(model, currentRegions);

		Map additionsMap = new HashMap();
		for (Iterator iter = currentRegions.iterator(); iter.hasNext();) {
			Object position = iter.next();
			additionsMap.put(new ProjectionAnnotation(false), position);
		}

		if ((deletions.length != 0 || additionsMap.size() != 0)) {
			model.modifyAnnotations(deletions, additionsMap, new Annotation[] {});
		}
	}

	private Annotation[] computeDifferences(ProjectionAnnotationModel model, Set additions) {
		List deletions = new ArrayList();
		for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();) {
			Object annotation = iter.next();
			if (annotation instanceof ProjectionAnnotation) {
				Position position = model.getPosition((Annotation) annotation);
				if (additions.contains(position)) {
					additions.remove(position);
				} else {
					deletions.add(annotation);
				}
			}
		}
		return (Annotation[]) deletions.toArray(new Annotation[deletions.size()]);
	}

	public void initialize() {
		update();
	}

	public void modelChanged(IModelChangedEvent event) {
		update();
	}

	public void reconciled(IDocument document) {
		update();
	}

}
