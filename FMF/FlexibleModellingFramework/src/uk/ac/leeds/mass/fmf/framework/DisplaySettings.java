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

import java.awt.Font;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class DisplaySettings {
    Font mf = new Font("SansSerif", Font.PLAIN,12);
    Font mif = new Font("SansSerif", Font.PLAIN,12);




    
    
    /** Creates a new instance of DisplaySettings */

    public DisplaySettings() {

    }
    






    /** Returns the Font to be used for all Menus
     * @return java.awt.Font
     */

    public Font getMenuFont(){

        return mf;

    }








    
    /** Returns the Font to be used for all MenuItems
     * @return java.awt.Font
     */

    public Font getMenuItemFont(){

        return mif;

    }
    

}
