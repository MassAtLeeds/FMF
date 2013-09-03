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
public class SaveTableAction  implements ActionListener, Runnable{
    public SaveTableAction (){}
    
    
    public void actionPerformed(ActionEvent e) {
        new Thread(this).start();
    }

    public void run() {
        RegisteredDataSources.getCurrent().saveTable();
    }

}
