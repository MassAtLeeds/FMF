/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;





/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */

    class ChangeDataDir implements ActionListener{

        private IApplicationInformation ai;


	/**
	*Creates a new instance of ChangeDataDir
	*/

	public ChangeDataDir(IApplicationInformation a){ai = a;}						



	/**
	*Implimented interface of the ActionListener.
	*Changes the prefered starting point for the data directory.
	*/

    @Override
	public void actionPerformed (ActionEvent ae) {							

		File f = new File(ai.getDataDir());
			
		JFileChooser fc = null;
		if ((f.exists())&&(f.isDirectory())){
			//Create a file chooser at the prefered data directory
			fc = new JFileChooser(f);
		}else{
			//Create a new file chooser at the root
			fc = new JFileChooser();
		}
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
		//In response to a button click:
		int returnVal = fc.showOpenDialog(ai.getMainApplicationFrame());
			
		if(returnVal == JFileChooser.APPROVE_OPTION) {
				
			if(fc.getSelectedFile().isDirectory()){
				String s = fc.getSelectedFile().getPath();
				ai.setDataDir(s);
			}else{
				ai.writeToStatusWindow("A valid directory must be chosen. The data directory has not been changed.", false);
			}

		}


	}												


    }													





/**
 * Listens for the quit option on the application menu drop down to be activated.
 * @author Kirk Harland
 */


class QuitApplication implements ActionListener{

	private IApplicationInformation ai;


	/**
	*Creates a new instance of QuitApplication
	*/

	public QuitApplication(IApplicationInformation a){ai = a;}						//public constructor



	/**
	*Implimented interface of the ActionListener.
	*Terminates all of the database connections and then quits this application.
	*/

    @Override
	public void actionPerformed (ActionEvent ae) {							//Start actionPerformed method

		//put in some code to tidy up any unsaved data changes here
		System.exit(0);

	}
}
