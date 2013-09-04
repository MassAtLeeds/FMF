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


/*
 * MainScreen.java
 *
 * Created on 09 October 2008, 18:57
 */

package uk.ac.leeds.mass.fmf.framework;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.DefaultListModel;
import javax.swing.JDesktopPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import uk.ac.leeds.mass.fmf.data_management.DataSourcePopupListener;
import uk.ac.leeds.mass.fmf.data_management.OpenTableAction;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import uk.ac.leeds.mass.fmf.shared_objects.FMFPopupListener;
import uk.ac.leeds.mass.fmf.shared_objects.DataSourceHandler;


/**
 *
 * @author  Kirk Harland <k.harland98@leeds.ac.uk>
 * 
 * This is the main application screen
 * 
 */
public class MainScreen extends JPanel{

    private JTextArea reportingArea;
    private JSplitPane splitPane;
    private JSplitPane subSplitPain;
    
    /** Creates new form MainScreen */
    public MainScreen() {

        MainPanelTransferHandler handler = new MainPanelTransferHandler();

        //Sets up the singleton ApplicationInformation.
        ApplicationInformation ai = ApplicationInformation.getCurrent();
        
        
        //Create the main configuration pane
        JDesktopPane mainPanel = new JDesktopPane();
        mainPanel.setBackground(Color.GRAY);
        mainPanel.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        mainPanel.setTransferHandler(new DataSourceHandler(){
            @Override
                public void doAction(TreeCellInfo t){
                    OpenTableAction open = new OpenTableAction(t);
                    open.openTable();
                }

            @Override
                public boolean shouldImport(TreeCellInfo t){
                    if ( t.getType() == TreeCellInfo.TABLE | t.getType() == TreeCellInfo.TABLE_ALTERED |
                            t.getType() == TreeCellInfo.TABLE_CACHED ){
                        return true;
                    }
                    return false;
                }
        });

        JScrollPane mainScrollPanel = new JScrollPane(mainPanel);


        //Set up the text area and make it scrollable but not editable
        reportingArea = new JTextArea();
        reportingArea.setLineWrap(true);
        reportingArea.setEditable(false);
        reportingArea.setBackground(Color.WHITE);
        reportingArea.setForeground(Color.BLACK);
        JScrollPane spReportingArea = new JScrollPane(reportingArea);

	
	
        //Set up the process window
        JPanel progressPanel = new JPanel();
        progressPanel.setVisible(true);
        JScrollPane spProgressPanel = new JScrollPane(progressPanel);
        
        //Set up the data source list
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new TreeCellInfo("Available Data Sources",TreeCellInfo.RDS, null));
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        DataSourceTree dataList = new DataSourceTree(treeModel);
        dataList.setTransferHandler(handler);
        ai.setDataSourceTree(dataList);
        JScrollPane spDataList = new JScrollPane(dataList);
        
        //Set up the window list
        JList windowList = new JList(new DefaultListModel());
        windowList.addListSelectionListener(new MainScreenListener());
        JScrollPane spWindowList = new JScrollPane(windowList);
        
        //create the Tab control
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Processes", spProgressPanel);
        tab.addTab("Windows", spWindowList);
        tab.addTab("Data Sources", spDataList);
        

        //add the context popup window to the status window
        reportingArea.addMouseListener(new FMFPopupListener(new StatusWindowPopup(this).getPopup(ai)));
        
        //add the context popup window to the data source tree
        dataList.addMouseListener(new DataSourcePopupListener());
        
        //Add the screen areas that are controllable to the ai object
        ai.setMainPanel(mainPanel);
        ai.setProgressPanel(progressPanel);
        ai.setReportingPanel(reportingArea);
        ai.setWindowList(windowList);

              
        //Provide minimum sizes for the two components in the split pane.
        Dimension dim = new Dimension(250, 100);
        mainScrollPanel.setMinimumSize(dim);
        tab.setMinimumSize(dim);
        spReportingArea.setMinimumSize(new Dimension(500,0));
        mainScrollPanel.setPreferredSize(new Dimension(
                (int)Toolkit.getDefaultToolkit().getScreenSize().width - 250,
                (int)Toolkit.getDefaultToolkit().getScreenSize().height - 200));
        
        subSplitPain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,mainScrollPanel, tab);
        subSplitPain.setOneTouchExpandable(true);
        subSplitPain.setDividerLocation((int)Toolkit.getDefaultToolkit().getScreenSize().width - 250);
        
        
        //Create a split pane with the two scroll panes in it.
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,subSplitPain, spReportingArea);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation((int)Toolkit.getDefaultToolkit().getScreenSize().height - 200);
        
        ai.setMainScreen(this);
        
    }
    
    public JSplitPane getSplitPane() {
        return splitPane;
    }
    
    
    public void actionPerformed(String action) {
		
		//check to see what action has been performed
		if (action.equals(StatusWindowPopup.CLEAR_ALL)) {
			reportingArea.setText("");
		}else if(action.equals(StatusWindowPopup.COPY)){
			reportingArea.copy();
		}else if (action.equals(StatusWindowPopup.SELECT_ALL)){
			reportingArea.selectAll();
		}
	}
        
}