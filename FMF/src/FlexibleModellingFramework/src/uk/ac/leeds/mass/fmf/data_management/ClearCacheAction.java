/*
 * This is the new license...
 * It has been edited
 */


package uk.ac.leeds.mass.fmf.data_management;

import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.framework.DataSourceTree;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author geo8kh
 */
public class ClearCacheAction implements ActionListener, Runnable{
    
    private FMFTable table = null;

    public ClearCacheAction(){}

    public ClearCacheAction(FMFTable table){
        this.table = table;
    }

    public void actionPerformed(ActionEvent e) {
        go();
    }

    public void go(){
        new Thread(this).start();
    }

    @Override
    public void run() {
        ApplicationInformation ai = ApplicationInformation.getCurrent();

        if ( table == null ){
            DataSourceTree dataList = ai.getDataSourceTree();

            TreePath currentSelection = dataList.getSelectionPath();
            TreeCellInfo t = null;

            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
            t = (TreeCellInfo)currentNode.getUserObject();

            table = t.getRDS().getTable( t.toString() );
        }

        if ( table != null ){
            table.clear();
            table = null;
        }

    }

}
