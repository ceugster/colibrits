/*
 * Created on 24.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.devices.displays;

import java.io.IOException;

import org.jdom.Element;

import ch.eugster.pos.devices.AsciiConstants;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Cd7220Display extends AbstractCustomerDisplay
{
	
	public Cd7220Display(Element element)
	{
		super(element);
		
		for (int i = 0; i < Cd7220Display.validFonts.length; i++)
		{
			switch (i)
			{
				case Cd7220Display.IC_AMERICAN:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_BRITISH:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_DANISH1:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_DANISH2:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_FRENCH:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_GERMAN:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_ITALIAN:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_JAPANESE:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_NORWEGIAN:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_RUSSIAN:
					Cd7220Display.validFonts[i] = true;
					break;
				case Cd7220Display.IC_SWEDISH:
					Cd7220Display.validFonts[i] = true;
					break;
				default:
					Cd7220Display.validFonts[i] = false;
			}
		}
		this.setDisplayEmulationMode(55);
		this.selectInternationalCharacterSet(this.codetable);
	}
	
	public void selectInternationalCharacterSet(int characterSet)
	{
		int set = 0;
		switch (characterSet)
		{
			case ICustomerDisplay.AMERICAN:
				set = Cd7220Display.IC_AMERICAN;
				break;
			case ICustomerDisplay.FRENCH:
				set = Cd7220Display.IC_FRENCH;
				break;
			case ICustomerDisplay.GERMAN:
				set = Cd7220Display.IC_GERMAN;
				break;
			case ICustomerDisplay.ITALIAN:
				set = Cd7220Display.IC_ITALIAN;
				break;
			case ICustomerDisplay.SPANISH:
				set = Cd7220Display.IC_SPANISH;
				break;
			default:
				set = 0;
		}
		this.selectInternationalFont(set);
	}
	
	public void scrollUpperLine(String text)
	{
		this.setUpperLineMessageScrollContinuously(text.getBytes());
	}
	
	protected void setDisplayEmulationMode(int mode)
	{
		if (this.useCustomerDisplay)
		{
			try
			{
				this.outputStream.write(AsciiConstants.STX);
				this.outputStream.write(AsciiConstants.ENQ);
				this.outputStream.write(AsciiConstants.C);
				this.outputStream.write(mode);
				this.outputStream.write(AsciiConstants.ETX);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	protected void setOverwriteMode()
	{
		if (this.useCustomerDisplay)
		{
			try
			{
				this.outputStream.write(AsciiConstants.HT);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	protected void setVerticalScrollMode()
	{
		if (this.useCustomerDisplay)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.DC2);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	protected void setHorizontalScrollMode()
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.DC3;
		this.write(b);
	}
	
	protected void setStringDisplayModeAndWrite(int line, byte[] text)
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.Q;
		b[2] = new Integer(line).byteValue();
		this.write(b);
		this.write(text);
		b = new byte[1];
		b[0] = AsciiConstants.CR;
		this.write(b);
	}
	
	public void setUpperLineMessageScrollContinuously(byte[] text)
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.Q;
		b[2] = AsciiConstants.D;
		this.write(b);
		this.write(text);
		b = new byte[1];
		b[0] = AsciiConstants.CR;
		this.write(b);
	}
	
	protected void moveCursorLeft()
	{
		byte[] b = new byte[3];
		b[0] = 27;
		b[1] = 91;
		b[2] = 68;
		this.write(b);
	}
	
	protected void moveCursorRight()
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.LEFT_BRACKET;
		b[2] = AsciiConstants.C;
		this.write(b);
	}
	
	protected void moveCursorUp()
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.LEFT_BRACKET;
		b[2] = AsciiConstants.A;
		this.write(b);
	}
	
	protected void moveCursorDown()
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.LEFT_BRACKET;
		b[2] = AsciiConstants.B;
		this.write(b);
	}
	
	public void moveCursorHome()
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.LEFT_BRACKET;
		b[2] = AsciiConstants.H;
		this.write(b);
	}
	
	protected void moveCursorToLeftMostPosition()
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.LEFT_BRACKET;
		b[2] = AsciiConstants.L;
		this.write(b);
	}
	
	protected void moveCursorToRightMostPosition()
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.LEFT_BRACKET;
		b[2] = AsciiConstants.R;
		this.write(b);
	}
	
	protected void moveCursorToBottomPosition()
	{
		byte[] b = new byte[3];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.LEFT_BRACKET;
		b[2] = AsciiConstants.K;
		this.write(b);
	}
	
	protected void moveCursorToSpecifiedPosition(int x, int y)
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.l;
		this.write(b);
		this.write(x);
		this.write(y);
	}
	
	public void initialize()
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.AT;
		this.write(b);
	}
	
	protected void reset()
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.W;
		this.write(b);
		this.write(0);
	}
	
	protected void reset(int start, int end, int line)
	{
		start = start == 0 ? 1 : start;
		end = end == 0 ? this.lineLength : end;
		line = line < 1 ? 1 : line;
		line = line > this.lineCount ? this.lineCount : line;
		
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.W;
		this.write(b);
		this.write(1);
		this.write(start);
		this.write(end);
		this.write(line);
	}
	
	public void clear()
	{
		this.write(AsciiConstants.FF);
	}
	
	protected void clearCurrentLine()
	{
		this.write(AsciiConstants.CAN);
	}
	
	/**
	 * @param n
	 *            values: 3: brightness = 70% 4: brightness = 100%
	 */
	protected void brightnessAdjustment(int n)
	{
		if (n > 2 && n < 5)
		{
			byte[] b = new byte[2];
			b[0] = AsciiConstants.ESC;
			b[1] = AsciiConstants.ASTERISK;
			this.write(b);
			this.write(n);
		}
	}
	
	/**
	 * @param mode
	 *            0 = set cursor OFF 1 = set cursor ON
	 */
	protected void setCursorOnOff(int mode)
	{
		if (mode == 0 || mode == 1)
		{
			byte[] b = new byte[2];
			b[0] = AsciiConstants.ESC;
			b[1] = AsciiConstants.ASTERISK;
			this.write(b);
			this.write(mode);
		}
	}
	
	/**
	 * @param font
	 */
	public void selectInternationalFont(int font)
	{
		if (Cd7220Display.validFonts[font])
		{
			byte[] b = new byte[2];
			b[0] = AsciiConstants.ESC;
			b[1] = AsciiConstants.f;
			this.write(b);
			this.write(font);
		}
	}
	
	/**
	 * @param device
	 *            valid values: 1 (enable printer, disable display), 2 (disable
	 *            printer, enable display), 3 (enable printer, enable display)
	 */
	protected void selectPeripheralDevice(int device)
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.EQUAL;
		this.write(b);
		this.write(device);
	}
	
	protected void setUserDefinedCharacters(int m, int n, int a)
	{
	}
	
	/**
	 * @param m
	 *            beginning charactercode
	 * @param n
	 *            ending charactercode
	 * @param a
	 *            character
	 */
	protected void setUserDefinedCharacters(int m, int n, int a, int x)
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.AMPERSAND;
		this.write(b);
		this.write(1);
		this.write(m);
		this.write(n);
		this.write(a);
		this.write(x);
	}
	
	/**
	 * @param n
	 *            valid values: 0 = cancel user defined characters, use
	 *            international character set 1 = select user defined character
	 *            set, if defined by <code>setUserDefinedCharacters</code>
	 */
	protected void resetUserDefinedCharacterSet(int n)
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.PERCENT;
		this.write(b);
		this.write(n);
	}
	
	/**
	 * user defined characters are cancelled
	 * 
	 * @param n
	 *            cancel the defined characters specified by n
	 */
	protected void cancelUserDefinedCharacters(int n)
	{
		byte[] b = new byte[2];
		b[0] = AsciiConstants.ESC;
		b[1] = AsciiConstants.QUESTION_MARK;
		this.write(b);
		this.write(n);
	}
	
	protected static final int IC_AMERICAN = AsciiConstants.A;
	protected static final int IC_GERMAN = AsciiConstants.G;
	protected static final int IC_ITALIAN = AsciiConstants.I;
	protected static final int IC_JAPANESE = AsciiConstants.J;
	protected static final int IC_BRITISH = AsciiConstants.U;
	protected static final int IC_FRENCH = AsciiConstants.F;
	protected static final int IC_SPANISH = AsciiConstants.S;
	protected static final int IC_NORWEGIAN = AsciiConstants.N;
	protected static final int IC_SWEDISH = AsciiConstants.W;
	protected static final int IC_DANISH1 = AsciiConstants.D;
	protected static final int IC_DANISH2 = AsciiConstants.E;
	protected static final int IC_SLAVONIC = AsciiConstants.L;
	protected static final int IC_RUSSIAN = AsciiConstants.R;
	
	protected static final int EM_DSP800 = 48;
	protected static final int EM_ESC_POS = 49;
	protected static final int EM_ADM788 = 50;
	protected static final int EM_ADM787 = 51;
	protected static final int EM_AEDEX = 52;
	protected static final int EM_UTC_P = 53;
	protected static final int EM_UTC_S = 54;
	protected static final int EM_CD5220 = 55;
	
	protected static boolean[] validFonts = new boolean[127];
	
}
