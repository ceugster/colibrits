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
public class ColibriNewExportFileFilter implements FileFilter {

	/**
	 * 
	 */
	public ColibriNewExportFileFilter() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File file) {
		if (file.isDirectory()) return false;
		
		String name = file.getName();
		if (!name.endsWith(".xml")) return false; //$NON-NLS-1$
		
		return Long.parseLong(name.substring(0, name.length() - 4)) > 0;
	}
}
