/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.client.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ch.eugster.pos.db.CustomKey;
import ch.eugster.pos.db.Tab;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ABlockFunction extends ABlock
{
	
	private static final long serialVersionUID = 0l;
	
	/**
	 * 
	 */
	public ABlockFunction(UserPanel context)
	{
		super(new BorderLayout(), context);
		this.init();
	}
	
	private void init()
	{
		this.setFont(this.getFont().deriveFont(24.0f));
		this.buildFunctionBlock();
	}
	
	public void buildFunctionBlock()
	{
		ch.eugster.pos.db.Block block = ch.eugster.pos.db.Block.getByClass(this.getClass().getName());
		if (block != null)
		{
			Tab[] tabs = Tab.getByBlock(block);
			if (tabs.length == 0)
			{
				return;
			}
			else if (tabs.length == 1)
			{
				JPanel p = new JPanel(new GridLayout(tabs[0].rows, tabs[0].columns));
				this.addButtons(tabs[0], p);
				this.add(p, BorderLayout.CENTER);
			}
			else
			{
				this.tabbedPane = new TabbedPane(this.context);
				this.tabbedPane.setFont(this.getFont().deriveFont(block.fontStyle, (float) block.fontSize));
				for (int i = 0; i < tabs.length; i++)
				{
					JPanel p = new JPanel(new GridLayout(tabs[i].rows, tabs[i].columns));
					this.tabbedPane.addTab(tabs[i].title, p);
					if (tabs[i].defaultTabPosition)
					{
						this.defaultTabPosition = i;
					}
					else if (tabs[i].defaultTabPayment)
					{
						this.defaultTabPayment = i;
					}
					this.addButtons(tabs[i], p);
				}
				this.add(this.tabbedPane, BorderLayout.CENTER);
				this.context.getReceiptModel().addReceiptChangeListener(this);
			}
		}
	}
	
	private void addButtons(Tab tab, JPanel p)
	{
		CustomKey[] keys = CustomKey.getByTab(tab.getId());
		int rows = tab.rows;
		int cols = tab.columns;
		CustomKey[][] keyPlaces = new CustomKey[rows][cols];
		for (int i = 0; i < keys.length; i++)
		{
			int row = keys[i].row;
			int col = keys[i].column;
			if (row < rows && col < cols)
			{
				keyPlaces[row][col] = keys[i];
			}
		}
		for (int i = 0; i < keyPlaces.length; i++)
		{
			for (int j = 0; j < keyPlaces[i].length; j++)
			{
				if (keyPlaces[i][j] == null)
				{
					JPanel dummy = new JPanel();
					dummy.setRequestFocusEnabled(false);
					p.add(dummy);
				}
				else
				{
					CustomKey key = keyPlaces[i][j];
					JComponent comp = key.createButton(this.context, this);
					if (comp == null)
					{
						comp = new JPanel();
					}
					comp.setRequestFocusEnabled(false);
					p.add(comp);
				}
			}
		}
	}
	
}
