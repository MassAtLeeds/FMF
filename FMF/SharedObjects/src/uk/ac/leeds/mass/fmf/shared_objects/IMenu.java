/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 * Interface that must be implemented so that the Menus will load into the framework.
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
import javax.swing.*;
import java.awt.*;



public interface IMenu {
    
    public JMenu getMenu(IApplicationInformation ai);
    
}
