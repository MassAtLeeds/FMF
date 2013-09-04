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
