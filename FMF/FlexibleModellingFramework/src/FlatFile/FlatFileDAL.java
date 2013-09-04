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

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.JOptionPane;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.data_management.DataCallBack;
import uk.ac.leeds.mass.fmf.data_management.IThreadWakeUp;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFDialog;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.IProcessManager;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
public class FlatFileDAL implements IThreadWakeUp{

    private RegisteredDataSource rds = null;
    private boolean shouldSleep = false;

    private FlatFileDAL(){}

    public FlatFileDAL(RegisteredDataSource rds){this.rds = rds;}

    /**
     * Creates a File from the passed RegisteredDataSource and checks to see if it exists
     * @param RegisteredDataSource rds from the SharedObjects package
     * @return If the file tested from the rds is valid and exists returns a file object
     * relating to the directory otherwise null is returned.
     */
    public File getFileFromRDS(RegisteredDataSource rds){
        File f = new File(rds.getFileName());

        if ( f.exists() && f.isDirectory() ){ return f; } else { return null; }
    }

    /**
     * Checks to see if the current RegisteredDataSource rds created during construction
     * could be conneced to.
     *
     * @return true if a connection can be made by calling getFileFromRDS using the internally created rds
     * and false if getFileFromRDS returns null.
     */
    public boolean isConnected(){
        File f = getFileFromRDS(rds);

        if ( f != null ){ return true; } else { return false;}

    }

    /**
     * Get a list of all of the tables in the registered datasource.  A table in the registered datasource is
     * defined as a flat file with a corresponding entry in the registration table in the application database.
     */
    public String[] getTables(){

        ApplicationDatabase ad = ApplicationDatabase.getCurrent();

        //get all of the registered flat files associated with this rds object
        ResultSet rs = ad.executeQuery(
                "SELECT DISTINCT " + RegistrationHandler.VALUE + ", " + RegistrationHandler.FILE_ID
                + " FROM " + RegistrationHandler.TABLE_NAME
                + " WHERE " + RegistrationHandler.DATA_SOURCE_ID + " = " + rds.getRdsID()
                + " AND " + RegistrationHandler.PROPERTY + " = '" + RegistrationHandler.FILE_NAME + "'"
                );

        ArrayList<String> allFiles = new ArrayList<String>();

        //cycle through the results if they are not null
        if ( rs != null ){
            try {
                
                while (rs.next()) {
                    //create a file out of the rds path and the name of the flat file.
                    File f = new File(rds.getFileName() + System.getProperty("file.separator") + 
                            FlatFileManager.appendExtention(rds.getFileName(), rs.getString(RegistrationHandler.VALUE)) );

                    //If the file exists in the file system add it to the list of files otherwise delete the entry
                    //from the application persistance database.
                    if (f.exists()) {
                        allFiles.add(rs.getString(RegistrationHandler.VALUE));
                    } else {
                        RegistrationHandler.deleteFMLFile(rs.getInt(RegistrationHandler.FILE_ID));
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        //convert the arraylist to a native array and return it.
        return allFiles.toArray(new String[0]);

    }

    /**
     * This method tests if the table passed as a parameter exists or not.
     * If the underlying file represented by this table exists and the information about the
     * table is recorded in the application database (the table is registered) then this method returns true.
     * If either of these conditions is not true then this method will return false.
     *
     * @param table FMFTable object representing the table to be examined.
     *
     * @return true if the file and information exists otherwise false
     */
    public boolean tableExists(FMFTable table){

        boolean exists = false;
        ApplicationDatabase ad = ApplicationDatabase.getCurrent();

        //get all of the registered flat files associated with this rds object
        ResultSet rs = ad.executeQuery(
                "SELECT DISTINCT " + RegistrationHandler.VALUE + ", " + RegistrationHandler.FILE_ID
                + " FROM " + RegistrationHandler.TABLE_NAME
                + " WHERE " + RegistrationHandler.DATA_SOURCE_ID + " = " + rds.getRdsID()
                + " AND " + RegistrationHandler.PROPERTY + " = '" + RegistrationHandler.FILE_NAME + "'"
                + " AND " + RegistrationHandler.VALUE + " = '" + table.getName() + "'"
                );

        //cycle through the results if they are not null
        if ( rs != null ){
            try {

                while (rs.next()) {
                    //create a file out of the rds path and the name of the flat file.
                    File f = new File(rds.getFileName() + System.getProperty("file.separator") + 
                            FlatFileManager.appendExtention(rds.getFileName(), rs.getString(RegistrationHandler.VALUE)) );

                    //If the file exists in the file system add it to the list of files otherwise delete the entry
                    //from the application persistance database.
                    if (f.exists()) {
                        exists = true;
                    } else {
                        exists = false;
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return exists;
        
    }

    public void loadFields(FMFTable table){
        RegistrationHandler rh = new RegistrationHandler(rds.getRdsID(), table.getName());
        rh.readFMLFile();
        FlatFileResultSet ffrs = new FlatFileResultSet(true);

        try{
            for (int i = 0; i < rh.getFieldNames().size(); i++) {
                int type = rh.getFieldTypes(i);
                switch (type){
                    case FMFTable.FIELD_TYPE_STRING:
                        ffrs.addField(new String[]{""}, rh.getFieldNames(i));
                        break;
                    case FMFTable.FIELD_TYPE_INT:
                        ffrs.addField(new int[]{0}, rh.getFieldNames(i));
                        break;
                    case FMFTable.FIELD_TYPE_DOUBLE:
                        ffrs.addField(new double[]{0.0}, rh.getFieldNames(i));
                        break;
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        table.setResultSet(ffrs, new FlatFileStatement());
    }

    public void loadTable(FMFTable table){
        //load the data
        FlatFileReader reader = new FlatFileReader(rds);
        reader.loadData(table);

    }


    public ArrayList getUniqueValues(String fieldName, FMFTable table){

        ArrayList values = new ArrayList();

        try{
            HashSet set = new HashSet();
            loadTable(table);

            int column = table.getFieldIndex(fieldName);
            int type = table.getColumnType(column);

            table.moveBeforeFirst();
            while(table.hasMoreRows()){
                table.moveToNextRow();
                switch(type){
                    case FMFTable.FIELD_TYPE_STRING:
                        set.add(table.getStringValue(column));
                        break;

                    case FMFTable.FIELD_TYPE_DOUBLE:
                        set.add(table.getDoubleValue(column));
                        break;

                    case FMFTable.FIELD_TYPE_INT:
                        set.add(table.getIntegerValue(column));
                        break;
                }
            }


            //put all of the unique values in the arraylist to be returned
            Collections.addAll(values, set.toArray());

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            return values;
        }

    }

    public void saveData(FMFTable table){
        FlatFileWriter writer = new FlatFileWriter(table);
		writer.setName("Saving to " + table.getName());
        writer.setCallBack(new DataCallBack(this));
		IProcessManager pm = ApplicationInformation.getCurrent().getProcessManager();
        shouldSleep = true;
		pm.addProcess(writer);
        while(shouldSleep){sleep();}
        table.setDataSaving(false);
    }

    public static void dropAllDataSourceTables(RegisteredDataSource rds){
        FMFTable[] tables = rds.getTables();
        int rdsID = rds.getRdsID();
        //check and make sure the data source has some tables!
        if ( tables != null ){
            for (int i = 0; i < tables.length; i++) {
                int fileID = RegistrationHandler.getFileIDFromName(tables[i].getName(), rdsID);
                RegistrationHandler.deleteFMLFile(fileID);
            }
        }
    }

    public boolean dropTable(FMFTable table, boolean deleteFile){
        int fileID = RegistrationHandler.getFileIDFromName(table.getName(), table.getRDS().getRdsID());

        if ( deleteFile ){
            FlatFileWriter ffw = new FlatFileWriter(table);
            ffw.deleteFile();
        }

        RegistrationHandler.deleteFMLFile(fileID);
        return true;
    }

    public boolean dropTable(FMFTable table){
//        int selection = FMFDialog.showInformationDialog (ApplicationInformation.getCurrent(),
//                "Keep the underlying data in file " + table.getName(),
//                "click yes to unregister the file, click no to delete the underlying data file",
//                JOptionPane.QUESTION_MESSAGE,
//                JOptionPane.YES_NO_CANCEL_OPTION);
//
//        if ( selection == JOptionPane.YES_OPTION ){
//            return dropTable(table, false);
//        }else if ( selection == JOptionPane.NO_OPTION ){
//            return dropTable(table, true);
//        }
//
//        //otherwise we don't do anything because the operation was cancelled by the user...
//        return false;
        return dropTable(table, false);
    }

    public void createNewTable(FMFTable table){

        RegistrationHandler rh = new RegistrationHandler(table);
        rh.setHeadersInFirstRow(true);
        rh.setDelimiter(",");
        rh.setTextQualifier(null);
        
        for (int i=0; i < table.getNewTableFieldCount(); i++){
            rh.addField(table.getNewTableFieldName(i), table.getNewTableFieldType(i));
        }

        rh.writeFMLFile();

        loadFields(table);

        FlatFileWriter writer = new FlatFileWriter(table);
        
        writer.runProcess(true);

        rh.setRegistered(true);
        rh.setVerified(true);

    }

    private synchronized void sleep(){
        try {
            if (shouldSleep){
                wait();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public synchronized void wakeUp(){
        shouldSleep = false;
        notifyAll();
    }

}
