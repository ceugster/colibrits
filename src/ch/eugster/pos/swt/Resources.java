/*
 * Created on 04.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.swt;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ch.eugster.pos.Messages;
import ch.eugster.pos.util.Path;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Resources
{
	
	public static void initialize()
	{
		Resources.ir = new ImageRegistry();
		
		String largeIcons = Path.getInstance().iconDir.concat(Messages.getString("Resources.banner_1")); //$NON-NLS-1$
		String smallIcons = Path.getInstance().iconDir.concat(Messages.getString("Resources.16x16_2")); //$NON-NLS-1$
		File iconDir = new File(Path.getInstance().iconDir);
		File largeIconDir = new File(largeIcons);
		File smallIconDir = new File(smallIcons);
		
		if (iconDir.exists() && iconDir.isDirectory())
		{
			FileFilter filter = new FileFilter()
			{
				public boolean accept(File pathname)
				{
					if (pathname.getName().endsWith(".gif")) { //$NON-NLS-1$
						return true;
					}
					return false;
				}
			};
			
			if (largeIconDir.exists() && largeIconDir.isDirectory())
			{
				File[] files = largeIconDir.listFiles(filter);
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].exists())
					{
						Resources.ir.put(files[i].getName(),
										new Image(Display.getCurrent(), files[i].getAbsolutePath()));
					}
				}
			}
			
			if (smallIconDir.exists() && smallIconDir.isDirectory())
			{
				File[] files = smallIconDir.listFiles(filter);
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].exists())
					{
						Resources.ir.put(files[i].getName(),
										new Image(Display.getCurrent(), files[i].getAbsolutePath()));
					}
				}
			}
			
		}
		
	}
	
	public static ImageRegistry getImageRegistry()
	{
		return Resources.ir;
	}
	
	private static ImageRegistry ir = null;
}
