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
 
package tooltemplate;


import uk.ac.leeds.mass.fmf.shared_objects.*;
import uk.ac.leeds.mass.fmf.framework.*;
import uk.ac.leeds.mass.fmf.data_management.*;

/**
 * This is a template/example for a basic FMF plugin tool process.
 * @version 1.0
 * @author <A href="http://uk.linkedin.com/pub/kirk-harland/2b/624/5a3">Kirk Harland</A> and <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class ToolProcess extends FrameworkProcess{
    
	
    /**
	* Structure for data.
	*/
    private FMFTable fmfTable = null;
	
	/**
	* Structure for accessing other parts of the application.
	*/
    private IApplicationInformation ai = null;
    
   
    
	/**
	* Constructor just sets the instance variables and name.
	*/
    public ToolProcess(IApplicationInformation ai, FMFTable fmfTable) {
        this.fmfTable = fmfTable;
		this.ai = ai;
        processName = "Running Tool..."; // Inherited.
    }

    

	
	/**
	* Standard toString override.
	* @return name of tool.
	*/
    @Override
    public String toString() {return "Tool";}
    

	
	
	/**
	* The method called by FMF to run the process.
	* In this case, we just print the data to the status window.
	* See ToolPanel for how this is enacted.
	*/
    @Override
    public void runProcess() {
	
		printData();
		writeResults();
		
	}
	
	
	
	
	/**
	* Prints the data from a table dragged into the tool window to the status window.
	* Demonstrates getting data, progress bars, and writing to the status window.
	*/
	private void printData() {
	
	
        int cols = fmfTable.getFieldCount();
        int rows = fmfTable.getRowCount();
		int type = 0;
		
		// Initialise is used to divide the progress bar.
        // The bar is progressed using "progress++;" and closed with "finished();"
		// Whether the cancel button has been pushed can be checked using the 
		// "cancelled" boolean.
		// The progress variable, cancelled, initilise(), and finished() are inherited.
        initilise(rows);
		
		for (int c = 0; c < cols; c++) {

			type = fmfTable.getColumnType(c);

			switch (type) {
				case FMFTable.FIELD_TYPE_DOUBLE: 
					ai.writeToStatusWindow("col " + String.valueOf(c) + "-----------", false);
					for (int r = 1; r <= rows; r++) {
						ai.writeToStatusWindow(String.valueOf(fmfTable.getDoubleValue(c, r)), false);
						progress++; 
						if (cancelled) break;
					}
					break;
				case FMFTable.FIELD_TYPE_INT:
					ai.writeToStatusWindow("col " + String.valueOf(c) + "-----------", false);
					for (int r = 1; r <= rows; r++) {
						ai.writeToStatusWindow(String.valueOf(fmfTable.getIntegerValue(c, r)), false);
						progress++; 
						if (cancelled) break;
					}
					break;
				case FMFTable.FIELD_TYPE_STRING :
					ai.writeToStatusWindow("col " + String.valueOf(c) + "-----------", false);
					for (int r = 1; r <= rows; r++) {
						ai.writeToStatusWindow(fmfTable.getStringValue(c, r), false);
						progress++; 
						if (cancelled) break;
					}
					break;
				default:
					// FIELD_TYPE_UNDEFINED
					// There is no getObjectValue, but getStringValue runs getString on the object.
					ai.writeToStatusWindow("col " + String.valueOf(c) + "-----------", false);
					for (int r = 1; r <= rows; r++) {
						ai.writeToStatusWindow(fmfTable.getStringValue(c, r), false);
						progress++; 
						if (cancelled) break;
					}
			
			}
				
			
		}
    }
	
	
	
	
	/**
	* Writes the data to a new table, or a pre-existing table if already created.
	* Demonstrates creating and writing to a file / table, and progress bars.
	* Writes a new table (if not extant) in the current RegisteredDataSource. 
	* RegisteredDataSources are the collections of tables in the data source window.
	* In this case, as an example, the table will contain information on the dimensions of any table 
	* dragged into the tool window.
	*/
	private void writeResults() {
	
		// Name for the table to write to.
		String outputTableName = "results";
		
		// Current set of tables.
		RegisteredDataSource r = fmfTable.getRDS(); 
		

		FMFTable output = null;
		
		// Check if the table has already been created or not.
		if (!r.tableExists(outputTableName)) {
		
			// New table to be added to the set of tables.
			output = new FMFTable(outputTableName,null,null); 
			
			// Add appropriate fields.
			// Other options include FIELD_TYPE_DOUBLE and FIELD_TYPE_UNDEFINED.
			output.addFieldToNewTable("TABLENAME", FMFTable.FIELD_TYPE_STRING);
			output.addFieldToNewTable("ROWS", FMFTable.FIELD_TYPE_INT);
			output.addFieldToNewTable("COLUMNS", FMFTable.FIELD_TYPE_INT); 

			// Create the table in the underlying file system.
			ai.getDataAccessFactory().createTable(r,output, true);
			
        } else {
		
			// If pre-existing, get the table.
			output = r.getTable(outputTableName);
			
		}
		
        
		
		// Initialise is used to divide the progress bar.
        // The bar is progressed using "progress++;" and closed with "finished();"
		// Whether the cancel button has been pushed can be checked using the 
		// "cancelled" boolean.
		// The progress variable, cancelled, initilise(), and finished() are inherited.
		// Only really initialised here incase writing is delayed.
        initilise(1);
	
	
		// Open the new table and read it to memory.
        output.loadData(ai, null);
		
        // Lock the table for edits.
        output.lock();
	
		// Build the table header.
        output.insertValue("TABLENAME", fmfTable.getName());
		output.insertValue("ROWS", fmfTable.getRowCount());
		output.insertValue("COLUMNS", fmfTable.getFieldCount());
	
		// Insert the header row.
        output.insertRow();

        progress++; 
	
		// Check to see if the user has cancelled the process,
        // if they have, don't commit.
        if (!cancelled){
			//commit the inserts
			output.commitInserts();;
		}
			
		
        // Unlock the table.
        output.unlock();
		
        // Clear the table.
		// This will clear any uncommitted changes as well as clearing various other connections etc.
        output.clear();

        // If the process has not been cancelled, close the progress bar.
        if(!cancelled){finished();}  
	
	}
	

   
}