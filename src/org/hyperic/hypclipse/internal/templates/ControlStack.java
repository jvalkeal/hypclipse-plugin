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
package org.hyperic.hypclipse.internal.templates;

import java.util.Iterator;
import java.util.Stack;

public class ControlStack {
	private Stack<Entry> stack;
	private PreprocessorParser parser;

	class Entry {
		boolean value;
	}

	public ControlStack() {
		stack = new Stack<Entry>();
		parser = new PreprocessorParser();
	}

	public void setValueProvider(IVariableProvider provider) {
		parser.setVariableProvider(provider);
	}

	public void processLine(String line) {
		if (line.startsWith("if")) { //$NON-NLS-1$
			String expression = line.substring(2).trim();
			boolean result = false;
			try {
				result = parser.parseAndEvaluate(expression);
			} catch (Exception e) {
			}
			Entry entry = new Entry();
			entry.value = result;
			stack.push(entry);
		} else if (line.startsWith("else")) { //$NON-NLS-1$
			if (stack.isEmpty() == false) {
				Entry entry = stack.peek();
				entry.value = !entry.value;
			}
		} else if (line.startsWith("endif")) { //$NON-NLS-1$
			// pop the stack
			if (!stack.isEmpty())
				stack.pop();
		} else {
			// a preprocessor comment - ignore it
		}
	}

	public boolean getCurrentState() {
		if (stack.isEmpty())
			return true;
		// All control levels must evaluate to true to
		// return result==true
		for (Iterator<Entry> iter = stack.iterator(); iter.hasNext();) {
			Entry entry = iter.next();
			if (!entry.value)
				return false;
		}
		return true;
	}
}
