
package FlatFile;

import java.awt.event.*;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author Kirk Harland
 */
public class FlatFileListener implements ActionListener{

    public final static int IMPORT = 1;
    public final static int REGISTER_FILE = 2;

    /**
     * Main source of all info on the application.
     */
	private IApplicationInformation ai;
    private int mode;
    private RegisteredDataSource rds;

    private FlatFileListener(){}

    /**
       * Sets the whole thing rolling.
       * @param a : IApplicationInformation used for getting hold of GUI, files, etc.
      */
	public FlatFileListener (IApplicationInformation a, int mode, RegisteredDataSource rds) {
            ai = a;
            this.mode = mode;
            this.rds = rds;
        }


	/**
	 * Creates the GUI.
     *
     * @param ae : ActionEvent object passed in automatically
     */
    @Override
	public void actionPerformed (ActionEvent ae) {

        switch(mode){
            case IMPORT:
                ai.getMainPanel("Import Data From Flat File",new FlatFileImport(ai));
                break;
            case REGISTER_FILE:
                ai.getMainPanel("Register Flat Files - " + rds.getAbreviatedName(),new RegisterFile(ai, rds));
                break;
        }
            

	}

}
