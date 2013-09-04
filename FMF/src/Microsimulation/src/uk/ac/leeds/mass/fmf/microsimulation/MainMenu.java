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

package uk.ac.leeds.mass.fmf.microsimulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import uk.ac.leeds.mass.fmf.shared_objects.FMFDialog;
import uk.ac.leeds.mass.fmf.shared_objects.FMFFrame;

/**
 *
 * @author geo8kh
 */
public class MainMenu extends uk.ac.leeds.mass.fmf.shared_objects.Menu{


    public MainMenu(){
        setMenuName(JarInfo.MENU_NAME);
    }

    @Override
    protected Object[][] getItems(){
        String s[] = ai.getAllModelNames(JarInfo.MICROSIMULATION_MODEL);
        int configs = s.length;

        String v[] = ai.getAllModelNames(JarInfo.MICROSIMULATION_VALIDATION);
        int vconfigs = v.length;

        Object[][] o = new Object[7 + (configs * 2) + (vconfigs * 2)][6];

        //sets up the microsimulation main menu
        o[0][0] = "New microsimulation model";
        o[0][1] = new ConfigureModel(JarInfo.MICROSIMULATION_MODEL);

        o[1][0] = "Load microsimulation model";
        o[1][1] = null;
        o[1][5] = 1;

        for (int i = 0; i < configs; i++) {
            o[2+i][0] = s[i];
            o[2+i][1] = new LoadModel(s[i], JarInfo.MICROSIMULATION_MODEL);
            o[2+i][5] = 1;
        }

        o[2+configs][0] = "Delete microsimulation model";
        o[2+configs][1] = null;
        o[2+configs][5] = 2;
        
        for (int i = 0; i < configs; i++) {
            o[3+configs+i][0] = s[i];
            o[3+configs+i][1] = new DeleteModel(s[i], JarInfo.MICROSIMULATION_MODEL);
            o[3+configs+i][5] = 2;
        }

        o[3+(configs*2)][0] = null;
        o[3+(configs*2)][1] = null;

        o[4+(configs*2)][0] = "New validation setup";
        o[4+(configs*2)][1] = new ConfigureModel(JarInfo.MICROSIMULATION_VALIDATION);

        o[5+(configs*2)][0] = "Load validation";
        o[5+(configs*2)][1] = null;
        o[5+(configs*2)][5] = 3;

        for (int i = 0; i < vconfigs; i++) {
            o[6+(configs*2)+i][0] = v[i];
            o[6+(configs*2)+i][1] = new LoadModel(v[i], JarInfo.MICROSIMULATION_VALIDATION);
            o[6+(configs*2)+i][5] = 3;
        }

        o[6+(configs*2)+vconfigs][0] = "Delete validation";
        o[6+(configs*2)+vconfigs][1] = null;
        o[6+(configs*2)+vconfigs][5] = 5;

        for (int i = 0; i < vconfigs; i++) {
            o[7+(configs*2)+(vconfigs)+i][0] = v[i];
            o[7+(configs*2)+(vconfigs)+i][1] = new DeleteModel(v[i], JarInfo.MICROSIMULATION_VALIDATION);
            o[7+(configs*2)+(vconfigs)+i][5] = 5;
        }

        return o;
    }






    //nested class listens to the configure model menu option
    class ConfigureModel implements ActionListener{

        private String modelType;

        public ConfigureModel (String modelType){
            this.modelType = modelType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    ConfigurationScreen cs = new ConfigurationScreen(ai, modelType);
                    FMFFrame f = ai.getMainPanel( JarInfo.MENU_NAME, cs );

                    cs.setWindowTitle( f.getTitle() );

                    f = null;
                }
            };

            t.start();
            
        }

    }

    //nested class listens to the configure model menu option
    class DeleteModel implements ActionListener{

        private String name = "";
        private String modelType = "";

        DeleteModel(String modelName, String modelType){
            name = modelName;
            this.modelType = modelType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            int answer = FMFDialog.showInformationDialog(ai, JarInfo.MENU_NAME,
                                    "Do you want to delete "+name+"?",
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION);

            if ( answer == JOptionPane.YES_OPTION ){
                ai.deleteModel(modelType, name);
                ai.refreshMenu(menuName);
            }
        }

    }

    class LoadModel implements ActionListener{

        private String name = "";
        private String modelType = "";

        LoadModel (String modelName,  String modelType){
            name = modelName;
            this.modelType = modelType;
        }

        @Override
        public void actionPerformed(ActionEvent e){

            Thread t = new Thread() {

                @Override
                public void run() {
                    LinkConfiguration lc = new LinkConfiguration();

                    //if the configuration loads ok open the screen.  If it doesn't don't
                    if (lc.loadConfiguration(ai, modelType, name)){

                        ConfigurationScreen cs = new ConfigurationScreen(ai, modelType);
                        cs.loadConfiguration(lc);
                        FMFFrame f = ai.getMainPanel( cs.getWindowTitle(), cs );

                        cs.setWindowTitle( f.getTitle() );

                        f = null;
                    }
                }
            };

            t.start();

        }

    }

}
