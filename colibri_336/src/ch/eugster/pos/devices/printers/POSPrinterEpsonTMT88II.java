/*
 * Created on 29.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.devices.printers;

import java.io.IOException;

import org.jdom.Element;

import ch.eugster.pos.devices.AsciiConstants;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class POSPrinterEpsonTMT88II extends POSPrinter
{
	
	/**
	 * 
	 */
	public POSPrinterEpsonTMT88II(Element settings)
	{
		super(settings);
	}
	
	/**
	 * Initializes the printer.
	 * 
	 */
	public void initialize()
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.AT);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Sets the smaller of two standard fonts (9 * 17).
	 * 
	 */
	public void setSmallFont()
	{
		this.selectCharacterFont(49);
	}
	
	/**
	 * sets the larger of two standard fonts (12 * 24).
	 * 
	 */
	public void setLargeFont()
	{
		this.selectCharacterFont(48);
	}
	
	/**
	 * 
	 * @param font
	 *            an int that represents the font: 0, 48 = 12 * 24 1, 49 = 9 *
	 *            17
	 * 
	 */
	public void selectCharacterFont(int font)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.M);
				this.outputStream.write(font);
			}
			catch (IOException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param set
	 *            an int that represents the international character set: 0 =
	 *            U.S.A (default) 1 = France 2 = Germany 3 = U.K. 4 = Denmark I
	 *            5 = Sweden 6 = Italy 7 = Spain I 8 = Japan 9 = Norway 10 =
	 *            Denmark II 11 = Spain II 12 = Latin America 13 = Korea
	 */
	public void setInternationalCharacterSet(int set)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.R);
				this.outputStream.write(set);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Switches from page mode to standard mode.
	 * 
	 */
	public void selectStandardMode()
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.S);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * selects the print direction from left to right (as usual in europe).
	 * 
	 */
	public void selectPrintDirectionFromLeftToRight()
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.T);
				this.outputStream.write(48);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param direction
	 *            int: selects the print direction: 0, 48 = left to right 1, 49
	 *            = bottom to top 2, 50 = right to left 3, 51 = top to bottom
	 * 
	 */
	public void selectPrintDirection(int direction)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.T);
				this.outputStream.write(direction);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param h
	 *            (int) sets the relative horizontal position.
	 * @param v
	 *            (int) sets the relative vertical position.
	 */
	public void setRelativePrintPosition(int h, int v)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.BACKSLASH);
				this.outputStream.write(h);
				this.outputStream.write(v);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Justifies the printline to the left.
	 * 
	 */
	public void justifyLeft()
	{
		this.selectJustification(48);
	}
	
	/**
	 * Justifies the printline to the center.
	 * 
	 */
	public void justifyCenter()
	{
		this.selectJustification(49);
	}
	
	/**
	 * Justifies the printline to the right.
	 * 
	 */
	public void justifyRight()
	{
		this.selectJustification(50);
	}
	
	/**
	 * 
	 * @param justification
	 *            Justifies the printline to the left as indicated by
	 *            justification: 0, 48 = left 1, 49 = center 2, 50 = right
	 * 
	 */
	public void selectJustification(int justification)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.a);
				this.outputStream.write(justification);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param enable
	 *            enables/disables emphasized mode.
	 */
	public void setEmphasized(boolean enable)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.E);
				this.outputStream.write(enable ? 1 : 0);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param enable
	 *            enables/disables double strike mode.
	 */
	public void setDoubleStrike(boolean enable)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.G);
				this.outputStream.write(enable ? 1 : 0);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void printAndFeedPaper(int lf)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.J);
				this.outputStream.write(lf);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Only available in parallel mode
	 * 
	 * @param out
	 *            outputStreamputStream
	 * @param sensor
	 *            int enables/disables the sensors
	 */
	public void selectPaperSensorToOutputPaperEndSignal(int sensor)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.c);
				this.outputStream.write(AsciiConstants.THREE);
				this.outputStream.write(sensor);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void selectPaperSensorToStopPrinting(int sensor)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.c);
				this.outputStream.write(AsciiConstants.FOUR);
				this.outputStream.write(sensor);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void setEnabledPanelButtons(boolean enable)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.c);
				this.outputStream.write(AsciiConstants.FIVE);
				this.outputStream.write(enable ? 0 : 1);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void printAndFeedLines(String text, int feedLines)
	{
		if (this.usePOSPrinter)
		{
			this.print(text);
			this.feedLines(feedLines);
		}
	}
	
	public void feedLines(int lines)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.d);
				this.outputStream.write(lines);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	protected void kickOutDrawerOne(int t1, int t2)
	{
		this.generatePulse(0, t1, t2);
	}
	
	protected void kickOutDrawerTwo(int t1, int t2)
	{
		this.generatePulse(1, t1, t2);
	}
	
	public void kickOutDrawer(int drawer)
	{
		if (this.cashDrawers != null)
		{
			if (drawer >= 0 && drawer < this.cashDrawers.length)
			{
				this.cashDrawers[drawer].open();
			}
		}
	}
	
	public void kickOutDrawer(int[] drawers)
	{
		if (this.cashDrawers != null)
		{
			for (int i = 0; i < drawers.length; i++)
			{
				if (this.cashDrawers != null && drawers[i] < this.cashDrawers.length)
				{
					this.cashDrawers[i].open();
				}
			}
		}
	}
	
	public void generatePulse(int pin, int t1, int t2)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.p);
				this.outputStream.write(pin);
				this.outputStream.write(t1);
				this.outputStream.write(t2);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void kickOutDrawerOneRealtime(int time)
	{
		this.generatePulseRealtime(0, 4);
	}
	
	public void kickOutDrawerTwoRealtime(int time)
	{
		this.generatePulseRealtime(1, 4);
	}
	
	/**
	 * @param outputStream
	 * @param pin
	 *            sets the connector pin (0 = pin 2; 1 = pin 5)
	 * @param time
	 *            sets the pulse time (time * 100ms)
	 */
	public void generatePulseRealtime(int pin, int time)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.DLE);
				this.outputStream.write(AsciiConstants.DC4);
				this.outputStream.write(1);
				this.outputStream.write(pin);
				this.outputStream.write(time);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void selectCharacterCodeTableStandardEurope()
	{
		this.selectCharacterCodeTable(0);
	}
	
	public void selectCharacterCodeTable(int table)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.t);
				this.outputStream.write(table);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void setUpsideDown(boolean enabled)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.LEFT_BRACKET);
				this.outputStream.write(enabled ? 1 : 0);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param mode
	 *            sets the printer mode:
	 * 
	 *            bit 0: Character Font 0 = Font A (12x24) 1 = Font B (9x17)
	 * 
	 *            bit 3: Emphasized 0 = off 8 = on
	 * 
	 *            bit 4: Double height 0 = off 16 = on
	 * 
	 *            bit 5: Double width 0 = off 32 = on
	 * 
	 *            bit 7: Underline 0 = off 128 = on
	 * 
	 */
	public void selectPrintMode(int mode)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.EXCLAMATION);
				this.outputStream.write(mode);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param x
	 *            sets the absolute horizontal print position.
	 * @param y
	 *            sets the absolute vertical print position.
	 */
	public void setAbsolutePrintPosition(int x, int y)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.DOLLAR);
				this.outputStream.write(x);
				this.outputStream.write(y);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void setPrintingAreaWidth(int leftMargin, int width)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.W);
				this.outputStream.write(leftMargin);
				this.outputStream.write(width);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void selectFontForHumanReadableInterpretationCharacters(int font)
	{
		/*
		 * 
		 */
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.f);
				this.outputStream.write(font);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * 
	 * @param height
	 *            1 <= height <= 255
	 */
	public void selectBarCodeHeight(int height)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.h);
				this.outputStream.write(height);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void setBarCodeWidth(int width)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.ESC);
				this.outputStream.write(AsciiConstants.w);
				this.outputStream.write(width);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void printBarCode(int system, String code)
	{
		/*
		 * 
		 */
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.k);
				this.outputStream.write(system);
				this.outputStream.write(code.getBytes());
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void printBarCode(int system, int[] codes)
	{
		/*
		 * 
		 */
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.k);
				this.outputStream.write(system);
				for (int i = 0; i < codes.length; i++)
				{
					this.outputStream.write(codes[i]);
				}
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void selectCharacterSizeNormal()
	{
		this.selectCharacterSize(0);
	}
	
	public void selectCharacterSizeDoubleHeight()
	{
		this.selectCharacterSize(1);
	}
	
	/**
	 * 
	 * @param outputStream
	 * @param size
	 *            0 <= size <= 255 ( 0 = normal; 1 = double width)
	 */
	public void selectCharacterSize(int size)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.EXCLAMATION);
				this.outputStream.write(size);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void printLogo(int logo, int logoPrintMode)
	{
		this.printNVBitImage(logo, logoPrintMode);
	}
	
	public void printNVBitImage(int n, int m)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.FS);
				this.outputStream.write(AsciiConstants.p);
				this.outputStream.write(n);
				this.outputStream.write(m);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void defineDownloadedBitImage(int width, int height, byte[] image)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.ASTERISK);
				this.outputStream.write(width);
				this.outputStream.write(height);
				this.outputStream.write(image);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void printDownloadedBitImage(int density)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.SLASH);
				this.outputStream.write(density);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void executeTestPrint(int paper, int pattern)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.LEFT_BRACKET);
				this.outputStream.write(AsciiConstants.A);
				this.outputStream.write(2);
				this.outputStream.write(paper);
				this.outputStream.write(pattern);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void setWhiteBlackReversePrintingMode(boolean enable)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.B);
				this.outputStream.write(enable ? 1 : 0);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void selectPrintingPositionForHRICharacters(int position)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.H);
				this.outputStream.write(position);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void transmitPrinterID(int spec)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.I);
				this.outputStream.write(spec);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void setLeftMargin(int nl, int nh)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.L);
				this.outputStream.write(nl);
				this.outputStream.write(nh);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void setHorizontalAndVerticalMotionUnits(int x, int y)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.P);
				this.outputStream.write(x);
				this.outputStream.write(y);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public void fullCut()
	{
		this.selectCutModeAndCutPaper(1, 0, POSPrinterEpsonTMT88II.LINE_FEED_BEFORE_CUT);
	}
	
	public void partialCut()
	{
		this.selectCutModeAndCutPaper(0, 0, POSPrinterEpsonTMT88II.LINE_FEED_BEFORE_CUT);
	}
	
	public void fullCut(int lf)
	{
		this.selectCutModeAndCutPaper(1, 0, lf);
	}
	
	public void partialCut(int lf)
	{
		this.selectCutModeAndCutPaper(0, 0, lf);
	}
	
	public void selectCutModeAndCutPaper(int m, int n, int lf)
	{
		if (this.usePOSPrinter)
		{
			try
			{
				for (int i = 0; i < lf; i++)
				{
					this.outputStream.write(AsciiConstants.LF);
				}
				
				this.outputStream.write(AsciiConstants.GS);
				this.outputStream.write(AsciiConstants.V);
				this.outputStream.write(m);
				this.outputStream.write(n);
				this.outputStream.flush();
			}
			catch (IOException e)
			{
				//				Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(e.getLocalizedMessage()); //$NON-NLS-1$
			}
		}
	}
	
	public int getColumnCount(int font)
	{
		if (font == 0)
		{
			return this.getColumnCountByLargeFont();
		}
		else if (font == 1)
		{
			return this.getColumnCountBySmallFont();
		}
		else
		{
			return 0;
		}
	}
	
	public int getColumnCountByLargeFont()
	{
		return POSPrinterEpsonTMT88II.COLUMNS_COUNT_LARGE_FONT;
	}
	
	public int getColumnCountBySmallFont()
	{
		return POSPrinterEpsonTMT88II.COLUMNS_COUNT_SMALL_FONT;
	}
	
	public int getLineFeedBeforeCut()
	{
		return POSPrinterEpsonTMT88II.LINE_FEED_BEFORE_CUT;
	}
	
	protected static final int COLUMNS_COUNT_LARGE_FONT = 42;
	protected static final int COLUMNS_COUNT_SMALL_FONT = 56;
	protected static final int LINE_FEED_BEFORE_CUT = 6;
	
}
