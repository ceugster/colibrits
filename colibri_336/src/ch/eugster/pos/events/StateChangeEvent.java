/*
 * Created on 30.07.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.pos.events;

import ch.eugster.pos.db.Connection;
import ch.eugster.pos.db.Database;
import ch.eugster.pos.product.Code128;
import ch.eugster.pos.product.ProductServer;
import ch.eugster.pos.util.Config;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StateChangeEvent
{
	public StateChangeEvent(int type)
	{
		this.type = type;
		
		switch (this.type)
		{
			case STANDARD_DATABASE:
			{
				this.setStates(Database.getStandard());
				break;
			}
			case TEMPORARY_DATABASE:
			{
				this.setStates(Database.getTemporary());
				break;
			}
			case TUTORIAL_DATABASE:
			{
				this.setStates(Database.getTutorial());
				break;
			}
			case CODE128:
			{
				this.states[0] = Code128.isAnyUsed() ? 2 : 0;
				this.states[1] = StateChangeEvent.OK;
				break;
			}
			case TITLE:
			{
				if (Code128.isAnyUsed())
				{
					this.states[0] = Code128.isGalileoSearched() ? 2 : 0;
					if (ProductServer.isUsed() && ProductServer.getInstance().isActive())
					{
						this.states[1] = StateChangeEvent.OK;
					}
					else
					{
						if (Code128.isGalileoSearched())
						{
							this.states[1] = StateChangeEvent.ERROR;
						}
						else
						{
							this.states[1] = StateChangeEvent.OK;
						}
					}
				}
				else
				{
					this.states[0] = 0;
					this.states[1] = StateChangeEvent.OK;
				}
				break;
			}
			case GALILEO:
			{
				if (!ProductServer.isUsed())
				{
					this.states[0] = StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.OK;
				}
				else if (ProductServer.getInstance().isActive())
				{
					this.states[0] = StateChangeEvent.RUNNING;
					this.states[1] = StateChangeEvent.OK;
				}
				else
				{
					this.states[0] = StateChangeEvent.RUNNING;
					this.states[1] = StateChangeEvent.ERROR;
				}
				break;
			}
			case CD:
			{
				boolean readCd = ProductServer.getInstance().isReadCd();
				if (!ProductServer.isUsed())
				{
					this.states[0] = StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.OK;
				}
				else if (ProductServer.getInstance().isActive())
				{
					this.states[0] = readCd ? StateChangeEvent.RUNNING : StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.OK;
				}
				else
				{
					this.states[0] = readCd ? StateChangeEvent.RUNNING : StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.ERROR;
				}
				break;
			}
			case STOCK:
			{
				if (!ProductServer.isUsed() || ProductServer.getInstance() == null)
				{
					this.states[0] = StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.OK;
				}
				else if (ProductServer.getInstance().getUpdate() > 0)
				{
					if (ProductServer.getInstance().isActive())
					{
						this.states[0] = Config.getInstance().getGalileoUpdate();
						this.states[1] = StateChangeEvent.OK;
					}
					else
					{
						this.states[0] = Config.getInstance().getGalileoUpdate();
						this.states[1] = StateChangeEvent.ERROR;
					}
				}
				else
				{
					this.states[0] = StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.OK;
				}
				break;
			}
			case SCANPANEL:
			{
				if (!ProductServer.isUsed() || ProductServer.getInstance() == null)
				{
					this.states[0] = StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.OK;
				}
				else if (ProductServer.getInstance().getUpdate() == 1)
				{
					if (ProductServer.getInstance().isActive())
					{
						this.states[0] = 2;
						this.states[1] = 0;
					}
					else
					{
						this.states[0] = 2;
						this.states[1] = StateChangeEvent.ERROR;
					}
				}
				else
				{
					this.states[0] = StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.OK;
				}
				break;
			}
		}
	}
	
	private void setStates(Connection connection)
	{
		if (connection != null)
		{
			if (connection.isActive())
			{
				if (connection.isConnected())
				{
					if (Database.getCurrent().equals(connection))
					{
						this.states[0] = StateChangeEvent.RUNNING;
						this.states[1] = StateChangeEvent.OK;
					}
					else
					{
						this.states[0] = StateChangeEvent.STANDBY;
						this.states[1] = StateChangeEvent.OK;
					}
				}
				else
				{
					this.states[0] = StateChangeEvent.DOWN;
					this.states[1] = StateChangeEvent.ERROR;
				}
			}
			else
			{
				this.states[0] = StateChangeEvent.DOWN;
				this.states[1] = StateChangeEvent.OK;
			}
		}
		else
		{
			this.states[0] = StateChangeEvent.DOWN;
			this.states[1] = StateChangeEvent.ERROR;
		}
		// if (Database.getCurrent().equals(connection)) {
		// states[0] = RUNNING;
		// states[1] = OK;
		// }
		//		
		// else if (connection.isConnected()) {
		// states[0] = STANDBY;
		// states[1] = OK;
		// }
		// else if (!connection.isActive()) {
		// if (connection.equals(Database.getStandard())) {
		// states[0] = STANDBY;
		// states[1] = ERROR;
		// }
		// else {
		// states[0] = DOWN;
		// states[1] = OK;
		// }
		// }
		// else {
		// }
	}
	
	public int type;
	public int[] states = new int[2];
	
	// Types
	public static final int STANDARD_DATABASE = 0;
	public static final int TEMPORARY_DATABASE = 1;
	public static final int TUTORIAL_DATABASE = 2;
	public static final int CODE128 = 3;
	public static final int TITLE = 4;
	public static final int GALILEO = 5;
	public static final int CD = 6;
	public static final int SCANPANEL = 7;
	public static final int STOCK = 8;
	
	public static final int DOWN = 0;
	public static final int STANDBY = 1;
	public static final int RUNNING = 2;
	
	public static final int OK = 0;
	public static final int ERROR = 1;
	
	public static final int NO_CHANGE = -1;
}
