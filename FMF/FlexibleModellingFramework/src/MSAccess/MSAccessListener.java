/*
 * This is the new license...
 * It has been edited
 */

package MSAccess;

import java.awt.event.*;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class MSAccessListener implements ActionListener{



    	/**
     	 * Main source of all info on the application.
     	 */
	private IApplicationInformation ai;
	

    private MSAccessListener(){}

    	/**
       * Sets the whole thing rolling.
       * @param a : IApplicationInformation used for getting hold of GUI, files, etc.
      */
	public MSAccessListener (IApplicationInformation a) {
            ai = a;
        }

    
	/**
	 * Creates the GUI.
         * 
         * @param ae : ActionEvent object passed in automatically
         */
	public void actionPerformed (ActionEvent ae) {
            
            ai.getMainPanel("Import Data From Microsoft Access",new MSAccessDataLocator(ai));
            
	}
	
}
