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


package FlatFile;


import javax.swing.table.AbstractTableModel;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author geo8kh
 */
public class RegisterTableModel extends AbstractTableModel{
    
    private int rows = 0;
    private int columns = 0;
    private String[][] contents = null;
    private String[] columnNames = null;
    private Object[] columnClasses = null;
    
    public RegisterTableModel (int rows, int columns, String[][] contents){
        //set up the initial row and column dimensions
        this.rows = rows;
        this.columns = columns;
        this.contents = contents;
        columnClasses = new Object[this.columns];
        for (int i = 0; i < columnClasses.length; i++) {columnClasses[i] = new String();}
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
        if (rowIndex <= rows && columnIndex <= columns){
            Class C = getColumnClass(columnIndex);
            try{
                if ( C==Integer.class ){
                    return new Integer(contents[rowIndex][columnIndex]);
                }else if ( C==Double.class ){
                    return new Double(contents[rowIndex][columnIndex]);
                }

                return contents[rowIndex][columnIndex];
            }catch (Exception e){
                System.out.println("Could not convert the value at row "+(rowIndex+1)+" data loss may occur");
                if ( C==Integer.class ){
                    return new Integer(0);
                }else if ( C==Double.class ){
                    return new Double(0.0);
                }
            }
        }
        return "";
    }
    
    @Override
    public String getColumnName(int col) {
        if (columnNames == null){
            return "Field " + col;
        }else{
            return columnNames[col];
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
      return false;
    }
    
    @Override
    public int findColumn(String columnName){
        for (int i = 0; i < columns; i++) {
            if ( columnName.equalsIgnoreCase(getColumnName(i)) ) {return i;}
        }
        return -1;
    }
    
    int getColumnFMFType(int c){
        Class C = getColumnClass(c);
        if (C == Integer.class){
            return FMFTable.FIELD_TYPE_INT;
        }else if(C == Double.class){
            return FMFTable.FIELD_TYPE_DOUBLE;
        }else if (C == String.class){
            return FMFTable.FIELD_TYPE_STRING;
        }else{
            return FMFTable.FIELD_TYPE_UNDEFINED;
        }
    }

    @Override
    public Class getColumnClass(int c) {
        return columnClasses[c].getClass();
    }

    public void setColumnNames(String[] columnNames){
        this.columnNames = columnNames;
    }

    public void setColumnClass(Object colClass, int col){
        if ( col < this.columnClasses.length ){this.columnClasses[col] = colClass;}
    }

}
