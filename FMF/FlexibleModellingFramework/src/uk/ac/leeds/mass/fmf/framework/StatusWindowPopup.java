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

import uk.ac.leeds.mass.fmf.shared_objects.Menu;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class StatusWindowPopup extends Menu{
    
    public static final String COPY = "Copy";
    public static final String SELECT_ALL = "Select all";
    public static final String CLEAR_ALL = "Clear all";
    
    private MainScreen ms = null;
    
    private StatusWindowPopup(){}
    public StatusWindowPopup(MainScreen m){ms=m;}
    
    protected Object[][] getItems(){
        Object[][] o = new Object[3][6];
        
	//sets up the entries for the application menu.
        o[0][0] = COPY;
        o[0][1] = new StatusWindowActionListener(ms,COPY);
        
        o[1][0] = SELECT_ALL;
        o[1][1] = new StatusWindowActionListener(ms,SELECT_ALL);       

        o[2][0] = CLEAR_ALL;
        o[2][1] = new StatusWindowActionListener(ms,CLEAR_ALL); 

        
        return o;
    }
    
}
