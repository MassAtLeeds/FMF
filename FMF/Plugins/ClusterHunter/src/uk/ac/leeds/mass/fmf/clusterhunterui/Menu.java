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


package uk.ac.leeds.mass.fmf.clusterhunterui;

import uk.ac.leeds.mass.fmf.clusterhunterui.ConfigurationScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import uk.ac.leeds.mass.fmf.shared_objects.FMFFrame;

/**
 * This is the main entry class for the cluster hunter user interface to the FMF.
 * This class extends the abstract Menu class in the SharedObjects package to 
 * ensure that the abstract factory pattern in the main FMF start mechanism picks up
 * the menu and adds it to the menu bar.
 * 
 * @author kirkharland
 */
public class Menu extends uk.ac.leeds.mass.fmf.shared_objects.Menu{

    public Menu(){
        //add the menu name in the constructor for the new class using the 
        //super classes setMenuName method
        setMenuName("Cluster Hunter");
        
        //set menuMnemonic (shortcut key)
        setMenuMnemonic(KeyEvent.VK_C);
    }

    /**
    *The getItems method needs to be overridden to add items to the menu drop
    *down.  The object array is two dimensional, the first dimension contains the 
    *number of menu items that you would like to include in the menu.  The second 
    *dimension is 6 elements long and contains:
    *<ol>
    * <li>The Object array contains the name of the menu item in o[i][0] (leave null to indicate a menu separator to be inserted)</li>
    * <li>and the object to be bound to the click event in o[i][1] (the object that is to be bound to the click event must implement the ActionListener interface)</li>
    * <li>keyboard shortcuts for the Mneumonic can be stored in o[i][2] (leave null for no keyboard shortcut)</li>
    * <li>an image to be used as an icon can be stored in o[i][3] (leave null to display no icon)</li>
    * <li>keyboard shortcuts to be used directly from the keyboard can be stored in o[i][4](leave null for no shortcut)</li>
    * <li>groups of subitems are created o[i][5] which should contain an integer value remaining constant for each item in a group.
    * The first item in a group is added to the menu each subsequent item becomes a subitem in a submenu.
    * All sub items must be grouped together, if they are not an error will occur. </li>
    * </ol>
    */
    @Override
    protected Object[][] getItems(){

        Object[][] o = new Object[1][6];

        //sets up the microsimulation main menu
        o[0][0] = "New Cluster Hunter Job";
        o[0][1] = new ConfigureModel();
        
        return o;
    }



    //nested class listens to the configure model menu option
    class ConfigureModel implements ActionListener{


        public ConfigureModel (){}

        @Override
        public void actionPerformed(ActionEvent e) {
            //when the menu item has been selected launch the configuration screen for 
            //the job type i.e. clustering job
            //we start it in a new thread so any data reading does not interfere with the 
            //response time of the overall GUI
            Thread t = new Thread() {

                @Override
                public void run() {
                    //create an instance of our configuration screen for clustering
                    ConfigurationScreen cs = new ConfigurationScreen(ai);
                    //add the frame to the internal windows of the FMF and give it 
                    //a name.
                    FMFFrame f = ai.getMainPanel( "Cluster Hunter", cs );
                    //if more than one window with this name is open numbers are added to the end 
                    //so once it has been added update the name in the actual configuration screen
                    //with the exact name it has been allocated.
                    cs.setWindowTitle(f.getWindowTitle());
                    //set the frame to null to tidy up!
                    f = null;
                }
            };

            //start the thread running
            t.start();
            
        }

    }


}

