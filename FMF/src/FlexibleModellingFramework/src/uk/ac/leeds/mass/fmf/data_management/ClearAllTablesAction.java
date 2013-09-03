/*
 * This is the new license...
 * It has been edited
 */
package uk.ac.leeds.mass.fmf.data_management;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.framework.DataSourceTree;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author geo8kh
 */
public class ClearAllTablesAction implements ActionListener, Runnable{

    public ClearAllTablesAction(){}

    @Override
    public void actionPerformed(ActionEvent e) {
        new Thread(this).start();
    }

    @Override
    public void run() {
        ApplicationInformation ai = ApplicationInformation.getCurrent();
        DataSourceTree dataList = ai.getDataSourceTree();

        TreePath currentSelection = dataList.getSelectionPath();
        TreeCellInfo t = null;

        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
        t = (TreeCellInfo)currentNode.getUserObject();

        if ( t != null ){

            FMFTable[] tables = t.getRDS().getTables();

            for (int i = 0; i < tables.length; i++) {
                tables[i].clear();
            }

        }
    }


}
