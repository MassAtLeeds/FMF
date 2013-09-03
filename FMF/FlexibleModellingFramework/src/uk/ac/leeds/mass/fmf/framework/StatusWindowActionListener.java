/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class StatusWindowActionListener implements ActionListener {

    private MainScreen ms = null;
    private String action = "";
    
    private StatusWindowActionListener(){}
    
    public StatusWindowActionListener (MainScreen m, String action){
        ms=m;
        this.action = action;
    }
    
    public void actionPerformed(ActionEvent e) {
        ms.actionPerformed(action);
    }

}
