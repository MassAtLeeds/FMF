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
package tooltemplates.toolmenutemplate;
 
import java.awt.event.*;
import javax.swing.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;


/**
 * This class shows how to build a more complicated tool menu for FMF.
 * @version 1.0
 * @author <A href="http://uk.linkedin.com/pub/kirk-harland/2b/624/5a3">Kirk Harland</A> and <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class ToolMenuTemplate extends uk.ac.leeds.mass.fmf.shared_objects.Menu{

String jarName = null;
	/**
	* Constructor just sets up the tool menu name.
	*/
    public ToolMenuTemplate(){
        setMenuName("Template: Complicated Menu");
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
    * <li>an image to be used as an icon can be stored in o[i][3] (leave null to display no icon); images (16x16 gifs) should be either in class folder or root of jar.</li>
    * <li>keyboard shortcuts to be used directly from the keyboard can be stored in o[i][4](leave null for no shortcut)</li>
    * <li>groups of subitems are created o[i][5] which should contain an int value. The system will add [i] as a menu item with a 
    *       submenu. It will then search for other menu items with the same int in o[j][5], and add these as submenu items to the [i] submenu. 
    *       All sub items must be in sequence [i],[i+1],[i+2] together; if they are not an error will occur. </li>
    * </ol>
	* See code for example.
    */
    @Override
    protected Object[][] getItems(){
        
        Object[][] o = new Object[8][6];

        o[0][0] = "Tool"; // Menu item title.
        o[0][1] = new ToolMenuListener();
		o[0][2] = KeyEvent.VK_T; // java.awt.event
		o[0][3] = ai.getImage("ToolTemplates.jar","icon.gif"); // images 16x16 should be either in class folder or root of jar.
        o[0][4] = KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK);
		
		o[1][0] = null;
		
		o[2][0] = "Submenu 1"; // This added as menuitem
		o[2][1] = new ToolMenuListener();
		o[2][5] = 1;		   // System then looks for other items in sequence with this int in position[5] to add as submenu items.	
		o[3][0] = "Submenu item 1";
		o[3][1] = new ToolMenuListener();
		o[3][5] = 1;
		o[4][0] = "Submenu item 2";
		o[4][1] = new ToolMenuListener();
		o[4][5] = 1;
		
		o[5][0] = "Submenu 2";
		o[5][5] = 2;
		o[6][0] = "Submenu item 3";
		o[6][1] = new ToolMenuListener();
		o[6][5] = 2;
		o[7][0] = "Submenu item 4";
		o[7][1] = new ToolMenuListener();
		o[7][5] = 2;
		

		return o;
    }




    /**
	* Here, for simplicity, we use a nested class to listen to the menu option.
	**/
    class ToolMenuListener implements ActionListener{

      
		/**
		* Method starts a new thread that here just writes to the status window.
		*/
        @Override
        public void actionPerformed(ActionEvent e) {
		
            Thread t = new Thread() {

				/**
				* Just writes to the status window.
				*/
                @Override
                public void run() {
                    ai.writeToStatusWindow("Hello World", false);
                }
				
            };

            t.start();
            
        }

    }

}
