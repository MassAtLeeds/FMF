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
 *   Contact email: Andy Evans: a.j.evans@leeds.ac.uk
 */
package uk.ac.leeds.mass.fmf.microsimulation;
 
import java.awt.event.*;
import javax.swing.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;



/**
 * This code joins synthetic populations (Area and microdata ID) back to the microdata so you get one file with everything.
 * This is the tool element. It initiates a DataCombinerPanel, which then starts a DataCombiner Process.
 * @version 1.0
 * @author <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
class DataCombiner implements ActionListener{


	/**
	* Structure for accessing other parts of the application.
	*/
    private IApplicationInformation ai = null;


	
	
	/**
	* Constructor just sets up ai.
	*/
	public DataCombiner(IApplicationInformation ai) {
		this.ai = ai;
	}

      
	  
	  
	/**
	* Method starts a new thread that initialises a DataCombinerPanel.
	*/
	@Override
	public void actionPerformed(ActionEvent e) {
	
		Thread t = new Thread() {

			/**
			* Initialises a DataCombinerPanel.
			*/
			@Override
			public void run() {
				DataCombinerPanel tp = new DataCombinerPanel(ai);
				FMFFrame f = ai.getMainPanel("Data Combiner", tp);
				f = null;
			}
			
		};

		t.start();
		
	}

}


