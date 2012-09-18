/*
 * Created on 15.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.io.File;
import java.io.FileFilter;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ColibriOldExportFileFilter implements FileFilter {

	/**
	 * 
	 */
	public ColibriOldExportFileFilter() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File file) {
		if (file.isDirectory()) return false;
		
		String name = file.getName();
		if (name.startsWith("KB") && name.endsWith(".txt") && name.length() == 26) { //$NON-NLS-1$ //$NON-NLS-2$
			String p = "KP" + name.substring(2); //$NON-NLS-1$
			String b = file.getAbsolutePath().replaceFirst(name, p);
			File child = new File(b);
			return child.exists() && child.isFile();
		}
		else {
			return false; 
		}
	}
}
