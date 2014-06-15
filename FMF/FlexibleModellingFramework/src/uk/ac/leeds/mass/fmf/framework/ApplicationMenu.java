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

package uk.ac.leeds.mass.fmf.framework;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class ApplicationMenu  extends uk.ac.leeds.mass.fmf.shared_objects.Menu{
    /** Creates a new instance of Menu */
    public ApplicationMenu() {
        setMenuName("Application");
        
        //set menuMnemonic
        setMenuMnemonic(KeyEvent.VK_A);
    }
    
    /**
     *This method must be overwritten to create a working menu.
     *The Object array contains the name of the menu item in o[i][1]
     *and the object to be bound to the click event in o[i][2].
     *The object that is to be bound to the click event must implement the ActionListener interface.
     */
    @Override
    protected Object[][] getItems(){
        Object[][] o = new Object[3][6];
        
	//sets up the entries for the application menu.
        o[0][0] = "Set Data Directory";
        o[0][1] = new ChangeDataDir(ai);
        
        
        o[1][0] = null;
        o[1][1] = null;
        
        
        o[2][0] = "Quit...";
        o[2][1] = new QuitApplication(ai);
        o[2][2] = KeyEvent.VK_Q;
        o[2][3] = ai.getImage(JarInfo.JAR_NAME, JarInfo.EXIT_ICON);
        o[2][4] = KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK);
        
        
        return o;
    }
}
