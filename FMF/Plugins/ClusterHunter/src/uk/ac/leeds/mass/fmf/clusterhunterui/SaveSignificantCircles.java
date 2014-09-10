/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.fmf.clusterhunterui;

import java.util.ArrayList;
import uk.ac.leeds.mass.cluster.Gamk;
import uk.ac.leeds.mass.cluster.SignificantCircle;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author kirkharland
 */
public class SaveSignificantCircles extends FrameworkProcess{

    private Gamk gam = null;
    private RegisteredDataSource r;
    private String table;
    private IApplicationInformation ai;
    
    
    private SaveSignificantCircles(){}
    
    public SaveSignificantCircles(IApplicationInformation ai, Gamk gam, RegisteredDataSource r, String table){
        this.gam = gam;
        this.r = r;
        this.table = table;
        this.ai = ai;
        
        this.processName = "Save "+table;
    }
    
    @Override
    public void runProcess() {
        //create a new table with the correct table name in the datasource
        FMFTable output = new FMFTable(table,null,null);
        
        //add the fields
        output.addFieldToNewTable("ID", FMFTable.FIELD_TYPE_INT);
        output.addFieldToNewTable("X", FMFTable.FIELD_TYPE_DOUBLE);
        output.addFieldToNewTable("Y", FMFTable.FIELD_TYPE_DOUBLE);
        output.addFieldToNewTable("Radius", FMFTable.FIELD_TYPE_DOUBLE);
        output.addFieldToNewTable("Value", FMFTable.FIELD_TYPE_DOUBLE);
        
        //create the table in the underlying file system
        ai.getDataAccessFactory().createTable(r,output, true);
        
        //get the results set from the clustering algorithm
        ArrayList<SignificantCircle> results = gam.getResults();
        //initialise the size of the process in this case the number 
        //of records to be writen to the table
        initilise( results.size() );
        
        //open the new table and read it to memory
        output.loadData(ai, null);
        //lock the table for edits
        output.lock();
        
        //cycle through the results set
        for (SignificantCircle significantCircle : results) {
            
            //insert the correct value from the resultset into the
            //relevant field in the new table
            output.insertValue("ID", significantCircle.getID());
            output.insertValue("X", significantCircle.getX());
            output.insertValue("Y", significantCircle.getY());
            output.insertValue("Radius", significantCircle.getRadius());
            output.insertValue("Value", significantCircle.getValue());
            
            //insert the new row
            output.insertRow();

            //increment progress
            progress++;
            //check to see if the user has cancelled the process
            //if they have break out of the loop and allow the process to finish
            if (cancelled){break;}
        }
        
        //commit the inserts
        output.commitInserts();
        //unlock the table
        output.unlock();
        //clear the table
        output.clear();

        //if the process has not been cancelled mark it as finished
        if(!cancelled){finished();}

    }
    
}
