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

package org.hyperic.hypclipse.internal.util;

import java.util.HashMap;

/**
 * PDEHTMLHelper
 *
 */
public class HQDEHTMLHelper {

	public final static HashMap<String, String> fgEntityLookup = new HashMap<String, String>(6);
	static {
		fgEntityLookup.put("lt", "<");
		fgEntityLookup.put("gt", ">");
		fgEntityLookup.put("nbsp", " ");
		fgEntityLookup.put("amp", "&");
		fgEntityLookup.put("apos", "'");
		fgEntityLookup.put("quot", "\"");
	}

	public static String stripTags(String html) {
		if (html == null) {
			return null;
		}
		int length = html.length();
		boolean write = true;
		char oldChar = ' ';
		StringBuffer sb = new StringBuffer(length);

		boolean processingEntity = false;
		StringBuffer entityBuffer = null;

		for (int i = 0; i < length; i++) {
			char curr = html.charAt(i);

			// Detect predefined character entities
			if (curr == '&') {
				// Process predefined character entity found
				processingEntity = true;
				entityBuffer = new StringBuffer();
				continue;
			} else if (processingEntity && (curr == ';')) {
				// End of predefined character entity found
				processingEntity = false;
				// Resolve the entity
				String entity = ((String) fgEntityLookup.get(entityBuffer.toString()));
				if (entity == null) {
					// If the entity is not found or supported, ignore it
					continue;
				}
				// Present the resolved character for writing
				curr = entity.charAt(0);
			} else if (processingEntity) {
				// Collect predefined character entity name character by 
				// character
				entityBuffer.append(curr);
				continue;
			}

			if (curr == '<') {
				write = false;
			} else if (curr == '>') {
				write = true;
			} else if (write && curr != '\r' && curr != '\n' && curr != '\t') {
				if (!(curr == ' ') || !(oldChar == curr)) { // skip multiple spaces
					sb.append(curr);
					oldChar = curr;
				}
			}
		}
		if (isAllWhitespace(sb.toString())) {
			return null;
		}
		return sb.toString();
	}

	public static boolean isAllWhitespace(String string) {
		if (string == null) {
			return false;
		}
		char[] characters = string.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			if (!Character.isWhitespace(characters[i])) {
				return false;
			}
		}
		return true;
	}

}
