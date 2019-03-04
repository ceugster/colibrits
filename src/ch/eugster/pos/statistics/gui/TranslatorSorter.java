/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.pos.statistics.data.Translator;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TranslatorSorter extends ViewerSorter
{
	
	/**
	 * 
	 */
	public TranslatorSorter(int column)
	{
		super();
		this.column = column;
	}
	
	/**
	 * @param collator
	 */
	public TranslatorSorter(Collator collator, int column)
	{
		super(collator);
		this.column = column;
	}
	
	public int compare(Viewer viewer, Object item1, Object item2)
	{
		Translator t1 = (Translator) item1;
		Translator t2 = (Translator) item2;
		
		int result;
		switch (this.column)
		{
			case SOURCE_CODE:
			{
				result = t1.sourceCode.compareTo(t2.sourceCode);
				break;
			}
			case TARGET_TABLE:
			{
				result = t1.targetTable.compareTo(t2.targetTable);
				break;
			}
			case TARGET_ID:
			{
				result = t1.getTargetId().compareTo(t2.getTargetId());
				break;
			}
			default:
				result = 0;
		}
		return result;
	}
	
	private int column;
	
	public static final int SOURCE_CODE = 0;
	public static final int TARGET_TABLE = 1;
	public static final int TARGET_ID = 2;
}
