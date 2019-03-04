package ch.eugster.pos.devices.displays;


/*
 * Created on 07.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ICustomerDisplay {
	public void clear(AbstractCustomerDisplay display);
	public void welcome(AbstractCustomerDisplay display);
	public void scrollUpperLine(AbstractCustomerDisplay display, String text);

	public void write(AbstractCustomerDisplay display, String text);
	public void write(AbstractCustomerDisplay display, byte[] text);
	public void selectInternationalCharacterSet(AbstractCustomerDisplay display, int characterSet);
	
	public static final int AMERICAN = 0;
	public static final int GERMAN = 1;
	public static final int FRENCH = 2;
	public static final int ITALIAN = 3;
	public static final int SPANISH = 4;

}
