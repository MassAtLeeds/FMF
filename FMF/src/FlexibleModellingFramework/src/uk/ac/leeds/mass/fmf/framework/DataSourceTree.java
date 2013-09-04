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

package uk.ac.leeds.mass.fmf.framework;

import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class DataSourceTree extends JTree{

    private boolean shouldRepaint = true;
    
    public DataSourceTree(){
        super();
        initialise();
    }
    public DataSourceTree(Hashtable<?,?> value){
        super(value);
        initialise();
    }
    public DataSourceTree(Object[] value){
        super(value);
        initialise();
    }
    public DataSourceTree(TreeModel newModel){
        super(newModel);
        initialise();
    }
    public DataSourceTree(TreeNode root){
        super(root);
        initialise();
    }
    public DataSourceTree(TreeNode root, boolean asksAllowsChildren){
        super(root, asksAllowsChildren);
        initialise();
    }
    public DataSourceTree(Vector<?> value){
        super(value);
        initialise();
    }

    public boolean isShouldRepaint() {
        return shouldRepaint;
    }

    public void setShouldRepaint(boolean shouldRepaint) {
        this.shouldRepaint = shouldRepaint;
    }
    
    public void repaint(){
        if ( shouldRepaint ){super.repaint();}
    }
    
    public void repaint(long tm){
        if ( shouldRepaint ){super.repaint(tm);}
    }
    
    public void repaint(int x, int y, int width, int height){
        if ( shouldRepaint ){super.repaint(x,y,width,height);}
    }
    
    public void repaint(long tm, int x, int y, int width, int height){
        if ( shouldRepaint ){super.repaint(tm, x, y, width, height);}
    }
    
    private void initialise(){
        DefaultTreeSelectionModel model = new DefaultTreeSelectionModel();
        model.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.setSelectionModel(model);
        this.setDragEnabled(true);
    }
    
}
