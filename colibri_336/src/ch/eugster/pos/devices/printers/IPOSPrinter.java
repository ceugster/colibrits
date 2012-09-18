/*
 * Created on 13.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.devices.printers;




/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IPOSPrinter {
	/**
	 * Initializes the printer.
	 * 
	 */
	public abstract void initialize();
	
	/**
	 * Sets the smaller of two standard fonts.
	 * 
	 */
	public abstract void setSmallFont();
	
	/**
	 * sets the larger of two standard fonts.
	 *
	 */
	public abstract void setLargeFont();

	/**
	 * 
	 * @param font an int that represents the font:
	 * 				0, 48 = 12 * 24
	 * 				1, 49 =  9 * 17
	 * 
	 */
	public abstract void selectCharacterFont(int font);
	
	/**
	 * 
	 * @param set an int that represents the international character set.
	 * 
	 */
	public abstract void setInternationalCharacterSet(int set);
	
	/**
	 * Switches from page mode to standard mode.
	 *
	 */
	public abstract void selectStandardMode();
	
	/**
	 * selects the print direction from left to right (as usual in europe).
	 *
	 */
	public abstract void selectPrintDirectionFromLeftToRight();
	
	/**
	 * 
	 * @param direction int: selects the print direction.
	 * 			
	 */
	public abstract void selectPrintDirection(int direction);
	
	/**
	 * 
	 * @param h (int) sets the relative horizontal position.
	 * @param v (int) sets the relative vertical position.
	 */
	public abstract void setRelativePrintPosition(int h, int v);
	
	/**
	 * Justifies the printline to the left.
	 *
	 */
	public abstract void justifyLeft();

	/**
	 * Justifies the printline to the center.
	 *
	 */
	public abstract void justifyCenter();
	
	/**
	 * Justifies the printline to the right.
	 *
	 */
	public abstract void justifyRight();

	/**
	 * 
	 * @param justification Justifies the printline to the left as indicated by 
	 * justification:
	 * 				0, 48 = left
	 * 				1, 49 = center
	 * 				2, 50 = right
	 *
	 */
	public abstract void selectJustification(int justification);

	/**
	 * 
	 * @param enable enables/disables emphasized mode.
	 */
	public abstract void setEmphasized(boolean enable);

	/**
	 * 
	 * @param enable enables/disables double strike mode.
	 */
	public abstract void setDoubleStrike(boolean enable);
	
	/**
	 * 
	 * @param n 
	 */
	public abstract void printAndFeedPaper(int n);
	/**
	 * Only available in parallel mode
	 * 
	 * @param out OutputStream
	 * @param sensor int enables/disables the sensors
	 */
	public abstract void selectPaperSensorToOutputPaperEndSignal(int sensor);
	public abstract void selectPaperSensorToStopPrinting(int sensor);
	public abstract void setEnabledPanelButtons(boolean enable);
	public abstract void printAndFeedLines(String text, int lines);
	public abstract void generatePulse(int pin, int t1, int t2);
	public abstract void kickOutDrawerOneRealtime(int t1);
	public abstract void kickOutDrawerTwoRealtime(int t1);
	public abstract void generatePulseRealtime(int pin, int t1);
	public abstract void selectCharacterCodeTableStandardEurope();
	public abstract void selectCharacterCodeTable(int table);
	public abstract void setUpsideDown(boolean enabled);

	/**
	 * 
	 * @param mode
	 * 
	 */
	public abstract void selectPrintMode(int mode);

	/**
	 * 
	 * @param x sets the absolute horizontal print position.
	 * @param y sets the absolute vertical print position.
	 */
	public abstract void setAbsolutePrintPosition(int x, int y);
	public abstract void setPrintingAreaWidth(int leftMargin, int width);
	public abstract void selectFontForHumanReadableInterpretationCharacters(int font);

	/**
	 * 
	 * @param height 1 <= height <= 255
	 */
	public abstract void selectBarCodeHeight(int height);
	public abstract void setBarCodeWidth(int width);
	public abstract void printBarCode(int system, String code);
	public abstract void printBarCode(int system, int[] codes);
	public abstract void selectCharacterSizeNormal();
	public abstract void selectCharacterSizeDoubleHeight();

	/**
	 * 
	 * @param size 0 <= size <= 255 ( 0 = normal; 1 = double width)
	 */
	public abstract void selectCharacterSize(int size);
	public abstract void printLogo(int logo, int logoPrintMode);
	public abstract void printNVBitImage(int n, int m);
	public abstract void defineDownloadedBitImage(int width, int height, byte[] image);
	public abstract void printDownloadedBitImage(int density);
	public abstract void executeTestPrint(int paper, int pattern);
	public abstract void setWhiteBlackReversePrintingMode(boolean enable);
	public abstract void selectPrintingPositionForHRICharacters(int position);
	public abstract void transmitPrinterID(int spec);
	public abstract void setLeftMargin(int nl, int nh);
	public abstract void setHorizontalAndVerticalMotionUnits(int x, int y);
	public abstract void fullCut();
	public abstract void partialCut();
	public abstract void fullCut(int lf);
	public abstract void partialCut(int lf);
	public abstract void selectCutModeAndCutPaper(int m, int n, int lf);

	public abstract int getColumnCount(int font);
	public abstract int getColumnCountByLargeFont();
	public abstract int getColumnCountBySmallFont();
	public abstract int getLineFeedBeforeCut();
}
