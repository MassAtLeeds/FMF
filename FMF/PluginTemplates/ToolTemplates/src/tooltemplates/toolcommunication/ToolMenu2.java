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
package tooltemplates.toolcommunication;
 
import java.awt.event.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;


/**
 * This class is a minimum required skeleton of a tool menu for FMF.
 * The same class as ToolMenu1, but for name.
 * @version 1.0
 * @author <A href="http://uk.linkedin.com/pub/kirk-harland/2b/624/5a3">Kirk Harland</A> and <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class ToolMenu2 extends uk.ac.leeds.mass.fmf.shared_objects.Menu{


	/**
	* Constructor just sets up the tool menu name.
	*/
    public ToolMenu2(){
        setMenuName("Inter-tool Comm Template 2");
    }



	
	/**
    * The getItems method needs to be overridden to add items to the menu drop down.
    */
    @Override
    protected Object[][] getItems(){
        
        Object[][] o = new Object[1][6];

        o[0][0] = "Tool 2"; // Menu item title.
        o[0][1] = new ToolMenu2Listener();

        return o;
    }




    /**
	* Here, for simplicity, we use a nested class to listen to the menu option.
	**/
    class ToolMenu2Listener implements ActionListener{

      
		/**
		* Method starts a new thread encapsulating a JPanel that's added to the main window.
		*/
        @Override
        public void actionPerformed(ActionEvent e) {
		
            Thread t = new Thread() {

				/**
				* Just makes a new JPanel extending object and adds it with a title.
				* Calls ApplicationInformation object's getMainPanel to add.
				* @see ApplicationInformation.getMainPanel(String windowTitle, JPanel GUI)
				*/
                @Override
                public void run() {
				
                    ToolPanel2 tp = new ToolPanel2(ai);
                    FMFFrame f = ai.getMainPanel("Tool 2 Window Title", tp);
					
					// Let the new panel know about the frame it is embedded within.
					tp.setEmbeddingFrame(f);
                    
					// Set the frame to null to tidy up thread.
                    f = null;
                }
				
            };

            t.start();
            
        }

    }

}
