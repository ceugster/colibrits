/*
 * Created on 01.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.printing;

/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SettlementGroupData {

	public Long id;
	public String galileoId = ""; //$NON-NLS-1$
	public String name;
	public Double items = new Double(0d);
	public Double amount = new Double(0d);
	public Double tax = new Double(0d);

	/**
	 * 
	 */
	public SettlementGroupData() {
		super();
	}
	/**
	 * 
	 * @param id Die Id des gruppierten Objektes
	 * @param name Die Bezeichnung des gruppierten Objektes
	 */
	public SettlementGroupData(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	/**
	 * 
	 * @param id Die Id des gruppierten Objektes
	 * @param galileoId Die GalileoId (also die Warengruppennummer, welche die Produktgruppe
	 * in Galileo hat). Wird nur bei Produktgruppen verwendet.
	 * @param name Die Bezeichnung des gruppierten Objektes
	 */
	public SettlementGroupData(Long id, String galileoId, String name) {
		super();
		this.id = id;
		this.galileoId = galileoId;
		this.name = name;
	}
	
}
