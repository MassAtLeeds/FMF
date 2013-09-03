/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.data_management;

/**
 * This class manages the access to the application's derby database.
 * 
 * 
 * 
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.derby.drda.NetworkServerControl;
import java.net.InetAddress;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;

public class ApplicationDatabase {
    
    //variable used to connect to the application persistence database
    //ensures that there is only ever one of these objects in existance
    private static ApplicationDatabase db;

    private final String DATABASE_NAME = "ApplicationStore";

    private final String Table_Name = "REGISTEREDDATASOURCES";
    private final String System_Properties = "SYSTEMPROPERTIES";

    private ApplicationInformation ai = ApplicationInformation.getCurrent();

    private static Connection conn = null;
    private static Statement stmt = null;


    private ApplicationDatabase (){
        
        NetworkServerControl server;
        try {
            server = new NetworkServerControl(InetAddress.getByName("localhost"), 1527);
            server.start(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        conn = connect();

        if ( conn != null ){
            if (!tableExists(Table_Name)){CreateDataSourceTable();}
            if (!tableExists(System_Properties)){CreateSystemPropertiesTable();}
        }else{
            JOptionPane.showMessageDialog(null, "Unable to connect to the application database",
                    "FMF System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * implimentation of a singleton pattern to ensure that only one route exists
     * for accessing the persistence database.
     * 
     * @return the current connection to the derby database used for persistance
     */
    public static synchronized ApplicationDatabase getCurrent(){

        if (db == null) {
            db = new ApplicationDatabase();
        }
        return db;        

    }
    
    private Connection connect(){

        // set the database directory to be the application route
        String rootDir = System.getProperty("user.home", ".");
        String dbDir = rootDir + "/.fmf/";
        String protocol = "jdbc:derby://localhost:1527/";
        String properties = ";create=true;user=FMF;password=FMF";
        try{

            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            Connection c = DriverManager.getConnection(protocol + dbDir + DATABASE_NAME + properties);

            return c;
        }catch(Exception e){
            ai.writeToStatusWindow("connect: " + e.toString(), true);
            return null;
        }
    }
    
    
    /**
     * Cycles through a list of all of the tables in the database and returns them as a String array.
     * 
     * @return String array of all the tables in the database
     */
    public String[] getAllTables(){

        ArrayList tabs = new ArrayList();
        
        try{
            //test and make sure that their is a valid connection
            if (conn != null){
 
                //get a meta data object for the database
                DatabaseMetaData md = conn.getMetaData();

                //get a results set of the metadata for the table
                ResultSet rs = md.getTables(null,"FMF",null,null);              

                //cycle through the results
                while(rs.next()){
                    //add each table name to the array
                    tabs.add(rs.getString("TABLE_NAME"));
                }
                String s[] = new String[tabs.size()];
                tabs.toArray(s);
                return s;
             }else{
                 return new String[]{""};
             }

        }catch (SQLException eSQL){
            //if an exception has been thrown then return false
            return new String[]{""};
        }
    }
    
    
    
    /**
    * Checks to see if table exists in the current connected database.  The search is case sensitive
     * even though the equalsIgnoreCase method is used to check for equivalence so it is best to
     * call the .toUpperCase() method on the input String when calling this method.
     *
     * (Need to come back and look at why this happens when I have more time)
    *
    * @param tableName Name of the table to check exists.  The search is not case
    * sensitive.
    * @return true if the table exists else false.
    */

    public boolean tableExists (String tableName){

        boolean b = false;
        
        try{
            //test and make sure that their is a valid connection
            if (conn != null){
                //get a meta data object for the database
                DatabaseMetaData md = conn.getMetaData();

                //get a results set of the metadata for the table
                ResultSet rs = md.getTables(null,"FMF",tableName,null);

                //cycle through the results in case more than one row is returned
                while(rs.next()){
                    //check to see if the table name is the same as the one we are looking for
                    //if it is break the while loop and return true
                    if (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME"))){
                        b = true;
                        break;
                    }
                }
            }
        }catch (SQLException eSQL){
            //if an exception has been thrown then return false
            eSQL.printStackTrace();
            return false;
        }

        return b;
    }
    
    
    private void CreateDataSourceTable(){
        
        try{
            getCurrentStatement().execute("CREATE TABLE " + Table_Name + " (SourceID SMALLINT, PropertyName VARCHAR(100), PropertyValue VARCHAR(100))");
        }catch(SQLException e){
            ai.writeToStatusWindow("CreateDataSourceTable: " + e.toString(), true);
        }
    }

    /**
     * Executes a non-return statement such a create table statement.  The statement will be executed in the
     * application database
     *
     * @param statementToExecute this is the string SQL statement to be run
     */
    public void executeStatment(String statementToExecute){
        try{
            getCurrentStatement().execute(statementToExecute);
        }catch(SQLException e){
            System.err.println("Error executing "+statementToExecute);
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String queryToExecute){
        ResultSet rs = null;
        try {
            rs = getCurrentStatement().executeQuery(queryToExecute);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            return rs;
        }
    }

    private void CreateSystemPropertiesTable(){    
        try{
            getCurrentStatement().execute("CREATE TABLE " + System_Properties + " (PropertyName VARCHAR(100), PropertyValue VARCHAR(250))");
        }catch(SQLException e){
            ai.writeToStatusWindow("CreateSystemPropertiesTable: " + e.toString(), true);
        }
    }
    
    /**
     * Sets the system property in the persistance database
     * 
     * @param property : string property name from the SystemProperties class
     * @param value : string value to be set for the property
     */
    public void setSystemProperty(String property,String value){
        try {
            getCurrentStatement().execute("DELETE FROM " + System_Properties + " WHERE PropertyName = '" + property + "'");
            getCurrentStatement().execute("INSERT INTO " + System_Properties +
                    " (PropertyName, PropertyValue) VALUES ('" + property + "', '" + value + "' )");
        } catch (SQLException ex) {
            ai.writeToStatusWindow("setSystemProperty: " + ex.toString(), true);
        }
    }
    
    /**
     * returns a property from the persistance database
     * 
     * @param property : valid property from the SystemProperties class (shared_objects)
     * @return String value for the property if found or an empty string if not found.
     */
    public String getSystemProperty(String property){
        String s = "";
        try {
            ResultSet rs = getCurrentStatement().executeQuery("SELECT PropertyValue FROM " + System_Properties + " WHERE PropertyName = '" + property + "'");
            while(rs.next()){
                s = rs.getString("PropertyValue");
            }
        } catch (SQLException ex) {
            ai.writeToStatusWindow("getSystemProperty: " + ex.toString(), true);
        }finally{
            return s;
        }
        
        
    }
    
    public boolean datasourceExists(String datasource){
        String s = "";
        try {
            ResultSet rs = getCurrentStatement().executeQuery("SELECT PropertyValue FROM " + Table_Name + " WHERE PropertyValue = '" + datasource + "'");
            while(rs.next()){
                s = rs.getString("PropertyValue");
            }
        } catch (SQLException ex) {
            ai.writeToStatusWindow("datasourceExists: " + ex.toString(), true);
        }finally{
            if (s.equals("")){return false;}else{return true;}
        }
    }
  
    public void dropDatasource(String datasource){
        int srcID = -1;
        try {
            ResultSet rs = getCurrentStatement().executeQuery("SELECT SourceID FROM " + Table_Name + " WHERE PropertyValue = '" + datasource + "'");
            while(rs.next()){
                srcID = rs.getInt("SourceID");
            }
        } catch (SQLException ex) {
            ai.writeToStatusWindow("dropDatasource: " + ex.toString(), true);
        }finally{
            if (srcID > -1){dropDatasource(srcID);}
        }
    }
    
    public void dropDatasource(int datasourceID){
        try {
            getCurrentStatement().execute("DELETE FROM " + Table_Name + " WHERE SourceID = " + datasourceID);
        }catch (SQLException ex) {
            ai.writeToStatusWindow("dropDatasource: " + ex.toString(), true);
        }
    }

    public int getNextDatasourceID(){
        int srcID = 0;
        try {
            ResultSet rs = getCurrentStatement().executeQuery("SELECT SourceID FROM " + Table_Name + " ORDER BY SourceID DESC");
            while(rs.next()){
                srcID = rs.getInt("SourceID");
                break;
            }
        } catch (SQLException ex) {
            ai.writeToStatusWindow("getNextDatasourceID: " + ex.toString(), true);
        }finally{
            return srcID+1;
        }
    }
    
    public void setDatasourceProperty(int scrID, String property, String value){
        try {
            getCurrentStatement().execute("INSERT INTO " + Table_Name +
                    " (SourceID, PropertyName, PropertyValue) VALUES ("+scrID+", '" + property + "', '" + value + "' )");
        } catch (SQLException ex) {
            ai.writeToStatusWindow("setDatasourceProperty: " + ex.toString(), true);
        }
    }
    
    public String getDatasourceProperty(int scrID, String property){
 
        String s = "";
        try {
            ResultSet rs = getCurrentStatement().executeQuery("SELECT PropertyValue FROM " + Table_Name +
                    " WHERE SourceID = "+ Integer.toString(scrID) + " AND PropertyName = '" + property + "'");
            while(rs.next()){
                s = rs.getString("PropertyValue");
            }
        } catch (SQLException ex) {
            ai.writeToStatusWindow("getDatasourceProperty: " + ex.toString(), true);
        }finally{
            return s;
        }
    }
    
    int[] getAllDataSourceIDs(){
        ArrayList<Integer> srcID = new ArrayList<Integer>();
        try {
            ResultSet rs = getCurrentStatement().executeQuery("SELECT DISTINCT SourceID FROM " + Table_Name + " ORDER BY SourceID ASC");
            while(rs.next()){
                srcID.add(new Integer(rs.getInt("SourceID")));                
            }
        } catch (SQLException ex) {
            ai.writeToStatusWindow("getAllDataSourceIDs: " + ex.toString(), true);
        }finally{
            int ret[] = new int[srcID.size()];
            for (int i=0;i<ret.length;i++){ret[i] = srcID.get(i).intValue();}
            return ret;
        }
    }


    private Statement getCurrentStatement(){

        if ( conn != null ){
            if ( stmt == null ){
                try {
                    return conn.createStatement();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }else{
                return stmt;
            }
        }else{
            return null;
        }
    }









    private int addModel(String modelType, String modelName){
        int srcID = 0;
        try {
            ResultSet rs = getCurrentStatement().executeQuery(
                    "SELECT ModelID FROM " + modelType.toUpperCase() + "MODELS ORDER BY ModelID DESC");

            while(rs.next()){
                srcID = rs.getInt("ModelID");
                break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            srcID++;
            try {
                getCurrentStatement().execute("INSERT INTO " + modelType.toUpperCase() + "MODELS " +
                        " (ModelName, ModelID) VALUES ('" + modelName + "', " +
                        Integer.toString(srcID) + ")");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return srcID;
        }
    }


    private int getModelID(String modelType, String modelName){
        int srcID = -1;
        try{
            ResultSet rs = getCurrentStatement().executeQuery(
                    "SELECT ModelID FROM " + modelType.toUpperCase() + "MODELS WHERE ModelName = '" + modelName + "'" );
            while(rs.next()){
                srcID = rs.getInt("ModelID");
                break;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return srcID;
    }


    private void createModelTables(String modelType){
        
        try{
            if ( !tableExists(modelType.toUpperCase() + "MODELS") ){
                getCurrentStatement().execute("CREATE TABLE " + modelType.toUpperCase() + "MODELS " +
                    "(ModelName VARCHAR(100), ModelID INTEGER)");
            }

            if ( !tableExists(modelType.toUpperCase() + "MODELPROPERTIES") ){
                getCurrentStatement().execute("CREATE TABLE " + modelType.toUpperCase() +
                    "MODELPROPERTIES (ModelID INTEGER, Property VARCHAR(100), Value VARCHAR(200))");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Sets a property in a model table
     *
     * @param modelType : String model type e.g. Microsimulation, SIM, ABM
     * @param modelName : String user given name of the model
     * @param property : string property name from the SystemProperties class
     * @param value : string value to be set for the property
     * @param unique : boolean true if this should be th only property of this type for this model otherwise false
     */
    public void setModelProperty(String modelType, String modelName, String property, String value, boolean unique){
        createModelTables(modelType);
        try {
            int modelID = getModelID(modelType, modelName);
            if ( modelID == -1 ) { modelID = addModel(modelType, modelName); }

            if ( unique ){
                getCurrentStatement().execute(
                        "DELETE FROM " + modelType.toUpperCase() + "MODELPROPERTIES WHERE Property = '" + property +
                        "' AND ModelID = " + Integer.toString(modelID) );
            }
            
            getCurrentStatement().execute("INSERT INTO " + modelType.toUpperCase() + "MODELPROPERTIES " +
                    " (ModelID, Property, Value) VALUES (" + Integer.toString(modelID) + ", '" +
                    property + "', '" + value + "' )");

        } catch (SQLException ex) {
            ai.writeToStatusWindow("setSystemProperty: " + ex.toString(), true);
        }
    }

    /**
     * returns a property from a model
     *
     * @param modelType : String model type e.g. Microsimulation, SIM, ABM
     * @param modelName : String user given name of the model
     * @param property : valid property from the SystemProperties class (shared_objects)
     * @return String value for the property if found or an empty string if not found.
     */
    public String getModelProperty(String modelType, String modelName, String property){
        createModelTables(modelType);
        String s = "";
        try {
            ResultSet rs = getCurrentStatement().executeQuery(
                    "SELECT Property FROM " + modelType.toUpperCase() + "MODELPROPERTIES WHERE Property = '" +
                    property + "' AND ModelID = " + Integer.toString(getModelID( modelType, modelName )) );
            while(rs.next()){
                s = rs.getString("Property");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            return s;
        }

    }


    /**
     * returns all of the model properties for a model
     *
     * @param modelType : String model type e.g. Microsimulation, SIM, ABM
     * @param modelName : String user given name of the model
     * @return String array of all the property values for the specified model. The string is in the form of
     * <property name>property value, so the tokens to use in a string tokenizer are  <>.
     */
    public String[] getAllModelProperty(String modelType, String modelName){
        createModelTables(modelType);
        ArrayList<String> s = new ArrayList<String>();

        try {
            ResultSet rs = getCurrentStatement().executeQuery(
                    "SELECT Property, Value FROM " + modelType.toUpperCase() + "MODELPROPERTIES WHERE " +
                    "ModelID = " + Integer.toString(getModelID( modelType, modelName )) );
            while(rs.next()){
                s.add("<"+rs.getString("Property")+">"+rs.getString("Value"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            String[] sRet = new String[s.size()];
            return s.toArray(sRet);
        }

    }


    public String[] getAllModelNames(String modelType){
        createModelTables(modelType);
        ArrayList<String> s = new ArrayList<String>();
        try {
            ResultSet rs = getCurrentStatement().executeQuery(
                    "SELECT DISTINCT ModelName FROM " + modelType.toUpperCase() + "MODELS");
            while(rs.next()){
                s.add( rs.getString("ModelName") );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            if ( s.isEmpty() ){
                return new String[0];
            }else{
                String[] sRet = new String[s.size()];
                return s.toArray(sRet);
            }
        }
    }


    public void deleteModel(String modelType, String modelName){
        createModelTables(modelType);

        int modelID = getModelID(modelType, modelName);
        try {
            getCurrentStatement().execute("DELETE FROM " + modelType.toUpperCase() +
                    "MODELPROPERTIES WHERE ModelID = " + Integer.toString(modelID));
            getCurrentStatement().execute("DELETE FROM " + modelType.toUpperCase() +
                    "MODELS WHERE ModelID = " + Integer.toString(modelID));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public boolean modelExists(String modelType, String modelName){
        if ( getModelID(modelType, modelName) == -1 ){return false;} else {return true;}
    }














    @Override
    public void finalize(){
         try{
            if (stmt != null){
                stmt.close();
            }

            if ( conn != null ){
                conn.close();
            }
        }catch (SQLException sqlExc){
            ai.writeToStatusWindow("finalize: " + sqlExc.toString(), true);
        }finally{
            try {
                super.finalize();
            } catch (Throwable ex) {
                ai.writeToStatusWindow(ex.toString(), true);
            }
        }
    }
    
}
