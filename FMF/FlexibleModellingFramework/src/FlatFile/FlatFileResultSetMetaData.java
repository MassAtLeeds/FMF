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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 *
 * @author geo8kh
 */
public class FlatFileResultSetMetaData implements ResultSetMetaData{

    private ArrayList<String> fieldNames = new ArrayList<String>();
    private ArrayList fieldTypes = new ArrayList();


    int getColumnIndex(String name){
        for (int i = 0; i < fieldNames.size(); i++) {
            if (fieldNames.get(i).equalsIgnoreCase(name)){
                return i+1;
            }
        }
        return -1;
    }

    void removeField(int index){
        int colIndex = index-1;
        if ( colIndex > -1 && colIndex < fieldNames.size() ){
            fieldTypes.remove(colIndex);
            fieldNames.remove(colIndex);
        }
    }

    void addField(String name,Integer I){
        fieldTypes.add(I);
        fieldNames.add(name);
    }
    void addField(String name,Double D){
        fieldTypes.add(D);
        fieldNames.add(name);
    }
    void addField(String name,String S){
        fieldTypes.add(S);
        fieldNames.add(name);
    }

    @Override
    public int getColumnCount() throws SQLException {
        return fieldTypes.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int isNullable(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        int colIndex = column-1;
        if ( colIndex > -1 && colIndex < fieldNames.size()){
            return fieldNames.get(colIndex);
        }else{
            throw new SQLException("Index " + column + " out of range: FlatFileResultSetMetaData : getColumnName");
        }
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getScale(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTableName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        int colIndex = column-1;
        if ( colIndex > -1 && colIndex < fieldTypes.size()){
            Class c = fieldTypes.get(colIndex).getClass();
            if ( c == Integer.class ){
                return Types.INTEGER;
            }else if( c == Double.class ){
                return Types.DOUBLE;
            }else{
                return Types.VARCHAR;
            }
        }else{
            throw new SQLException("Index " + column + " out of range: FlatFileResultSetMetaData : getColumnType");
        }
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
