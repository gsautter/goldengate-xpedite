/*
 * Copyright (c) 2006-, IPD Boehm, Universitaet Karlsruhe (TH) / KIT, by Guido Sautter
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Universitaet Karlsruhe (TH) / KIT nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY UNIVERSITAET KARLSRUHE (TH) / KIT AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.uka.ipd.idaho.xm.xpedite.plugins;

import java.awt.event.KeyEvent;

import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay;
import de.uka.ipd.idaho.goldenGate.ui.KeystrokeActionDescriptor;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.InstantAction;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.PointSelection;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.TagSelection;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.TokenSelection;

/**
 * Provider of click actions working on XML Markup documents, to dynamically
 * extend functionality.
 * 
 * @author sautter
 */
public interface KeystrokeActionProvider extends GoldenGateXpeditePlugin {
	
	/**
	 * Convenience implementation of an instant action, ensuring the actual
	 * logic implemented in the <code>doExecuteAction()</code> method runs
	 * within an atomic action in the target XML document markup panel.
	 * 
	 * @author sautter
	 */
	public static abstract class AtomicInstantAction extends InstantAction {
		private int priority;
		
		/** Constructor
		 * @param name the name of the selection action
		 * @param label the label string to show in the context menu
		 * @param priority the priority of the actions
		 */
		public AtomicInstantAction(String name, String label, int priority) {
			this(name, label, null, priority);
		}
		
		/** Constructor
		 * @param name the name of the selection action
		 * @param label the label string to show in the context menu
		 * @param tooltip the tooltip text for the context menu
		 * @param priority the priority of the actions
		 */
		public AtomicInstantAction(String name, String label, String tooltip, int priority) {
			super(name, label, tooltip);
			this.priority = priority;
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.InstantAction#getPriority()
		 */
		public int getPriority() {
			return this.priority;
		}
		
		/**
		 * This implementation wraps calling the <code>doExecuteAction()</code>
		 * in an atomic action, with the <code>doExecuteAction()</code> method
		 * responsible for actually providing the functionality of the action.
		 * @see de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.InstantAction#executeAction(de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel)
		 */
		public final boolean executeAction(XmDocumentMarkupPanel invoker) {
			boolean handleAtomicAction = !invoker.isAtomicActionRunning();
			try {
				if (handleAtomicAction)
					invoker.beginAtomicAction(this.label);
				return this.doExecuteAction(invoker);
			}
			finally {
				if (handleAtomicAction)
					invoker.endAtomicAction();
			}
		}
		
		/**
		 * Perform the actual action, wrapped into an atomic action by the
		 * <code>executeAction()</code> method.
		 * @param invoker the XML document markup panel for the action to act
		 *        upon
		 * @return true if the action was executed
		 */
		protected abstract boolean doExecuteAction(XmDocumentMarkupPanel invoker);
	}
	
	/**
	 * Retrieve the available instantaneous actions for keyboard input in the
	 * presence of a token selection. The argument editor panel is to provide
	 * the current configuration of the editing interface.
	 * @param ts the token selection to retrieve the actions for
	 * @param ke the keyboard event describing details of input
	 * @param xdmp the document editor panel to display the actions in
	 * @return an array holding the actions
	 */
	public abstract InstantAction[] getKeystrokeActions(TokenSelection ts, KeyEvent ke, XmDocumentMarkupPanel xdmp, DocumentDisplay display);
	
	/**
	 * Retrieve the available instantaneous actions for keyboard input in the
	 * presence of an annotation tag selection. The argument editor panel is to
	 * provide the current configuration of the editing interface.
	 * @param ts the annotation tag selection to retrieve the actions for
	 * @param ke the keyboard event describing details of input
	 * @param xdmp the document editor panel to display the actions in
	 * @param display the document display the argument editor panel is a part
	 *            of (for a wider context, may be null)
	 * @return an array holding the actions
	 */
	public abstract InstantAction[] getKeystrokeActions(TagSelection ts, KeyEvent ke, XmDocumentMarkupPanel xdmp, DocumentDisplay display);
	
	/**
	 * Retrieve the available actions for a given keyboard input in some spot
	 * in an XML document markup panel. The argument editor panel is to provide
	 * the current configuration of the editing interface.
	 * @param leftPs a point selection describing the contents to the left of
	 *            the cursor position the keyboard input occurred at
	 * @param rightPs a point selection describing the contents to the right of
	 *            the cursor position the keyboard input occurred at
	 * @param ke the keyboard event describing details of input
	 * @param xdmp the document editor panel to use the actions in
	 * @param display the document display the argument editor panel is a part
	 *            of (for a wider context, may be null)
	 * @return an array holding the actions
	 */
	public abstract InstantAction[] getKeystrokeActions(PointSelection leftPs, PointSelection rightPs, KeyEvent ke, XmDocumentMarkupPanel xdmp, DocumentDisplay display);
	
	/**
	 * Retrieve descriptors for all the keystroke actions offered by the
	 * provider. This is both for conflict checking and for compiling an
	 * overview for users.
	 * @param display the document display the argument editor panel is a part
	 *            of (for a wider context, may be null)
	 * @return and array holding the keystroke action descriptors
	 */
	public abstract KeystrokeActionDescriptor[] getKeystrokeActionDescriptors();
}
