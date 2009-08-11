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


import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * creates a string representation of the expression.
 * 
 * useful for debug and dump.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnnoPrinter implements ExpressionVisitor {
    
    
    /** in this mode, reference to other expression is
     * one of the terminal symbol of stringnization.
     * 
     * Suitable to dump the entire grammar
     */
    public final static int MASTER = 0x001;
    
    /** in this mode, element declaration is
     * one of the terminal symbol of stringnization.
     * 
     * Suitable to dump the content model of element declarations.
     */
    public final static int SUB = 0x002;

    
    
    // singleton access
    public static AnnoPrinter masterInstance = new AnnoPrinter(MASTER);
    public static AnnoPrinter subInstance = new AnnoPrinter(SUB);
    
    public static String printMaster(Expression exp) {
        return (String)exp.visit(masterInstance);
    }
    public static String printSub(Expression exp, String tagName) {
    	subInstance.setTagName(tagName);
        return (String)exp.visit(subInstance);
    }
    
    
    /** this flag controls how expression will be stringnized */
    protected final int mode;
    
    // tag to find
    protected String tagName;
    
    protected AnnoPrinter( int mode) {
    	this.mode = mode;
    }
    
    public void setTagName(String tagName) {
    	this.tagName = tagName;
    }
    
    /** dumps all the contents of ReferenceContainer.
     * 
     * this method is a useful piece to dump the entire grammar.
     */
    public String printRefContainer( ReferenceContainer cont ) {
        String r="";
        java.util.Iterator itr = cont.iterator();
        while( itr.hasNext() ) {
            ReferenceExp exp = (ReferenceExp)itr.next();
            
            r += exp.name + "  : " + exp.exp.visit(this) + "\n";
        }
        return r;
    }
    
    /** determines whether brackets should be used to represent the pattern */
    protected static boolean isComplex( Expression exp ) {
        return exp instanceof BinaryExp;
    }
    
    protected String printBinary( BinaryExp exp, String op ) {
        String r;
        
        if( exp.exp1.getClass()==exp.getClass() || !isComplex(exp.exp1) )
            r = (String)exp.exp1.visit(this);
        else
//            r = "("+exp.exp1.visit(this)+")";
        	r = (String) exp.exp1.visit(this);

//        r+=op;
        
        if( !isComplex(exp.exp2) )
                r+=exp.exp2.visit(this);
        else
//            r+="("+exp.exp2.visit(this)+")";
                r+=exp.exp2.visit(this);
        
        return r;
    }
    
    public Object onAttribute( AttributeExp exp ) {
//        return "@"+exp.nameClass.toString()+"<"+exp.exp.visit(this)+">";
        return "";
    }
    
    private Object optional( Expression exp ) {
        if( exp instanceof OneOrMoreExp ) {
            OneOrMoreExp ome = (OneOrMoreExp)exp;
//            if( isComplex(ome.exp) )    return "("+ome.exp.visit(this)+")*";
            if( isComplex(ome.exp) )    return ome.exp.visit(this);
            else                        return ome.exp.visit(this)+"*";
        } else {
//            if( isComplex(exp) )    return "("+exp.visit(this)+")?";
            if( isComplex(exp) )    return exp.visit(this);
//            else                    return exp.visit(this)+"?";
            else                    return exp.visit(this);
        }
    }
    
    public Object onChoice( ChoiceExp exp )     {
        if( exp.exp1==Expression.epsilon )    return optional(exp.exp2);
        if( exp.exp2==Expression.epsilon )    return optional(exp.exp1);
            
        return printBinary(exp,"|");
    }

    public Object onConcur( ConcurExp exp ) {
        return printBinary(exp,"&");
    }
    public Object onInterleave( InterleaveExp exp ){
        return printBinary(exp,"^");
    }
    
    public Object onElement( ElementExp exp ) {
        if( mode==SUB ) {
        	if (exp.getNameClass().toString().equals(tagName)) {
//        		return exp.contentModel.visit(this);	
            	return exp.getNameClass().toString();
//        		return exp.contentModel.toString();
        	} else {
        		return "";
        	}
//        	return exp.getNameClass().toString();
        } else {
//          return exp.getNameClass().toString()+"<"+exp.contentModel.visit(this)+">";
            return exp.contentModel.visit(this);
        	
        }
    }
    
    public Object onOneOrMore( OneOrMoreExp exp ) {
//        if( isComplex(exp.exp) )    return "("+exp.exp.visit(this)+")+";
        if( isComplex(exp.exp) )    return exp.exp.visit(this);
        else                        return exp.exp.visit(this)+"+";
    }
    
    public Object onMixed( MixedExp exp ) {
//        return "mixed["+exp.exp.visit(this)+"]";
        return "mixed["+exp.exp.visit(this)+"]";
    }
    
    public Object onList( ListExp exp ) {
//        return "list["+exp.exp.visit(this)+"]";
        return "list["+exp.exp.visit(this)+"]";
    }
    
    public Object onEpsilon() {
//        return "#epsilon";
        return "";
    }
    
    public Object onNullSet() {
//        return "#nullSet";
        return "";
    }
    
    public Object onAnyString() {
//        return "<anyString>";
        return "";
    }
    
    public Object onSequence( SequenceExp exp )    {
        return printBinary(exp,",");
    }
    
    public Object onData( DataExp exp ) {
//        return "$"+exp.name.localName;
        return "";
    }    

    public Object onValue( ValueExp exp ) {
//        return "$$"+exp.value;
        return "";
    }    
    
    public Object onOther( OtherExp exp ) {
    	if(exp instanceof AnnotationExp) {
    		if( mode==SUB ) {
    			String compare = (String) exp.exp.visit(this);
    			if(compare.equals(tagName))
    				return ((AnnotationExp)exp).annotation;
//    			String name = ((AnnotationExp)exp).exp.peelOccurence().toString();
//    			System.out.println("NAME: " + name);
//    			if(exp.printName().equals(tagName))
//    				return ((AnnotationExp)exp).annotation ;
//    			else
    				return exp.exp.visit(this);
//    			return "";
    		} else {
                return ((AnnotationExp)exp).annotation ;    			
    		}
//            return exp.printName()+"\n"+ ((AnnotationExp)exp).annotation +"\n"+"["+exp.exp.visit(this)+"]";
//            return ((AnnotationExp)exp).annotation ;
    		
    	} else {
            return exp.printName()+"["+exp.exp.visit(this)+"]";
    		
    	}
        
    }
        
    public Object onRef( ReferenceExp exp ) {
//        if( mode==SUB )        return "{%"+exp.name+"}";
////        else                            return "("+exp.exp.visit(this)+")";
//        else                            return "####"+exp.exp.visit(this)+"::::";
//    	return "####"+exp.exp.visit(this)+"::::";
    	return exp.exp.visit(this);
    }
}
