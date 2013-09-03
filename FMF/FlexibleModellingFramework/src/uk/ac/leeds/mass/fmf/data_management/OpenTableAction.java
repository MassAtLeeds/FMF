/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.data_management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class OpenTableAction implements ActionListener, Runnable{

    TreeCellInfo tci = null;

    public OpenTableAction (){}
    
    public OpenTableAction (TreeCellInfo t){
        tci = t;
    }

    public void actionPerformed(ActionEvent e) {
        openTable();
    }

    public void openTable(){
        new Thread(this).start();
    }

    public void run() {

        if( tci == null ){
            RegisteredDataSources.getCurrent().openTable();
        }else{
            RegisteredDataSources.getCurrent().openTable(tci);
        }
    }

}
