/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.data_management;

import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.framework.JarInfo;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class DataSourceTreeCellRenderer extends DefaultTreeCellRenderer {

    private ApplicationInformation ai = ApplicationInformation.getCurrent();
    
    ImageIcon rdsIcon = new ImageIcon(ai.getImage(JarInfo.JAR_NAME, JarInfo.DATASOURCE_ICON));
    ImageIcon invalidRDSIcon = new ImageIcon(ai.getImage(JarInfo.JAR_NAME, JarInfo.INVALID_DATASOURCE_ICON));
    ImageIcon tableIcon = new ImageIcon(ai.getImage(JarInfo.JAR_NAME, JarInfo.TABLE_ICON));
    ImageIcon numericFieldIcon = new ImageIcon(ai.getImage(JarInfo.JAR_NAME, JarInfo.NUMERIC_FIELD_ICON));
    ImageIcon textFieldIcon = new ImageIcon(ai.getImage(JarInfo.JAR_NAME, JarInfo.TEXT_FIELD_ICON));
    ImageIcon tableCachedIcon = new ImageIcon( ai.getImage( JarInfo.JAR_NAME, JarInfo.TABLE_CACHED_ICON ) );
    ImageIcon tableAlteredIcon = new ImageIcon( ai.getImage( JarInfo.JAR_NAME, JarInfo.TABLE_ALTERED_ICON ) );
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
        boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        int type = objectType(value);

        if (type == TreeCellInfo.RDS) {
            setIcon(rdsIcon);
        }else if (type == TreeCellInfo.TABLE) {
            setIcon(tableIcon);
        }else if (type == TreeCellInfo.TEXT_FIELD) {
            setIcon(textFieldIcon);
        }else if (type == TreeCellInfo.INVALID_RDS) {
            this.setForeground(Color.RED);
            setIcon(invalidRDSIcon);
        }else if (type == TreeCellInfo.NUMERIC_FIELD) {
            setIcon(numericFieldIcon);
        }else if ( type == TreeCellInfo.TABLE_CACHED ){
            setIcon( tableCachedIcon );
        }else if ( type == TreeCellInfo.TABLE_ALTERED ){
            setIcon( tableAlteredIcon );
        }

        return this;
    }

    protected int objectType(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        TreeCellInfo nodeInfo = (TreeCellInfo)(node.getUserObject());
        return nodeInfo.getType();

    }
}
