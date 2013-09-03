
package uk.ac.leeds.mass.fmf.data_management;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
public interface ITreeUpdate {

    void alterTree();
    void setDefaultTreeModel(DefaultTreeModel dtm);
    void setTable(FMFTable table);
    FMFTable getTable();
    void setDefaultMutableTreeNode(DefaultMutableTreeNode dmtn);
    void setRegisteredDataSource(RegisteredDataSource rds);
    RegisteredDataSource getRegisteredDataSource();
    String getSearchItem();

}
