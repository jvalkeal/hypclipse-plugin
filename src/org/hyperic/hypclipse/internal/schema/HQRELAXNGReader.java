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

import java.util.Stack;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.ng.comp.RELAXNGCompReader;
import com.sun.msv.util.StartTagInfo;

public class HQRELAXNGReader extends RELAXNGCompReader {

	public HQRELAXNGReader(GrammarReaderController controller,
			SAXParserFactory parserFactory, ExpressionPool pool) {
		super(controller, parserFactory, pool);
	}

	protected Expression interceptExpression(State state, Expression exp) {
		final StartTagInfo tag = state.getStartTag();
		String name = tag.localName;
//		System.out.println("TAG INTERCEPT: "+name);
		
		String doc = stack.pop().getFirst();
		
//		System.out.println("STACK: "+name+" "+ (doc!=null ? doc : "null") + "::: " + stack.size());
		//exp = new ReferenceExp( name, exp );

		if(doc != null) {
//			System.out.println("annotation");
			AnnotationExp aExp = new AnnotationExp();
//			setDeclaredLocationOf(aExp);
			aExp.annotation = doc;
			
			aExp.exp = exp;
			exp = aExp;
//			exp = new ReferenceExp( name, exp );
		}
		return super.interceptExpression(state, exp);
	}
	
	/**
	 * Currently parsing annonation tag
	 */
    private boolean inAnnotation = false;
    
	/**
	 * Stack to contain annonations. 
	 */
	private AnnoStack stack = new AnnoStack();

    public void startElement( String uri, String local, String qname, Attributes atts ) throws SAXException {
    	if(local.equals("documentation")) {
    		inAnnotation = true;
    	} else {
    		// if tag name is param, there no interception.
    		// maybe param is not related to expression.
    		// so hack it and don't prepare new stack item.
    		
    		if(!local.equals("param"))
    			stack.prepareNew();
    		inAnnotation = false;
    	}
        super.startElement(uri,local,qname,atts);
    	
    }

	public void characters(char[] ch, int start, int length) {
		if(inAnnotation) {
			String doc = new String(ch,start,length);
//			System.out.println("::"+doc+"::");
			stack.write(doc);
		}
		try {
			super.characters(ch, start, length);
		} catch (SAXException e) {
		}
	}

    public void endElement( String uri, String local, String qname ) throws SAXException {
    	if(local.equals("documentation")) {
    		stack.finish();
    	}
        
        inAnnotation = false;

        super.endElement(uri,local,qname);
    }

	

}
