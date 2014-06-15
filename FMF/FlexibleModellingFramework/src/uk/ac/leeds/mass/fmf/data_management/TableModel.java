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
public class TableModel extends AbstractTableModel{

    FMFTable table;
    
    int rows = 0;
    int columns = 0;
    
    public TableModel (FMFTable table){
        this.table = table;
        
        //set up the initial row and column dimensions
        rows = this.table.getRowCount();
        columns = this.table.getFieldCount();
    }
    
    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return columns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int retRow = rowIndex+1;
        if (retRow > table.getRowCount()){
//System.out.println("getValueAt r:" + retRow+" c:"+columnIndex+" v:"+table.getInsertedValue(rowIndex-table.getRowCount(), columnIndex).toString());
            return table.getInsertedValue(retRow-table.getRowCount(), columnIndex);
        }else{
            table.moveToRow(retRow);
            switch(table.getColumnType(columnIndex)){
                case FMFTable.FIELD_TYPE_DOUBLE:
//System.out.println("getValueAt r:" + rowIndex+" c:"+columnIndex+" v:"+table.getDoubleValue(columnIndex).toString());
                    return table.getDoubleValue(columnIndex);
                case FMFTable.FIELD_TYPE_INT:
//System.out.println("getValueAt r:" + rowIndex+" c:"+columnIndex+" v:"+table.getIntegerValue(columnIndex).toString());
                    return table.getIntegerValue(columnIndex);
                case FMFTable.FIELD_TYPE_STRING:
//System.out.println("getValueAt r:" + rowIndex+" c:"+columnIndex+" v:"+table.getStringValue(columnIndex).toString());
                    return table.getStringValue(columnIndex);
            }
        }
        return null;
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
        
        switch(table.getColumnType(c)){
            case FMFTable.FIELD_TYPE_DOUBLE:
                return new Double(0.0).getClass();
            case FMFTable.FIELD_TYPE_INT:
                return new Integer(0).getClass();
            default:
                return new String("").getClass();
        }
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex){
        if (rowIndex >= table.getRowCount() ){
            table.insertValue(table.getFieldName(columnIndex), rowIndex+1, value);
        }else{
            table.editField(columnIndex, value, rowIndex+1);
        }
    }
    
    void addRow(){
        table.insertRow();
        fireTableRowsInserted(rows, rows);
        rows = this.table.getRowCount()+this.table.insertedRowCount();
    }

    void deleteRow(int row){
       if ( row > -1 && row < table.getRowCount() ){
           table.deleteRow(row+1);
           this.fireTableRowsDeleted(row, row);
           rows = this.table.getRowCount()+this.table.insertedRowCount();
        }
    }

    void commitChanges(){
        table.commitInserts();
    }
    
}
