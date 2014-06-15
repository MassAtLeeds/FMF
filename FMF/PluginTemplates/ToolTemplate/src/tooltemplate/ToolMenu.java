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
package tooltemplate;
 
import java.awt.event.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;


/**
 * This class is a minimum required skeleton of a tool menu for FMF.
 * @version 1.0
 * @author <A href="http://uk.linkedin.com/pub/kirk-harland/2b/624/5a3">Kirk Harland</A> and <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class ToolMenu extends uk.ac.leeds.mass.fmf.shared_objects.Menu{


	/**
	* Constructor just sets up the tool menu name.
	*/
    public ToolMenu(){
        setMenuName("Tool Menu");
    }



	
	/**
    * The getItems method needs to be overridden to add items to the menu drop down.
    * The object array is two dimensional, the first dimension contains the 
    * number of menu items that you would like to include in the menu.  The second 
    * dimension is 6 elements long and contains:
    * <ol>
    * <li>The name of the menu item in o[i][0] (leave null to indicate a menu separator to be inserted)</li>
    * <li>the object to be bound to the click event in o[i][1] (the object that is to be bound to the click event must implement the ActionListener interface)</li>
    * <li>keyboard shortcuts for the Mneumonic can be stored in o[i][2] (leave null for no keyboard shortcut)</li>
    * <li>an image to be used as an icon can be stored in o[i][3] (leave null to display no icon)</li>
    * <li>keyboard shortcuts to be used directly from the keyboard can be stored in o[i][4](leave null for no shortcut)</li>
    * <li>groups of subitems are created o[i][5] which should contain an integer value remaining constant for each item in a group.
    * The first item in a group is added to the menu each subsequent item becomes a subitem in a submenu.
    * All sub items must be grouped together, if they are not an error will occur. </li>
    * </ol>
	* Here's an example from the main application:
	* <ol>
	* <li>o[2][0] = "Quit...";</li>
    * <li>o[2][1] = new QuitApplication(ai);</li>
    * <li>o[2][2] = KeyEvent.VK_Q;</li>
    * <li>o[2][3] = ai.getImage(JarInfo.JAR_NAME, JarInfo.EXIT_ICON); // Strings</li>
    * <li>o[2][4] = KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK);</li>
	* </ol>
    */
    @Override
    protected Object[][] getItems(){
        
        Object[][] o = new Object[1][6];

        o[0][0] = "Tool"; // Menu item title.
        o[0][1] = new ToolMenuListener();

        return o;
    }




    /**
	* Here, for simplicity, we use a nested class to listen to the menu option.
	**/
    class ToolMenuListener implements ActionListener{

      
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
				
                    ToolPanel tp = new ToolPanel(ai);
                    FMFFrame f = ai.getMainPanel("Tool Window Title", tp);
					
					// If more than one window with this name is open numbers are 
					// added to the end so once it has been added, update the name 
					// in the actual configuration screen with the exact name 
					// it has been allocated.
                    // tp.setWindowTitle(f.getWindowTitle());
                    
					// Set the frame to null to tidy up thread.
                    f = null;
                }
				
            };

            t.start();
            
        }

    }

}
