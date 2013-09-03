/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
