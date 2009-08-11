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

import java.util.Collection;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.util.ExpressionWalker;

public class AttributeCollector extends ExpressionWalker {

	private Collection<AttributeExp> result;
	private String[] elements;
	
	boolean got = false;
	boolean stop = false;
	int position = 0;
	
	public AttributeCollector() {
		
	}
	
	public final void collect(Expression exp, Collection<AttributeExp> result, String path) {
		this.result = result;
		this.elements = handlePath(path);
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
		System.out.println(name);
		if(name.equals(elements[position])) {
			if(position < elements.length-1) {
				position++;
			} else {
				got = true;
			}
			if(!stop)
				exp.contentModel.visit(this);
			
		}
	}


	public void onAttribute(AttributeExp exp) {
		if(got) {
			stop = true;
			result.add(exp);
		}
	}
	

}
