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
