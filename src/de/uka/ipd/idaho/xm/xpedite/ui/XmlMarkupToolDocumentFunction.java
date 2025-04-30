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
package de.uka.ipd.idaho.xm.xpedite.ui;

import de.uka.ipd.idaho.gamta.Annotation;
import de.uka.ipd.idaho.goldenGate.plugins.GoldenGatePlugin;
import de.uka.ipd.idaho.goldenGate.ui.DocumentFunction;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay;
import de.uka.ipd.idaho.xm.XmDocument;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.XmlMarkupTool;

/**
 * Document function wrapper for XML markup tools to apply to a XML markup
 * document displaying in a XML document display.
 * 
 * @author sautter
 */
public class XmlMarkupToolDocumentFunction extends DocumentFunction {
	private XmlMarkupTool xmt;
	public XmlMarkupToolDocumentFunction(GoldenGatePlugin owner, String name, XmlMarkupTool xmt, int flags) {
		super(owner, name, xmt.getLabel(), xmt.getTooltip(), flags);
		this.xmt = xmt;
	}
	public boolean isApplicableTo(Class docClass) {
		return XmDocument.class.isAssignableFrom(docClass);
	}
	public boolean isApplicableTo(GoldenGateUI ggui, DocumentDisplay display, Annotation selection) {
		return (super.isApplicableTo(ggui, display, selection) && (display instanceof XmlDocumentDisplay) && (selection == null) && display.areAnnotationsEditable());
	}
	public void applyTo(GoldenGateUI ggui, DocumentDisplay display, Annotation selection) {
		if (display instanceof XmlDocumentDisplay)
			((XmlDocumentDisplay) display).getXmDocumentPanel().applyMarkupTool(this.xmt, null);
	}
}
