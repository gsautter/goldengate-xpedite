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

import de.uka.ipd.idaho.goldenGate.GoldenGateConstants;

/**
 * Constants for the GoldenGATE Imagine
 * 
 * @author sautter
 */
public interface GoldenGateXpediteConstants extends GoldenGateConstants {
	
	/** The default title for a GoldenGATE Imagine window, namely 'GoldenGATE Imagine' */
	public static final String DEFAULT_WINDOW_TITLE = "GoldenGATE Xpedite";
	
	/** The current GoldenGATE Imagine version number, prefixed by a 'V' */
	public static final String VERSION_STRING = "XM";
//	
//	/**
//	 * The version date of the GoldenGATE Imagine Core, indicating when the
//	 * editor core was last modified
//	 */
//	public static final String VERSION_DATE = "2024.03.10.21.00";
//	
//	/** the name of the file containing the startup parameters */
//	public static final String PARAMETER_FILE_NAME = "Parameters.cnfg";
	
	/** the name of the file containing GoldenGATE Imagine's configuration parameters */
	public static final String CONFIG_FILE_NAME = "GgXpedite.cnfg";
//	
//	/** the name of GoldenGATE Imagine's readme file */
//	public static final String README_FILE_NAME = "README.txt";
	
//	
//	/** the name of the file holding the URLs to obtain configurations from */
//	public static final String CONFIG_HOST_FILE_NAME = "ConfigHosts.cnfg";
//	
//	/** the name of the folder containing the configurations available locally */
//	public static final String CONFIG_FOLDER_NAME = "Configurations";
//	
//	
//	/** the name of the setting holding the initial memory for the Java VM */
//	public static final String START_MEMORY_NAME = "START_MEMORY";
//	
//	/** the name of the setting holding the maximum memory for the Java VM */
//	public static final String MAX_MEMORY_NAME = "MAX_MEMORY";
	
	/** default value for initial VM memory: 128 MB */
	public static final String DEFAULT_START_MEMORY = "128";
	
	/** default value for maximum VM memory: 512 MB */
	public static final String DEFAULT_MAX_MEMORY = "512";
	
	/** the name of the setting holding the initial memory for the Java VM */
	public static final String LOOK_AND_FEEL_NAME = "LOOK_AND_FEEL";
	
//	
//	/** the parameter indicating the GoldenGATE Imagine main class that it was started in an appropriate way */
//	public static final String RUN_PARAMETER = "RUN";
//	
//	/** the parameter indicating the GoldenGATE Imagine main class that it is granted access to the network and the web */
//	public static final String ONLINE_PARAMETER = "ONLINE";
	
	/** the parameter specifying the base path where GoldenGATE Imagine runs */
	public static final String BASE_PATH_PARAMETER = "PATH";
//	
//	/** the parameter specifying the logging behavior and log file name */
//	public static final String LOG_PARAMETER = "LOG";
//	
//	
//	/** the name of the file holding the URLs to obtain core system updates from */
//	public static final String UPDATE_HOST_FILE_NAME = "UpdateHosts.cnfg";
//	
//	/** the name of the folder to store updates locally */
//	public static final String UPDATE_FOLDER_NAME = "Update";
//	
//	
//	/** the setting holding the name of the www proxy (if any) */
//	public static final String PROXY_NAME = "PROXY_NAME";
//	
//	/** the setting holding the port of the www proxy (if any) */
//	public static final String PROXY_PORT = "PROXY_PORT";
//	
//	/** the setting holding the user name for the www proxy (if any) */
//	public static final String PROXY_USER = "PROXY_USER";
//	
//	/** the setting holding the password for the www proxy (if any) */
//	public static final String PROXY_PWD = "PROXY_PWD";
//	
//	
//	/** the setting for the log file folder */
//	public static final String LOG_PATH = "LOG_PATH";
//	
//	/** the default folder for log files data*/
//	public static final String LOG_FOLDER_NAME = "Logs";
//	
//	/** the folder for misc data*/
//	public static final String DATA_FOLDER_NAME = "Data";
//	
//	/** the folder for help html files */
//	public static final String DOCUMENTATION_FOLDER_NAME = "Documentation";
//	
//	/** the folder for plugin components*/
//	public static final String PLUGIN_FOLDER_NAME = "Plugins";
//	
//	
//	/** the suffix to append to the name of a jar file for the name of the folder holding subordinate jar files: 'Bin' */
//	public static final String JAR_BIN_FOLDER_SUFFIX = "Bin";
//	
//	/** the suffix to append to the name of a jar file for the name of the folder holding the jar's data: 'Data' */
//	public static final String JAR_DATA_FOLDER_SUFFIX = "Data";
//	
//	
//	/** the default name of the file containing the GoldenGATE logo icon image */
//	public static final String ICON_FILE_NAME = "GoldenGATE.logo.gif";
//	
//	
//	/** constant menu item to use for plugins to indicate a separator in their menu */
//	public static final JMenuItem MENU_SEPARATOR_ITEM = new JMenuItem();
}