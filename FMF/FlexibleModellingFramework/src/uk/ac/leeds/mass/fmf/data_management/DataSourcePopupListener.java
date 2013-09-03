/*
 * This is the new license...
 * It has been edited
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
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
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
