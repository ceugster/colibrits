/*
 * Created on 07.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.statistics.data;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.pos.Messages;
import ch.eugster.pos.db.CurrentTax;
import ch.eugster.pos.db.ForeignCurrency;
import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.PaymentType;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.ProductGroup;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.db.Salespoint;
import ch.eugster.pos.statistics.gui.DateRangeGroup;
import ch.eugster.pos.statistics.gui.SalespointComposite;
import ch.eugster.pos.statistics.gui.SettlementComposite;
import ch.eugster.pos.statistics.gui.SettlementDateComposite;
import ch.eugster.pos.statistics.gui.SettlementNumberComposite;
import ch.eugster.pos.statistics.gui.SettlementReceiptComposite;
import ch.eugster.pos.swt.PrintDestinationGroup;
import ch.eugster.pos.util.NumberUtility;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SettlementStatistics extends Statistics
{
	
	/**
	 * @param salespointComposite
	 * @param dateRangeGroup
	 * @param printDestinationGroup
	 * @param properties
	 */
	public SettlementStatistics(SalespointComposite salespointComposite, DateRangeGroup dateRangeGroup,
					PrintDestinationGroup printDestinationGroup, Properties properties,
					SettlementComposite settlementComposite)
	{
		super(salespointComposite, dateRangeGroup, printDestinationGroup, properties);
		this.settlementComposite = settlementComposite;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setOptions()
	 */
	protected void setOptions()
	{
		this.printReversedReceipts = this.settlementComposite.getFullSelection();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#setReport()
	 */
	protected void setReport()
	{
		if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			SettlementDateComposite cp = (SettlementDateComposite) this.settlementComposite.getCurrentControl();
			int report = cp.getSelectedDateReport();
			switch (report)
			{
				case 0:
				{
					this.reportTemplate = "SettlementStatistics"; //$NON-NLS-1$
					this.reportDesignName = "SettlementStatistics"; //$NON-NLS-1$
					break;
				}
				case 2:
				{
					this.reportTemplate = "SettlementDiscounts"; //$NON-NLS-1$
					this.reportDesignName = "SettlementDiscounts"; //$NON-NLS-1$
					break;
				}
			}
		}
		else
		{
			this.reportTemplate = "SettlementStatistics"; //$NON-NLS-1$
			this.reportDesignName = "SettlementStatistics"; //$NON-NLS-1$
		}
	}
	
	protected boolean configureReport()
	{
		return false;
	}
	
	public void run(IProgressMonitor monitor)
	{
		if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
		{
			super.run(monitor);
		}
		else if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			SettlementDateComposite dateComposite = (SettlementDateComposite) this.settlementComposite
							.getCurrentControl();
			this.doRun(monitor, dateComposite.getSelectedDateReport());
		}
		else if (this.settlementComposite.getCurrentControl() instanceof SettlementReceiptComposite)
		{
			this.doRun(monitor, 0);
		}
	}
	
	protected void doRun(IProgressMonitor monitor, int selectedReport)
	{
		String path = "";
		
		switch (selectedReport)
		{
			case 0:
			{
				this.rows = new Hashtable();
				Salespoint[] salespoints = null;
				if (!this.sc.areAllSalespointsSelected())
				{
					salespoints = this.sc.getSelectedSalespoints();
				}
				
				this.defineDateRange();
				
				this.count = Receipt.count(salespoints, this.from, this.to,
								this.settlementComposite.getFullSelection(), true);
				if (this.count == 0l)
				{
					org.eclipse.jface.dialogs.MessageDialog
									.openInformation(
													this.pdg.getShell(),
													Messages.getString("Statistics.Keine_Daten_gefunden_14"), Messages.getString("Statistics.Es_wurden_keine_Daten_innerhalb_des_Selektionsbereiches_gefunden._15")); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
				
				if (this.prepare(monitor, 13))
				{
					this.rows = new Hashtable();
					
					Iterator positions = this.selectPositionData(salespoints, this.from, this.to);
					monitor.worked(1);
					this.preparePositionOutput(positions);
					monitor.worked(1);
					
					Iterator payments = this.selectPaymentData(salespoints, this.from, this.to);
					monitor.worked(1);
					this.preparePaymentOutput(payments);
					monitor.worked(1);
					
					if (this.settlementComposite.getFullSelection())
					{
						Iterator receipts = this.selectReversedReceipts(salespoints, this.from, this.to);
						this.prepareReversedReceiptsOutput(receipts);
					}
					
					JRDataSource source = this.computeOutput(monitor);
					
					if (this.pdg.getDestination() == PrintDestinationGroup.FILE) path = this.pdg.getFileName();
					
					this.preparePrint(monitor, source, path);
					
					this.complete(monitor);
					return;
				}
			}
			case 1:
			{
				/*
				 * Nichts tun: Die SettlementDiffs-Report ist nur möglich für
				 * Settlement-Datensätze
				 */
				org.eclipse.jface.dialogs.MessageDialog
								.openInformation(
												this.pdg.getShell(),
												Messages.getString("Statistics.Keine_Daten_gefunden_14"), Messages.getString("Statistics.Es_wurden_keine_Daten_innerhalb_des_Selektionsbereiches_gefunden._15")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			case 2:
			{
				/*
				 * Nichts tun. Falls Positionen vorhanden wären, dann hätte sie
				 * bereits SettlementStatistics2 gefunden.
				 */
				org.eclipse.jface.dialogs.MessageDialog
								.openInformation(
												this.pdg.getShell(),
												Messages.getString("Statistics.Keine_Daten_gefunden_14"), Messages.getString("Statistics.Es_wurden_keine_Daten_innerhalb_des_Selektionsbereiches_gefunden._15")); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}
	}
	
	protected Iterator selectData()
	{
		Salespoint[] salespoints = null;
		if (!this.sc.areAllSalespointsSelected())
		{
			salespoints = this.sc.getSelectedSalespoints();
		}
		Iterator iterator = null;
		if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
		{
			iterator = Receipt.select(this.settlementComposite.getSelectedSettlement(), this.settlementComposite
							.getFullSelection());
		}
		else if (this.settlementComposite.getCurrentControl() instanceof SettlementDateComposite)
		{
			iterator = Receipt.selectSettled(salespoints, this.from, this.to, this.settlementComposite
							.getFullSelection());
		}
		else if (this.settlementComposite.getCurrentControl() instanceof SettlementReceiptComposite)
		{
			iterator = Receipt.selectSettled(salespoints, this.from, this.to, this.settlementComposite
							.getFullSelection());
		}
		return iterator;
	}
	
	// private Collection selectData(Salespoint[] salespoints, Date date,
	// boolean reversedToo)
	// {
	// return Receipt.selectSettled(salespoints, date, reversedToo);
	// }
	
	private Iterator selectPositionData(Salespoint[] salespoints, Date from, Date to)
	{
		return Position.selectSettled(salespoints, from, to);
	}
	
	private Iterator selectPaymentData(Salespoint[] salespoints, Date from, Date to)
	{
		return Payment.selectSettled(salespoints, from, to);
	}
	
	private Iterator selectReversedReceipts(Salespoint[] salespoints, Date from, Date to)
	{
		return Receipt.selectReversed(salespoints, from, to);
	}
	
	protected void preparePositionOutput(Iterator iterator)
	{
		while (iterator.hasNext())
		{
			Object object = iterator.next();
			Object[] row = (Object[]) object;
			this.putPosition(this.rows, row);
		}
	}
	
	protected void preparePaymentOutput(Iterator iterator)
	{
		while (iterator.hasNext())
		{
			Object object = iterator.next();
			Object[] row = (Object[]) object;
			this.putPayment(this.rows, row);
		}
	}
	
	protected void prepareReversedReceiptsOutput(Iterator iterator)
	{
		while (iterator.hasNext())
		{
			Object object = iterator.next();
			Object[] row = (Object[]) object;
			this.putReversed(this.rows, row);
		}
	}
	
	private JRDataSource computeOutput(IProgressMonitor monitor)
	{
		
		ArrayList list = new ArrayList();
		Enumeration enumerationeration = this.rows.elements();
		while (enumerationeration.hasMoreElements())
		{
			list.add(enumerationeration.nextElement());
		}
		this.rows.clear();
		monitor.worked(1);
		
		Row[] rows = (Row[]) list.toArray(new Row[0]);
		list.clear();
		monitor.worked(1);
		
		Arrays.sort(rows);
		monitor.worked(1);
		
		return new JRMapArrayDataSource(rows);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.pos.statistics.data.Statistics#computeOutput(java.util.Iterator
	 * )
	 */
	protected JRDataSource computeOutput(Iterator iterator)
	{
		Hashtable ht = new Hashtable();
		this.betrag = 0d;
		this.count = 0;
		while (iterator.hasNext())
		{
			Receipt r = (Receipt) iterator.next();
			if (r.status == Receipt.RECEIPT_STATE_SERIALIZED)
			{
				this.count++;
				this.putPositions(ht, r);
				this.putPayments(ht, r);
			}
			else if (r.status == Receipt.RECEIPT_STATE_REVERSED)
			{
				this.putReversed(ht, r);
			}
		}
		
		ArrayList list = new ArrayList();
		Enumeration enumerationeration = ht.elements();
		while (enumerationeration.hasMoreElements())
		{
			list.add(enumerationeration.nextElement());
		}
		ht.clear();
		
		Row[] rows = (Row[]) list.toArray(new Row[0]);
		list.clear();
		Arrays.sort(rows);
		
		return new JRMapArrayDataSource(rows);
	}
	
	private void putPositions(Hashtable ht, Receipt r)
	{
		Position[] p = r.getPositionsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			this.putPosition(ht, p[i]);
		}
	}
	
	private void putPosition(Hashtable ht, Position p)
	{
		String key = null;
		
		if (p.getProductGroup().type == ProductGroup.TYPE_INCOME)
		{
			key = this.getIncomeKey(p);
		}
		else if (p.getProductGroup().type == ProductGroup.TYPE_NOT_INCOME)
		{
			key = this.getOtherSalesKey(p);
		}
		else if (p.getProductGroup().type == ProductGroup.TYPE_EXPENSE)
		{
			key = this.getExpenseKey(p);
		}
		else if (p.getProductGroup().type == ProductGroup.TYPE_INPUT)
		{
			key = this.getInputKey(p);
		}
		else if (p.getProductGroup().type == ProductGroup.TYPE_WITHDRAW)
		{
			key = this.getWithdrawKey(p);
		}
		
		Row row = (Row) ht.get(key);
		if (row == null)
		{
			row = new Row(p, key);
			ht.put(key, row);
		}
		else
		{
			row.update(p);
			ht.put(key, row);
		}
		
		if (p.getProductGroup().type != ProductGroup.TYPE_INPUT
						&& p.getProductGroup().type != ProductGroup.TYPE_WITHDRAW)
		{
			key = this.getTaxKey(p);
			row = (Row) ht.get(key);
			if (row == null)
			{
				row = new Row(p, key);
				ht.put(key, row);
			}
			else
			{
				row.update(p);
				ht.put(key, row);
			}
		}
	}
	
	private void putPayments(Hashtable ht, Receipt r)
	{
		Payment[] p = r.getPaymentsAsArray();
		for (int i = 0; i < p.length; i++)
		{
			this.putPayment(ht, p[i]);
		}
	}
	
	private void putPayment(Hashtable ht, Object[] p)
	{
		String key;
		// Long ptId = (Long) p[0];
		// Long fcId = (Long) p[2];
		boolean back = ((Boolean) p[3]).booleanValue(); // 10372
		// Double amount = (Double) p[4];
		
		PaymentType type = PaymentType.selectById((Long) p[0]);
		ForeignCurrency cur = ForeignCurrency.selectById((Long) p[2]);
		
		if (p[2].equals(ForeignCurrency.getDefaultCurrency().getId()))
		{
			// 10372
			if (back)
			{
				// 10373
				// key = this.getBackKey(p);
				if (cur.getId().equals(ForeignCurrency.getDefaultCurrency().getId())
								&& !type.getId().equals(PaymentType.selectCash().getId()))
				{
					key = this.getBackKey(p);
				}
				else
					key = this.getPayKey(p);
				// 10373
			}
			else
			{
				// 10372
				key = this.getPayKey(p);
				// 10372
			}
			// 10372
			
			Row row = (Row) ht.get(key);
			if (row == null)
			{
				row = new Row(p, key);
				ht.put(key, row);
			}
			else
			{
				row.updatePayment(p);
				ht.put(key, row);
			}
		}
		else
		{
			// 10372
			// if (back)
			// {
			// key = this.getBackCurKey(p);
			// }
			// else
			// {
			key = this.getCurKey(p);
			// }
			// 10372
			
			Row row = (Row) ht.get(key);
			if (row == null)
			{
				row = new Row(p, key);
				ht.put(key, row);
			}
			else
			{
				row.updatePayment(p);
				ht.put(key, row);
			}
		}
	}
	
	private void putPayment(Hashtable ht, Payment p)
	{
		String key;
		if (p.getForeignCurrency().getId().equals(ForeignCurrency.getDefaultCurrency().getId()))
		{
			if (p.isBack())
			{
				key = this.getBackKey(p);
			}
			else
			{
				key = this.getPayKey(p);
			}
			
			Row row = (Row) ht.get(key);
			if (row == null)
			{
				row = new Row(p, key);
				ht.put(key, row);
			}
			else
			{
				row.update(p);
				ht.put(key, row);
			}
		}
		else
		{
			if (p.isBack())
			{
				key = this.getBackCurKey(p);
			}
			else
			{
				key = this.getCurKey(p);
			}
			
			Row row = (Row) ht.get(key);
			if (row == null)
			{
				row = new Row(p, key);
				ht.put(key, row);
			}
			else
			{
				row.update(p);
				ht.put(key, row);
			}
		}
	}
	
	private void putReversed(Hashtable ht, Object[] r)
	{
		String key = this.getRevKey(r);
		Row row = (Row) ht.get(key);
		if (row == null)
		{
			row = new Row(r, key);
			ht.put(key, row);
		}
	}
	
	private void putReversed(Hashtable ht, Receipt r)
	{
		String key = this.getRevKey(r);
		Row row = (Row) ht.get(key);
		if (row == null)
		{
			row = new Row(r, key);
			ht.put(key, row);
		}
	}
	
	private void putPosition(Hashtable ht, Object[] p)
	{
		String key = null;
		
		if (p[0].equals(new Integer(ProductGroup.TYPE_INCOME)))
		{
			key = this.getIncomeKey(p[1]);
		}
		else if (p[0].equals(new Integer(ProductGroup.TYPE_NOT_INCOME)))
		{
			key = this.getOtherSalesKey(p[1]);
		}
		else if (p[0].equals(new Integer(ProductGroup.TYPE_EXPENSE)))
		{
			key = this.getExpenseKey(p[1]);
		}
		else if (p[0].equals(new Integer(ProductGroup.TYPE_INPUT)))
		{
			key = this.getInputKey(p[1]);
		}
		else if (p[0].equals(new Integer(ProductGroup.TYPE_WITHDRAW)))
		{
			key = this.getWithdrawKey(p[1]);
		}
		
		Row row = (Row) ht.get(key);
		if (row == null)
		{
			row = new Row(p, key);
			ht.put(key, row);
		}
		else
		{
			row.updatePosition(p);
			ht.put(key, row);
		}
		
		if (!p[0].equals(new Integer(ProductGroup.TYPE_INPUT)) && !p[0].equals(new Integer(ProductGroup.TYPE_WITHDRAW)))
		{
			key = this.getTaxKey(p[4]);
			row = (Row) ht.get(key);
			if (row == null)
			{
				row = new Row(p, key);
				ht.put(key, row);
			}
			else
			{
				row.updatePosition(p);
				ht.put(key, row);
			}
		}
	}
	
	private String getIncomeKey(Object id)
	{
		return "a." + id.toString(); //$NON-NLS-1$
	}
	
	private String getOtherSalesKey(Object id)
	{
		return "b." + id.toString(); //$NON-NLS-1$
	}
	
	private String getExpenseKey(Object id)
	{
		return "e." + id.toString(); //$NON-NLS-1$
	}
	
	private String getInputKey(Object id)
	{
		return "u." + id.toString(); //$NON-NLS-1$
	}
	
	private String getWithdrawKey(Object id)
	{
		return "u." + id.toString(); //$NON-NLS-1$
	}
	
	private String getTaxKey(Object id)
	{
		return "t." + id.toString(); //$NON-NLS-1$
	}
	
	private String getIncomeKey(Position p)
	{
		return "a." + p.getProductGroup().getId().toString(); //$NON-NLS-1$
	}
	
	private String getOtherSalesKey(Position p)
	{
		return "b." + p.getProductGroup().getId().toString(); //$NON-NLS-1$
	}
	
	private String getExpenseKey(Position p)
	{
		return "e." + p.getProductGroup().getId().toString(); //$NON-NLS-1$
	}
	
	private String getInputKey(Position p)
	{
		return "u." + p.getProductGroup().getId().toString(); //$NON-NLS-1$
	}
	
	private String getWithdrawKey(Position p)
	{
		return "u." + p.getProductGroup().getId().toString(); //$NON-NLS-1$
	}
	
	private String getTaxKey(Position p)
	{
		return "t." + p.getCurrentTax().getId().toString(); //$NON-NLS-1$
	}
	
	private String getPayKey(Object[] p)
	{
		return this.getPaymentTypePreposition() + p[0].toString();
	}
	
	private String getBackKey(Object[] p)
	{
		return this.getPaymentTypePreposition() + "-" + p[0].toString();
	}
	
	private String getCurKey(Object[] p)
	{
		return this.getCurrencyPreposition() + p[2].toString();
	}
	
	private String getBackCurKey(Object[] p)
	{
		return this.getCurrencyPreposition() + "-" + p[2].toString();
	}
	
	private String getPayKey(Payment p)
	{
		return this.getPaymentTypePreposition() + p.getPaymentTypeId().toString();
	}
	
	private String getBackKey(Payment p)
	{
		return this.getPaymentTypePreposition() + "-" + p.getPaymentTypeId().toString();
	}
	
	private String getCurKey(Payment p)
	{
		return this.getCurrencyPreposition() + p.getForeignCurrencyId().toString();
	}
	
	private String getBackCurKey(Payment p)
	{
		return this.getCurrencyPreposition() + "-" + p.getForeignCurrencyId().toString();
	}
	
	private String getRevKey(Object[] r)
	{
		return "z." + (String) r[0]; //$NON-NLS-1$
	}
	
	private String getRevKey(Receipt r)
	{
		return "z." + r.getNumber(); //$NON-NLS-1$
	}
	
	private String getPaymentTypePreposition()
	{
		return "p."; //$NON-NLS-1$
	}
	
	private String getCurrencyPreposition()
	{
		return "q."; //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.pos.statistics.data.Statistics#getParameters()
	 */
	protected Hashtable getMoreParameters(Hashtable ht)
	{
		String text = null;
		if (this.settlementComposite.getCurrentControl() instanceof SettlementNumberComposite)
		{
			text = "Abschluss: " + this.settlementComposite.getSelectedSettlement().toString();
		}
		else
		{
			text = "";
		}
		ht.put("settlements", text);
		ht.put("receiptCount", "Anzahl Belege: " + this.count);
		return ht;
	}
	
	public boolean getPrintReversedReceipts()
	{
		return this.printReversedReceipts;
	}
	
	private class Row extends HashMap implements Comparable
	{
		public static final long serialVersionUID = 0l;
		
		public Row(Object[] p, String key)
		{
			if (key.startsWith("a.")) { //$NON-NLS-1$
				this.putProductGroup(p);
			}
			if (key.startsWith("b.")) { //$NON-NLS-1$
				this.putOtherSales(p);
			}
			else if (key.startsWith("e.")) { //$NON-NLS-1$
				this.putExpense(p);
			}
			else if (key.startsWith("u.")) { //$NON-NLS-1$
				this.putWithdraw(p);
			}
			else if (key.startsWith("t.")) { //$NON-NLS-1$
				this.putTax(p);
			}
			else if (key.startsWith("p.")) { //$NON-NLS-1$
				if (key.startsWith("p.-"))
					this.putPaymentTypeBack(p);
				else
					this.putPaymentType(p);
			}
			else if (key.startsWith("q.")) { //$NON-NLS-1$
				if (key.startsWith("q.-"))
					this.putForeignCurrencyBack(p);
				else
					this.putForeignCurrency(p);
			}
			else if (key.startsWith("z.")) { //$NON-NLS-1$
				this.putReversed(p);
			}
		}
		
		public Row(Position p, String key)
		{
			if (key.startsWith("a.")) { //$NON-NLS-1$
				this.putProductGroup(p);
			}
			if (key.startsWith("b.")) { //$NON-NLS-1$
				this.putOtherSales(p);
			}
			else if (key.startsWith("e.")) { //$NON-NLS-1$
				this.putExpense(p);
			}
			else if (key.startsWith("u.")) { //$NON-NLS-1$
				this.putWithdraw(p);
			}
			else if (key.startsWith("t.")) { //$NON-NLS-1$
				this.putTax(p);
			}
		}
		
		public Row(Payment p, String key)
		{
			if (key.startsWith("p.")) { //$NON-NLS-1$
				if (key.startsWith("p.-"))
				{
					this.putPaymentTypeBack(p);
				}
				else
				{
					this.putPaymentType(p);
				}
			}
			else if (key.startsWith("q.")) { //$NON-NLS-1$
				if (key.startsWith("q.-"))
				{
					this.putForeignCurrencyBack(p);
				}
				else
				{
					this.putForeignCurrency(p);
				}
			}
		}
		
		public Row(Receipt r, String key)
		{
			if (key.startsWith("z.")) { //$NON-NLS-1$
				this.putReversed(r);
			}
		}
		
		public void updatePosition(Object[] p)
		{
			int qty = ((Integer) p[6]).intValue();
			double am = ((Double) p[7]).doubleValue();
			double tx = ((Double) p[5]).doubleValue();
			
			Integer quantity = (Integer) this.get("quantity"); //$NON-NLS-1$
			quantity = new Integer(quantity.intValue() + qty);
			this.put("quantity", quantity); //$NON-NLS-1$
			
			Double amount = (Double) this.get("amount1"); //$NON-NLS-1$
			amount = new Double(NumberUtility.round(amount.doubleValue() + am, ForeignCurrency.getDefaultCurrency()
							.getCurrency().getDefaultFractionDigits()));
			this.put("amount1", amount); //$NON-NLS-1$
			
			Double taxAmount = (Double) this.get("amount2"); //$NON-NLS-1$
			taxAmount = new Double(NumberUtility.round(taxAmount.doubleValue() + tx, ForeignCurrency
							.getDefaultCurrency().getCurrency().getDefaultFractionDigits()));
			this.put("amount2", taxAmount); //$NON-NLS-1$
		}
		
		public void update(Position p)
		{
			Integer quantity = (Integer) this.get("quantity"); //$NON-NLS-1$
			quantity = new Integer(quantity.intValue() + p.getQuantity());
			this.put("quantity", quantity); //$NON-NLS-1$
			
			Double amount = (Double) this.get("amount1"); //$NON-NLS-1$
			amount = new Double(NumberUtility.round(amount.doubleValue() + p.getAmount(), ForeignCurrency
							.getDefaultCurrency().getCurrency().getDefaultFractionDigits()));
			this.put("amount1", amount); //$NON-NLS-1$
			
			Double taxAmount = (Double) this.get("amount2"); //$NON-NLS-1$
			// taxAmount = NumberUtility.round(new
			// Double(taxAmount.doubleValue() + p.getTaxAmount().doubleValue()),
			// ForeignCurrency
			// .getDefaultCurrency().getCurrency().getDefaultFractionDigits());
			taxAmount = new Double(taxAmount.doubleValue() - p.getTaxAmount());
			this.put("amount2", taxAmount); //$NON-NLS-1$
		}
		
		public void updatePayment(Object[] p)
		{
			Long ptId = (Long) p[0];
			Long fcId = (Long) p[2];
			double amount1 = ((Double) p[4]).doubleValue();
			double amount2 = ((Double) p[5]).doubleValue();
			PaymentType type = PaymentType.selectById(ptId);
			ForeignCurrency fc = ForeignCurrency.selectById(fcId);
			
			Integer quantity = (Integer) this.get("quantity"); //$NON-NLS-1$
			Integer qt = (Integer) p[6];
			quantity = new Integer(quantity.intValue() + qt.intValue());
			this.put("quantity", quantity); //$NON-NLS-1$
			
			if (!type.getForeignCurrencyId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				Double fcAmount = (Double) this.get("amount1"); //$NON-NLS-1$
				fcAmount = new Double(NumberUtility.round(fcAmount.doubleValue() + amount2, fc.getCurrency()
								.getDefaultFractionDigits()));
				this.put("amount1", fcAmount); //$NON-NLS-1$
			}
			Double amount = (Double) this.get("amount2"); //$NON-NLS-1$
			amount = new Double(NumberUtility.round(amount.doubleValue() + amount1, ForeignCurrency
							.getDefaultCurrency().getCurrency().getDefaultFractionDigits()));
			this.put("amount2", amount); //$NON-NLS-1$
		}
		
		public void update(Payment p)
		{
			Integer quantity = (Integer) this.get("quantity"); //$NON-NLS-1$
			quantity = new Integer(quantity.intValue() + 1);
			this.put("quantity", quantity); //$NON-NLS-1$
			
			if (!p.getPaymentType().getForeignCurrencyId().equals(ForeignCurrency.getDefaultCurrency().getId()))
			{
				Double fcAmount = (Double) this.get("amount1"); //$NON-NLS-1$
				fcAmount = new Double(NumberUtility.round(fcAmount.doubleValue() + p.getAmountFC(), p
								.getForeignCurrency().getCurrency().getDefaultFractionDigits()));
				this.put("amount1", fcAmount); //$NON-NLS-1$
			}
			Double amount = (Double) this.get("amount2"); //$NON-NLS-1$
			amount = new Double(NumberUtility.round(amount.doubleValue() + p.getAmount(), ForeignCurrency
							.getDefaultCurrency().getCurrency().getDefaultFractionDigits()));
			this.put("amount2", amount); //$NON-NLS-1$
		}
		
		private void putProductGroup(Object[] p)
		{
			this.put("summaryKey", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "a"); //$NON-NLS-1$ //$NON-NLS-2$
			String galileoId = (String) p[2];
			String name = (String) p[3];
			if (galileoId.length() > 0)
			{
				this.put("text", galileoId + " " + name); //$NON-NLS-1$
			}
			else
			{
				this.put("text", name); //$NON-NLS-1$
			}
			this.put("quantity", p[6]); //$NON-NLS-1$
			Double amount = (Double) p[7];
			Double tax = (Double) p[5];
			this.put("amount1", amount); //$NON-NLS-1$
			this.put("amount2", tax); //$NON-NLS-1$
		}
		
		private void putOtherSales(Object[] p)
		{
			this.put("summaryKey", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "b"); //$NON-NLS-1$ //$NON-NLS-2$
			String name = (String) p[3];
			this.put("text", name); //$NON-NLS-1$
			this.put("quantity", p[6]); //$NON-NLS-1$
			Double amount = (Double) p[7];
			Double tax = (Double) p[5];
			this.put("amount1", amount); //$NON-NLS-1$
			this.put("amount2", tax); //$NON-NLS-1$
		}
		
		private void putExpense(Object[] p)
		{
			this.put("summaryKey", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "e"); //$NON-NLS-1$ //$NON-NLS-2$
			String name = (String) p[3];
			this.put("text", name); //$NON-NLS-1$
			this.put("quantity", p[6]); //$NON-NLS-1$
			Double amount = (Double) p[7];
			Double tax = (Double) p[5];
			this.put("amount1", amount); //$NON-NLS-1$
			this.put("amount2", tax); //$NON-NLS-1$
		}
		
		private void putWithdraw(Object[] p)
		{
			this.put("summaryKey", "4"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "u"); //$NON-NLS-1$ //$NON-NLS-2$
			String name = (String) p[3];
			this.put("text", name); //$NON-NLS-1$
			int quantity = ((Integer) p[6]).intValue();
			double amountFC = ((Double) p[8]).doubleValue();
			Double amount = (Double) p[7];
			if (quantity < 0) amountFC = -Math.abs(amountFC);
			this.put("quantity", new Integer(Math.abs(quantity))); //$NON-NLS-1$
			this.put("amount1", amount); //$NON-NLS-1$
			this.put("amount2", new Double(amountFC)); //$NON-NLS-1$
		}
		
		private void putTax(Object[] p)
		{
			Long taxId = (Long) p[4];
			Double amount = (Double) p[7];
			Double taxAmount = (Double) p[5];
			CurrentTax tax = CurrentTax.selectById(taxId);
			this.put("summaryKey", "3"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "t"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", tax.getTax().getTaxType().name + " " + tax.percentage + "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			this.put("quantity", p[6]); //$NON-NLS-1$
			this.put("amount1", amount); //$NON-NLS-1$
			this.put("amount2", taxAmount); //$NON-NLS-1$
		}
		
		private void putProductGroup(Position p)
		{
			this.put("summaryKey", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "a"); //$NON-NLS-1$ //$NON-NLS-2$
			if (p.getProductGroup().galileoId != null && p.getProductGroup().galileoId.length() > 0)
			{
				this.put("text", p.getProductGroup().galileoId + " " + p.getProductGroup().name); //$NON-NLS-1$
			}
			else
			{
				this.put("text", p.getProductGroup().name); //$NON-NLS-1$
			}
			this.put("quantity", new Integer(p.getQuantity())); //$NON-NLS-1$
			this.put("amount1", new Double(p.getAmount())); //$NON-NLS-1$
			this.put("amount2", new Double(-p.getTaxAmount())); //$NON-NLS-1$
		}
		
		private void putOtherSales(Position p)
		{
			this.put("summaryKey", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "b"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", p.getProductGroup().name); //$NON-NLS-1$
			this.put("quantity", new Integer(p.getQuantity())); //$NON-NLS-1$
			this.put("amount1", new Double(p.getAmount())); //$NON-NLS-1$
			this.put("amount2", new Double(-p.getTaxAmount())); //$NON-NLS-1$
		}
		
		private void putExpense(Position p)
		{
			this.put("summaryKey", "1"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "e"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", p.getProductGroup().name); //$NON-NLS-1$
			this.put("quantity", new Integer(p.getQuantity())); //$NON-NLS-1$
			this.put("amount1", new Double(p.getAmount())); //$NON-NLS-1$
			this.put("amount2", new Double(-p.getTaxAmount())); //$NON-NLS-1$
		}
		
		private void putWithdraw(Position p)
		{
			this.put("summaryKey", "4"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "u"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", p.getProductGroup().name); //$NON-NLS-1$
			this.put("quantity", new Double(p.getQuantity())); //$NON-NLS-1$
			this.put("amount1", new Double(p.getAmount())); //$NON-NLS-1$
			this.put("amount2", new Double(p.amountFC)); //$NON-NLS-1$
		}
		
		private void putTax(Position p)
		{
			this.put("summaryKey", "3"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "t"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", p.getCurrentTax().getTax().getTaxType().name + " " + p.getCurrentTax().percentage + "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			this.put("quantity", new Integer(1)); //$NON-NLS-1$
			this.put("amount1", new Double(p.getAmount())); //$NON-NLS-1$
			this.put("amount2", new Double(-p.getTaxAmount())); //$NON-NLS-1$
		}
		
		private void putPaymentType(Object[] p)
		{
			String name = (String) p[1];
			Double amount = (Double) p[4];
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "p"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", name); //$NON-NLS-1$
			this.put("quantity", p[6]); //$NON-NLS-1$
			this.put("amount1", null); //$NON-NLS-1$
			this.put("amount2", amount); //$NON-NLS-1$
		}
		
		private void putForeignCurrency(Object[] p)
		{
			Long fcId = (Long) p[2];
			Double amount1 = (Double) p[4];
			Double amount2 = (Double) p[5];
			
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "q"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", ForeignCurrency.selectById(fcId).name); //$NON-NLS-1$
			this.put("quantity", new Integer(1)); //$NON-NLS-1$
			this.put("amount1", amount2); //$NON-NLS-1$
			this.put("amount2", amount1); //$NON-NLS-1$
		}
		
		private void putPaymentTypeBack(Object[] p)
		{
			Long ptId = (Long) p[0];
			PaymentType type = PaymentType.selectById(ptId);
			
			String text = null;
			if (type.voucher)
				text = "Rückgutscheine " + type.code;
			else
				text = "Rückgeld " + type.code;
			
			Double amount = (Double) p[4];
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "p"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", text); //$NON-NLS-1$
			this.put("quantity", p[6]); //$NON-NLS-1$
			this.put("amount1", null); //$NON-NLS-1$
			this.put("amount2", amount); //$NON-NLS-1$
		}
		
		private void putForeignCurrencyBack(Object[] p)
		{
			Long fcId = (Long) p[2];
			Double amount1 = (Double) p[4];
			Double amount2 = (Double) p[5];
			
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "q"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", "Rückgeld " + ForeignCurrency.selectById(fcId).name); //$NON-NLS-1$
			this.put("quantity", new Integer(1)); //$NON-NLS-1$
			this.put("amount1", amount2); //$NON-NLS-1$
			this.put("amount2", amount1); //$NON-NLS-1$
		}
		
		private void putPaymentType(Payment p)
		{
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "p"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", p.getPaymentType().code); //$NON-NLS-1$
			this.put("quantity", new Integer(1)); //$NON-NLS-1$
			this.put("amount1", null); //$NON-NLS-1$
			this.put("amount2", new Double(p.getAmount())); //$NON-NLS-1$
		}
		
		private void putPaymentTypeBack(Payment p)
		{
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "p"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", "Rückgeld " + p.getPaymentType().code); //$NON-NLS-1$
			this.put("quantity", new Integer(1)); //$NON-NLS-1$
			this.put("amount1", null); //$NON-NLS-1$
			this.put("amount2", new Double(p.getAmount())); //$NON-NLS-1$
		}
		
		private void putForeignCurrency(Payment p)
		{
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "q"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", p.getForeignCurrency().code); //$NON-NLS-1$
			this.put("quantity", new Integer(1)); //$NON-NLS-1$
			this.put("amount1", new Double(p.getAmountFC())); //$NON-NLS-1$
			this.put("amount2", new Double(p.getAmount())); //$NON-NLS-1$
		}
		
		private void putForeignCurrencyBack(Payment p)
		{
			this.put("summaryKey", "2"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "q"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", "Rückgeld " + p.getForeignCurrency().code); //$NON-NLS-1$
			this.put("quantity", new Integer(1)); //$NON-NLS-1$
			this.put("amount1", new Double(p.getAmountFC())); //$NON-NLS-1$
			this.put("amount2", new Double(p.getAmount())); //$NON-NLS-1$
		}
		
		private void putReversed(Object[] r)
		{
			String number = (String) r[0];
			Timestamp timestamp = (Timestamp) r[1];
			Double amount = (Double) r[2];
			
			DateFormat df = DateFormat.getDateTimeInstance();
			this.put("summaryKey", "5"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "z"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", number + " " + df.format(timestamp)); //$NON-NLS-1$
			this.put("quantity", null); //$NON-NLS-1$
			this.put("amount1", amount); //$NON-NLS-1$
			this.put("amount2", null); //$NON-NLS-1$
		}
		
		private void putReversed(Receipt r)
		{
			DateFormat df = DateFormat.getDateTimeInstance();
			this.put("summaryKey", "5"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("key", "z"); //$NON-NLS-1$ //$NON-NLS-2$
			this.put("text", r.getNumber() + " " + df.format(r.timestamp)); //$NON-NLS-1$
			this.put("quantity", null); //$NON-NLS-1$
			this.put("amount1", new Double(r.getAmount())); //$NON-NLS-1$
			this.put("amount2", null); //$NON-NLS-1$
		}
		
		public int compareTo(Object other)
		{
			Row row = (Row) other;
			int result = ((String) this.get("key")).compareTo(((String) row.get("key"))); //$NON-NLS-1$ //$NON-NLS-2$
			if (result == 0)
			{
				return ((String) this.get("text")).compareTo(((String) row.get("text"))); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				return result;
			}
		}
	}
	
	public long getReceiptCount()
	{
		return this.count;
	}
	
	private SettlementComposite settlementComposite;
	private boolean printReversedReceipts = false;
	double betrag = 0d;
	private long count = 0;
	private Hashtable rows;
	// private JRDataSource dataSource;
}
