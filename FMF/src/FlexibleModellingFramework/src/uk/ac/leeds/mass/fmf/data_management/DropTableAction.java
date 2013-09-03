
package uk.ac.leeds.mass.fmf.data_management;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFDialog;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class DropTableAction implements ActionListener{

    public DropTableAction (){}


    public void actionPerformed(ActionEvent e) {

        
        Thread t = new Thread(){

            @Override
            public void run(){

                try {
                    SwingUtilities.invokeAndWait(new Runnable() {

                        public void run() {

                            TreeCellInfo t = RegisteredDataSources.getCurrent().getCurrentSelectedTreeCellInfo();
                            int answer = FMFDialog.showInformationDialog(ApplicationInformation.getCurrent(),
                                    "Drop Table From " + t.getRDS().getAbreviatedName(),
                                    "Are you sure you want to permanently delete " + t.toString() + "?", 
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION);

                            if ( answer == JOptionPane.YES_OPTION ){
                                RegisteredDataSources.getCurrent().deleteTable();
                            }
                        }

                    });

                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        };
        t.start();

    }

}

