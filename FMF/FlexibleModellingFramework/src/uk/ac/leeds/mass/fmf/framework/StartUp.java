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

package uk.ac.leeds.mass.fmf.framework;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import uk.ac.leeds.mass.fmf.data_management.DataSourceTreeCellRenderer;
import uk.ac.leeds.mass.fmf.data_management.RegisteredDataSources;

/**
 *
 * This is the main entry point for the application.  
 * The main method is called which then subsequently calls the constructor
 * StartUp which displays the splash screen and loads the application with all 
 * menus etc.
 * 
 * @author Kirk Harland
 * @version 1.0
 */
public class StartUp {

    private JMenuBar MainMenu;
    
    /**
     * Creates a new instance of StartUp.
     * 
     * displays a splash screen and then launches the application.
     */
    
    public StartUp() {

        //set up the application information object.
        ApplicationInformation ai = ApplicationInformation.getCurrent();

        //Create the initalisation class and invoke its main method
        AppInitialisation init = new AppInitialisation();
        init.InitialiseApplication();

        String errMsg = "";

        try{
        //direct error reporting to the log file
            System.setErr( new PrintStream( new FileOutputStream(new FileManager().getLogFile()) ) );
        }catch (FileNotFoundException fnf) {
            errMsg = "Log file cannot be found";
        }
        
        System.err.println("Debug mode - " + ApplicationInformation.getCurrent().getDebugMode());

        //Load the splash screen
        SplashScreen splash = SplashScreen.getSplashScreen(JarInfo.SPLASH_SCREEN_IMAGE);
        
        splash.waitForSplashScreen();

        if ( !errMsg.equals("") ){SplashScreen.setStatus(errMsg);}
        
        //set the look and feel to be consistent with the operating system
         try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch( Exception e ){
            if ( !errMsg.equals("") ){
                SplashScreen.setStatus(e.getMessage());
            }else{
                e.printStackTrace();
            }
        }

        
        //Create and set up the window.
        JFrame frame = new JFrame(ai.getApplicationTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainScreen mainScreen = new MainScreen();
        frame.getContentPane().add(mainScreen.getSplitPane());

        //Ask for window decorations provided by the look and feel.
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        //add our own window listener which fires off any events
        // that are required to be completed as the application closes down
        frame.addWindowListener(new MainScreenListener());
        
        //Initialise window constants
        MainMenu = new JMenuBar();
        frame.setJMenuBar(MainMenu);
        
        //Set the frame icon to an image loaded from a file.
        frame.setIconImage(ResourceExtractor.getCurrent().getImage(JarInfo.JAR_NAME, JarInfo.APPLICATION_ICON_IMAGE));
        
        //Get the Menus available for the MainMenu bar and dynamically add them to the bar if they are not null.
        SplashScreen.setStatus("Loading Menus...");
        MainMenuLoader ml = new MainMenuLoader(ai);
        JMenu allMenus[] = ml.getMenus();
        
        //display load progress on the splash screen
        SplashScreen.setStatus("Creating Menu...");
        for (int i = 0; i < allMenus.length; i++){

            if (allMenus[i] != null) { MainMenu.add(allMenus[i]); }

        }
       
        //take the border off of the main menu
        MainMenu.setBorderPainted(false);
        
        //add the main application window to the application information object
        ai.setMainApplicationFrame(frame);
        
        //set the main window up ready for dispay.
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //set splash screen message
        SplashScreen.setStatus("Loading data sources...");
        
        //load the current data sources
        DataSourceTree dataList = ai.getDataSourceTree();
        dataList.setCellRenderer(new DataSourceTreeCellRenderer());
        DefaultTreeModel treeModel = (DefaultTreeModel)dataList.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel.getRoot();
        dataList.expandPath(new TreePath(root.getPath()));
        dataList.setRootVisible(false);
        RegisteredDataSources rds = RegisteredDataSources.getCurrent();
        ApplicationInformation.getCurrent().getProcessManager().addProcess(rds);
        
        //dispaly the main window
        frame.setVisible(true);
        
        //let the rest of the application know that the status window has loaded 
        //so that errors can be displayed on screen as well as in the log
        ai.setStatusWindowOpen(true);
        
        //redirect the default output streams to the reporting area
        ScreenOutputStream screenOut = new ScreenOutputStream();
        PrintStream ps = new PrintStream(screenOut);
        System.setOut(ps);
        if ( !errMsg.equals("") | ai.getDebugMode() ){System.setErr(ps);}
        
        //dispose of the splash screen
        splash.dispose();
        
        //get the operating system specific line seperator from the System class
        String nextLine = System.getProperty("line.separator");
        
        //build the start string to be displayed in the status window on startup
        String startString = ai.getApplicationTitle()+" Copyright (C) 2013 University of Leeds " + nextLine;
        startString = startString+"This program is licensed under the GNU General Public License version 3 "+
                "(see license.txt or <http://www.gnu.org/licenses/>)." + nextLine + nextLine;
        
        //write the start up string to the application status window
        ai.writeToStatusWindow(startString,false);
       
    } 
    
    
    /**
     * main entry point for the whole application. 
     * 
     * @param args the command line arguments
     */

    public static void main(String[] args) {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {

                    Thread t = new Thread(){
                        @Override
                        public void run(){
                            new StartUp();
                        }
                    };

                    t.start();
                    
                }
            });
        } catch (Exception ex) {}

    } 
    
}
