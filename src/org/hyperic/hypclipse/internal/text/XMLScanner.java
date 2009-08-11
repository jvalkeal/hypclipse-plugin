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

public class XMLScanner extends BaseHQDEScanner {
	private Token fProcInstr;

//	private Token fExternalizedString;

	public XMLScanner(IColorManager manager) {
		super(manager);
	}

	protected void initialize() {
		fProcInstr = new Token(createTextAttribute(IPDEColorConstants.P_PROC_INSTR));
//		fExternalizedString = new Token(createTextAttribute(IPDEColorConstants.P_EXTERNALIZED_STRING));

		IRule[] rules = new IRule[2];
		//Add rule for processing instructions
		rules[0] = new SingleLineRule("<?", "?>", fProcInstr);
//		rules[1] = new ExternalizedStringRule(fExternalizedString);
		// Add generic whitespace rule.
		rules[1] = new WhitespaceRule(new XMLWhitespaceDetector());
		setRules(rules);
		setDefaultReturnToken(new Token(createTextAttribute(IPDEColorConstants.P_DEFAULT)));
	}

	protected Token getTokenAffected(PropertyChangeEvent event) {
		if (event.getProperty().startsWith(IPDEColorConstants.P_PROC_INSTR)) {
			return fProcInstr;
		}/* else if (event.getProperty().startsWith(IPDEColorConstants.P_EXTERNALIZED_STRING)) {
			return fExternalizedString;
		}*/
		return (Token) fDefaultReturnToken;
	}

	public boolean affectsTextPresentation(String property) {
		return property.startsWith(IPDEColorConstants.P_DEFAULT) || property.startsWith(IPDEColorConstants.P_PROC_INSTR) /*|| property.startsWith(IPDEColorConstants.P_EXTERNALIZED_STRING)*/;
	}

}
