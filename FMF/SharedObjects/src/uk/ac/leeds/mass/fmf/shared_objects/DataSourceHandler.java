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
