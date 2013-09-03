


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
public class TableInsert implements ITreeUpdate{

    private RegisteredDataSource rds = null;
    private FMFTable table = null;
    private DefaultTreeModel dtm = null;
    private DefaultMutableTreeNode dmtn = null;

    public void alterTree() {
        RegisteredDataSources.getCurrent().addTable( dtm, table, dmtn, rds, TreeCellInfo.TABLE );
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
        return "Tables";
    }

}
