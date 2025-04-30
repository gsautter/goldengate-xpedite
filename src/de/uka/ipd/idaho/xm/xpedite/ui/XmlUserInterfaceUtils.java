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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import de.uka.ipd.idaho.gamta.Annotation;
import de.uka.ipd.idaho.goldenGate.GoldenGATE;
import de.uka.ipd.idaho.goldenGate.plugins.ResourceManager;
import de.uka.ipd.idaho.goldenGate.ui.DocumentFunction;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay;
import de.uka.ipd.idaho.goldenGate.ui.KeystrokeActionDescriptor;
import de.uka.ipd.idaho.goldenGate.ui.UserInterfaceUtils;
import de.uka.ipd.idaho.goldenGate.ui.UserInterfaceUtils.DocumentFunctionGroup;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuBar;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuElement;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuFunction;
import de.uka.ipd.idaho.goldenGate.util.DialogPanel;
import de.uka.ipd.idaho.xm.XmAnnotation;
import de.uka.ipd.idaho.xm.XmToken;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.SelectionAction;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.ViewConfigurationPanel;
import de.uka.ipd.idaho.xm.util.XmUtils;
import de.uka.ipd.idaho.xm.xpedite.GoldenGateXpedite;
import de.uka.ipd.idaho.xm.xpedite.plugins.KeystrokeActionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.SelectionActionProvider;

/**
 * @author sautter
 */
public class XmlUserInterfaceUtils {
	private static final boolean DEBUG_CONTEXT_MENU_STYLING = false;
	
	public static JMenuItem styleContextMenuItem(JMenuItem mi, SelectionAction action) {
		if (DEBUG_CONTEXT_MENU_STYLING) System.out.println("Styling menu item for " + action.name);
		String providerClassName = getProviderClassName(action);
		if (providerClassName != null) {
			if (DEBUG_CONTEXT_MENU_STYLING) System.out.println(" ==> applying generic styling with provider class name '" + providerClassName + "'");
			styleContextMenuItem(mi, providerClassName, action, null);
		}
		else if (DEBUG_CONTEXT_MENU_STYLING) System.out.println(" ==> styled by provider");
		return mi;
	}
	
	private static final String STYLED_EXTERNALLY = "STYLED_EXTERNALLY";
	private static Map providerClassNamesByActionClassNames = Collections.synchronizedMap(new HashMap());
	private static String getProviderClassName(SelectionAction action) {
		String actionClassName = action.getClass().getName();
		String providerClassName = ((String) providerClassNamesByActionClassNames.get(actionClassName));
		if (providerClassName == null) {} // need to analyze this one below
		else if (providerClassName == STYLED_EXTERNALLY) // provider styled, nothing to do on our end
			return null;
		else return providerClassName; // cache hit
		try {
			Class actionClass = action.getClass();
			if (DEBUG_CONTEXT_MENU_STYLING) System.out.println(" - class is " + actionClass.getName());
			Method miCreatorMethod = actionClass.getMethod("getMenuItem", XmDocumentMarkupPanel.class);
			if (DEBUG_CONTEXT_MENU_STYLING) System.out.println(" - menu item getter declared by " + miCreatorMethod.getDeclaringClass().getName());
			if (SelectionAction.class.equals(miCreatorMethod.getDeclaringClass())) {
				if (actionClassName.indexOf("$") == -1) {
					if (DEBUG_CONTEXT_MENU_STYLING) System.out.println(" ==> top level action class name");
					providerClassNamesByActionClassNames.put(actionClassName, STYLED_EXTERNALLY);
					return null;
				}
				else {
					providerClassName = actionClassName.substring(0, actionClassName.indexOf("$"));
					providerClassNamesByActionClassNames.put(actionClassName, providerClassName);
					if (DEBUG_CONTEXT_MENU_STYLING) System.out.println(" ==> got provider class name '" + providerClassName + "'");
					return providerClassName;
				}
			}
			else {
				if (DEBUG_CONTEXT_MENU_STYLING) System.out.println(" ==> styled externally by provider");
				providerClassNamesByActionClassNames.put(actionClassName, STYLED_EXTERNALLY);
				return null;
			}
		}
		catch (Exception e) {
			providerClassNamesByActionClassNames.put(actionClassName, STYLED_EXTERNALLY); // no need to try again, exception would just come back
			e.printStackTrace(System.out);
			return null;
		}
	}
	
	public static JMenuItem styleContextMenuItem(JMenuItem menuItem, SelectionActionProvider actionProvider, SelectionAction action, Color targetColor) {
		if (menuItem == null)
			return menuItem;
		return styleContextMenuItem(menuItem, ((actionProvider == null) ? null : actionProvider.getClass().getName()), action, targetColor);
	}
	
	public static JMenuItem styleContextMenuItem(JMenuItem menuItem, String providerClassName, SelectionAction action, Color targetColor) {
		if (menuItem == null)
			return menuItem;
		return styleContextMenuItem(menuItem, providerClassName, action.name, action.label, action.tooltip, targetColor);
	}
	
	public static JMenuItem styleContextMenuItem(JMenuItem menuItem, String providerClassName, String name, String label, String tooltip, Color targetColor) {
		if (menuItem == null)
			return menuItem;
		return UserInterfaceUtils.styleDesktopContextMenuItem(menuItem, providerClassName, name, label, tooltip, targetColor, null);
	}
	
	public static SelectionAction createAdvancedSelectionAction(GoldenGATE goldenGate, final String functionOwnerClassName, final XmlDocumentDisplay display, final Annotation selection) {
		if (selection == null)
			return null;
		final DocumentFunctionGroup[] dfgs = UserInterfaceUtils.getGenericDocumentFunctionsForSelection(goldenGate, functionOwnerClassName, display, selection);
		if (dfgs.length == 0)
			return null;
		return new SelectionAction("advancedContextManu", "Advanced", ("Apply any advanced generic functions to the selected " + selection.getType() + "")) {
			public boolean performAction(XmDocumentMarkupPanel invoker) {
				return false; // document functions handle atomic actions and refresh
			}
			public JMenuItem getMenuItem(XmDocumentMarkupPanel invoker) {
				JMenu advancedMenu = new JMenu();
				for (int g = 0; g < dfgs.length; g++) {
					if (dfgs[g].functions.length == 1) {
						final DocumentFunction df = dfgs[g].functions[0];
						JMenuItem groupMenuItem = new JMenuItem(df.label);
						groupMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								df.applyTo(null, display, selection);
							}
						});
						UserInterfaceUtils.styleDesktopContextMenuItem(df, groupMenuItem);
						advancedMenu.add(groupMenuItem);
					}
					else {
						JMenu groupMenu = new JMenu();
						String menuLabel;
						if (dfgs[g].owner instanceof ResourceManager) {
							menuLabel = ((ResourceManager) dfgs[g].owner).getResourceTypeLabel();
							if (menuLabel == null)
								menuLabel = dfgs[g].owner.getPluginName();
							else if (menuLabel.endsWith("es")) {}
							else if (menuLabel.endsWith("s") || menuLabel.endsWith("x"))
								menuLabel = (menuLabel + "es");
							else menuLabel = (menuLabel + "s");
							groupMenu.setText(menuLabel);
						}
						else menuLabel = dfgs[g].owner.getPluginName();
						UserInterfaceUtils.styleDesktopContextMenuItem(groupMenu, dfgs[g].owner.getClass().getName(), "advancedContextActions", menuLabel, ("Functionality from " + dfgs[g].owner.getPluginName()), null, null);
						for (int f = 0; f < dfgs[g].functions.length; f++) {
							final DocumentFunction df = dfgs[g].functions[f];
							JMenuItem functionMenuItem = new JMenuItem(df.label);
							functionMenuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent ae) {
									df.applyTo(null, display, selection);
								}
							});
							UserInterfaceUtils.styleDesktopContextMenuItem(df, functionMenuItem);
							groupMenu.add(functionMenuItem);
						}
						advancedMenu.add(groupMenu);
					}
				}
				return XmlUserInterfaceUtils.styleContextMenuItem(advancedMenu, functionOwnerClassName, this, null);
			}
		};
	}
	
	public static String getAnnotationShortValue(XmAnnotation annot) {
		return getAnnotationShortValue(annot.getFirstToken(), annot.getLastToken());
	}
	public static String getAnnotationShortValue(XmToken firstToken, XmToken lastToken) {
		if (annotationShortValueLength < 0) {
			Object asvlObj = UserInterfaceUtils.getDisplayProperty("annot.shortValueLength");
			if (asvlObj instanceof Number)
				annotationShortValueLength = ((Number) asvlObj).intValue();
			else {
				UserInterfaceUtils.setDisplayProperty("annot.shortValueLength", Integer.valueOf(defaultAnnotationShortValueLength));
				annotationShortValueLength = defaultAnnotationShortValueLength;
			}
		}
		return XmUtils.getString(firstToken, lastToken, true, annotationShortValueLength);
	}
	private static final int defaultAnnotationShortValueLength = 20;
	private static int annotationShortValueLength = -1;
	
	public static String getAnnotationLabelValue(XmAnnotation annot) {
		return getAnnotationLabelValue(annot.getFirstToken(), annot.getLastToken());
	}
	public static String getAnnotationLabelValue(XmToken firstToken, XmToken lastToken) {
		if (annotationLabelValueLength < 0) {
			Object alvlObj = UserInterfaceUtils.getDisplayProperty("annot.labelValueLength");
			if (alvlObj instanceof Number)
				annotationLabelValueLength = ((Number) alvlObj).intValue();
			else {
				UserInterfaceUtils.setDisplayProperty("annot.labelValueLength", Integer.valueOf(defaultAnnotationLabelValueLength));
				annotationLabelValueLength = defaultAnnotationLabelValueLength;
			}
		}
		return XmUtils.getString(firstToken, lastToken, true, annotationLabelValueLength);
	}
	private static final int defaultAnnotationLabelValueLength = 40;
	private static int annotationLabelValueLength = -1;
	
	public static String getAnnotationTooltipValue(XmAnnotation annot) {
		return getAnnotationTooltipValue(annot.getFirstToken(), annot.getLastToken());
	}
	public static String getAnnotationTooltipValue(XmToken firstToken, XmToken lastToken) {
		if (annotationTooltipValueLength < 0) {
			Object alvlObj = UserInterfaceUtils.getDisplayProperty("annot.tooltipValueLength");
			if (alvlObj instanceof Number)
				annotationTooltipValueLength = ((Number) alvlObj).intValue();
			else {
				UserInterfaceUtils.setDisplayProperty("annot.tooltipValueLength", Integer.valueOf(defaultAnnotationTooltipValueLength));
				annotationTooltipValueLength = defaultAnnotationTooltipValueLength;
			}
		}
		return XmUtils.getString(firstToken, lastToken, true, annotationTooltipValueLength);
	}
	private static final int defaultAnnotationTooltipValueLength = 100;
	private static int annotationTooltipValueLength = -1;
	
	public static void configureDisplay(XmDocumentMarkupPanel xdmp, String title) {
		if (title == null)
			title = "Adjust Display Configuration";
		DisplayConfigDialog dcd = new DisplayConfigDialog(title, xdmp);
		dcd.setVisible(true);
	}
	
	private static class DisplayConfigDialog extends DialogPanel {
		private static Dimension initialSize = new Dimension(1000, 250);
		private static Point initialPos = null;
		ViewConfigurationPanel configPanel;
		DisplayConfigDialog(String title, XmDocumentMarkupPanel xdmp) {
			super(title, false);
			this.configPanel = xdmp.getConfigurationPanel();
			
			//	create and tray up buttons
			JButton ok = new JButton("OK");
			ok.setBorder(BorderFactory.createRaisedBevelBorder());
			ok.setPreferredSize(new Dimension(80, 21));
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					configPanel.applySettings();
					dispose();
				}
			});
			JButton apply = new JButton("Apply");
			apply.setBorder(BorderFactory.createRaisedBevelBorder());
			apply.setPreferredSize(new Dimension(80, 21));
			apply.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					configPanel.applySettings();
				}
			});
			JButton reset = new JButton("Reset");
			reset.setBorder(BorderFactory.createRaisedBevelBorder());
			reset.setPreferredSize(new Dimension(80, 21));
			reset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					for (int n = 0; n < XmDocumentMarkupPanel.displayPropertyNames.length; n++)
						configPanel.setDisplayProperty(XmDocumentMarkupPanel.displayPropertyNames[n], null);
				}
			});
			JButton cancel = new JButton("Cancel");
			cancel.setBorder(BorderFactory.createRaisedBevelBorder());
			cancel.setPreferredSize(new Dimension(80, 21));
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER), true);
			buttonPanel.add(ok);
			buttonPanel.add(apply);
			buttonPanel.add(reset);
			buttonPanel.add(cancel);
			
			//	assemble whole shebang
			this.add(this.configPanel, BorderLayout.CENTER);
			this.add(buttonPanel, BorderLayout.SOUTH);
			
			//	configure dialog proper
			this.setSize(initialSize);
			if (initialPos == null)
				this.setLocationRelativeTo(xdmp);
			else this.setLocation(initialPos);
		}
		public void dispose() {
			initialSize = this.getSize();
			initialPos = this.getLocation();
			super.dispose();
		}
	}
	
	public static WindowMenuElement getDefaultKeystrokeExplanationMenuElement(final GoldenGateXpedite ggXpedite, String label, String tooltip, final String dialogTitle) {
		KeystrokeActionProvider[] kaps = ggXpedite.getKeystrokeActionProviders();
		if (kaps.length == 0)
			return null; // those plug-ins do not get added at runtime out of thin air, only on startup
		if (label == null)
			label = "List Keyboard Actions";
		if (tooltip == null)
			tooltip = "Show a list of the keyboard actions available in the current window";
		int wmeFlags = 0;
		wmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
		wmeFlags |= WindowMenuElement.encodePreferredMenuName(WindowMenuBar.WINDOW_MENU_NAME, wmeFlags);
		return new WindowMenuFunction("ggXpedite", "keyboardActionOverview", "List Keyboard Actions", "Show a list of the keyboard actions available in the document view", wmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return true;
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				showKeyboardActionDescriptors(ggXpedite, dialogTitle);
			}
		};
	}
	static void showKeyboardActionDescriptors(GoldenGateXpedite ggXpedite, String dialogTitle) {
		
		//	collect keystroke descriptors
		ArrayList kads = new ArrayList();
		KeystrokeActionProvider[] kaps = ggXpedite.getKeystrokeActionProviders();
		for (int p = 0; p < kaps.length; p++) {
			KeystrokeActionDescriptor[] pkads = kaps[p].getKeystrokeActionDescriptors();
			if (pkads != null)
				for (int d = 0; d < pkads.length; d++) {
					if (pkads[d] != null)
						kads.add(pkads[d]);
				}
		}
		if (kads.size() != 0)
			UserInterfaceUtils.showKeyboardActionDescriptors(((KeystrokeActionDescriptor[]) kads.toArray(new KeystrokeActionDescriptor[kads.size()])), dialogTitle);
	}
}
