/*
 * Created on 06.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos;


/**
 * @author ceugster
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Constants {
	
	public static final int OK_ID 						= 0;
	public static final int CANCEL_ID 					= 1;
	public static final int ADD_ID 						= 2;
	public static final int SAVE_ID 					= 3;
	public static final int RESET_ID 					= 4;
	public static final int REMOVE_ID 					= 5;
	public static final int CLOSE_ID 					= 6;
	
	public static final Integer ADD_ITEM 				= new Integer(100);
	public static final Integer REMOVE_ITEM 			= new Integer(101);
	public static final Integer ADD_CHILD 				= new Integer(102);
	public static final Integer REMOVE_CHILDREN 		= new Integer(103);
	public static final Integer REMOVE_SELECTION	 	= new Integer(200);
	public static final Integer ADD_WIZARD				= new Integer(300);
	public static final Integer ADD_CURRENT_TAX_WIZARD 	= new Integer(310);
	
	public static final String ADD_LABEL		 		= Messages.getString("Constants.neu"); //$NON-NLS-1$
	public static final String SAVE_LABEL 				= Messages.getString("Constants.save"); //$NON-NLS-1$
	public static final String RESET_LABEL		 		= Messages.getString("Constants.setback"); //$NON-NLS-1$
	public static final String REMOVE_LABEL 			= Messages.getString("Constants.delete"); //$NON-NLS-1$
	public static final String CLOSE_LABEL 				= Messages.getString("Constants.close"); //$NON-NLS-1$
	public static final String OK_LABEL 				= Messages.getString("Constants.ok"); //$NON-NLS-1$
	public static final String CANCEL_LABEL 			= Messages.getString("Constants.cancel"); //$NON-NLS-1$
	
	public static final int FONT_STYLE_PLAIN 			= 0;
	public static final int FONT_STYLE_BOLD 			= 1;
	public static final int FONT_STYLE_ITALIC 			= 2;
}
