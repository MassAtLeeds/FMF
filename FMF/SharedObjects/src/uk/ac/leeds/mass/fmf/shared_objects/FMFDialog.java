/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author geo8kh
 */
public class FMFDialog {



    /**
     * Shows an information dialog of the specified message type and with the specified options
     *
     * @param ai the current ApplicationInformation object
     * @param title String title for the message box
     * @param message String message to be displayed in the main body
     * @param messageType The type of message, a JOptionPane constant
     * @param optionType the options that should be displayed, a JOptionPane constant
     * @return int value relating to a JOptionPane Constant
     */
    public static int showInformationDialog(IApplicationInformation ai, String title,
            String message, int messageType, int optionType){

        JOptionPane option = new JOptionPane(
                message,
                messageType,
                optionType
                );

        JDialog dialog = option.createDialog(ai.getMainPanel(), title);

        dialog.setIconImage(ai.getApplicationIcon().getImage());

        dialog.setVisible(true);

        if (option.getValue()==null){
            return JOptionPane.CANCEL_OPTION;
        }else{
            return ((Integer)option.getValue()).intValue();
        }

    }


}
