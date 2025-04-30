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

import java.awt.event.MouseEvent;

import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.PointSelection;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.SelectionAction;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.TagSelection;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.TokenSelection;

/**
 * Provider of actions working on XML Markup documents, to dynamically extend
 * functionality.
 * 
 * @author sautter
 */
public interface SelectionActionProvider extends GoldenGateXpeditePlugin {
	
	/**
	 * Retrieve the available actions for a token selection. The argument
	 * editor panel is to provide the current configuration of the editing
	 * interface.
	 * @param ts the token selection to retrieve the actions for
	 * @param xdmp the document editor panel to display the actions in
	 * @param display the document display the argument editor panel is a part
	 *            of (for a wider context, may be null)
	 * @return an array holding the actions
	 */
	public abstract SelectionAction[] getActions(TokenSelection ts, XmDocumentMarkupPanel xdmp, DocumentDisplay display);
	
	/**
	 * Retrieve the available actions for an annotation tag selection. The
	 * argument editor panel is to provide the current configuration of the
	 * editing interface.
	 * @param ts the annotation tag selection to retrieve the actions for
	 * @param xdmp the document editor panel to display the actions in
	 * @param display the document display the argument editor panel is a part
	 *            of (for a wider context, may be null)
	 * @return an array holding the actions
	 */
	public abstract SelectionAction[] getActions(TagSelection ts, XmDocumentMarkupPanel xdmp, DocumentDisplay display);
	
	/**
	 * Retrieve the available actions for a click selection. The type of the
	 * argument selection provides the information where the click occurred,
	 * as well as further details. The argument mouse event can be null on
	 * keyboard triggered 'right clicks' to show the context menu. For actual
	 * mouse clicks, it provides details like the number of clicks as well as
	 * any associated modifier keys. The argument editor panel is to provide
	 * the current configuration of the editing interface.
	 * @param ps the click based point selection to retrieve the actions for
	 * @param me the mouse event providing details on the click
	 * @param xdmp the document editor panel to display the actions in
	 * @param display the document display the argument editor panel is a part
	 *            of (for a wider context, may be null)
	 * @return an array holding the actions
	 */
	public abstract SelectionAction[] getActions(PointSelection ps, MouseEvent me, XmDocumentMarkupPanel xdmp, DocumentDisplay display);
}