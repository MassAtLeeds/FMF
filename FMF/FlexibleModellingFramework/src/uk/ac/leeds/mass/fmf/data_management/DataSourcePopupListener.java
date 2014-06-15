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

import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.framework.DataSourceTree;


/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class DataSourcePopupListener implements MouseListener{

    private JPopupMenu popup = null;
    
    public DataSourcePopupListener (){}
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    private void showPopup(MouseEvent e) {
 
        if (e.isPopupTrigger()){

            TreeCellInfo t = null;
            
            int popupType;
            ApplicationInformation a = ApplicationInformation.getCurrent();
            DataSourceTree dataList = a.getDataSourceTree();
            
            int selRow = dataList.getRowForLocation(e.getX(), e.getY());
            if ( selRow != -1 ){
                dataList.setSelectionRow(selRow);
            }

            TreePath selection = dataList.getLeadSelectionPath();
            
            if(selection!=null){
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (selection.getLastPathComponent());
                t = (TreeCellInfo)currentNode.getUserObject();
            }else{
                popupType=-1;
            }

            if (t!=null){popupType = t.getType();}else{popupType=-1;}

            if ((popupType==TreeCellInfo.RDS) || (popupType==TreeCellInfo.INVALID_RDS)){
                //sets the popup to be datasource popup
                popup = new DataSourcePopup(TreeCellInfo.RDS, t.getRDS()).getPopup(a);
            }else if(popupType==TreeCellInfo.TABLE){
                //sets the popup to be a table popup
                popup = new DataSourcePopup(TreeCellInfo.TABLE, t.getRDS()).getPopup(a);
            }else if(popupType==TreeCellInfo.TABLE_CACHED){
                //sets the popup to be a table popup
                popup = new DataSourcePopup(TreeCellInfo.TABLE_CACHED, t.getRDS()).getPopup(a);
            }else if(popupType==TreeCellInfo.TABLE_ALTERED){
                //sets the popup to be a table popup
                popup = new DataSourcePopup(TreeCellInfo.TABLE_ALTERED, t.getRDS()).getPopup(a);
            }else if(popupType==TreeCellInfo.TABLE_FOLDER){
                //sets the popup to be a table popup
                popup = new DataSourcePopup(TreeCellInfo.TABLE_FOLDER, t.getRDS()).getPopup(a);
            }else{
                popup = new DataSourcePopup(-1).getPopup(a);
            }

            if (popup!=null){popup.show(e.getComponent(), e.getX(), e.getY());}
        }
 
    }
    
}
