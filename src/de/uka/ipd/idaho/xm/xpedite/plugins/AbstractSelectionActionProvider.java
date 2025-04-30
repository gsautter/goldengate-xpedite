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
 * Convenience abstract super class for providers of selection actions,
 * inheriting the life cycle methods from abstract GoldenGATE plugin. This
 * class further includes default implementations of the action getter methods,
 * sub classes need to overwrite either one or both to provide any meaningful
 * functionality.
 * 
 * @author sautter
 */
public abstract class AbstractSelectionActionProvider extends AbstractGoldenGateXpeditePlugin implements SelectionActionProvider {
	
	/** we need a zero-argument constructor for class loading */
	protected AbstractSelectionActionProvider() {}
	
	/**
	 * This default implementation simply returns null; sub classes are welcome
	 * to overwrite it as needed.
	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.SelectionActionProvider#getActions(de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.TokenSelection, de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel, de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay)
	 */
	public SelectionAction[] getActions(TokenSelection ts, XmDocumentMarkupPanel xdmp, DocumentDisplay display) {
		return null;
	}
	
	/**
	 * This default implementation simply returns null; sub classes are welcome
	 * to overwrite it as needed.
	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.SelectionActionProvider#getActions(de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.TagSelection, de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel, de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay)
	 */
	public SelectionAction[] getActions(TagSelection ts, XmDocumentMarkupPanel xdmp, DocumentDisplay display) {
		return null;
	}
	
	/**
	 * This default implementation simply returns null; sub classes are welcome
	 * to overwrite it as needed.
	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.SelectionActionProvider#getActions(de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.PointSelection, java.awt.event.MouseEvent, de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel, de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay)
	 */
	public SelectionAction[] getActions(PointSelection ps, MouseEvent me, XmDocumentMarkupPanel xdmp, DocumentDisplay display) {
		return null;
	}
}