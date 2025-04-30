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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import de.uka.ipd.idaho.gamta.Annotation;
import de.uka.ipd.idaho.gamta.Attributed;
import de.uka.ipd.idaho.gamta.EditableAnnotation;
import de.uka.ipd.idaho.gamta.MutableAnnotation;
import de.uka.ipd.idaho.gamta.QueriableAnnotation;
import de.uka.ipd.idaho.gamta.util.ProgressMonitor;
import de.uka.ipd.idaho.gamta.util.constants.LiteratureConstants;
import de.uka.ipd.idaho.gamta.util.swing.DialogFactory;
import de.uka.ipd.idaho.gamta.util.swing.ProgressMonitorWindow;
import de.uka.ipd.idaho.goldenGate.GoldenGATE;
import de.uka.ipd.idaho.goldenGate.plugins.AnnotationSource;
import de.uka.ipd.idaho.goldenGate.plugins.AnnotationSourceManager;
import de.uka.ipd.idaho.goldenGate.plugins.AnnotationSourceManager.AnnotationSourceResult;
import de.uka.ipd.idaho.goldenGate.plugins.DocumentProcessor;
import de.uka.ipd.idaho.goldenGate.plugins.DocumentProcessorManager;
import de.uka.ipd.idaho.goldenGate.plugins.ResourceSplashScreen;
import de.uka.ipd.idaho.goldenGate.ui.DynamicWindowMenu;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay;
import de.uka.ipd.idaho.goldenGate.ui.NamedElementUsageStatistics;
import de.uka.ipd.idaho.goldenGate.ui.UserInterfaceUtils;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuOwner;
import de.uka.ipd.idaho.goldenGate.util.AnnotationSourceResultDialog;
import de.uka.ipd.idaho.xm.XmAnnotation;
import de.uka.ipd.idaho.xm.XmDocument;
import de.uka.ipd.idaho.xm.XmDocument.XmDocumentListener;
import de.uka.ipd.idaho.xm.XmObject;
import de.uka.ipd.idaho.xm.XmSupplement;
import de.uka.ipd.idaho.xm.gamta.LazyAnnotation;
import de.uka.ipd.idaho.xm.gamta.LazyMutableAnnotation;
import de.uka.ipd.idaho.xm.gamta.LazyQueriableAnnotation;
import de.uka.ipd.idaho.xm.gamta.XmDocumentRoot;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.AtomicActionListener;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.SelectionAction;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.XmlMarkupTool;
import de.uka.ipd.idaho.xm.xpedite.GoldenGateXpedite;
import de.uka.ipd.idaho.xm.xpedite.plugins.ClickActionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.KeystrokeActionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.ReactionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.SelectionActionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentDropHandler;
import de.uka.ipd.idaho.xm.xpedite.ui.XmlDocumentDisplay;
import de.uka.ipd.idaho.xm.xpedite.ui.XmlUserInterfaceUtils;

/**
 * Panel for displaying and editing an XML Markup document, intended for use
 * in the UI of an application built around a GoldenGATE Xpedite core. This
 * class handles display control, integration with GoldenGATE Xpedite plug-ins
 * (selection actions, reactions, drop handling, and display extensions), and
 * 'Undo' management. The latter reverts atomic actions in corresponding
 * inverse atomic actions whose IDs are the inverse of the IDs of the original
 * actions. The IDs of atomic actions reverting changes made individually are
 * always -1. Further, this class provides mounting points for integration in a
 * window based UI.<br/>
 * By default, the panel contains the document display and its associated view
 * control in the <code>BorderLayout.CENTER</code> and <code>BorderLayout.EAST</code>
 * positions, respectively. Client code, mainly sub classes, may add other
 * components around them if required.
 * 
 * @author sautter
 */
public abstract class XmlDocumentMarkupPanel extends JPanel implements LiteratureConstants, XmlDocumentDisplay {
	final GoldenGATE goldenGate;
	final GoldenGateXpedite ggXpedite;
	final Map createdAnnotColors = Collections.synchronizedMap(new HashMap());
	XmlDocumentMarkupUI parent;
//	
//	final SelectionActionUsageStats saUsageStats;
	
	final XmDocumentMarkupPanel xdmp;
	final JScrollPane xdmpBox;
	Rectangle xdmpViewSize;
	
	static final int fastScrollEnterRatioDenom = 20;
	static final int fastScrollMaintainRatioDenom = 20;
	boolean xdmpBoxInFastScroll = false;
	
	private XmDocumentListener undoRecorder;
//	final LinkedList undoActions = new LinkedList();
	final ArrayList undoActions = new ArrayList();
	private MultipartUndoAction multipartUndoAction = null;
	boolean inUndoAction = false;
	private int undoMenuMaxSize = 10;
	
	private XmDocumentListener reactionTrigger = null;
	boolean xmToolActive = false;
	
	int modCount = 0;
	private int savedModCount = 0;
	
	private static class XmlWrapperCache {
		private HashMap wrappers = new HashMap();
		LazyMutableAnnotation getXmlWrapper(int flags) {
			Integer flagObj = Integer.valueOf(flags);
			WeakReference wrapperRef = ((WeakReference) this.wrappers.get(flagObj));
			if (wrapperRef == null)
				return null;
			LazyMutableAnnotation wrapper = ((LazyMutableAnnotation) wrapperRef.get());
			if (wrapper == null) // reclaimed by GC
				this.wrappers.remove(flagObj);
			return wrapper;
		}
		void addXmlWrapper(LazyMutableAnnotation wrapper) {
			this.wrappers.put(Integer.valueOf(wrapper.getFlags()), new WeakReference(wrapper));
		}
		void invalidateWrappers() {
			ArrayList flagObjs = new ArrayList(this.wrappers.keySet());
			for (int f = 0; f < flagObjs.size(); f++) {
				Integer flagObj = ((Integer) flagObjs.get(f));
				WeakReference wrapperRef = ((WeakReference) this.wrappers.get(flagObj));
				if (wrapperRef == null) {
					this.wrappers.remove(flagObj);
					continue;
				}
				LazyMutableAnnotation wrapper = ((LazyMutableAnnotation) wrapperRef.get());
				if (wrapper == null) // reclaimed by GC
					this.wrappers.remove(flagObj);
				else wrapper.invalidateData();
			}
		}
	}
	
	private int xmlWrapperFlags = XmDocumentRoot.NORMALIZE_CHARACTERS; // TODO load that from display property ... at some point
	private XmlWrapperCache docCache = new XmlWrapperCache();
	private LazyQueriableAnnotation docReadOnly;
	private LazyMutableAnnotation docMutable;
	
	/**
	 * Constructor
	 * @param doc the document to display
	 * @param ggXpedite the GoldenGATE Xpedite core providing editing functionality
	 * @param ggxConfig the GoldenGATE Xpedite configuration
	 */
	protected XmlDocumentMarkupPanel(XmDocument doc, GoldenGateXpedite ggXpedite) {
		super(new BorderLayout(), true);
		this.goldenGate = ggXpedite.getGoldenGATE();
		this.ggXpedite = ggXpedite;
		
		this.xdmp = new XmDocumentEditorPanel(doc);
		
		//	configure document display panel from central defaults (if any)
		for (int p = 0; p < XmDocumentMarkupPanel.displayPropertyNames.length; p++) {
			Object value = UserInterfaceUtils.getDisplayProperty(XmDocumentMarkupPanel.displayPropertyNames[p]);
			if (value == null)
				continue;
			Object defValue = XmDocumentMarkupPanel.getDisplayPropertyDefault(XmDocumentMarkupPanel.displayPropertyNames[p]);
			if (UserInterfaceUtils.equals(value, defValue))
				continue; // no use setting value to default
			try {
				this.xdmp.setDisplayProperty(XmDocumentMarkupPanel.displayPropertyNames[p], value);
			}
			catch (RuntimeException re) {
				System.out.println("Failed to initialize property '" + XmDocumentMarkupPanel.displayPropertyNames[p] + "' from central configuration: " + re.getMessage());
			}
		}
		
		//	mark structural annotations as such
		String[] structAnnotTypes = doc.getAnnotationTypes(false);
		for (int t = 0; t < structAnnotTypes.length; t++)
			UserInterfaceUtils.setDisplayProperty(("annot." + structAnnotTypes[t] + ".showStruct"), Boolean.TRUE);
		
		//	make sure to push changes to colors we created to central settings (random color might well be off, or to close to something else)
		this.xdmp.addDisplayPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent pce) {
				String propName = pce.getPropertyName();
				Object newValue = pce.getNewValue();
				if (createdAnnotColors.containsKey(propName) && (newValue instanceof Color)) {
					Color newColor = ((Color) newValue);
					createdAnnotColors.put(propName, newColor);
					String annotType = propName;
					if (annotType.startsWith("annot."))
						annotType = annotType.substring("annot.".length());
					else return;
					if (annotType.endsWith(".color"))
						annotType = annotType.substring(0, (annotType.length() - ".color".length()));
					else return;
					UserInterfaceUtils.setAnnotationColor(annotType, newColor);
				}
				else if (propName.startsWith("annot.detail.") && propName.endsWith(".mode") && (newValue == XmDocumentMarkupPanel.DISPLAY_MODE_INVISIBLE)) {
					String annotType = propName;
					if (annotType.startsWith("annot.detail."))
						annotType = annotType.substring("annot.detail.".length());
					else return;
					if (annotType.endsWith(".mode"))
						annotType = annotType.substring(0, (annotType.length() - ".mode".length()));
					else return;
					if (pce.getOldValue() == XmDocumentMarkupPanel.DISPLAY_MODE_SHOW_TAGS)
						UserInterfaceUtils.setDisplayProperty(("annot." + annotType + ".showStruct"), Boolean.TRUE);
					else UserInterfaceUtils.setDisplayProperty(("annot." + annotType + ".showStruct"), null);
				}
//				else if (propName.startsWith("annot.struct.") && propName.endsWith(".mode") && (newValue == XmDocumentMarkupPanel.DISPLAY_MODE_INVISIBLE)) {
//					String annotType = propName;
//					if (annotType.startsWith("annot.struct."))
//						annotType = annotType.substring("annot.struct.".length());
//					else return;
//					if (annotType.endsWith(".mode"))
//						annotType = annotType.substring(0, (annotType.length() - ".mode".length()));
//					else return;
//					if (pce.getOldValue() == XmDocumentMarkupPanel.DISPLAY_MODE_SHOW_TAGS)
//						UserInterfaceUtils.setDisplayProperty(("annot." + annotType + ".showStruct"), Boolean.TRUE);
//					else UserInterfaceUtils.setDisplayProperty(("annot." + annotType + ".showStruct"), null);
//				}
				else if (propName.startsWith("annot.")) {}
				else {
					//	TODO_not store font and background settings automatically ???
					//	==> no, got dedicated menu item for that purpose
				}
			}
		});
//		
//		//	get singleton selection action usage stats
//		this.saUsageStats = getSelectionActionUsageStats(this.ggxConfig);
		
		//	prepare recording UNDO actions
		this.undoRecorder = new UndoRecorder();
		this.xdmp.document.addDocumentListener(this.undoRecorder);
		Object undoMenuMaxSizeObj = UserInterfaceUtils.getDisplayProperty("ggXpedite.undoMenuMaxSize");
		if (undoMenuMaxSizeObj instanceof Number)
			this.undoMenuMaxSize = ((Number) undoMenuMaxSizeObj).intValue();
		else UserInterfaceUtils.setDisplayProperty("ggXpedite.undoMenuMaxSize", new Integer(this.undoMenuMaxSize));
		
		//	get reaction providers
		ReactionProvider[] reactionProviders = this.ggXpedite.getReactionProviders();
		System.out.println("Got " + reactionProviders.length + " reaction providers");
		if (reactionProviders.length != 0) {
			this.reactionTrigger = new ReactionTrigger(reactionProviders);
			this.xdmp.document.addDocumentListener(this.reactionTrigger);
		}
//		
//		//	distribute display extension changes to individual editor tabs
//		this.ggXpedite.addDisplayExtensionListener(this);
		
		//	inform any listeners about atomic actions
		this.xdmp.addAtomicActionListener(new AtomicActionNotifier(this.ggXpedite, this.xdmp));
		
		//	get drop handlers
		final XmlDocumentDropHandler[] dropHandlers = this.ggXpedite.getDropHandlers();
		
		//	add drop target if any drop handlers present
		if (dropHandlers.length != 0) {
			DropTarget dropTarget = new DropTarget(this.xdmp, new DropTargetAdapter() {
				public void drop(DropTargetDropEvent dtde) {
					dtde.acceptDrop(dtde.getDropAction()); // we can do this only once, and we have to do it before inspecting data
					if (!this.handleDrop(dtde))
						XmlDocumentMarkupPanel.this.handleDrop(dtde.getTransferable());
				}
				private boolean handleDrop(DropTargetDropEvent dtde) {
					/* TODO find XM object at drop position in panel:
					 * - implement in XML document markup panel
					 * - return annotation or paragraph if drop point on tag panel
					 * - return token if drop point on actual token inside token panel
					 * - no dropping on annotation highlight end caps or spaces
					 */
//					PagePoint dpp = xdmp.pagePointAt(dtde.getLocation().x, dtde.getLocation().y);
//					if (dpp == null)
//						return false;
//					for (int h = 0; h < dropHandlers.length; h++) try {
//						if (dropHandlers[h].handleDrop(xdmp, dpp.page, dpp.x, dpp.y, dtde))
//							return true;
//					}
//					catch (Exception e) {
//						e.printStackTrace(System.out);
//					}
					return false;
				}
			});
			dropTarget.setActive(true);
		}
		
		//	make document view scrollable
		this.xdmpBox = new JScrollPane();
		this.xdmpBox.setViewport(this.xdmp.getViewport());
		this.xdmpViewSize = this.xdmpBox.getViewport().getVisibleRect();
		
		//	adjust primary target of mouse wheel to page alignment, and zoom with Ctrl plus mouse wheel
		this.xdmpBox.setWheelScrollingEnabled(false);
		this.xdmpBox.addMouseWheelListener(new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent mwe) {
				JScrollBar tsb = xdmpBox.getVerticalScrollBar();
				int valueDelta = (tsb.getBlockIncrement() * mwe.getWheelRotation());
				if (valueDelta < 0)
					tsb.setValue(Math.max(tsb.getMinimum(), (tsb.getValue() + valueDelta)));
				else if (valueDelta > 0)
					tsb.setValue(Math.min(tsb.getMaximum(), (tsb.getValue() + valueDelta)));
			}
		});
		
		//	set scroll distances
		final JScrollBar vsb = this.xdmpBox.getVerticalScrollBar();
		vsb.setUnitIncrement(this.xdmpViewSize.height / 10);
		vsb.setBlockIncrement(this.xdmpViewSize.height / 3);
		
		//	track window resizing
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				xdmpViewSize = xdmpBox.getViewport().getViewRect();
				vsb.setUnitIncrement(xdmpViewSize.height / 10);
				vsb.setBlockIncrement(xdmpViewSize.height / 3);
			}
		});
		
		//	make scroll tractable, and enable fast scrolling (disables page rendering when scrolling at high speed)
		vsb.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				setXdmpBoxFastScroll(false);
			}
		});
		vsb.addAdjustmentListener(new AdjustmentListener() {
			private AdjustmentEvent lastAe = null;
			private long lastAeTime = -1;
			public void adjustmentValueChanged(AdjustmentEvent ae) {
//				updateScrollPosition();
				long aeTime = System.currentTimeMillis();
				//	valueIsAdjusting is only true if mouse button held down in scrollbar _outside_ the buttons at the ends (on either side of the know, or on knob proper)
				if (ae.getValueIsAdjusting()) {
					float valueDelta = ((this.lastAe == null) ? ae.getValue() : (ae.getValue() - this.lastAe.getValue()));
					int timeDelta = ((this.lastAe == null) ? 10 : Math.max(10, ((int) (aeTime - this.lastAeTime))));
					setXdmpBoxFastScroll(Math.abs(valueDelta / timeDelta) > Math.max(1, (xdmpViewSize.height / (xdmpBoxInFastScroll ? fastScrollMaintainRatioDenom : fastScrollEnterRatioDenom))));
				}
				else setXdmpBoxFastScroll(false);
				this.lastAe = ae;
				this.lastAeTime = aeTime;
			}
		});
		
		//	assemble UI components
		this.add(this.xdmpBox, BorderLayout.CENTER);
		this.add(this.xdmp.getControlPanel(), BorderLayout.EAST);
	}
	
	void setParent(XmlDocumentMarkupUI parent) {
		this.parent = parent;
	}
//	
//	/**
//	 * Update the scroll position indicator of the surrounding UI, e.g. when a
//	 * markup panel is newly opened, or when it is selected in a multi-document
//	 * UI.
//	 */
//	public void updateScrollPosition() {
//		Rectangle viewRect = this.xdmpBox.getViewport().getViewRect();
//		int viewCenterX = ((int) (viewRect.getMinX() + (viewRect.getWidth() / 2)));
//		int viewCenterY = ((int) (viewRect.getMinY() + (viewRect.getHeight() / 2)));
////		PagePoint viewPagePoint = this.xdmp.pagePointAt(viewCenterX, viewCenterY);
////		ImPage viewPage;
////		if (viewPagePoint == null) // happens on opening, before actually becoming visible
////			viewPage = this.xdmp.document.getPage(this.xdmp.document.getFirstPageId());
////		else viewPage = viewPagePoint.page;
////		Object pageNumber = viewPage.getAttribute(PAGE_NUMBER_ATTRIBUTE);
////		this.scrollPositionChanged("Page " + ((viewPage.pageId - this.xdmp.document.getFirstPageId()) + 1) + " / " + this.xdmp.document.getPageCount() + ((pageNumber == null) ? "" : (" (Nr. " + pageNumber + ")")));
//	}
	
	void setXdmpBoxFastScroll(boolean xbfs) {
		if (this.xdmpBoxInFastScroll == xbfs)
			return;
		if (xbfs) {
//			System.out.println("Entering fast scroll mode");
			this.xdmpBoxInFastScroll = true;
		}
		else {
//			System.out.println("Quitting fast scroll mode");
			this.xdmpBoxInFastScroll = false;
			this.xdmp.validate();
			this.xdmp.repaint();
		}
	}
//	
//	/* (non-Javadoc)
//	 * @see de.uka.ipd.idaho.xm.xpedite.plugins.DisplayExtensionListener#displayExtensionsModified(de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel)
//	 */
//	public void displayExtensionsModified(ImDocumentMarkupPanel xdmp) {
//		if ((xdmp == null) || (xdmp == this.xdmp))
//			this.xdmp.setDisplayExtensionsModified();
//	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getUserInterface()
	 */
	public GoldenGateUI getUserInterface() {
		return this.parent;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#isRootDisplay()
	 */
	public boolean isRootDisplay() {
		return true; // TODO any cases this might not hold true ???
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getParentDisplay()
	 */
	public DocumentDisplay getParentDisplay() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.xm.xpedite.ui.XmlDocumentDisplay#getXmlWrapperFlags()
	 */
	public int getXmlWrapperFlags() {
		return this.xmlWrapperFlags;
	}
	void setXmlWrapperFlags(int flags) {
		this.xmlWrapperFlags = flags;
	}
	
	private boolean ensureXmlWrappers() /* true indicates wrappers already existed */ {
		if (this.docReadOnly != null)
			return true;
		this.docReadOnly = new LazyQueriableAnnotation(this.xdmp.document, this.xmlWrapperFlags);
		this.docMutable = new LazyMutableAnnotation(this.docReadOnly);
		return false;
	}
	void invalidateXmlWrappers() {
		if (this.docReadOnly != null)
			this.docReadOnly.invalidateData();
		else if (this.docMutable != null)
			this.docMutable.invalidateData();
		this.docCache.invalidateWrappers();
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.xm.xpedite.ui.XmlDocumentDisplay#getXmlWrapper(int)
	 */
	public LazyMutableAnnotation getXmlWrapper(int flags) {
//		if (this.ensureXmlWrappers())
//			this.docMutable.setFlags(this.xmlWrapperFlags);
//		if (flags != -1)
//			this.docMutable.setFlags(flags);
//		return this.docMutable;
		if ((flags == -1) || (flags == this.xmlWrapperFlags)) {
			if (this.ensureXmlWrappers())
				this.docMutable.setFlags(this.xmlWrapperFlags);
			return this.docMutable;
		}
		else {
//			return new LazyMutableAnnotation(this.xdmp.document, flags); // TODOne use weak cache for this
			LazyMutableAnnotation wrapper = this.docCache.getXmlWrapper(flags);
			if (wrapper == null) {
				wrapper = new LazyMutableAnnotation(this.xdmp.document, flags);
				this.docCache.addXmlWrapper(wrapper);
			}
			else wrapper.setFlags(flags);
			return wrapper;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#applyAnnotationSource(de.uka.ipd.idaho.goldenGate.plugins.AnnotationSourceManager, de.uka.ipd.idaho.goldenGate.plugins.AnnotationSource, de.uka.ipd.idaho.gamta.Annotation)
	 */
	public void applyAnnotationSource(final AnnotationSourceManager sourceManager, final AnnotationSource annotationSource, final Annotation data) {
		
		//	check if we got anything to work with
		if ((sourceManager == null) && (annotationSource == null))
			return;
		
		//	prepare applying annotation source
//		final QueriableAnnotation qData = ((data == null) ? this.xdmp.document : this.xdmp.document.getAnnotation(data.getAnnotationID()));
//		if (qData == null)
//			return;
		XmObject xmObj = ((data == null) ? null : this.xdmp.document.getObjectByUUID(data.getAnnotationID()));
		final XmAnnotation xmData = ((xmObj instanceof XmAnnotation) ? ((XmAnnotation) xmObj) : null);
		String pmTitle;
		String pmText;
		if (sourceManager == null) {
			pmTitle = ("Apply " + annotationSource.getTypeLabel() + " '" + annotationSource.getName() + "'");
			pmText = ("Please wait while applying " + annotationSource.getTypeLabel() + " '" + annotationSource.getName() + "' ...");
		}
		else {
			pmTitle = ("Apply " + sourceManager.getResourceTypeLabel() + " ...");
			pmText = ("Please select the " + sourceManager.getResourceTypeLabel() + " to apply");
		}
		final ProgressMonitor pm = this.xdmp.getProgressMonitor(pmTitle, pmText, false, false);
		final ProgressMonitorWindow pmw = ((pm instanceof ProgressMonitorWindow) ? ((ProgressMonitorWindow) pm) : null);
		
		//	apply annotation source in dedicated thread
		Thread asThread = new Thread("AnnotationSourceApplicator") {
			public void run() {
				try {
					
					//	wait for splash screen progress monitor to come up (we must not reach the dispose() line before the splash screen even comes up)
					while ((pmw != null) && !pmw.getWindow().isVisible()) try {
						Thread.sleep(10);
					} catch (InterruptedException ie) {}
					
					//	mark as interactive
					Properties params = new Properties();
					params.setProperty(AnnotationSource.INTERACTIVE_PARAMETER, AnnotationSource.INTERACTIVE_PARAMETER);
					
					//	prepare document
					EditableAnnotation eData = ((xmData == null) ? XmlDocumentMarkupPanel.this.getDocumentMutable() : new XmDocumentRoot(xmData, XmlDocumentMarkupPanel.this.getXmlWrapperFlags()));
					AnnotationSourceResult asr;
					
					//	no source manager given, apply annotation source directly
					if (sourceManager == null) {
						Annotation[] annots = annotationSource.createAnnotations(eData, params, pm);
						if (annots == null)
							return;
						AnnotationSourceResultDialog asrd = new AnnotationSourceResultDialog(("Result of " + annotationSource.getTypeLabel() + " '" + annotationSource.getName() + "'"), annots, null, xdmp.document.getAnnotationTypes(true), null, null);
						annots = asrd.getSelectedAnnotations();
						if (annots == null)
							return;
						String annotType = asrd.getSelectedAnnotationType();
						if (annotType == null)
							return;
						for (int a = 0; a < annots.length; a++)
							annots[a].changeTypeTo(annotType);
						asr = new AnnotationSourceResult(annots, annotationSource);
					}
					
					//	have manager apply annotation source
					else asr = sourceManager.applyAnnotationSource(((annotationSource == null) ? null : annotationSource.getName()), params, eData, XmlDocumentMarkupPanel.this, pm);
					
					//	anything to work with?
					if (asr == null)
						return;
					
					//	add annotations under atomic action
					xdmp.startAtomicAction(("Apply " + asr.annotationSource.getTypeLabel() + " '" + asr.annotationSource.getName() + "'"), null, xmData, pm);
					HashSet annotTypes = new HashSet();
					for (int a = 0; a < asr.annotations.length; a++) {
						Annotation annot = eData.addAnnotation(asr.annotations[a].getStartIndex(), asr.annotations[a].getEndIndex(), asr.annotations[a].getType());
						if (annot == null)
							continue;
						annot.copyAttributes(asr.annotations[a]);
						if (annotTypes.add(annot.getType()))
							xdmp.ensureAnnotationsVisible(annot.getType());
					}
					xdmp.finishAtomicAction(pm);
				}
				finally {
					if (pmw != null)
						pmw.close();
				}
			}
		};
		asThread.start();
		
		//	open splash screen progress monitor (this waits)
		if (pmw != null)
			pmw.popUp(true);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#applyDocumentProcessor(de.uka.ipd.idaho.goldenGate.plugins.DocumentProcessorManager, de.uka.ipd.idaho.goldenGate.plugins.DocumentProcessor, de.uka.ipd.idaho.gamta.Annotation)
	 */
	public void applyDocumentProcessor(DocumentProcessorManager processorManager, DocumentProcessor documentProcessor, Annotation data) {
		if ((processorManager == null) && (documentProcessor == null))
			return;
		XmObject xmObj = ((data == null) ? null : this.xdmp.document.getObjectByUUID(data.getAnnotationID()));
		XmAnnotation xmAnnot = ((xmObj instanceof XmAnnotation) ? ((XmAnnotation) xmObj) : null);
		this.applyGenericXmlMarkupTool(new DocumentProcessorMarkupTool(processorManager, documentProcessor), xmAnnot);
	}
	private class DocumentProcessorMarkupTool implements XmlMarkupTool {
		private DocumentProcessorManager processorManager;
		private DocumentProcessor documentProcessor;
		private String label;
		DocumentProcessorMarkupTool(DocumentProcessorManager processorManager, DocumentProcessor documentProcessor) {
			this.processorManager = processorManager;
			this.documentProcessor = documentProcessor;
			if (this.documentProcessor == null)
				this.label = (this.processorManager.getResourceTypeLabel() + " ...");
			else this.label = (this.documentProcessor.getTypeLabel() + " '" + this.documentProcessor.getName() + "'");
		}
		public String getLabel() {
			return this.label;
		}
		public String getTooltip() {
			return null; // this is an ad-hoc wrapper, no tooltip needed
		}
		public String getHelpText() {
			return null; // this is an ad-hoc wrapper, no help needed
		}
		public void process(XmDocument doc, XmAnnotation annot, XmDocumentMarkupPanel xdmp, ProgressMonitor pm) {
			
			//	get target annotation
//			MutableAnnotation data = ((annot == null) ? doc : doc.getMutableAnnotation(annot.getAnnotationID()));
//			if (data == null)
//				return;
//			XmDocumentRoot data = ((annot == null) ? new XmDocumentRoot(xdmp.document, XmlDocumentEditorTab.this.getXmlWrapperFlags()) : new XmDocumentRoot(annot, XmlDocumentEditorTab.this.getXmlWrapperFlags()));
			MutableAnnotation data = ((annot == null) ? XmlDocumentMarkupPanel.this.getDocumentMutable() : new XmDocumentRoot(annot, XmlDocumentMarkupPanel.this.getXmlWrapperFlags()));
			
			//	apply document processor, directly or in manager
			Properties params = new Properties();
			params.setProperty(AnnotationSource.INTERACTIVE_PARAMETER, AnnotationSource.INTERACTIVE_PARAMETER);
			if (this.processorManager == null)
				this.documentProcessor.process(data, params, pm);
			else this.processorManager.applyDocumentProcessor(this.documentProcessor, params, data, XmlDocumentMarkupPanel.this, pm);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.xm.xpedite.ui.XmlDocumentDisplay#applyGenericXmlMarkupTool(de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.XmlMarkupTool, de.uka.ipd.idaho.xm.XmAnnotation)
	 */
	public void applyGenericXmlMarkupTool(XmlMarkupTool xmt, XmAnnotation annot) {
		((XmDocumentEditorPanel) this.xdmp).applyMarkupTool(xmt, annot, false);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getDocumentReadOnly()
	 */
	public QueriableAnnotation getDocumentReadOnly() {
		if (this.ensureXmlWrappers())
			this.docReadOnly.setFlags(this.xmlWrapperFlags);
		return this.docReadOnly;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getDocumentMutable()
	 */
	public MutableAnnotation getDocumentMutable() {
		if (this.ensureXmlWrappers())
			this.docMutable.setFlags(this.xmlWrapperFlags);
		return this.docMutable;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getDocumentId()
	 */
	public String getDocumentId() {
		return this.xdmp.document.docId;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getSourceDocumentClass()
	 */
	public Class getSourceDocumentClass() {
		return XmDocument.class;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getSourceDocument()
	 */
	public Attributed getSourceDocument() {
		return this.xdmp.document;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#areAnnotationsEditable()
	 */
	public boolean areAnnotationsEditable() {
		return true; // annotations are editable in XMF
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#areTokensEditable()
	 */
	public boolean areTokensEditable() {
		return false; // tokens are _not_ editable in XMF
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#areAnnotationsVisible(java.lang.String)
	 */
	public boolean areAnnotationsVisible(String type) {
		return this.xdmp.areAnnotationsVisible(type);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#setAnnotationsVisible(java.lang.String, boolean)
	 */
	public void setAnnotationsVisible(String type, boolean visible) {
		if (visible)
			this.xdmp.ensureAnnotationsVisible(type); // uses preferred highlight strategy
		else {
			int structAnnotPercentage = this.xdmp.document.getStructuralAnnotationPercentage(type);
			if (0 < structAnnotPercentage)
				this.xdmp.setStructuralAnnotationDisplayMode(type, XmDocumentMarkupPanel.DISPLAY_MODE_INVISIBLE);
			if (structAnnotPercentage < 100)
				this.xdmp.setDetailAnnotationDisplayMode(type, XmDocumentMarkupPanel.DISPLAY_MODE_INVISIBLE);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getAnnotationColor(java.lang.String)
	 */
	public Color getAnnotationColor(String type) {
		return this.xdmp.getAnnotationColor(type);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#setAnnotationColor(java.lang.String, java.awt.Color)
	 */
	public void setAnnotationColor(String type, Color color) {
		this.xdmp.setAnnotationColor(type, color);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel#createAnnotationColor(java.lang.String)
	 */
	protected Color createAnnotationColor(String type) {
		Color annotColor = UserInterfaceUtils.getAnnotationColor(type);
		if (annotColor == null) {
			annotColor = UserInterfaceUtils.createAnnotationColor(type);
			this.createdAnnotColors.put(("annot." + type + ".color"), annotColor);
		}
		return annotColor;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel#getPreferredDetailAnnotationShowingMode(java.lang.String)
	 */
	protected Character getPreferredDetailAnnotationShowingMode(String type) {
		if (this.xdmp.document.getStructuralAnnotationPercentage(type) > 50)
			return XmDocumentMarkupPanel.DISPLAY_MODE_SHOW_TAGS;
		else if (UserInterfaceUtils.getDisplayProperty("annot." + type + ".showStruct") == null)
			return XmDocumentMarkupPanel.DISPLAY_MODE_SHOW_HIGHLIGHTS;
		else return XmDocumentMarkupPanel.DISPLAY_MODE_SHOW_TAGS;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getDisplayProperty(java.lang.String)
	 */
	public Object getDisplayProperty(String name) {
		return this.xdmp.getDisplayProperty(name);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#setDisplayProperty(java.lang.String, java.lang.Object)
	 */
	public void setDisplayProperty(String name, Object value) {
		this.xdmp.setDisplayProperty(name, value);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getAnnotationTypes()
	 */
	public String[] getAnnotationTypes() {
		return this.xdmp.document.getAnnotationTypes(true);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#highlightAnnotation(de.uka.ipd.idaho.gamta.Annotation)
	 */
	public void highlightAnnotation(Annotation annotation) {
		XmObject xmObj = this.xdmp.document.getObjectByUUID(annotation.getAnnotationID());
		this.xdmp.setSelectedObject(xmObj, true, null);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay#getProgressMonitor(java.lang.String, java.lang.String, boolean, boolean)
	 */
	public ProgressMonitor getProgressMonitor(String title, String text, boolean supportPauseResume, boolean supportAbort) {
		return new ResourceSplashScreen(getMainWindow(), title, text, supportPauseResume, supportAbort);
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.xm.xpedite.ui.XmlDocumentDisplay#getXmDocument()
	 */
	public XmDocument getXmDocument() {
		return this.xdmp.document;
	}
	
	/* (non-Javadoc)
	 * @see de.uka.ipd.idaho.xm.xpedite.ui.XmlDocumentDisplay#getXmDocumentPanel()
	 */
	public XmDocumentMarkupPanel getXmDocumentPanel() {
		return this.xdmp;
	}
	
	private class XmDocumentEditorPanel extends XmDocumentMarkupPanel implements AtomicActionListener {
		XmDocumentEditorPanel(XmDocument document) {
			super(document);
			this.addAtomicActionListener(this);
		}
		protected Color createAnnotationColor(String type) {
			return XmlDocumentMarkupPanel.this.createAnnotationColor(type);
		}
		protected Character getPreferredDetailAnnotationShowingMode(String type) {
			return XmlDocumentMarkupPanel.this.getPreferredDetailAnnotationShowingMode(type);
		}
		public void atomicActionStarted(long id, String label, XmlMarkupTool xmt, XmAnnotation annot, ProgressMonitor pm) {
			if (inUndoAction)
				return; // no 'Undo' recording on 'Undo' ... TODO maybe use this for 'Redo' ...
			startMultipartUndoAction(id, label);
		}
		public void atomicActionFinishing(long id, ProgressMonitor pm) { /* no follow-up actions from our end */ }
		public void atomicActionFinished(long id, ProgressMonitor pm) {
			if (inUndoAction)
				return; // no 'Undo' recording on 'Undo' ... TODO maybe use this for 'Redo' ...
			finishMultipartUndoAction();
		}
		protected SelectionAction[] getActions(TokenSelection ts) {
			ArrayList actions = new ArrayList(Arrays.asList(super.getActions(ts)));
			SelectionActionProvider[] saps = XmlDocumentMarkupPanel.this.ggXpedite.getSelectionActionProviders();
			for (int p = 0; p < saps.length; p++) try {
				SelectionAction[] sas = saps[p].getActions(ts, this, XmlDocumentMarkupPanel.this);
				if ((sas != null) && (sas.length != 0)) {
					if (actions.size() != 0)
						actions.add(SelectionAction.SEPARATOR);
					actions.addAll(Arrays.asList(sas));
				}
			}
			catch (Exception e) {
				System.out.println("Error getting actions for word selection: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			return ((SelectionAction[]) actions.toArray(new SelectionAction[actions.size()]));
		}
		protected SelectionAction[] getActions(TagSelection ts) {
			ArrayList actions = new ArrayList(Arrays.asList(super.getActions(ts)));
			SelectionActionProvider[] saps = XmlDocumentMarkupPanel.this.ggXpedite.getSelectionActionProviders();
			for (int p = 0; p < saps.length; p++) try {
				SelectionAction[] sas = saps[p].getActions(ts, this, XmlDocumentMarkupPanel.this);
				if ((sas != null) && (sas.length != 0)) {
					if (actions.size() != 0)
						actions.add(SelectionAction.SEPARATOR);
					actions.addAll(Arrays.asList(sas));
				}
			}
			catch (Exception e) {
				System.out.println("Error getting actions for word selection: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			if (ts.isStartTagSelection())
				this.addAdvancedAction(actions, ts.getAnnotation());
			return ((SelectionAction[]) actions.toArray(new SelectionAction[actions.size()]));
		}
		protected SelectionAction[] getActions(PointSelection ps, MouseEvent me) {
			ArrayList actions = new ArrayList(Arrays.asList(super.getActions(ps, me)));
			SelectionActionProvider[] saps = XmlDocumentMarkupPanel.this.ggXpedite.getSelectionActionProviders();
			for (int p = 0; p < saps.length; p++) try {
				SelectionAction[] sas = saps[p].getActions(ps, me, this, XmlDocumentMarkupPanel.this);
				if ((sas != null) && (sas.length != 0)) {
					if (actions.size() != 0)
						actions.add(SelectionAction.SEPARATOR);
					actions.addAll(Arrays.asList(sas));
				}
			}
			catch (Exception e) {
				System.out.println("Error getting actions for point selection: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			if (ps.type == PointSelection.POINT_TYPE_ANNOTATION_START_TAG)
				this.addAdvancedAction(actions, ps.getAnnotation());
			return ((SelectionAction[]) actions.toArray(new SelectionAction[actions.size()]));
		}
		private void addAdvancedAction(ArrayList actions, XmAnnotation xmSelection) {
			if (xmSelection == null)
				return;
			Annotation selection = new LazyAnnotation(xmSelection, XmlDocumentMarkupPanel.this.getXmlWrapperFlags());
			SelectionAction asa = XmlUserInterfaceUtils.createAdvancedSelectionAction(XmlDocumentMarkupPanel.this.goldenGate, "ggXpedite", XmlDocumentMarkupPanel.this, selection);
			if (asa != null)
				actions.add(asa);
		}
		protected JMenuItem getContextMenuItemFor(SelectionAction action) {
			return XmlUserInterfaceUtils.styleContextMenuItem(super.getContextMenuItemFor(action), action);
		}
		protected boolean[] markAdvancedSelectionActions(SelectionAction[] sas) {
//			return XmlDocumentMarkupPanel.this.saUsageStats.markAdvancedSelectionActions(sas);
			return getSelectionActionUsageStats().markAdvancedSelectionActions(sas);
		}
		protected void selectionActionPerformed(SelectionAction sa) {
//			XmlDocumentMarkupPanel.this.saUsageStats.selectionActionUsed(sa);
			getSelectionActionUsageStats().selectionActionUsed(sa);
		}
		protected String getAttributeEditorAnnotationValue(XmAnnotation annot) {
			return XmlUserInterfaceUtils.getAnnotationLabelValue(annot);
		}
		protected InstantAction[] getClickActions(PointSelection ps, MouseEvent me) {
			ArrayList actions = new ArrayList(Arrays.asList(super.getClickActions(ps, me)));
			ClickActionProvider[] caps = XmlDocumentMarkupPanel.this.ggXpedite.getClickActionProviders();
			for (int p = 0; p < caps.length; p++) try {
				InstantAction[] csas = caps[p].getClickActions(ps, me, this, XmlDocumentMarkupPanel.this);
				if ((csas != null) && (csas.length != 0))
					actions.addAll(Arrays.asList(csas));
			}
			catch (Exception e) {
				System.out.println("Error getting actions for non-selection mouse click: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			return ((InstantAction[]) actions.toArray(new InstantAction[actions.size()]));
		}
		protected InstantAction[] getKeystrokeActions(TokenSelection ts, KeyEvent ke) {
			ArrayList actions = new ArrayList(Arrays.asList(super.getKeystrokeActions(ts, ke)));
			KeystrokeActionProvider[] kaps = XmlDocumentMarkupPanel.this.ggXpedite.getKeystrokeActionProviders();
			for (int p = 0; p < kaps.length; p++) try {
				InstantAction[] kas = kaps[p].getKeystrokeActions(ts, ke, this, XmlDocumentMarkupPanel.this);
				if ((kas != null) && (kas.length != 0))
					actions.addAll(Arrays.asList(kas));
			}
			catch (Exception e) {
				System.out.println("Error getting actions for token selection keystroke: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			return ((InstantAction[]) actions.toArray(new InstantAction[actions.size()]));
		}
		protected InstantAction[] getKeystrokeActions(TagSelection ts, KeyEvent ke) {
			ArrayList actions = new ArrayList(Arrays.asList(super.getKeystrokeActions(ts, ke)));
			KeystrokeActionProvider[] kaps = XmlDocumentMarkupPanel.this.ggXpedite.getKeystrokeActionProviders();
			for (int p = 0; p < kaps.length; p++) try {
				InstantAction[] kas = kaps[p].getKeystrokeActions(ts, ke, this, XmlDocumentMarkupPanel.this);
				if ((kas != null) && (kas.length != 0))
					actions.addAll(Arrays.asList(kas));
			}
			catch (Exception e) {
				System.out.println("Error getting actions for tag selection keystroke: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			return ((InstantAction[]) actions.toArray(new InstantAction[actions.size()]));
		}
		protected InstantAction[] getKeystrokeActions(PointSelection leftPs, PointSelection rightPs, KeyEvent ke) {
			ArrayList actions = new ArrayList(Arrays.asList(super.getKeystrokeActions(leftPs, rightPs, ke)));
			KeystrokeActionProvider[] kaps = XmlDocumentMarkupPanel.this.ggXpedite.getKeystrokeActionProviders();
			for (int p = 0; p < kaps.length; p++) try {
				InstantAction[] kas = kaps[p].getKeystrokeActions(leftPs, rightPs, ke, this, XmlDocumentMarkupPanel.this);
				if ((kas != null) && (kas.length != 0))
					actions.addAll(Arrays.asList(kas));
			}
			catch (Exception e) {
				System.out.println("Error getting actions for non-selection keystroke: " + e.getMessage());
				e.printStackTrace(System.out);
			}
			return ((InstantAction[]) actions.toArray(new InstantAction[actions.size()]));
		}
//		protected DisplayExtensionGraphics[] getDisplayExtensionGraphics(ImPage page) {
//			ArrayList degs = new ArrayList();
//			DisplayExtensionProvider[] deps = XmlDocumentMarkupPanel.this.ggXpedite.getDisplayExtensionProviders();
//			for (int p = 0; p < deps.length; p++) try {
//				DisplayExtension[] des = deps[p].getDisplayExtensions();
//				if (des == null)
//					continue;
//				for (int e = 0; e < des.length; e++) {
//					if (des[e].isActive())
//						degs.addAll(Arrays.asList(des[e].getExtensionGraphics(page, this)));
//				}
//			}
//			catch (Exception e) {
//				System.out.println("Error getting display extension graphics: " + e.getMessage());
//				e.printStackTrace(System.out);
//			}
//			return ((DisplayExtensionGraphics[]) degs.toArray(new DisplayExtensionGraphics[degs.size()]));
//		}
		public ProgressMonitor getProgressMonitor(String title, String text, boolean supportPauseResume, boolean supportAbort) {
			return XmlDocumentMarkupPanel.this.getProgressMonitor(title, text, supportPauseResume, supportAbort);
		}
//		public boolean setDisplayOverlay(DisplayOverlay overlay, int pageId) {
//			if (!super.setDisplayOverlay(overlay, pageId))
//				return false;
//			
//			//	scroll to show this thing
//			Point ol = overlay.getOnPageLocation();
//			Dimension os = overlay.getOnPageSize();
//			int pid = overlay.getPageId();
//			
//			//	get position of overlay, and compare to current view
//			Rectangle vpPos = xdmpBox.getViewport().getViewRect();
//			Rectangle oPos = this.getPosition(new BoundingBox(ol.x, (ol.x + os.width), ol.y, (ol.y + os.height)), pid);
//			
//			//	scroll selection to view if required (moving near center)
//			if (!vpPos.contains(oPos)) {
////				xdmpBox.getViewport().scrollRectToVisible(wsPos); // DOESN'T SEEM TO WORK AS SUPPOSED TO, FOR WHATEVER REASON
//				int vx;
//				if ((vpPos.x <= oPos.x) && ((vpPos.x + vpPos.width) >= (oPos.x + oPos.width))) // selection in bounds horizontally, no need for scrolling
//					vx = vpPos.x;
//				else /* center selection in viewport */ {
//					int ocx = (oPos.x + (oPos.width / 2));
//					vx = (ocx - (vpPos.width / 2));
//					if (vx < 0)
//						vx = 0;
//				}
//				int vy;
//				if ((vpPos.y <= oPos.y) && ((vpPos.y + vpPos.height) >= (oPos.y + oPos.height))) // selection in bounds vertically, no need for scrolling
//					vy = vpPos.y;
//				else /* center selection in viewport */ {
//					int ocy = (oPos.y + (oPos.height / 2));
//					vy = (ocy - (vpPos.height / 2));
//					if (vy < 0)
//						vy = 0;
//				}
//				xdmpBox.getViewport().setViewPosition(new Point(vx, vy));
//			}
//			
//			//	pass on super class success
//			return true;
//		}
//		public boolean setWordSelection(ImWord startWord, ImWord endWord) {
//			if (!super.setWordSelection(startWord, endWord))
//				return false;
//			
//			//	get position of word selection, and compare to current view
//			Rectangle vpPos = xdmpBox.getViewport().getViewRect();
//			Rectangle swPos = this.getPosition(startWord);
//			if (swPos == null)
//				return true;
//			Rectangle wsPos;
//			if ((endWord == null) || (endWord == startWord))
//				wsPos = swPos;
//			else {
//				Rectangle ewPos = this.getPosition(endWord);
//				
//				//	word selection doesn't fit view vertically, use start word
//				if (vpPos.height < (ewPos.y + ewPos.height - swPos.y))
//					wsPos = swPos;
//				
//				//	word selection doesn't fit view horizontally, use start word
//				else if (vpPos.width < (Math.max((swPos.x + swPos.width), (ewPos.x + ewPos.width)) - Math.min(swPos.x, ewPos.x)))
//					wsPos = swPos;
//				
//				//	word selection fits view
//				else wsPos = swPos.union(ewPos);
//			}
//			
//			//	scroll selection to view if required (moving near center)
//			this.ensorePositionVisible(vpPos, wsPos);
//			
//			//	pass on super class success
//			return true;
//		}
//		public boolean setBoxSelection(int pageId, BoundingBox box) {
//			if (!super.setBoxSelection(pageId, box))
//				return false;
//			
//			//	get position of word selection, and compare to current view
//			Rectangle vpPos = xdmpBox.getViewport().getViewRect();
//			Rectangle boxPos = this.getPosition(box, pageId);
//			if (boxPos == null)
//				return true;
//			Rectangle bsPos = new Rectangle(boxPos);
//			
//			//	box selection doesn't fit view vertically, reduce height
//			if (vpPos.height < bsPos.height)
//				bsPos.height = vpPos.height;
//			
//			//	box selection doesn't fit view horizontally, reduce width
//			if (vpPos.width < bsPos.width)
//				bsPos.width = vpPos.width;
//			
//			//	scroll selection to view if required (moving near center)
//			this.ensorePositionVisible(vpPos, bsPos);
//			
//			//	pass on super class success
//			return true;
//		}
//		private void ensorePositionVisible(Rectangle vpPos, Rectangle visPos) {
//			if (vpPos.contains(visPos))
//				return;
////			xdmpBox.getViewport().scrollRectToVisible(wsPos); // DOESN'T SEEM TO WORK AS SUPPOSED TO, FOR WHATEVER REASON
//			
//			//	compute target X coordinate
//			int vx;
//			if ((vpPos.x <= visPos.x) && ((vpPos.x + vpPos.width) >= (visPos.x + visPos.width))) // selection in bounds horizontally, no need for scrolling
//				vx = vpPos.x;
//			else /* center selection in viewport */ {
//				int wscx = (visPos.x + (visPos.width / 2));
//				vx = (wscx - (vpPos.width / 2));
//				if (vpPos.x < vx) // scrolling right, don't go all that far
//					vx -= (vpPos.width / 4);
//				else if (vpPos.x > vx) // scrolling left, don't go all that far
//					vx += (vpPos.width / 4);
//				if (vx < 0)
//					vx = 0;
//			}
//			
//			//	compute target Y coordinate
//			int vy;
//			if ((vpPos.y <= visPos.y) && ((vpPos.y + vpPos.height) >= (visPos.y + visPos.height))) // selection in bounds vertically, no need for scrolling
//				vy = vpPos.y;
//			else /* center selection in viewport */ {
//				int wscy = (visPos.y + (visPos.height / 2));
//				vy = (wscy - (vpPos.height / 2));
//				if (vpPos.y < vy) // scrolling down, don't go all that far
//					vy -= (vpPos.height / 4);
//				else if (vpPos.y > vy) // scrolling up, don't go all that far
//					vy += (vpPos.height / 4);
//				if (vy < 0)
//					vy = 0;
//			}
//			
//			//	perform scroll
//			xdmpBox.getViewport().setViewPosition(new Point(vx, vy));
//		}
//		public void setPageVisible(int pageId, boolean pv) {
//			if (pv == this.isPageVisible(pageId))
//				return;
//			super.setPageVisible(pageId, pv);
//			XmlDocumentMarkupPanel.this.validate();
//			XmlDocumentMarkupPanel.this.repaint();
//		}
//		public void setPagesVisible(int fromPageId, int toPageId, boolean pv) {
//			boolean pageVisibilityUnchanged = true;
//			for (int p = fromPageId; p <= toPageId; p++)
//				if (pv != this.isPageVisible(p)) {
//					pageVisibilityUnchanged = false;
//					break;
//				}
//			if (pageVisibilityUnchanged)
//				return;
//			super.setPagesVisible(fromPageId, toPageId, pv);
//			XmlDocumentMarkupPanel.this.validate();
//			XmlDocumentMarkupPanel.this.repaint();
//		}
//		public void setVisiblePages(int[] visiblePageIDs) {
//			boolean pageVisibilityUnchanged = true;
//			HashSet visiblePageIdSet = new HashSet();
//			for (int i = 0; i < visiblePageIDs.length; i++)
//				visiblePageIdSet.add(new Integer(visiblePageIDs[i]));
//			for (int p = 0; p < this.document.getPageCount(); p++)
//				if (this.isPageVisible(p) != visiblePageIdSet.contains(new Integer(p))) {
//					pageVisibilityUnchanged = false;
//					break;
//				}
//			if (pageVisibilityUnchanged)
//				return;
//			super.setVisiblePages(visiblePageIDs);
//			XmlDocumentMarkupPanel.this.validate();
//			XmlDocumentMarkupPanel.this.repaint();
//		}
//		public ImPage[] getVisiblePages() {
//			ImPage[] sPages = super.getVisiblePages();
//			Rectangle vpPos = xdmpBox.getViewport().getViewRect();
//			ArrayList vPages = new ArrayList();
//			for (int p = 0; p < sPages.length; p++) {
//				Rectangle pPos = this.getPosition(sPages[p]);
//				if (pPos == null)
//					continue;
//				if (vpPos.intersects(pPos))
//					vPages.add(sPages[p]);
//			}
//			return ((ImPage[]) vPages.toArray(new ImPage[vPages.size()]));
//		}
//		public void setSideBySidePages(int sbsp) {
//			if (sbsp == this.getSideBySidePages())
//				return;
//			super.setSideBySidePages(sbsp);
//			XmlDocumentMarkupPanel.this.validate();
//			XmlDocumentMarkupPanel.this.repaint();
//		}
		public void applyMarkupTool(XmlMarkupTool xmt, XmAnnotation annot) {
			this.applyMarkupTool(xmt, annot, true);
		}
		void applyMarkupTool(XmlMarkupTool xmt, XmAnnotation annot, boolean isNativeXmMarkupTool) {
			if (isNativeXmMarkupTool)
				invalidateXmlWrappers(); // no use tagging along any wrappers, good chance they break at some point anyway
			try {
				xmToolActive = true;
				super.applyMarkupTool(xmt, annot);
			}
			finally {
				xmToolActive = false;
			}
		}
		public void paint(Graphics graphics) {
			if (xdmpBoxInFastScroll)
				return;
			super.paint(graphics);
		}
		public void validate() {
			if (xdmpBoxInFastScroll)
				return;
			super.validate();
		}
		public void repaint() {
			if (xdmpBoxInFastScroll)
				return;
			super.repaint();
		}
	}
	
	private class UndoRecorder implements XmDocumentListener {
		public void typeChanged(final XmObject object, final String oldType) {
			if (inUndoAction)
				return;
			addUndoAction(new UndoAction(("Change Object Type to '" + object.getType() + "'"), XmlDocumentMarkupPanel.this) {
				void doExecute() {
					object.setType(oldType);
				}
//				int doExecute() {
//					object.setType(oldType);
//					return 1;
//				}
			});
		}
//		public void regionAdded(final ImRegion region) {
//			if (inUndoAction)
//				return;
//			addUndoAction(new UndoAction(("Add '" + region.getType() + "' Region"), XmlDocumentMarkupPanel.this) {
//				void doExecute() {
//					xdmp.document.getPage(region.pageId).removeRegion(region);
//				}
////				int doExecute() {
////					xdmp.document.getPage(region.pageId).removeRegion(region);
////					return 1;
////				}
//			});
//		}
//		public void regionRemoved(final ImRegion region) {
//			if (inUndoAction)
//				return;
//			if (region instanceof ImWord)
//				addUndoAction(new UndoAction(("Remove Word '" + region.getAttribute(ImWord.STRING_ATTRIBUTE) + "'"), XmlDocumentMarkupPanel.this) {
//					void doExecute() {
//						xdmp.document.getPage(region.pageId).addWord((ImWord) region);
//					}
////					int doExecute() {
////						xdmp.document.getPage(region.pageId).addWord((ImWord) region);
////						return 1;
////					}
//				});
//			else addUndoAction(new UndoAction(("Remove '" + region.getType() + "' Region"), XmlDocumentMarkupPanel.this) {
//				void doExecute() {
//					xdmp.document.getPage(region.pageId).addRegion(region);
//				}
////				int doExecute() {
////					xdmp.document.getPage(region.pageId).addRegion(region);
////					return 1;
////				}
//			});
//		}
		public void attributeChanged(final XmObject object, final String attributeName, final Object oldValue) {
			if (inUndoAction)
				return;
			if (oldValue == null)
				addUndoAction(new UndoAction(("Add " + attributeName + " Attribute to " + object.getType()), XmlDocumentMarkupPanel.this) {
					void doExecute() {
						object.setAttribute(attributeName, oldValue); // we need to set here instead of removing, as some objects have built-in special attributes
					}
				});
			else if (object.getAttribute(attributeName) == null)
				addUndoAction(new UndoAction(("Remove '" + attributeName + "' Attribute from " + object.getType()), XmlDocumentMarkupPanel.this) {
					void doExecute() {
						object.setAttribute(attributeName, oldValue);
					}
				});
			else addUndoAction(new UndoAction(("Change '" + attributeName + "' Attribute of " + object.getType() + " to '" + object.getAttribute(attributeName).toString() + "'"), XmlDocumentMarkupPanel.this) {
				void doExecute() {
					object.setAttribute(attributeName, oldValue);
				}
			});
		}
		public void supplementChanged(final String supplementId, final XmSupplement oldValue) {
			if (inUndoAction)
				return;
			if (oldValue == null) {
				final XmSupplement newValue = xdmp.document.getSupplement(supplementId);
				addUndoAction(new UndoAction(("Add '" + supplementId + "' Supplement"), XmlDocumentMarkupPanel.this) {
					void doExecute() {
						xdmp.document.removeSupplement(newValue);
					}
//					int doExecute() {
//						xdmp.document.removeSupplement(newValue);
//						return 1;
//					}
				});
			}
			else if (xdmp.document.getSupplement(supplementId) == null)
				addUndoAction(new UndoAction(("Remove '" + supplementId + "' Supplement"), XmlDocumentMarkupPanel.this) {
					void doExecute() {
						xdmp.document.addSupplement(oldValue);
					}
//					int doExecute() {
//						xdmp.document.addSupplement(oldValue);
//						return 1;
//					}
				});
			else addUndoAction(new UndoAction(("Change '" + supplementId + "' Supplemen"), XmlDocumentMarkupPanel.this) {
				void doExecute() {
					xdmp.document.addSupplement(oldValue);
				}
//				int doExecute() {
//					xdmp.document.addSupplement(oldValue);
//					return 1;
//				}
			});
		}
//		public void fontChanged(final String fontName, final ImFont oldValue) {
//			if (inUndoAction)
//				return;
//			if (oldValue == null) {
//				final ImFont newValue = xdmp.document.getFont(fontName);
//				addUndoAction(new UndoAction(("Add Font '" + fontName + "'"), XmlDocumentMarkupPanel.this) {
//					void doExecute() {
//						xdmp.document.removeFont(newValue);
//					}
////					int doExecute() {
////						xdmp.document.removeSupplement(newValue);
////						return 1;
////					}
//				});
//			}
//			else if (xdmp.document.getFont(fontName) == null)
//				addUndoAction(new UndoAction(("Remove Font '" + fontName + "'"), XmlDocumentMarkupPanel.this) {
//					void doExecute() {
//						xdmp.document.addFont(oldValue);
//					}
////					int doExecute() {
////						xdmp.document.addSupplement(oldValue);
////						return 1;
////					}
//				});
//			else addUndoAction(new UndoAction(("Replace Font '" + fontName + "'"), XmlDocumentMarkupPanel.this) {
//				void doExecute() {
//					xdmp.document.addFont(oldValue);
//				}
////				int doExecute() {
////					xdmp.document.addSupplement(oldValue);
////					return 1;
////				}
//			});
//		}
		public void annotationAdded(final XmAnnotation annotation) {
			if (inUndoAction)
				return;
			addUndoAction(new UndoAction(("Add '" + annotation.getType() + "' Annotation"), XmlDocumentMarkupPanel.this) {
				void doExecute() {
//					/* We need to re-get annotation and make our own
//					 * comparison, as removing and re-adding thwarts
//					 * this simple approach */
//					ImAnnotation[] annots = annotation.getDocument().getAnnotations(annotation.getFirstWord(), null);
//					for (int a = 0; a < annots.length; a++) {
//						if (!annots[a].getLastWord().getLocalID().equals(annotation.getLastWord().getLocalID()))
//							continue;
//						if (!annots[a].getType().equals(annotation.getType()))
//							continue;
//						xdmp.document.removeAnnotation(annots[a]);
//						break;
//					}
					xdmp.document.removeAnnotation(annotation);
				}
//				int doExecute() {
////					/* We need to re-get annotation and make our own
////					 * comparison, as removing and re-adding thwarts
////					 * this simple approach */
////					ImAnnotation[] annots = annotation.getDocument().getAnnotations(annotation.getFirstWord(), null);
////					for (int a = 0; a < annots.length; a++) {
////						if (!annots[a].getLastWord().getLocalID().equals(annotation.getLastWord().getLocalID()))
////							continue;
////						if (!annots[a].getType().equals(annotation.getType()))
////							continue;
////						xdmp.document.removeAnnotation(annots[a]);
////						break;
////					}
//					xdmp.document.removeAnnotation(annotation);
//					return 1;
//				}
			});
		}
		public void annotationRemoved(final XmAnnotation annotation) {
			if (inUndoAction)
				return;
			addUndoAction(new UndoAction(("Remove '" + annotation.getType() + "' Annotation"), XmlDocumentMarkupPanel.this) {
				void doExecute() {
//					ImAnnotation reAnnot = xdmp.document.addAnnotation(annotation.getFirstWord(), annotation.getLastWord(), annotation.getType());
//					if (reAnnot != null)
//						reAnnot.copyAttributes(annotation);
					xdmp.document.addAnnotation(annotation);
				}
//				int doExecute() {
////					ImAnnotation reAnnot = xdmp.document.addAnnotation(annotation.getFirstWord(), annotation.getLastWord(), annotation.getType());
////					if (reAnnot != null)
////						reAnnot.copyAttributes(annotation);
//					xdmp.document.addAnnotation(annotation);
//					return 1;
//				}
			});
		}
	}
	
	private class ReactionTrigger implements XmDocumentListener {
		private ReactionProvider[] reactionProviders;
		private HashSet inReactionObjects = new HashSet();
		ReactionTrigger(ReactionProvider[] reactionProviders) {
			this.reactionProviders = reactionProviders;
		}
		public void typeChanged(XmObject object, String oldType) {
			if (inUndoAction || xmToolActive || !this.inReactionObjects.add(object))
				return;
			try {
				for (int p = 0; p < this.reactionProviders.length; p++)
					this.reactionProviders[p].typeChanged(object, oldType, xdmp, allowReactionPrompts());
			}
			catch (Throwable t) {
				System.out.println("Error reacting to object type change: " + t.getMessage());
				t.printStackTrace(System.out);
			}
			finally {
				this.inReactionObjects.remove(object);
			}
		}
//		public void regionAdded(ImRegion region) {
//			if (inUndoAction || imToolActive || !this.inReactionObjects.add(region))
//				return;
//			try {
//				for (int p = 0; p < this.reactionProviders.length; p++)
//					this.reactionProviders[p].regionAdded(region, xdmp, allowReactionPrompts());
//			}
//			catch (Throwable t) {
//				System.out.println("Error reacting to region addition: " + t.getMessage());
//				t.printStackTrace(System.out);
//			}
//			finally {
//				this.inReactionObjects.remove(region);
//			}
//		}
//		public void regionRemoved(ImRegion region) {
//			if (inUndoAction || imToolActive || !this.inReactionObjects.add(region))
//				return;
//			try {
//				for (int p = 0; p < this.reactionProviders.length; p++)
//					this.reactionProviders[p].regionRemoved(region, xdmp, allowReactionPrompts());
//			}
//			catch (Throwable t) {
//				System.out.println("Error reacting to region removal: " + t.getMessage());
//				t.printStackTrace(System.out);
//			}
//			finally {
//				this.inReactionObjects.remove(region);
//			}
//		}
		public void attributeChanged(XmObject object, String attributeName, Object oldValue) {
			if (inUndoAction || xmToolActive || !this.inReactionObjects.add(object))
				return;
			try {
				for (int p = 0; p < this.reactionProviders.length; p++)
					this.reactionProviders[p].attributeChanged(object, attributeName, oldValue, xdmp, allowReactionPrompts());
			}
			catch (Throwable t) {
				System.out.println("Error reacting to object attribute change: " + t.getMessage());
				t.printStackTrace(System.out);
			}
			finally {
				this.inReactionObjects.remove(object);
			}
		}
		public void supplementChanged(String supplementId, XmSupplement oldValue) {
			//	no reaction triggering for supplement modifications
		}
//		public void fontChanged(String fontName, ImFont oldValue) {
//			//	no reaction triggering for font modifications
//		}
		public void annotationAdded(XmAnnotation annotation) {
			if (inUndoAction || xmToolActive || !this.inReactionObjects.add(annotation))
				return;
			try {
				for (int p = 0; p < this.reactionProviders.length; p++)
					this.reactionProviders[p].annotationAdded(annotation, xdmp, allowReactionPrompts());
			}
			catch (Throwable t) {
				System.out.println("Error reacting to annotation addition: " + t.getMessage());
				t.printStackTrace(System.out);
			}
			finally {
				this.inReactionObjects.remove(annotation);
			}
		}
		public void annotationRemoved(XmAnnotation annotation) {
			if (inUndoAction || xmToolActive || !this.inReactionObjects.add(annotation))
				return;
			try {
				for (int p = 0; p < this.reactionProviders.length; p++)
					this.reactionProviders[p].annotationRemoved(annotation, xdmp, allowReactionPrompts());
			}
			catch (Throwable t) {
				System.out.println("Error reacting to annotation removal: " + t.getMessage());
				t.printStackTrace(System.out);
			}
			finally {
				this.inReactionObjects.remove(annotation);
			}
		}
	}
	
	private class AtomicActionNotifier implements AtomicActionListener {
		GoldenGateXpedite ggXpedite;
		XmDocumentMarkupPanel xdmp;
		AtomicActionNotifier(GoldenGateXpedite ggXpedite, XmDocumentMarkupPanel xdmp) {
			this.ggXpedite = ggXpedite;
			this.xdmp = xdmp;
		}
		public void atomicActionStarted(long id, String label, XmlMarkupTool xmt, XmAnnotation annot, ProgressMonitor pm) {
			this.ggXpedite.notifyAtomicActionStarted(id, label, xmt, annot, this.xdmp, pm);
		}
		public void atomicActionFinishing(long id, ProgressMonitor pm) {
			this.ggXpedite.notifyAtomicActionFinishing(id, this.xdmp, pm);
		}
		public void atomicActionFinished(long id, ProgressMonitor pm) {
			this.ggXpedite.notifyAtomicActionFinished(id, this.xdmp, pm);
		}
	}
	
	/**
	 * Scroll up by one page, e.g. in reaction to a press of the 'Page Up'
	 * button.
	 */
	public void scrollUp() {
		JScrollBar vsb = this.xdmpBox.getVerticalScrollBar();
		vsb.setValue(Math.max(vsb.getMinimum(), (vsb.getValue() - this.xdmpBox.getViewport().getViewRect().height)));
	}
	
	/**
	 * Scroll down by one page, e.g. in reaction to a press of the 'Page Down'
	 * button.
	 */
	public void scrollDown() {
		JScrollBar vsb = this.xdmpBox.getVerticalScrollBar();
		vsb.setValue(Math.min(vsb.getMaximum(), (vsb.getValue() + this.xdmpBox.getViewport().getViewRect().height)));
	}
	
	/**
	 * Handle a change to the scroll position of this markup panel, e.g. in a
	 * status bar. The argument scroll position label takes the form "Page X
	 * of Y", followed by the page number if the latter is available. This
	 * default implementation does nothing. Sub classes are welcome to overwrite
	 * it as needed.
	 * @param posLabel the scroll position label
	 */
	protected void scrollPositionChanged(String posLabel) {}
//	
//	/**
//	 * Set the rendering DPI. This method also affects the zoom percentage;
//	 * namely, this method sets the zoom percentage to <code>renderingDpi
//	 * * 100 / 96</code>.
//	 * @param renderingDpi the new rendering DPI
//	 */
//	public void setRenderingDpi(int renderingDpi) {
//		int oldRenderingDpi = this.xdmp.getRenderingDpi();
//		if (renderingDpi == oldRenderingDpi)
//			return;
//		
//		//	we're not visible, just set resolution and we're done
//		if (!this.isVisible()) {
//			this.xdmp.setRenderingDpi(renderingDpi);
//			this.validate();
//			this.repaint();
//			return;
//		}
//		
//		//	get current view center point
//		Dimension viewSize = this.xdmpBox.getViewport().getExtentSize();
//		Point oldViewPos = this.xdmpBox.getViewport().getViewPosition();
//		Point oldViewCenter = new Point((oldViewPos.x + (viewSize.width / 2)), (oldViewPos.y + (viewSize.height / 2)));
//		
//		//	find page panel at view center for use as anchor (will be null before we're added to UI)
//		Component centerComp = this.xdmp.getComponentAt(oldViewCenter);
//		if (centerComp == null) {
//			this.xdmp.setRenderingDpi(renderingDpi);
//			this.validate();
//			this.repaint();
//			return;
//		}
//		
//		//	seek page panel if view center in main document panel proper
//		if ((this.xdmp.getSideBySidePages() < 1) && (centerComp.getLocation().x < 0)) /* horizontal page arrangement */ {
//			Point seekViewCenter = new Point(oldViewCenter.x, oldViewCenter.y);
//			while ((centerComp.getLocation().x < 0) && (oldViewPos.x < seekViewCenter.x)) /* this is the markup panel proper in its parent scrolling viewport */ {
//				seekViewCenter.x--;
//				centerComp = this.xdmp.getComponentAt(seekViewCenter);
//			}
//		}
//		else if ((this.xdmp.getSideBySidePages() > 0) && (centerComp.getLocation().y < 0)) /* vertical page arrangement */ {
//			Point seekViewCenter = new Point(oldViewCenter.x, oldViewCenter.y);
//			while ((centerComp.getLocation().y < 0) && (oldViewPos.y < seekViewCenter.y)) /* this is the markup panel proper in its parent scrolling viewport */ {
//				seekViewCenter.y--;
//				centerComp = this.xdmp.getComponentAt(seekViewCenter);
//			}
//		}
//		
//		//	compute position relative to anchor component
//		Point oldCenterCompPos = centerComp.getLocation();
//		Point oldRelViewCenter = new Point((oldViewCenter.x - oldCenterCompPos.x), (oldViewCenter.y - oldCenterCompPos.y));
//		
//		//	change zoom level
//		this.xdmp.setRenderingDpi(renderingDpi);
//		this.validate();
//		this.repaint();
//		
//		//	compute zoomed view center from anchor component
//		Point newCenterCompPos = centerComp.getLocation();
//		Point newRelViewCenter = new Point(((oldRelViewCenter.x * renderingDpi) / oldRenderingDpi), ((oldRelViewCenter.y * renderingDpi) / oldRenderingDpi));
//		Point newViewCenter = new Point((newCenterCompPos.x + newRelViewCenter.x), (newCenterCompPos.y + newRelViewCenter.y));
//		Point newViewPos = new Point(Math.max((newViewCenter.x - (viewSize.width / 2)), 0), Math.max((newViewCenter.y - (viewSize.height / 2)), 0));
//		
//		//	adjust scroll position
//		this.xdmpBox.getViewport().setViewPosition(newViewPos);
//	}
//	
//	/**
//	 * Set the number of pages displayed side by side before breaking into a
//	 * new row. If the argument number is less than 1, all pages are lain out
//	 * in one single row left to right.
//	 * @param sbsp the number of pages per row
//	 */
//	public void setSideBySidePages(int sbsp) {
//		int oldSbsp = this.xdmp.getSideBySidePages();
//		if (sbsp == oldSbsp)
//			return;
//		Dimension viewSize = this.xdmpBox.getViewport().getExtentSize();
//		Point viewPos = this.xdmpBox.getViewport().getViewPosition();
//		Point viewCenter = new Point((viewPos.x + (viewSize.width / 2)), (viewPos.y + (viewSize.height / 2)));
//		Component viewCenterPage = this.xdmp.getComponentAt(viewCenter);
//		while (((viewCenterPage == null) || (viewCenterPage == this.xdmp)) && (viewCenter.x > 0) && (viewCenter.y > 0)) {
//			viewCenter = new Point((viewCenter.x - 20), (viewCenter.y - 20));
//			viewCenterPage = this.xdmp.getComponentAt(viewCenter);
//		}
//		this.xdmp.setSideBySidePages(sbsp);
//		this.validate();
//		this.repaint();
//		if (viewCenterPage != null)
//			this.xdmpBox.getViewport().setViewPosition(viewCenterPage.getLocation());
//	}
//	
//	/**
//	 * Open a dialog offering the user to show or hide individual pages.
//	 */
//	public void selectVisiblePages() {
//		
//		//	create selector tiles and compute size
//		ImPage[] pages = this.xdmp.document.getPages();
//		PageSelectorTile[] psts = new PageSelectorTile[pages.length];
//		int ptWidth = 0;
//		int ptHeight = 0;
//		for (int p = 0; p < pages.length; p++) {
//			PageThumbnail pt = this.xdmp.getPageThumbnail(pages[p].pageId);
//			psts[p] = new PageSelectorTile(pt, pages[p].pageId, this.xdmp.isPageVisible(pages[p].pageId));
//			ptWidth = Math.max(ptWidth, pt.getPreferredSize().width);
//			ptHeight = Math.max(ptHeight, pt.getPreferredSize().height);
//		}
//		
//		//	set selector tile size (adding 4 for border width)
//		for (int p = 0; p < psts.length; p++)
//			psts[p].setPreferredSize(new Dimension(((ptWidth * 2) + 4), ((ptHeight * 2) + 4)));
//		
//		//	create dialog
//		final DialogPanel vps = new DialogPanel("Select Visible Pages", true);
//		vps.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//		vps.setSize(vps.getOwner().getSize());
//		vps.setLocationRelativeTo(vps.getOwner());
//		
//		//	compute number of selector tiles that fit side by side
//		int sideBySidePsts = ((vps.getSize().width + 10) / (((ptWidth * 2) + 4) + 10));
//		
//		//	line up selector tiles
//		JPanel pstPanel = new JPanel(new GridBagLayout(), true);
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.insets.left = 5;
//		gbc.insets.right = 5;
//		gbc.insets.top = 5;
//		gbc.insets.bottom = 5;
//		gbc.gridwidth = 1;
//		gbc.gridheight = 1;
//		gbc.weightx = 0;
//		gbc.weighty = 0;
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		for (int p = 0; p < psts.length; p++) {
//			pstPanel.add(psts[p], gbc.clone());
//			gbc.gridx++;
//			if (gbc.gridx == sideBySidePsts) {
//				gbc.gridx = 0;
//				gbc.gridy++;
//			}
//		}
//		gbc.gridwidth = Math.min(psts.length, sideBySidePsts);
//		gbc.weighty = 1;
//		gbc.gridx = 0;
//		gbc.gridy++;
//		pstPanel.add(new JPanel(), gbc.clone());
//		JScrollPane pstPanelBox = new JScrollPane(pstPanel);
//		pstPanelBox.getVerticalScrollBar().setUnitIncrement(50);
//		pstPanelBox.getVerticalScrollBar().setBlockIncrement(50);
//		
//		//	add buttons
//		final boolean[] cancelled = {false};
//		JButton ok = new JButton("OK");
//		ok.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				vps.dispose();
//			}
//		});
//		JButton cancel = new JButton("Cancel");
//		cancel.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
//				cancelled[0] = true;
//				vps.dispose();
//			}
//		});
//		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
//		buttons.add(ok);
//		buttons.add(cancel);
//		
//		//	assemble dialog content
//		vps.add(pstPanelBox, BorderLayout.CENTER);
//		vps.add(buttons, BorderLayout.SOUTH);
//		
//		//	show dialog
//		vps.setVisible(true);
//		
//		//	cancelled
//		if (cancelled[0])
//			return;
//		
//		//	select visible pages
//		int[] visiblePageIDs = new int[psts.length];
//		for (int p = 0; p < psts.length; p++)
//			visiblePageIDs[p] = (psts[p].pageVisible ? psts[p].pageId : -1);
//		this.xdmp.setVisiblePages(visiblePageIDs);
//	}
//	
//	private static class PageSelectorTile extends JPanel {
//		private final PageThumbnail pt;
//		int pageId;
//		boolean pageVisible;
//		PageSelectorTile(PageThumbnail pt, int pageId, boolean pageVisible) {
//			super(new BorderLayout(), true);
//			this.pt = pt;
//			this.pageId = pageId;
//			this.pageVisible = pageVisible;
//			this.setBorder();
//			this.setToolTipText(this.pt.getTooltipText());
//			this.addMouseListener(new MouseAdapter() {
//				public void mouseClicked(MouseEvent me) {
//					togglePageVisible();
//				}
//			});
//		}
//		void setBorder() {
//			this.setBorder(BorderFactory.createLineBorder((this.pageVisible ? Color.DARK_GRAY : Color.LIGHT_GRAY), 2));
//		}
//		public void paint(Graphics g) {
//			super.paint(g);
//			this.pt.paint(g, 2, 2, (this.getWidth()-4), (this.getHeight()-4), this);
//		}
//		void togglePageVisible() {
//			this.pageVisible = !this.pageVisible;
//			this.setBorder();
//			this.validate();
//			this.repaint();
//		}
//	}
	
	private StackTraceElement[] lastAtomicActionStarter = null;
	private StackTraceElement[] lastAtomicActionFinisher = null;
	private void printStackTrace(StackTraceElement[] stackTrace) {
		if (stackTrace == null)
			return;
		for (int e = 0; e < stackTrace.length; e++)
			System.err.println("  at " + stackTrace[e].toString());
	}
	
	void addUndoAction(UndoAction ua) {
		if (this.inUndoAction)
			return;
		if (this.multipartUndoAction == null) {
			this.modCount++;
//			this.undoActions.addFirst(ua);
			this.undoActions.add(ua);
			System.err.println("NO GOOD: Got UNDO outside atomic action, last one finished from");
			this.printStackTrace(this.lastAtomicActionFinisher);
			this.updateUndoMenu();
		}
		else this.multipartUndoAction.addUndoAction(ua);
	}
	
	void startMultipartUndoAction(long id, String label) {
		if (this.multipartUndoAction != null) {
			System.err.println("NO GOOD: Started nested atomic action, running one started from");
			this.printStackTrace(this.lastAtomicActionStarter);
			System.err.println("NO GOOD: Started nested atomic action, call coming from");
			this.printStackTrace(Thread.currentThread().getStackTrace());
		}
		this.multipartUndoAction = new MultipartUndoAction(id, label, this);
		this.lastAtomicActionStarter = Thread.currentThread().getStackTrace();
	}
	
	void finishMultipartUndoAction() {
		if ((this.multipartUndoAction == null) && !this.inUndoAction) {
			System.err.println("NO GOOD: Finishing non-existing atomic action, last one finished from");
			this.printStackTrace(this.lastAtomicActionFinisher);
			System.err.println("NO GOOD: Finishing non-existing atomic action, call coming from");
			this.printStackTrace(Thread.currentThread().getStackTrace());
		}
		if ((this.multipartUndoAction != null) && (this.multipartUndoAction.parts.size() != 0)) {
			this.modCount++;
//			this.undoActions.addFirst(this.multipartUndoAction);
			this.undoActions.add(this.multipartUndoAction);
			this.updateUndoMenu();
		}
		this.multipartUndoAction = null;
		this.lastAtomicActionFinisher = Thread.currentThread().getStackTrace();
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
	 * Update the 'Undo' menu of the surrounding UI, e.g. when a markup panel
	 * is newly opened, or when it is selected in a multi-document UI.
	 */
//	public void updateUndoMenu() {
//		JMenu undoMenu = this.getUndoMenu();
//		if (undoMenu == null)
//			return;
//		undoMenu.removeAll();
//		for (Iterator uait = this.undoActions.iterator(); uait.hasNext();) {
//			final UndoAction ua = ((UndoAction) uait.next());
//			JMenuItem mi = new JMenuItem(ua.label);
//			mi.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent ae) {
//					try {
//						ua.target.inUndoAction = true;
//						long us = System.currentTimeMillis();
//						while (undoActions.size() != 0) {
//							UndoAction eua = ((UndoAction) undoActions.removeFirst());
//							try {
//								if (ua instanceof MultipartUndoAction)
//									ua.target.xdmp.startAtomicAction(((MultipartUndoAction) ua).actionId, "UNDO", null, null, null);
//								else ua.target.xdmp.startAtomicAction(-1, "UNDO", null, null, null);
//								eua.execute();
//							}
//							finally {
//								ua.target.xdmp.endAtomicAction();
//							}
//							if (eua == ua)
//								break;
//						}
//						
//						updateUndoMenu();
//						System.out.println("Executed undo actions in " + (System.currentTimeMillis() - us) + "ms");
//					}
//					finally {
//						ua.target.inUndoAction = false;
//						
//						/* we are on the EDT, so we can repaint right here
//						 * without any risk of incurring a deadlock between
//						 * on synchronized parts of UI or data structures */
//						ua.target.xdmp.validate();
//						ua.target.xdmp.repaint();
//						ua.target.xdmp.validateControlPanel();
//					}
//				}
//			});
//			undoMenu.add(mi);
//			if (undoMenu.getMenuComponentCount() >= 10)
//				break;
//		}
//		undoMenu.setEnabled(this.undoActions.size() != 0);
//	}
	public void updateUndoMenu() {
		System.out.println("XmlDocumentMarkupPanel: updating UNDO menu");
		WindowMenuOwner undoMenuOwner = this.getUndoMenuOwner();
		DynamicWindowMenu undoMenu = this.getUndoMenu();
		if ((undoMenuOwner == null) || (undoMenu == null)) {
			System.out.println(" ==> menu or menu owner is null");
			return;
		}
		undoMenu.clearElements(undoMenuOwner);
//		for (Iterator uait = this.undoActions.iterator(); uait.hasNext();) {
		for (int a = (this.undoActions.size() - 1); a >= 0; a--) {
//			final UndoAction ua = ((UndoAction) uait.next());
			final UndoAction ua = ((UndoAction) this.undoActions.get(a));
			JMenuItem mi = new JMenuItem();
			UserInterfaceUtils.styleDesktopMenuItem(mi, "ggXpedite", "undo.option", ua.label, ("Revert all modifications back to '" + ua.label + "'"), null, null);
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						ua.target.inUndoAction = true;
						long us = System.currentTimeMillis();
						while (undoActions.size() != 0) {
//							UndoAction eua = ((UndoAction) undoActions.removeFirst());
							UndoAction eua = ((UndoAction) undoActions.remove(undoActions.size() - 1));
							try {
								if (ua instanceof MultipartUndoAction)
									ua.target.xdmp.startAtomicAction(((MultipartUndoAction) ua).actionId, "UNDO", null, null, null);
								else ua.target.xdmp.startAtomicAction(-1, "UNDO", null, null, null);
								eua.execute();
							}
							finally {
								ua.target.xdmp.endAtomicAction();
							}
							if (eua == ua)
								break;
						}
						
						updateUndoMenu();
						System.out.println("Executed undo actions in " + (System.currentTimeMillis() - us) + "ms");
					}
					finally {
						ua.target.inUndoAction = false;
						
						/* we are on the EDT, so we can repaint right here
						 * without any risk of incurring a deadlock between
						 * on synchronized parts of UI or data structures */
						ua.target.xdmp.validate();
						ua.target.xdmp.repaint();
						ua.target.xdmp.validateControlPanel();
					}
				}
			});
			undoMenu.addDesktopElement(mi, undoMenuOwner);
			if (undoMenuMaxSize <= undoMenu.itemCount())
				break;
		}
		System.out.println(" ==> added " + undoMenu.itemCount() + " items");
//		undoMenu.updateItems(undoMenuOwner);
		undoMenuOwner.updateMenu();
		System.out.println(" ==> menu refresh done");
	}
	
	/**
	 * Provide the owner of the 'Undo' menu integrated in a UI for the markup
	 * panel to show its 'Undo' options in. If this method returns null, UI
	 * based 'Undo' will not be accessible.
	 * @return the owner of the 'Undo' menu of the surrounding UI
	 */
	protected abstract WindowMenuOwner getUndoMenuOwner();
	
	/**
	 * Provide the 'Undo' menu integrated in a UI for the markup panel to show
	 * its 'Undo' options in. If this method returns null, UI based 'Undo' will
	 * not be accessible.
	 * @return the 'Undo' menu of the surrounding UI
	 */
//	protected abstract JMenu getUndoMenu();
	protected abstract DynamicWindowMenu getUndoMenu();
	
	/**
	 * Check whether or not the Image Markup document displayed in this panel
	 * has been modified since the last call to <code>markClean()</code>. This
	 * is mostly to track whether or not the document needs saving.
	 * @return true if the document has been modified
	 */
	public boolean isDirty() {
		return (this.modCount != this.savedModCount);
	}
	
	/**
	 * Mark the Image Markup document displayed in this panel as clean. Client
	 * code will mostly call this method after saving a document to persistent
	 * storage.
	 */
	public void markClean() {
		this.savedModCount = this.modCount;
	}
	
	/**
	 * Handle a drop on the markup panel that did not go to any of the present
	 * drop handlers. This default implementation does nothing. Sub classes are
	 * welcome to overwrite it as needed.
	 * @param dropped the dropped data
	 */
	protected void handleDrop(Transferable dropped) {}
	
	/**
	 * Indicate whether or no reaction providers are allowed to prompt the user
	 * for input.
	 * @return true to allow reactions, false to disallow them
	 */
	protected abstract boolean allowReactionPrompts();
	
	/**
	 * Dispose of the markup panel, cleaning up inner data structures,
	 * unregistering listeners, etc.
	 * @param storeSettings store annotation and region color settings?
	 */
	public void dispose(boolean storeSettings) {
//		this.ggXpedite.removeDisplayExtensionListener(this);
		this.xdmp.document.removeDocumentListener(this.undoRecorder);
		if (this.reactionTrigger != null)
			this.xdmp.document.removeDocumentListener(this.reactionTrigger);
		this.ggXpedite.notifyDocumentClosed(this.xdmp.document.docId);
		this.invalidateXmlWrappers();
		
		if (storeSettings) {
//			Settings annotationColors = this.ggxConfig.getSubset("annotation.color");
//			String[] annotationTypes = this.xdmp.getAnnotationTypes();
//			for (int t = 0; t < annotationTypes.length; t++) {
//				Color ac = this.xdmp.getAnnotationColor(annotationTypes[t]);
//				if (ac != null)
//					annotationColors.setSetting(annotationTypes[t], GoldenGateXpedite.getHex(ac));
//			}
//			//	TODOnot get and store font and background settings
//			Settings layoutObjectColors = this.ggxConfig.getSubset("layoutObject.color");
//			String[] layoutObjectTypes = this.xdmp.getLayoutObjectTypes();
//			for (int t = 0; t < layoutObjectTypes.length; t++) {
//				Color loc = this.xdmp.getLayoutObjectColor(layoutObjectTypes[t]);
//				if (loc != null)
//					layoutObjectColors.setSetting(layoutObjectTypes[t], GoldenGateXpedite.getHex(loc));
//			}
//			Settings textStreamColors = this.ggxConfig.getSubset("textStream.color");
//			String[] textStreamTypes = this.xdmp.getTextStreamTypes();
//			for (int t = 0; t < textStreamTypes.length; t++) {
//				Color tsc = this.xdmp.getTextStreamTypeColor(textStreamTypes[t]);
//				if (tsc != null)
//					textStreamColors.setSetting(textStreamTypes[t], GoldenGateXpedite.getHex(tsc));
//			}
//			this.saUsageStats.storeTo(this.ggxConfig.getSubset("selectionAction"));
		}
	}
	
	private static abstract class UndoAction {
		final String label;
		final XmlDocumentMarkupPanel target;
		final int modCount;
		UndoAction(String label, XmlDocumentMarkupPanel target) {
			this.label = label;
			this.target = target;
			this.modCount = this.target.modCount;
		}
		final void execute() {
			this.doExecute();
			this.target.modCount = this.modCount;
		}
		abstract void doExecute();
	}
	
//	private static class MultipartUndoAction extends UndoAction {
//		final LinkedList parts = new LinkedList();
//		final long actionId;
//		MultipartUndoAction(long id, String label, XmlDocumentMarkupPanel target) {
//			super(label, target);
//			this.actionId = -id;
//		}
//		synchronized void addUndoAction(UndoAction ua) {
//			this.parts.addFirst(ua);
//		}
//		void doExecute() {
//			while (this.parts.size() != 0)
//				((UndoAction) this.parts.removeFirst()).doExecute();
//		}
//	}
	private static class MultipartUndoAction extends UndoAction {
//		final LinkedList parts = new LinkedList();
		final ArrayList parts = new ArrayList();
		final long actionId;
		MultipartUndoAction(long id, String label, XmlDocumentMarkupPanel target) {
			super(label, target);
			this.actionId = -id;
		}
		synchronized void addUndoAction(UndoAction ua) {
//			this.parts.addFirst(ua);
			this.parts.add(ua);
		}
		void doExecute() {
//			while (this.parts.size() != 0)
//				((UndoAction) this.parts.removeFirst()).doExecute();
			for (int p = (this.parts.size() -1); p >= 0; p--)
				((UndoAction) this.parts.get(p)).doExecute();
			this.parts.clear();
		}
	}
	
//	private static SelectionActionUsageStats selectionActionUsageStats = null;
//	private static SelectionActionUsageStats getSelectionActionUsageStats(Settings ggxConfig) {
//		if (selectionActionUsageStats == null) {
//			selectionActionUsageStats = new SelectionActionUsageStats();
//			selectionActionUsageStats.fillFrom(ggxConfig.getSubset("selectionAction"));
//		}
//		return selectionActionUsageStats;
//	}
//	private static class SelectionActionUsageStats extends TreeMap {
//		private static class SelectionActionUsage {
//			int shown = 0;
//			int used = 0;
//			int usedLast = 0;
//			SelectionActionUsage() {}
//		}
//		
//		private int isSaAdvancedPivotIndex = 10;
//		private int saUseCounter = 1;
//		
//		private SelectionActionUsage getSelectionActionUsage(String saName) {
//			SelectionActionUsage saUsage = ((SelectionActionUsage) this.get(saName));
//			if (saUsage == null) {
//				saUsage = new SelectionActionUsage();
//				this.put(saName, saUsage);
//			}
//			return saUsage;
//		}
//		
//		boolean[] markAdvancedSelectionActions(SelectionAction[] sas) {
//			float[] isSaAdvancedScoresBySa = new float[sas.length];
//			float[] isSaAdvancedScoresByVal = new float[sas.length];
//			for (int a = 0; a < sas.length; a++) {
//				float isSaAdvancedScore = 0;
//				if (sas[a] != SelectionAction.SEPARATOR) {
//					SelectionActionUsage saUsage = this.getSelectionActionUsage(sas[a].name);
//					saUsage.shown++;
//					isSaAdvancedScore += (((float) saUsage.used) / saUsage.shown); // MFU part
//					isSaAdvancedScore += (((float) saUsage.usedLast) / this.saUseCounter); // MRU part
//				}
//				isSaAdvancedScoresBySa[a] = isSaAdvancedScore;
//				isSaAdvancedScoresByVal[a] = isSaAdvancedScore;
//			}
//			
//			Arrays.sort(isSaAdvancedScoresByVal);
//			float isSaAdvancedThreshold = ((sas.length < this.isSaAdvancedPivotIndex) ? 0 : isSaAdvancedScoresByVal[sas.length - this.isSaAdvancedPivotIndex]);
//			
//			boolean[] isSaAdvanced = new boolean[sas.length];
//			for (int a = 0; a < sas.length; a++)
//				isSaAdvanced[a] = (isSaAdvancedScoresBySa[a] < isSaAdvancedThreshold);
//			
//			return isSaAdvanced;
//		}
//		
//		void selectionActionUsed(SelectionAction sa) {
//			SelectionActionUsage saUsage = this.getSelectionActionUsage(sa.name);
//			saUsage.used++;
//			saUsage.usedLast = this.saUseCounter++;
//		}
//		
//		void fillFrom(Settings set) {
//			this.isSaAdvancedPivotIndex = Math.max(1, Integer.parseInt(set.getSetting("isAdvancedPivotIndex", ("" + this.isSaAdvancedPivotIndex))));
//			this.saUseCounter = Math.max(1, Integer.parseInt(set.getSetting("useCounter", "1")));
//			
//			String[] saNames = set.getSubsetPrefixes();
//			for (int n = 0; n < saNames.length; n++) {
//				Settings saUsageSet = set.getSubset(saNames[n]);
//				SelectionActionUsage saUsage = this.getSelectionActionUsage(saNames[n]);
//				saUsage.shown = Integer.parseInt(saUsageSet.getSetting("shown", "0"));
//				saUsage.used = Integer.parseInt(saUsageSet.getSetting("used", "0"));
//				saUsage.usedLast = Integer.parseInt(saUsageSet.getSetting("usedLast", "0"));
//			}
//		}
//		void storeTo(Settings set) {
//			set.setSetting("isAdvancedPivotIndex", ("" + this.isSaAdvancedPivotIndex));
//			set.setSetting("useCounter", ("" + this.saUseCounter));
//			
//			for (Iterator sanit = this.keySet().iterator(); sanit.hasNext();) {
//				String saName = ((String) sanit.next());
//				SelectionActionUsage saUsage = this.getSelectionActionUsage(saName);
//				Settings saUsageSet = set.getSubset(saName);
//				saUsageSet.setSetting("shown", ("" + saUsage.shown));
//				saUsageSet.setSetting("used", ("" + saUsage.used));
//				saUsageSet.setSetting("usedLast", ("" + saUsage.usedLast));
//			}
//		}
//	}
	private static SelectionActionUsageStats selectionActionUsageStats = null;
	private static SelectionActionUsageStats getSelectionActionUsageStats() {
		if (selectionActionUsageStats == null)
			selectionActionUsageStats = new SelectionActionUsageStats();
		return selectionActionUsageStats;
	}
	private static class SelectionActionUsageStats {
		private int advancedActionPivotIndex = 10;
		private NamedElementUsageStatistics stats;
		SelectionActionUsageStats() {
			this.stats = NamedElementUsageStatistics.getElementUsageStatistics("main");
			Object contextMenuBaseSize = UserInterfaceUtils.getDisplayProperty("main.contextMenuBaseSize");
			if (contextMenuBaseSize instanceof Number)
				this.advancedActionPivotIndex = Math.max(((Number) contextMenuBaseSize).intValue(), this.advancedActionPivotIndex);
			else UserInterfaceUtils.setDisplayProperty("main.contextMenuBaseSize", new Integer(this.advancedActionPivotIndex));
		}
		boolean[] markAdvancedSelectionActions(SelectionAction[] actions) {
			String[] actionNames = new String[actions.length];
			for (int a = 0; a < actions.length; a++) {
				if (actions[a] != SelectionAction.SEPARATOR)
					actionNames[a] = actions[a].name;
			}
			
			float[] actionUsageScores = this.stats.getElementUsageScores(actionNames);
			float[] actionUsageScoresSorted = Arrays.copyOf(actionUsageScores, actionUsageScores.length);
			
			Arrays.sort(actionUsageScoresSorted);
			float isSaAdvancedThreshold = ((actions.length < this.advancedActionPivotIndex) ? 0 : actionUsageScoresSorted[actions.length - this.advancedActionPivotIndex]);
			
			boolean[] isAdvancedAction = new boolean[actions.length];
			for (int a = 0; a < actions.length; a++)
				isAdvancedAction[a] = (actionUsageScores[a] < isSaAdvancedThreshold);
			
			return isAdvancedAction;
		}
		void selectionActionUsed(SelectionAction sa) {
			this.stats.elementUsed(sa.name);
		}
	}
}