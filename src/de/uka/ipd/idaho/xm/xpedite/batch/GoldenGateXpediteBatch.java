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
package de.uka.ipd.idaho.xm.xpedite.batch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.uka.ipd.idaho.gamta.util.DocumentStyle;
import de.uka.ipd.idaho.gamta.util.ParallelJobRunner;
import de.uka.ipd.idaho.gamta.util.ProgressMonitor;
import de.uka.ipd.idaho.goldenGate.GoldenGateConfiguration;
import de.uka.ipd.idaho.goldenGate.applications.ApplicationRuntimeUtils;
import de.uka.ipd.idaho.goldenGate.applications.ApplicationRuntimeUtils.ConsoleApplicationINterface;
import de.uka.ipd.idaho.goldenGate.configuration.ConfigurationRuntimeUtils;
import de.uka.ipd.idaho.goldenGate.plugins.GoldenGatePlugin;
import de.uka.ipd.idaho.stringUtils.StringVector;
import de.uka.ipd.idaho.xm.XmDocument;
import de.uka.ipd.idaho.xm.XmSupplement;
import de.uka.ipd.idaho.xm.util.XmDocumentData.XmDocumentEntry;
import de.uka.ipd.idaho.xm.util.XmDocumentIO;
import de.uka.ipd.idaho.xm.util.XmDocumentMarkupPanel.XmlMarkupTool;
import de.uka.ipd.idaho.xm.xml.XmlDocumentConverter;
import de.uka.ipd.idaho.xm.xpedite.GoldenGateXpedite;
import de.uka.ipd.idaho.xm.xpedite.GoldenGateXpediteConstants;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlConversionProfile;
import de.uka.ipd.idaho.xm.xpedite.plugins.XmlDocumentFileExporter;

/**
 * Batch runner utility for GoldenGATE Xpedite. This command line tool fully
 * automatically converts XML documents into XMF documents, optionally running
 * a series of XML Markup tools in between. This fully automated conversion
 * tool works best if document style templates exist for the XML documents to
 * process.
 * 
 * @author sautter
 */
public class GoldenGateXpediteBatch implements GoldenGateXpediteConstants {
//	private static final String CONFIG_PATH_PARAMETER = "CONF";
	private static final String CACHE_PATH_PARAMETER = "CACHE";
	private static final String DATA_PARAMETER = "DATA";
//	private static final String DATA_TYPE_PARAMETER = "DT";
//	private static final String FONT_MODE_PARAMETER = "FM";
//	private static final String FONT_CHARSET_PARAMETER = "CS";
//	private static final String FONT_CHARSET_PATH_PARAMETER = "CP";
	private static final String OUT_PARAMETER = "OUT";
	private static final String OUT_TYPE_PARAMETER = "OT";
	private static final String HELP_PARAMETER = "HELP";
	private static final String SINGLE_THREAD_PARAMETER = "ST";
	private static final String VERBOSE_CONSOLE_PARAMETER = "VC";
	
	private static final String LOG_TIMESTAMP_DATE_FORMAT = "yyyyMMdd-HHmm";
	private static final DateFormat LOG_TIMESTAMP_FORMATTER = new SimpleDateFormat(LOG_TIMESTAMP_DATE_FORMAT);
	
	public static void main(String[] args) throws Exception {
		if ("GgXpedite".equals(System.getProperty("gg." + APPLICATION_FAMILY_NAME_APPLICATION_PROPERTY)))
			mainApplication(args); // got application family name from starter or IDE, good to go
		else mainStarter(args); // loop through to starter routine otherwise
	}
	
	private static void mainStarter(String[] args) throws Exception {
		ApplicationRuntimeUtils.startApplication(new File("."), "GgXpedite", "GgXpediteBatch.ggApp.cnfg", args, false, true);
	}
	
	/**	the main method to run GoldenGATE Xpedite as a batch application
	 */
	private static void mainApplication(String[] args) throws Exception {
		
		//	adjust basic parameters
		boolean online = false;
		String basePath = "./";
		String logFileName = ("GgXpediteBatch." + LOG_TIMESTAMP_FORMATTER.format(new Date()) + ".log");
//		String ggiConfigPath = "GgXpediteBatch.cnfg";
//		String fontMode = "U";
//		String fontCharSet = "S";
//		String fontCharSetPath = null;
		String cacheRootPath = null;
		String dataBaseName = null;
//		String dataType = "G"; // TODO use this for specifying XML conversion profile
		String dataOutPath = null;
		String dataOutType = "F";
		boolean useSingleThread = false;
		boolean verboseConsoleOutput = false;
		boolean printHelpImplicit = true;
		boolean printHelpExplicit = false;
		
		//	parse remaining args
		for (int a = 0; a < args.length; a++) {
			if (args[a] == null)
				continue;
			if (args[a].startsWith(BASE_PATH_PARAMETER + "="))
				basePath = args[a].substring((BASE_PATH_PARAMETER + "=").length());
//			else if (args[a].startsWith(CONFIG_PATH_PARAMETER + "="))
//				ggiConfigPath = args[a].substring((CONFIG_PATH_PARAMETER + "=").length());
			else if (args[a].startsWith(CACHE_PATH_PARAMETER + "="))
				cacheRootPath = args[a].substring((CACHE_PATH_PARAMETER + "=").length());
			else if (args[a].startsWith(DATA_PARAMETER + "=")) {
				dataBaseName = args[a].substring((DATA_PARAMETER + "=").length());
				printHelpImplicit = false;
			}
			else if (args[a].equals(HELP_PARAMETER)) {
				printHelpExplicit = true;
				break;
			}
			else if (args[a].equals(SINGLE_THREAD_PARAMETER))
				useSingleThread = true;
			else if (args[a].equals(VERBOSE_CONSOLE_PARAMETER))
				verboseConsoleOutput = true;
			else if (ONLINE_PARAMETER.equals(args[a]))
				online = true;
//			else if (args[a].startsWith(DATA_TYPE_PARAMETER + "="))
//				dataType = args[a].substring((DATA_TYPE_PARAMETER + "=").length());
//			else if (args[a].startsWith(FONT_MODE_PARAMETER + "="))
//				fontMode = args[a].substring((FONT_MODE_PARAMETER + "=").length());
//			else if (args[a].startsWith(FONT_CHARSET_PARAMETER + "="))
//				fontCharSet = args[a].substring((FONT_CHARSET_PARAMETER + "=").length());
//			else if (args[a].startsWith(FONT_CHARSET_PATH_PARAMETER + "="))
//				fontCharSetPath = args[a].substring((FONT_CHARSET_PATH_PARAMETER + "=").length());
			else if (args[a].startsWith(OUT_PARAMETER + "="))
				dataOutPath = args[a].substring((OUT_PARAMETER + "=").length());
			else if (args[a].startsWith(OUT_TYPE_PARAMETER + "="))
				dataOutType = args[a].substring((OUT_TYPE_PARAMETER + "=").length());
			else if (args[a].equals(LOG_PARAMETER + "=DOC"))
				logFileName = "DOC";
			else if (args[a].equals(LOG_PARAMETER + "=IDE") || args[a].equals(LOG_PARAMETER + "=NO"))
				logFileName = null;
			else if (args[a].startsWith(LOG_PARAMETER + "="))
				logFileName = args[a].substring((LOG_PARAMETER + "=").length());
		}
		
		//	set up console interface first thing (we need that for sending help or error messages as well)
		ConsoleApplicationINterface console = (verboseConsoleOutput ? new ConsoleApplicationINterface() : new ConsoleApplicationINterface() {
			public void setInfo(String info) {
				System.out.println(info); // send as normal log file entry in non-verbose mode
			}
		});
		
		//	print help and exit if asked to
		if (printHelpExplicit || printHelpImplicit) {
//			System.out.println("GoldenGATE Xpedite Batch can take the following parameters:");
			console.sendMessage("GoldenGATE Xpedite Batch can take the following parameters:");
//			System.out.println("");
			console.sendMessage("");
//			System.out.println("PATH:\tthe folder to run GoldenGATE Xpedite Batch in (defaults to the\r\n\tinstallation folder)");
			console.sendMessage("PATH:\tthe folder to run GoldenGATE Xpedite Batch in (defaults to the");
			console.sendMessage("\tinstallation folder)");
//			System.out.println("CACHE:\tthe root folder for all data caching folders (defaults to the path\r\n\tfolder, useful for directing caching to a RAM disc, etc.)");
			console.sendMessage("CACHE:\tthe root folder for all data caching folders (defaults to the path");
			console.sendMessage("\tfolder, useful for directing caching to a RAM disc, etc.)");
//			System.out.println("DATA:\tthe XML files to process:");
			console.sendMessage("DATA:\tthe XML files to process:");
//			System.out.println("\t- set to XML file path and name to process that file");
			console.sendMessage("\t- set to XML file path and name to process that file");
//			System.out.println("\t- set to folder path and name to process all XML files in that folder");
			console.sendMessage("\t- set to folder path and name to process all XML files in that folder");
//			System.out.println("\t- set to TXT file to process all XML files listed in that file");
			console.sendMessage("\t- set to TXT file to process all XML files listed in that file");
//			System.out.println("OUT:\tthe folder to store the produced XMF files in (defaults to the folder\r\n\teach individual source XML file was loaded from)");
			console.sendMessage("OUT:\tthe folder to store the produced XMF files in (defaults to the folder");
			console.sendMessage("\teach individual source XML file was loaded from)");
//			System.out.println("OT:\tthe way of storing the produced XMF files (defaults to 'F' for 'file'):");
			console.sendMessage("OT:\tthe way of storing the produced XMF files (defaults to 'F' for 'file'):");
//			System.out.println("\t- set to 'F' or omit to indicate (zipped) single file storage");
			console.sendMessage("\t- set to 'F' or omit to indicate (zipped) single file storage");
//			System.out.println("\t- set to 'D' to indicate indicate (non-zipped) folder storage");
			console.sendMessage("\t- set to 'D' to indicate indicate (non-zipped) folder storage");
//			System.out.println("LOG:\tthe name for the log files to write respective information to (file\r\n\tnames are suffixed with '.out.log' and '.err.log', set to 'IDE' or 'NO'\r\n\tto log directly to the console, or to DOC to create one log file per\r\n\tdocument, located next to the XMF)");
			console.sendMessage("LOG:\tthe name for the log files to write respective information to (file");
			console.sendMessage("\tnames are suffixed with '.out.log' and '.err.log', set to 'IDE' or 'NO'");
			console.sendMessage("\tto log directly to the console, or to DOC to create one log file per");
			console.sendMessage("\tdocument, located next to the XMF)");
//			System.out.println("ST:\tno value, just add this token to the command to make the batch run on a\r\n\tsingle core (e.g. if resources required for other simultaneous tasks)");
			console.sendMessage("ST:\tno value, just add this token to the command to make the batch run on a");
			console.sendMessage("\tsingle core (e.g. if resources required for other simultaneous tasks)");
//			System.out.println("VC:\tno value, just add this token to the command to make the batch produce\r\n\tverbose console output");
			console.sendMessage("VC:\tno value, just add this token to the command to make the batch produce");
			console.sendMessage("\tverbose console output");
//			System.out.println("HELP:\tprint this help text");
			console.sendMessage("HELP:\tprint this help text");
//			System.out.println("");
			console.sendMessage("");
//			System.out.println("The file configuration file ('GgXpediteBatch.cnfg' by default) specifies how\r\nto process XML documents after decoding, and can also provide environmental and\r\nXML decoding parameters:");
//			System.out.println("- imageMarkupTools: a space separated list of the XML Markup Tools to run");
//			System.out.println("- documentExporters: a space separated list of the Document Exporters to run\r\n  after processing is finished (defaults to all available)");
//			System.out.println("- configName: the name of the GoldenGATE Xpedite configuration to load the\r\n  XML Markup Tools and Document Exporters from");
//			System.out.println("- cacheRootFolder: configurable default for 'CACHE' parameter");
			console.sendMessage("The configuration file 'GgXpediteBatch.ggApp.cnfg' specifies how to process XML");
			console.sendMessage("documents after conversion. It can be amended and modified via its supplementary");
			console.sendMessage("counterpart 'GgXpediteBatch.ggApp.local.cnfg'");
			console.sendMessage("Documentation inside these configuration files details out individual parameters");
			console.sendMessage("and their possible values.");
			System.exit(0);
		}
		
		//	get list of files to process (either all XMLs in some folder, or the ones listed in some TXT file, or some already-decoded files)
		File[] dataInFiles = null;
		
		//	folder to process
		File dataInBase = new File(dataBaseName);
		if (dataInBase.isDirectory()) {
			dataInFiles = dataInBase.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return (file.isFile() && file.getName().toLowerCase().endsWith(".xml"));
				}
			});
		}
		else if (dataInBase.getName().toLowerCase().endsWith(".xml")) {
			dataInFiles = new File[1];
			dataInFiles[0] = dataInBase;
		}
		else if (dataInBase.getName().toLowerCase().endsWith(".xmf")) {
			dataInFiles = new File[1];
			dataInFiles[0] = dataInBase;
		}
		else if (dataInBase.getName().toLowerCase().endsWith(".xmd")) {
			dataInFiles = new File[1];
			dataInFiles[0] = dataInBase;
		}
		else if (dataInBase.getName().toLowerCase().endsWith(".txt")) {
			StringVector dataInNames = StringVector.loadList(dataInBase);
			ArrayList dataInFileList = new ArrayList();
			for (int d = 0; d < dataInNames.size(); d++) {
				File dataInFile = new File(dataInNames.get(d));
				if (dataInFile.isDirectory())
					dataInFileList.addAll(Arrays.asList(dataInFile.listFiles(new FileFilter() {
						public boolean accept(File file) {
							return (file.isFile() && file.getName().toLowerCase().endsWith(".xml"));
						}
					})));
				else if (dataInFile.getName().toLowerCase().endsWith(".xml"))
					dataInFileList.add(dataInFile);
			}
			dataInFiles = ((File[]) dataInFileList.toArray(new File[dataInFileList.size()]));
		}
		
		//	anything to work on?
		if ((dataInFiles == null) || (dataInFiles.length == 0)) {
//			System.out.println("No data specified to work with, use 'DATA' parameter:");
			console.sendMessage("No data specified to work with, use 'DATA' parameter:");
//			System.out.println("- set to XML file name: process that file");
			console.sendMessage("- set to XML file name: process that file");
//			System.out.println("- set to XMF file name: process that file");
			console.sendMessage("- set to XMF file name: process that file");
//			System.out.println("- set to folder name: process all XML files in that folder");
			console.sendMessage("- set to folder name: process all XML files in that folder");
//			System.out.println("- set to TXT file: process all XML files listed in there");
			console.sendMessage("- set to TXT file: process all XML files listed in there");
			System.exit(0);
		}
		
		//	remember program base path
		final File rootFolder = new File(basePath);
		
		//	load application properties
		Properties appProperties = ApplicationRuntimeUtils.loadApplicationProperties(rootFolder);
		
		//	configure web access
//		if (online)
		ApplicationRuntimeUtils.setUpWebAccess(rootFolder, appProperties, false);
//		
//		//	preserve original System.out and write major steps there
//		final PrintStream systemOut = new PrintStream(System.out, true) {
//			public void println(String str) {
//				super.println(str);
//				if (System.out != this.out)
//					System.out.println(str);
//			}
//			public void println() {
//				super.println();
//				if (System.out != this.out)
//					System.out.println();
//			}
//			public void println(boolean x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//			public void println(char x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//			public void println(int x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//			public void println(long x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//			public void println(float x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//			public void println(double x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//			public void println(char[] x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//			public void println(Object x) {
//				super.println(x);
//				if (System.out != this.out)
//					System.out.println(x);
//			}
//		};
		
		//	create log files if required (which should hardly ever be the case, as starter handles logging)
		if (logFileName != null)
			ApplicationRuntimeUtils.setUpLogFiles(rootFolder, logFileName);
//		if (logFileName != null) try {
//			ApplicationRuntimeUtils.setUpLogFiles(rootFolder, logFileName);
//		}
//		catch (Exception e) {
//			logFileName = logFileName.replace('\\', '/');
//			File logFolder;
//			if (logFileName.startsWith("/") || (logFileName.indexOf(":/") != -1))
//				logFolder = (new File(logFileName)).getAbsoluteFile().getParentFile();
//			else if (logFileName.indexOf("/") == -1)
//				logFolder = new File(rootFolder, LOG_FOLDER_NAME);
//			else {
//				String logFolderName = logFileName.substring(0, logFileName.lastIndexOf("/"));
//				logFolder = new File(rootFolder, logFolderName);
//			}
//			System.out.println("Could not create log files in folder '" + logFolder.getAbsolutePath() + "'.");
//			System.out.println("Common reasons are a full hard drive, lack of write permissions to the folder, or system protection software.");
//			System.out.println("Use the 'Configure' button in the configuration selector dialog to select a different log location.");
//			System.out.println("Then exit and re-start GoldenGATE Xpedite to apply the change.");
//			System.out.println("\nNote that you can work normally without the log files, it's just that in case of an error, there are");
//			System.out.println("no log files to to help investigate what exactly went wrong and help developers fix the problem.");
//		}
		
		//	load GoldenGATE Xpedite specific settings
		//	TODO load settings via GG core
		//	TODO OR BETTER, get this from app configuration
//		File ggxSettingsFile;
//		if (ggiConfigPath.startsWith("/") || (ggiConfigPath.indexOf(":\\") == 1) || (ggiConfigPath.indexOf(":/") == -1))
//			ggxSettingsFile = new File(ggiConfigPath);
//		else ggxSettingsFile = new File(rootFolder, ggiConfigPath);
//		Settings ggxSettings = Settings.loadSettings(ggxSettingsFile);
		
		//	get list of image markup tools to run
//		String xmtNameString = ggxSettings.getSetting("xmlMarkupTools");
		String xmtNameString = appProperties.getProperty("xmlMarkupTools");
		if (xmtNameString == null) {
//			systemOut.println("No XML Markup Tools configured to run, check entry" +
////					"\r\n'xmlMarkupTools' in GgXpediteBatch.cnfg");
//					"\r\n'@xmlMarkupTools' in GgXpediteBatch.ggApp.local.cnfg");
			console.sendError("No XML Markup Tools configured to run, check entry");
			console.sendError("'@xmlMarkupTools' in GgXpediteBatch.ggApp.local.cnfg");
			System.exit(0);
		}
		String[] xmtNames = xmtNameString.split("\\s+");
		
		//	get exporters to use
//		String exporterNames = ggxSettings.getSetting("documentExporters");
		String exporterNames = appProperties.getProperty("documentExporters");
		
		//	use configuration specified in settings (default to 'Default.imagine' for now)
//		String ggxConfigName = ggxSettings.getSetting("configName");
		String ggxConfigName = appProperties.getProperty(CONFIGURATION_NAME_APPLICATION_PROPERTY);
		
		//	get GoldenGATE Xpedite configuration
		String[] ggxConfigHosts = (online ? ConfigurationRuntimeUtils.getConfigHosts(rootFolder) : new String[0]);
		GoldenGateConfiguration ggxConfig = ConfigurationRuntimeUtils.loadConfiguration(ggxConfigName, ggxConfigHosts, rootFolder, ProgressMonitor.dummy);
		
		//	anything to work with?
		if (ggxConfig == null) {
			System.out.println("Cannot " + ((ggxConfigName == null) ? "work without configuration" : ("find configuration '" + ggxConfigName + "', please check config files")));
			System.exit(0);
		}
		
		//	check for config file specified cache root
		if (cacheRootPath == null)
//			cacheRootPath = ggxSettings.getSetting("cacheRootFolder");
			cacheRootPath = appProperties.getProperty("cacheRootFolder");
		
		//	folder for temporarily storing documents during batch processing
		File tempDocRootFolder = null;
		
		//	if cache path set, add settings for page image and supplement cache
		if (cacheRootPath != null) {
			
			//	make sure cache path denotes folder
			if (!cacheRootPath.endsWith("/"))
				cacheRootPath += "/";
			
			//	set up temporary document storage for batch crash recovery
			String tempDocRootFolderName = (cacheRootPath + "TempDocs");
			if (tempDocRootFolderName.startsWith("/") || (tempDocRootFolderName.indexOf(':') != -1))
				tempDocRootFolder = new File(tempDocRootFolderName);
			else if (tempDocRootFolderName.startsWith("./"))
				tempDocRootFolder = new File(rootFolder, tempDocRootFolderName.substring("./".length()));
			else tempDocRootFolder = new File(rootFolder, tempDocRootFolderName);
			if (!tempDocRootFolder.exists())
				tempDocRootFolder.mkdirs();
		}
		
		//	instantiate GoldenGATE Xpedite
		GoldenGateXpedite goldenGateXpedite = GoldenGateXpedite.openGoldenGATE(rootFolder, ggxConfig, ProgressMonitor.dummy);
//		systemOut.println("GoldenGATE Xpedite core created, configuration is " + ggxConfigName);
		console.sendMessage("GoldenGATE Xpedite core created, configuration is " + ggxConfigName);
		
		//	get individual XML markup tools
		XmlMarkupTool[] xmts = new XmlMarkupTool[xmtNames.length];
		for (int t = 0; t < xmtNames.length; t++) {
			xmts[t] = goldenGateXpedite.getXmlMarkupToolForName(xmtNames[t]);
			if (xmts[t] == null) {
//				systemOut.println("XML Markup Tool '" + xmtNames[t] + "' not found," +
////						"\r\ncheck entry 'xmlMarkupTools' in GgXpediteBatch.cnfg");
//						"\r\ncheck entry '@xmlMarkupTools' in GgXpediteBatch.ggApp.local.cnfg");
				console.sendError("XML Markup Tool '" + xmtNames[t] + "' not found,");
				console.sendError("check entry '@xmlMarkupTools' in GgXpediteBatch.ggApp.local.cnfg");
				System.exit(0);
			}
//			else systemOut.println("XML Markup Tool '" + xmtNames[t] + "' loaded");
			else console.sendMessage("XML Markup Tool '" + xmtNames[t] + "' loaded");
		}
		
		//	get document exporters for additional output
		XmlDocumentFileExporter[] xdfes = getFileExporters(ggxConfig.getPlugins(), exporterNames);
//		
//		//	create progress monitor forking steps to console
//		final PrintStream pmInfoSystemOut = (verboseConsoleOutput ? systemOut : System.out);
//		ProgressMonitor pm = new ProgressMonitor() {
//			public void setStep(String step) {
//				systemOut.println(step);
//			}
//			public void setInfo(String info) {
//				pmInfoSystemOut.println(info);
//			}
//			public void setBaseProgress(int baseProgress) {}
//			public void setMaxProgress(int maxProgress) {}
//			public void setProgress(int progress) {}
//		};
		
		//	switch off multi-threading if requested
		if (useSingleThread)
			ParallelJobRunner.setLinear(true);
		
		//	get conversion profile
		String xcpName = appProperties.getProperty("xmlConversionProfile");
		XmlConversionProfile xcp = null;
		
		//	process files
		PerDocLogger perDocLogger = null;
		for (int d = 0; d < dataInFiles.length; d++) {
			
			//	prepare intermediate caching of document during processing (need to do it up here so we have the folder accessible to cleanup)
			File tempDocFolder = null;
			if (tempDocRootFolder != null) {
				tempDocFolder = new File(tempDocRootFolder, dataInFiles[d].getName());
				if (!tempDocFolder.exists())
					tempDocFolder.mkdirs();
			}
			
			//	trace processing success
			boolean docFullyProcessed = true;
			
			//	convert XML and batch process document
			try {
				
				//	determine where to store document
				String dataOutName;
				File dataOutFile;
				
				//	request for converting and processing XML
				if (dataInFiles[d].getName().toLowerCase().endsWith(".xml")) {
					dataOutName = (dataInFiles[d].getName() + ("D".equals(dataOutType) ? ".xmd" : ".xmf"));
					if (dataOutPath == null)
						dataOutFile = new File(dataInFiles[d].getAbsoluteFile().getParentFile(), dataOutName);
					else dataOutFile = new File(dataOutPath, dataOutName);
					
					//	we've processed this one before
					if (dataOutFile.exists()) {
//						systemOut.println("Document '" + dataInFiles[d].getAbsolutePath() + "' processed before, skipping");
						console.sendMessage("Document '" + dataInFiles[d].getAbsolutePath() + "' processed before, skipping");
						continue;
					}
				}
				
				//	request for processing XMF
				else if (dataInFiles[d].getName().toLowerCase().endsWith(".xmf")) {
					dataOutName = (dataInFiles[d].getName().substring(0, (dataInFiles[d].getName().length() - ".xmf".length())) + ("D".equals(dataOutType) ? ".xmd" : ".xmf"));
					if (dataOutPath == null)
						dataOutFile = new File(dataInFiles[d].getAbsoluteFile().getParentFile(), dataOutName);
					else dataOutFile = new File(dataOutPath, dataOutName);
				}
				
				//	request for processing XMD
				else if (dataInFiles[d].getName().toLowerCase().endsWith(".xmd")) {
					dataOutName = (dataInFiles[d].getName().substring(0, (dataInFiles[d].getName().length() - ".xmd".length())) + ("D".equals(dataOutType) ? ".xmd" : ".xmf"));
					if (dataOutPath == null)
						dataOutFile = new File(dataInFiles[d].getAbsoluteFile().getParentFile(), dataOutName);
					else dataOutFile = new File(dataOutPath, dataOutName);
				}
				
				//	some other file format (that cannot occur with the above logic, but the compiler don't know)
				else {
//					systemOut.println("Unknown input format in document '" + dataInFiles[d].getAbsolutePath() + "', skipping");
					console.sendMessage("Unknown input format in document '" + dataInFiles[d].getAbsolutePath() + "', skipping");
					continue;
				}
				
				//	we're processing this one
//				systemOut.println("Processing document '" + dataInFiles[d].getAbsolutePath() + "'");
				console.sendMessage("Processing document '" + dataInFiles[d].getAbsolutePath() + "'");
				
				//	create document specific log files if requested
				if ("DOC".equals(logFileName)) try {
					File logFolder = dataOutFile.getAbsoluteFile().getParentFile();
					perDocLogger = new PerDocLogger(logFolder, dataInFiles[d].getName());
				}
				catch (Exception e) {
//					systemOut.println("Could not create log files in folder '" + dataOutFile.getAbsoluteFile().getParentFile().getAbsolutePath() + "':" + e.getMessage());
					console.sendError("Could not create log files in folder '" + dataOutFile.getAbsoluteFile().getParentFile().getAbsolutePath() + "':" + e.getMessage());
//					e.printStackTrace(systemOut);
					console.sendError(e);
				}
				
				//	convert input XML, load XMF
				XmDocument doc = null;
				
				//	check if we have an earlier version cached (batch might have failed at some point)
				if ((tempDocFolder != null) && (new File(tempDocFolder, "entries.tsv")).exists()) try {
					doc = XmDocumentIO.loadDocument(tempDocFolder);
//					systemOut.println(" - document restored from previous batch run");
					console.sendMessage(" - document restored from previous batch run");
				}
				
				//	don't let a cache lookup get in the way
				catch (Throwable t) {
//					systemOut.println("Error loading document '" + dataInFiles[d].getAbsolutePath() + "' from cache: " + t.getMessage());
					console.sendError("Error loading document '" + dataInFiles[d].getAbsolutePath() + "' from cache: " + t.getMessage());
//					t.printStackTrace(systemOut);
					console.sendError(t);
				}
				
				//	cache miss or error, convert input XML or load input XMF or XMD
				if (doc == null) {
					
					//	convert XML
					if (dataInFiles[d].getName().toLowerCase().endsWith(".xml")) {
						if (xcp == null) {
							if (xcpName == null) {
//								systemOut.println("No XML Conversion Profile configured to use, check entry" +
////										"\r\n'xmlConversionProfile' in GgXpediteBatch.cnfg");
//										"\r\n'@xmlConversionProfile' in GgXpediteBatch.ggApp.local.cnfg");
								console.sendError("No XML Conversion Profile configured to use, check entry");
								console.sendError("'@xmlConversionProfile' in GgXpediteBatch.ggApp.local.cnfg");
								continue;
							}
							else {
								xcp = goldenGateXpedite.getXmlConversionProfileForName(xcpName);
								if (xcp == null) {
//									systemOut.println("XML Conversion Profile '" + xcpName + "' not found," +
////											"\r\ncheck entry 'xmlConversionProfile' in GgXpediteBatch.cnfg");
//											"\r\ncheck entry '@xmlConversionProfile' in GgXpediteBatch.ggApp.local.cnfg");
									console.sendError("XML Conversion Profile '" + xcpName + "' not found,");
									console.sendError("check entry '@xmlConversionProfile' in GgXpediteBatch.ggApp.local.cnfg");
									continue;
								}
							}
							
							//	cache input file
							BufferedInputStream docIn = new BufferedInputStream(new FileInputStream(dataInFiles[d]));
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							byte[] buffer = new byte[1024];
							for (int r; (r = docIn.read(buffer, 0, buffer.length)) != -1;)
								baos.write(buffer, 0, r);
							docIn.close();
							byte[] xmlBytes = baos.toByteArray();
							
							//	convert document TODOne determine encoding !!! (UTF-8 should be safe bet for now, but be thorough !!!)
//							BufferedReader xmlBr = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(xmlBytes), "UTF-8"));
							InputStreamReader xmlByteDecoder = GoldenGateXpedite.createReader(xmlBytes);
							BufferedReader xmlBr = new BufferedReader(xmlByteDecoder);
							doc = XmlDocumentConverter.convertXml(xmlBr, xcp);
							doc.addSupplement(XmSupplement.Source.createSource(doc, "text/xml", xmlBytes));
						}
					}
					
					//	load XMF
					else if (dataInFiles[d].getName().toLowerCase().endsWith(".xmf"))
						doc = XmDocumentIO.loadDocument(dataInFiles[d]);
					
					//	load XMD
					else if (dataInFiles[d].getName().toLowerCase().endsWith(".xmd"))
						doc = XmDocumentIO.loadDocument(new File(dataInFiles[d].getParentFile(), (dataInFiles[d].getName() + "ir")));
					
					//	some other (yet to implement) format
					else {
//						systemOut.println(" - unknown document format");
						console.sendMessage(" - unknown document format");
						continue;
					}
				}
				
				//	test if document style detected
				if (DocumentStyle.getStyleFor(doc) == null) {
//					systemOut.println(" - unable to assign document style");
					console.sendMessage(" - unable to assign document style");
					continue;
				}
//				else systemOut.println(" - assigned document style '" + ((String) doc.getAttribute(DocumentStyle.DOCUMENT_STYLE_NAME_ATTRIBUTE)) + "'");
				else console.sendMessage(" - assigned document style '" + ((String) doc.getAttribute(DocumentStyle.DOCUMENT_STYLE_NAME_ATTRIBUTE)) + "'");
				
				//	notify listeners
//				goldenGateXpedite.notifyDocumentOpened(doc, dataInFiles[d], pm);
				goldenGateXpedite.notifyDocumentOpened(doc, dataInFiles[d], console);
				
				//	keep track of which IMTs have already run
				StringBuffer runXmtNames = new StringBuffer((String) doc.getAttribute("_runXmtNames", "|"));
				int runXmts = 0;
				
				//	process document
				for (int imt = 0; imt < xmts.length; imt++) {
					
					//	skip over previously-run IMTs
					if (runXmtNames.indexOf("|" + xmtNames[imt] + "|") != -1) {
//						systemOut.println("Skipping previously-run XML Markup Tool '" + xmts[imt].getLabel() + "'");
						console.sendMessage("Skipping previously-run XML Markup Tool '" + xmts[imt].getLabel() + "'");
						continue;
					}
					
					//	cache batch processing result (unless we have just started over)
					if (runXmts != 0) try {
//						goldenGateXpedite.notifyDocumentSaving(doc, tempDocFolder, pm);
						goldenGateXpedite.notifyDocumentSaving(doc, tempDocFolder, console);
//						XmDocumentIO.storeDocument(doc, tempDocFolder, pm);
						XmDocumentIO.storeDocument(doc, tempDocFolder, console);
//						goldenGateXpedite.notifyDocumentSaved(doc, tempDocFolder, pm);
						goldenGateXpedite.notifyDocumentSaved(doc, tempDocFolder, console);
//						systemOut.println("Document stored to temporary folder");
						console.sendMessage("Document stored to temporary folder");
					}
					
					//	don't let a caching operation get in the way
					catch (Throwable t) {
//						systemOut.println("Error caching document '" + dataInFiles[d].getAbsolutePath() + "': " + t.getMessage());
						console.sendError("Error caching document '" + dataInFiles[d].getAbsolutePath() + "': " + t.getMessage());
//						t.printStackTrace(systemOut);
						console.sendError(t);
					}
					
					//	run IMT
//					systemOut.println("Running XML Markup Tool '" + xmts[imt].getLabel() + "'");
					console.sendMessage("Running XML Markup Tool '" + xmts[imt].getLabel() + "'");
//					xmts[imt].process(doc, null, null, pm);
					xmts[imt].process(doc, null, null, console);
					
					//	update tracking data
					runXmtNames.append(xmtNames[imt] + "|");
					runXmts++;
					doc.setAttribute("_runXmlNames", runXmtNames.toString());
				}
				
				//	remove batch tracking attribute (we do not want this in the final output)
				doc.removeAttribute("_runImtNames");
				
				//	store document to directory ...
				if ("D".equals(dataOutType)) {
					dataOutFile.getAbsoluteFile().getParentFile().mkdirs();
					File dataOutFolder = new File(dataOutFile.getAbsolutePath() + "ir");
					if (!dataOutFolder.exists())
						dataOutFolder.mkdirs();
//					systemOut.println("Storing document to '" + dataOutFile.getAbsolutePath() + "'");
					console.sendMessage("Storing document to '" + dataOutFile.getAbsolutePath() + "'");
//					goldenGateXpedite.notifyDocumentSaving(doc, dataOutFolder, pm);
					goldenGateXpedite.notifyDocumentSaving(doc, dataOutFolder, console);
//					XmDocumentEntry[] entries = XmDocumentIO.storeDocument(doc, dataOutFolder, pm);
					XmDocumentEntry[] entries = XmDocumentIO.storeDocument(doc, dataOutFolder, console);
//					systemOut.println("Document entries stored");
					console.sendMessage("Document entries stored");
					if (dataOutFile.exists()) {
						String exDataOutFileName = dataOutFile.getAbsolutePath();
						dataOutFile.renameTo(new File(exDataOutFileName + "." + System.currentTimeMillis() + ".old"));
						dataOutFile = new File(exDataOutFileName);
					}
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataOutFile), "UTF-8"));
					for (int e = 0; e < entries.length; e++) {
						out.write(entries[e].toTabString());
						out.newLine();
					}
					out.flush();
					out.close();
//					systemOut.println("Document stored");
					console.sendMessage("Document stored");
//					goldenGateXpedite.notifyDocumentSaved(doc, dataOutFolder, pm);
					goldenGateXpedite.notifyDocumentSaved(doc, dataOutFolder, console);
				}
				
				//	... or file
				else {
					if (dataOutFile.exists()) {
						String exDataOutFileName = dataOutFile.getAbsolutePath();
						dataOutFile.renameTo(new File(exDataOutFileName + "." + System.currentTimeMillis() + ".old"));
						dataOutFile = new File(exDataOutFileName);
					}
					dataOutFile.getAbsoluteFile().getParentFile().mkdirs();
					OutputStream out = new BufferedOutputStream(new FileOutputStream(dataOutFile));
//					systemOut.println("Storing document to '" + dataOutFile.getAbsolutePath() + "'");
					console.sendMessage("Storing document to '" + dataOutFile.getAbsolutePath() + "'");
//					goldenGateXpedite.notifyDocumentSaving(doc, dataOutFile, pm);
					goldenGateXpedite.notifyDocumentSaving(doc, dataOutFile, console);
//					XmDocumentIO.storeDocument(doc, out, pm);
					XmDocumentIO.storeDocument(doc, out, console);
					out.flush();
					out.close();
//					systemOut.println("Document stored");
					console.sendMessage("Document stored");
//					goldenGateXpedite.notifyDocumentSaved(doc, dataOutFile, pm);
					goldenGateXpedite.notifyDocumentSaved(doc, dataOutFile, console);
				}
				
				//	export additional data formats
				for (int e = 0; e < xdfes.length; e++) try {
//					xdfes[e].exportDocument(doc, dataOutFile, pm);
					xdfes[e].exportDocument(doc, dataOutFile, console);
				}
				
				//	don't let any additional export error disturb main process
				catch (Throwable t) {
//					systemOut.println("Error exporting document '" + dataInFiles[d].getAbsolutePath() + "' via '" + xdfes[e].getExportMenuLabel() + "': " + t.getMessage());
					console.sendError("Error exporting document '" + dataInFiles[d].getAbsolutePath() + "' via '" + xdfes[e].getExportMenuLabel() + "': " + t.getMessage());
//					t.printStackTrace(systemOut);
					console.sendError(t);
				}
				
				//	notify listeners that we're done (only after exports, as they might target cached content)
				goldenGateXpedite.notifyDocumentClosed(doc.docId);
				doc.dispose();
			}
			
			//	catch and log whatever might go wrong
			catch (Throwable t) {
//				systemOut.println("Error processing document '" + dataInFiles[d].getAbsolutePath() + "': " + t.getMessage());
				console.sendError("Error processing document '" + dataInFiles[d].getAbsolutePath() + "': " + t.getMessage());
//				t.printStackTrace(systemOut);
				console.sendError(t);
				docFullyProcessed = false;
			}
			
			//	clean up, error or not
			finally {
				
				//	if cache root set, clean up cache (fast but small RAM discs will run out of space quickly otherwise)
				if (cacheRootPath != null) {
					cleanCacheFolder(new File(cacheRootPath), 0);
					if (docFullyProcessed)
						cleanCacheFolder(tempDocFolder, 2);
				}
				
				//	close log files if logging per document
				if (perDocLogger != null)
					perDocLogger.close();
				perDocLogger = null;
				
				//	garbage collect whatever is left
				System.gc();
			}
		}
		
		//	shut down whatever threads are left
		System.exit(0);
	}
	
	private static class PerDocLogger {
		private File logFolder;
		private String docName;
		
		private File logFileOut;
		private PrintStream logOut;
		private PrintStream sysOut;
		
		private File logFileErr;
		private PrintStream logErr;
		private PrintStream sysErr;
		
		PerDocLogger(File logFolder, String docName) throws Exception {
			this.logFolder = logFolder;
			this.docName = docName;
			
			//	create log files
			this.logFolder.mkdirs();
			this.logFileOut = new File(this.logFolder, (this.docName + ".out.log"));
			this.logFileErr = new File(this.logFolder, (this.docName + ".err.log"));
			
			//	redirect System.out
			this.logFileOut.createNewFile();
			this.sysOut = System.out;
			this.logOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(this.logFileOut)), true, "UTF-8");
			System.setOut(this.logOut);
			
			//	redirect System.err
			this.logFileErr.createNewFile();
			this.sysErr = System.err;
			this.logErr = new PrintStream(new BufferedOutputStream(new FileOutputStream(this.logFileErr)), true, "UTF-8");
			System.setErr(this.logErr);

		}
		
		void close() {
			
			//	restore System.out
			System.setOut(this.sysOut);
			this.logOut.flush();
			this.logOut.close();
			
			//	restore System.err
			System.setErr(this.sysErr);
			this.logErr.flush();
			this.logErr.close();
			
			//	zip up log files
			try {
				File zipFile = new File(this.logFolder, (this.docName + ".logs.zip"));
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
				this.zipUp(this.logFileOut, zipOut);
				this.zipUp(this.logFileErr, zipOut);
				zipOut.flush();
				zipOut.close();
			}
			catch (Exception e) {
				System.out.println("Could not zip up log files in '" + this.logFolder.getAbsolutePath() + "':" + e.getMessage());
				e.printStackTrace(System.out);
			}
		}
		
		void zipUp(File logFile, ZipOutputStream zipOut) throws Exception {
			
			//	zip up log file (unless it's empty)
			if (logFile.length() != 0) {
				InputStream logIn = new BufferedInputStream(new FileInputStream(logFile));
				zipOut.putNextEntry(new ZipEntry(logFile.getName()));
				byte[] buffer = new byte[1024];
				for (int r; (r = logIn.read(buffer, 0, buffer.length)) != -1;)
					zipOut.write(buffer, 0, r);
				zipOut.closeEntry();
				logIn.close();
			}
			
			//	clean up plain log file
			logFile.delete();
		}
	}
	
	private static void cleanCacheFolder(File folder, int depth) {
		File[] folderContent = folder.listFiles();
		for (int c = 0; c < folderContent.length; c++) try {
			if (folderContent[c].isDirectory()) {
				if ((depth == 0) && "TempDocs".equals(folderContent[c].getName()))
					continue; // do not touch intermediate results of batch, they are cleaned up separately
				cleanCacheFolder(folderContent[c], (depth+1));
				if (depth != 0)
					folderContent[c].delete();
			}
			else folderContent[c].delete();
		}
		catch (Throwable t) {
			System.out.println("Error cleaning up cached file '" + folderContent[c].getAbsolutePath() + "': " + t.getMessage());
			t.printStackTrace(System.out);
		}
	}
	
	private static XmlDocumentFileExporter[] getFileExporters(GoldenGatePlugin[] ggPlugins, String exporterClassNames) {
		ArrayList idfeList = new ArrayList();
		for (int p = 0; p < ggPlugins.length; p++)
			if (ggPlugins[p] instanceof XmlDocumentFileExporter) {
				if (exporterClassNames == null)
					idfeList.add(ggPlugins[p]);
				else {
					String exporterClassName = ggPlugins[p].getClass().getName();
					exporterClassName = exporterClassName.substring(exporterClassName.lastIndexOf('.') + 1);
					if (exporterClassNames.indexOf(exporterClassName) != -1)
						idfeList.add(ggPlugins[p]);
				}
			}
		return ((XmlDocumentFileExporter[]) idfeList.toArray(new XmlDocumentFileExporter[idfeList.size()]));
	}
}