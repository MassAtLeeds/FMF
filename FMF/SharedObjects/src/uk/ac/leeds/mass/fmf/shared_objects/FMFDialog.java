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
