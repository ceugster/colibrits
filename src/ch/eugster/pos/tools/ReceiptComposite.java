/*
 * Created on 16.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.tools;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jdom.Document;
import org.jdom.Element;

import ch.eugster.pos.db.Payment;
import ch.eugster.pos.db.Position;
import ch.eugster.pos.db.Receipt;
import ch.eugster.pos.util.Serializer;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReceiptComposite extends Composite /* implements ControlListener */
{
	
	private Font font;
	
	/**
	 * 
	 */
	public ReceiptComposite(Composite parent, File file, int style)
	{
		super(parent, style);
		this.init(file);
	}
	
	private void init(File file)
	{
		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		try
		{
			Document doc = Serializer.getInstance().readReceipt(file);
			// Document doc = this.buildDocument(file);
			Receipt receipt = this.buildReceipt(doc);
			ReceiptFormatter formatter = ReceiptFormatter.getInstance();
			Text text = new Text(this, SWT.BORDER | SWT.MULTI);
			// text.setLayoutData(new GridData(GridData.FILL_BOTH));
			this.font = new Font(this.getDisplay(), "Courier", 10, SWT.NORMAL);
			text.setFont(this.font);
			text.setText(formatter.getText(receipt));
			text.setEditable(false);
		}
		catch (Exception e)
		{
			this.receiptComposite = new Composite(this, SWT.NONE);
			this.receiptComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.receiptComposite.setLayout(new GridLayout(2, false));
			
			Label label = new Label(this.receiptComposite, SWT.NONE);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			label.setText(e.getLocalizedMessage() == null ? "" : e.getLocalizedMessage());
		}
		
	}
	
	// private Document buildDocument(File file) throws Exception
	// {
	// InputStream in = this.openFile(file);
	// Document document = this.buildDocument(in);
	// this.close(in);
	// return document;
	// }
	
	// private InputStream openFile(File file) throws Exception
	// {
	// InputStream in = null;
	//		
	// if (file.getName().endsWith(".xml.zip"))
	// {
	// InputStream inStream = new FileInputStream(file);
	// in = new GZIPInputStream(inStream);
	// }
	// else
	// {
	// in = new FileInputStream(file);
	// }
	// return in;
	// }
	
	// private void close(InputStream in) throws Exception
	// {
	// in.close();
	// }
	
	// private SAXBuilder getBuilder()
	// {
	// if (this.builder == null)
	// {
	// this.builder = new SAXBuilder();
	// }
	// return this.builder;
	// }
	
	// private Document buildDocument(InputStream in) throws Exception
	// {
	// return this.getBuilder().build(in,
	// Path.getInstance().cfgDir.concat("receipt.dtd"));
	// }
	
	private Receipt buildReceipt(Document document) throws Exception
	{
		Element root = document.getRootElement();
		Receipt receipt = Receipt.getEmptyReceipt();
		receipt = this.setRecordAttributes(receipt, root);
		
		Element positionsList = root.getChild("positions");
		Element[] positions = (Element[]) positionsList.getChildren("position").toArray(new Element[0]);
		for (int i = 0; i < positions.length; i++)
		{
			Position position = Position.getEmptyInstance();
			position = this.setRecordAttributes(receipt, position, positions[i]);
			receipt.addPosition(position);
		}
		
		Element paymentsList = root.getChild("payments");
		Element[] payments = (Element[]) paymentsList.getChildren("payment").toArray(new Element[0]);
		for (int i = 0; i < payments.length; i++)
		{
			Payment payment = Payment.getEmptyInstance();
			payment = this.setRecordAttributes(receipt, payment, payments[i]);
			receipt.addPayment(payment);
		}
		return receipt;
	}
	
	public void dispose()
	{
		if (!this.font.isDisposed())
		{
			this.font.dispose();
		}
	}
	
	private Receipt setRecordAttributes(Receipt receipt, Element element) throws Exception
	{
		receipt.setRecordAttributes(element, true, false);
		return receipt;
	}
	
	private Position setRecordAttributes(Receipt receipt, Position position, Element element) throws Exception
	{
		position.setRecordAttributes(receipt, element, true, false);
		return position;
	}
	
	private Payment setRecordAttributes(Receipt receipt, Payment payment, Element element) throws Exception
	{
		payment.setRecordAttributes(receipt, element, true, false);
		return payment;
	}
	
	// private SAXBuilder builder = null;
	private Composite receiptComposite;
}
