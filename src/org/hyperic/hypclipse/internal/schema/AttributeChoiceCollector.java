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

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

public class AttributeChoiceCollector implements ExpressionVisitorVoid {

	private ArrayList<String> result;
	
	public final void collect(Expression exp, ArrayList<String> result) {
		this.result = result;
		exp.visit(this);
	}

	protected boolean isComplex( Expression exp ) {
        return exp instanceof BinaryExp;
    }
	
    protected void binary( BinaryExp exp) {
        
        if( exp.exp1.getClass()==exp.getClass() || !isComplex(exp.exp1) ) {
            exp.exp1.visit(this);
           
        } else {
            exp.exp1.visit(this);
           
        }
        
        if( !isComplex(exp.exp2) ) 
                exp.exp2.visit(this);
        else
                exp.exp2.visit(this);
        
    }

    private void optional( Expression exp ) {
        if( exp instanceof OneOrMoreExp ) {
            OneOrMoreExp ome = (OneOrMoreExp)exp;
            if( isComplex(ome.exp) )    ome.exp.visit(this);
            else                        ome.exp.visit(this);
        } else {
            if( isComplex(exp) )    exp.visit(this);
            else                    exp.visit(this);
        }
    }

	
    public void onRef( ReferenceExp exp ) {
        exp.exp.visit(this);
    }
        
    public void onOther( OtherExp exp ) {
    	exp.exp.visit(this);
    }
    
    public void onElement( ElementExp exp ) {
//        exp.contentModel.visit(this);
    }
        
    public void onEpsilon() {}
    public void onNullSet() {}
    public void onAnyString() {}
    public void onData( DataExp exp ) {}
    public void onValue( ValueExp exp ) {
    	result.add((String)exp.value);
    }
        
    public void onInterleave( InterleaveExp exp ) {
    	binary(exp);
    }
        
    public void onConcur( ConcurExp exp ) {
    	binary(exp);
    }
            
    public void onChoice( ChoiceExp exp ) {
        if( exp.exp1==Expression.epsilon ){
        	optional(exp.exp2);
        	return;
        }
        if( exp.exp2==Expression.epsilon ){
        	optional(exp.exp1);
        	return;
        }
            
        binary(exp);
    }
        
    public void onSequence( SequenceExp exp ) {
    	binary(exp);
    }
        
    public void onMixed( MixedExp exp ) {
        exp.exp.visit(this);
    }
    
    public void onList( ListExp exp ) {
        exp.exp.visit(this);
    }
    
    public void onOneOrMore( OneOrMoreExp exp ) {
        exp.exp.visit(this);
    }
        
    public void onAttribute( AttributeExp exp ) {
        exp.exp.visit(this);
    }

	
}
