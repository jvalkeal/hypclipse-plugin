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

/**
 * ExternalizedStringRule
 *
 */
public class ExternalizedStringRule extends WordPatternRule {

	//TODO if we are not using language support, remove this class
	
	/**
	 * ExternalizedStringDetector
	 *
	 */
	private static class ExternalizedStringDetector implements IWordDetector {

		/**
		 * 
		 */
		public ExternalizedStringDetector() {
			super();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
		 */
		public boolean isWordStart(char character) {
			if (character == '%') {
				return true;
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
		 */
		public boolean isWordPart(char character) {
			if (Character.isLetterOrDigit(character)) {
				return true;
			} else if (character == '.') {
				return true;
			} else if (character == '-') {
				return true;
			} else if (character == '_') {
				return true;
			}
			return false;
		}
	}

	/**
	 * @param token
	 */
	public ExternalizedStringRule(IToken token) {
		super(new ExternalizedStringDetector(), "%", null, token);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.WordPatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		// Read the next character
		char character = (char) scanner.read();
		// Detect end character
		if (Character.isWhitespace(character)) {
			return true;
		} else if (character == '<') {
			return true;
		}
		// Unread the character just read
		scanner.unread();
		// Process as normal
		return super.endSequenceDetected(scanner);
	}

}
