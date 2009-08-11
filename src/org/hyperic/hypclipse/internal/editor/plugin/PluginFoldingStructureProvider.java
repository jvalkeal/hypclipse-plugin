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
package org.hyperic.hypclipse.internal.editor.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.hyperic.hypclipse.internal.editor.AbstractFoldingStructureProvider;
import org.hyperic.hypclipse.internal.editor.HQDESourcePage;
import org.hyperic.hypclipse.internal.hqmodel.IHQMetrics;
import org.hyperic.hypclipse.internal.hqmodel.IHQServer;
import org.hyperic.hypclipse.internal.text.IDocumentElementNode;
import org.hyperic.hypclipse.internal.text.PluginModel;
import org.hyperic.hypclipse.internal.text.plugin.PluginBaseNode;
import org.hyperic.hypclipse.plugin.IEditingModel;
import org.hyperic.hypclipse.plugin.IPluginBase;
import org.hyperic.hypclipse.plugin.IPluginParent;

public class PluginFoldingStructureProvider extends AbstractFoldingStructureProvider {

	private Map fPositionToElement = new HashMap();

	public PluginFoldingStructureProvider(HQDESourcePage editor, IEditingModel model) {
		super(editor, model);
	}

	public void addFoldingRegions(Set currentRegions, IEditingModel model) throws BadLocationException {

		IPluginBase base = ((PluginModel) model).getPluginBase();
		
		IDocumentElementNode[] nodes = ((PluginBaseNode)base).getChildNodes();
		addFoldingRegions(currentRegions, nodes, model.getDocument());
//		IHQServer[] servers = base.getServers();
//		addFoldingRegions(currentRegions, servers, model.getDocument());
//		IHQMetrics[] metrics = base.getMetrics();
//		addFoldingRegions(currentRegions, metrics, model.getDocument());
	}

	private void addFoldingRegions(Set regions, IPluginParent[] nodes, IDocument document) throws BadLocationException {
		for (int i = 0; i < nodes.length; i++) {
			IDocumentElementNode element = (IDocumentElementNode) nodes[i];
			int startLine = document.getLineOfOffset(element.getOffset());
			int endLine = document.getLineOfOffset(element.getOffset() + element.getLength());
			if (startLine < endLine) {
				int start = document.getLineOffset(startLine);
				int end = document.getLineOffset(endLine) + document.getLineLength(endLine);
				Position position = new Position(start, end - start);
				regions.add(position);
				fPositionToElement.put(position, element);
			}
			IDocumentElementNode[] children = element.getChildNodes();
			if (children != null) {
				addFoldingRegions(regions, children, document);
			}
		}
	}


	private void addFoldingRegions(Set regions, IDocumentElementNode[] nodes, IDocument document) throws BadLocationException {
		for (int i = 0; i < nodes.length; i++) {
			IDocumentElementNode element = nodes[i];
			int startLine = document.getLineOfOffset(element.getOffset());
			int endLine = document.getLineOfOffset(element.getOffset() + element.getLength());
			if (startLine < endLine) {
				int start = document.getLineOffset(startLine);
				int end = document.getLineOffset(endLine) + document.getLineLength(endLine);
				Position position = new Position(start, end - start);
				regions.add(position);
				fPositionToElement.put(position, element);
			}
			IDocumentElementNode[] children = element.getChildNodes();
			if (children != null) {
				addFoldingRegions(regions, children, document);
			}
		}
	}

}
