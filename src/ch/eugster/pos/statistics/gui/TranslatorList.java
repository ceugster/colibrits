/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 */

package ch.eugster.pos.statistics.gui;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.eugster.pos.Messages;
import ch.eugster.pos.statistics.data.Translator;
import ch.eugster.pos.util.Path;

/**
 * Class that plays the role of the domain model in the TableViewerExample
 * In real life, this class would access a persistent store of some kind.
 * 
 */

public class TranslatorList {

	private final int COUNT = 10;
	private ArrayList translators = new ArrayList(COUNT);
	private Set changeListeners = new HashSet();

	/**
	 * Constructor
	 */
	public TranslatorList() {
		super();
		this.initData();
	}
	
	/*
	 * Initialize the table data.
	 * Create COUNT tasks and add them them to the 
	 * collection of tasks
	 */
	private void initData() {
		char[] buffer = new char[0];
		String path = Path.getInstance().cfgDir + "translator.data"; //$NON-NLS-1$
		File file = new File(path);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String s = br.readLine();
			while (s != null) {
				String[] u = s.split("\t");
				translators.add(new Translator(u[0], u[1], new Long(u[2])));
				s = br.readLine();
			}
			br.close();
			fr.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}

	}
	
	public void save() {
		if (dirty) {
			String path = Path.getInstance().cfgDir + "translator.data"; //$NON-NLS-1$
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			try {
				if (file.createNewFile()) {
					FileWriter writer = new FileWriter(file);
					Translator[] tl = (Translator[])translators.toArray(new Translator[0]);
					for (int i = 0; i < tl.length; i++) {
						writer.write(tl[i].sourceCode + "\t"); //$NON-NLS-1$
						writer.write(tl[i].targetTable + "\t"); //$NON-NLS-1$
						writer.write(tl[i].getTargetId().toString() + System.getProperty("line.separator")); //$NON-NLS-1$
					}
					writer.close();
				} 
			}
			catch (IOException e) {}
		}
	}

	/**
	 * Add a new task to the collection of tasks
	 */
	public void addTranslator(Translator translator) {
		translators.add(translators.size(), translator);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITranslatorListViewer) iterator.next()).addTranslator(translator);
		dirty = true;
	}

	/**
	 * @param task
	 */
	public void removeTranslator(Translator translator) {
		translators.remove(translator);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITranslatorListViewer) iterator.next()).removeTranslator(translator);
		dirty = true;
	}

	/**
	 * @param task
	 */
	public void translatorChanged(Translator translator) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITranslatorListViewer) iterator.next()).updateTranslator(translator);
		dirty = true;
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(ITranslatorListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(ITranslatorListViewer viewer) {
		changeListeners.add(viewer);
	}
	
	public ArrayList getTranslators() {
		return translators;
	}
	
	private boolean dirty = false;
	
	public static final String PRODUCT_GROUP = Messages.getString("TranslatorList.Warengruppe_8"); //$NON-NLS-1$
	public static final String PAYMENT_TYPE = Messages.getString("TranslatorList.Zahlungsart_9"); //$NON-NLS-1$
}
