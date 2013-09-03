
package uk.ac.leeds.mass.fmf.framework;

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import uk.ac.leeds.mass.fmf.data_management.RegisteredDataSources;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class MainPanelTransferHandler extends TransferHandler{


    @Override
    public int getSourceActions(JComponent c){
        return COPY;
    }


    @Override
    protected Transferable createTransferable(JComponent c) {

        if ( c instanceof JTree ){
            TreeCellInfo t = RegisteredDataSources.getCurrent().getCurrentSelectedTreeCellInfo();
            return t;
        }else{
            return null;
        }

    }




    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        return false;
    }




}
