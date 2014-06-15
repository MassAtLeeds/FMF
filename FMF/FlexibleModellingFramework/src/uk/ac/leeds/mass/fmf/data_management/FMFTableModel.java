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

import javax.swing.table.AbstractTableModel;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class FMFTableModel extends AbstractTableModel{

    FMFTable table;
    
    int rows = 0;
    int columns = 0;
    
    public FMFTableModel (FMFTable table){
        this.table = table;
        
        //set up the initial row and column dimensions
        rows = table.getRowCount();
        columns = table.getFieldCount();
    }
    
    public int getRowCount() {
        return rows;
    }

    public int getColumnCount() {
        return columns;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        table.moveToRow(rowIndex);
        switch(table.getColumnType(columnIndex)){
            case FMFTable.FIELD_TYPE_DOUBLE:
                return table.getDoubleValue(columnIndex);
            case FMFTable.FIELD_TYPE_INT:
                return table.getIntegerValue(columnIndex);
            case FMFTable.FIELD_TYPE_STRING:
                return table.getStringValue(columnIndex);
        }
        return "";
    }
    
    @Override
    public String getColumnName(int col) {
        return table.getFieldName(col);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
      return true;
    }
    
    @Override
    public int findColumn(String columnName){
        return table.getFieldIndex(columnName);
    }
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    
}
