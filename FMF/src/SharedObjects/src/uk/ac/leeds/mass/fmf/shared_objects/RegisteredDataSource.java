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

package uk.ac.leeds.mass.fmf.shared_objects;

import java.util.ArrayList;


/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class RegisteredDataSource implements IFMFTableListener{


    private boolean DSN;
    private String fileLocation="";
    private String password="";
    private String username="";
    
    private String name="";
    private String path="";


    private ArrayList<FMFTable> tables = new ArrayList<FMFTable>();
    private ArrayList<IRDSListener> listeners = new ArrayList<IRDSListener>();

    private int rdsID;
    private int dataType;
    private boolean valid = false;


    public void setDSN(boolean dsn){DSN=dsn;}
    public boolean getDSN(){return DSN;}

    public void setFileName(String fileName){
        fileLocation = fileName;
        
        int pos = fileLocation.lastIndexOf(System.getProperty("file.separator"));
        if ( pos > -1){
            name = fileLocation.substring(pos+1);
            path = fileLocation.substring(0, pos);
        }else{
            name = "";
            path = "";
            fileLocation = "";
        }
        
        
    }

    public void addDataListener(IRDSListener listener){
        if ( listener != null && !listeners.contains(listener) ){ listeners.add(listener); }
    }


    public void removeDataListener (IRDSListener listener){
        if ( listeners.contains(listener) ) {
            listeners.remove(listener);
        }
    }

    public ArrayList<IRDSListener> getAllDataListeners(){
        return listeners;
    }

    public void clearAllDataListeners(){
        listeners.clear();
    }
    

    public String getFileName(){return fileLocation;}

    public void setPassword(String pwd){password = pwd;}
    public String getPassword(){return password;}

    public void setUserName(String usrname){username = usrname;}
    public String getUserName(){return username;}

    public String getAbreviatedName(){return name;}

    public String getPath(){return path;}

    public int getRdsID() {
        return rdsID;
    }

    public void setRdsID(int rdsID) {
        this.rdsID = rdsID;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void addTable (FMFTable t){
        t.addTableListener(this);
        t.setRDS(this);
        tables.add(t);
    }
    
    public FMFTable getTable (String t){
        for (int i=0;i<tables.size();i++){
            if (tables.get(i).getName().equalsIgnoreCase(t)){
                return tables.get(i);
            }
        }
        return null;
    }
    
    public FMFTable[] getTables (){
        if (!tables.isEmpty()){
            FMFTable t[] = new FMFTable[tables.size()];
            tables.toArray(t);
            return t;
        }else{
            return null;
        }
    }
    
    
    public boolean tableExists(FMFTable table){
        if (tables.contains(table)){return true;}else{return false;}
    }
    
    public boolean tableExists(String t){
        FMFTable temp = getTable(t);
        if (temp==null){return false;}else{return true;}
    }

    public void dropTable(FMFTable table, IApplicationInformation ai){
        if (tableExists(table)){tables.remove(table);}
        table.clear();
        table = null;
    }

    public FMFTable clearCache(FMFTable table, IApplicationInformation ai){
        table.clear();

        return table;
    }

    @Override
    public void tableChanged(String tableName, String fieldName) {}

    /**
     * This method only fires the first time that the data in a table becomes dirty
     *
     * @param tableName the string name of the dirty table
     * @param fieldName the string name of the dirty field
     */
    @Override
    public void tableIsDirty(String tableName, String fieldName) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).DataChanged(this, tableName, fieldName);
        }
    }

    void loadFields(FMFTable table){
        table.getAi().getDataAccessFactory().loadFields(table);
    }

    public void loadTable(String table, IApplicationInformation ai, ICallBack cb){
        ai.getDataAccessFactory().loadData(getTable(table), cb);
    }


    public void loadTable(FMFTable table, IApplicationInformation ai, ICallBack cb){
        ai.getDataAccessFactory().loadData(table, cb);
    }

    
    public void loadTableInBackground(String table, IApplicationInformation ai, ICallBack cb){
        ai.getDataAccessFactory().loadDataInBackground(getTable(table), cb);
    }


    public void loadTableInBackground(FMFTable table, IApplicationInformation ai, ICallBack cb){
        ai.getDataAccessFactory().loadDataInBackground(table, cb);
    }


}
