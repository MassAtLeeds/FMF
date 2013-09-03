/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 * 
 * This is a window listener attached to the Main Screen window of the application. 
 * Allows any tidying up of datasources etc. to be done before the application closes.
 * 
 */

//java imports
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;


public class MainScreenListener implements WindowListener, ListSelectionListener{
	public void windowOpened(WindowEvent e){}


	public void windowClosing(WindowEvent e){

            //code for tidying up should go here
            ApplicationDatabase db = ApplicationDatabase.getCurrent();
            db.finalize();

	}


	public void windowClosed(WindowEvent e){}


	public void windowIconified(WindowEvent e){}


	public void windowDeiconified(WindowEvent e){}


	public void windowActivated(WindowEvent e){}


	public void windowDeactivated(WindowEvent e){}

        
        
    public void valueChanged(ListSelectionEvent e) {
        ApplicationInformation.getCurrent().setWindowOrder();
    }

    
}
