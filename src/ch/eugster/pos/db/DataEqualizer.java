/*
 * Created on 12.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

import javax.swing.ProgressMonitor;

import ch.eugster.pos.Messages;


/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DataEqualizer {

	public DataEqualizer(boolean withServerConnection) {
		if (withServerConnection) {
			initServer();
		}
		else {
			initLocal();
		}
	}
	
	private void initLocal() {
	}
	
	private void initServer() {
		
	}
	
	public static void main(String[] args) {
		new DataEqualizer(true);
	}
	
	public static void equalize() {
		if (Database.getCurrent() != null && Database.getCurrent().equals(Database.getStandard())) {
			if (Database.getStandard().isConnected() && Database.getTemporary().isConnected()) {
				Connection source = Database.getStandard();
				Connection target = Database.getTemporary();
				int classCount = 18;
				Class[] classes = new Class[classCount];
				classes[0] = Block.class;
				classes[1] = ForeignCurrency.class;
				classes[2] = CurrentTax.class;
				classes[3] = CustomKey.class;
				classes[4] = FixKey.class;
				classes[5] = FixKeyGroup.class;
				classes[6] = Function.class;
				classes[7] = KeyGroup.class;
				classes[8] = Option.class;
				classes[9] = PaymentType.class;
				classes[10] = PaymentTypeGroup.class;
				classes[11] = ProductGroup.class;
				classes[12] = Salespoint.class;
				classes[13] = Tab.class;
				classes[14] = Tax.class;
				classes[15] = TaxRate.class;
				classes[16] = TaxType.class;
				classes[17] = User.class;

				ProgressMonitor monitor = new ProgressMonitor(null, Messages.getString("DataEqualizer.Die_lokale_Datenbank_wird_mit_den_Daten_des_Servers_aktualisiert._Bitte_Warten._1"), //$NON-NLS-1$
									   Messages.getString("DataEqualizer.Die_Daten_der_lokalen_tempor_u00E4ren_Datenbank_werden_mit_den_Daten_der_Standarddatenbank_abgeglichen._Bitte_haben_Sie_etwas_Geduld..._2"), //$NON-NLS-1$
									   0,
									   classCount);

				for (int i = 0; i < classes.length; i++) {
					monitor.setProgress(i);
					Table.equalize(classes[i], source, target);
				}
				
				monitor.close();
			}
		}
	}
	
}
