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
package org.hyperic.hypclipse.internal.text;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.util.PropertyChangeEvent;

public class XMLTagScanner extends BaseHQDEScanner {

	private Token fStringToken;

//	private Token fExternalizedStringToken;

	public XMLTagScanner(IColorManager manager) {
		super(manager);
	}

	protected void initialize() {
		fStringToken = new Token(createTextAttribute(IPDEColorConstants.P_STRING));
//		fExternalizedStringToken = new Token(createTextAttribute(IPDEColorConstants.P_EXTERNALIZED_STRING));
		IRule[] rules = new IRule[3];
//		rules[0] = new SingleLineRule("\"%", "\"", fExternalizedStringToken);
//		rules[1] = new SingleLineRule("'%", "'", fExternalizedStringToken);
		// Add rule for single and double quotes
		rules[0] = new MultiLineRule("\"", "\"", fStringToken);
		rules[1] = new SingleLineRule("'", "'", fStringToken);
		// Add generic whitespace rule.
		rules[2] = new WhitespaceRule(new XMLWhitespaceDetector());
		setRules(rules);
		setDefaultReturnToken(new Token(createTextAttribute(IPDEColorConstants.P_TAG)));
	}

	protected Token getTokenAffected(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.startsWith(IPDEColorConstants.P_STRING)) {
			return fStringToken;
		}/* else if (property.startsWith(IPDEColorConstants.P_EXTERNALIZED_STRING)) {
			return fExternalizedStringToken;
		}*/
		return (Token) fDefaultReturnToken;
	}

	public boolean affectsTextPresentation(String property) {
		return property.startsWith(IPDEColorConstants.P_TAG) || property.startsWith(IPDEColorConstants.P_STRING) /*|| property.startsWith(IPDEColorConstants.P_EXTERNALIZED_STRING)*/;
	}

}
