/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.core;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.ProductGroup;


/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Translator {

	public String sourceCode;
	public String targetTable;
	private Long targetId;
	private ProductGroup pg = null;
	private PaymentType pt = null;
	/**
	 * 
	 */
	public Translator() {
		super();
		sourceCode = ""; //$NON-NLS-1$
		targetTable = ""; //$NON-NLS-1$
		targetId = new Long(0l);
	}

	public Translator(String source, String table, Long id) {
		super();
		sourceCode = source;
		targetTable = table;
		setTargetId(id);
	}
	
	private void setItem() {
		if (targetTable.equals(tables[0])) {
			pg = ProductGroup.selectById(targetId);
			if (pg.getId() == null || pg.getId().equals(ProductGroup.ZERO_VALUE)) {
				pg = null;
			}
			pt = null;
		}
		else if (targetTable.equals(tables[1])) {
			pt = PaymentType.selectById(targetId);
			if (pt.getId() == null || pt.getId().equals(PaymentType.ZERO_VALUE)) {
				pt = null;
			}
			pg = null;
		}
	}
	
	public void setTargetId(Long id) {
		this.targetId = id;
		this.setItem();
	}
	
	public Long getTargetId() {
		return this.targetId;
	}
	
	public String getValue() {
			if (pg != null) {
				return targetId + " " + pg.name;
			}
			else if (pt != null) {
				return targetId + " " + pt.name;
			}
			return null;
	}

	private static String[] tables = Messages.getString("Constants.tables").split(":");
}
