/*
 * Created on 09.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.events;

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ModeChangeRequest {
	public boolean 	addModeChangeListener(ModeChangeListener l);
	public boolean 	removeModeChangeListener(ModeChangeListener l);
	public void 	fireModeChangeEvent(ModeChangeEvent e);
}
