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

package uk.ac.leeds.mass.fmf.data_management;

import FlatFile.FlatFileDAL;
import FlatFile.FlatFileListener;
import FlatFile.FlatFileVerifyDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import MSAccess.MSAccessDAL;
import MSAccess.MSAccessVerifyDataSource;
import MSAccess.MSAccessListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.ICallBack;
import uk.ac.leeds.mass.fmf.shared_objects.IDataAccessFactory;
import uk.ac.leeds.mass.fmf.shared_objects.InputBox;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class DataAccessFactory implements ActionListener, IDataAccessFactory{

    public final static int MS_ACCESS = 2;
    public final static int FLAT_FILE = 1;
    
    //this is the count of data types available to the FMF
    //if another driver is added then this should increment by 1
    final static int DATA_TYPE_COUNT = 1;

    private InputBox input = null;
    
    private String newTableName = "";
    private boolean cancelled = false;
    private boolean waitForResponse = true;
    private FMFTable possibleTableToDrop = null;

    String getMenuName(int DataType){
        switch(DataType){
            case MS_ACCESS: 
                return "MS Access";
                
            case FLAT_FILE:
                return "Flat File";

            default:
                return "Unknown data type";
        }
                
    }
    
    ActionListener getMenuAction(int DataType, IApplicationInformation ai){
        switch(DataType){
            case MS_ACCESS: 
                return new MSAccessListener(ai);

            case FLAT_FILE:
                return new FlatFileListener(ai,FlatFileListener.IMPORT, null);
                
            default:
                return null;
        }
    }
    
    RegisteredDataSource verifyDataSource(int DataType, int scrID){
        switch(DataType){
            case MS_ACCESS: 
                MSAccessVerifyDataSource verify = new MSAccessVerifyDataSource(
                        ApplicationInformation.getCurrent(), scrID);
                verify.runProcess();
                return verify.getSuccess();

            case FLAT_FILE:
                FlatFileVerifyDataSource verifyFlatFile = new FlatFileVerifyDataSource(
                        ApplicationInformation.getCurrent(), scrID);
                verifyFlatFile.runProcess();
                return verifyFlatFile.getSuccess();
            default:
                return null;
        }
    }


    @Override
    public ArrayList getUniqueValues(FMFTable table, String fieldName){

        switch(table.getRDS().getDataType()){
            case MS_ACCESS:

                MSAccessDAL msdal = new MSAccessDAL(table.getRDS(), table);
                return msdal.getUniqueValues(fieldName);

            case FLAT_FILE:

                FlatFileDAL ffdal = new FlatFileDAL(table.getRDS());
                return ffdal.getUniqueValues(fieldName, table);

            default:
                return null;
        }

    }

    @Override
    public void loadDataInBackground(final FMFTable table, final ICallBack cb){
        Thread t =  new Thread(){
            @Override
            public void run(){
                loadData(table, cb);
            }
        };
        t.start();
    }


    @Override
    public void loadData(FMFTable table, ICallBack cb){
        loadData(table.getRDS(), table,cb);
    }
    
    private void loadData(RegisteredDataSource rds, FMFTable table, ICallBack cb){


        table.setDataLoading(true);

        switch(rds.getDataType()){

            case MS_ACCESS:

                table.clear();

                MSAccessDAL msdal = new MSAccessDAL(rds, table);
                if ( msdal.tableExists() ){msdal.loadTable(table);}

                msdal = null;

                break;

            case FLAT_FILE:

                FlatFileDAL ffdal = new FlatFileDAL(rds);
                if ( ffdal.tableExists(table) ){
                    ffdal.loadTable(table);
                }
                break;

        }

        table.setDataLoading(false);


        //set up the screen display etc...
        RegisteredDataSources.getCurrent().updateTree(rds, table, TreeCellInfo.TABLE_CACHED);
        if ( cb != null ){cb.callBack();}
    }
    
    
    
    @Override
    public FMFTable clearCache(FMFTable table){

            RegisteredDataSources.getCurrent().updateTree(table.getRDS(), table, TreeCellInfo.TABLE);

            System.runFinalization();
            System.gc();

            return table;

    }
    
    
    
    
    
    @Override
    public void commitInsert(FMFTable table){

        RegisteredDataSource rds = table.getRDS();

        switch(rds.getDataType()){

            case MS_ACCESS:

                MSAccessDAL msdal = new MSAccessDAL(rds,table);
                msdal.saveData(table);
                msdal = null;
                break;

            case FLAT_FILE:

                FlatFileDAL ffdal = new FlatFileDAL(table.getRDS());
                ffdal.saveData(table);
                ffdal = null;
                break;

        }

       //set up the screen display etc...
        RegisteredDataSources.getCurrent().updateTree(rds, table, TreeCellInfo.TABLE_CACHED);


    }

    private void dropTable(FMFTable table, boolean deleteData){

        boolean success = false;

        RegisteredDataSource rds = table.getRDS();

        switch(rds.getDataType()){

            case MS_ACCESS:

                MSAccessDAL msdal = new MSAccessDAL(rds, table);
                if ( msdal.tableExists() ){success = msdal.dropTable();}
                msdal = null;
                break;

            case FLAT_FILE:

                FlatFileDAL ffdal = new FlatFileDAL(table.getRDS());
                if ( ffdal.tableExists(table) ){
                    if( deleteData ){
                        success = ffdal.dropTable(table,true);
                    }else{
                        success = ffdal.dropTable(table);
                    }
                }
                ffdal = null;
                break;

        }

        if (success){
            if ( table.isTableOpen() ){
                    ApplicationInformation.getCurrent().closeMainPanel("Table - " + table.getName());
            }
            //set up the screen display etc...
            RegisteredDataSources.getCurrent().dropTable( rds, table );
            rds.dropTable(table, ApplicationInformation.getCurrent());
        }
    }

    @Override
    public void dropTable(FMFTable table){
        dropTable(table,false);
    }


    
    
    
    @Override
    public void createTable(RegisteredDataSource rds, FMFTable table, boolean overwrite){

        table.setAi(ApplicationInformation.getCurrent());
        possibleTableToDrop = rds.getTable(table.getName());


        if( table.getNewTableFieldCount() > 0 ){

            //check and see if the table already exists
            if ( rds.tableExists(table.getName()) ){
                //if it does but we are setup to drop existing do that
                if ( !overwrite ){

                    //if it does inform user and request solution
                    requestInput(table.getName());
                    //cycle round and check that a response has been made before proceeding
                    while (waitForResponse){
                        //if we are still waiting for user input
                        if (waitForResponse){
                            //make the thread wait
                            putThreadToSleep();
                            //when thread is woken up check response is viable
                            //and we haven't been cancelled
                            if ( !newTableName.equals("") ){table.setName(newTableName);}

                            if((!waitForResponse)&( rds.tableExists( table.getName() ))&(!cancelled)){
                                //open up a new input box
                                requestInput(table.getName());
                                //this will make thread wait again on next cycle
                                waitForResponse = true;
                            }
                        }
                    }
                }else{
                    this.dropTable( rds.getTable(table.getName()), true );
                }
            }

            possibleTableToDrop = null;

            if ( !cancelled ){


                table.setDataAltered( true );

                //actually create the table
                rds.addTable(table);
                createNewTable(table);
//                table.loadData(ApplicationInformation.getCurrent(), null);

                //set up the screen display etc...
                RegisteredDataSources.getCurrent().insertTable(rds, table);
                table.clear();

            }

        }else{
            ApplicationInformation.getCurrent().writeToStatusWindow(table.getName() + " has no fields", false);
        }

    }

    @Override
    public void loadFields(FMFTable table) {
        switch(table.getRDS().getDataType()){

            case MS_ACCESS:

                MSAccessDAL msadal = new MSAccessDAL(table);
                if ( msadal.tableExists() ){
                    msadal.loadFields(table);
                }
                msadal = null;
                break;

            case FLAT_FILE:

                FlatFileDAL ffdal = new FlatFileDAL(table.getRDS());
                if ( ffdal.tableExists(table) ){
                    ffdal.loadFields(table);
                }
                ffdal = null;
                break;

        }
    }


    private void createNewTable(FMFTable table){

        table.setAi(ApplicationInformation.getCurrent());

        switch(table.getRDS().getDataType()){

            case MS_ACCESS:

                MSAccessDAL msdal = new MSAccessDAL(table.getRDS(), table);
                msdal.createNewTable();
                msdal = null;
                break;

            case FLAT_FILE:

                FlatFileDAL ffdal = new FlatFileDAL(table.getRDS());
                ffdal.createNewTable(table);
                ffdal = null;
                break;


        }

        table.clearNewTableInformation();

    }

	synchronized void putThreadToSleep(){try{wait();}catch(Exception e){}}
	synchronized void wakeThreadUp(){notify();}
	
	private void requestInput(String tableName){
		input = new InputBox(ApplicationInformation.getCurrent(),"Table exists - "+tableName);
		input.addMessageLine("click overwrite to replace table "+tableName);
		input.addMessageLine("   or...");
		input.addMessageLine("enter a new name and click new name");
		input.addMessageLine("   or...");
		input.addMessageLine("click cancel to terminate process.");
		input.setActionListener(this);
		input.setButtonYesText("overwrite");
		input.setButtonNoText("new name");
		input.popup();
	}


    @Override
    public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(InputBox.YES)){
			dropTable(possibleTableToDrop);
		}else if (ae.getActionCommand().equals(InputBox.NO)){
			if (!input.getInput().equals("")){newTableName = input.getInput();}
		}else if (ae.getActionCommand().equals(InputBox.CANCEL)){
			cancelled=true;
		}
		
		input.dispose();
		waitForResponse = false;
		wakeThreadUp();
	}



}
