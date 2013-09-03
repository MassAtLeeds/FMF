/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.TransferHandler;

/**
 *
 * @author geo8kh
 */
public abstract class DataSourceHandler extends TransferHandler{

    DataFlavor flavour = null;
    private RegisteredDataSource rds = null;

    public DataSourceHandler(){
        try{
            flavour = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType +
                        ";class=uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo");
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {

        if ( !support.isDrop() ){return false;}

        if ( !support.isDataFlavorSupported( flavour ) ){
            return false;
        }


        Transferable t = support.getTransferable();

        TreeCellInfo tci = null;
        try {
            if (t.getTransferData(flavour) instanceof TreeCellInfo) {
                tci = (TreeCellInfo) t.getTransferData(flavour);
            } else {
                return false;
            }
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        boolean importItem =  shouldImport(tci);

        if (importItem){rds = tci.getRDS();}
        
        return importItem;
        
    }


    /**
     * This method should be overwriten to setup whether an object can recieve the drop action
     *
     * @param t is the TreeCellInfo object that is the source of the drag
     * @return true if the target object should accept the drop else false
     */
    public abstract boolean shouldImport(TreeCellInfo t);

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }

        //show the drop location
        support.setShowDropLocation(true);

        if ( !support.isDataFlavorSupported( flavour ) ){
                return false;
        }



        Transferable t = support.getTransferable();

        TreeCellInfo tci = null;
        try {
            if (t.getTransferData(flavour) instanceof TreeCellInfo) {
                tci = (TreeCellInfo) t.getTransferData(flavour);
            } else {
                return false;
            }
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        doAction(tci);

        return true;
    }

    /**
     * this method must be overwriten to invoke a drop action
     * 
     * @param t is the TreeCellInfo object that is the source of the drag
     */
    public abstract void doAction(TreeCellInfo t);

    public RegisteredDataSource getDataSource(){
        return rds;
    }

}
