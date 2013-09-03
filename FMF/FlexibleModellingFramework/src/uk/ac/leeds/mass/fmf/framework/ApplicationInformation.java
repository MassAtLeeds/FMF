/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

//java imports
import java.awt.Font;
import java.awt.Image;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import uk.ac.leeds.mass.fmf.shared_objects.*;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.data_management.DataAccessFactory;
import uk.ac.leeds.mass.fmf.data_management.RegisteredDataSources;



//local imports
//import uk.ac.leeds.ccg.modelling.DatabaseManagement.ConnectionManager;


/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 * 
 * This is a singleton class that stores and broadcasts relevant information to all other interested parties in the framework
 * 
 */
public class ApplicationInformation implements IApplicationInformation{
    //static variable ensures that only one of these objects can exist per virtual machine.
    private static ApplicationInformation ai;

    private boolean StatusWindowOpen = false;


    //Private variables for setting the display look    
    private Font mf;
    private Font mif;




    //private variables for controlling the main screen
    private MainScreen mainScreen;
    private JDesktopPane mainPanel;
    private JPanel progressPanel;
    private JTextArea reportingPanel;
    private JFrame mainFrame;
    private JList windowList;
    private DataSourceTree dataSources;




    //private variables holding the configuration settings
    //configChanged is the flag that is set by the system to signal if the application configuration has been changed and needs saving
    //debug controls if the log file is written to
    //load sets whether the application loads only the list of jar files in the config database or all jars in the route directory (Yet to be developed)
    //applicationTitle holds the configuration title for the application
    private boolean configChanged = false;
    private boolean debug = false;
    private boolean load = false;
    private String applicationTitle = "";



    //private hashtable stores all the locally loaded classes
    private Hashtable localClass = new Hashtable();
    private Hashtable localResolvedClass = new Hashtable();


    
    
    
    //private ProcessManager - controls all the process running
    private ProcessManager pm; 

    
    /** Creates a new instance of ApplicationInformation */

    private ApplicationInformation() {

        setVariables();

    }
   







    
    /**This is the method that returns the instance of the singleton instantiated in the static variable */

    public static synchronized ApplicationInformation getCurrent(){

       if (ai == null)
            ai = new ApplicationInformation();
        return ai;        

    }
    



    /**
     * Sets a system property for persistance.
     * <br>
     * Valid system property keys are located in the SystemProperties class in
     * shared_objects.
     * 
     * @param property : key or property name found in SystemProperties
     * @param value : values of the property to be stored
     */
    @Override
    public void setProperty(String property, String value){
        ApplicationDatabase.getCurrent().setSystemProperty(property, value);
    }
    
    
    @Override
    public String getProperty(String property){
        return ApplicationDatabase.getCurrent().getSystemProperty(property);
    }
   







    /**
    *package protected set method to pass in the main screen main panel
    */
    void setMainPanel(JDesktopPane p){
	    mainPanel = p;
    }
    
    
    /**
    *package protected set method to pass in the main screen frame progress panel
    */
    void setProgressPanel(JPanel p){
	    progressPanel = p;
    }
    
    
    
    /**
    *package protected set method to pass in the main screen frame reporting panel
    */
    void setReportingPanel(JTextArea p){
	    reportingPanel = p;
    }
    
    
    void setWindowList(JList l){
        windowList = l;
    }




    
    
    
    

    @Override
    public void refreshMenu (String menuName){

        JMenuBar bar = mainFrame.getJMenuBar();
        JMenu menu = null;
        for (int i = 0; i < bar.getMenuCount(); i++) {
            menu = bar.getMenu(i);
            if ( menu.getName().equals(menuName)  && menu instanceof Menu ){
                Menu m = (Menu)menu ;
                m.loadMenuItems();
            }
        }
    }





    




    /**
    *public method to return the main screen frame main panel
    */
    @Override
    public FMFFrame getMainPanel(String windowTitle, JPanel GUI){
        
          
        if(windowTitle == null || windowTitle.equals("")){
            windowTitle="Window";
        }
        
        windowTitle = countWindows(windowTitle, -1);
        
        FMFFrame newWindow = new FMFFrame(this,windowTitle);
	
        JScrollPane sp = new JScrollPane(GUI);
        newWindow.add(sp);
        
        mainPanel.add(newWindow);
        
        newWindow.pack();
        newWindow.setVisible(true);
        
        return newWindow;
    }
    
    
    @Override
    public void closeMainPanel(final String windowTitle){
        int i = closeWindow(windowTitle);
    }
    
    @Override
    public String upDateMainPanelTitle(String oldTitle, String newTitle){

        if ( !oldTitle.equalsIgnoreCase(newTitle) ){
            String s = this.getUniqueWindowName(newTitle);

            int index = getWindowIndex( oldTitle );
            if ( index > -1 ) {
                DefaultListModel list = (DefaultListModel)windowList.getModel();
                list.setElementAt(s, index);
            }

            FMFFrame f = getWindow( oldTitle );
            if ( f != null ) { f.setTitle(s); }

            return s;
        }

        return oldTitle;

    }
    
    
    
    /**
    *public method to return the main screen frame progress panel
    */
    @Override
    public JPanel getProgressPanel(){
	    return progressPanel;
    }
    
    /**
    *public method to return the main screen frame reporting text area
    */
    @Override
    public JTextArea getReportingPanel(){
	    return reportingPanel;
    }
    
    
    @Override
    public JFrame getMainApplicationFrame(){
        return mainFrame;
    }
    
    public DataSourceTree getDataSourceTree(){
        return dataSources;
    }
    
    
    
    
    
    /**
    *public method to write messages to the Status Window
    *
    *@param s is a String containing the message for the Status Window
    *@param err is a boolean which will highlight the message as an error message if it is set to true.
    */
    @Override
    public void writeToStatusWindow(String s, boolean err){
	    
	    String msg ="";
	    
            //get the operating system specific line seperator from the System class
            String nextLine = System.getProperty("line.separator");
            
	    if (err){
                if (StatusWindowOpen){
		    msg = "********** ERROR **********" + nextLine + s + nextLine + "******************************" + nextLine;
                }else{
                    writeToLog("","",s);
                }
	    }else{
		msg = s+ nextLine;
	    }

            
            //only report the error to the screen if the screen is open!
            if (StatusWindowOpen){
                reportingPanel.append(msg);
                reportingPanel.setCaretPosition(reportingPanel.getDocument().getLength());
            }
    }


    //get the data access factory
    @Override
    public IDataAccessFactory getDataAccessFactory(){
        return new DataAccessFactory();
    }





    @Override
    public void setModelProperty(String modelType, String modelName, String property, String value, boolean unique){
        ApplicationDatabase.getCurrent().setModelProperty(modelType, modelName, property, value, unique);
    }
    @Override
    public void deleteModel(String modelType, String modelName){
        ApplicationDatabase.getCurrent().deleteModel(modelType, modelName);
    }
    @Override
    public String[] getAllModelNames(String modelType){
        return ApplicationDatabase.getCurrent().getAllModelNames(modelType);
    }
    @Override
    public String[] getAllModelProperty(String modelType, String modelName){
        return ApplicationDatabase.getCurrent().getAllModelProperty(modelType, modelName);
    }
    @Override
    public String getModelProperty(String modelType, String modelName, String property){
        return ApplicationDatabase.getCurrent().getModelProperty(modelType, modelName, property);
    }
    @Override
    public boolean modelExists(String modelType, String modelName){
        return ApplicationDatabase.getCurrent().modelExists(modelType, modelName);
    }















    
    /** returns the menu font for the application*/

    @Override
    public Font getMenuFont(){

        return mf;

    }
    









    
    /** returns the menu item font for the application */

    @Override
    public Font getMenuItemFont(){

        return mif;

    }
 




    /** get the process manager*/
    @Override
    public IProcessManager getProcessManager(){
	    if (pm==null){pm=new ProcessManager((IApplicationInformation) this);}
	    return (IProcessManager) pm;
    }

    
    
    
    
    
    
    /**
     * get the registered data source from the file name
     * @param uniqueName the unique name of the data source (is the rds.getFileName)
     * @return the registered data source at the file name location or null if no rds 
     * exists with that file name
     */
    @Override
    public RegisteredDataSource getRDSfromFileName(String uniqueName){
        return RegisteredDataSources.getCurrent().getRDS(uniqueName);
    }

    
    /**get the current home data directory*/
    @Override
    public String getDataDir(){
	    return ApplicationDatabase.getCurrent().getSystemProperty(SystemProperties.CURRENT_DATA_DIR);
    }



    /**set the current home data directory*/
    @Override
    public void setDataDir(String dir){
        ApplicationDatabase.getCurrent().setSystemProperty(SystemProperties.CURRENT_DATA_DIR, dir);
    }
    
    
    
    
    @Override
    public ImageIcon getApplicationIcon(){
        return new ImageIcon(getImage(JarInfo.JAR_NAME,JarInfo.APPLICATION_ICON_IMAGE));
    }

    @Override
    public ImageIcon getErrorIcon(){
        return new ImageIcon(getImage(JarInfo.JAR_NAME,JarInfo.ERROR_ICON_IMAGE));
    }
    
    @Override
    public Image getImage(String jarName, String imageName){
        return ResourceExtractor.getCurrent().getImage(jarName, imageName);
    }
    
    @Override
    public ResourceBundle getResourceBundle(String jar, String resourceBundleName, Locale locale) {
        return ResourceExtractor.getCurrent().getResourceBundle(jar, resourceBundleName, locale);
    }
    
    @Override
    public void setWindowSelection(String windowName){
        windowList.setSelectedValue(windowName, true);
    }
    
/*********************************** THE METHODS BELOW THIS POINT ARE ONLY AVAILABLE TO THE FRAMEWORK *******************************/


    
    void printToStatusWindow(String s){
        if (StatusWindowOpen){
            reportingPanel.append(s);
            reportingPanel.setCaretPosition(reportingPanel.getDocument().getLength());
        }
    }
    

    /**
    *package protected set method to pass in the main screen frame
    */

    void setMainScreen(MainScreen f) {

	mainScreen = f;

    }


    /**
    *public method to return the main screen frame
     * @return the current main screen object
     */

    MainScreen getMainScreen() {

	return mainScreen;

    }
    
    
    
    void setMainApplicationFrame(JFrame f){
        mainFrame = f;
    }

    
    void setDataSourceTree(DataSourceTree t){
        dataSources = t;
    }
    

    void setWindowOrder(){
        if (!windowList.isSelectionEmpty()){
            DefaultListModel list = (DefaultListModel)windowList.getModel();
            JInternalFrame[] frames = mainPanel.getAllFrames();
            for(int i =0; i<frames.length;i++){
                if(frames[i].getTitle().equals(list.getElementAt(windowList.getSelectedIndex()).toString())){
                    frames[i].moveToFront();
                    try{frames[i].setSelected(true);}catch(Exception e){}
                    break;
                }
            }
        }
    }
    
/*********************************** THIS IS THE LOCAL CLASS STORAGE SOURCE USED BY RESOURCE EXTRACTOR AND CLASS LOADER *******************************/




    /**
    *puts an entry into the local storage table
    */

     void enterFile (String name, byte[] data){


        try{

          localClass.put(name,data);

        }catch(Exception e){

           writeToLog("ApplicationInformation","enterFile",e.toString() + " " + name);

        }


     }




    /**
    *retrieves an entry from the local storage table
    */

    byte[] retrieveFile (String name) {

	byte[] b = null;

        try{

          b = (byte[])localClass.get(name);


        }catch(Exception e){

           writeToLog("ApplicationInformation","retrieveFile",e.toString() + " " + name);

        }

        return b;

    }








    /**
    *puts a resolved class entry into the local storage table
    */

     void enterClass (String name, Class c){


        try{

          localResolvedClass.put(name,c);

        }catch(Exception e){

           writeToLog("ApplicationInformation","enterClass",e.toString() + " " + name);

        }


     }




    /**
    *retrieves a resolved class entry from the local storage table
    */

    Class retrieveClass (String name) {

	Class c = null;

        try{

          c = (Class)localResolvedClass.get(name);


        }catch(Exception e){

           writeToLog("ApplicationInformation","retrieveClass",e.toString() + " " + name);
	   c = null;

        }

        return c;

    }










/*********************************** THESE ARE THE CONFIGURATION SETTINGS METHODS USED BY THE INIT FILE *******************************/


    /**
    *sets the configuration changed flag
    */

    void setConfigChanged(boolean b){

        configChanged = b;

    }




    /**
    *gets the configuration changed flag
    */

    boolean getConfigChanged(){

        return configChanged;

    }












    /**
    *sets the debug mode flag
    */

    void setDebugMode(boolean b){

        debug = b;

    }




    /**
    *gets the debug mode flag
    */

    boolean getDebugMode(){

        return debug;

    }












    /**
    *sets the load mode flag if set to true this will make the application load from a logon user name (yet to be developed)
    */

    void setLoadMode(boolean b){

        load = b;

    }




    /**
    *gets the load mode flag
    */

    boolean getLoadMode(){

        return load;

    }













    /**
    *sets the application title
    */

    void setApplicationTitle(String s){

        applicationTitle = s;

    }




    /**
    *gets the application title
    */

    String getApplicationTitle(){

        return applicationTitle;

    }













//    /**
//    *sets the default database
//    */
//
//    void setDatabase(String s){
//
//        database = s;
//
//    }




//    /**
//    *gets the default database
//    */
//
//    public String getDatabase(){
//
//        return database;
//
//    }







//    void initDataDir(String dir){
//	    dataDir = dir;
//    }
    


    
    
    
    
    void setStatusWindowOpen(boolean open){StatusWindowOpen = open;}
    
    

    
    
    
        /**
    * writes out any information to the application log file -- good for debugging!
    */

    private void writeToLog(String className, String methodName, String msg){

	FileManager fileManager = new FileManager();
	fileManager.writeToLog(className, methodName, msg);

    }










    /**
    *clears the current log file
    */

    void clearLog() {

	FileManager fileManager = new FileManager();
        fileManager.clearLogFile();

    }
    
    
    


/************************************************************** private methods ***************************************************/










    //private method for setting up all the variables required by the application

    private void setVariables(){

        DisplaySettings ds = new DisplaySettings();
        
        mf = ds.getMenuFont();
        mif = ds.getMenuItemFont();
	
	//set up the ApplicationInformation instance on the connection manager
//	cm.setAI(this);

    }
    
    
    // private method to add new panels to the windows tab and make sure they have a unique name
    private String countWindows(String windowName, int index){
        
        String s = getUniqueWindowName(windowName);
        
        DefaultListModel list = (DefaultListModel)windowList.getModel();
        
        if (index == -1 && index < list.getSize()){
            list.addElement(s);
        }else{
            list.add(index, s);
        }
        
        return s;
    }


    private String getUniqueWindowName(String windowName){

        int c = 0;
        String s = windowName;
        while(s.contains("[")){
            s = s.replace('[', '(');
        }
        while(s.contains("]")){
            s = s.replace(']', ')');
        }

        DefaultListModel list = (DefaultListModel)windowList.getModel();
        int size = list.getSize();

        //there are items in the list.
        if (size != 0){
            for (int i = 0; i < size; i++){
                String currentWindow = list.getElementAt(i).toString();

                //are the strings a direct match?
                if (s.equalsIgnoreCase(currentWindow)){
                    if (c==0){c++;}
                //are they a match with a suffix?
                }else if (currentWindow.length() > (s.length()+3)){
                    if ((s.equalsIgnoreCase(currentWindow.substring(0, s.length()))) &&
                            (" - [".equalsIgnoreCase(currentWindow.substring(s.length(), s.length()+4)))){
                        String sInt = "";
                        for(int j = s.length()+5;j<currentWindow.length();j++){
                            if ("]".equals(currentWindow.substring(j-1,j))){
                                break;
                            }else{
                                sInt += currentWindow.substring(j-1,j);
                            }
                        }

                        int windowVal = new Integer(sInt).intValue();
                        if (c <= windowVal){c = windowVal+1;}//else{c++;}

                    }
                }
            }
            if (c > 0){s += " - [" + Integer.toString(c) + "]";}
        }

        return s;
    }





    private int closeWindow(String windowTitle){
        DefaultListModel list = (DefaultListModel)windowList.getModel();
        int index = getWindowIndex(windowTitle);
        if ( index > -1 ){ list.remove(index); }
        FMFFrame f = getWindow(windowTitle);
        f.setFrameClosing(true);
        if ( f != null  && ( !f.isClosed() ) ){ f.doDefaultCloseAction(); }
        return index;
    }


    private int getWindowIndex(String windowTitle){
        DefaultListModel list = (DefaultListModel)windowList.getModel();

        int size = list.getSize();
        int i = 0;
        boolean itemRemoved = false;

        //there are items in the list.
        if (size != 0){
            for (; i < size; i++){
                String currentWindow = list.getElementAt(i).toString();

                //are the strings a direct match?
                if (windowTitle.equalsIgnoreCase(currentWindow)){
                    itemRemoved = true;
                    break;
                }
            }
        }

        if(itemRemoved){return i;} else {return -1;}
    }


    private FMFFrame getWindow(String windowTitle){
        JInternalFrame[] frames = mainPanel.getAllFrames();
        for (int j = 0; j < frames.length; j++) {
            FMFFrame f = null;
            if (frames[j] instanceof FMFFrame){
                f = (FMFFrame)frames[j];
                if ( f.getTitle().equalsIgnoreCase(windowTitle) ){
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * @return the mainPanel
     */
    @Override
    public JDesktopPane getMainPanel() {
        return mainPanel;
    }




}
