/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.awt.Font;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class DisplaySettings {
    Font mf = new Font("SansSerif", Font.PLAIN,12);
    Font mif = new Font("SansSerif", Font.PLAIN,12);




    
    
    /** Creates a new instance of DisplaySettings */

    public DisplaySettings() {

    }
    






    /** Returns the Font to be used for all Menus
     * @return java.awt.Font
     */

    public Font getMenuFont(){

        return mf;

    }








    
    /** Returns the Font to be used for all MenuItems
     * @return java.awt.Font
     */

    public Font getMenuItemFont(){

        return mif;

    }
    

}
