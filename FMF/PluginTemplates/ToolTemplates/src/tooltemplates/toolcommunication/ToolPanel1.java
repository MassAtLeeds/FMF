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
package tooltemplates.toolcommunication;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;


/**
 * This is a template/example for a basic FMF plugin tool GUI that registers to recieve data change alerts, and sends such alerts.
 * This appears when the user selects the "Inter-tool Comm Template 1" menu item.
 * This template gives the code for allowing the user to drag and drop in a data table.
 * This class is identical to ToolPanel2, save for name.
 * @version 1.0
 * @author <A href="http://uk.linkedin.com/pub/kirk-harland/2b/624/5a3">Kirk Harland</A> and <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 */
public class ToolPanel1 extends JPanel implements DataChangeListener, InternalFrameListener {


	/**
	* Structure for data.
	*/
    private FMFTable fmfTable = null;
	
	/**
	* Structure for accessing other parts of the application.
	*/
    private IApplicationInformation ai = null;

	

    /** 
	 * Creates new ToolPanel1.
	 * Registers objects of this class as data change listeners.
     * @param ai current ApplicationInformation object
     */
    public ToolPanel1(IApplicationInformation ai) {
        
		this.ai = ai;
		initGUI();
		StorageSingleton.getInstance().registerDataChangeListener(this);
		fmfTable = StorageSingleton.getInstance().getData(); // Null if no other tools have loaded data.

    }



	
	/**
	* Doesn't actually set an instance variable (but could), instead registers objects of this class with the embedding frame as listeners.
	* Registers objects of this class with the FMFFRame as an InternalFrameListener. This is 
	* so they can be informed when the external frame is closing and deregister as data change listeners.
	* @param embeddingFrame the frame the panel sits within within FMF.
	*/
	public void setEmbeddingFrame (FMFFrame embeddingFrame) {
	
		embeddingFrame.addInternalFrameListener(this);
	
	}
	
	
	
	
    /** 
	 * Only here as a minimal user interface element. 
	 * Just paints the current data table dimensions.
     * @param g ToolPanel graphics context.
     */	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
		g.drawString("Drag a table for dimensions.", 50,50);
			
		if (fmfTable != null) {
			int cols = fmfTable.getFieldCount();
			int rows = fmfTable.getRowCount();
			g.drawString("Cols = " + String.valueOf(cols), 50,65);
			g.drawString("Rows = " + String.valueOf(rows), 50,80);
		}
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

            }



			
			/**
			* Enacts the result of drag and drop.
			* Here we do everything, but in a more usual application one would load data in the 
			* drag and drop, and enact the process in an actionPerformed.
			*/
            @Override
            public void doAction(TreeCellInfo t) {

				String tableName = t.toString();
                FMFTable methodTable = t.getRDS().getTable(tableName);
				
				
				// Load data in the file table into memory so it's available in fmfTable.
				methodTable.loadData(ai, null);
				
				// Add the data to the singleton, and let listener objects (including this one) know it has changed.
				StorageSingleton.getInstance().setData(methodTable);
				StorageSingleton.getInstance().alertDataChangeListeners(); // Of which this is one in this case.
				
			}

        });
		
    }
	
	
	
	
	/**
	* Called by the StorageSingleton.getInstance().alertDataChangeListeners().
	* Gets the data from the singleton when the singleton informs it of changes.
	* Then calls repaint to paint the new data.
	*/
	public void dataChanged() {
	
		fmfTable = StorageSingleton.getInstance().getData();
		repaint();
	
	}
	
	
	
  
	/**
	* Deregisters objects of this class from the DataChangeListeners in the singleton.
	* Does this when the external frame around them closes.
	*/
	public void internalFrameClosing(InternalFrameEvent e) {
		StorageSingleton.getInstance().removeDataChangeListener(this);
	}   
	
	
	
	
	/**
	* Empty method for implementation of InternalFrameListener.
	*/
 	public void internalFrameActivated(InternalFrameEvent e) {}  
	/**
	* Empty method for implementation of InternalFrameListener.
	*/	
	public void internalFrameClosed(InternalFrameEvent e) {}   
	/**
	* Empty method for implementation of InternalFrameListener.
	*/
	public void internalFrameDeactivated(InternalFrameEvent e) {} 
	/**
	* Empty method for implementation of InternalFrameListener.
	*/	
	public void internalFrameDeiconified(InternalFrameEvent e) {} 
	/**
	* Empty method for implementation of InternalFrameListener.
	*/	
	public void internalFrameIconified(InternalFrameEvent e) {}   
	/**
	* Empty method for implementation of InternalFrameListener.
	*/
	public void internalFrameOpened(InternalFrameEvent e) {}   
	
}

   



    
    



