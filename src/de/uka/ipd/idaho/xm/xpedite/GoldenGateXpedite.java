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
package de.uka.ipd.idaho.xm.xpedite;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.uka.ipd.idaho.easyIO.EasyIO;
import de.uka.ipd.idaho.easyIO.settings.Settings;
import de.uka.ipd.idaho.gamta.util.ProgressMonitor;
import de.uka.ipd.idaho.goldenGate.GoldenGATE;
import de.uka.ipd.idaho.goldenGate.GoldenGateConfiguration;
import de.uka.ipd.idaho.goldenGate.plugins.GoldenGatePlugin;
import de.uka.ipd.idaho.goldenGate.ui.UserInterfaceUtils;
import de.uka.ipd.idaho.xm.XmAnnotation;
import de.uka.ipd.idaho.xm.XmDocument;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.XmlMarkupTool;
import de.uka.ipd.idaho.xm.xpedite.plugins.ClickActionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.GoldenGateXpediteAtomicActionListener;
import de.uka.ipd.idaho.xm.xpedite.plugins.GoldenGateXpediteDocumentListener;
import de.uka.ipd.idaho.xm.xpedite.plugins.GoldenGateXpediteDocumentListener.CancelSavingException;
import de.uka.ipd.idaho.xm.xpedite.plugins.GoldenGateXpeditePlugin;
import de.uka.ipd.idaho.xm.xpedite.plugins.KeystrokeActionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.ReactionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.SelectionActionProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlConversionProfile;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlConversionProfileProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentDropHandler;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentExporter;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentIoProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlMarkupToolProvider;

/**
 * @author sautter
 *
 */
public class GoldenGateXpedite implements GoldenGateXpediteConstants {
//	
//	private static final SimpleDateFormat yearTimestamper = new SimpleDateFormat("yyyy");
//	private static final String ABOUT_TEXT = 
//		"GoldenGATE Xpedite " + VERSION_STRING + "\n" +
//		"The easy way to mark up Documents\n" +
//		"Version Date: " + VERSION_DATE + "\n" +
//		"\n" +
//		"\u00A9 by Guido Sautter 2006-" + yearTimestamper.format(new Date()) + "\n" +
//		"IPD Boehm\n" +
//		"Karlsruhe Institute of Technology (KIT)";
//	
//	private static final int maxInMemorySupplementBytes = (50 * 1024 * 1024); // 50 MB
	
	private GoldenGateConfiguration configuration;
	private GoldenGATE goldenGate;
	private Settings settings;
	private File rootFolder;
//	
//	private GgiPageImageStore pageImageStore;
//	private PdfExtractor pdfExtractor;
	
	private GoldenGateXpedite(GoldenGateConfiguration configuration, GoldenGATE gg, File rootFolder) {
		this.configuration = configuration;
		this.goldenGate = gg;
		this.rootFolder = rootFolder;
//		
//		//	get settings
//		Settings set = configuration.getSettings();
//		
//		//	read cache root path
//		String cacheRootFolderName = set.getSetting("cacheRootFolder");
//		File cacheRootFolder;
//		if (cacheRootFolderName == null)
//			cacheRootFolder = this.rootFolder;
//		else {
//			if (cacheRootFolderName.startsWith("/") || (cacheRootFolderName.indexOf(':') != -1))
//				cacheRootFolder = new File(cacheRootFolderName);
//			else cacheRootFolder = new File(this.rootFolder, cacheRootFolderName);
//			if (!cacheRootFolder.exists())
//				cacheRootFolder.mkdirs();
//		}
		
		//	get settings
		this.settings = this.goldenGate.getApplicationSettings("GgXpedite.cnfg");
		String[] setNames = this.settings.getKeys();
		for (int n = 0; n < setNames.length; n++) try {
			UserInterfaceUtils.decodeDisplayProperty(setNames[n], this.settings.getSetting(setNames[n]));
		}
		catch (RuntimeException re) {
			System.out.println("Failed to initialize property '" + setNames[n] + "' from central GoldenGATE Xpedite settings: " + re.getMessage());
		}
		
		//	get and index applicable plugins (only now, as instance proper is fully initialized)
		GoldenGatePlugin[] ggps = this.goldenGate.getPlugins();
		for (int p = 0; p < ggps.length; p++) {
			if (ggps[p] instanceof GoldenGateXpeditePlugin) try {
				((GoldenGateXpeditePlugin) ggps[p]).setXpediteParent(this);
				((GoldenGateXpeditePlugin) ggps[p]).initXpedite();
			}
			catch (Throwable t) {
				System.out.println(t.getClass().getName() + " (" + t.getMessage() + ") while initializing " + ggps[p].getClass().getName());
				t.printStackTrace(System.out);
				continue;
			}
//			if (ggps[p] instanceof XmlEditToolProvider)
//				this.registerImageEditToolProvider((XmlEditToolProvider) ggps[p]);
			if (ggps[p] instanceof XmlMarkupToolProvider)
				this.registerXmlMarkupToolProvider((XmlMarkupToolProvider) ggps[p]);
			if (ggps[p] instanceof SelectionActionProvider)
				this.registerSelectionActionProvider((SelectionActionProvider) ggps[p]);
			if (ggps[p] instanceof ClickActionProvider)
				this.registerClickActionProvider((ClickActionProvider) ggps[p]);
			if (ggps[p] instanceof KeystrokeActionProvider)
				this.registerKeystrokeActionProvider((KeystrokeActionProvider) ggps[p]);
			if (ggps[p] instanceof XmlDocumentDropHandler)
				this.registerDropHandler((XmlDocumentDropHandler) ggps[p]);
			if (ggps[p] instanceof XmlDocumentIoProvider)
				this.registerDocumentIoProvider((XmlDocumentIoProvider) ggps[p]);
			if (ggps[p] instanceof XmlConversionProfileProvider)
				this.registerXmlConversionProfileProvider((XmlConversionProfileProvider) ggps[p]);
			if (ggps[p] instanceof XmlDocumentExporter)
				this.registerDocumentExporter((XmlDocumentExporter) ggps[p]);
			if (ggps[p] instanceof ReactionProvider)
				this.registerReactionProvider((ReactionProvider) ggps[p]);
//			if (ggps[p] instanceof DisplayExtensionProvider)
//				this.registerDisplayExtensionProvider((DisplayExtensionProvider) ggps[p]);
			if (ggps[p] instanceof GoldenGateXpediteDocumentListener)
				this.registerDocumentListener((GoldenGateXpediteDocumentListener) ggps[p]);
			if (ggps[p] instanceof GoldenGateXpediteAtomicActionListener)
				this.registerAtomicActionListener((GoldenGateXpediteAtomicActionListener) ggps[p]);
			//XmlConversionProfile
		}
	}
	
	/**
	 * @return the GoldenGATE icon as provided by the current configuration
	 */
	public Image getGoldenGateIcon() {
		return this.goldenGate.getGoldenGateIcon();
	}
	
	/**
	 * @return the name of the configuration wrapped in this GoldenGATE Xpedite instance
	 */
	public String getConfigurationName() {
		return this.goldenGate.getConfigurationName();
	}
	
	/**
	 * @return the enclosed GoldenGATE instance
	 */
	public GoldenGATE getGoldenGATE() {
		return this.goldenGate;
	}
	
	//	register and lookup method for drop handlers
	private HashMap dropHandlersByClassName = new LinkedHashMap();
	
	private void registerDropHandler(XmlDocumentDropHandler xddh) {
		if (xddh != null)
			this.dropHandlersByClassName.put(xddh.getClass().getName(), xddh);
	}
	
	/**
	 * Find a document drop handler by its class name.
	 * @param pluginClassName the class name of the desired drop handler
	 * @return the drop handler with the specified class name
	 */
	public XmlDocumentDropHandler getDropHandler(String pluginClassName) {
		return ((XmlDocumentDropHandler) this.dropHandlersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all drop handlers that are currently available.
	 * @return an array holding all drop handlers registered
	 */
	public XmlDocumentDropHandler[] getDropHandlers() {
		ArrayList iddhs = new ArrayList(this.dropHandlersByClassName.values());
		return ((XmlDocumentDropHandler[]) iddhs.toArray(new XmlDocumentDropHandler[iddhs.size()]));
	}
	
	//	register and lookup method for document IO providers
	private HashMap docmentIoProvidersByClassName = new LinkedHashMap();
	
	private void registerDocumentIoProvider(XmlDocumentIoProvider xdip) {
		if (xdip != null)
			this.docmentIoProvidersByClassName.put(xdip.getClass().getName(), xdip);
	}
	
	/**
	 * Find a document IO provider by its class name.
	 * @param pluginClassName the class name of the desired IO provider
	 * @return the document IO provider with the specified class name
	 */
	public XmlDocumentIoProvider getDocumentIoProvider(String pluginClassName) {
		return ((XmlDocumentIoProvider) this.docmentIoProvidersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all document IO providers that are currently available.
	 * @return an array holding all document IO providers registered
	 */
	public XmlDocumentIoProvider[] getDocumentIoProviders() {
		ArrayList idips = new ArrayList(this.docmentIoProvidersByClassName.values());
		return ((XmlDocumentIoProvider[]) idips.toArray(new XmlDocumentIoProvider[idips.size()]));
	}
	
	//	register and lookup method for XML conversion profile providers
	private HashMap xmlConversionProfileProvidersByClassName = new LinkedHashMap();
	
	private void registerXmlConversionProfileProvider(XmlConversionProfileProvider xcpp) {
		if (xcpp != null)
			this.xmlConversionProfileProvidersByClassName.put(xcpp.getClass().getName(), xcpp);
	}
	
	/**
	 * Find an XML conversion profile provider by its class name.
	 * @param pluginClassName the class name of the XML conversion profile provider
	 * @return the XML conversion profile provider with the specified class name
	 */
	public XmlConversionProfileProvider getXmlConversionProfileProvider(String pluginClassName) {
		return ((XmlConversionProfileProvider) this.xmlConversionProfileProvidersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all XML conversion profile providers that are currently available.
	 * @return an array holding all XML conversion profile providers registered
	 */
	public XmlConversionProfileProvider[] getXmlConversionProfileProviders() {
		ArrayList xcpps = new ArrayList(this.xmlConversionProfileProvidersByClassName.values());
		return ((XmlConversionProfileProvider[]) xcpps.toArray(new XmlConversionProfileProvider[xcpps.size()]));
	}
	
	/**
	 * Retrieve an XML conversion profile by its name. The name may be fully
	 * qualified, i.e., include the providerClassName, but need not. In the
	 * latter case, all XML conversion profile providers will be asked for an
	 * XML conversion profile with the specified name, and the first one found
	 * will be returned.
	 * @param name the name of the XML conversion profile
	 * @return the XML conversion profile with the specified name, or null, if
	 *         there is no such XML conversion profile
	 */
	public XmlConversionProfile getXmlConversionProfileForName(String name) {
		int nameSplit = ((name == null) ? -1 : name.indexOf('@'));
		if ((nameSplit == -1) || ((nameSplit + 1) == name.length()))
			return this.getXmlConversionProfileForName(name, null);
		else return this.getXmlConversionProfileForName(name.substring(0, nameSplit), name.substring(nameSplit + "@".length()));
	}
	
	/**
	 * Retrieve an XML conversion profile by its name. The providerClassName
	 * may be null. In this latter case, all XML conversion profile providers
	 * will be asked for an XML conversion profile with the specified name, and
	 * the first one found will be returned.
	 * @param name the name of the XML conversion profile
	 * @param providerClassName the class name of the desired XML conversion
	 *            profile provider to ask for the XML conversion profile
	 * @return the XML conversion profile with the specified name, or null, if
	 *         there is no such XML conversion profile
	 */
	public XmlConversionProfile getXmlConversionProfileForName(String name, String providerClassName) {
		if (providerClassName == null) {
			XmlConversionProfileProvider[] xcpps = this.getXmlConversionProfileProviders();
			for (int p = 0; p < xcpps.length; p++) {
				XmlConversionProfile xcp = xcpps[p].getXmlConversionProfile(name);
				if (xcp != null)
					return xcp;
			}
			return null;
		}
		else {
			XmlConversionProfileProvider xcpp = this.getXmlConversionProfileProvider(providerClassName);
			return ((xcpp == null) ? null : xcpp.getXmlConversionProfile(name));
		}
	}
	
	//	register and lookup method for document exporters
	private HashMap documentExportersByClassName = new LinkedHashMap();
	
	private void registerDocumentExporter(XmlDocumentExporter xde) {
		if (xde != null)
			this.documentExportersByClassName.put(xde.getClass().getName(), xde);
	}
	
	/**
	 * Find a document exporter by its class name.
	 * @param pluginClassName the class name of the desired exporter
	 * @return the exporter with the specified class name
	 */
	public XmlDocumentExporter getDocumentExporter(String pluginClassName) {
		return ((XmlDocumentExporter) this.documentExportersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all document exporters that are currently available.
	 * @return an array holding all document exporters registered
	 */
	public XmlDocumentExporter[] getDocumentExporters() {
		ArrayList ides = new ArrayList(this.documentExportersByClassName.values());
		return ((XmlDocumentExporter[]) ides.toArray(new XmlDocumentExporter[ides.size()]));
	}
//	
//	//	register and lookup method for image edit tool providers
//	private HashMap imageEditToolProvidersByClassName = new LinkedHashMap();
//	
//	private void registerImageEditToolProvider(XmlEditToolProvider ietp) {
//		if (ietp  != null)
//			this.imageEditToolProvidersByClassName.put(ietp.getClass().getName(), ietp);
//	}
//	
//	/**
//	 * Find an image edit tool provider by its class name.
//	 * @param pluginClassName the class name of the desired image edit tool provider
//	 * @return the image edit tool provider with the specified class name
//	 */
//	public XmlEditToolProvider getImageEditToolProvider(String pluginClassName) {
//		return ((XmlEditToolProvider) this.imageEditToolProvidersByClassName.get(pluginClassName));
//	}
//	
//	/**
//	 * Get all image edit tool providers that are currently available.
//	 * @return an array holding all image edit tool providers registered
//	 */
//	public XmlEditToolProvider[] getImageEditToolProviders() {
//		ArrayList ietps = new ArrayList(this.imageEditToolProvidersByClassName.values());
//		return ((XmlEditToolProvider[]) ietps.toArray(new XmlEditToolProvider[ietps.size()]));
//	}
	
	//	register and lookup method for XML edit tool providers
	private HashMap xmlMarkupToolProvidersByClassName = new LinkedHashMap();
	
	private void registerXmlMarkupToolProvider(XmlMarkupToolProvider xmtp) {
		if (xmtp != null)
			this.xmlMarkupToolProvidersByClassName.put(xmtp.getClass().getName(), xmtp);
	}
	
	/**
	 * Find an XML markup tool provider by its class name.
	 * @param pluginClassName the class name of the desired XML markup tool provider
	 * @return the XML markup tool provider with the specified class name
	 */
	public XmlMarkupToolProvider getXmlMarkupToolProvider(String pluginClassName) {
		return ((XmlMarkupToolProvider) this.xmlMarkupToolProvidersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all XML markup tool providers that are currently available.
	 * @return an array holding all XML markup tool providers registered
	 */
	public XmlMarkupToolProvider[] getXmlMarkupToolProviders() {
		ArrayList imtps = new ArrayList(this.xmlMarkupToolProvidersByClassName.values());
		return ((XmlMarkupToolProvider[]) imtps.toArray(new XmlMarkupToolProvider[imtps.size()]));
	}
	
	/**
	 * Retrieve an XML markup tool by its name. The name may be fully
	 * qualified, i.e., include the providerClassName, but need not. In the
	 * latter case, all XML markup tool providers will be asked for an XML
	 * markup tool with the specified name, and the first one found will be
	 * returned.
	 * @param name the name of the XML markup tool
	 * @return the XML markup tool with the specified name, or null, if there
	 *         is no such XML markup tool
	 */
	public XmlMarkupTool getXmlMarkupToolForName(String name) {
		int nameSplit = ((name == null) ? -1 : name.indexOf('@'));
		if ((nameSplit == -1) || ((nameSplit + 1) == name.length()))
			return this.getXmlMarkupToolForName(name, null);
		else return this.getXmlMarkupToolForName(name.substring(0, nameSplit), name.substring(nameSplit + "@".length()));
	}
	
	/**
	 * Retrieve an XML markup tool by its name. The providerClassName may be
	 * null. In this latter case, all XML markup tool providers will be asked
	 * for an XML markup tool with the specified name, and the first one found
	 * will be returned.
	 * @param name the name of the XML markup tool
	 * @param providerClassName the class name of the desired XML markup tool
	 *            provider to ask for the XML markup tool
	 * @return the XML markup tool with the specified name, or null, if there
	 *         is no such XML markup tool
	 */
	public XmlMarkupTool getXmlMarkupToolForName(String name, String providerClassName) {
		if (providerClassName == null) {
			XmlMarkupToolProvider[] xmtps = this.getXmlMarkupToolProviders();
			for (int p = 0; p < xmtps.length; p++) {
				XmlMarkupTool xmt = xmtps[p].getXmlMarkupTool(name);
				if (xmt != null) return xmt;
			}
			return null;
		}
		else {
			XmlMarkupToolProvider xmtp = this.getXmlMarkupToolProvider(providerClassName);
			return ((xmtp == null) ? null : xmtp.getXmlMarkupTool(name));
		}
	}
	
	//	register and lookup method for selection action providers
	private HashMap selectionActionProvidersByClassName = new LinkedHashMap();
	
	private void registerSelectionActionProvider(SelectionActionProvider sap) {
		if (sap != null)
			this.selectionActionProvidersByClassName.put(sap.getClass().getName(), sap);
	}
	
	/**
	 * Find a GoldenGatePlugin by its class name.
	 * @param pluginClassName the class name of the desired GoldenGatePlugin
	 * @return the GoldenGatePlugin with the specified class name
	 */
	public SelectionActionProvider getSelectionActionProvider(String pluginClassName) {
		return ((SelectionActionProvider) this.selectionActionProvidersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all GoldenGatePlugins that are currently available.
	 * @return an array holding all GoldenGatePlugins registered
	 */
	public SelectionActionProvider[] getSelectionActionProviders() {
		ArrayList saps = new ArrayList(this.selectionActionProvidersByClassName.values());
		return ((SelectionActionProvider[]) saps.toArray(new SelectionActionProvider[saps.size()]));
	}
	
	//	register and lookup method for click action providers
	private HashMap clickActionProvidersByClassName = new LinkedHashMap();
	
	private void registerClickActionProvider(ClickActionProvider cap) {
		if (cap != null)
			this.clickActionProvidersByClassName.put(cap.getClass().getName(), cap);
	}
	
	/**
	 * Find a GoldenGatePlugin by its class name.
	 * @param pluginClassName the class name of the desired GoldenGatePlugin
	 * @return the GoldenGatePlugin with the specified class name
	 */
	public ClickActionProvider getClickActionProvider(String pluginClassName) {
		return ((ClickActionProvider) this.clickActionProvidersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all GoldenGatePlugins that are currently available.
	 * @return an array holding all GoldenGatePlugins registered
	 */
	public ClickActionProvider[] getClickActionProviders() {
		ArrayList caps = new ArrayList(this.clickActionProvidersByClassName.values());
		return ((ClickActionProvider[]) caps.toArray(new ClickActionProvider[caps.size()]));
	}
	
	//	register and lookup method for typing action providers
	private HashMap keystrokeActionProvidersByClassName = new LinkedHashMap();
	
	private void registerKeystrokeActionProvider(KeystrokeActionProvider tap) {
		if (tap != null)
			this.keystrokeActionProvidersByClassName.put(tap.getClass().getName(), tap);
	}
	
	/**
	 * Find a GoldenGatePlugin by its class name.
	 * @param pluginClassName the class name of the desired GoldenGatePlugin
	 * @return the GoldenGatePlugin with the specified class name
	 */
	public KeystrokeActionProvider getKeystrokeActionProvider(String pluginClassName) {
		return ((KeystrokeActionProvider) this.keystrokeActionProvidersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all GoldenGatePlugins that are currently available.
	 * @return an array holding all GoldenGatePlugins registered
	 */
	public KeystrokeActionProvider[] getKeystrokeActionProviders() {
		ArrayList caps = new ArrayList(this.keystrokeActionProvidersByClassName.values());
		return ((KeystrokeActionProvider[]) caps.toArray(new KeystrokeActionProvider[caps.size()]));
	}
	
	//	register and lookup method for reaction providers
	private HashMap reactionProvidersByClassName = new LinkedHashMap();
	
	private void registerReactionProvider(ReactionProvider rp) {
		if (rp != null)
			this.reactionProvidersByClassName.put(rp.getClass().getName(), rp);
	}
	
	/**
	 * Find a reaction provider by its class name.
	 * @param pluginClassName the class name of the desired reaction provider
	 * @return the reaction provider with the specified class name
	 */
	public ReactionProvider getReactionProvider(String pluginClassName) {
		return ((ReactionProvider) this.reactionProvidersByClassName.get(pluginClassName));
	}
	
	/**
	 * Get all reaction providers that are currently available.
	 * @return an array holding all reaction providers registered
	 */
	public ReactionProvider[] getReactionProviders() {
		ArrayList rps = new ArrayList(this.reactionProvidersByClassName.values());
		return ((ReactionProvider[]) rps.toArray(new ReactionProvider[rps.size()]));
	}
//	
//	//	register and lookup method for display extension providers
//	private ArrayList displayExtensionProviders = new ArrayList();
//	
//	private void registerDisplayExtensionProvider(DisplayExtensionProvider dep) {
//		if (dep != null)
//			this.displayExtensionProviders.add(dep);
//	}
//	
//	/**
//	 * Get all display extension providers that are currently available.
//	 * @return an array holding all registered display extension providers
//	 */
//	public DisplayExtensionProvider[] getDisplayExtensionProviders() {
//		return ((DisplayExtensionProvider[]) this.displayExtensionProviders.toArray(new DisplayExtensionProvider[this.displayExtensionProviders.size()]));
//	}
//	
//	//	register for document listeners
//	private ArrayList displayExtensionListeners = new ArrayList();
//	
//	/**
//	 * Register a listener for changes to document display extensions.
//	 * @param del the listener to register
//	 */
//	public void addDisplayExtensionListener(DisplayExtensionListener del) {
//		if (del != null)
//			this.displayExtensionListeners.add(del);
//	}
//	
//	/**
//	 * Remove a listener for changes to document display extensions.
//	 * @param del the listener to remove
//	 */
//	public void removeDisplayExtensionListener(DisplayExtensionListener del) {
//		if (del != null)
//			this.displayExtensionListeners.remove(del);
//	}
//	
//	/**
//	 * Add a resource listener so it is notified when resources change.
//	 * @param ro the resource listener to add
//	 */
//	public void addResourceListener(ResourceListener rl) {
//		this.goldenGate.registerResourceListener(rl);
//	}
//	public void addResourceObserver(ResourceObserver rl) {
//		this.goldenGate.registerResourceObserver(rl);
//	}
//	
//	/**
//	 * Remove a resource listener so it is not notified any more when resources
//	 * change.
//	 * @param rl the resource listener to remove
//	 */
//	public void removeResourceListener(ResourceListener rl) {
//		this.goldenGate.unregisterResourceListener(rl);
//	}
//	public void removeResourceObserver(ResourceObserver rl) {
//		this.goldenGate.unregisterResourceObserver(rl);
//	}
	
	//	register for document listeners
	private ArrayList documentListeners = new ArrayList();
	
	private void registerDocumentListener(GoldenGateXpediteDocumentListener ggxdl) {
		if (ggxdl != null)
			this.documentListeners.add(ggxdl);
	}
	
	/**
	 * Get all document listeners that are currently available. This getter is
	 * mainly intended for applications that prefer to implement their own
	 * notification mechanisms instead of using the ones provided by this class.
	 * @return an array holding all document listeners registered
	 */
	public GoldenGateXpediteDocumentListener[] getDocumentListeners() {
		return ((GoldenGateXpediteDocumentListener[]) this.documentListeners.toArray(new GoldenGateXpediteDocumentListener[this.documentListeners.size()]));
	}
	
	/**
	 * Notify all registered document listeners that an XML Markup document
	 * has been opened in an application built around this GoldenGATE Xpedite
	 * core. This method should be called by client code right after an XML
	 * Markup document has been loaded.
	 * @param doc the document that was opened
	 * @param source the source the document was loaded from
	 * @param pm a progress monitor observing post-load processing
	 */
	public void notifyDocumentOpened(XmDocument doc, Object source, ProgressMonitor pm) {
		for (int l = 0; l < this.documentListeners.size(); l++)
			((GoldenGateXpediteDocumentListener) this.documentListeners.get(l)).documentOpened(doc, source, pm);
	}
	
	/**
	 * Notify all registered document listeners that an XML Markup document
	 * has been selected for editing in an application built around this
	 * GoldenGATE Xpedite core. This method should be called by client code
	 * right after an XML Markup document has been selected (most importantly
	 * after changing between documents in a multi-document UI).
	 * @param doc the document that was selected
	 */
	public void notifyDocumentSelected(XmDocument doc) {
		for (int l = 0; l < this.documentListeners.size(); l++)
			((GoldenGateXpediteDocumentListener) this.documentListeners.get(l)).documentSelected(doc);
	}
	
	/**
	 * Notify all registered document listeners that an XML Markup document
	 * is about to be saved to persistent storage in an application built
	 * around this GoldenGATE Xpedite core. This method should only be called
	 * if the XML Markup document is stored as such, rather than exported in
	 * another format. 
	 * If an implementor of the <code>documentSaving()</code> method decides to
	 * cancel the saving process, this method throws a
	 * <code>CancelSavingException</code>, with the reason for the cancellation
	 * in the message, e.g. to write it to a log file or display it in a UI.
	 * @param doc the document that is about to be saved
	 * @param dest the destination the document will be saved to
	 * @param pm a progress monitor observing saving preparations
	 * @param throws CancelSavingException any of the registered listeners
	 *            desires to cancel the saving process
	 */
	public void notifyDocumentSaving(XmDocument doc, Object dest, ProgressMonitor pm) throws CancelSavingException {
		for (int l = 0; l < this.documentListeners.size(); l++)
			((GoldenGateXpediteDocumentListener) this.documentListeners.get(l)).documentSaving(doc, dest, pm);
	}
	
	/**
	 * Notify all registered document listeners that an XML Markup document
	 * has been saved to persistent storage in an application built around this
	 * GoldenGATE Xpedite core. This method should only be called if the XML
	 * Markup document has been stored as such, rather than exported in another
	 * format, and only if the saving process has completed successfully.
	 * @param doc the document that has been saved
	 * @param dest the destination the document was saved to
	 * @param pm a progress monitor observing post-save preparations
	 */
	public void notifyDocumentSaved(XmDocument doc, Object dest, ProgressMonitor pm) {
		for (int l = 0; l < this.documentListeners.size(); l++)
			((GoldenGateXpediteDocumentListener) this.documentListeners.get(l)).documentSaved(doc, dest, pm);
	}
	
	/**
	 * Notify all registered document listeners that an XML Markup document
	 * has been closed in an application built around this GoldenGATE Xpedite
	 * core. This method should be called by client code right after the
	 * document is disposed.
	 * @param docId the ID of the document that was closed
	 */
	public void notifyDocumentClosed(String docId) {
		for (int l = 0; l < this.documentListeners.size(); l++)
			((GoldenGateXpediteDocumentListener) this.documentListeners.get(l)).documentClosed(docId);
//		
//		//	clean up page image cache
//		this.pageImageStore.cleanup(docId);
//		
//		//	clean up supplement cache folder
//		File docSupplementFolder = ((File) this.docSupplementFoldersById.get(docId));
//		if ((docSupplementFolder != null) && docSupplementFolder.exists()) try {
//			File[] docSupplements = docSupplementFolder.listFiles();
//			for (int s = 0; s < docSupplements.length; s++)
//				docSupplements[s].delete();
//			docSupplementFolder.delete();
//		}
//		catch (Exception e) {
//			System.out.println("Error cleaning up import supplement cache for document '" + docId + "': " + e.getMessage());
//			e.printStackTrace(System.out);
//		}
	}
	
	//	register for atomic action listeners
	private ArrayList atomicActionListeners = new ArrayList();
	
	private void registerAtomicActionListener(GoldenGateXpediteAtomicActionListener ggxaal) {
		if (ggxaal != null)
			this.atomicActionListeners.add(ggxaal);
	}
	
	/**
	 * Get all document listeners that are currently available. This getter is
	 * mainly intended for applications that prefer to implement their own
	 * notification mechanisms instead of using the ones provided by this class.
	 * @return an array holding all document listeners registered
	 */
	public GoldenGateXpediteAtomicActionListener[] getAtomicActionListeners() {
		return ((GoldenGateXpediteAtomicActionListener[]) this.atomicActionListeners.toArray(new GoldenGateXpediteAtomicActionListener[this.atomicActionListeners.size()]));
	}
	
	/**
	 * Notify all registered atomic action listeners that an atomic action is
	 * starting on an XML Document markup panel in a UI application built
	 * around this GoldenGATE Xpedite core. This method should be called by
	 * client UI code right after an atomic action has been started on an Image
	 * Image Document markup panel.
	 * @param id the unique ID of the started action
	 * @param label the label of the action
	 * @param xmt the XML Markup Tool performing the action
	 * @param annot the annotation being processed
	 * @param xdmp the document editor panel the atomic action is starting on
	 * @param pm the progress monitor observing on the action (if any)
	 */
	public void notifyAtomicActionStarted(long id, String label, XmlMarkupTool imt, XmAnnotation annot, XmDocumentMarkupPanel xdmp, ProgressMonitor pm) {
		for (int l = 0; l < this.atomicActionListeners.size(); l++)
			((GoldenGateXpediteAtomicActionListener) this.atomicActionListeners.get(l)).atomicActionStarted(id, label, imt, annot, xdmp, pm);
	}
	/**
	 * Notify all registered atomic action listeners that the running atomic
	 * action is finishing on an XML Document markup panel in a UI application
	 * built around this GoldenGATE Xpedite core. This method should be called
	 * by client UI code when an atomic action is finishing on an XML Document
	 * markup panel.
	 * @param id the unique ID of the finishing action
	 * @param xdmp the document editor panel the atomic action is finishing on
	 * @param pm the progress monitor observing on the action (if any)
	 */
	public void notifyAtomicActionFinishing(long id, XmDocumentMarkupPanel xdmp, ProgressMonitor pm) {
		for (int l = 0; l < this.atomicActionListeners.size(); l++)
			((GoldenGateXpediteAtomicActionListener) this.atomicActionListeners.get(l)).atomicActionFinishing(id, xdmp, pm);
	}
	
	/**
	 * Notify all registered atomic action listeners that the running atomic
	 * action has finished on an XML Document markup panel in a UI application
	 * built around this GoldenGATE Xpedite core. This method should be called
	 * by client UI code when an atomic action has finished on an XML Document
	 * markup panel.
	 * @param id the unique ID of the finished action
	 * @param xdmp the document editor panel the atomic action was finished on
	 * @param pm the progress monitor observing on the action (if any)
	 */
	public void notifyAtomicActionFinished(long id, XmDocumentMarkupPanel xdmp, ProgressMonitor pm) {
		for (int l = 0; l < this.atomicActionListeners.size(); l++)
			((GoldenGateXpediteAtomicActionListener) this.atomicActionListeners.get(l)).atomicActionFinished(id, xdmp, pm);
	}
//	
//	/**
//	 * Issue a notification of a change to the display extensions in a given
//	 * document markup panel. Use a null argument to indicate a change that
//	 * affects all document markup panels current in use.
//	 * @param idmp the document markup panel affected by the change
//	 */
//	public void notifyDisplayExtensionsModified(ImDocumentMarkupPanel idmp) {
//		for (int l = 0; l < this.displayExtensionListeners.size(); l++) try {
//			((DisplayExtensionListener) this.displayExtensionListeners.get(l)).displayExtensionsModified(idmp);
//		}
//		catch (Exception e) {
//			System.out.println("Error issuing display extension change notification: " + e.getMessage());
//			e.printStackTrace(System.out);
//		}
//	}
	
	/**
	 * Shut down the GoldenGATE Xpedite instance.
	 * @param pm a progress monitor receiving information on the shutdown
	 *            process
	 */
	public void exit(ProgressMonitor pm) {
		
		//	gather and store settings
		if (this.configuration.isDataEditable()) {
			String[] dpNames = UserInterfaceUtils.getDisplayPropertyNames();
			for (int n = 0; n < dpNames.length; n++) {
//				if (dpNames[n].startsWith("annot.") && dpNames[n].endsWith(".color"))
//					continue; // annotation colors are stored in core
//				else if (dpNames[n].startsWith("core."))
//					continue; // something else labeled as core setting
				if (GoldenGATE.isCoreDisplayProperty(dpNames[n]))
					continue;
				this.settings.setSetting(dpNames[n], UserInterfaceUtils.encodeDisplayProperty(dpNames[n]));
			}
			try {
				this.goldenGate.storeApplicationSettings("GgXpedite.cnfg", this.settings);
			}
			catch (IOException ioe) {
				System.out.println("Failed to store central GoldenGATE Xpedite settings; " + ioe.getMessage());
				ioe.printStackTrace(System.out);
			}
		}
		
		//	shut down underlying GoldenGATE core
		this.goldenGate.exit(pm);
	}
//	
//	/**
//	 * Retrieve the current status of local application settings for
//	 * GoldenGATE Editor, i.e., the contents of the local version of
//	 * <code>GgXpedite.cnfg</code> if the application was to exit right now.
//	 * Because this method is exclusively intended for exporting user
//	 * configurations, it only works in local master mode.
//	 * @return the contents of the local version of the settings file
//	 */
//	public Settings getLocalApplicationSettings() {
//		if (!this.configuration.isMasterConfiguration())
//			throw new IllegalStateException("Local settings can be accessed only in local master mode");
//		Settings set = new Settings();
//		String[] dpNames = UserInterfaceUtils.getDisplayPropertyNames();
//		for (int n = 0; n < dpNames.length; n++) {
//			if (dpNames[n].startsWith("annot.") && dpNames[n].endsWith(".color"))
//				continue; // annotation colors are stored in core
//			else if (dpNames[n].startsWith("core."))
//				continue; // something else labeled as core setting
//			set.setSetting(dpNames[n], UserInterfaceUtils.encodeDisplayProperty(dpNames[n]));
//		}
//		return this.goldenGate.getLocalApplicationSettings("GgXpedite.cnfg", set);
//	}
	/*
DO NOT DO THIS, using local settings makes preciously little sense (display preferences of exporting user ... might be color blind or like their dark mode or something)
- instead, add support for 'GgImagine.<configName>.cnfg' (to be held in data folder of configuration manager) ...
- ... as well as for export dedicated 'GgImagine.cnfg' (to be held in data folder of configuration manager)
==> 'GgImagine.cnfg' and 'GgImagine.local.cnfg' still belong to installation (use might be color blind or like their dark mode or something) ...
==> ... so those local settings need to prevail (and do, with new defaulting approach)
==> 'GgImagine.cnfg' from configuration provides means to inject configuration specific additions over installed defaults ...
==> ... e.g. special annotation types or attribute suggestions
	 */
	
	/**
	 * Create an instance of the GoldenGATE Xpedite core with a specific
	 * configuration.
	 * @param path the base path of the GoldenGATE Xpedite installation
	 * @param configuration the GoldenGateConfiguration to use
	 * @param pm a progress monitor receiving information on the startup
	 *            process
	 * @return a new GoldenGATE Xpedite instance to work with the specified
	 *         configuration
	 */
	public static synchronized GoldenGateXpedite openGoldenGATE(File path, GoldenGateConfiguration configuration, ProgressMonitor pm) throws IOException {
		return new GoldenGateXpedite(configuration, GoldenGATE.openGoldenGATE(path, configuration, pm), path);
	}
	
	/**
	 * Create an instance of the GoldenGATE Xpedite core with a GoldenGATE core
	 * wrapping a specific configuration.
	 * @param path the base path of the GoldenGATE Xpedite installation
	 * @param goldenGate the GoldenGATE core to use
	 * @return a new GoldenGATE Xpedite instance to work with the specified
	 *         configuration
	 */
	public static synchronized GoldenGateXpedite openGoldenGATE(File path, GoldenGATE goldenGate) throws IOException {
		return new GoldenGateXpedite(goldenGate.getConfiguration(), goldenGate, path);
	}
	
	//	UTF-16LE byte order mark
	private static final int UTF_16_LE_BOM_FIRST_BYTE = 0x00FF;//255;
	private static final int UTF_16_LE_BOM_SECOND_BYTE = 0x00FE;//254;
	
	//	UTF-16BE byte order mark
	private static final int UTF_16_BE_BOM_FIRST_BYTE = 0x00FE;//254;
	private static final int UTF_16_BE_BOM_SECOND_BYTE = 0x00FF;//255;
	
	//	UTF-8 byte order mark
	private static final int UTF_8_BOM_FIRST_BYTE = 0x00EF;//239;
	private static final int UTF_8_BOM_SECOND_BYTE = 0x00BB;//187;
	private static final int UTF_8_BOM_THIRD_BYTE = 0x00BF;//191;
	
	//	just in case this misbehaves in some odd case ...
	private static final boolean DEBUG_CHAR_ENCODING = false;
	
	/**
	 * Determine the character encoding of a sequence of XML bytes, and create
	 * an appropriate Reader for it. This method also observed and skips over
	 * any leading byte order marks.
	 * @param xmlBytes the byte sequence to create a reader for
	 * @return an InputStreamReader decoding the argument data
	 */
	public static InputStreamReader createReader(byte[] xmlBytes) throws IOException {
		if (DEBUG_CHAR_ENCODING) System.out.println("Determining character encoding ...");
		
		//	too little data for determining encoding
		if (xmlBytes.length < 3) {
			if (DEBUG_CHAR_ENCODING) System.out.print("  - stream too short for determining encoding, using defualt UTF-8");
			return new InputStreamReader(new ByteArrayInputStream(xmlBytes), "UTF-8");
		}
		
		//	check byte order marks
		if (DEBUG_CHAR_ENCODING) System.out.println("  - checking BOMs");
		if (DEBUG_CHAR_ENCODING) System.out.println("    - first byte is " + xmlBytes[0]);
		
		//	byte order mark for UTF-16 little endian
		if (UTF_16_LE_BOM_FIRST_BYTE == xmlBytes[0]) {
			if (DEBUG_CHAR_ENCODING) System.out.println("    - second byte is " + xmlBytes[1]);
			if (UTF_16_LE_BOM_SECOND_BYTE == xmlBytes[1]) {
				if (DEBUG_CHAR_ENCODING) System.out.println("  ==> recognized BOM of UTF-16 little endian.");
				return new InputStreamReader(new ByteArrayInputStream(xmlBytes, 2, xmlBytes.length), "UTF-16LE");
			}
		}
		
		//	byte order mark for UTF-16 big endian
		else if (UTF_16_BE_BOM_FIRST_BYTE == xmlBytes[0]) {
			if (DEBUG_CHAR_ENCODING) System.out.println("    - second byte is " + xmlBytes[1]);
			if (UTF_16_BE_BOM_SECOND_BYTE == xmlBytes[1]) {
				if (DEBUG_CHAR_ENCODING) System.out.println("  ==> recognized BOM of UTF-16 big endian.");
				return new InputStreamReader(new ByteArrayInputStream(xmlBytes, 2, xmlBytes.length), "UTF-16BE");
			}
		}
		
		//	byte order mark for UTF-8
		else if (UTF_8_BOM_FIRST_BYTE == xmlBytes[0]) {
			if (DEBUG_CHAR_ENCODING) System.out.println("    - second byte is " + xmlBytes[1]);
			if (UTF_8_BOM_SECOND_BYTE == xmlBytes[1]) {
				if (DEBUG_CHAR_ENCODING) System.out.println("    - third byte is " + xmlBytes[2]);
				if (UTF_8_BOM_THIRD_BYTE == xmlBytes[2]) {
					if (DEBUG_CHAR_ENCODING) System.out.println("  ==> recognized BOM of UTF-8.");
					return new InputStreamReader(new ByteArrayInputStream(xmlBytes, 3, xmlBytes.length), "UTF-8");
				}
			}
		}
		
		else if (DEBUG_CHAR_ENCODING) {
			System.out.println("    - second byte is " + xmlBytes[1]);
			System.out.println("    - third byte is " + xmlBytes[2]);
		}
		
		//	check for XML encoding specifications
		int firstChar = 0;
		while ((firstChar < xmlBytes.length) && ((xmlBytes[firstChar] < 0x20) || (0x7E < xmlBytes[firstChar])))
			firstChar++;
		
		//	file seems to start with an HTML or XML tag
		if ((firstChar < xmlBytes.length) && (xmlBytes[firstChar] == '<')) {
			StringBuffer tagBuffer = new StringBuffer();
			int tagLength = 0;
			while ((firstChar + tagLength) < xmlBytes.length) {
				char ch = ((char) xmlBytes[firstChar + tagLength]);
				tagBuffer.append(ch);
				if (ch == '>') {
					firstChar = (firstChar + tagLength + 1);
					break;
				}
				else tagLength++;
			}
			String tag = tagBuffer.toString();
			if (DEBUG_CHAR_ENCODING) System.out.println("  - read first tag: " + tag);
			
			//	jump over DOCTYPE declaration
			while (tag.startsWith("<!")) {
				
				//	find start of next tag
				while ((firstChar < xmlBytes.length) && (xmlBytes[firstChar] != '<'))
					firstChar++;
				
				//	read tag to end
				tagBuffer = new StringBuffer();
				tagLength = 0;
				while ((firstChar + tagLength) < xmlBytes.length) {
					char ch = ((char) xmlBytes[firstChar + tagLength]);
					tagBuffer.append(ch);
					if (ch == '>') {
						firstChar = (firstChar + tagLength + 1);
						break;
					}
					else tagLength++;
				}
				tag = tagBuffer.toString();
				if (DEBUG_CHAR_ENCODING) System.out.println("    - jumped to next tag: " + tag);
			}
			
			//	XML processing instruction <?xml version="1.0" encoding="UTF-8"?>
			if (tag.startsWith("<?") && tag.endsWith("?>")) {
				int split = tag.indexOf("encoding");
				if (split != -1) {
					String encoding = tag.substring(split + "encoding".length());
					split = encoding.indexOf('"');
					if (split != -1) {
						encoding = encoding.substring(split + 1);
						split = encoding.indexOf('"');
						if (split != -1) {
							encoding = encoding.substring(0, encoding.indexOf('"'));
							if (DEBUG_CHAR_ENCODING) System.out.println("  ==> using parsed encoding: " + encoding);
							return new InputStreamReader(new ByteArrayInputStream(xmlBytes), encoding);
						}
					}
				}
			}
		}
		
		//	investigate bytes proper
		String encoding = EasyIO.inferEncoding(xmlBytes);
		if (DEBUG_CHAR_ENCODING) System.out.println("  - bytes suggest encoding " + encoding);
		
		//	use UTF-8 as default fallback, by far most common by now
		if (encoding == null) {
			if (DEBUG_CHAR_ENCODING) System.out.println("  ==> defaulting to UTF-8");
			return new InputStreamReader(new ByteArrayInputStream(xmlBytes), "UTF-8");
		}
		
		//	use whatever bytes suggest
		else {
			if (DEBUG_CHAR_ENCODING) System.out.println("  ==> using suggested encoding");
			return new InputStreamReader(new ByteArrayInputStream(xmlBytes), encoding);
		}
	}
}