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
package de.uka.ipd.idaho.xm.xpedite.swing;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import de.uka.ipd.idaho.gamta.util.constants.LiteratureConstants;
import de.uka.ipd.idaho.goldenGate.GoldenGateConstants;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuElement;
import de.uka.ipd.idaho.goldenGate.util.DialogPanel;
import de.uka.ipd.idaho.xm.XmDocument;
import de.uka.ipd.idaho.xm.xpedite.GoldenGateXpedite;
import de.uka.ipd.idaho.xm.xpedite.swing.XmlDocumentMarkupUI.XmlDocumentEditorTab;

/**
 * Dialog for displaying and editing a single XML Markup documents, for use
 * in the UI of an application built around a GoldenGATE Xpedite core. This
 * class displays the document, a main menu (including export functionality),
 * and view control. Document IO is up to sub classes, as is adding any button
 * panel if required.<br/>
 * By default, the dialog only contains the document markup UI in the
 * <code>BorderLayout.CENTER</code> position. Client code, mainly sub classes,
 * may add other components around it if required.
 * 
 * @author sautter
 */
public abstract class XmlDocumentMarkupDialog extends DialogPanel implements LiteratureConstants, GoldenGateConstants {
	private DialogDoumentMarkupUI ui;
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param doc the document to display
	 * @param docName the name of the document to display
	 */
//	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, Settings ggxConfig, XmDocument doc, String docName) {
	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, XmDocument doc, String docName) {
//		this(ggXpedite, ggxConfig, doc, docName, false);
		this(ggXpedite, doc, docName, false);
	}
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param doc the document to display
	 * @param docName the name of the document to display
	 * @param isSubDocument is the document a sub document of another one (setting to true hides 'File' and 'Export' menu)
	 */
//	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, Settings ggxConfig, XmDocument doc, String docName, boolean isSubDocument) {
	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, XmDocument doc, String docName, boolean isSubDocument) {
		super("GoldenGATE Xpedite - " + docName);
//		this.init(ggXpedite, new DialogDoumentMarkupUI(ggXpedite, ggxConfig, doc, docName, isSubDocument));
		this.init(ggXpedite, new DialogDoumentMarkupUI(ggXpedite, doc, docName, isSubDocument));
	}
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param docTag the document tab to display
	 */
//	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, Settings ggxConfig, XmlDocumentEditorTab docTab) {
	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, XmlDocumentEditorTab docTab) {
//		this(ggXpedite, ggxConfig, docTab, false);
		this(ggXpedite, docTab, false);
	}
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param docTag the document tab to display
	 * @param isSubDocument is the document a sub document of another one (setting to true hides 'File' and 'Export' menu)
	 */
//	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, Settings ggxConfig, XmlDocumentEditorTab docTab, boolean isSubDocument) {
	protected XmlDocumentMarkupDialog(GoldenGateXpedite ggXpedite, XmlDocumentEditorTab docTab, boolean isSubDocument) {
		super("GoldenGATE Xpedite - " + docTab.getDocName());
//		this.init(ggXpedite, new DialogDoumentMarkupUI(ggXpedite, ggxConfig, docTab, isSubDocument));
		this.init(ggXpedite, new DialogDoumentMarkupUI(ggXpedite, docTab, isSubDocument));
	}
	
	private void init(GoldenGateXpedite ggXpedite, DialogDoumentMarkupUI ui) {
		
		//	use UI in single-document mode
		this.ui = ui;
		
		//	set window icon
		this.getDialog().setIconImage(ggXpedite.getGoldenGateIcon());
		
		//	make sure we exit on window closing
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent we) {
				close();
			}
			public void windowClosing(WindowEvent we) {
				close();
			}
		});
		
		//	assemble major parts
		this.setLayout(new BorderLayout());
		this.add(this.ui, BorderLayout.CENTER);
		this.setSize(1000, 800);
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * Retrieve the enclosed markup UI. This method intentionally returns a
	 * generic JComponent because the only reason sub classes are intended to
	 * access the markup UI is for dialog content layout purposes.
	 * @return the markup UI component
	 */
	protected JComponent getMarkupUI() {
		return this.ui;
	}
	
	void close() {
		if (!this.ui.close())
			return;
		this.dispose();
	}
	
	private class DialogDoumentMarkupUI extends XmlDocumentMarkupUI {
//		DialogDoumentMarkupUI(GoldenGateXpedite ggXpedite, Settings ggxConfig, XmDocument doc, String docName, boolean isSubDocument) {
		DialogDoumentMarkupUI(GoldenGateXpedite ggXpedite, XmDocument doc, String docName, boolean isSubDocument) {
//			super(ggXpedite, ggxConfig, doc, docName, isSubDocument);
			super(ggXpedite, doc, docName, isSubDocument);
		}
//		DialogDoumentMarkupUI(GoldenGateXpedite ggXpedite, Settings ggxConfig, XmlDocumentEditorTab docTab, boolean isSubDocument) {
		DialogDoumentMarkupUI(GoldenGateXpedite ggXpedite, XmlDocumentEditorTab docTab, boolean isSubDocument) {
//			super(ggXpedite, ggxConfig, docTab, isSubDocument);
			super(ggXpedite, docTab, isSubDocument);
		}
//		protected FileMenuItem[] getFileMenuItems() {
//			return XmlDocumentMarkupDialog.this.getFileMenuItems();
//		}
		protected WindowMenuElement[] getFileMenuElements() {
			return XmlDocumentMarkupDialog.this.getFileMenuElements();
		}
		protected void documentNameChanged(XmlDocumentEditorTab xdet) {
			super.documentNameChanged(xdet);
			XmlDocumentMarkupDialog.this.setTitle("GoldenGATE Xpedite - " + xdet.getDocName());
		}
		protected boolean saveDocument(XmlDocumentEditorTab xdet) {
			return XmlDocumentMarkupDialog.this.saveDocument(xdet);
		}
		public boolean closeDocument(XmlDocumentEditorTab xdet) {
			if (!super.closeDocument(xdet))
				return false;
			XmlDocumentMarkupDialog.this.close();
			return true;
		}
		protected Window getMainWindow() {
			return XmlDocumentMarkupDialog.this.getDialog();
		}
	}
	
	/**
	 * Retrieve the embedded markup UI.
	 * @return the embedded markup UI
	 */
	public XmlDocumentMarkupUI getMarkupUi() {
		return this.ui;
	}
	
	/**
	 * Provide custom options for the 'File' menu. By default, the 'File' menu
	 * only contains two options, namely "Save Document" and "Close Document".
	 * They delegate to the respective methods of this class with the selected
	 * document tab as the argument. This default implementation returns an
	 * empty array, sub classes are welcome to overwrite it as needed.
	 * @return an array holding the menu elements
	 */
//	protected FileMenuItem[] getFileMenuItems() {
//		return new FileMenuItem[0];
//	}
	protected WindowMenuElement[] getFileMenuElements() {
		return new WindowMenuElement[0];
	}
	
	/**
	 * Save the contents of a document editor tab. Implementations may only
	 * return true if the content of the argument editor tab was actually saved
	 * to persistent storage. The runtime type of the argument editor tab is
	 * whatever sub classes hand to <code>openDocument()</code> or the
	 * constructor that takes a tab as an argument.
	 * @param xdet the document editor tab whose content to save
	 * @return true if the document was saved, false otherwise
	 */
	protected abstract boolean saveDocument(XmlDocumentEditorTab xdet);
}