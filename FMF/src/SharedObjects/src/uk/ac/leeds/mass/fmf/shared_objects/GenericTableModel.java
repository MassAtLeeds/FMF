/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;


import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author geo8kh
 */
public class GenericTableModel extends AbstractTableModel{

    ArrayList<ArrayList> columns = new ArrayList<ArrayList>();
    ArrayList<String> columnNames = new ArrayList<String>();
    ArrayList<Boolean> columnEditable = new ArrayList<Boolean>();
    ArrayList columnTypes = new ArrayList();
    

    public int getRowCount() {
        if ( columns.isEmpty() ){
            return 0;
        }else{
            return columns.get(0).size();
        }
    }

    public int getColumnCount() {
        return columns.size();
    }

    
    public Object getValueAt(int rowIndex, int columnIndex) {
        if ( columnIndex < columns.size() ){
            ArrayList a = columns.get(columnIndex);
            if ( rowIndex < a.size() ){
                return a.get(rowIndex);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }


    @Override
    public String getColumnName(int col) {
        if ( col < columnNames.size() ){
            return columnNames.get(col);
        }
        return "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        if ( columnIndex < columnEditable.size() ){
            return columnEditable.get(columnIndex);
        }
        return false;
    }

    @Override
    public int findColumn(String columnName){

        for (int i = 0; i < columnNames.size(); i++) {
            if ( columnName.equals( columnNames.get(i) ) ) {return i;}
        }
        return -1;
    }

    @Override
    public Class getColumnClass(int c) {
        if ( c < columnTypes.size() ){
            return columnTypes.get(c).getClass();
        }else{
            return String.class;
        } 
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex){

        if ( columnIndex < columns.size() ){
            ArrayList a = columns.get(columnIndex);
            if ( rowIndex < a.size() ){
                a.set(rowIndex, value);
            }
        }
        
    }

    

    public void rowAdded(){
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).add(null);
        }
        fireTableRowsInserted(getRowCount()-1, getRowCount());
    }

    public void rowDeleted(int row){
        if ( row > -1 ){
            for (int i = 0; i < columns.size(); i++) {
                columns.get(i).remove(row);
            }
            this.fireTableRowsDeleted(row, row);
        }
    }
    
    
    
    public void addColumn(String name, boolean editable, Object type){
        columns.add(new ArrayList());
        columnNames.add(name);
        columnEditable.add(editable);
        columnTypes.add(type);
    }


    /**
     * this method clears the data from the table without changing the structure
     */
    public void clearData(){
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).clear();
        }
    }

    /**
     * this method clears the data from the table and then clears the field structure!
     */
    public void clearModel(){
        clearData();
        columns.clear();
        columnNames.clear();
        columnEditable.clear();
    }
    

}

