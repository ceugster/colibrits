/*
 * Created on 26.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.swt;

import java.text.DecimalFormat;

import org.eclipse.jface.dialogs.IInputValidator;

import ch.eugster.pos.Messages;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DoubleInputValidator implements IInputValidator
{
	
	private double min = Double.NEGATIVE_INFINITY;
	private double max = Double.MAX_VALUE;
	private String pattern = Messages.getString("DoubleInputValidator._#._#_#_1"); //$NON-NLS-1$
	
	/**
	 * 
	 */
	public DoubleInputValidator()
	{
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
	 */
	public String isValid(String text)
	{
		DecimalFormat df = new DecimalFormat(this.pattern);
		double value = 0d;
		
		try
		{
			value = Double.parseDouble(df.format(Double.parseDouble(text)));
		}
		catch (NumberFormatException e)
		{
			return Messages.getString("DoubleInputValidator.Die_Eingabe_muss_numerisch_sein._2"); //$NON-NLS-1$
		}
		
		if (value < this.min || value > this.max)
		{
			return Messages.getString("DoubleInputValidator.Die_Eingabe_muss_zwischen__3") + this.min + Messages.getString("DoubleInputValidator._und__4") + this.max + Messages.getString("DoubleInputValidator._liegen._5"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		return null;
	}
	
	public void setRange(double min, double max)
	{
		this.min = min;
		this.max = max;
	}
	
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}
	
}
