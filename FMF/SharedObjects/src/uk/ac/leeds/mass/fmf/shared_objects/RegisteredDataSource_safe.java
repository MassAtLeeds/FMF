/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import java.util.ArrayList;


/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class RegisteredDataSource_safe implements IFMFTableListener{

    private String uniqueName = "";

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
        name = fileLocation.substring(pos+1);
        path = fileLocation.substring(0, pos);
        
        
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
//TO BE UNCOMMENTED IF ROLLED BACK
//        t.setRDS(this);
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

    public void dropTable(FMFTable table){
        if (tableExists(table)){tables.remove(table);}
//TO BE UNCOMMENTED IF ROLLED BACK
//        table.dropTable();
        table = null;
    }

    public FMFTable clearCache(FMFTable table){
//TO BE UNCOMMENTED IF ROLLED BACK
//        FMFTable t = table.clone();
//        tables.remove(table);
//        table=null;
//        t.addTableListener(this);
//        tables.add(t);
//        return t;
        return null;
    }

    public void tableChanged(String tableName, String fieldName) {}

    /**
     * This method only fires the first time that the data in a table becomes dirty
     *
     * @param tableName the string name of the dirty table
     * @param fieldName the string name of the dirty field
     */
    public void tableIsDirty(String tableName, String fieldName) {
        for (int i = 0; i < listeners.size(); i++) {
//TO BE UNCOMMENTED IF ROLLED BACK
//            listeners.get(i).DataChanged(this, tableName, fieldName);
        }
    }

    /**
     * The unique name is the name given to the datasource within the application
     *
     * @return the uniqueName
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * The unique name is the name given to the data source within the application
     *
     * @param uniqueName the uniqueName to set
     */
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }


}
