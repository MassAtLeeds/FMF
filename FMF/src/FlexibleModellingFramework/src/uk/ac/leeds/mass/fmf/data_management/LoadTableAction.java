/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.data_management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class LoadTableAction implements ActionListener, Runnable{

    boolean openTable = true;
    
    private LoadTableAction (){}
    
    public LoadTableAction( boolean openTable ){
        this.openTable = openTable;
    }
    
    public void actionPerformed(ActionEvent e) {
        new Thread(this).start();
    }

    public void run() {
        if ( openTable ){
            RegisteredDataSources.getCurrent().openTable();
        }else{
            RegisteredDataSources.getCurrent().loadTable();
        }
    }

}
