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

import uk.ac.leeds.mass.fmf.data_management.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.InputBox;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.SystemProperties;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class MSAccessVerifyDataSource extends FrameworkProcess  implements ActionListener{



    private IApplicationInformation ai;
    private RegisteredDataSource rds;
    private boolean b = false;
    
    InputBox input;
    
    //needs to be volatile because more that one thread can set the state
    private volatile boolean waitForResponse = true;


    private MSAccessVerifyDataSource(){}
    
    public MSAccessVerifyDataSource(IApplicationInformation ai, RegisteredDataSource rds){
        this.ai = ai;
        this.rds = rds;
    }
    
    public MSAccessVerifyDataSource(IApplicationInformation ai, int scrID){
        this.ai = ai;
        this.rds = createRDS(scrID);
    }

    public void runProcess(){

        //create an IDAL object and attemp to make a connection to the data source
        MSAccessDAL d = new MSAccessDAL(rds, null);

        //See if the connection was made
        if (d.isConnected()){

            //get a list of all the tables in the data source
            String tables[] = d.getTables();
            if (tables!=null){

                //set up the length of the process and the fields array
                initilise(tables.length);

                //loop through all of the tables and get the fields for each one
                for(int i=0;i<tables.length;i++){
                    FMFTable t = new FMFTable(tables[i], null, null);
                    t.setAi(ai);

                    //update progress
                    progress=i;
                    //if cancelled flag has been set terminate process and return
                    if(cancelled){
                        rds.setValid(false);
                        break;
                    }
            
                    rds.addTable(t);


                }

            }

            //close the connection to the datasource
            if( d.isConnected() ){d.closeConnection();}
            if(!cancelled){rds.setValid(true);}
        }else{
            rds.setValid(false);
            initilise(1);
        }

        if(!cancelled){finished();}

    }

    public RegisteredDataSource getSuccess(){return rds;}
    
    
    
    RegisteredDataSource createRDS(int scrID){
        RegisteredDataSource r =  new RegisteredDataSource();
        ApplicationDatabase db = ApplicationDatabase.getCurrent();

        r.setFileName(db.getDatasourceProperty(scrID, SystemProperties.DB_NAME));
        r.setUserName(db.getDatasourceProperty(scrID, SystemProperties.DB_USER));
        r.setDSN(false);
        String pwd = db.getDatasourceProperty(scrID, SystemProperties.DB_PSWD);
        if (pwd.equals(SystemProperties.DB_PASSWORD_REQUIRED)){
            //cycle round and check that a response has been made before proceeding
            while (waitForResponse){
                //if we are still waiting for user input
                if (waitForResponse){
                    //ask for input
                    requestInput(r.getFileName());
                    //make the thread wait
                    putThreadToSleep();
                }
            }
            pwd = input.getInput();
        }
        r.setPassword(pwd);

        return r;

    }

    private void requestInput(String db){
        input = new InputBox(ai,"Enter password for - ");
        input.isPassword(true);
        input.addMessageLine(db);
        input.setActionListener(this);
        input.setButtonYesText("OK");
        input.setCancelVisible(false);
        input.setNoVisible(false);
        input.popup();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals(InputBox.YES)){
            input.dispose();
            waitForResponse = false;
            wakeThreadUp();
        }
    }

    synchronized void putThreadToSleep(){try{wait();}catch(Exception e){}}
    synchronized void wakeThreadUp(){notify();}
}
