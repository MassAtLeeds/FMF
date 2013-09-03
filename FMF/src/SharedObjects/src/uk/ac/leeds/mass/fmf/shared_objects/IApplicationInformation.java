/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 * 
 * Carries the information around the application to allow remote objects to set up the screen.
 * 
 */



import java.awt.Font;
import java.awt.Image;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public interface IApplicationInformation {

    
    
    //display setting interfaces
    public Font getMenuFont();
    public Font getMenuItemFont();

    //provide access to resources throught the ApplicationInformation object
    public Image getImage(String jarName, String imageName);
    public ResourceBundle getResourceBundle(String jar, String resourceBundleName, Locale locale);
    
    
    //Main screen display controls
    public FMFFrame getMainPanel(String windowTitle,JPanel GUI);
    public void closeMainPanel(String windowTitle);
    public String upDateMainPanelTitle(String oldTitle,String newTitle);
    public JPanel getProgressPanel();
    public JTextArea getReportingPanel();
    public JFrame getMainApplicationFrame();
    public ImageIcon getApplicationIcon();
    public ImageIcon getErrorIcon();
    public void writeToStatusWindow(String s,boolean err);
    public void setWindowSelection(String windowName);
    public void refreshMenu (String menuName);
    public JDesktopPane getMainPanel();


    //System property methods
    public void setProperty(String property, String value);
    public String getProperty(String property);

    //data access methods
    //get the data access factory
    public IDataAccessFactory getDataAccessFactory();
    public RegisteredDataSource getRDSfromFileName(String fileName);
    public String getDataDir();
    public void setDataDir(String dir);


    //configuration property methods for individual models
    public void deleteModel(String modelType, String modelName);
    public String[] getAllModelNames(String modelType);
    public String[] getAllModelProperty(String modelType, String modelName);
    public String getModelProperty(String modelType, String modelName, String property);
    public void setModelProperty(String modelType, String modelName, String property, String value, boolean unique);
    public boolean modelExists(String modelType, String modelName);
    



    //Process control objects.
    public IProcessManager getProcessManager();
}
