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
 *   Contact email: Andy Evans: a.j.evans@leeds.ac.uk
 */
 
package uk.ac.leeds.mass.fmf.microsimulation;


import uk.ac.leeds.mass.fmf.shared_objects.*;
import uk.ac.leeds.mass.fmf.framework.*;
import uk.ac.leeds.mass.fmf.data_management.*;



/**
 * This code joins synthetic populations (Area and microdata ID) back to the microdata so you get one file with everything.
 * This is the process element initialised by DataCombinerPanel. 
 * @version 1.0
 * @author <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class DataCombinerProcess extends FrameworkProcess{
    
	
	/**
	* Name for the table to write to.
	*/
	private String outputTableName = "Combined_Pop";
	
	
	/**
	* Structure for accessing other parts of the application.
	*/
    private IApplicationInformation ai = null;
	
	
	/**
	* RDS (~directory) for synthetic population data.
	*/
    private RegisteredDataSource popRDS = null;
	
	
	/**
	* RDS (~directory) for microdata.
	*/
	private RegisteredDataSource mdRDS = null;
	
	
	/**
	* FMFTable for synthetic population data.
	*/
	private FMFTable popTable = null;
	
	
	/**
	* FMFTable for microdata.
	*/
	private FMFTable mdTable = null;
	
	
	/**
	* Column field for microdata individual IDs.
	*/
	private String personIDField = null;
   
   

   
	/**
	* Constructor just sets the instance variables and name.
	*/
    public DataCombinerProcess(IApplicationInformation ai, FMFTable popTable, FMFTable mdTable, String personIDField) {
		this.ai = ai;
		this.popTable = popTable;
		this.mdTable = mdTable;
		popRDS = popTable.getRDS();
		mdRDS = mdTable.getRDS();
		this.personIDField = personIDField;
        processName = "Running Tool..."; // Inherited.
    }

    

	
	/**
	* Standard toString override.
	* @return name of tool.
	*/
    @Override
    public String toString() {return "Data Combiner";}
    

	
	
	/**
	* The method called by FMF to run the process.
	* In this case, call writeResults().
	*/
    @Override
    public void runProcess() {
	
		writeResults();
		
	}
	
	
	
	
	/**
	* Writes the combined data to a new table, or a pre-existing table if already created.
	* If any columns have the same name it current fills the second with nulls or zeros. 
	*/
	private void writeResults() {
	
		
		int popCols = popTable.getFieldCount();
		int mdCols = mdTable.getFieldCount();
		int popRows = popTable.getRowCount();
		int mdRows = mdTable.getRowCount();

		FMFTable output = null;
		
		// Check if the table has already been created or not.
		if (!popRDS.tableExists(outputTableName)) {
		
			// New table to be added to the set of tables.
			output = new FMFTable(outputTableName,null,null); 
			
			// Add appropriate fields.
			
			for (int i = 0; i < popCols; i++) {
				output.addFieldToNewTable(popTable.getFieldName(i) , popTable.getColumnType(i));
			}
			
			for (int i = 0; i < mdCols; i++) {
				output.addFieldToNewTable(mdTable.getFieldName(i) , mdTable.getColumnType(i));
			}

			
			// Create the table in the underlying file system.
			ai.getDataAccessFactory().createTable(popRDS,output, true);
			
        } else {
		
			// If pre-existing, get the table.
			output = popRDS.getTable(outputTableName);
			
		}
		
		
		// Initialise is used to divide the progress bar.
        // The bar is progressed using "progress++;" and closed with "finished();"
		// Whether the cancel button has been pushed can be checked using the 
		// "cancelled" boolean.
		// The progress variable, cancelled, initilise(), and finished() are inherited.
		// Only really initialised here incase writing is delayed.
        initilise(popRows);
	
	
		// Open the new table and read it to memory.
        output.loadData(ai, null);
		
        // Lock the table for edits.
        output.lock();
	
		
		int popIDCol = 1;
		int mdIDCol = mdTable.getFieldIndex(personIDField); 
		
		//ai.writeToStatusWindow("popIDCol " + popIDCol + " " + popTable.getFieldName(popIDCol) + " " + popTable.getFieldTypeName(popTable.getColumnType(popTable.getFieldName(popIDCol))) , false);
		//ai.writeToStatusWindow("mdIDCol " + mdIDCol + " " + mdTable.getFieldName(mdIDCol) + " " + mdTable.getFieldTypeName(mdTable.getColumnType(mdTable.getFieldName(mdIDCol))) , false);
		
		
		for (int i = 1; i <= popRows; i++) {
		
			output.insertRow(); // Apparently must be called before inserts, but inserts a blank row?
			
			for (int j = 1; j <= mdRows; j++) {
			
			
				if (popTable.getDoubleValue(popIDCol, i).doubleValue() == mdTable.getDoubleValue(mdIDCol,j).doubleValue()) {

					for (int k = 0; k < popCols; k++) {
						output.insertValue(output.getFieldName(k), i, getObject(popTable, k, i));
					}
					for (int k = 0; k < mdCols; k++) {
						output.insertValue(output.getFieldName(k + popCols), i, getObject(mdTable, k, j));
					}
					break;
				}
			}
			
			progress++; 
		}
	
			
	
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
	
	
	

	/**
	* Returns the data in the supplying table as a generic object. 
	* Utilises the appropriate class-specific method to get the data, and casts it to an Object on return.
	*/
	private Object getObject (FMFTable fmfTable, int col, int row) {
	
		int type = fmfTable.getColumnType(col);

		switch (type) {
			case FMFTable.FIELD_TYPE_DOUBLE: 
				return fmfTable.getDoubleValue(col, row);
			case FMFTable.FIELD_TYPE_INT:
				return fmfTable.getIntegerValue(col, row);
			case FMFTable.FIELD_TYPE_STRING :
				return fmfTable.getStringValue(col, row);
			default:
				// FIELD_TYPE_UNDEFINED
				// There is no getObjectValue, but getStringValue runs getString on the object.
				return fmfTable.getStringValue(col, row);
		
		}
			
	}
	
   
}