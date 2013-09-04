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

package uk.ac.leeds.mass.fmf.shared_objects;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPopupMenu;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class FMFPopupListener implements MouseListener{

    private JPopupMenu popup = null;
    
    private FMFPopupListener (){}
    
    public FMFPopupListener(JPopupMenu pop){
        popup = pop;
    }
    
    public void mouseClicked(MouseEvent e) {
        
    }

    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    public void mouseEntered(MouseEvent e) {
        
    }

    public void mouseExited(MouseEvent e) {
        
    }
    
    private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()){
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
}
