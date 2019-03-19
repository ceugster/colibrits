/*
 * Created on 18.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.gui;

import ch.eugster.pos.statistics.core.Translator;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ITranslatorListViewer {
	public void addTranslator(Translator translator);
	public void removeTranslator(Translator translator);
	public void updateTranslator(Translator translator);
}
