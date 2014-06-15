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
 * @author Kirk Harland k.harland98@leeds.ac.uk
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
