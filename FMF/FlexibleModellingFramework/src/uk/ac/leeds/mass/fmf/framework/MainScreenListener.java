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

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 * 
 * This is a window listener attached to the Main Screen window of the application. 
 * Allows any tidying up of datasources etc. to be done before the application closes.
 * 
 */

//java imports
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;


public class MainScreenListener implements WindowListener, ListSelectionListener{
	public void windowOpened(WindowEvent e){}


	public void windowClosing(WindowEvent e){

            //code for tidying up should go here
            ApplicationDatabase db = ApplicationDatabase.getCurrent();
            db.finalize();

	}


	public void windowClosed(WindowEvent e){}


	public void windowIconified(WindowEvent e){}


	public void windowDeiconified(WindowEvent e){}


	public void windowActivated(WindowEvent e){}


	public void windowDeactivated(WindowEvent e){}

        
        
    public void valueChanged(ListSelectionEvent e) {
        ApplicationInformation.getCurrent().setWindowOrder();
    }

    
}
