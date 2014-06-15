/*
 *   The Flexible Modelling Framework is a Social Science application for 
 *   synthesising individual level populations
 *   Copyright (C) 2013  Kirk Harland
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contact email: k.harland@leeds.ac.uk
 */

package MSAccess;

import java.awt.event.*;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
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
