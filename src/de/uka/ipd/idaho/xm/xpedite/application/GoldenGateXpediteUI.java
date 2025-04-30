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
package de.uka.ipd.idaho.xm.xpedite.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import de.uka.ipd.idaho.easyIO.settings.Settings;
import de.uka.ipd.idaho.gamta.util.ProgressMonitor;
import de.uka.ipd.idaho.gamta.util.constants.LiteratureConstants;
import de.uka.ipd.idaho.gamta.util.swing.ProgressMonitorDialog;
import de.uka.ipd.idaho.goldenGate.GoldenGATE;
import de.uka.ipd.idaho.goldenGate.GoldenGateConstants;
import de.uka.ipd.idaho.goldenGate.plugins.ResourceSplashScreen;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI;
import de.uka.ipd.idaho.goldenGate.ui.GoldenGateUI.DocumentDisplay;
import de.uka.ipd.idaho.goldenGate.ui.NamedElementUsageStatistics;
import de.uka.ipd.idaho.goldenGate.ui.UserInterfaceUtils;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuBar;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuElement;
import de.uka.ipd.idaho.goldenGate.ui.WindowMenuFunction;
import de.uka.ipd.idaho.goldenGate.util.DialogPanel;
import de.uka.ipd.idaho.xm.XmDocument;
import de.uka.ipd.idaho.xm.XmSupplement;
import de.uka.ipd.idaho.xm.util.XmDocumentData.XmDocumentEntry;
import de.uka.ipd.idaho.xm.util.XmDocumentIO;
import de.uka.ipd.idaho.xm.xml.XmlDocumentConverter;
import de.uka.ipd.idaho.xm.xpedite.GoldenGateXpedite;
import de.uka.ipd.idaho.xm.xpedite.plugins.GoldenGateXpediteDocumentListener.CancelSavingException;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlConversionProfile;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlConversionProfileProvider;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentIoProvider;
import de.uka.ipd.idaho.xm.xpedite.swing.XmlDocumentMarkupUI;
import de.uka.ipd.idaho.xm.xpedite.swing.XmlDocumentMarkupUI.XmlDocumentEditorTab;

/**
 * Default GUI for GoldenGATE Xpedite
 * 
 * @author sautter
 */
public class GoldenGateXpediteUI extends JFrame implements LiteratureConstants, GoldenGateConstants {
	private GoldenGateXpedite ggXpedite;
	private GoldenGATE goldenGate;
//	private Settings ggxConfig;
	
	private GgxDoumentMarkupUI ui;
	
	private JFileChooser fileChooser = new JFileChooser();
	private Dimension fileChooserSize = new Dimension(750, 500);
	private XmlConversionFileFilter[] xmlConversionFileFilters = {};
	private WindowMenuElement[] fileMenuElements = null;
	
	private File docCacheRoot;
	
//	GoldenGateXpediteUI(GoldenGateXpedite ggXpedite, File docCacheRoot, Settings ggxConfig) {
	GoldenGateXpediteUI(GoldenGateXpedite ggXpedite, String specVersionDate, File docCacheRoot) {
		super("GoldenGATE Xpedite" + ((specVersionDate == null) ? "" : (" (version " + specVersionDate + ")")) + " - " + ggXpedite.getConfigurationName());
		this.ggXpedite = ggXpedite;
		this.goldenGate = this.ggXpedite.getGoldenGATE();
//		this.ggxConfig = ggxConfig;
		
		//	set window icon
		this.setIconImage(this.ggXpedite.getGoldenGateIcon());
		
		//	set folder for caching XMF contents
		this.docCacheRoot = docCacheRoot;
		
		//	get XML conversion profiles
		XmlConversionProfileProvider[] xcpps = this.ggXpedite.getXmlConversionProfileProviders();
		if (xcpps.length != 0) {
			ArrayList xcffs = new ArrayList();
			for (int p = 0; p < xcpps.length; p++) {
				XmlConversionProfile[] xcps = xcpps[p].getConversionProfiles();
				if (xcps == null)
					continue;
				for (int f = 0; f < xcps.length; f++)
					xcffs.add(new XmlConversionFileFilter(xcps[f]));
			}
			if (xcffs.size() != 0)
				this.xmlConversionFileFilters = ((XmlConversionFileFilter[]) xcffs.toArray(new XmlConversionFileFilter[xcffs.size()]));
		}
		
		//	configure file chooser
		this.fileChooser.setMultiSelectionEnabled(false);
		this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		this.fileChooser.setSelectedFile(new File((this.ggxConfig.getSetting("lastDocFolder", (new File(".")).getAbsolutePath())), " ")); // we need this dummy file name so the folder is actually opened instead of being selected in its parent folder
		Object lastDocFolder = UserInterfaceUtils.getDisplayProperty("lastDocFolder");
		if (lastDocFolder instanceof String)
			this.fileChooser.setSelectedFile(new File(lastDocFolder.toString(), " ")); // we need this dummy file name so the folder is actually opened instead of being selected in its parent folder
		else this.fileChooser.setSelectedFile(new File((new File(".")).getAbsolutePath(), " ")); // we need this dummy file name so the folder is actually opened instead of being selected in its parent folder
		
		//	initialize basis for selection action usage statistics
		Settings actionUsageData = this.goldenGate.getApplicationSettings("GgXpedite.actionUsage.cnfg");
		NamedElementUsageStatistics.initializeData((actionUsageData == null) ? new Settings() : actionUsageData);
		
		//	create UI
//		this.ui = new GgxDoumentMarkupUI(this.ggXpedite, this.ggxConfig);
		this.ui = new GgxDoumentMarkupUI(this.ggXpedite);
		
		//	make sure we exit on window closing
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				exit();
			}
		});
		this.addWindowFocusListener(this.ui);
		
		//	register UI to GoldenGATE core
		this.goldenGate.setUserInterface(this.ui);
		
		//	assemble major parts
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.ui, BorderLayout.CENTER);
		this.setSize(1000, 800);
		this.setLocationRelativeTo(null);
	}
	
	WindowMenuElement[] getFileMenuElements() {
		if (this.fileMenuElements == null)
			this.initFileMenuElements();
		return this.fileMenuElements;
	}
	private void initFileMenuElements() {
		ArrayList fmes = new ArrayList();
		int fmeFlags;
		
		//	get document IO providers
		XmlDocumentIoProvider[] docIoProviders = this.ggXpedite.getDocumentIoProviders();
		
		//	add built-in loading options
		fmeFlags = 0;
		fmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
		fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
		fmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.FILE_MENU_NAME, fmeFlags);
		fmes.add(new WindowMenuFunction("ggXpedite", "openDoc", "Open Document", "Load a document from a local file", fmeFlags) {
			private FileFilter loadFormat = genericXmlFileFilter;
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return true;
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				clearFileFilters(fileChooser);
				fileChooser.addChoosableFileFilter(xmfFileFilter);
				fileChooser.addChoosableFileFilter(xmdFileFilter);
				fileChooser.addChoosableFileFilter(batchCacheDocFileFilter);
				fileChooser.addChoosableFileFilter(genericXmlFileFilter);
				for (int f = 0; f < xmlConversionFileFilters.length; f++)
					fileChooser.addChoosableFileFilter(xmlConversionFileFilters[f]);
				fileChooser.setFileFilter((this.loadFormat == null) ? xmfFileFilter : this.loadFormat);
				
				fileChooser.setPreferredSize(fileChooserSize);
				
				int choice = fileChooser.showOpenDialog(GoldenGateXpediteUI.this);
				
				fileChooser.getSize(fileChooserSize);
				
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
				
				File file = fileChooser.getSelectedFile();
				this.loadFormat = fileChooser.getFileFilter();
				this.loadFile(file);
			}
			private void loadFile(File file) {
				UserInterfaceUtils.setDisplayProperty("lastDocFolder", file.getParentFile().getAbsolutePath());
				try {
					if (this.loadFormat == xmdFileFilter) {
						loadDocument(file.getName(), file, null, this.loadFormat, null, -1);
					}
					else if (this.loadFormat == batchCacheDocFileFilter) {
						loadDocument(file.getParentFile().getName(), file, null, this.loadFormat, null, -1);
					}
					else {
						InputStream in = new BufferedInputStream(new FileInputStream(file));
						loadDocument(file.getName(), file, null, this.loadFormat, in, file.length());
						in.close();
					}
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(GoldenGateXpediteUI.this, ("An error occurred while loading a document from '" + file.getAbsolutePath() + "':\n" + e.getMessage()), "Error Loading Document", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace(System.out);
				}
			}
		});
		
		fmeFlags = 0;
		fmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
		fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
		fmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.FILE_MENU_NAME, fmeFlags);
		fmes.add(new WindowMenuFunction("ggXpedite", "loadUrl", "Load Document from URL", "Load a document from a URL", fmeFlags) {
			private FileFilter loadFormat = genericXmlFileFilter;
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return true;
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				UrlLoadDialog uld = new UrlLoadDialog(null, this.loadFormat/*, this.fontMode, this.fontCharset, this.loadFlags*/);
				uld.setVisible(true);
				this.loadFormat = uld.getFormat();
			}
		});
		
		//	add loading items from custom document IO providers
		for (int p = 0; p < docIoProviders.length; p++) {
			String sourceName = docIoProviders[p].getLoadSourceName();
			if (sourceName == null)
				continue;
			final XmlDocumentIoProvider xdip = docIoProviders[p];
			fmeFlags = 0;
			fmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
			fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
			fmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.FILE_MENU_NAME, fmeFlags);
			fmes.add(new WindowMenuFunction(xdip, "load", ("Load Document from " + sourceName), ("Load a document from " + sourceName), fmeFlags) {
				public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
					return true;
				}
				public void execute(GoldenGateUI ggui, DocumentDisplay display) {
					loadDocument(xdip);
				}
			});
		}
		
		//	add built-in saving options
		fmeFlags = 0;
		fmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
		fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
		fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
		fmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.FILE_MENU_NAME, fmeFlags);
		fmes.add(new WindowMenuFunction("ggXpedite", "saveDocAs", "Save Document As", "Save the current document to a local file in a new format", fmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return (display instanceof GgxDocumentEditorTab);
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				if (display instanceof GgxDocumentEditorTab)
					((GgxDocumentEditorTab) display).saveAs(fileChooser, fileChooserSize);
			}
		});
		
		//	add saving items from custom document IO providers
		for (int p = 0; p < docIoProviders.length; p++) {
			String destName = docIoProviders[p].getSaveDestinationName();
			if (destName == null)
				continue;
			final XmlDocumentIoProvider xdip = docIoProviders[p];
			fmeFlags = 0;
			fmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
			fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
			fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_DOCUMENT;
			fmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.FILE_MENU_NAME, fmeFlags);
			fmes.add(new WindowMenuFunction(xdip, "save", ("Save Document to " + destName), ("Save the current document to " + destName), fmeFlags) {
				public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
					return (display instanceof GgxDocumentEditorTab);
				}
				public void execute(GoldenGateUI ggui, DocumentDisplay display) {
					if (display instanceof GgxDocumentEditorTab)
						((GgxDocumentEditorTab) display).saveAs(xdip);
				}
			});
		}
		
		//	add 'exit' option
		fmeFlags = 0;
		fmeFlags |= WindowMenuElement.PROPERTY_AVAILABLE_DESKTOP;
		fmeFlags |= WindowMenuElement.PROPERTY_REQUIRES_MAIN_WINDOW;
		fmeFlags = WindowMenuElement.encodePreferredMenuName(WindowMenuBar.FILE_MENU_NAME, fmeFlags);
		fmes.add(new WindowMenuFunction("ggXpedite", "exit", "Exit", "Close GoldenGATE Xpedite", fmeFlags) {
			public boolean checkAvailable(GoldenGateUI ggui, DocumentDisplay display) {
				return true;
			}
			public void execute(GoldenGateUI ggui, DocumentDisplay display) {
				exit();
			}
		});
		
		//	finally ...
		this.fileMenuElements = ((WindowMenuElement[]) fmes.toArray(new WindowMenuElement[fmes.size()]));
	}
	
	private void handleDrop(Transferable transfer) {
		DataFlavor[] dataFlavors = transfer.getTransferDataFlavors();
		for (int f = 0; f < dataFlavors.length; f++) {
			System.out.println("Trying data flavor " + dataFlavors[f].toString());
			System.out.println(" - MIME type is " + dataFlavors[f].getMimeType());
			System.out.println(" - representation class is " + dataFlavors[f].getRepresentationClass());
			
			//	nothing to work with
			if (dataFlavors[f].getMimeType() == null)
				continue;
			
			//	get basic data
			String mimeType = dataFlavors[f].getMimeType();
			Class representationClass = dataFlavors[f].getRepresentationClass();
			
			//	file drop
			if (("application/x-java-file-list".equalsIgnoreCase(mimeType) || mimeType.toLowerCase().startsWith("application/x-java-file-list; class=")) && List.class.isAssignableFrom(representationClass)) try {
				List droppedFileList = ((List) transfer.getTransferData(dataFlavors[f]));
				for (int t = 0; t < droppedFileList.size(); t++)
					loadDroppedFile((File) droppedFileList.get(t));
				return;
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
			}
			
			//	URL drop
			if (("application/x-java-url".equalsIgnoreCase(mimeType) || mimeType.toLowerCase().startsWith("application/x-java-url; class=")) && URL.class.isAssignableFrom(representationClass)) try {
				URL droppedUrl = ((URL) transfer.getTransferData(dataFlavors[f]));
				String droppedUrlString = droppedUrl.toString();
				FileFilter matchFileFilter = null;
				if (droppedUrlString.toLowerCase().matches("http\\:\\/\\/(.+\\/)+.+\\.xmf"))
					matchFileFilter = xmfFileFilter;
				else if (droppedUrlString.toLowerCase().matches("http\\:\\/\\/(.+\\/)+.+\\.xml"))
					matchFileFilter = genericXmlFileFilter;
				//	TODO observe XML conversion profiles (maybe need match and priority getters)
				UrlLoadDialog uld = new UrlLoadDialog(droppedUrlString, matchFileFilter/*, fontDecoderMode, fontDecoderCharset, pdfLoadFlags*/);
				uld.setVisible(true);
				return;
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
			}
		}
	}
	
	private class UrlLoadDialog extends DialogPanel {
		private JTextField urlInput = new JTextField("http://");
		private JComboBox formatChooser = new JComboBox();
		private JSpinner timeoutChooser = new JSpinner(new SpinnerNumberModel(15, 5, 300, 5));
		
		UrlLoadDialog(String urlString, FileFilter format/*, char fontMode, FontDecoderCharset fontCharset, int scanFlags*/) {
			super("Open Document from URL", true);
			
			if (urlString != null)
				this.urlInput.setText(urlString);
			this.urlInput.setBorder(BorderFactory.createLoweredBevelBorder());
			this.urlInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
			this.urlInput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					loadUrl();
				}
			});
			
			this.formatChooser.addItem(xmfFileFilter);
			this.formatChooser.addItem(genericXmlFileFilter);
			for (int f = 0; f < xmlConversionFileFilters.length; f++)
				this.formatChooser.addItem(xmlConversionFileFilters[f]);
			this.formatChooser.setSelectedItem((format == null) ? xmfFileFilter : format);
			this.formatChooser.setBorder(BorderFactory.createLoweredBevelBorder());
			this.formatChooser.setEditable(false);
			
			this.timeoutChooser.setBorder(BorderFactory.createLoweredBevelBorder());
			
			JPanel selectorPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.weighty = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets.top = 5;
			gbc.insets.left = 5;
			gbc.insets.right = 5;
			gbc.insets.bottom = 5;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.weightx = 0;
			selectorPanel.add(new JLabel("URL"), gbc.clone());
			gbc.gridx = 1;
			gbc.weightx = 1;
			selectorPanel.add(this.urlInput, gbc.clone());
			
			gbc.gridy = 1;
			gbc.gridx = 0;
			gbc.weightx = 0;
			selectorPanel.add(new JLabel("Format"), gbc.clone());
			gbc.gridx = 1;
			gbc.weightx = 1;
			selectorPanel.add(this.formatChooser, gbc.clone());
			
			//	initialize timeout selector
			JPanel timeoutPanel = new JPanel(new BorderLayout(), true);
			timeoutPanel.add(new JLabel("Timeout (seconds) "), BorderLayout.WEST);
			timeoutPanel.add(this.timeoutChooser, BorderLayout.CENTER);
			
			//	initialize buttons
			JButton commitButton = new JButton("Open Document");
			commitButton.setBorder(BorderFactory.createRaisedBevelBorder());
			commitButton.setPreferredSize(new Dimension(100, 21));
			commitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					loadUrl();
				}
			});
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
			cancelButton.setPreferredSize(new Dimension(100, 21));
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			
			//	assemble button panel
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttonPanel.add(timeoutPanel);
			buttonPanel.add(commitButton);
			buttonPanel.add(cancelButton);
			
			//	put the whole stuff together
			this.add(selectorPanel, BorderLayout.NORTH);
			this.add(buttonPanel, BorderLayout.SOUTH);
			
			//	configure dialog proper
			this.setResizable(true);
			this.setSize(new Dimension(500, 150));
			this.setLocationRelativeTo(GoldenGateXpediteUI.this);
			
			//	if we have a URL and a file format, we can start loading right after dialog comes up (load from EDT, though)
			if ((urlString != null) && (format != null)) {
				Thread loadTrigger = new Thread() {
					public void run() {
						while (!getDialog().isVisible()) try {
							Thread.sleep(100);
						} catch (InterruptedException ie) {}
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								loadUrl();
							}
						});
					}
				};
				loadTrigger.start();
			}
		}
		
		void loadUrl() {
			String urlString = this.urlInput.getText();
			try {
				this.loadUrl(urlString);
			}
			catch (Exception e) {
				e.printStackTrace(System.out);
				JOptionPane.showMessageDialog(this, ("An error occurred while loading a document from '" + urlString + "':\n  " + e.getMessage() + "\nIf the error was due to a read timeout, increasing the timeout might fix it."), "Error Loading Document", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		void loadUrl(final String urlString) throws IOException {
			final URL url = new URL(urlString);
			
			final int timeoutMillis = (Integer.parseInt(this.timeoutChooser.getValue().toString()) * 1000);
			final long[] lastReadMillis = {System.currentTimeMillis()};
			
			final HttpURLConnection[] urlCon = {null};
			final InputStream[] urlIn = {null};
			final byte[][] urlData = {null};
			final IOException[] error = {null};
			
			//	use progress monitor
			final ProgressMonitorDialog urlLoadPm = new ProgressMonitorDialog(this.getDialog(), ("Buffering Data from '" + urlString + "'"));
			urlLoadPm.setBaseProgress(0);
			urlLoadPm.setMaxProgress(100);
			urlLoadPm.setSize(this.getSize());
			urlLoadPm.setLocationRelativeTo(this.getDialog());
			
			//	use extra thread and progress dialog to buffer document
			final Thread urlLoader = new Thread() {
				public void run() {
					while (!urlLoadPm.getWindow().isVisible()) try {
						Thread.sleep(50);
					} catch (InterruptedException ie) {}
					
					try {
						urlLoadPm.setInfo("Connecting to source");
						urlCon[0] = ((HttpURLConnection) url.openConnection());
						urlCon[0].setRequestMethod("GET");
						urlCon[0].setRequestProperty("User-Agent", "GoldenGATE Xpedite");
						urlCon[0].connect();
						urlLoadPm.setInfo("Connection established");
						urlIn[0] = new BufferedInputStream(urlCon[0].getInputStream());
						urlLoadPm.setInfo("Got input stream");
						if (error[0] != null) // we might have timed out by now
							return;
						lastReadMillis[0] = System.currentTimeMillis();
						int dataBytesTotal = ((urlCon[0].getContentLength() == -1) ? (1024 * 1024) : urlCon[0].getContentLength()); // start with estimate of 1MB if content length missing
						int dataBytesRead = 0;
						ByteArrayOutputStream dataBytes = new ByteArrayOutputStream();
						byte[] dataByteBuffer = new byte[1024];
						for (int r; (r = urlIn[0].read(dataByteBuffer, 0, dataByteBuffer.length)) != -1;) {
							dataBytes.write(dataByteBuffer, 0, r);
							lastReadMillis[0] = System.currentTimeMillis();
							if (error[0] != null)
								break;
							dataBytesRead += r;
							urlLoadPm.setInfo("Read " + r + " more bytes");
							while (dataBytesTotal < dataBytesRead)
								dataBytesTotal += (1024 * 512); // increment estimate by 512KB
							urlLoadPm.setProgress((dataBytesRead * 100) / dataBytesTotal);
						}
						urlIn[0].close();
						urlData[0] = dataBytes.toByteArray();
						urlLoadPm.setInfo("Data buffered completely");
						urlLoadPm.setProgress(100);
					}
					catch (IOException ioe) {
						error[0] = ioe;
						urlLoadPm.setInfo("Error buffering data: " + ioe.getMessage());
					}
					finally {
						urlLoadPm.close();
					}
				}
			};
			urlLoader.start();
			
			final Thread timeoutGuard = new Thread() {
				public void run() {
					while (!urlLoadPm.getWindow().isVisible()) try {
						Thread.sleep(50);
					} catch (InterruptedException ie) {}
					
					try {
						while (true) {
							try {
								Thread.sleep(timeoutMillis / 5);
							} catch (InterruptedException ie) {}
							
							//	we're done reading
							if (urlData[0] != null)
								return;
							
							//	we're on time
							if (System.currentTimeMillis() < (lastReadMillis[0] + timeoutMillis))
								continue;
							
							//	connection timeout
							if (urlIn[0] == null) {
								urlLoader.interrupt();
								error[0] = new IOException("Timeout establishing connection to '" + urlString + "'");
							}
							
							//	read timeout (exception is thrown from reader in that case)
							else try {
								urlLoader.interrupt();
								urlIn[0].close();
							} catch (IOException ioe) {}
							
							//	whichever way, we're done here
							return;
						}
					}
					finally {
						urlLoadPm.close();
					}
				}
			};
			timeoutGuard.start();
			
			//	open progress monitor (waits for buffering to time out or complete)
			urlLoadPm.popUp(true);
			
			//	throw any exception that might have occurred
			if (error[0] != null)
				throw error[0];
			
			//	delegate to main loading method
			this.dispose();
			String docName = urlString;
			if (docName.indexOf("//") != -1)
				docName = docName.substring(docName.indexOf("//") + "//".length());
			docName = docName.replaceAll("[\\/\\:]+", "_");
			loadDocument(docName, null, urlString, this.getFormat(), new ByteArrayInputStream(urlData[0]), urlData[0].length);
		}
		
		FileFilter getFormat() {
			return ((FileFilter) this.formatChooser.getSelectedItem());
		}
	}
	
	void loadDocument(final String docName, final File docSource, final String docSourceUrl, final FileFilter fileFilter, final InputStream in, final long inLength) throws IOException {
		
		//	load XMF
		if (fileFilter == xmfFileFilter) {
			final IOException[] loadException = {null};
			final ResourceSplashScreen loadScreen = new ResourceSplashScreen(this, ("Loading XMF Archive '" + docName + "'"), "", true, false);
			System.out.println("Creating load thread");
			Thread loadThread = new Thread("LoaderThread") {
				public void run() {
					try {
						loadScreen.setStep("Loading XMF Archive");
						XmDocument doc;
						File docDataCache = null;
						
						//	wait for load screen to show
						while (!loadScreen.isVisible()) try {
							Thread.sleep(10);
						} catch (InterruptedException ie) {}
						
						 // 50 MB should OK to hold in memory
						if (inLength < (1024 * 1024 * 50))
							doc = XmDocumentIO.loadDocument(in, loadScreen, inLength);
						
						//	use disk based cache if XMF is larger
						else {
							docDataCache = new File(docCacheRoot, docName);
							docDataCache.mkdirs();
							doc = XmDocumentIO.loadDocument(in, docDataCache, loadScreen, inLength);
						}
						in.close();
						
						//	add document name and (if any) source URL attributes
						doc.setAttribute(DOCUMENT_NAME_ATTRIBUTE, docName);
						if ((docSourceUrl != null) && !doc.hasAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE))
							doc.setAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE, docSourceUrl);
						
						//	register cache folder for cleanup when document closed
						if (docDataCache != null)
							registerDocDataCache(doc.docId, docDataCache);
						
						//	open document in UI
						ggXpedite.notifyDocumentOpened(doc, ((docSource == null) ? docSourceUrl : docSource), loadScreen);
						ui.openDocument(new GgxDocumentEditorTab(doc, docName, docSource, fileFilter, null));
					}
					catch (IOException ioe) {
						loadException[0] = ioe;
					}
					finally {
						loadScreen.dispose();
					}
				}
			};
			loadThread.start();
			loadScreen.setVisible(true);
			if (loadException[0] == null)
				return;
			else throw loadException[0];
		}
		
		//	load IMD
		if (fileFilter == xmdFileFilter) {
			final IOException[] loadException = {null};
			final ResourceSplashScreen loadScreen = new ResourceSplashScreen(this, ("Loading XMF Directory '" + docName + "'"), "", true, false);
			System.out.println("Creating load thread");
			Thread loadThread = new Thread("LoaderThread") {
				public void run() {
					try {
						
						//	wait for load screen to show
						while (!loadScreen.isVisible()) try {
							Thread.sleep(10);
						} catch (InterruptedException ie) {}
						
						//	get entry folder and load document
						File docFolder = new File(docSource.getAbsolutePath() + "ir");
						if (!docFolder.exists())
							throw new FileNotFoundException("Data directory not found for " + docSource.getName());
						loadScreen.setStep("Loading XMF Directory");
						XmDocument doc = XmDocumentIO.loadDocument(docFolder, loadScreen);
						
						//	add document name and (if any) source URL attributes
						doc.setAttribute(DOCUMENT_NAME_ATTRIBUTE, docName);
						if ((docSourceUrl != null) && !doc.hasAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE))
							doc.setAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE, docSourceUrl);
						
						//	open document in UI
						ggXpedite.notifyDocumentOpened(doc, docFolder, loadScreen);
						ui.openDocument(new GgxDocumentEditorTab(doc, docName, docSource, fileFilter, null));
					}
					catch (IOException ioe) {
						loadException[0] = ioe;
					}
					finally {
						loadScreen.dispose();
					}
				}
			};
			loadThread.start();
			loadScreen.setVisible(true);
			if (loadException[0] == null)
				return;
			else throw loadException[0];
		}
		
		//	load batch cached IMD (helps with recovery)
		if (fileFilter == batchCacheDocFileFilter) {
			final IOException[] loadException = {null};
			final ResourceSplashScreen loadScreen = new ResourceSplashScreen(this, ("Loading Batch Cached Document '" + docName + "'"), "", true, false);
			System.out.println("Creating load thread");
			Thread loadThread = new Thread("LoaderThread") {
				public void run() {
					try {
						
						//	wait for load screen to show
						while (!loadScreen.isVisible()) try {
							Thread.sleep(10);
						} catch (InterruptedException ie) {}
						
						//	load list of document entries
						ArrayList docEntries = new ArrayList();
						BufferedReader docEntryIn = new BufferedReader(new InputStreamReader(new FileInputStream(docSource), "UTF-8"));
						for (String docEntryLine; (docEntryLine = docEntryIn.readLine()) != null;) {
							XmDocumentEntry docEntry = XmDocumentEntry.fromTabString(docEntryLine);
							if (docEntry != null)
								docEntries.add(docEntry);
						}
						docEntryIn.close();
						
						//	load document from entry folder
						loadScreen.setStep("Loading Batch Cached Document");
						XmDocument doc = XmDocumentIO.loadDocument(docSource.getParentFile(), ((XmDocumentEntry[]) docEntries.toArray(new XmDocumentEntry[docEntries.size()])), loadScreen);
						
						//	add document name and (if any) source URL attributes
						doc.setAttribute(DOCUMENT_NAME_ATTRIBUTE, docName);
						if ((docSourceUrl != null) && !doc.hasAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE))
							doc.setAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE, docSourceUrl);
						
						//	open document in UI
						ggXpedite.notifyDocumentOpened(doc, docSource.getParentFile(), loadScreen);
						ui.openDocument(new GgxDocumentEditorTab(doc, docName, docSource, fileFilter, null));
					}
					catch (IOException ioe) {
						loadException[0] = ioe;
					}
					finally {
						loadScreen.dispose();
					}
				}
			};
			loadThread.start();
			loadScreen.setVisible(true);
			if (loadException[0] == null)
				return;
			else throw loadException[0];
		}
		
		//	load and convert XML
		//	TODO observe XML conversion profiles (maybe need match and priority getters)
		//	==> TODO likely load tree first, and then match on raw XML tree
		if (docName.toLowerCase().endsWith(".xml") || (fileFilter instanceof XmlConversionFileFilter)) {
			final IOException[] loadException = {null};
			final ResourceSplashScreen loadScreen = new ResourceSplashScreen(this, ("Loading XML '" + docName + "'"), "", true, false);
			Thread loadThread = new Thread("LoaderThread") {
				public void run() {
					try {
						loadScreen.setStep("Loading XML Document");
						XmDocument doc;
						
						//	wait for load screen to show
						while (!loadScreen.isVisible()) try {
							Thread.sleep(10);
						} catch (InterruptedException ie) {}
						
						//	cache input document
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						for (int r; (r = in.read(buffer, 0, buffer.length)) != -1;)
							baos.write(buffer, 0, r);
						in.close();
						byte[] xmlBytes = baos.toByteArray();
						
						//	convert document TODOne determine encoding !!! (UTF-8 should be safe bet for now, but be thorough !!!)
//						BufferedReader xmlBr = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(xmlBytes), "UTF-8"));
						InputStreamReader xmlByteDecoder = GoldenGateXpedite.createReader(xmlBytes);
						BufferedReader xmlBr = new BufferedReader(xmlByteDecoder);
						if (fileFilter instanceof XmlConversionFileFilter)
							doc = XmlDocumentConverter.convertXml(xmlBr, ((XmlConversionFileFilter) fileFilter).conversionProfile);
						else doc = XmlDocumentConverter.convertXml(xmlBr, null);
						doc.addSupplement(XmSupplement.Source.createSource(doc, "text/xml", xmlBytes));
						
						//	open document
						doc.setAttribute(DOCUMENT_NAME_ATTRIBUTE, docName);
						if ((docSourceUrl != null) && !doc.hasAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE))
							doc.setAttribute(DOCUMENT_SOURCE_LINK_ATTRIBUTE, docSourceUrl);
						ggXpedite.notifyDocumentOpened(doc, ((docSource == null) ? docSourceUrl : docSource), loadScreen);
						ui.openDocument(new GgxDocumentEditorTab(doc, docName, docSource, null, null));
					}
					catch (IOException ioe) {
						loadException[0] = ioe;
					}
					finally {
						loadScreen.dispose();
					}
				}
			};
			loadThread.start();
			loadScreen.setVisible(true);
			if (loadException[0] == null)
				return;
			else throw loadException[0];
		}
	}
	
	void loadDroppedFile(String droppedFilePath) {
		this.loadDroppedFile(new File(droppedFilePath));
	}
	
	void loadDroppedFile(File droppedFile) {
		try {
			FileFilter matchFileFilter;
			if (xmfFileFilter.accept(droppedFile))
				matchFileFilter = xmfFileFilter;
			else if (xmdFileFilter.accept(droppedFile))
				matchFileFilter = xmfFileFilter;
			else if (genericXmlFileFilter.accept(droppedFile))
				matchFileFilter = genericXmlFileFilter;
			else return;
			InputStream in = new BufferedInputStream(new FileInputStream(droppedFile));
			this.loadDocument(droppedFile.getName(), droppedFile, null, matchFileFilter, in, droppedFile.length());
			in.close();
		}
		catch (SecurityException se) {
			System.out.println("Error opening document '" + droppedFile.getName() + "':\n   " + se.getClass().getName() + " (" + se.getMessage() + ")");
			se.printStackTrace(System.out);
			JOptionPane.showMessageDialog(GoldenGateXpediteUI.this, ("Not allowed to open file '" + droppedFile.getName() + "':\n" + se.getMessage() + "\n\nIf you are currently running GoldenGATE Editor as an applet, your\nbrowser's security mechanisms might prevent reading files from your local disc."), "Not Allowed To Open File", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e) {
			System.out.println("Error opening document '" + droppedFile.getAbsolutePath() + "':\n   " + e.getClass().getName() + " (" + e.getMessage() + ")");
			e.printStackTrace(System.out);
			JOptionPane.showMessageDialog(GoldenGateXpediteUI.this, ("Could not open file '" + droppedFile.getAbsolutePath() + "':\n" + e.getMessage()), "Error Opening File", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	void loadDocument(final XmlDocumentIoProvider xdip) {
		final ResourceSplashScreen loadScreen = new ResourceSplashScreen(this, ("Loading Document from " + xdip.getLoadSourceName()), "", true, false);
		System.out.println("Creating load thread");
		Thread loadThread = new Thread("LoaderThread") {
			public void run() {
				try {
					loadScreen.setStep("Loading Document");
					while (!loadScreen.isVisible()) try {
						Thread.sleep(10);
					} catch (InterruptedException ie) {}
					
					XmDocument doc = xdip.loadDocument(loadScreen);
					if (doc == null)
						return;
					String docName = ((String) doc.getAttribute(DOCUMENT_NAME_ATTRIBUTE));
					
					ggXpedite.notifyDocumentOpened(doc, xdip, loadScreen);
					ui.openDocument(new GgxDocumentEditorTab(doc, docName, null, null, ((xdip.getSaveDestinationName() == null) ? null : xdip)));
				}
				finally {
					loadScreen.dispose();
				}
			}
		};
		loadThread.start();
		loadScreen.setVisible(true);
	}
	
	private Map docDataCachePathsById = Collections.synchronizedMap(new HashMap());
	void registerDocDataCache(String docId, File docDataCache) {
		this.docDataCachePathsById.put(docId, docDataCache.getAbsolutePath());
	}
	
	void exit() {
		if (!this.ui.close())
			return;
		Settings actionUsageData = new Settings();
		NamedElementUsageStatistics.storeData(actionUsageData);
		try {
			this.goldenGate.storeApplicationSettings("GgXpedite.actionUsage.cnfg", actionUsageData);
		}
		catch (IOException ioe) {
			System.out.println("Failed to store central GoldenGATE Xpedite action usage statistics; " + ioe.getMessage());
			ioe.printStackTrace(System.out);
		}
		this.ggXpedite.exit(ProgressMonitor.dummy);
		this.dispose();
	}
	
	private class GgxDoumentMarkupUI extends XmlDocumentMarkupUI {
//		GgxDoumentMarkupUI(GoldenGateXpedite ggXpedite, Settings ggxConfig) {
		GgxDoumentMarkupUI(GoldenGateXpedite ggXpedite) {
//			super(ggXpedite, ggxConfig, null, null);
			super(ggXpedite, null, null);
		}
//		protected FileMenuItem[] getFileMenuItems() {
//			return GoldenGateXpediteUI.this.getFileMenuItems();
//		}
		protected WindowMenuElement[] getFileMenuElements() {
			return GoldenGateXpediteUI.this.getFileMenuElements();
		}
		protected Window getMainWindow() {
			return GoldenGateXpediteUI.this;
		}
		protected void handleDrop(Transferable dropped) {
			GoldenGateXpediteUI.this.handleDrop(dropped);
		}
		protected File getLikelyExportDestination(XmlDocumentEditorTab xdet) {
			File file = ((GgxDocumentEditorTab) xdet).docSource;
			return ((file == null) ? GoldenGateXpediteUI.this.fileChooser.getSelectedFile() : file);
		}
		protected boolean saveDocument(XmlDocumentEditorTab xdet) {
			return false; // we never get here because our editor tab provides its own saving logic
		}
		public boolean closeDocument(XmlDocumentEditorTab xdet) {
			if (!super.closeDocument(xdet))
				return false;
			
			//	dispose document
			XmDocument doc = xdet.getXmDocument();
			doc.dispose();
			
			//	clean up any cached files
			String docDataCachePath = ((String) GoldenGateXpediteUI.this.docDataCachePathsById.get(doc.docId));
			if (docDataCachePath != null) {
				File docDataCache = new File(docDataCachePath);
				if (docDataCache.exists()) {
					File[] docDataFiles = docDataCache.listFiles();
					if (docDataFiles != null) {
						for (int f = 0; f < docDataFiles.length; f++)
							docDataFiles[f].delete();
					}
					docDataCache.delete();
				}
			}
			
			//	finally ...
			return true;
		}
		public boolean close() {
			return super.close(); // need to overwrite here to make it accessible
		}
	}
	
	GgxDocumentEditorTab getActiveDocument() {
		return ((GgxDocumentEditorTab) this.ui.getActiveDocument());
	}
	
	private class GgxDocumentEditorTab extends XmlDocumentEditorTab {
		File docSource;
		FileFilter docFormat = xmfFileFilter;
		XmlDocumentIoProvider docIo;
		GgxDocumentEditorTab(XmDocument doc, String docName, File docSource, FileFilter docFormat, XmlDocumentIoProvider docIo) {
			super(GoldenGateXpediteUI.this.ui, doc, docName);
			this.docSource = docSource;
			this.docFormat = ((docFormat == null) ? xmfFileFilter : docFormat);
			this.docIo = docIo;
		}
		
		/* (non-Javadoc)
		 * @see de.uka.ipd.idaho.im.imagine.swing.ImageDocumentMarkupUI.ImageDocumentEditorTab#save()
		 */
		public boolean save() {
			if (!this.isDirty())
				return true;
			else if (this.docSource != null)
				return this.saveAs(this.docSource, this.docFormat);
			else if (this.docIo != null)
				return this.saveAs(this.docIo);
			else return this.saveAs(GoldenGateXpediteUI.this.fileChooser, GoldenGateXpediteUI.this.fileChooserSize);
		}

		boolean saveAs(JFileChooser fileChooser, Dimension fileChooserSize) {
			clearFileFilters(fileChooser);
			fileChooser.addChoosableFileFilter(xmfFileFilter);
			fileChooser.addChoosableFileFilter(xmdFileFilter);
			//	TODO make sure to clear file name and populate with current document name
			fileChooser.setFileFilter(this.docFormat);
			if (this.docSource != null)
				fileChooser.setSelectedFile(this.docSource);
			else {
				File docFolder = fileChooser.getSelectedFile();
				if (docFolder != null)
					docFolder = docFolder.getAbsoluteFile();
				if ((docFolder != null) && docFolder.isFile())
					docFolder = docFolder.getParentFile();
				if (docFolder != null)
					fileChooser.setSelectedFile(new File(docFolder, this.getDocName()));
			}
			fileChooser.setPreferredSize(fileChooserSize);
			int choice = fileChooser.showSaveDialog(this);
			fileChooser.getSize(fileChooserSize);
			if (choice != JFileChooser.APPROVE_OPTION)
				return false;
			File file = fileChooser.getSelectedFile();
			if (file.isDirectory())
				return false;
			return this.saveAs(file, fileChooser.getFileFilter());
		}
		
		boolean saveAs(File file, final FileFilter fileFormat) {
			
			//	check file name
			if ((fileFormat == xmfFileFilter) && !file.getName().endsWith(".xmf")) {
				String fileName = file.getAbsolutePath();
				if (fileName.endsWith(".xmd"))
					fileName = fileName.substring(0, (fileName.length() - ".xmd".length()));
				file = new File(fileName + ".xmf");
			}
			if ((fileFormat == xmdFileFilter) && !file.getName().endsWith(".xmd")) {
				String fileName = file.getAbsolutePath();
				if (fileName.endsWith(".xmf"))
					fileName = fileName.substring(0, (fileName.length() - ".xmf".length()));
				file = new File(fileName + ".xmd");
			}
			
			//	create splash screen
			final ResourceSplashScreen saveScreen = new ResourceSplashScreen(GoldenGateXpediteUI.this, "Saving Document, Please Wait", "", false, false);
			
			//	save document, in separate thread
			final boolean[] saveSuccess = {false};
			final File[] saveFile = {file};
			final String[] saveFileName = {file.getName()};
			Thread saveThread = new Thread() {
				public void run() {
					try {
						
						//	wait for splash screen to come up (we must not reach the dispose() line before the splash screen even comes up)
						while (!saveScreen.isVisible()) try {
							Thread.sleep(10);
						} catch (InterruptedException ie) {}
						
						//	notify listeners that saving is imminent
						GoldenGateXpediteUI.this.ggXpedite.notifyDocumentSaving(GgxDocumentEditorTab.this.getXmDocument(), saveFile[0], saveScreen);
						
						//	make way
						if (saveFile[0].exists()) {
							String fileName = saveFile[0].getAbsolutePath();
							saveFile[0].renameTo(new File(fileName + "." + System.currentTimeMillis() + ".old"));
							saveFile[0] = new File(fileName);
						}
						
						//	save document to folder, and entry list as file
						if (fileFormat == xmdFileFilter) {
							File docFolder = new File(saveFile[0].getAbsolutePath() + "ir");
							if (!docFolder.exists())
								docFolder.mkdirs();
							XmDocumentEntry[] docEntries = XmDocumentIO.storeDocument(GgxDocumentEditorTab.this.getXmDocument(), docFolder, saveScreen);
							BufferedWriter eOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile[0]), "UTF-8"));
							for (int e = 0; e < docEntries.length; e++) {
								eOut.write(docEntries[e].toTabString());
								eOut.newLine();
							}
							eOut.flush();
							eOut.close();
						}
						
						//	save document to batch cache folder (internal entry list only)
						else if (fileFormat == batchCacheDocFileFilter) {
							XmDocumentIO.storeDocument(GgxDocumentEditorTab.this.getXmDocument(), saveFile[0].getParentFile(), saveScreen);
							saveFileName[0] = saveFile[0].getParentFile().getName();
						}
						
						//	save document to zip archive
						else {
							OutputStream dOut = new BufferedOutputStream(new FileOutputStream(saveFile[0]));
							XmDocumentIO.storeDocument(GgxDocumentEditorTab.this.getXmDocument(), dOut, saveScreen);
							dOut.flush();
							dOut.close();
						}
						
						//	remember saving
						GgxDocumentEditorTab.this.savedAs(saveFileName[0], saveFile[0], fileFormat);
						saveSuccess[0] = true;
						
						//	notify listeners of saving success
						GoldenGateXpediteUI.this.ggXpedite.notifyDocumentSaved(GgxDocumentEditorTab.this.getXmDocument(), saveFile[0], saveScreen);
					}
					
					//	catch saving cancellation at hands of listener
					catch (CancelSavingException cse) {
						if (!cse.isUserDecision) {
							cse.printStackTrace(System.out);
							JOptionPane.showMessageDialog(GgxDocumentEditorTab.this, ("An error occurred while saving the document to '" + saveFile[0].getAbsolutePath() + "':\n" + cse.getMessage()), "Error Saving Document", JOptionPane.ERROR_MESSAGE);
						}
					}
					
					//	catch whatever might happen
					catch (Throwable t) {
						JOptionPane.showMessageDialog(GgxDocumentEditorTab.this, ("An error occurred while saving the document to '" + saveFile[0].getAbsolutePath() + "':\n" + t.getMessage()), "Error Saving Document", JOptionPane.ERROR_MESSAGE);
						t.printStackTrace(System.out);
					}
					
					//	dispose splash screen
					finally {
						saveScreen.dispose();
					}
				}
			};
			saveThread.start();
			
			//	open splash screen (this waits)
			saveScreen.setVisible(true);
			
			//	finally ...
			return saveSuccess[0];
		}
		
		boolean saveAs(final XmlDocumentIoProvider xdip) {
			
			//	create splash screen
			final ResourceSplashScreen saveScreen = new ResourceSplashScreen(GoldenGateXpediteUI.this, "Saving Document, Please Wait", "", false, false);
			
			//	save document, in separate thread
			final boolean[] saveSuccess = {false};
			Thread saveThread = new Thread() {
				public void run() {
					try {
						
						//	wait for splash screen to come up (we must not reach the dispose() line before the splash screen even comes up)
						while (!saveScreen.isVisible()) try {
							Thread.sleep(10);
						} catch (InterruptedException ie) {}
						
						//	notify listeners that saving is imminent
						GoldenGateXpediteUI.this.ggXpedite.notifyDocumentSaving(GgxDocumentEditorTab.this.getXmDocument(), xdip, saveScreen);
						
						//	save document
						String docName = xdip.saveDocument(GgxDocumentEditorTab.this.getXmDocument(), GgxDocumentEditorTab.this.getDocName(), saveScreen);
						
						//	check success
						if (docName != null) {
							
							//	remember saving
							GgxDocumentEditorTab.this.savedAs(docName, xdip);
							saveSuccess[0] = true;
							
							//	notify listeners of saving success
							GoldenGateXpediteUI.this.ggXpedite.notifyDocumentSaved(GgxDocumentEditorTab.this.getXmDocument(), xdip, saveScreen);
						}
					}
					
					//	catch saving cancellation at hands of listener
					catch (CancelSavingException cse) {
						if (!cse.isUserDecision) {
							cse.printStackTrace(System.out);
							JOptionPane.showMessageDialog(GgxDocumentEditorTab.this, ("An error occurred while saving the document to " + xdip.getSaveDestinationName() + ":\n" + cse.getMessage()), "Error Saving Document", JOptionPane.ERROR_MESSAGE);
						}
					}
					
					//	catch whatever might happen
					catch (Throwable t) {
						t.printStackTrace(System.out);
						JOptionPane.showMessageDialog(GgxDocumentEditorTab.this, ("An error occurred while saving the document to " + xdip.getSaveDestinationName() + ":\n" + t.getMessage()), "Error Saving Document", JOptionPane.ERROR_MESSAGE);
					}
					
					//	dispose splash screen
					finally {
						saveScreen.dispose();
					}
				}
			};
			saveThread.start();
			
			//	open splash screen (this waits)
			saveScreen.setVisible(true);
			
			//	finally ...
			return saveSuccess[0];
		}
		
		void savedAs(String saveDocName, File saveFile, FileFilter saveFileFormat) {
			this.savedAs(saveDocName, saveFile, saveFileFormat, null);
		}
		void savedAs(String saveDocName, XmlDocumentIoProvider saveDest) {
			this.savedAs(saveDocName, null, this.docFormat, saveDest);
		}
		private void savedAs(String saveDocName, File saveFile, FileFilter saveFileFormat, XmlDocumentIoProvider saveDest) {
			this.savedAs(saveDocName);
			this.docSource = saveFile;
			this.docFormat = saveFileFormat;
			this.docIo = saveDest;
		}
	}
	
	private static final FileFilter xmfFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".xmf"));
		}
		public String getDescription() {
			return "XML Markup Files";
		}
		public String toString() {
			return this.getDescription();
		}
	};
	private static final FileFilter xmdFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".xmd"));
		}
		public String getDescription() {
			return "XML Markup Directories";
		}
		public String toString() {
			return this.getDescription();
		}
	};
	private static final FileFilter genericXmlFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".xml"));
		}
		public String getDescription() {
			return "XML Files (for conversion)";
		}
		public String toString() {
			return this.getDescription();
		}
	};
	private static final FileFilter batchCacheDocFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return (file.isDirectory() || (file.getName().startsWith("entries.") && file.getName().endsWith(".tsv")));
		}
		public String getDescription() {
			return "Batch Cached Documents";
		}
		public String toString() {
			return this.getDescription();
		}
	};
	private static class XmlConversionFileFilter extends FileFilter {
		final XmlConversionProfile conversionProfile;
		XmlConversionFileFilter(XmlConversionProfile conversionProfile) {
			this.conversionProfile = conversionProfile;
		}
		public boolean accept(File file) {
			return this.conversionProfile.accept(file);
		}
		public String getDescription() {
			return this.conversionProfile.label;
		}
		public String toString() {
			return this.getDescription();
		}
	}
	private static void clearFileFilters(JFileChooser fileChooser) {
		FileFilter[] fileFilters = fileChooser.getChoosableFileFilters();
		for (int f = 0; f < fileFilters.length; f++)
			fileChooser.removeChoosableFileFilter(fileFilters[f]);
	}
}
