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

package uk.ac.leeds.mass.fmf.data_management;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
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
