

package uk.ac.leeds.mass.fmf.data_management;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author geo8kh
 */
public class TableDesignTableModel extends AbstractTableModel{

    ArrayList names = new ArrayList<String>();
    ArrayList types = new ArrayList<String>();

    public int getRowCount() {
        return names.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if ( columnIndex == 0 ){
            return names.get(rowIndex);
        }else if( columnIndex == 1 ){
            return types.get(rowIndex);
        }
        return "";
    }


    @Override
    public String getColumnName(int col) {
        if ( col == 0 ){
            return "Field Name";
        }else if( col == 1 ){
            return "Field Type";
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return true;
    }

    @Override
    public int findColumn(String columnName){
        if ( columnName.equals("Field Name") ){
            return 0;
        }else if( columnName.equals("Field Type") ){
            return 1;
        }
        return -1;
    }

    @Override
    public Class getColumnClass(int c) {
        String s = "";
        return s.getClass();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex){

        if ( columnIndex == 0 ){
            names.set(rowIndex, value);
        }else if( columnIndex == 1 ){
            types.set(rowIndex, value);
        }

    }
    

    void rowAdded(){
        names.add("");
        types.add("");
        fireTableRowsInserted(getRowCount()-1, getRowCount());
    }

    void rowDeleted(int row){
        if ( row > -1 ){
            names.remove(row);
            types.remove(row);
            this.fireTableRowsDeleted(row, row);
        }
    }

}
