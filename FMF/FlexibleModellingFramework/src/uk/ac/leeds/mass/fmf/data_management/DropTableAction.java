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
 * @author Kirk Harland k.harland98@leeds.ac.uk
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

