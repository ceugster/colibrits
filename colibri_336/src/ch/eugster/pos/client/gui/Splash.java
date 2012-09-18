/*
 * Created on 12.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ch.eugster.pos.Messages;
import ch.eugster.pos.client.App;
import ch.eugster.pos.db.Version;
import ch.eugster.pos.events.DatabaseEvent;
import ch.eugster.pos.events.DatabaseListener;
import ch.eugster.pos.events.InitializationListener;
import ch.eugster.pos.events.MessageDialog;
import ch.eugster.pos.util.Path;

/**
 * @author Administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Splash extends JFrame implements DatabaseListener, InitializationListener
{
	private static final long serialVersionUID = 0l;
	
	private static int initializationSteps = 30;
	File file = null;
	private static Properties props = null;
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public Splash(int max) throws HeadlessException
	{
		super();
		this.init(max);
	}
	
	/**
	 * @param gc
	 */
	public Splash(int max, GraphicsConfiguration gc)
	{
		super(gc);
		this.init(max);
	}
	
	/**
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public Splash(int max, String title) throws HeadlessException
	{
		super(title);
		this.init(max);
	}
	
	/**
	 * @param title
	 * @param gc
	 */
	public Splash(int max, String title, GraphicsConfiguration gc)
	{
		super(title, gc);
		this.init(max);
	}
	
	private void init(int max)
	{
		Splash.splash = this;
		
		Splash.props = new Properties();
		this.file = new File(Path.getInstance().cfgDir + "splash.ini");
		try
		{
			InputStream in = new FileInputStream(this.file);
			Splash.props.load(in);
			in.close();
		}
		catch (Exception e)
		{
		}
		if (Splash.props.getProperty("initialization-steps") == null)
		{
			Splash.props.setProperty("initialization-steps", "30");
		}
		try
		{
			Splash.initializationSteps = new Integer(Splash.props.getProperty("initialization-steps")).intValue();
		}
		catch (NumberFormatException e)
		{
			Splash.initializationSteps = 30;
		}
		
		this.getContentPane().setLayout(new BorderLayout(10, 10));
		this.getContentPane().setBackground(Color.WHITE);
		
		JLabel dummyWest = new JLabel();
		dummyWest.setBackground(Color.WHITE);
		this.getContentPane().add(dummyWest, BorderLayout.WEST);
		
		JLabel dummyNorth = new JLabel();
		dummyNorth.setBackground(Color.WHITE);
		this.getContentPane().add(dummyNorth, BorderLayout.NORTH);
		
		JPanel dummySouth = new JPanel(new BorderLayout());
		dummySouth.setBackground(Color.WHITE);
		this.getContentPane().add(dummySouth, BorderLayout.SOUTH);
		
		this.message = new JLabel(""); //$NON-NLS-1$
		this.message.setFont(this.message.getFont().deriveFont(0, 10f));
		this.message.setBackground(Color.WHITE);
		dummySouth.add(this.message, BorderLayout.NORTH);
		
		DefaultBoundedRangeModel brm = new DefaultBoundedRangeModel(0, 1, 0, max);
		this.progressBar = new JProgressBar(brm);
		this.progressBar.setBackground(Color.WHITE);
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(Splash.initializationSteps);
		this.progressBar.setValue(0);
		dummySouth.add(this.progressBar, BorderLayout.SOUTH);
		
		ImageIcon i = new ImageIcon(Messages.getString("Splash.icons__splash.gif_2")); //$NON-NLS-1$
		// ImageIcon i = new
		// ImageIcon(App.iconDir.concat(File.separator.concat("splash.gif")));
		JLabel imageLabel = new JLabel(i);
		this.getContentPane().add(imageLabel, BorderLayout.EAST);
		
		JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
		infoPanel.setBackground(Color.WHITE);
		this.getContentPane().add(infoPanel, BorderLayout.CENTER);
		
		JPanel titelPanel = new JPanel(new BorderLayout(10, 10));
		titelPanel.setBackground(Color.WHITE);
		infoPanel.add(titelPanel, BorderLayout.NORTH);
		
		JPanel textPanel = new JPanel(new BorderLayout(10, 10));
		textPanel.setBackground(Color.WHITE);
		infoPanel.add(textPanel, BorderLayout.CENTER);
		
		JPanel textPanelNorth = new JPanel(new BorderLayout(10, 10));
		textPanelNorth.setBackground(Color.WHITE);
		textPanel.add(textPanelNorth, BorderLayout.NORTH);
		
		JPanel textPanelCenter = new JPanel(new BorderLayout(10, 10));
		textPanelCenter.setBackground(Color.WHITE);
		textPanel.add(textPanelCenter, BorderLayout.CENTER);
		
		JPanel versionPanel = new JPanel(new BorderLayout(10, 10));
		versionPanel.setBackground(Color.WHITE);
		infoPanel.add(versionPanel, BorderLayout.SOUTH);
		
		JLabel titleLabel = new JLabel(Messages.getString("Splash.ColibriTS_3")); //$NON-NLS-1$
		titleLabel.setFont(new Font(Messages.getString("Splash.Times_New_Roman_4"), 2, 22)); //$NON-NLS-1$
		titleLabel.setBackground(Color.white);
		titelPanel.add(titleLabel, BorderLayout.CENTER);
		
		JLabel textLabel1 = new JLabel(Messages.getString("Splash.Kassenprogramm_5")); //$NON-NLS-1$
		textLabel1.setFont(new Font(Messages.getString("Splash.Times_New_Roman_6"), 2, 14)); //$NON-NLS-1$
		textLabel1.setBackground(Color.white);
		textPanelNorth.add(textLabel1, BorderLayout.NORTH);
		
		JLabel textLabel2 = new JLabel(Messages.getString("Splash.f_u00FCr_den_7")); //$NON-NLS-1$
		textLabel2.setFont(new Font(Messages.getString("Splash.Times_New_Roman_8"), 2, 14)); //$NON-NLS-1$
		textLabel2.setBackground(Color.white);
		textPanelNorth.add(textLabel2, BorderLayout.CENTER);
		
		JLabel textLabel3 = new JLabel(Messages.getString("Splash.Schweizer_Buchhandel_9")); //$NON-NLS-1$
		textLabel3.setFont(new Font(Messages.getString("Splash.Times_New_Roman_10"), 2, 14)); //$NON-NLS-1$
		textLabel3.setBackground(Color.white);
		textPanelNorth.add(textLabel3, BorderLayout.SOUTH);
		
		JLabel versionLabel = new JLabel(Messages.getString("Splash.Version__11") + Version.version() + ". Datenversion: " + Version.getMyDataVersion()); //$NON-NLS-1$
		versionLabel.setFont(new Font(Messages.getString("Splash.Times_New_Roman_12"), 2, 12)); //$NON-NLS-1$
		versionLabel.setBackground(Color.white);
		versionPanel.add(versionLabel, BorderLayout.NORTH);
		
		JLabel dateLabel = new JLabel("Datum: " + Version.getVersionDate()); //$NON-NLS-1$
		dateLabel.setFont(new Font(Messages.getString("Splash.Times_New_Roman_12"), 2, 12)); //$NON-NLS-1$
		dateLabel.setBackground(Color.white);
		versionPanel.add(dateLabel, BorderLayout.SOUTH);
	}
	
	public void databaseErrorOccured(DatabaseEvent e)
	{
		switch (e.type)
		{
			case 0:
				MessageDialog.showInformation(this, e.title, e.text, 0);
				break;
			case 1:
				this.setTitle(App.getApp().getTitle());
				MessageDialog.showInformation(this, e.title, e.text, 0);
				break;
			case 2:
				this.closeApplication();
		}
	}
	
	public void initialized(int value, String text)
	{
		this.message.setText(text);
		Splash.initializationSteps = this.progressBar.getValue();
		if (Splash.initializationSteps == this.progressBar.getMaximum())
		{
			this.progressBar.setMaximum(Splash.initializationSteps + 1);
		}
		this.progressBar.setValue(++Splash.initializationSteps);
	}
	
	public void initialized(String text)
	{
		this.message.setText(text);
		Splash.initializationSteps = this.progressBar.getValue();
		if (Splash.initializationSteps == this.progressBar.getMaximum())
		{
			this.progressBar.setMaximum(Splash.initializationSteps + 1);
		}
		this.progressBar.setValue(++Splash.initializationSteps);
	}
	
	public void closeApplication()
	{
		App.getApp().dispose();
	}
	
	public static Splash getInstance()
	{
		return Splash.splash;
	}
	
	public void dispose()
	{
		try
		{
			OutputStream out = new FileOutputStream(this.file);
			Splash.props.setProperty("initialization-steps", new Integer(Splash.initializationSteps).toString());
			Splash.props.store(out, "Program Initialization Step Counts");
			out.close();
		}
		catch (Exception e)
		{
		}
		super.dispose();
	}
	
	public static void main(String[] args)
	{
		Splash s = new Splash(0);
		s.setUndecorated(true);
		s.pack();
		s.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - s.getWidth()) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - s.getHeight()) / 2);
		s.setVisible(true);
	}
	
	private static Splash splash = null;
	
	private JLabel message;
	private JProgressBar progressBar;
}
