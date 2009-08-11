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
package org.hyperic.hypclipse.internal.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.util.ExpressionWalker;

public class ElementPathCollector extends ExpressionWalker {

	private Collection<ElementExp> result;
	private String[] elements;
	
	private int position = 0;
	private ExpressionPool pool = new ExpressionPool();
	
	public ElementPathCollector() {
		// nothing to do
	}
	
	public final void collect(Expression exp, Collection<ElementExp> result, String path) {
		this.result = result;
		elements = handlePath(path);
		if(elements != null && elements.length > 0)
			exp.visit(this);
	}
	
	private String[] handlePath(String path) {
		String orig[] =  path.split("/");
		String ret[] = new String[orig.length-1];
		System.arraycopy(orig, 1, ret, 0, ret.length);
		return ret;
	}

	public void onElement(ElementExp exp) {

		String name = exp.getNameClass().toString();

		// we have found the element which childs we need to collect
		if(elements.length == position+1 && name.equals(elements[position])) {
			Collection<ElementExp> sElements1 = new ArrayList<ElementExp>();
			ElementCollector coll1 = new ElementCollector();
			Expression eExp1 = exp.contentModel.getExpandedExp(pool);
			coll1.collect(eExp1, sElements1);
			result.addAll(sElements1);
			return;
		}
		
		// find direct childs
		ArrayList<ElementExp> sElements = new ArrayList<ElementExp>();
		ElementCollector coll = new ElementCollector();
		Expression eExp = exp.contentModel.getExpandedExp(pool);
		coll.collect(eExp, sElements);
		
		// check if we find matching child from current position
		ListIterator<ElementExp> iter = sElements.listIterator();
		ElementExp found = null;
		while(iter.hasNext()) {
			ElementExp exp1 = iter.next();
			String name1 = exp1.getNameClass().toString();
			if(name1.equals(elements[position+1])) {
				position++;
				found = exp1;
				break;
			}
		}
		if(found != null)
			found.visit(this);
	}


	

}
