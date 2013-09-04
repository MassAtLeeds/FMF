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

/**
 * Abstract class containing the code to create a menu with the look and feel definied for the application.
 * All main menu objects must extend this class.
 * 
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public abstract class Menu extends JMenu implements IMenu{
    protected IApplicationInformation ai;
    protected String menuName = "Default";
    protected Integer menuMnemonic = null;
    
    
    /** 
     * Creates a new instance of Menu.
     * Should contain a call to the protected setMenuName method,
     * if the menu name is not set it will default to 'Default'.
     * 
     * To set a menu Mnemonic call the protected method setMenuMnemonic 
     * and pass a valid KeyEvent constant as the parameter
     */

    public Menu() {

        //Set the menu name here in the subclass.
        //setMenuName("***Something here***");
        //set menuMnemonic here if required for example
        //setMenuMnemonic(KeyEvent.VK_A);

    }
    
    



    
    public JPopupMenu getPopup(IApplicationInformation a){
        return getMenu(a).getPopupMenu();
    }
    

    
    
    
    public JMenu getMenu(IApplicationInformation a){


        ai = a;

        loadMenuItems();
        
        return this;

    }
    
    
    



    public void loadMenuItems(){
        //Create an array of menu items and set it to be the return of the getMenuItems method.
        //getMenuItems should be overwritten in the subclass.

        JMenuItem mi[] = getMenuItems(ai.getMenuItemFont());

        this.removeAll();
        
        this.setFont(ai.getMenuFont());

        for (int i = 0; i < mi.length;i++){
            if (mi[i] == null){
                this.addSeparator();
            }else{

                this.add(mi[i]);
            }
        }
    }




    
    
    /**
     *Creates an array of JMenuItems to be added to the Menu.
     * @param menuItem is the font to be used for the menu items
     * @return an array of JMenuItems
     */

    protected JMenuItem[] getMenuItems(Font menuItem){

        Object o[][] = getItems();
        int items = 0;
        Integer currentItem = null;
        for(int i = 0;i<o.length;i++){
            if(o[i][5]==null){
                items++;
                currentItem = null;
            }else if( currentItem==null || currentItem != (Integer)o[i][5] ){
                items++;
                currentItem = (Integer)o[i][5];
            }
        }
        JMenuItem[] jm = new JMenuItem[items];
        


        int oi = 0;
        for (int i = 0; i < jm.length; i++){
                
            if (o[oi][0]==null){
                    jm[i]=null;
            }else{
                if (o[oi][5]!=null){
                    int j = oi + 1;
                    int offset = 0;
                    jm[i] = createMenu(o[oi],menuItem);
                    for (; j < o.length; j++){
                        if(o[oi][5]==o[j][5]){
                            jm[i].add(createMenuItem(o[j],menuItem));
                        }else{
                            offset = 1;
                            break;
                        }
                    }
                    oi = j - offset;
                }else{
                    jm[i] = createMenuItem(o[oi],menuItem);
                }

            }
            oi++;
        }
        


        return jm;


    }
    
   
    
    private JMenu createMenu(Object o[], Font MIFont){

        JMenu jm = null;

        jm = new JMenu((String)o[0]);

        if (o[3]!=null){
           jm.setIcon(new ImageIcon((Image)o[3]));
        }

        jm.setFont(MIFont);
        jm.addActionListener((ActionListener)o[1]);
        if (o[2]!=null){
            jm.setMnemonic((Integer)o[2]);
        }

        return jm;
    }
    

    private JMenuItem createMenuItem(Object o[], Font MIFont){

        JMenuItem jm = null;

        jm = new JMenuItem((String)o[0]);

        if (o[3]!=null){
           jm.setIcon(new ImageIcon((Image)o[3]));
        }

        jm.setFont(MIFont);
        jm.addActionListener((ActionListener)o[1]);
        if (o[2]!=null){
            jm.setMnemonic((Integer)o[2]);
        }
        jm.setAccelerator((KeyStroke)o[4]);

        return jm;
    }




    
    
    /**
     * This method must be overwritten to create a working menu. it must return a two dimensional Object array
     * with the second dimension being 6, the first dimension can be the length of the number of menu items required.
     * <ol>
     * <li>The Object array contains the name of the menu item in o[i][0] (leave null to indicate a menu separator to be inserted)</li>
     * 
     * <li>and the object to be bound to the click event in o[i][1] (the object that is to be bound to the click event must implement the ActionListener interface)</li>
     * 
     * <li>keyboard shortcuts for the Mneumonic can be stored in o[i][2] (leave null for no keyboard shortcut)</li>
     *
     * <li>an image to be used as an icon can be stored in o[i][3] (leave null to display no icon)</li>
     * 
     * <li>keyboard shortcuts to be used directly from the keyboard can be stored in o[i][4](leave null for no shortcut)</li>
     * 
     * <li>groups of subitems are created o[i][5] which should contain an integer value remaining constant for each item in a group.
     * The first item in a group is added to the menu each subsequent item becomes a subitem in a submenu.
     * All sub items must be grouped together, if they are not an error will occur. </li>
     * </ol>
     * 
     * @return Two dimensional object array containing the menu items for inclusion in the menu
     */

    protected abstract Object[][] getItems();//{

//        Object[][] o = new Object[0][6];
        
//        return o;

//    }
    
    
    







    
    /**returns the name for the Menu
     * @return Menu name as a String
     */

    protected String getMenuName(){

        return menuName;

    }
    
    







    
    
    /**sets the name for the Menu - this should be called in the constructor or the Menu class
     * @param s is a String containing the menus name
     */

    protected void setMenuName(String s){

        menuName = s;
        this.setName(s);
        this.setText(s);

    }
    
    







    
    
    /**
     * sets the mnemonic for the Menu - this should be called in the constructor of the Menu class
     * @param i is the Integer representing the Key stroke required to open the menu
     */

    protected void setMenuMnemonic(Integer i){

        menuMnemonic = i;
        this.setMnemonic(i);

    }
    
    
    
    
}
