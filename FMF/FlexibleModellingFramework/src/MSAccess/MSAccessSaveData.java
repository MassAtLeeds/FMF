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

package MSAccess;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
import java.awt.event.*;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.InputBox;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;


public class MSAccessSaveData extends FrameworkProcess implements ActionListener{

	
	private MSAccessDAL dal = null;
	IApplicationInformation ai = null;
	
	
	//needs to be volatile because more that one thread can set the state
	private volatile boolean waitForResponse = true;
	
	private InputBox input = null;

    private FMFTable table = null;

    private MSAccessSaveData(){}
            
    public MSAccessSaveData (MSAccessDAL dal, FMFTable table){
        this.dal = dal;
        this.table = table;
    }

	public void runProcess(){
//		String s = "";
//        String values = "";
//
//		try{
//
//			Connection con = dal.getConnection();
//			//only attempt to make the table if we have a valid connection to the database
//			if (con != null){
//
//				//get the data array and initialise the progress bar
//				initilise(table.getRowCount());
//
//				//check and see if the table already exists
//				if (dal.tableExists()){
//					//if it does but we are setup to drop existing do that
//					if (dal.replaceAll()){
//						dal.dropTable();
//					//else inform the user that it already exists
//					}else{
//
//						//if it does inform user and request solution
//						requestInput();
//						//cycle round and check that a response has been made before proceeding
//						while (waitForResponse){
//							//if we are still waiting for user input
//							if (waitForResponse){
//								//make the thread wait
//								putThreadToSleep();
//								//when thread is woken up check response is viable
//								//and we haven't been cancelled
//								if((!waitForResponse)&(dal.tableExists())&(!cancelled)){
//									//open up a new input box
//									requestInput();
//									//this will make thread wait again on next cycle
//									waitForResponse = true;
//								}
//							}
//						}
//					}
//				}
//
//				if(!cancelled){
//					//Create a statment object from the connection and create the table
//					Statement stmt = con.createStatement();
//					stmt.execute(dal.createTable());
//					progress=1;
//
//                    FMFField[] fields = table.getFields();
//
//					s = dal.insert();
//					//loop through all of the data and execute insert statements
//					for(int i=0; i<table.getRowCount(); i++){
//
//						//if cancelled flag has been set terminate process and return
//						if(cancelled){break;}
//
//                        values = "";
//                        for (int j = 0; j < fields.length; j++) {
//                            if ( j>0 ){ values += ","; }
//                            if ( fields[j].isNumeric() ){
//                                values += fields[j].getStringValue(i);
//                            }else {
//                                values += "'" + fields[j].getStringValue(i) + "'";
//                            }
//                        }
//
//						stmt.execute( s + values + ");" );
//
//						//update progress
//						progress=i+1;
//					}
//
//					//close the statement
//					stmt.close();
//                    fields = null;
//				}
//
//			}else{
//				initilise(1);
//			}
//			if(!cancelled){finished();}
//
//
//		}catch (SQLException eSQL){
//            eSQL.printStackTrace();
//            System.err.println( s + values + ");");
//        }

	}
	
	void setAI(IApplicationInformation a){ai = a;}
	
	synchronized void putThreadToSleep(){try{wait();}catch(Exception e){}}
	synchronized void wakeThreadUp(){notify();}
	
	private void requestInput(){
		input = new InputBox(ai,"Table exists - "+table.getName());
		input.addMessageLine("click overwrite to replace table "+table.getName());
		input.addMessageLine("   or...");
		input.addMessageLine("enter a new name and click new name");
		input.addMessageLine("   or...");
		input.addMessageLine("click cancel to terminate process.");
		input.setActionListener(this);
		input.setButtonYesText("overwrite");
		input.setButtonNoText("new name");
		input.popup();
	}
	
	/**
	*Implemented interface to listen to action events
         * 
         * @param ae action event automatically passed in when event is fired.
         */
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals(InputBox.YES)){
			dal.dropTable();
		}else if (ae.getActionCommand().equals(InputBox.NO)){
			if (!input.getInput().equals("")){ table.setName(input.getInput()); }
		}else if (ae.getActionCommand().equals(InputBox.CANCEL)){
			cancelled=true;
		}
		
		input.dispose();
		waitForResponse = false;
		wakeThreadUp();
	}
	
}

