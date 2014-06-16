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
package tooltemplates.tooltemplate;

import javax.swing.*;
import java.awt.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;


/**
 * This is a template/example for a basic FMF plugin tool GUI.
 * This appears when the user selects the tool menu item.
 * This template gives the code for allowing the user to drag and drop in a data table.
 * @version 1.0
 * @author <A href="http://uk.linkedin.com/pub/kirk-harland/2b/624/5a3">Kirk Harland</A> and <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class ToolPanel extends JPanel {


	/**
	* Structure for data.
	*/
    private FMFTable fmfTable = null;
	
	/**
	* Structure for accessing other parts of the application.
	*/
    private IApplicationInformation ai = null;

	

   
    /** 
	 * Creates new ToolPanel.
     * @param ai current ApplicationInformation object
     */
    public ToolPanel(IApplicationInformation ai) {
        
		this.ai = ai;
		initGUI();

    }

	
	
	
    /** 
	 * Only here as a minimal user interface element. 
     * @param g ToolPanel graphics context.
     */	
	public void paintComponent(Graphics g) {
            super.paintComponent(g);
			g.drawString("Drag a table in here to print the", 50,50);
			g.drawString("data to the status window and save", 50,65);
			g.drawString("table dimensions to a \'results\' table", 50,80);
			g.drawString("in the selected data source.", 50,95);
	}
	
	
	
	
	/**
	* Minimal GUI setup, but also set up data drag and drop.
	*/
	private void initGUI(){
	
		// Just a bit of minimal GUI setup.
		setPreferredSize(new Dimension(300,300));

		
		// Allows the user to drag and drop data from the 
		// data sources to the tool. This is the preferred method 
		// of connecting the two, as it explicitly defines the 
		// dataset, rather than trying to find it in the mass of
		// potential sources.
		setTransferHandler(new DataSourceHandler(){
		
		
			/**
			* Returns whether to enact the drag and drop or not.
			*/
            @Override
            public boolean shouldImport(TreeCellInfo t) {

				return t.isTable(); // If the dragged object is a table.

				// Alternative options include checking the 
				// GUI element is enabled first:
                // if (!isEnabled()){return false;}
				
				// Or checking the specific type of table:
                // if ( 
				//		t.getType() == TreeCellInfo.TABLE | 
				//		t.getType() == TreeCellInfo.TABLE_ALTERED |
                //      t.getType() == TreeCellInfo.TABLE_CACHED ){
                // 			return true;
                //		}
                // return false;
            }



			
			/**
			* Enacts the result of drag and drop.
			* Here we do everything, but in a more usual application one would load data in the 
			* drag and drop, and enact the process in an actionPerformed.
			*/
            @Override
            public void doAction(TreeCellInfo t) {

				String tableName = t.toString();
                fmfTable = t.getRDS().getTable(tableName);
				
				// Alternative is to get the data by uniqueName and table name/location.
				// But you need the names.
				// fmfTable r = ai.getRDSfromFileName(uniqueName).getTables()[0];
				// fmfTable r = ai.getRDSfromFileName(uniqueName).getTables(tableName);
				// RegisteredDataSources.getCurrent() contains methods for 
				// getting selected data sources, but not all in list?
				
				// Load data in the file table into memory so it's available in fmfTable.
				fmfTable.loadData(ai, null);
				
				// Add a process for the tool. This will make sure that the process has a 
				// progress bar and a cancel button in the processes tab on the screen
        		ai.getProcessManager().addProcess(new ToolProcess(ai, fmfTable));
				
				// Alternative is to add an array of queued processes:
				// FrameworkProcessArray fpa = new FrameworkProcessArray();
				// Add first running process:
				// fpa.addFrameworkProcess(process);
				// Add the second:
				// fpa.addFrameworkProcess(process);
				// Get the process manager from the application object and submit the process array to it 
				// this will start the processes running sequentially in their own threads which will be 
				// monitored and tracked in the processes tab on screen
				// ai.getProcessManager().addProcessArray(fpa);

		
				// If you just want the process to enact once, or think there is potential 
				// conflicts if it runs twice, you may want to close the panel at this point:
				// ai.closeMainPanel(this.getWindowTitle());
				
				
				
			}

        });
		
		
       
    }
	
  
      
}

   



    
    



