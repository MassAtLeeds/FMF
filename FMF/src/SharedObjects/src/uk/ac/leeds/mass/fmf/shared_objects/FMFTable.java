/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class FMFTable implements Serializable{

    //static field type definitions
    public final static int FIELD_TYPE_UNDEFINED = 0;
    public final static int FIELD_TYPE_DOUBLE = 1;
    public final static int FIELD_TYPE_INT = 2;
    public final static int FIELD_TYPE_STRING = 3;

    public final static int FIELD_TYPE_COUNT = 4;


    private String name;
    private ArrayList<String> fields = new ArrayList<String>();
    private ArrayList<Integer> fieldTypes = new ArrayList<Integer>();
    private boolean dataInserted = false;
    private boolean dataAltered = false;
    private boolean tableOpen = false;
    private boolean dataLoading = false;
    private boolean dataSaving = false;
    private boolean filtered = false;
    private String filterField = "";
    private String filterValue = "";
    private RegisteredDataSource rds = null;
    private DefaultMutableTreeNode dmtn = null;
    private boolean fieldsLoaded = false;
    private boolean lock = false;
    private boolean filterChanged = false;

    private ResultSetMetaData rsmd = null;
    private ResultSet resultset = null;
    private Statement statement = null;
    private IApplicationInformation ai = null;

    private ArrayList<IFMFTableListener> listeners = new ArrayList<IFMFTableListener>();

    private ArrayList<String> createTableFieldNames = new ArrayList<String>();
    private ArrayList<Integer> createTableFieldTypes = new ArrayList<Integer>();

    private ArrayList<ArrayList> insertedRows = new ArrayList<ArrayList>();
    private boolean rowCommited;
    private boolean dataHasBeenLoaded = false;

    public FMFTable(String name, ResultSet rs,Statement stmt){
        this.name = name;
        this.setResultSet(rs, stmt);
    }



    /**
     * Gets the java.sql.ResultSet that this table is based on.
     *
     * @return a java.sql.ResultSet if the result set has been set or null otherwise.
     */
    public ResultSet getResultSet(){
        return resultset;
    }


    /**
     * Sets the java.sql.ResultSet that this table is based on.
     *
     * @param rs is the java.sql.ResultSet for this table to be based on.
     */

    public synchronized void setResultSet(ResultSet rs, Statement stmt){
        resultset = rs;
        statement = stmt;
        if (resultset != null){
            try {
                rsmd = resultset.getMetaData();

                if ( rsmd != null ){
                    fields.clear();
                    fieldTypes.clear();

                    //if the table is locked it is in the middle of a process so don't update inserted fields
                    if ( !this.isLocked() ){
                        this.resetInsertedRows();
                        insertedRows.clear();
                    }

                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        fields.add(rsmd.getColumnName(i));

                        if ( !this.isLocked() ){insertedRows.add(new ArrayList());}

                        if (rsmd.getColumnType(i) == Types.BIGINT
                                | rsmd.getColumnType(i) == Types.DECIMAL
                                | rsmd.getColumnType(i) == Types.DOUBLE
                                | rsmd.getColumnType(i) == Types.FLOAT
                                | rsmd.getColumnType(i) == Types.NUMERIC
                                | rsmd.getColumnType(i) == Types.REAL) {

                            fieldTypes.add(new Integer(FMFTable.FIELD_TYPE_DOUBLE));

                        } else if (rsmd.getColumnType(i) == Types.INTEGER
                                | rsmd.getColumnType(i) == Types.SMALLINT
                                | rsmd.getColumnType(i) == Types.TINYINT) {

                            fieldTypes.add(new Integer(FMFTable.FIELD_TYPE_INT));

                        } else {
                            fieldTypes.add(new Integer(FMFTable.FIELD_TYPE_STRING));
                        }

                    }

                    if ( !this.isLocked() ){insertRow();}
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }

  

    /**
     * gets the name of this table
     * @return String name of the table
     */

    public String getName() {
        return name;
    }


    /**
     * sets the name of this table
     * @param name String name for the table
     */

    public synchronized void setName(String name) {
        this.name = name;
    }


    public synchronized void loadFields(){
        if ( !fieldsLoaded & resultset == null){

            this.rds.loadFields(this);

            fieldsLoaded = true;
        }
    }


    public synchronized boolean loadDataWithReturn(IApplicationInformation ai, ICallBack cb){
        loadData(ai,cb);
        if(dataHasBeenLoaded){
            dataHasBeenLoaded = false;
            return true;
        }
        return false;
    }



    public synchronized void loadData(IApplicationInformation ai, ICallBack cb){

        dataHasBeenLoaded = false;

        rds.loadTable(this, ai, cb);
        this.setAi(ai);

        dataHasBeenLoaded = true;

    }

    public synchronized void loadDataInBackground(IApplicationInformation ai, ICallBack cb){
        try {

            dataHasBeenLoaded = false;

            if (resultset == null || resultset.isClosed()) {
                rds.loadTableInBackground(this, ai, cb);
                this.setAi(ai);

                dataHasBeenLoaded = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Moves to the specific row in the table.  Row 1 is the first row.
     * The return value from getRowCount() would be the last row.
     * 
     * @param index
     */

    public void moveToRow(int index){
        try {
            resultset.absolute(index);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean moveToFirst(){
        try {
            return resultset.first();
        } catch (SQLException ex) {
            return false;
        }
    }

    public void moveBeforeFirst(){
        try {
            resultset.beforeFirst();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean moveToLast(){
        try {
            return resultset.last();
        } catch (SQLException ex) {
            return false;
        }
    }

    public void moveAfterLast(){
        try {
            resultset.afterLast();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean hasMoreRows(){
        if (getRowCount()==0){return false;}
        try {
            return !resultset.isLast();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void moveToNextRow(){
        if (hasMoreRows()){
            try {
                if (resultset.isBeforeFirst()){
                    resultset.first();
                }else{
                    int row = resultset.getRow();
                    resultset.absolute(row + 1);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Double getDoubleValue(int idx){
        
        Object o = null;
        try {
            o = resultset.getObject(idx+1);
            if (o == null){
                return new Double(Double.NaN);
            }else{
                return new Double(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Double getDoubleValue(String field){
        
        Object o = null;
        try {
            o = resultset.getObject(field);
            if ( o == null){
                return new Double(Double.NaN);
            }else{
                return new Double(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }



    public Double getDoubleValue(int idx, int row){
        
        Object o = null;
        try {
            moveToRow(row);
            o = resultset.getObject(idx+1);
            if (o == null){
                return new Double(Double.NaN);
            }else{
               return new Double(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Double getDoubleValue(String field, int row){
        
        Object o = null;
        try {
            moveToRow(row);
            o = resultset.getObject(field);
            if ( o == null ){
                return new Double(Double.NaN);
            }else{
                return new Double(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public Integer getIntegerValue(int idx){
        
        Object o = null;
        try {
            o = resultset.getObject(idx+1);
            if (o == null){
                return null;
            }else{
                return new Integer(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Integer getIntegerValue(String field){
        
        Object o = null;
        try {
            o = resultset.getObject(field);
            if (o == null){
                return new Integer(0);
            }else{
                return new Integer(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Integer getIntegerValue(int idx, int row){
        
        Object o = null;
        try {
            moveToRow(row);
            o = resultset.getObject(idx+1);
            if (o == null){
                return null;
            }else{
                return new Integer(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Integer getIntegerValue(String field, int row){
        
        Object o = null;
        try {
            moveToRow(row);
            o = resultset.getObject(field);
            if (o == null){
                return null;
            }else{
                return new Integer(o.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public String getStringValue(int idx){
        
        Object o = null;
        try {
            o = resultset.getObject(idx+1);
            if (o == null){
                return null;
            }else{
                return o.toString();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getStringValue(String field){
        
        Object o = null;
        try {
            o = resultset.getObject(field);
            if (o == null){
                return null;
            }else{
                return o.toString();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String getStringValue(int idx, int row){
        
        Object o = null;
        try {
            moveToRow(row);
            o = resultset.getObject(idx+1);
            if (o == null){
                return null;
            }else{
                return o.toString();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public String getStringValue(String field, int row){
        
        Object o = null;
        try {
            moveToRow(row);
            o = resultset.getObject(field);
            if (o == null){
                return null;
            }else{
                return o.toString();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * gets the row id for the specified value.  This method will return the first encountered
     * instance of the value.
     *
     * @param fieldName : String name of the field to be searched.
     * @param value : String representation of the value to be searched for (case sensitive).
     * @return int : row id for the first instance of the value. If the value is not found -1 will be returned.
     */
    public int getFirstRowID(String fieldName, String value){
        
        try {

            if ( resultset != null ){

                int currentrow = resultset.getRow();
                int retValue = -1;
                resultset.beforeFirst();
                while (resultset.next()) {
                    if ( resultset.getString(fieldName).equalsIgnoreCase(value) ){
                        retValue = resultset.getRow();
                        break;
                    }
                }
                if (currentrow > 0){resultset.absolute(currentrow);}
                return retValue;

            }else{
                System.out.println("result set = null!");
                return -1;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }



    }




    public synchronized void editField (String f, Object value, int rowIndex){
        int colID = getFieldIndex(f);
        editField (colID, value, rowIndex);
    }


    /**
     * Sets the value in the specified field and row to the new value entered.
     *
     * @param f : String name of the field to be edited.
     * @param value : Object (String, Integer or Double) of the new value to be entered.
     * @param index : row id for the new value to be entered.
     */
    public synchronized void editField (int colID, Object value, int rowIndex){


        String f = getFieldName(colID);

        if ( colID > -1 ){

            int colType = getColumnType(colID);

            if ( colType != FMFTable.FIELD_TYPE_UNDEFINED ){

                try{

                    if ( rowIndex > 0 ){resultset.absolute(rowIndex);}

                    // if the value passed in is a null put a null in the table
                    if ( value == null ){
                        resultset.updateNull(colID+1);
                        fieldChanged(f, resultset.getRow());

                    // if we have a string passed in try to convert it to the required field type
                    }else if (value instanceof String){

                        // if its a string field put the value straight in
                        if ( colType == FMFTable.FIELD_TYPE_STRING ){
                            resultset.updateString( colID+1, value.toString() );
                            fieldChanged(f, resultset.getRow());

                        //else try to convert the string into the appropriate numeric type.
                        }else{
                            try{
                                //first integer if the field is an int
                                if ( colType == FMFTable.FIELD_TYPE_INT ){
                                    Integer I = Integer.parseInt((String)value);
                                    resultset.updateInt( colID+1, I );
                                    fieldChanged(f, resultset.getRow());
                                //or double if the field is a double
                                }else if( colType == FMFTable.FIELD_TYPE_DOUBLE ){
                                    Double D = Double.parseDouble((String)value);
                                    resultset.updateDouble( colID+1, D );
                                    fieldChanged(f, resultset.getRow());
                                }
                            //catch any exception and report the conversion failure to the screen
                            }catch(Exception e){
                                System.out.println("Field " + f + " in table " + this.getName() + " is of type " + fieldTypeText(colID) );
                                System.out.println("Update value to " + value.toString() + " of type " + value.getClass().getSimpleName() + " failed. Incompatible types.");
                            }
                        }

                    // put integers in integer fields
                    }else if( value instanceof Integer  && colType == FMFTable.FIELD_TYPE_INT ){
                        resultset.updateInt( colID+1, (Integer)value );
                        fieldChanged(f, resultset.getRow());

                    // put doubles in double fields
                    }else if( value instanceof Double  && colType == FMFTable.FIELD_TYPE_DOUBLE ){
                        resultset.updateDouble( colID+1, (Double)value );
                        fieldChanged(f, resultset.getRow());

                    //allows us to insert numeric types into string fields
                    }else if ( colType == FMFTable.FIELD_TYPE_STRING ){
                        resultset.updateString( colID+1, value.toString() );
                        fieldChanged(f, resultset.getRow());
                    }else{
                        System.out.println("Field " + f + " in table " + this.getName() + " is of type " + fieldTypeText(colID) );
                        System.out.println("Update value to " + value.toString() + " of type " + value.getClass().getSimpleName() + " failed. Incompatible types.");
                    }


                    if ( rowIndex > 0 ){resultset.updateRow();}

                }catch(SQLException ex){
                    System.out.println("Update of field " + f + " to value " + value.toString() + " in table " + this.getName() + " failed.");
                    ex.printStackTrace();
                }
            }else{
                System.out.println("Field " + f + " has an undefined field type in table " + this.getName());
                System.out.println("Update value to " + value.toString() + " failed");
            }

        }else{
            System.out.println("Field " + f + " cannot be found in table " + this.getName());
            System.out.println("Update value to " + value.toString() + " failed");
        }

    }
    

    private String fieldTypeText(int fieldType){
        switch(fieldType){
            case FMFTable.FIELD_TYPE_INT:
                return "Integer";
            case FMFTable.FIELD_TYPE_DOUBLE:
                return "Double";
            case FMFTable.FIELD_TYPE_STRING:
                return "String";
            case FMFTable.FIELD_TYPE_UNDEFINED:
                return "Undefined";
            default:
                return "Unrecognised";
        }
    }

    public String getFieldName(int index){
        if (index > -1 && index < fields.size()){
            return fields.get(index);
        }else{
            return "no such field name";
        }

    }

    public int getFieldIndex(String fieldName){

        for (int i = 0; i < fields.size(); i++) {
            if ( fieldName.equalsIgnoreCase(fields.get(i)) ){return i;}
        }
        return -1;
    }


    public boolean isNumeric(String field){
        return  isNumeric( getFieldIndex(field) );
    }
    
    public boolean isNumeric(int fieldIndex){
        if ( getColumnType(fieldIndex) == FMFTable.FIELD_TYPE_STRING ){
            return false;
        }else {
            return true;
        }
    }


    public int getColumnType(String fieldName){
        return getColumnType( getFieldIndex(fieldName) );
    }

    public int getColumnType(int colID){

        if (colID > -1 && colID < fieldTypes.size()){
            return fieldTypes.get(colID).intValue();
        }else{
            return FMFTable.FIELD_TYPE_UNDEFINED;
        }

    }


    public static int getFieldType(String description){
        if( description.equals("Double") ){
            return FIELD_TYPE_DOUBLE;
        }else if( description.equals("Integer") ){
            return FIELD_TYPE_INT;
        }else if( description.equals("Text") ){
            return FIELD_TYPE_STRING;
        }else{
            return FIELD_TYPE_UNDEFINED;
        }
    }


    public static String getFieldTypeName(int fieldType){
        switch(fieldType){
            case FIELD_TYPE_DOUBLE: return "Double";
            case FIELD_TYPE_INT: return "Integer";
            case FIELD_TYPE_STRING: return "Text";
        }
        return "Undefined";
    }

    /**
     * Adds a table listener to the table.  The listeners will be informed when data changes in the table.
     *
     * @param listener : valid IFMFTableListener.
     */

    public synchronized void addTableListener (IFMFTableListener listener){
        if ( listener != null && !listeners.contains(listener) ) {listeners.add(listener);}
    }

    /**
     * Removes the specified IFMFTableListener listener from the table.
     * @param listener
     */
    public synchronized void removeTableListener (IFMFTableListener listener){
        if ( listeners.contains(listener) ) {
            listeners.remove(listener);
        }
    }


    /**
     * Gets all of the IFMFTableListener listeners.
     * @return
     */
    public ArrayList<IFMFTableListener> getAllTableListeners(){
        return listeners;
    }

    /**
     * clears all of the current listeners from the table.
     */
    public synchronized void clearAllTableListeners(){
        listeners.clear();
    }

    /**
     * Tests to see if a field exists in the table.
     *
     * @param f name of the field to search for.
     * @return true if the field exists otherwise false.
     */
    public boolean fieldExists(String f){
        if ( getFieldIndex(f) == -1 ){
            return false;
        }else{
            return true;
        }
    }



    /**
     * tests to see if the data has been loaded
     * @return true if the data has been loaded otherwise false
     */
    public boolean isDataLoaded() {
        return resultset!=null;
    }

    /**
     * Sets the tables data altered flag.
     * @param dataAltered : true if the data has been altered
     */
    public synchronized void setDataAltered( boolean dataAltered ){
        this.dataAltered = dataAltered;
    }

    /**
     * tests to see if the data has been altered
     * @return : true if the data has been altered otherwise false.
     */
    public boolean isDataAltered() {
        return dataAltered;
    }

    /**
     * Checks to see if the table is already open.
     * @return : true if the table is already open for editing otherwise false.
     */
    public boolean isTableOpen() {
        return tableOpen;
    }

    /**
     * sets whether the table is open for editing or not
     * @param tableOpen : true if the table is open for editing.
     */
    public synchronized void setTableOpen(boolean tableOpen) {
        this.tableOpen = tableOpen;
    }



    public synchronized ArrayList getUniqueValues(String fieldName){

        return getAi().getDataAccessFactory().getUniqueValues(this, fieldName);

    }


    public Object getObject(ResultSet rs, String fieldName){
        try {
            switch (getColumnType(fieldName)){
                case FMFTable.FIELD_TYPE_DOUBLE:
                    return rs.getDouble(fieldName);
                case FMFTable.FIELD_TYPE_INT:
                    return rs.getInt(fieldName);
                case FMFTable.FIELD_TYPE_STRING:
                    return rs.getString(fieldName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * Clears this table.
     */
    public synchronized void clear(){

            try{

                if ( !isLocked() && !isDataSaving() ){

                    if ( isTableOpen() ){
                        getAi().closeMainPanel("Table - " + getName());
                    }

                    if (resultset != null) {
                        resultset.close();
                        resultset = null;
                    }
                    if (statement != null){
                        statement.getConnection().close();
                        statement.close();
                        statement = null;
                    }

                    dataAltered = false;
                    dataLoading = false;

                    resetInsertedRows();

                    if ( getAi() != null ){getAi().getDataAccessFactory().clearCache(this);}
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
    }




    synchronized void resetInsertedRows(){
        if (!this.isLocked()){

           for (int i = 0; i < insertedRows.size(); i++) {
                insertedRows.get(i).clear();
            }
            insertRow();
        }
    }




    synchronized public void refreshData(){
        clear();
        loadData(getAi(), null);
    }

    /**
     * Inserts a new row into all the table's fields
     */
    public synchronized void insertRow(){

        for (int i = 0; i < insertedRows.size(); i++) {
            switch(getColumnType(i)){
                case FMFTable.FIELD_TYPE_DOUBLE:
                    insertedRows.get(i).add(new Double(0.0));
                    break;
                case FMFTable.FIELD_TYPE_INT:
                    insertedRows.get(i).add(new Integer(0));
                    break;
                case FMFTable.FIELD_TYPE_STRING:
                    insertedRows.get(i).add(new String());
                    break;
            }

        }

       rowCommited = true;

    }

    public Object getInsertedValue(int row, int col){
        return insertedRows.get(col).get(row);
    }

    public synchronized void commitInserts(){
        setDataSaving(true);
        if (!rowCommited){insertRow();}
        getAi().getDataAccessFactory().commitInsert(this);

        this.dataAltered = false;
        resetInsertedRows();
    }

    public synchronized void insertValue(String fieldName, Object value){
        if (!insertedRows.isEmpty()){
                insertValue(fieldName,insertedRows.get(0).size()-1,value);
        }
    }


    public synchronized void insertValue(String fieldName, int rowID, Object value){
        if (!insertedRows.isEmpty()){
            int fieldIndex = getFieldIndex(fieldName);
            if (fieldIndex > -1){
                if (rowID < insertedRows.get(fieldIndex).size()){
                    if (rowID == -1){
                        if( insertedRows.get(fieldIndex).size() == 0 ){
                            insertRow();
                        }
                        rowID = insertedRows.get(fieldIndex).size()-1;
                    }else{
                        insertedRows.get(fieldIndex).set(rowID,value);
                    }
                    rowCommited = false;
                }
            }
        }
    }


    /**
     * Deletes the specified row from all the fields in the table.
     * After the execution of this method the isDataAltered method will return true.
     * @param index : row id to be deleted
     */
    public synchronized void deleteRow(int index){
        try {
            if ( resultset.absolute(index) ) {
                resultset.deleteRow();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Checks to see if data has been inserted or not
     * @return : true if data has been inserted otherwise false.
     */
    public boolean isDataInserted() {
        return dataInserted;
    }



    /**
     * Gets the number of rows that have been inserted.
     * @return : int number of rows that have been inserted
     */
    public int insertedRowCount(){
        if (insertedRows.size() < 1){
            return 0;
        }else{
            return insertedRows.get(0).size()-1;
        }
    }




    /**
     * Gets the current number of rows in the table.
     * @return int number of rows in the table.
     */
    public int getRowCount(){
        int rows = -1;
        try {

            int currentRow = resultset.getRow();
            
            if (currentRow == 0) {
                if (resultset.isBeforeFirst()) {
                    currentRow = -1;
                } else if (resultset.isAfterLast()) {
                    currentRow = -2;
                }
            }

            resultset.absolute(-1);
            rows = resultset.getRow();

            if (currentRow == -2) {
                resultset.afterLast();
            } else if (currentRow == -1) {
                resultset.beforeFirst();
            } else {
                resultset.absolute(currentRow);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rows;
    }

    /**
     * Gets the current number of fields in the table
     * @return int count of fields in the table.
     */
    public int getFieldCount(){

        return fields.size();
        
    }

    /**
     * This is the method that is fired when data is changed in a field and in turn
     * calls any objects listening for data change events.
     * @param fieldName : Name of the field that has been altered.
     */
    public void fieldChanged(String fieldName, int rowIdx) {
        // do we have some registered listeners to broadcast to?
        if ( !listeners.isEmpty() ){
            boolean firstChange = false;
            if ( !dataAltered && !dataInserted ){
                firstChange = true;
            }
            for ( int i = 0; i < listeners.size() ; i++) {
                if ( firstChange ){ listeners.get(i).tableIsDirty(name, fieldName); }
                listeners.get(i).tableChanged(name, fieldName);
            }
        }

    }


    /**
     * @return the dataLoading
     */
    public boolean isDataLoading() {
        return dataLoading;
    }

    /**
     * @param dataLoading the dataLoading to set
     */
    public synchronized void setDataLoading(boolean dataLoading) {
        this.dataLoading = dataLoading;
    }

    /**
     * @return the dataSaving
     */
    public boolean isDataSaving() {
        return dataSaving;
    }

    /**
     * @param dataSaving the dataSaving to set
     */
    public synchronized void setDataSaving(boolean dataSaving) {
        this.dataSaving = dataSaving;
    }

    /**
     * @return the filtered
     */
    public boolean isFiltered() {
        return filtered;
    }

    /**
     * @return the filterField
     */
    public String getFilterField() {
        return filterField;
    }

    /**
     * @param filterField the filterField to set
     * @param filterValue the value to filter by
     */
    public synchronized void setFilter(String filterField, String filterValue) {
        this.filterField = filterField;
        this.filterValue = filterValue;
        filtered = true;
        filterChanged = true;
        rds.loadTable(this, ai, null);
    }

    public synchronized void clearFilter(){
        this.filterField = "";
        this.filterValue = "";
        filtered = false;
        filterChanged = true;
    }

    public synchronized void clearFilter(boolean reloadData){
        clearFilter();
        if ( reloadData ){
            rds.loadTable(this, ai, null);
        }
    }

    /**
     * returns true if either setFilter or clearFilter has been called
     * since the last time this method was checked.
     *
     * @return true if the filter settings have changed since the last time this method was checked
     * otherwise false.
     */
    public boolean hasFilterChanged(){
        boolean b = filterChanged;
        filterChanged = false;
        return b;
    }

    /**
     * @return the filterValue
     */
    public String getFilterValue() {
        return filterValue;
    }


    synchronized void setRDS(RegisteredDataSource rds){
        this.rds = rds;
    }

    public RegisteredDataSource getRDS(){
        return rds;
    }

    public void setTreeNode(DefaultMutableTreeNode dmtn){
        this.dmtn = dmtn;
    }

    public DefaultMutableTreeNode getTreeNode (){
        return dmtn;
    }

    public synchronized void addFieldToNewTable(String fieldName, int fieldType){
        createTableFieldNames.add(fieldName);
        createTableFieldTypes.add(new Integer(fieldType));
    }

    public int getNewTableFieldCount(){
        return createTableFieldNames.size();
    }

    public synchronized void clearNewTableInformation(){
        createTableFieldNames.clear();
        createTableFieldTypes.clear();
    }

    public String getNewTableFieldName(int fieldIndex){
        return createTableFieldNames.get(fieldIndex);
    }
    
    public int getNewTableFieldType(int fieldIndex){
        return createTableFieldTypes.get(fieldIndex).intValue();
    }

    /**
     * @return the ai
     */
    public IApplicationInformation getAi() {
        return ai;
    }

    /**
     * @param ai the ai to set
     */
    public synchronized void setAi(IApplicationInformation ai) {
        this.ai = ai;
    }

    /**
     * Tests to see if the table is locked
     * @return
     */

    public boolean isLocked(){return lock;}

    /**
     * unlocks the table.  This will allow the contents of the table to be cleared.
     */
    public synchronized void unlock(){lock = false;}

    /**
     * This means that the contents of the table cannot be cleared until the table is unlocked
     */
    public synchronized void lock(){lock = true;}


}
