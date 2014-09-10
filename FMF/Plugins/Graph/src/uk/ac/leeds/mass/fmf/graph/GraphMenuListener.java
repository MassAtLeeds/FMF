/*
 * GraphMenuListener.java
 *
 * --Copyright notice-- 
 *
 * Copyright (c) MASS Group. 
 * http://www.geog.leeds.ac.uk/groups/mass/
 * This software is licensed under 'The Artistic License' which can be found at 
 * the Open Source Initiative website at... 
 * http://www.opensource.org/licenses/artistic-license.php
 * Please note that the optional Clause 8 does not apply to this code.
 *
 * The Graph package in its original form is distributed under the 
 * GPL, compatible with this license.
 * 
 * The Standard Version source code, and associated documentation can be found at... 
 * http://www.geog.leeds.ac.uk/groups/mass/
 *
 * --End of Copyright notice-- 
 *
 **/
package uk.ac.leeds.mass.fmf.graph;

import java.awt.event.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;


/**
 * Simple GraphMenuListener.
 * @author Kirk Harland and <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 * @version 1.0
 */
class GraphMenuListener implements ActionListener{


    	/**
     	 * Main source of all info on the application.
     	 */
	private IApplicationInformation ai;
	

    	/**
       * Sets the whole thing rolling.
       * @param a IApplicationInformation used for getting hold of GUI, files, etc.
      */
	GraphMenuListener (IApplicationInformation a) {
            ai = a;
        }

    

            
		@Override
        public void actionPerformed(ActionEvent e) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    GraphGUI gg = new GraphGUI(ai);
                    FMFFrame f = ai.getMainPanel(GraphMenu.getResourceBundle().getString("ScriptingWindow"), gg );

                }
            };

            t.start();
            
        }
			
   
	
}



