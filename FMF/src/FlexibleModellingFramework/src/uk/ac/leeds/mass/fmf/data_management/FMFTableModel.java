/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.data_management;

import javax.swing.table.AbstractTableModel;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
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
