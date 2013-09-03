/*
 * This is the new license...
 * It has been edited
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
