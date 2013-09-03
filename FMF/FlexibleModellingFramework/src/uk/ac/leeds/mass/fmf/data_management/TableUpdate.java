
package uk.ac.leeds.mass.fmf.data_management;

import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
public class TableUpdate implements ITreeUpdate{

    private RegisteredDataSource rds = null;
    private FMFTable table = null;
    private DefaultTreeModel dtm = null;
    private DefaultMutableTreeNode dmtn = null;

    private String item = "";
    private int event = 0;

    private TableUpdate(){}
    
    public TableUpdate(FMFTable table, int event){
        this.table = table;
        this.event = event;
    }

    public void alterTree() {

        if (table.getTreeNode()!=null){
            TreeCellInfo t = (TreeCellInfo)table.getTreeNode().getUserObject();
            t.setType(event);
        }
    }

    public void setDefaultTreeModel(DefaultTreeModel dtm) {
        this.dtm = dtm;
    }

    public void setTable(FMFTable table) {
        this.table = table;
    }

    public FMFTable getTable() {
        return table;
    }

    public void setDefaultMutableTreeNode(DefaultMutableTreeNode dmtn) {
        this.dmtn = dmtn;
    }

    public void setRegisteredDataSource(RegisteredDataSource rds) {
        this.rds = rds;
    }

    public RegisteredDataSource getRegisteredDataSource() {
        return rds;
    }

    public String getSearchItem(){
        return item;
    }

}
