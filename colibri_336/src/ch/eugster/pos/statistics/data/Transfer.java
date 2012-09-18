/*
 * Created on 16.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.util.Properties;

import org.eclipse.jface.operation.IRunnableWithProgress;

import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Transfer extends Runner implements IRunnableWithProgress
{
	
	/**
	 * 
	 */
	public Transfer(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties)
	{
		super();
		this.sc = salespointComposite;
		this.drg = dateRangeGroup;
		this.pdg = printDestinationGroup;
		this.properties = properties;
	}
	
	protected abstract String getLogfileName();
	
	protected PrintDestinationGroup pdg;
	protected Properties properties;
}
