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
import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import de.uka.ipd.idaho.gamta.Annotation;
import de.uka.ipd.idaho.gamta.AnnotationUtils;
import de.uka.ipd.idaho.gamta.QueriableAnnotation;
import de.uka.ipd.idaho.gamta.util.GenericGamtaXML;
import de.uka.ipd.idaho.gamta.util.ProgressMonitor;
import de.uka.ipd.idaho.gamta.util.constants.LiteratureConstants;
import de.uka.ipd.idaho.gamta.util.swing.DialogFactory;
import de.uka.ipd.idaho.goldenGate.GoldenGATE;
import de.uka.ipd.idaho.goldenGate.GoldenGateConstants;
import de.uka.ipd.idaho.goldenGate.plugins.DocumentViewProvider;
import de.uka.ipd.idaho.goldenGate.plugins.ResourceSplashScreen;
import de.uka.ipd.idaho.goldenGate.ui.DialogMainButton;
import de.uka.ipd.idaho.goldenGate.ui.DocumentView.DocumentViewAugmenter;
import de.uka.ipd.idaho.goldenGate.ui.DocumentViewAdapter;
import de.uka.ipd.idaho.goldenGate.ui.DocumentViewDisplayService;
import de.uka.ipd.idaho.goldenGate.ui.DocumentViewDisplayService.DesktopDocumentViewDisplayService;
import de.uka.ipd.idaho.goldenGate.ui.DocumentViewInstance;
import de.uka.ipd.idaho.goldenGate.ui.DynamicWindowMenu;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI;
import de.uka.ipd.idaho.goldenGate.ui.UserInterfaceUtils;
import de.uka.ipd.idaho.goldenGate.ui.UserInterfaceUtils.LayoutMenu;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenu;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuBar;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuCheckBox;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuElement;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuFunction;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuOwner;
import de.uka.ipd.idaho.goldenGate.util.ResourceListener;
import de.uka.ipd.idaho.xm.XmAnnotation;
import de.uka.ipd.idaho.xm.XmDocument;
import de.uka.ipd.idaho.xm.XmObject;
import de.uka.ipd.idaho.xm.gamta.LazyMutableAnnotation;
import de.uka.ipd.idaho.xm.gamta.XmDocumentRoot;
import de.uka.ipd.idaho.xm.gamta.XmDocumentRootOptionPanel;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.AtomicActionListener;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.XmlMarkupTool;
import de.uka.ipd.idaho.xm.xpedite.GoldenGateXpedite;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentExporter;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentFileExporter;
import de.uka.ipd.idaho.xm.xpedite.ui.XmlDocumentViewInstance;
import de.uka.ipd.idaho.xm.xpedite.ui.XmlUserInterfaceUtils;
//import de.uka.ipd.idaho.goldenGate.editor.plugins.DocumentExporter;
//import de.uka.ipd.idaho.goldenGate.editor.plugins.DocumentFileExporter;
//import de.uka.ipd.idaho.goldenGate.editor.swing.DocumentMarkupUI.DocumentEditorTab;
//import de.uka.ipd.idaho.goldenGate.editor.plugins.DocumentExporter;
//import de.uka.ipd.idaho.goldenGate.editor.plugins.DocumentFileExporter;
//import de.uka.ipd.idaho.goldenGate.editor.swing.DocumentMarkupUI.DocumentEditorTab;

/**
 * UI for displaying and editing one or multiple XML Markup documents, for
 * use in the UI of an application built around a GoldenGATE Xpedite core. This
 * class provides multi-document capability, a main menu (including export
 * functionality), and view control. Document IO is up to sub classes.<br/>
 * By default, the UI panel contains the document markup panel (plain or in
 * tabs) and the main menu with view control in the <code>BorderLayout.CENTER</code>
 * and <code>BorderLayout.NORTH</code> positions, respectively. Client code,
 * mainly sub classes, may add other components around them if required.
 * 
 * @author sautter
 */
public abstract class XmlDocumentMarkupUI extends JPanel implements LiteratureConstants, GoldenGateConstants, GoldenGateUI, WindowFocusListener {
	final GoldenGATE goldenGate;
	final GoldenGateXpedite ggXpedite;
	final boolean ggxInMasterConfiguration;
	
	final WindowMenuOwner menuOwner;
	private WindowMenuBar mainMenu;
//	private JMenuBar mainMenu = new JMenuBar();
//	private ArrayList editMenuItemNames;
//	private JMenu editMenu;
//	final JMenu undoMenu = new JMenu("Undo");
	final DynamicWindowMenu undoMenu;
//	final JCheckBoxMenuItem allowReactionPrompts = new JCheckBoxMenuItem("Prompt in Reaction to Input");
//	final JCheckBoxMenuItem showContextMenuOnMouseRelese = new JCheckBoxMenuItem("Context Menu on Mouse Release", true);
	final WindowMenuCheckBox allowReactionPrompts;
	final WindowMenuCheckBox showExpandedContextMenu;
	final WindowMenuCheckBox showContextMenuOnMouseRelese;
//	private ArrayList toolsMenuItemNames;
//	private JMenu toolsMenu;
//	private int xmlWrapperFlags = XmDocumentRoot.NORMALIZE_CHARACTERS;
//	private LinkedHashSet documentDependentMenuItems = new LinkedHashSet();
	final WindowMenuFunction editDisplayConfig;
	final WindowMenuFunction resetDisplayConfig;
	final WindowMenuFunction storeDisplayConfig;
	final WindowMenuFunction storeAnnotDisplayModes;
	final WindowMenuFunction editXmlWrapperFlags;
	final WindowMenuFunction clearXmlWrappers;
//	
//	private GoldenGatePluginDataProvider helpDataProvider;
//	private HelpChapter helpContent;
//	private Help help;
//	private JMenu helpMenu;
	
	final DocumentViewDisplayService viewDisplayService;
	
	private boolean performDocumentIO;
	final JFileChooser fileChooser = new JFileChooser();
	
//	final ViewControl viewControl = new ViewControl();
	private JTabbedPane docTabs = null;
	private XmlDocumentEditorTab docTab = null;
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param doc the document to display (null activates multi-document mode)
	 * @param docName the name of the document to display
	 */
	protected XmlDocumentMarkupUI(GoldenGateXpedite ggXpedite, XmDocument doc, String docName) {
		this(ggXpedite, doc, docName, false);
	}
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param doc the document to display (null activates multi-document mode)
	 * @param docName the name of the document to display
	 * @param isSubDocument is the document a sub document of another one (setting to true hides 'File' and 'Export' menu)
	 */
	protected XmlDocumentMarkupUI(GoldenGateXpedite ggXpedite, XmDocument doc, String docName, boolean isSubDocument) {
		super(new BorderLayout(), true);
		this.goldenGate = ggXpedite.getGoldenGATE();
		this.ggXpedite = ggXpedite;
		this.ggxInMasterConfiguration = this.goldenGate.getConfiguration().isMasterConfiguration();
		
		//	initialize fixed parts of main menu
		this.menuOwner = new WindowMenuOwner(this) {
			public WindowMenuElement[] getMenuElements() {
				return getWindowMenuElements();
			}
		};
		int dwmFlags = 0;
		dwmFlags |= WindowMenuElement.PROPERTY_AVAILABLE_BOTH;
		dwmFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		this.undoMenu = new DynamicWindowMenu("ggXpedite", WindowMenuBar.UNDO_MENU_NAME, "Undo", "Revert latest modifications made to the document", dwmFlags, this.menuOwner, true);
		
		int wmeFlags = 0;
		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_BOTH;
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		wmeFlags = WindowMenu.encodePreferredMenuName(WindowMenuBar.EDIT_MENU_NAME, wmeFlags);
		this.allowReactionPrompts = new WindowMenuCheckBox("ggXpedite", "allowReactionPrompt", "Prompt in Reaction to Input", "Allow opening input dialogs in reaction to basic operations?", wmeFlags, true);
		this.showContextMenuOnMouseRelese = new WindowMenuCheckBox("ggXpedite", "instantContextMenu", "Context Menu on Mouse Release", "Show context menu immediately when mouse selection completed?", wmeFlags, true) {
			public void setSelected(boolean selected) {
				super.setSelected(selected);
				XmlDocumentEditorTab xdet = getActiveDocument();
				if (xdet != null)
					xdet.xdmp.setShowContextMenuOnMouseRelease(this.isSelected());
			}
		};
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
		this.showExpandedContextMenu = new WindowMenuCheckBox("ggXpedite", "expandContextMenu", "Show Expanded Context Menu", "Show context menu in expanded ytle (takes more space, but might be easier to read)?", wmeFlags, UserInterfaceUtils.stylesExpandedContextMenu()) {
			public void setSelected(boolean selected) {
				super.setSelected(selected);
				UserInterfaceUtils.setStyleExpandedContextMenu(selected);
			}
		};
		
		wmeFlags = 0;
		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		wmeFlags = WindowMenu.encodePreferredMenuName(WindowMenuBar.VIEW_MENU_NAME, wmeFlags);
		this.editDisplayConfig = new WindowMenuFunction("ggXpedite", "editDisplayConfig", "Configure Display", "Configure the XML display, i.e., fonts and colors of text and background", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to configure
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				editDisplayConfig();
			}
		};
		this.resetDisplayConfig = new WindowMenuFunction("ggXpedite", "resetDisplayConfig", "Reset Display", "Reset the configuration of the XML display to the default state", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to reset
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				resetDisplayConfig();
			}
		};
		this.storeDisplayConfig = new WindowMenuFunction("ggXpedite", "storeDisplayConfig", "Set Display Defaults", "Set the current configuration of the XML display as the default", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take config from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				storeDisplayConfig();
			}
		};
		this.storeAnnotDisplayModes = new WindowMenuFunction("ggXpedite", "storeAnnotDisplayModes", "Store Annotation Display Modes", "Store the display modes of currently visible annotations as the defaults", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take modes from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				storeAnnotDisplayModes();
			}
		};
		this.editXmlWrapperFlags = new WindowMenuFunction("ggXpedite", "editXmlWrapperFlags", "Edit Generic XML Flags", "Configure how the current document converts into generic XML", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take modes from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				editXmlWrapperFlags();
			}
		};
		this.clearXmlWrappers = new WindowMenuFunction("ggXpedite", "clearXmlWrappes", "Clear Generic XML Views", "Clear the generic XML views of all currently open documents, e.g. to free up memory", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take modes from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				clearXmlWrappers();
			}
		};
		
		//	create document view display service
		this.viewDisplayService = new XmlDocumentViewDisplayService(this);
		
		//	finalize setup
		this.performDocumentIO = ((doc == null) || !isSubDocument);
		this.init((doc == null) ? null : new XmlDocumentEditorTab(this, doc, docName));
	}
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param docTab the document tab to display (null activates multi-document mode)
	 */
	protected XmlDocumentMarkupUI(GoldenGateXpedite ggXpedite, XmlDocumentEditorTab docTab) {
		this(ggXpedite, docTab, false);
	}
	
	/** Constructor
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 * @param docTab the document tab to display (null activates multi-document mode)
	 * @param isSubDocument is the document a sub document of another one (setting to true hides 'File' and 'Export' menu)
	 */
	protected XmlDocumentMarkupUI(GoldenGateXpedite ggXpedite, XmlDocumentEditorTab docTab, boolean isSubDocument) {
		super(new BorderLayout(), true);
		this.goldenGate = ggXpedite.getGoldenGATE();
		this.ggXpedite = ggXpedite;
		this.ggxInMasterConfiguration = this.goldenGate.getConfiguration().isMasterConfiguration();
		
		//	initialize fixed parts of main menu
		this.menuOwner = new WindowMenuOwner(this) {
			public WindowMenuElement[] getMenuElements() {
				return getWindowMenuElements();
			}
		};
		int dwmFlags = 0;
		dwmFlags |= WindowMenuElement.PROPERTY_AVAILABLE_BOTH;
		dwmFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		this.undoMenu = new DynamicWindowMenu("ggXpedite", WindowMenuBar.UNDO_MENU_NAME, "Undo", "Revert latest modifications made to the document", dwmFlags, this.menuOwner, true);
		
		int wmeFlags = 0;
		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_BOTH;
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		wmeFlags = WindowMenu.encodePreferredMenuName(WindowMenuBar.EDIT_MENU_NAME, wmeFlags);
		this.allowReactionPrompts = new WindowMenuCheckBox("ggXpedite", "allowReactionPrompt", "Prompt in Reaction to Input", "Allow opening input dialogs in reaction to basic operations?", wmeFlags, true);
		this.showContextMenuOnMouseRelese = new WindowMenuCheckBox("ggXpedite", "instantContextMenu", "Context Menu on Mouse Release", "Show context menu immediately when mouse selection completed?", wmeFlags, true) {
			public void setSelected(boolean selected) {
				super.setSelected(selected);
				XmlDocumentEditorTab xdet = getActiveDocument();
				if (xdet != null)
					xdet.xdmp.setShowContextMenuOnMouseRelease(this.isSelected());
			}
		};
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
		this.showExpandedContextMenu = new WindowMenuCheckBox("ggXpedite", "expandContextMenu", "Show Expanded Context Menu", "Show context menu in expanded ytle (takes more space, but might be easier to read)?", wmeFlags, UserInterfaceUtils.stylesExpandedContextMenu()) {
			public void setSelected(boolean selected) {
				super.setSelected(selected);
				UserInterfaceUtils.setStyleExpandedContextMenu(selected);
			}
		};
		
		wmeFlags = 0;
		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		wmeFlags = WindowMenu.encodePreferredMenuName(WindowMenuBar.VIEW_MENU_NAME, wmeFlags);
		this.editDisplayConfig = new WindowMenuFunction("ggXpedite", "editDisplayConfig", "Configure Display", "Configure the XML display, i.e., fonts and colors of text and background", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to configure
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				editDisplayConfig();
			}
		};
		this.resetDisplayConfig = new WindowMenuFunction("ggXpedite", "resetDisplayConfig", "Reset Display", "Reset the configuration of the XML display to the default state", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to reset
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				resetDisplayConfig();
			}
		};
		this.storeDisplayConfig = new WindowMenuFunction("ggXpedite", "storeDisplayConfig", "Set Display Defaults", "Set the current configuration of the XML display as the default", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take config from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				storeDisplayConfig();
			}
		};
		this.storeAnnotDisplayModes = new WindowMenuFunction("ggXpedite", "storeAnnotDisplayModes", "Store Annotation Display Modes", "Store the display modes of currently visible annotations as the defaults", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take modes from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				storeAnnotDisplayModes();
			}
		};
		this.editXmlWrapperFlags = new WindowMenuFunction("ggXpedite", "editXmlWrapperFlags", "Edit Generic XML Flags", "Configure how the current document converts into generic XML", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take modes from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				editXmlWrapperFlags();
			}
		};
		this.clearXmlWrappers = new WindowMenuFunction("ggXpedite", "clearXmlWrappes", "Clear Generic XML Views", "Clear the generic XML views of all currently open documents, e.g. to free up memory", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display != null); // we do need a display to take modes from
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				clearXmlWrappers();
			}
		};
		
		//	create document view display service
		this.viewDisplayService = new XmlDocumentViewDisplayService(this);
		
		//	finalize setup
		if (docTab != null)
			docTab.setParent(this);
		this.performDocumentIO = ((docTab == null) || !isSubDocument);
		this.init(docTab);
	}
	
	private static class XmlDocumentViewDisplayService extends DesktopDocumentViewDisplayService {
		private XmlDocumentMarkupUI parent;
		XmlDocumentViewDisplayService(XmlDocumentMarkupUI parent) {
			super(parent.goldenGate);
			this.parent = parent;
		}
		protected DocumentViewAdapter getRootDocumentViewAdapter(DocumentDisplay display, final DocumentViewInstance viewInstance) {
			final XmlDocumentEditorTab xdet;
			int dvaModeFlags = 0;
			if (display == this.parent.getActiveDocument()) {
				xdet = ((XmlDocumentEditorTab) display);
				if (viewInstance.areAnotationsEditable() && xdet.areAnnotationsEditable())
					dvaModeFlags |= DocumentViewInstance.MODE_ANNOTATIONS_EDITABLE;
//				if (viewInstance.areTokensEditable() && xdet.areTokensEditable())
//					dvaModeFlags |= DocumentViewInstance.MODE_TOKENS_EDITABLE; // no editing tokens in GAMTA based views
				if (viewInstance.isDirectWriteThrough())
					dvaModeFlags |= DocumentViewInstance.MODE_DIRECT_WRITE_THROUGH;
			}
			else return null;
			
			//	set up wrapper for full document or annotation
			final XmAnnotation scopeAnnot;
			final int scopeDocFlags;
//			final XmDocumentRoot[] scopeDoc = {null};
//			final MutableAnnotation[] scopeDoc = {null};
			final LazyMutableAnnotation scopeDoc;
			if (viewInstance instanceof XmlDocumentViewInstance) {
				XmlDocumentViewInstance xmlViewInstance = ((XmlDocumentViewInstance) viewInstance);
				if (xmlViewInstance.xmScope == null)
					scopeAnnot = null;
				else if (xmlViewInstance.xmScope.getDocument() == null) // detached, add temporary view scope annotation to document
					scopeAnnot = xdet.xdmp.document.addAnnotation(xmlViewInstance.xmScope.getFirstToken(), xmlViewInstance.xmScope.getLastToken(), VIEW_SCOPE_DOCUMENT_ANNOTATION_TYPE);
				else scopeAnnot = xmlViewInstance.xmScope;
//				if (xmlViewInstance.xmlWrapperFlags == -1)
//					scopeDocFlags = xdet.getXmlWrapperFlags();
//				else scopeDocFlags = xmlViewInstance.xmlWrapperFlags;
				scopeDocFlags = xmlViewInstance.xmlWrapperFlags;
			}
			else {
				if (viewInstance.scope == null)
					scopeAnnot = null;
				else {
					XmObject scopeObj = xdet.xdmp.document.getObjectByUUID(viewInstance.scope.getAnnotationID());
					scopeAnnot = ((scopeObj instanceof XmAnnotation) ? ((XmAnnotation) scopeObj) : null);
				}
//				scopeDocFlags = xdet.getXmlWrapperFlags(); // use current flag if nothing else specified
				scopeDocFlags = -1;
			}
//			scopeDoc[0] = ((scopeAnnot == null) ? new XmDocumentRoot(xdet.xdmp.document, scopeDocFlags) : new XmDocumentRoot(scopeAnnot, scopeDocFlags));
//			scopeDoc[0] = ((scopeAnnot == null) ? xdet.getDocumentMutable() : new XmDocumentRoot(scopeAnnot, scopeDocFlags));
			if (scopeAnnot != null)
				scopeDoc = new LazyMutableAnnotation(scopeAnnot, ((scopeDocFlags == -1) ? xdet.getXmlWrapperFlags() : scopeDocFlags));
			else if (scopeDocFlags == -1)
				scopeDoc = xdet.getXmlWrapper(-1); // this will adapt to change of settings in main window in non-modal mode
			else scopeDoc = new LazyMutableAnnotation(xdet.getXmDocument(), scopeDocFlags); // use own wrapper with fixed flags otherwise
//			if (scopeDoc[0] == null)
//				return null;
			final DocumentViewAdapter docAdapter;
			
			//	set up test document (non need for wiring that one up at all, as underlying document remains untouched)
			if (viewInstance.isTestInstance()) {
				dvaModeFlags = DocumentViewInstance.MODE_TEST_DOCUMENT; // implies annotations and tokens editable, as well as modal
				docAdapter = new DocumentViewAdapter(scopeDoc, dvaModeFlags) {
					public void dispose() {
						super.dispose();
						if ((scopeAnnot != null) && (VIEW_SCOPE_DOCUMENT_ANNOTATION_TYPE.equals(scopeAnnot.getType())))
							xdet.xdmp.document.removeAnnotation(scopeAnnot);
					}
				};
			}
			
			//	set up direct write-through, with slide-of-hands updates for changes in parent adapter
			else if (viewInstance.isDirectWriteThrough()) {
				final AtomicActionListener[] docAdapterUpdater = {null};
				final boolean[] dvaAtomicActionActive = {false};
//				docAdapter = new DocumentViewAdapter(scopeDoc[0], dvaModeFlags) {
				docAdapter = new DocumentViewAdapter(scopeDoc, dvaModeFlags) {
					public void startModification(String label, long id) {
						if (id < 1)
							id = System.currentTimeMillis();
						dvaAtomicActionActive[0] = true;
						super.startModification(label, id); // need to do this in case there is listeners
						//	TODO loop in annotation view document display method signature !!!
						//	==> we cannot know that, this is coming from the view proper !!!
//						xdet.xdmp.startAtomicAction(id, label, null, ((viewInstance.scope == null) ? null : scopeDoc[0]), ProgressMonitor.dummy /* TODO do we need some splash screen ??? */);
						xdet.xdmp.startAtomicAction(id, label, null, scopeAnnot, ProgressMonitor.dummy /* TODO do we need some splash screen ??? */);
					}
					public DocumentModification finishModification() {
						xdet.xdmp.finishAtomicAction(ProgressMonitor.dummy /* TODO do we need some splash screen ??? */);
						DocumentModification dm = super.finishModification(); // need to do this in case there is listeners
						dvaAtomicActionActive[0] = false;
						return dm;
					}
					public void dispose() {
						super.dispose();
						xdet.xdmp.removeAtomicActionListener(docAdapterUpdater[0]); // no need to react to scope document removal anymore
						if ((scopeAnnot != null) && (VIEW_SCOPE_DOCUMENT_ANNOTATION_TYPE.equals(scopeAnnot.getType())))
							xdet.xdmp.document.removeAnnotation(scopeAnnot);
					}
				};
				docAdapterUpdater[0] = new AtomicActionListener() {
					public void atomicActionStarted(long id, String label, XmlMarkupTool imt, XmAnnotation annot, ProgressMonitor pm) { /* no need to start anything on view adapter, we get the updates via slide-of-hands */ }
					public void atomicActionFinishing(long id, ProgressMonitor pm) { /* we're not augmenting atomic actions */ }
					public void atomicActionFinished(long id, ProgressMonitor pm) {
						if (dvaAtomicActionActive[0])
							return;
						//	make changes in underlying document show in non-modal view
//						if (scopeAnnot == null) // full-document view
//							scopeDoc[0] = new XmDocumentRoot(xdet.xdmp.document, scopeDocFlags);
//							scopeDoc[0] = xdet.getDocumentMutable();
//						else if (scopeAnnot.getDocument() == null) // scope annotation proper has been detached, fall back on boundary tokens
//							scopeDoc[0] = new XmDocumentRoot(scopeAnnot.getFirstToken(), scopeAnnot.getLastToken(), scopeDocFlags);
//						else scopeDoc[0] = new XmDocumentRoot(scopeAnnot, scopeDocFlags); // scope annotation still good, reuse it
						if (scopeDocFlags == -1)
							scopeDoc.setFlags(xdet.getXmlWrapperFlags()); // make sure to pass through any changes to flags
						docAdapter.setSourceDocument(scopeDoc); // validates internally
					}
				};
				xdet.xdmp.addAtomicActionListener(docAdapterUpdater[0]);
			}
			
			//	set up working document based view adapter
//			else docAdapter = new DocumentViewAdapter(scopeDoc[0], dvaModeFlags) {
//			else docAdapter = new DocumentViewAdapter(scopeDoc, dvaModeFlags) {
			else docAdapter = new DocumentViewAdapter(scopeDoc, (dvaModeFlags | DocumentViewAdapter.MODE_AGGREGATE_ANNOTATION_WRITE_THROUGH)) {
				public void dispose() {
					super.dispose();
					if ((scopeAnnot != null) && (VIEW_SCOPE_DOCUMENT_ANNOTATION_TYPE.equals(scopeAnnot.getType())))
						xdet.xdmp.document.removeAnnotation(scopeAnnot);
				}
			};
			
			//	finally ...
			return docAdapter;
		}
	}
	
	private void init(XmlDocumentEditorTab docTab) {
		
		//	configure file chooser
		if (this.performDocumentIO) {
			this.fileChooser.setMultiSelectionEnabled(false);
			this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//			this.fileChooser.setSelectedFile(new File((this.ggxConfig.getSetting("lastDocFolder", (new File(".")).getAbsolutePath())), " ")); // we need this dummy file name so the folder is actually opened instead of being selected in its parent folder
			Object lastDocFolder = UserInterfaceUtils.getDisplayProperty("lastDocFolder");
			if (lastDocFolder instanceof String)
				this.fileChooser.setSelectedFile(new File(lastDocFolder.toString(), " ")); // we need this dummy file name so the folder is actually opened instead of being selected in its parent folder
			else this.fileChooser.setSelectedFile(new File((new File(".")).getAbsolutePath(), " ")); // we need this dummy file name so the folder is actually opened instead of being selected in its parent folder
		}
		
		//	read basic menu properties
		Object windowMenuStyle = UserInterfaceUtils.getDisplayProperty("main.windowMenuStyle");
		if (windowMenuStyle instanceof String)
			UserInterfaceUtils.setStyleExpandedWindowMenu("expanded".equals(windowMenuStyle));
		else UserInterfaceUtils.setDisplayProperty("main.windowMenuStyle", (UserInterfaceUtils.stylesExpandedWindowMenu() ? "expanded" : "classic"));
		Object contextMenuStyle = UserInterfaceUtils.getDisplayProperty("main.contextMenuStyle");
		if (contextMenuStyle instanceof String) {
			UserInterfaceUtils.setStyleExpandedContextMenu("expanded".equals(contextMenuStyle));
			this.showExpandedContextMenu.setSelected(UserInterfaceUtils.stylesExpandedContextMenu());
		}
		else UserInterfaceUtils.setDisplayProperty("main.contextMenuStyle", (UserInterfaceUtils.stylesExpandedContextMenu() ? "expanded" : "classic"));
		Object menuBorderWidth = UserInterfaceUtils.getDisplayProperty("main.menuBorderWidth");
		if (menuBorderWidth instanceof Number)
			UserInterfaceUtils.setMenuItemBorderWidth(((Number) menuBorderWidth).intValue());
		else UserInterfaceUtils.setDisplayProperty("main.menuBorderWidth", Integer.valueOf(UserInterfaceUtils.getMenuItemBorderWidth()));
		Object menuColorAlpha = UserInterfaceUtils.getDisplayProperty("main.menuColorAlpha");
		if (menuColorAlpha instanceof Number)
			UserInterfaceUtils.setMenuItemBackgroundAlpha(((Number) menuColorAlpha).intValue());
		else UserInterfaceUtils.setDisplayProperty("main.menuColorAlpha", Integer.valueOf(UserInterfaceUtils.getMenuItemBackgroundAlpha()));
		Object menuColorBackground = UserInterfaceUtils.getDisplayProperty("main.menuColorBackground");
		if (menuColorBackground instanceof Color)
			UserInterfaceUtils.setMenuItemBackgroundBaseColor((Color) menuColorBackground);
		else UserInterfaceUtils.setDisplayProperty("main.menuColorBackground", UserInterfaceUtils.getMenuItemBackgroundBaseColor());
//		
//		//	build help first, as entries in other menus have to link up to it
//		this.helpDataProvider = this.ggXpedite.getHelpDataProvider();
//		this.helpContent = this.buildHelpContentRoot();
//		this.helpMenu = this.createHelpMenu();
//		
//		//	read main menu layout settings
//		ArrayList fileMenuItemNames = new ArrayList();
//		ArrayList exportMenuItemNames = new ArrayList();
//		ArrayList editMenuItemNames = new ArrayList();
//		ArrayList toolsMenuItemNames = new ArrayList();
//		try {
//			ArrayList menuItemNames = null;
//			BufferedReader mlIn;
//			if (this.ggXpedite.getConfiguration().isDataAvailable("GgXpedite.menus.cnfg"))
//				mlIn = new BufferedReader(new InputStreamReader(this.ggXpedite.getConfiguration().getInputStream("GgXpedite.menus.cnfg"), "UTF-8"));
//			else mlIn = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./GgXpedite.menus.cnfg")), "UTF-8"));
//			for (String mll; (mll = mlIn.readLine()) != null;) {
//				mll = mll.trim();
//				if ((mll.length() == 0) || mll.startsWith("//"))
//					continue;
//				if ("FILE-MENU".equals(mll))
//					menuItemNames = fileMenuItemNames;
//				else if ("EXPORT-MENU".equals(mll))
//					menuItemNames = exportMenuItemNames;
//				else if ("EDIT-MENU".equals(mll))
//					menuItemNames = editMenuItemNames;
//				else if ("TOOLS-MENU".equals(mll))
//					menuItemNames = toolsMenuItemNames;
//				else if (menuItemNames != null)
//					menuItemNames.add(mll);
//			}
//			mlIn.close();
//		}
//		catch (IOException ioe) {
//			System.out.println("Error reading menu layout: " + ioe.getMessage());
//			ioe.printStackTrace(System.out);
//		}
		
		//	read main menu layout settings
		LayoutMenu[] layoutMenus;
		try {
//			BufferedReader lmBr;
//			if (this.ggXpedite.getConfiguration().isDataAvailable("GgEditor.menus.cnfg"))
//				lmBr = new BufferedReader(new InputStreamReader(this.ggXpedite.getConfiguration().getInputStream("GgEditor.menus.cnfg"), "UTF-8"));
//			else lmBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./GgEditor.menus.cnfg")), "UTF-8"));
			//	going for configuration provided menu layout makes little sense, as both come from same exact file in exporting GG installation
			BufferedReader lmBr = this.goldenGate.getApplicationConfigReader("GgXpedite.menus");
			layoutMenus = UserInterfaceUtils.loadLayoutMenu(lmBr, this.ggxInMasterConfiguration);
			lmBr.close();
		}
		catch (IOException ioe) {
			System.out.println("Error reading menu layout: " + ioe.getMessage());
			ioe.printStackTrace(System.out);
			layoutMenus = new LayoutMenu[0];
		}
//		
//		//	initially disable UNDO menu (need something to happen before that thing has any content)
//		this.undoMenu.setEnabled(false);
//		
//		//	build main menu
//		if (this.performDocumentIO) {
//			this.addFileMenu(fileMenuItemNames, this.documentDependentMenuItems);
//			this.addExportMenu(exportMenuItemNames, this.documentDependentMenuItems);
//		}
//		this.addEditMenu(editMenuItemNames, this.documentDependentMenuItems);
//		if (this.ggxInMasterConfiguration)
//			this.editMenuItemNames = editMenuItemNames;
//		this.addMenu(this.undoMenu);
//		this.addToolsMenu(toolsMenuItemNames, this.documentDependentMenuItems);
//		if (this.ggxInMasterConfiguration)
//			this.toolsMenuItemNames = toolsMenuItemNames;
		
		//	build main menu
		this.mainMenu = UserInterfaceUtils.createWindowMenu(layoutMenus, this.getWindowMenuElements(), true, this.menuOwner, "ggXpedite");
		System.out.println("Got menu bar with " + this.mainMenu.menuCount() + " menus");
		JMenuBar mainMenu = UserInterfaceUtils.createDesktopWindowMenu(this.mainMenu);
		System.out.println("Got Swing menu bar with " + mainMenu.getMenuCount() + " menus");
		this.menuOwner.updateMenu();
//		
//		//	make 'Plugins' menu available (Analyzer hot reload, etc.) ==> simplifies testing
//		if (this.ggxInMasterConfiguration) {
//			this.addPluginsMenu(this.documentDependentMenuItems);
//			Gamta.addTestDocumentProvider(new TestDocumentProvider() {
//				XmlDocumentEditorTab docTab = null;
//				QueriableAnnotation doc = null;
//				public QueriableAnnotation getTestDocument() {
//					XmlDocumentEditorTab xdet = getActiveDocument();
//					if (xdet == null)
//						return null;
//					if (xdet == this.docTab)
//						return this.doc;
//					this.docTab = xdet;
//					this.doc = new ReadOnlyDocument(new XmDocumentRoot(this.docTab.getMarkupPanel().document, xmlWrapperFlags), "Modifications are not allowed in test documents.");
//					return this.doc;
//				}
//			});
//		}
		
		//	keep 'Edit' and 'Tools' menus updated on edits
		if (this.ggxInMasterConfiguration) {
			this.goldenGate.registerResourceListener(new ResourceListener() {
				public void resourceUpdated(String resourceProviderClassName, String resourceName) {
//					checkUpdateXmlMarkupToolMenus(resourceProviderClassName);
					menuOwner.refreshMenu();
				}
				public void resourceDeleted(String resourceProviderClassName, String resourceName) {
//					checkUpdateXmlMarkupToolMenus(resourceProviderClassName);
					menuOwner.refreshMenu();
				}
			});
//			this.ggXpedite.addResourceObserver(new ResourceObserver() {
//				public void resourcesChanged(String resourceProviderClassName) {}
//				public void resourceUpdated(String resourceProviderClassName, String resourceName) {
//					checkUpdateXmlMarkupToolMenus(resourceProviderClassName);
//				}
//				public void resourceDeleted(String resourceProviderClassName, String resourceName) {
//					checkUpdateXmlMarkupToolMenus(resourceProviderClassName);
//				}
//			});
		}
//		
//		//	finish help
//		this.finishHelpMenu();
//		this.help = new Help("GoldenGATE Xpedite", this.helpContent, this.ggXpedite.getGoldenGateIcon());
		
		//	build menu panel
		JPanel menuPanel = new JPanel(new BorderLayout(), true);
//		menuPanel.add(this.mainMenu, BorderLayout.CENTER);
		menuPanel.add(mainMenu, BorderLayout.CENTER);
//		menuPanel.add(this.viewControl, BorderLayout.EAST);
		
		//	build drop target
		DropTarget dropTarget = new DropTarget(this, new DropTargetAdapter() {
			public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(dtde.getDropAction());
				handleDrop(dtde.getTransferable());
			}
		});
		dropTarget.setActive(true);
		
		//	prepare document display
		JComponent docComp;
		
		//	we are in multi-document mode, add tabs
		if (docTab == null) {
			this.docTabs = new JTabbedPane();
			docComp = this.docTabs;
			
			//	update UNDO menu and context menu control on tab changes, and notify listeners
			this.docTabs.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent ce) {
					XmlDocumentEditorTab xdet = getActiveDocument();
//					viewControl.update(xdet);
					if (xdet == null)
						showContextMenuOnMouseRelese.setSelected(false);
					else {
						showContextMenuOnMouseRelese.setSelected(xdet.xdmp.isShowingContextMenuOnMouseRelease());
						xdet.updateUndoMenu();
						XmlDocumentMarkupUI.this.ggXpedite.notifyDocumentSelected(xdet.getXmDocument());
					}
				}
			});
			
			//	set up context menu control
			this.showContextMenuOnMouseRelese.setSelected(false);
//			this.documentDependentMenuItems.add(this.showContextMenuOnMouseRelese);
//			this.showContextMenuOnMouseRelese.addItemListener(new ItemListener() {
//				public void itemStateChanged(ItemEvent ie) {
//					XmlDocumentEditorTab xdet = getActiveDocument();
//					if (xdet != null)
//						xdet.xdmp.setShowContextMenuOnMouseRelease(showContextMenuOnMouseRelese.isSelected());
//				}
//			});
//			
//			//	deactivate document dependent menu items initially (will be activated once document opened)
//			this.setDocumentDependentMenuItemsEnabled(false);
		}
		
		//	we're in single-document mode, show document right away
		else {
			this.docTab = docTab;
			docComp = this.docTab;
			this.showContextMenuOnMouseRelese.setSelected(this.docTab.xdmp.isShowingContextMenuOnMouseRelease());
//			this.showContextMenuOnMouseRelese.addItemListener(new ItemListener() {
//				public void itemStateChanged(ItemEvent ie) {
//					XmlDocumentMarkupUI.this.docTab.xdmp.setShowContextMenuOnMouseRelease(showContextMenuOnMouseRelese.isSelected());
//				}
//			});
		}
//		
//		//	make sure to focus document, not zoom control
//		this.setFocusTraversalPolicy(new FocusTraversalPolicy() {
//			public Component getComponentAfter(Container aContainer, Component aComponent) {
//				return viewControl;
//			}
//			public Component getComponentBefore(Container aContainer, Component aComponent) {
//				return viewControl;
//			}
//			public Component getFirstComponent(Container aContainer) {
//				return viewControl;
//			}
//			public Component getLastComponent(Container aContainer) {
//				return viewControl;
//			}
//			public Component getDefaultComponent(Container aContainer) {
//				return viewControl;
//			}
//		});
		
		//	make document views scroll on page-up and page-down
		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "docScrollUp", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				XmlDocumentEditorTab xdet = getActiveDocument();
				if (xdet != null)
					xdet.scrollUp();
			}
		});
		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "docScrollDown", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				XmlDocumentEditorTab xdet = getActiveDocument();
				if (xdet != null)
					xdet.scrollDown();
			}
		});
		
		//	trigger UNDO on Ctrl-Z
		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "docUndo", new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
//				if (undoMenu.getMenuComponentCount() != 0) {
//					JMenuItem mi = ((JMenuItem) undoMenu.getMenuComponent(0));
//					ActionListener[] miAls = mi.getActionListeners();
//					for (int l = 0; l < miAls.length; l++)
//						miAls[l].actionPerformed(ae);
//				}
				if (undoMenu.itemCount() != 0) {
					JMenuItem mi = ((JMenuItem) undoMenu.getDesktopElement(0, menuOwner));
					ActionListener[] miAls = mi.getActionListeners();
					for (int l = 0; l < miAls.length; l++)
						miAls[l].actionPerformed(ae);
				}
			}
		});
//		
//		//	zoom in and out on Ctrl-<plus> and Ctrl-<minus>
//		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK), "docZoomIn", new AbstractAction() {
//			public void actionPerformed(ActionEvent ae) {
//				viewControl.zoomIn();
//				viewControl.requestFocusInWindow();
//			}
//		});
//		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "docZoomOut", new AbstractAction() {
//			public void actionPerformed(ActionEvent ae) {
//				viewControl.zoomOut();
//				viewControl.requestFocusInWindow();
//			}
//		});
//		
//		//	set document layout using Ctrl+<arrow-keys>
//		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK), "docPagesHorizontal", null);
//		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK), "docPagesHorizontal", new AbstractAction() {
//			public void actionPerformed(ActionEvent ae) {
//				viewControl.setSideBySidePages(0);
//				viewControl.requestFocusInWindow();
//			}
//		});
//		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK), "docPagesVertical", null);
//		this.mapKeyStroke(docComp, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK), "docPagesVertical", new AbstractAction() {
//			public void actionPerformed(ActionEvent ae) {
//				viewControl.setSideBySidePages(1);
//				viewControl.requestFocusInWindow();
//			}
//		});
		
		//	assemble major parts
		this.add(menuPanel, BorderLayout.NORTH);
		this.add(docComp, BorderLayout.CENTER);
	}
	
	private void mapKeyStroke(JComponent docComp, KeyStroke ks, String ak, AbstractAction aa) {
		docComp.getInputMap().put(ks, ak);
//		this.viewControl.getInputMap().put(ks, ak);
//		this.viewControl.zoomSelector.getInputMap().put(ks, ak);
//		this.viewControl.layoutSelector.getInputMap().put(ks, ak);
		if (aa != null) {
			docComp.getActionMap().put(ak, aa);
//			this.viewControl.getActionMap().put(ak, aa);
//			this.viewControl.zoomSelector.getActionMap().put(ak, aa);
//			this.viewControl.layoutSelector.getActionMap().put(ak, aa);
		}
	}
//	
//	void checkUpdateXmlMarkupToolMenus(String resourceProviderClassName) {
//		GoldenGatePlugin rpp = this.ggXpedite.getPlugin(resourceProviderClassName);
//		if (rpp instanceof XmlMarkupToolProvider) {
//			String[] emxmtns = ((XmlMarkupToolProvider) rpp).getEditMenuItemNames();
//			if ((emxmtns != null) && (emxmtns.length != 0))
//				this.refreshEditMenu();
//			String[] tmxmtns = ((XmlMarkupToolProvider) rpp).getToolsMenuItemNames();
//			if ((tmxmtns != null) && (tmxmtns.length != 0))
//				this.refreshToolsMenu();
//		}
//	}
//	
//	private void addFileMenu(ArrayList itemNames, LinkedHashSet documentDependentMenuItems) {
//		HelpChapter menuHelp = new HelpChapterDataProviderBased("Menu 'File'", this.helpDataProvider, "GgXpedite.FileMenu.html");
//		this.helpContent.addSubChapter(menuHelp);
//		JMenuItem helpMi = new JMenuItem("Menu 'File'");
//		helpMi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				showHelp("Menu 'File'");
//			}
//		});
//		this.helpMenu.add(helpMi);
//		
//		if (this.ggxInMasterConfiguration)
//			System.out.println("FILE-MENU");
//		HashMap items = new LinkedHashMap() {
//			public Object put(Object key, Object value) {
//				if (ggxInMasterConfiguration)
//					System.out.println(key);
//				return super.put(key, value);
//			}
//		};
//		JMenuItem mi;
//		
//		//	add built-in saving options
//		mi = new JMenuItem("Save Document");
//		mi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				XmlDocumentEditorTab xdet = getActiveDocument();
//				if (xdet != null)
//					xdet.save();
//			}
//		});
//		items.put(mi.getText(), mi);
//		documentDependentMenuItems.add(mi);
//		
//		mi = new JMenuItem("Close Document");
//		mi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				XmlDocumentEditorTab xdet = getActiveDocument();
//				if (xdet != null)
//					closeDocument(xdet);
//			}
//		});
//		items.put(mi.getText(), mi);
//		documentDependentMenuItems.add(mi);
////		
////		//	offer selecting visible pages
////		mi = new JMenuItem("Select Pages");
////		mi.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent ae) {
////				XmlDocumentEditorTab xdet = getActiveDocument();
////				if (xdet != null)
////					xdet.selectVisiblePages();
////			}
////		});
////		items.put(mi.getText(), mi);
////		documentDependentMenuItems.add(mi);
//		
//		//	add custom items
//		FileMenuItem[] fmis = this.getFileMenuItems();
//		for (int i = 0; i < fmis.length; i++) {
//			items.put(fmis[i].getText(), fmis[i]);
//			if (fmis[i].usesActiveDocument)
//				documentDependentMenuItems.add(fmis[i]);
//		}
//		
//		//	finally ...
//		this.addMenu("File", itemNames, items);
//	}
//	
//	/**
//	 * Provide custom options for the 'File' menu. By default, the 'File' menu
//	 * only contains three options, namely "Save Document", "Close Document",
//	 * and "Select Pages". The former two delegating to the respective methods
//	 * of this class with the selected document tab as the argument, the last
//	 * delegates to the respective method of the displaying document. This
//	 * default implementation returns an empty array, sub classes are welcome
//	 * to overwrite it as needed.
//	 * @return an array holding the menu items
//	 */
//	protected FileMenuItem[] getFileMenuItems() {
//		return new FileMenuItem[0];
//	}
//	
//	/**
//	 * Specialized item for use in the 'File' menu of an Image Document Markup
//	 * UI, providing an indication whether or not it requires an open document
//	 * to have any effect.
//	 * 
//	 * @author sautter
//	 */
//	public static class FileMenuItem extends JMenuItem {
//		final boolean usesActiveDocument;
//		
//		/** Constructor
//		 * @param text the text to show on the menu item
//		 * @param icon an icon to show next to the text
//		 * @param usesActiveDocument does the action accessible through the
//		 *        menu item require an open document to take any effect?
//		 */
//		public FileMenuItem(String text, Icon icon, boolean usesActiveDocument) {
//			super(text, icon);
//			this.usesActiveDocument = usesActiveDocument;
//		}
//		
//		/** Constructor
//		 * @param text the text to show on the menu item
//		 * @param mnemonic the mnemonic key code to use for the menu item
//		 * @param usesActiveDocument does the action accessible through the
//		 *        menu item require an open document to take any effect?
//		 */
//		public FileMenuItem(String text, int mnemonic, boolean usesActiveDocument) {
//			super(text, mnemonic);
//			this.usesActiveDocument = usesActiveDocument;
//		}
//		
//		/** Constructor
//		 * @param text the text to show on the menu item
//		 * @param usesActiveDocument does the action accessible through the
//		 *        menu item require an open document to take any effect?
//		 */
//		public FileMenuItem(String text, boolean usesActiveDocument) {
//			super(text);
//			this.usesActiveDocument = usesActiveDocument;
//		}
//	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowFocusListener#windowGainedFocus(java.awt.event.WindowEvent)
	 */
	public void windowGainedFocus(WindowEvent we) {
		this.mainMenu.setActive();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowFocusListener#windowLostFocus(java.awt.event.WindowEvent)
	 */
	public void windowLostFocus(WindowEvent we) { /* we're only interested in becoming active window within the application */ }
	
	WindowMenuElement[] getWindowMenuElements() {
		ArrayList wmes = new ArrayList();
		System.out.println("GgXpediteUI: collecting window menu elements");
		
		//	collect main menu items
		if (this.performDocumentIO) {
			this.addFileMenuElements(wmes);
			this.addExportMenuElements(wmes);
			System.out.println(" ==> file and export menus added, got " + wmes.size() + " elements");
		}
		
		WindowMenuElement[] vmes = UserInterfaceUtils.getDefaultViewMenuElements(this.goldenGate, this, null, XmDocument.class);
		if (vmes != null)
			wmes.addAll(Arrays.asList(vmes));
		wmes.add(this.editDisplayConfig);
		wmes.add(this.resetDisplayConfig);
		wmes.add(this.storeDisplayConfig);
		wmes.add(this.storeAnnotDisplayModes);
		wmes.add(this.editXmlWrapperFlags);
		wmes.add(this.clearXmlWrappers);
		System.out.println(" ==> view menu added (+5 internal), got " + wmes.size() + " elements");
		
		wmes.add(this.allowReactionPrompts);
		wmes.add(this.showExpandedContextMenu);
		wmes.add(this.showContextMenuOnMouseRelese);
		WindowMenuElement[] dfmes = UserInterfaceUtils.getDefaultDocumentFunctionMenuElements(this.goldenGate, XmDocument.class);
		if (dfmes != null)
			wmes.addAll(Arrays.asList(dfmes));
		System.out.println(" ==> edit/tools menus added (+3 internal), got " + wmes.size() + " elements");
		
		wmes.add(this.undoMenu);
		System.out.println(" ==> undo menu added (+1 internal), got " + wmes.size() + " elements");
		
		//	make 'Plugins' menu available (Analyzer hot reload, etc.) ==> simplifies testing
		WindowMenuElement[] pmes = UserInterfaceUtils.getDefaultPluginsMenuElements(this.goldenGate, XmDocument.class, true);
		if ((pmes != null) && (pmes.length != 0)) {
			wmes.addAll(Arrays.asList(pmes));
			System.out.println(" ==> plug-ins menu added, got " + wmes.size() + " elements");
			int wmeFlags = 0;
			wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
			wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
//			wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MASTER_MODE;
			wmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.WINDOW_MENU_NAME, wmeFlags);
			wmes.add(new WindowMenuFunction("ggXpedite", "printMenuXml", "Print Current Menu", "Print the current status of the main menu to the log file", wmeFlags) {
				public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
					return true;
				}
				public void execute(GoldenGateUI ggui, DocumentDisplay display) {
					menuOwner.printLayoutXml();
				}
			});
		}
//		if (this.ggxInMasterConfiguration) {
//			WindowMenuElement[] pmes = UserInterfaceUtils.getDefaultPluginsMenuElements(this.goldenGate, XmDocument.class, true);
//			if (pmes != null)
//				wmes.addAll(Arrays.asList(pmes));
//			System.out.println(" ==> plug-ins menu added, got " + wmes.size() + " elements");
//			int wmeFlags = 0;
//			wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
//			wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
//			wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MASTER_MODE;
//			wmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.WINDOW_MENU_NAME, wmeFlags);
//			wmes.add(new WindowMenuFunction("ggXpedite", "printMenuXml", "Print Current Menu", "Print the current status of the main menu to the log file", wmeFlags) {
//				public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
//					return true;
//				}
//				public void execute(GoldenGateUI ggui, DocumentDisplay display) {
//					menuOwner.printLayoutXml();
//				}
//			});
//		}
		
		//	add window menu ('about', 'README', etc.)
		WindowMenuElement[] ggWmes = this.goldenGate.getWindowMenuElements(true);
		if (ggWmes != null)
			wmes.addAll(Arrays.asList(ggWmes));
		System.out.println(" ==> GG core window menu added, got " + wmes.size() + " elements");
		WindowMenuElement[] ggcWmes = this.goldenGate.getConfiguration().getWindowMenuElements(true);
		if (ggcWmes != null)
			wmes.addAll(Arrays.asList(ggcWmes));
		System.out.println(" ==> configuration window menu added, got " + wmes.size() + " elements");
		WindowMenuElement keWme = XmlUserInterfaceUtils.getDefaultKeystrokeExplanationMenuElement(this.ggXpedite, "List Keyboard Actions", "Show a list of the keyboard actions available in the GoldenGATE Xpedite main window", "Key Combination Overview for GoldenGATE Xpedite Main Window");
		if (keWme != null)
			wmes.add(keWme);
		System.out.println(" ==> keyboard action overview added, got " + wmes.size() + " elements");
//		
//		//	finish help
//		this.finishHelpMenu();
//		this.help = new Help("GoldenGATE Editor", this.helpContent, this.ggXpedite.getGoldenGateIcon());
		
		//	finally ...
		return ((WindowMenuElement[]) wmes.toArray(new WindowMenuElement[wmes.size()]));
	}
	
	void editDisplayConfig() {
		XmlDocumentEditorTab xdet = this.getActiveDocument();
		if (xdet == null)
			return;
		XmlUserInterfaceUtils.configureDisplay(xdet.xdmp, "Adjust Display Configuration");
	}
	
	void resetDisplayConfig() {
		XmlDocumentEditorTab xdet = this.getActiveDocument();
		if (xdet == null)
			return;
		for (int p = 0; p < XmDocumentMarkupPanel.displayPropertyNames.length; p++) {
			Object value = UserInterfaceUtils.getDisplayProperty(XmDocumentMarkupPanel.displayPropertyNames[p]);
			try {
				xdet.xdmp.setDisplayProperty(XmDocumentMarkupPanel.displayPropertyNames[p], value);
			}
			catch (RuntimeException re) {
				System.out.println("Failed to reset property '" + XmDocumentMarkupPanel.displayPropertyNames[p] + "' to default: " + re.getMessage());
			}
		}
	}
	
	void storeDisplayConfig() {
		XmlDocumentEditorTab xdet = this.getActiveDocument();
		if (xdet == null)
			return;
		for (int p = 0; p < XmDocumentMarkupPanel.displayPropertyNames.length; p++) {
			Object value = xdet.xdmp.getDisplayProperty(XmDocumentMarkupPanel.displayPropertyNames[p]);
			if (value == null)
				continue;
			Object defValue = XmDocumentMarkupPanel.getDisplayPropertyDefault(XmDocumentMarkupPanel.displayPropertyNames[p]);
			if (UserInterfaceUtils.equals(value, defValue))
				value = null; // erase custom property setting if value reset to default
			try {
				UserInterfaceUtils.setDisplayProperty(XmDocumentMarkupPanel.displayPropertyNames[p], value);
			}
			catch (RuntimeException re) {
				System.out.println("Failed to store property '" + XmDocumentMarkupPanel.displayPropertyNames[p] + "' as default: " + re.getMessage());
			}
		}
	}
	
	void storeAnnotDisplayModes() {
		XmlDocumentEditorTab xdet = this.getActiveDocument();
		if (xdet == null)
			return;
		String[] annotTypes = xdet.xdmp.getAnnotationTypes();
		for (int t = 0; t < annotTypes.length; t++) {
			Character atDm = xdet.xdmp.getDetailAnnotationDisplayMode(annotTypes[t]);
			if ((atDm == null) || (atDm == XmDocumentMarkupPanel.DISPLAY_MODE_INVISIBLE))
				continue;
			String dpName = ("annot." + annotTypes[t] + "showStruct");
			if (atDm == XmDocumentMarkupPanel.DISPLAY_MODE_SHOW_TAGS)
				UserInterfaceUtils.setDisplayProperty(dpName, Boolean.TRUE);
			else UserInterfaceUtils.setDisplayProperty(dpName, null);
		}
	}
	
	void editXmlWrapperFlags() {
		XmlDocumentEditorTab xdet = this.getActiveDocument();
		if (xdet == null)
			return;
		XmDocumentRootOptionPanel xdrop = new XmDocumentRootOptionPanel(xdet.getXmlWrapperFlags());
		int choice = DialogFactory.confirm(xdrop, "Edit Generix XML Flags", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (choice == JOptionPane.OK_OPTION)
			xdet.setXmlWrapperFlags(xdrop.getFlags());
	}
	
	void clearXmlWrappers() {
		if (this.docTab != null)
			this.docTab.invalidateXmlWrappers();
		else if (this.docTabs != null) {
			for (int t = 0; t < this.docTabs.getTabCount(); t++) {
				Component comp = this.docTabs.getComponentAt(t);
				if (comp instanceof XmlDocumentEditorTab)
					((XmlDocumentEditorTab) comp).invalidateXmlWrappers();
			}
		}
	}
	
	private void addFileMenuElements(ArrayList wmes) {
//		HelpChapter menuHelp = new HelpChapterDataProviderBased("Menu 'File'", this.helpDataProvider, "GgEditor.FileMenu.html");
//		this.helpContent.addSubChapter(menuHelp);
//		JMenuItem helpMi = new JMenuItem("Menu 'File'");
//		helpMi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				showHelp("Menu 'File'");
//			}
//		});
//		this.helpMenu.add(helpMi);
		
		//	set up flags
		int wmeFlags = 0; // TODO populate flags
		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
//		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_ONLINE; // TODO for real ??? might be something for web UI only
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		wmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.FILE_MENU_NAME, wmeFlags);
		
		//	add built-in saving options
		wmes.add(new WindowMenuFunction("ggXpedite", "saveDoc", "Save Document", "Save the currently selected document back to where it was loaded from", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display instanceof XmlDocumentEditorTab);
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				if (display instanceof XmlDocumentEditorTab)
					((XmlDocumentEditorTab) display).save();
			}
		});
		
		//	add closing option
		wmes.add(new WindowMenuFunction("ggXpedite", "closeDoc", "Close Document", "Close the currently selected document", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display instanceof XmlDocumentEditorTab);
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				if (display instanceof XmlDocumentEditorTab)
					closeDocument((XmlDocumentEditorTab) display);
			}
		});
		
		//	add custom items
		WindowMenuElement[] fmes = this.getFileMenuElements();
		if (fmes != null)
			wmes.addAll(Arrays.asList(fmes));
	}
	
	/**
	 * Provide custom options for the 'File' menu. By default, the 'File' menu
	 * only contains two options, namely "Save Document" and "Close Document".
	 * They delegate to the respective methods of this class with the selected
	 * document tab as the argument. This default implementation returns an
	 * empty array, sub classes are welcome to overwrite it as needed.
	 * @return an array holding the menu elements
	 */
	protected WindowMenuElement[] getFileMenuElements() {
		return new WindowMenuElement[0];
	}
	
	private void addExportMenuElements(ArrayList wmes) {
//		HelpChapter menuHelp = new HelpChapterDataProviderBased("Menu 'Export'", this.helpDataProvider, "GgEditor.ExportMenu.html");
//		this.helpContent.addSubChapter(menuHelp);
//		JMenuItem helpMi = new JMenuItem("Menu 'Export'");
//		helpMi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				showHelp("Menu 'Export'");
//			}
//		});
//		this.helpMenu.add(helpMi);
//		
//		if (this.ggeInMasterConfiguration)
//			System.out.println("EXPORT-MENU");
//		HashMap items = new LinkedHashMap() {
//			public Object put(Object key, Object value) {
//				if (ggeInMasterConfiguration)
//					System.out.println(key);
//				return super.put(key, value);
//			}
//		};
//		JMenuItem mi;
		
		//	set up flags
		int wmeFlags = 0; // TODO populate flags
		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
//		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_ONLINE; // TODO for real ??? might be something for web UI only
		wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		wmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.EXPORT_MENU_NAME, wmeFlags);
		
		//	add export as plain XML
		wmes.add(new WindowMenuFunction("ggXpedite", "exportXml", "Export XML", "Export the currently selected document as regular XML", wmeFlags) {
			private boolean exportIDs = false;
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display instanceof XmlDocumentEditorTab);
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				XmlDocumentEditorTab xdet;
				if (display instanceof XmlDocumentEditorTab)
					xdet = ((XmlDocumentEditorTab) display);
				else return;
				clearFileFilters(fileChooser);
				fileChooser.addChoosableFileFilter(xmlFileFilter);
				File likelyDest = getLikelyExportDestination(xdet);
				if (likelyDest != null)
					fileChooser.setSelectedFile(likelyDest);
				
				XmDocumentRootOptionPanel xdrop = new XmDocumentRootOptionPanel(xdet.getXmlWrapperFlags());
				JCheckBox exportIDs = new JCheckBox("Export Annotation IDs", this.exportIDs);
				xdrop.add(exportIDs);
				JPanel idropPosPanel = new JPanel(new BorderLayout());
				idropPosPanel.add(xdrop, BorderLayout.SOUTH);
				fileChooser.setAccessory(idropPosPanel);
				
				int choice = fileChooser.showSaveDialog(XmlDocumentMarkupUI.this);
				fileChooser.setAccessory(null);
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
//				this.xmlWrapperFlags = idrop.getFlags();
				int xmlWrapperFlags = xdrop.getFlags();
				xdet.setXmlWrapperFlags(xmlWrapperFlags);
				this.exportIDs = exportIDs.isSelected();
				File file = fileChooser.getSelectedFile();
				if (file.isDirectory())
					return;
				try {
					exportXml(xdet.getXmDocument(), file, xmlWrapperFlags, this.exportIDs);
				}
				catch (IOException ioe) {
					JOptionPane.showMessageDialog(XmlDocumentMarkupUI.this, ("An error occurred while exporting the document to '" + file.getAbsolutePath() + "':\n" + ioe.getMessage()), "Error Exporting Document", JOptionPane.ERROR_MESSAGE);
					ioe.printStackTrace(System.out);
				}
			}
		});
		
		//	add export as GAMTA XML stream
		wmes.add(new WindowMenuFunction("ggXpedite", "exportGamtaXml", "Export GAMTA XML", "Export the currently selected document as a GAMTA XML stream", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display instanceof XmlDocumentEditorTab);
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				XmlDocumentEditorTab xdet;
				if (display instanceof XmlDocumentEditorTab)
					xdet = ((XmlDocumentEditorTab) display);
				else return;
				clearFileFilters(fileChooser);
				fileChooser.addChoosableFileFilter(xmlFileFilter);
				File likelyDest = getLikelyExportDestination(xdet);
				if (likelyDest != null)
					fileChooser.setSelectedFile(likelyDest);
				if (fileChooser.showSaveDialog(XmlDocumentMarkupUI.this) != JFileChooser.APPROVE_OPTION)
					return;
				File file = fileChooser.getSelectedFile();
				if (file.isDirectory())
					return;
				try {
					
					//	make sure file has appropriate extension
					if (!file.getName().toLowerCase().endsWith(".xml"))
						file = new File(file.toString() + ".xml");
					
					//	make way
					if (file.exists()) {
						String fileName = file.toString();
						File oldFile = new File(fileName + "." + System.currentTimeMillis() + ".old");
						file.renameTo(oldFile);
						file = new File(fileName);
					}
					
					//	export document
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
					XmDocumentRoot gamtaDoc = new XmDocumentRoot(xdet.xdmp.document, xdet.getXmlWrapperFlags());
					GenericGamtaXML.storeDocument(gamtaDoc, out);
					out.close();
				}
				catch (IOException ioe) {
					JOptionPane.showMessageDialog(XmlDocumentMarkupUI.this, ("An error occurred while exporting the document to '" + file.getAbsolutePath() + "':\n" + ioe.getMessage()), "Error Exporting Document", JOptionPane.ERROR_MESSAGE);
					ioe.printStackTrace(System.out);
				}
			}
		});
//		
//		//	add document exports from configuration
//		//	TODO re-activate this once GG Editor fully implements upon GG Core
//		XmlDocumentSaver[] docSavers = this.ggXpedite.getDocumentSavers();
//		if (docSavers.length != 0) {
//			for (int s = 0; s < docSavers.length; s++) {
//				final DocumentSaver docSaver = docSavers[s];
//				JMenuItem dsmi = docSavers[s].getSaveDocumentMenuItem();
//				mi = new JMenuItem(dsmi.getText().replaceAll("Save", "Export"));
//				mi.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent ae) {
//						XmlDocumentEditorTab xdet = getActiveDocument();
//						if (xdet == null)
//							return;
//						try {
//							exportDocument(xdet.getMarkupPanel().document, docSaver, xdet.docName);
//						}
//						catch (IOException ioe) {
//							JOptionPane.showMessageDialog(XmlDocumentMarkupUI.this, ("An error occurred while exporting the document via " + ((GoldenGatePlugin) docSaver).getPluginName() + ":\n" + ioe.getMessage()), "Error Exporting Document", JOptionPane.ERROR_MESSAGE);
//							ioe.printStackTrace(System.out);
//						}
//					}
//				});
//				items.put(mi.getText(), mi);
////				
////				//	add plugin specific help chapter if available
////				HelpChapter docSaverHelp = ((GoldenGatePlugin) docSavers[s]).getHelp();
////				if (docSaverHelp != null)
////					menuHelp.addSubChapter(docSaverHelp);
//			}
//		}
		
		//	add dedicated exporters
		XmlDocumentExporter[] des = this.ggXpedite.getDocumentExporters();
		for (int e = 0; e < des.length; e++) {
			final XmlDocumentExporter de = des[e];
			wmeFlags = 0; // TODO populate flags
			wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
//			wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_ONLINE; // TODO for real ??? might be something for web UI only
			wmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
			wmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.EXPORT_MENU_NAME, wmeFlags);
			wmes.add(new WindowMenuFunction(des[e], "export", de.getExportMenuLabel(), de.getExportMenuTooltip(), wmeFlags) {
				public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
					return (display instanceof XmlDocumentEditorTab);
				}
				public void execute(GoldenGateUI ggui, DocumentDisplay display) {
					if (display instanceof XmlDocumentEditorTab) {
						XmlDocumentEditorTab xdet = ((XmlDocumentEditorTab) display);
						File likelyDest = ((de instanceof XmlDocumentFileExporter) ? getLikelyExportDestination(xdet) : null);
						exportDocument(likelyDest, xdet.xdmp.document, de);
					}
				}
			});
//			
//			//	add exporter specific help chapter if available
//			HelpChapter ideHelp = ((GoldenGatePlugin) ides[e]).getHelp();
//			if (ideHelp != null)
//				menuHelp.addSubChapter(ideHelp);
		}
//		
//		//	finally ...
//		JMenu exportMenu = this.addMenu("Export", itemNames, items);
//		documentDependentMenuItems.add(exportMenu);
	}
	
	void exportDocument(final File likelyDest, final XmDocument doc, final XmlDocumentExporter ide) {
		
		//	get progress monitor
		final ResourceSplashScreen ss = new ResourceSplashScreen(this.getMainWindow(), "Exporting Document", "Plaease wait while exporting the document.", true, true);
		
		//	apply document processor, in separate thread
		Thread ideThread = new Thread() {
			public void run() {
				try {
					
					//	wait for splash screen progress monitor to come up (we must not reach the dispose() line before the splash screen even comes up)
					while (!ss.isVisible()) try {
						Thread.sleep(10);
					} catch (InterruptedException ie) {}
					
					//	perform export
					if (ide instanceof XmlDocumentFileExporter)
						((XmlDocumentFileExporter) ide).exportDocument(likelyDest, doc, ss);
					else ide.exportDocument(doc, ss);
				}
				
				//	catch whatever might happen
				catch (Throwable t) {
					t.printStackTrace(System.out);
					JOptionPane.showMessageDialog(DialogFactory.getTopWindow(), ("An error occurred while exporting the document:\n" + t.getMessage()), "Error Exporting Document", JOptionPane.ERROR_MESSAGE);
				}
				
				//	clean up
				finally {
					
					//	dispose splash screen progress monitor
					ss.dispose();
				}
			}
		};
		ideThread.start();
		
		//	open splash screen progress monitor (this waits)
		ss.setVisible(true);
	}
	
	void exportXml(XmDocument doc, File file, int configFlags, boolean exportIDs) throws IOException {
		
		//	make sure file has appropriate extension
		if (!file.getName().toLowerCase().endsWith(".xml"))
			file = new File(file.toString() + ".xml");
		
		//	make way
		if (file.exists()) {
			String fileName = file.toString();
			File oldFile = new File(fileName + "." + System.currentTimeMillis() + ".old");
			file.renameTo(oldFile);
			file = new File(fileName);
		}
		
		//	export document
		XmDocumentRoot xmlDoc = new XmDocumentRoot(doc, configFlags);
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		AnnotationUtils.writeXML(xmlDoc, out, exportIDs);
		out.flush();
		out.close();
	}
//	
//	void exportDocument(XmDocument doc, DocumentSaver docSaver, String docName) throws IOException {
//		
//		//	obtain document save operation
//		DocumentSaveOperation dso = docSaver.getSaveOperation(docName, null);
//		if (dso == null)
//			return;
//		
//		//	export file
//		XmDocumentRoot xmlDoc = new XmDocumentRoot(doc, 0);
////		xmlDoc.setShowTokensAsWordsAnnotations(true);
//		dso.saveDocument(xmlDoc);
//	}
//	
//	private HelpChapter buildHelpContentRoot() {
//		HelpChapter helpRoot = new HelpChapterDataProviderBased("GoldenGATE Xpedite", this.helpDataProvider, "GgXpedite.html");
//		helpRoot.addSubChapter(new HelpChapterDataProviderBased("Glossary", this.helpDataProvider, "GgXpedite.Glossary.html"));
//		
//		HelpChapter editorHelp = new HelpChapterDataProviderBased("Editor", this.helpDataProvider, "GgXpedite.Editor.html");
//		helpRoot.addSubChapter(editorHelp);
//		SelectionActionProvider[] saps = this.ggXpedite.getSelectionActionProviders();
//		for (int p = 0; p < saps.length; p++) {
//			HelpChapter sapHelp = saps[p].getHelp();
//			if (sapHelp != null)
//				editorHelp.addSubChapter(sapHelp);
//		}
//		
//		XmlDocumentDropHandler[] dropHandlers = this.ggXpedite.getDropHandlers();
//		if (dropHandlers.length != 0) {
//			HelpChapter dragDropHelp = new HelpChapterDataProviderBased("Drag & Drop", this.helpDataProvider, "GgXpedite.DragDrop.html");
//			helpRoot.addSubChapter(dragDropHelp);
//			for (int h = 0; h < dropHandlers.length; h++) {
//				HelpChapter dhHelp = dropHandlers[h].getHelp();
//				if (dhHelp != null)
//					dragDropHelp.addSubChapter(dhHelp);
//			}
//		}
////		
////		HelpChapter pageImageHelp = new HelpChapterDataProviderBased("Page Image Editing", this.helpDataProvider, "GgXpedite.PageImageEditing.html");
////		helpRoot.addSubChapter(pageImageHelp);
////		XmlEditToolProvider[] ietps = this.ggXpedite.getImageEditToolProviders();
////		for (int p = 0; p < ietps.length; p++) {
////			HelpChapter ietpHelp = ietps[p].getHelp();
////			if (ietpHelp != null)
////				pageImageHelp.addSubChapter(ietpHelp);
////		}
//		
//		return helpRoot;
//	}
//	
//	private JMenu createHelpMenu() {
//		JMenu helpMenu = new JMenu("Help");
//		
//		JMenuItem mi = new JMenuItem("Help");
//		mi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				showHelp(null);
//			}
//		});
//		helpMenu.add(mi);
//		
//		helpMenu.addSeparator();
//		
//		return helpMenu;
//	}
//	
//	private void finishHelpMenu() {
//		this.helpMenu.addSeparator();
//		
//		JMenuItem ami = new JMenuItem("About");
//		ami.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				ggXpedite.showAbout();
//			}
//		});
//		this.helpMenu.add(ami);
//		
//		JMenuItem rmi = new JMenuItem("View Readme");
//		rmi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				ggXpedite.showReadme();
//			}
//		});
//		this.helpMenu.add(rmi);
//		
//		this.addMenu(this.helpMenu);
//	}
//	
//	/**
//	 * Show some help information.
//	 * @param on the subject of the desired help information
//	 */
//	public void showHelp(String on) {
//		if (this.help != null)
//			this.help.showHelp(on);
//	}
//	
//	class ViewControl extends JPanel {
//		private JLabel scrollPosition = new JLabel("Page 0 of 0", JLabel.CENTER);
//		private int pageImageDpi = ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI;
//		private JComboBox zoomSelector = new JComboBox();
//		private JComboBox layoutSelector = new JComboBox();
//		ViewControl() {
//			super(new GridLayout(1, 0), true);
//			
//			this.scrollPosition.setOpaque(true);
//			this.scrollPosition.setBackground(Color.WHITE);
//			this.scrollPosition.setBorder(BorderFactory.createLoweredBevelBorder());
//			
//			this.zoomSelector.addItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI / 4));
//			this.zoomSelector.addItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI / 3));
//			this.zoomSelector.addItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI / 2));
//			this.zoomSelector.addItem(new ZoomLevel((ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI * 2) / 3));
//			this.zoomSelector.addItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI));
//			this.zoomSelector.addItem(new ZoomLevel((ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI * 3) / 2));
//			this.zoomSelector.addItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI * 2));
//			this.zoomSelector.addItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI * 3));
//			this.zoomSelector.addItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI * 4));
//			this.zoomSelector.addItem(new ZoomLevel(0));
//			this.zoomSelector.setSelectedItem(new ZoomLevel(ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI));
//			this.zoomSelector.setEditable(false);
//			this.zoomSelector.addItemListener(new ItemListener() {
//				public void itemStateChanged(ItemEvent ie) {
//					zoomChanged();
//				}
//			});
//			
//			this.layoutSelector.addItem("Pages Top-Down");
//			this.layoutSelector.addItem("Pages Left-Right");
//			this.layoutSelector.setSelectedItem("Pages Top-Down");
//			this.layoutSelector.setEditable(false);
//			this.layoutSelector.addItemListener(new ItemListener() {
//				public void itemStateChanged(ItemEvent ie) {
//					layoutChanged();
//				}
//			});
//			
//			this.add(this.scrollPosition);
//			this.add(this.zoomSelector);
//			this.add(this.layoutSelector);
//		}
//		
//		void zoomIn() {
//			int szi = this.zoomSelector.getSelectedIndex();
//			if ((szi + 2) < this.zoomSelector.getItemCount()) // we have to block 'Original Resolution' at end of list
//				this.zoomSelector.setSelectedIndex(szi + 1);
//		}
//		void zoomOut() {
//			int szi = this.zoomSelector.getSelectedIndex();
//			if (szi > 0)
//				this.zoomSelector.setSelectedIndex(szi - 1);
//		}
//		void zoomChanged() {
//			if (this.inUpdate)
//				return;
//			XmlDocumentEditorTab xdet = getActiveDocument();
//			if (xdet == null)
//				return;
//			ZoomLevel zl = ((ZoomLevel) this.zoomSelector.getSelectedItem());
//			this.inNotification = true;
//			if (zl.dpi == 0)
//				xdet.setRenderingDpi(this.pageImageDpi);
//			else xdet.setRenderingDpi(zl.dpi);
//			this.inNotification = false;
//			KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
//		}
//		int getRenderingDpi() {
//			return ((ZoomLevel) this.zoomSelector.getSelectedItem()).dpi;
//		}
//		
//		void setSideBySidePages(int sbsp) {
//			this.layoutSelector.setSelectedItem((sbsp == 1) ? "Pages Top-Down" : "Pages Left-Right");
//		}
//		void layoutChanged() {
//			if (this.inUpdate)
//				return;
//			XmlDocumentEditorTab xdet = getActiveDocument();
//			if (xdet == null)
//				return;
//			this.inNotification = true;
//			xdet.setSideBySidePages("Pages Top-Down".equals(this.layoutSelector.getSelectedItem()) ? 1 : 0);
//			this.inNotification = false;
//			KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
//		}
//		boolean isLeftRightLayout() {
//			return "Pages Left-Right".equals(this.layoutSelector.getSelectedItem());
//		}
//		
//		private boolean inUpdate = false;
//		private boolean inNotification = false;
//		void update(XmlDocumentEditorTab xdet) {
//			if (this.inNotification)
//				return;
//			this.inUpdate = true;
//			if (xdet == null)
//				this.scrollPosition.setText("Page 0 of 0");
//			else {
//				xdet.updateScrollPosition();
//				this.pageImageDpi = xdet.getMarkupPanel().getMaxPageImageDpi();
//				this.zoomSelector.setSelectedItem(new ZoomLevel(xdet.getMarkupPanel().getRenderingDpi()));
//				this.layoutSelector.setSelectedItem((xdet.getMarkupPanel().getSideBySidePages() == 1) ? "Pages Top-Down" : "Pages Left-Right");
//			}
//			this.inUpdate = false;
//		}
//		
//		private class ZoomLevel {
//			final int dpi;
//			ZoomLevel(int dpi) {
//				this.dpi = dpi;
//			}
//			public String toString() {
//				if (this.dpi == 0)
//					return "Original Resolution";
//				else return (((this.dpi * 100) / ImDocumentMarkupPanel.DEFAULT_RENDERING_DPI) + "%");
//			}
//			public boolean equals(Object obj) {
//				return ((obj instanceof ZoomLevel) && (((ZoomLevel) obj).dpi == this.dpi));
//			}
//		}
//	}
//	
//	private JMenu addMenu(String name, ArrayList itemNames, HashMap itemsByName) {
//		JMenu menu = MenuBuilder.buildMenu(name, itemNames, itemsByName, this.ggxInMasterConfiguration);
//		this.addMenu(menu);
//		return menu;
//	}
//	
//	void addMenu(JMenu menu) {
//		this.mainMenu.add(menu);
//	}
	
	/* TODO add page navigator to editor tabs
	 * - represent pages a thumbnails
	 * - pages stacked vertical --> navigator on left edge, scrolling top down
	 * - pages side-by-side --> navigator at bottom, scrolling left right
	 * - click on page thumbnail --> scroll directly to that page
	 * 
	 * - when document navigator visible, scroll along with main editor window ...
	 * - ... but let document navigator scroll by itself without scrolling main editor window (obviously ...)
	 *
	 * - when page numbers are added to or removed from pages, update page thumbnails in document navigator
	 */
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#updateMenus()
	 */
	public void updateMenus() {
		this.menuOwner.updateMenu();
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#getActiveDocumentDisplay()
	 */
	public DocumentDisplay getActiveDocumentDisplay() {
		return this.getActiveDocument();
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#getActiveDocumentReadOnly()
	 */
	public QueriableAnnotation getActiveDocumentReadOnly() {
		XmlDocumentEditorTab xdet = this.getActiveDocument();
		return ((xdet == null) ? null : xdet.getDocumentReadOnly());
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#getActiveDocumentId()
	 */
	public String getActiveDocumentId() {
		XmlDocumentEditorTab xdet = this.getActiveDocument();
		return ((xdet == null) ? null : xdet.getDocumentId());
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#getDocumentCount()
	 */
	public int getDocumentCount() {
		return ((this.docTabs == null) ? 1 : this.docTabs.getTabCount());
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#getDocumentDisplays()
	 */
	public DocumentDisplay[] getDocumentDisplays() {
		if (this.docTab == null) {
			DocumentDisplay[] dds = new DocumentDisplay[this.docTabs.getTabCount()];
			for (int t = 0; t < this.docTabs.getTabCount(); t++)
				dds[t] = ((DocumentDisplay) this.docTabs.getComponentAt(t));
			return dds;
		}
		else {
			DocumentDisplay[] dds = {
				this.docTab
			};
			return dds;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#getDefaultAnnotationColor(java.lang.String)
	 */
	public Color getDefaultAnnotationColor(String type) {
		return UserInterfaceUtils.getAnnotationColor(type);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#setDefaultAnnotationColor(java.lang.String, java.awt.Color)
	 */
	public void setDefaultAnnotationColor(String type, Color color) {
		if (color != null)
			UserInterfaceUtils.setAnnotationColor(type, color);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI#getKnownAnnotationTypes()
	 */
	public String[] getKnownAnnotationTypes() {
		return UserInterfaceUtils.getKnownAnnotationTypes();
	}
	
	/**
	 * Display tab for a single document in the markup UI.
	 * 
	 * @author sautter
	 */
	public static class XmlDocumentEditorTab extends XmlDocumentMarkupPanel {
		private String docName;
		
		/** Constructor
		 * @param doc the document to display
		 * @param docName the name of the document
		 * @param ggXpedite the GoldenGATE Xpedite instance to use
		 * @param ggxConfig the configuration of the GoldenGATE Xpedite instance to use
		 */
//		protected XmlDocumentEditorTab(XmDocument doc, String docName, GoldenGateXpedite ggXpedite, Settings ggxConfig) {
		protected XmlDocumentEditorTab(XmDocument doc, String docName, GoldenGateXpedite ggXpedite) {
//			super(doc, ggXpedite, ggxConfig);
			super(doc, ggXpedite);
			this.docName = docName;
		}
		
		/** Constructor
		 * @param parent the parent UI (for callbacks)
		 * @param doc the document to display
		 * @param docName the name of the document
		 */
		protected XmlDocumentEditorTab(XmlDocumentMarkupUI parent, XmDocument doc, String docName) {
//			super(doc, parent.ggXpedite, parent.ggxConfig);
			super(doc, parent.ggXpedite);
			this.docName = docName;
			this.setParent(parent);
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getMenuOwner()
		 */
		public WindowMenuOwner getMenuOwner() {
			return ((this.parent == null) ? null : this.parent.menuOwner);
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getDocumentViews(de.uka.ipd.idaho.gamta.Annotation[], de.uka.ipd.idaho.gamta.QueriableAnnotation, int, java.lang.Class)
		 */
		public DocumentViewInstance[] getDocumentViews(Annotation[] annotations, QueriableAnnotation doc, int modeFlags, Class providerClass) {
			ArrayList dvis = new ArrayList();
			int docFlags = 0;
			if (this.areAnnotationsEditable())
				docFlags |= DocumentViewInstance.MODE_ANNOTATIONS_EDITABLE;
			if (this.areTokensEditable())
				docFlags |= DocumentViewInstance.MODE_TOKENS_EDITABLE;
			DocumentViewProvider[] dvps = this.parent.goldenGate.getDocumentViewProviders();
			for (int p = 0; p < dvps.length; p++) {
				DocumentViewInstance[] pDvis = dvps[p].getDocumentViews(this, this.xdmp.document, annotations, doc, docFlags, modeFlags);
				if (pDvis != null)
					dvis.addAll(Arrays.asList(pDvis));
			}
			return ((DocumentViewInstance[]) dvis.toArray(new DocumentViewInstance[dvis.size()]));
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#showDocumentView(de.uka.ipd.idaho.goldenGate.ui.DocumentView, de.uka.ipd.idaho.goldenGate.ui.DialogMainButton[], de.uka.ipd.idaho.goldenGate.ui.DocumentView.DocumentViewAugmenter)
		 */
		public DialogMainButton showDocumentView(DocumentViewInstance viewInstance, DialogMainButton[] mainButtons, DocumentViewAugmenter augmenter) {
			boolean handleAtomicAction;
			if (viewInstance.isDirectWriteThrough())
				handleAtomicAction = false; // in direct write-through, actions in view start atomic actions
			else if (this.xdmp.isAtomicActionRunning())
				handleAtomicAction = false; // atomic action started externally
			else handleAtomicAction = true;
			try {
				if (handleAtomicAction) {
					XmAnnotation aaAnnot; // get underlying annotation
					if (viewInstance instanceof XmlDocumentViewInstance)
						aaAnnot = ((XmlDocumentViewInstance) viewInstance).xmScope;
					else if (viewInstance.scope == null)
						aaAnnot = null;
					else {
						XmObject xmObj = this.xdmp.document.getObjectByUUID(viewInstance.scope.getAnnotationID());
						aaAnnot = ((xmObj instanceof XmAnnotation) ? ((XmAnnotation) xmObj) : null);
					}
					this.xdmp.startAtomicAction(viewInstance.label, null, aaAnnot, ProgressMonitor.dummy);
				}
				return this.parent.viewDisplayService.showDocumentView(this, viewInstance, mainButtons, augmenter, null /* TODO do we need to listen for anything ??? */);
			}
			finally {
				if (handleAtomicAction)
					this.xdmp.finishAtomicAction(ProgressMonitor.dummy);
			}
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getActiveDocumentViews()
		 */
		public DocumentViewInstance[] getActiveDocumentViews() {
			return this.parent.viewDisplayService.getActiveDocumentViews(this);
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.goldenGate.editor.swing.DocumentMarkupPanel#getUndoMenuOwner()
		 */
		protected WindowMenuOwner getUndoMenuOwner() {
			return this.parent.menuOwner;
		}
//		
//		/* (non-Javadoc)
//		 * @see de.uka.ipd.idaho.xm.xpedite.swing.ImageDocumentMarkupPanel#getUndoMenu()
//		 */
//		protected JMenu getUndoMenu() {
//			return this.parent.undoMenu;
//		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.xm.xpedite.swing.ImageDocumentMarkupPanel#getUndoMenu()
		 */
		protected DynamicWindowMenu getUndoMenu() {
			return this.parent.undoMenu;
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.xm.xpedite.swing.ImageDocumentMarkupPanel#allowReactionPrompts()
		 */
		protected boolean allowReactionPrompts() {
			return this.parent.allowReactionPrompts.isSelected();
		}
//		
//		/* (non-Javadoc)
//		 * @see de.uka.ipd.idaho.xm.xpedite.swing.ImageDocumentMarkupPanel#scrollPositionChanged(java.lang.String)
//		 */
//		protected void scrollPositionChanged(String posLabel) {
//			this.parent.viewControl.scrollPosition.setText(posLabel);
//		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.xm.xpedite.swing.ImageDocumentMarkupPanel#getMainWindow()
		 */
		protected Window getMainWindow() {
			return this.parent.getMainWindow();
		}
		
		/**
		 * Get the (current) name of the content document.
		 * @return the document name
		 */
		public String getDocName() {
			return this.docName;
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.xm.xpedite.swing.ImageDocumentMarkupPanel#handleDrop(java.awt.datatransfer.Transferable)
		 */
		protected void handleDrop(Transferable dropped) {
			this.parent.handleDrop(dropped);
		}
		
		/**
		 * Save the content document of the editor tab. This default
		 * implementation delegates to the <code>saveDocument()</code> method
		 * of the surrounding markup UI. Sub classes may provide more options.
		 * @return true if the document was saved, false otherwise
		 */
		public boolean save() {
			return (this.isDirty() ? this.parent.saveDocument(this) : true);
		}
		
		/**
		 * Notify the editor tab that its content document was saved under a
		 * specific name. This implementation updates the editor tab title and
		 * marks the document as clean. Sub classes overwriting this method to
		 * take further actions thus have to make the super call.
		 * @param saveDocName the name the content document was saved under
		 */
		public void savedAs(String saveDocName) {
			this.markClean();
			this.docName = saveDocName;
			this.parent.documentNameChanged(this);
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.xm.xpedite.swing.ImageDocumentMarkupPanel#dispose(boolean)
		 */
		public void dispose(boolean storeSettings) {
			super.dispose(storeSettings);
			this.parent.removeDocument(this);
		}
	}
	
	/**
	 * Open a document editor tab to show in the markup UI.
	 * @param xdet the editor tab to add
	 */
	public void openDocument(XmlDocumentEditorTab xdet) {
		if (this.docTabs == null)
			throw new IllegalStateException("Cannot add document editor tab in single-document mode.");
		xdet.setParent(this);
		this.docTabs.addTab(xdet.getDocName(), xdet);
		this.docTabs.setSelectedComponent(xdet);
//		this.setDocumentDependentMenuItemsEnabled(true);
	}
	
	void removeDocument(XmlDocumentEditorTab xdet) {
		if (this.docTabs != null) {
			this.docTabs.remove(xdet);
//			this.setDocumentDependentMenuItemsEnabled(this.docTabs.getTabCount() != 0);
		}
		else if (xdet == this.docTab)
			this.docTab = null;
	}
	
	/**
	 * Retrieve the editor tab holding currently selected document. The runtime
	 * type of the argument editor tab is whatever sub classes hand to
	 * <code>openDocument()</code> or the constructor that takes a tab as an
	 * argument.
	 * @return the editor tab holding the currently selected document
	 */
	public XmlDocumentEditorTab getActiveDocument() {
		if (this.docTabs == null)
			return this.docTab;
		else return ((this.docTabs.getComponentCount() == 0) ? null : ((XmlDocumentEditorTab) this.docTabs.getSelectedComponent()));
	}
	
	/**
	 * Close the displaying document. This implementation handles UI cleanup
	 * as well as on-demand saving. Sub classes overwriting this method to take
	 * further action thus must make the super call, best before performing any
	 * cleanup themselves.
	 * @return true if the document was actually closed, false otherwise
	 */
	public boolean closeDocument(XmlDocumentEditorTab xdet) {
		if (xdet == null)
			return true;
		
		//	save document if dirty
		if (this.performDocumentIO && xdet.isDirty()) {
			int choice = JOptionPane.showConfirmDialog(XmlDocumentMarkupUI.this, ("Document '" + xdet.getDocName() + "' has un-saved changes. Save them before closing it?"), "Save Changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (choice == JOptionPane.CANCEL_OPTION)
				return false;
			if (choice == JOptionPane.YES_OPTION) {
				if (!xdet.save())
					return false;
			}
		}
		
		//	close any non-modal views still left open
		this.viewDisplayService.closeActiveDocumentViews(xdet);
		
		//	close document tab proper
		xdet.dispose(true);
		
		//	might have been last document, need to adjust menu
		this.menuOwner.updateMenu();
		
		//	finally ...
		return true;
	}
	
	/**
	 * Close the markup UI. This method first closes all open documents and
	 * aborts if one remains open. Sub classes overwriting this method to take
	 * further action thus must make the super call, best before performing any
	 * cleanup themselves.
	 */
	public boolean close() {
		while (this.getActiveDocument() != null) {
			if (!this.closeDocument(this.getActiveDocument()))
				return false;
		}
		return true;
	}
	
	/**
	 * Retrieve the application main window, i.e., the one to set pop-ups and
	 * splash screens modal to. This default implementation simply returns the
	 * current top window. Sub classes are welcome to overwrite this behavior
	 * with a more sophisticated approach.
	 * @return the application main window
	 */
	protected Window getMainWindow() {
		return DialogFactory.getTopWindow();
	}
	
	/**
	 * Handle a drop on the markup panel that did not go to any of the present
	 * drop handlers. This default implementation does nothing. Sub classes are
	 * welcome to overwrite it as needed.
	 * @param dropped the dropped data
	 */
	protected void handleDrop(Transferable dropped) {}
	
	/**
	 * Handle a change to a document name, mainly after saving. The default
	 * implementation updates the title of the argument tab. Sub classes may
	 * overwrite it to take other actions. The runtime type of the argument
	 * editor tab is whatever sub classes hand to <code>openDocument()</code>
	 * or the constructor that takes a tab as an argument.
	 * @param xdet the editor tab whose content name changed
	 */
	protected void documentNameChanged(XmlDocumentEditorTab xdet) {
		if (this.docTabs != null)
			this.docTabs.setTitleAt(this.docTabs.indexOfComponent(xdet), xdet.getDocName());
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
	
	/**
	 * Predict the export destination for the document displayed in an editor
	 * tab. This is used to initialize the file selection dialog. This default
	 * implementation returns null, indicating "no prediction possible". Sub
	 * classes are welcome to overwrite it as needed. The runtime type of the
	 * argument editor tab is whatever sub classes hand to
	 * <code>openDocument()</code> or the constructor that takes a tab as an
	 * argument.
	 * @param xdet the document editor tab whose content will be exported
	 * @return a file pointing to the likely export destination
	 */
	protected File getLikelyExportDestination(XmlDocumentEditorTab xdet) {
		return null;
	}
	
	private static final FileFilter xmlFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".xml"));
		}
		public String getDescription() {
			return "XML Documents";
		}
	};
	private static void clearFileFilters(JFileChooser fileChooser) {
		FileFilter[] fileFilters = fileChooser.getChoosableFileFilters();
		for (int f = 0; f < fileFilters.length; f++)
			fileChooser.removeChoosableFileFilter(fileFilters[f]);
	}
}