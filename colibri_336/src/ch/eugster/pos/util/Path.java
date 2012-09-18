/*
 * Created on 11.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.util;

import java.io.File;

import ch.eugster.pos.Messages;
import ch.eugster.pos.events.MessageDialog;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Path {

	/**
	 * 
	 */
	private Path() {
		super();
	}
	
	public static Path getInstance() {
		if (path == null) {
			path = new Path();
		}
		return path;
	}

	public String getRootDirectory() {
		return System.getProperty("user.dir"); //$NON-NLS-1$
	}
	
	public boolean testPaths() {
		if (!testPath(lockDir, true)) {
			return false;
		}
		if (!testPath(iconDir, false)) {
			return false;
		}
		if (!testPath(logDir, true)) {
			return false;
		}
		if (!testPath(cfgDir, false)) {
			return false;
		}
		if (!testPath(ojbDir, false)) {
			return false;
		}
		if (!testPath(ojbDir, false)) {
			return false;
		}
		return true;
	}
	
	public boolean testPath(String path, boolean create) {
		File f = new File(path);
		f = new File(f.getAbsolutePath());
		
		if (!f.exists()) {
			if (create) {
				return f.mkdir();				
			} else {
				String msg = Messages.getString("Path.directory__2") +  f.getName() + Messages.getString("Path.in_root_directory_does_not_exist_3"); //$NON-NLS-1$ //$NON-NLS-2$
				MessageDialog.showInformation(null, Messages.getString("Path.directory_error"), msg, 0);
				return false;
			}
		}

		if (!f.isDirectory()) {
			String msg = Messages.getString("Path.file_6") +  f.getName() + Messages.getString("Path.in_root_directory_is_not_a_directory_7"); //$NON-NLS-1$ //$NON-NLS-2$
			MessageDialog.showInformation(null, Messages.getString("Path.directory_error"), msg, 0);
			return false;
		}
		return true;
	}
	
	public final String DIR_LOG 				= Messages.getString("Path.log").concat(File.separator); //$NON-NLS-1$
	public final String DIR_CFG 				= Messages.getString("Path.config").concat(File.separator); //$NON-NLS-1$
	public final String DIR_ICON 				= Messages.getString("Path.icons").concat(File.separator); //$NON-NLS-1$
	public final String DIR_ICON_MONEY 			= DIR_ICON.concat(Messages.getString("Path.money").concat(File.separator)); //$NON-NLS-1$
	public final String DIR_LOCK 				= Messages.getString("Path.lock").concat(File.separator); //$NON-NLS-1$
	public final String DIR_OJB					= Messages.getString("Path.ojb").concat(File.separator); //$NON-NLS-1$
	public final String DIR_SAV					= "save";
	public final String DIR_REP					= "reports"; //$NON-NLS-1$
	public final String DIR_CODE128				= "code128"; //$NON-NLS-1$
	public final String DIR_GALSERVE			= "galileo"; //$NON-NLS-1$
	
	public final String FILE_LOG 				= Messages.getString("Path.colibri.log"); //$NON-NLS-1$
	public final String FILE_CFG 				= "colibri.xml"; //$NON-NLS-1$
	public final String FILE_OJB 				= "repository.xml"; //$NON-NLS-1$
	public final String FILE_COLIBRI_LOCK 		= Messages.getString("Path.colibri.lock"); //$NON-NLS-1$
	public final String FILE_ADMIN_LOCK			= Messages.getString("Path.admin.lock"); //$NON-NLS-1$
	public final String FILE_STATISTICS_LOCK	= Messages.getString("Path.statistics.lock"); //$NON-NLS-1$
	public final String FILE_SPLASH 			= Messages.getString("Path.splash.gif"); //$NON-NLS-1$
	public final String FILE_OJBCFG 			= "OJB.properties"; //$NON-NLS-1$
	public final String FILE_JDBC_DEFAULTS 		= "jdbc.xml"; //$NON-NLS-1$
	public final String FILE_VFP6_DLL			= "VFP6R.DLL";
	
	public final String FILE_STATISTICS_INI  	= "statistics.properties"; //$NON-NLS-1$
	public final String FILE_RECEIPTBROWSER_INI  	= "receiptbrowser.properties"; //$NON-NLS-1$

	public String rootDir = getRootDirectory();
	public String lockDir = rootDir.concat(File.separator.concat(DIR_LOCK));
	public String iconDir = rootDir.concat(File.separator.concat(DIR_ICON));
	public String logDir = rootDir.concat(File.separator.concat(DIR_LOG));
	public String cfgDir = rootDir.concat(File.separator.concat(DIR_CFG));
	public String ojbDir = cfgDir.concat(DIR_OJB);
	public String savDir = rootDir.concat(File.separator.concat(DIR_SAV));
	public String code128Dir = cfgDir.concat(DIR_CODE128);
	public String repDir = rootDir.concat(File.separator.concat(DIR_REP));

	public String cfgFile = cfgDir.concat(FILE_CFG);
	public String jcdFile = cfgDir.concat(FILE_JDBC_DEFAULTS);
	public String ojbFile = ojbDir.concat(FILE_OJB);

	public String statisticsIniFile = cfgDir.concat(FILE_STATISTICS_INI);
	public String receiptBrowserIniFile = cfgDir.concat(FILE_RECEIPTBROWSER_INI);

	private static Path path = null;

//	public static final String DIR_LOG 				= Messages.getString("Path.log").concat(File.separator); //$NON-NLS-1$
//	public static final String DIR_CFG 				= Messages.getString("Path.config").concat(File.separator); //$NON-NLS-1$
//	public static final String DIR_ICON 			= Messages.getString("Path.icons").concat(File.separator); //$NON-NLS-1$
//	public static final String DIR_ICON_MONEY 		= DIR_ICON.concat(Messages.getString("Path.money").concat(File.separator)); //$NON-NLS-1$
//	public static final String DIR_LOCK 			= Messages.getString("Path.lock").concat(File.separator); //$NON-NLS-1$
//	public static final String DIR_OJB				= Messages.getString("Path.ojb").concat(File.separator); //$NON-NLS-1$
//	public static final String DIR_REP				= "reports"; //$NON-NLS-1$
//
//	public static final String FILE_LOG 			= Messages.getString("Path.colibri.log"); //$NON-NLS-1$
//	public static final String FILE_CFG 			= "colibri.xml"; //$NON-NLS-1$
//	public static final String FILE_OJB 			= "repository.xml"; //$NON-NLS-1$
//	public static final String FILE_COLIBRI_LOCK 	= Messages.getString("Path.colibri.lock"); //$NON-NLS-1$
//	public static final String FILE_ADMIN_LOCK		= Messages.getString("Path.admin.lock"); //$NON-NLS-1$
//	public static final String FILE_STATISTICS_LOCK = Messages.getString("Path.statistics.lock"); //$NON-NLS-1$
//	public static final String FILE_SPLASH 			= Messages.getString("Path.splash.gif"); //$NON-NLS-1$
//	public static final String FILE_OJBCFG 			= "OJB.properties"; //$NON-NLS-1$
//	public static final String FILE_JDBC_DEFAULTS 	= "jdbc.xml"; //$NON-NLS-1$
//	
//	public static final String FILE_STATISTICS_INI  = "statistics.properties"; //$NON-NLS-1$
//
//	public static String rootDir = getRootDirectory();
//	public static String lockDir = rootDir.concat(File.separator.concat(DIR_LOCK));
//	public static String iconDir = rootDir.concat(File.separator.concat(DIR_ICON));
//	public static String logDir = rootDir.concat(File.separator.concat(DIR_LOG));
//	public static String cfgDir = rootDir.concat(File.separator.concat(DIR_CFG));
//	public static String ojbDir = cfgDir.concat(DIR_OJB);
//	public static String repDir = rootDir.concat(File.separator.concat(DIR_REP));
//
//	public static String cfgFile = cfgDir.concat(FILE_CFG);
//	public static String jcdFile = cfgDir.concat(FILE_JDBC_DEFAULTS);
//	public static String ojbFile = ojbDir.concat(FILE_OJB);
//
//	public static String statisticsIniFile = cfgDir.concat(FILE_STATISTICS_INI);
//
//	private static Path path = null;
}
