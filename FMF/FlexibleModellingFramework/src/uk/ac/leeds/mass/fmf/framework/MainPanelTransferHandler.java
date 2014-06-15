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


package uk.ac.leeds.mass.fmf.framework;

import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import uk.ac.leeds.mass.fmf.data_management.RegisteredDataSources;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
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
