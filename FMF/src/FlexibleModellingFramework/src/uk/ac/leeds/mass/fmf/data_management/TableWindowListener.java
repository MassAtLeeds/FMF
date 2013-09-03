/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.data_management;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class TableWindowListener implements InternalFrameListener{

    private FMFTable table;
    
    private TableWindowListener(){}
    
    public TableWindowListener(FMFTable table){
        this.table = table;
    }
    
    public void internalFrameOpened(InternalFrameEvent e) {}

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        table.commitInserts();
        table.setTableOpen(false);
    }

    public void internalFrameClosed(InternalFrameEvent e) {
        table.clear();
    }

    public void internalFrameIconified(InternalFrameEvent e) {}

    public void internalFrameDeiconified(InternalFrameEvent e) {}

    public void internalFrameActivated(InternalFrameEvent e) {}

    public void internalFrameDeactivated(InternalFrameEvent e) {}

}
