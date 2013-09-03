/*
 * This is the new license...
 * It has been edited
 */
package uk.ac.leeds.mass.fmf.data_management;

import FlatFile.FlatFileDAL;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.framework.DataSourceTree;
import uk.ac.leeds.mass.fmf.shared_objects.FMFFrame;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.ICallBack;
import uk.ac.leeds.mass.fmf.shared_objects.IRDSListener;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.SystemProperties;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class RegisteredDataSources extends FrameworkProcess implements ActionListener, IRDSListener, ICallBack {

    ArrayList<RegisteredDataSource> rdsList = new ArrayList<RegisteredDataSource>();
    private static RegisteredDataSources r;
    private static int action = -1;
    
    /**
     * implimentation of a singleton pattern to ensure that only one route exists
     * for accessing the registered data sources.
     * 
     * @return the current RegisteredDataSources object
     */
    public static synchronized RegisteredDataSources getCurrent() {

        if (r == null) {
            r = new RegisteredDataSources();
        }
        return r;
    }

    public static synchronized RegisteredDataSources getCurrent(int action) {

        if (r == null) {
            r = new RegisteredDataSources();
        }
        
        RegisteredDataSources.action = action;
        
        return r;
    }



    private void loadDataSources() {
        ApplicationDatabase db = ApplicationDatabase.getCurrent();

        int scrIDs[] = db.getAllDataSourceIDs();
        DataAccessFactory daf = (DataAccessFactory)ApplicationInformation.getCurrent().getDataAccessFactory();

        initilise(scrIDs.length);
        
        for (int i = 0; i < scrIDs.length; i++) {
            
            if (cancelled){break;}

            String temp = db.getDatasourceProperty(scrIDs[i], SystemProperties.DB_TYPE);
            int dataSourceType = Integer.parseInt(temp);
            RegisteredDataSource rds = daf.verifyDataSource(dataSourceType, scrIDs[i]);

            rds.setDataType(dataSourceType);
            rds.setRdsID(scrIDs[i]);
            
            addDataSource(rds);
            
            progress++;

        }
        
        if (!cancelled){finished();}

    }
    
    public boolean datasourceUniqueNameExists(String name){
        if (rdsList.isEmpty()){
            return false;
        }else{
            for(int i=0; i<rdsList.size();i++){
                if( name.equalsIgnoreCase(rdsList.get(i).getFileName()) ){
                    return true;
                }
            }
            return false;
        }
    }

    private boolean datasourceExists(RegisteredDataSource rds){
        if (rdsList.isEmpty()){
            return false;
        }else{
            for(int i=0; i<rdsList.size();i++){
                if( rds.getFileName().equals(rdsList.get(i).getFileName()) ||
                        rds.getFileName().equalsIgnoreCase(rdsList.get(i).getFileName()) ){
                    return true;
                }
            }
            return false;
        }
    }
    
    public void addDataSource(RegisteredDataSource rds){
        if (!datasourceExists(rds)){
            rdsList.add(rds);
            rds.addDataListener(this);
            addDataSourceToList(rds);
        }
    }

    private void addDataSourceToList(RegisteredDataSource rds){
        ApplicationInformation ai = ApplicationInformation.getCurrent();
        DataSourceTree dataList = ai.getDataSourceTree();
        
        DefaultTreeModel treeModel = (DefaultTreeModel) dataList.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        dataList.setRootVisible(true);
        
        DefaultMutableTreeNode rdsNode = null;
        
        //if the rds is valid then we can add all the table and fields in it.
        if (rds.isValid()) {
            //create the RDS node
            rdsNode = new DefaultMutableTreeNode(
                    new TreeCellInfo(rds.getAbreviatedName(), TreeCellInfo.RDS, rds));
        } else {
            //create the Invalid RDS node
            rdsNode = new DefaultMutableTreeNode(
                    new TreeCellInfo(rds.getAbreviatedName(), TreeCellInfo.INVALID_RDS, rds));
        }
        
        //add the rds node onto the root.
        treeModel.insertNodeInto(rdsNode, root, root.getChildCount());
        dataList.expandPath(new TreePath(root.getPath()));
        dataList.setRootVisible(false);
        
        //create the properties node
        DefaultMutableTreeNode propertiesFolder = new DefaultMutableTreeNode(
                    new TreeCellInfo("Properties", TreeCellInfo.PROPERTIES_FOLDER, rds));
        
        //add the properties node onto the root.
        treeModel.insertNodeInto(propertiesFolder, rdsNode, rdsNode.getChildCount());
        
        
        //Path
        if (!rds.getFileName().equals("")){
            DefaultMutableTreeNode pathNode = new DefaultMutableTreeNode(
                    new TreeCellInfo("File Name - "+rds.getFileName(), TreeCellInfo.PROPERTY, rds));
            //add the properties node onto the root.
            treeModel.insertNodeInto(pathNode, propertiesFolder, propertiesFolder.getChildCount());
        }

        //Data type
        DataAccessFactory daf = new DataAccessFactory();
        DefaultMutableTreeNode dataTypeNode = new DefaultMutableTreeNode(
                new TreeCellInfo("Data Type - "+daf.getMenuName(rds.getDataType()), TreeCellInfo.PROPERTY,rds));
        //add the properties node onto the root.
        treeModel.insertNodeInto(dataTypeNode, propertiesFolder, propertiesFolder.getChildCount());
        
        //username
        if (!rds.getUserName().equals("")){
            DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(
                    new TreeCellInfo("User Name - "+rds.getUserName(), TreeCellInfo.PROPERTY,rds));
            //add the properties node onto the root.
            treeModel.insertNodeInto(userNode, propertiesFolder, propertiesFolder.getChildCount());
        }     
        
        
        //create the tables folder
        DefaultMutableTreeNode tableFolder = new DefaultMutableTreeNode(
                    new TreeCellInfo("Tables", TreeCellInfo.TABLE_FOLDER, rds));
        //add the tables node onto the root.
        treeModel.insertNodeInto(tableFolder, rdsNode, rdsNode.getChildCount());
        
        if (rds.isValid()){addTablesFields(rds,tableFolder,treeModel);}

    }
    
    
    private void addTablesFields(RegisteredDataSource rds, DefaultMutableTreeNode tableFolder, 
        DefaultTreeModel treeModel){

        AddTables at = new AddTables(rds,tableFolder,treeModel);
        at.setCallBack(this);

        synchronized(this){
            ApplicationInformation.getCurrent().getProcessManager().addProcess(at);
            sleep();
        }


    }


    void addTable(DefaultTreeModel treeModel, FMFTable table,
        DefaultMutableTreeNode tableFolder, RegisteredDataSource rds, int tableDisplayState ){

        //create the node for the table
        DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(
                new TreeCellInfo(table.getName(), tableDisplayState, rds));
        //add it to the tree model
        treeModel.insertNodeInto(tableNode, tableFolder, tableFolder.getChildCount());

        //add a reference to the tree node to the table
        table.setTreeNode(tableNode);

        table.loadFields();

        //cycle through the fields and add them
        for (int f = 0; f < table.getFieldCount(); f++) {
            //create the field node
            DefaultMutableTreeNode fieldNode = null;
            if ( table.isNumeric(f) ) {
                fieldNode = new DefaultMutableTreeNode(
                        new TreeCellInfo(table.getFieldName(f), TreeCellInfo.NUMERIC_FIELD, rds, table.getName()));
            } else {
                fieldNode = new DefaultMutableTreeNode(
                        new TreeCellInfo(table.getFieldName(f), TreeCellInfo.TEXT_FIELD, rds, table.getName()));
            }
            treeModel.insertNodeInto(fieldNode, tableNode, tableNode.getChildCount());
        }

//        table.clear();

    }

    
    public void loadDataList() {
        ApplicationInformation ai = ApplicationInformation.getCurrent();
        DataSourceTree dataList = ai.getDataSourceTree();

        DefaultTreeModel treeModel = (DefaultTreeModel) dataList.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        for (int i = 0; i < rdsList.size(); i++) {
            RegisteredDataSource rds = rdsList.get(i);
            addDataSourceToList(rds);
        }
    }

    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        ApplicationInformation ai = ApplicationInformation.getCurrent();
        DataSourceTree dataList = ai.getDataSourceTree();
        DefaultTreeModel treeModel = (DefaultTreeModel) dataList.getModel();
        
        if (action == DataSourcePopup.REMOVE_DATA_SOURCE){
            TreePath currentSelection = dataList.getSelectionPath();
            TreeCellInfo t = null;
            
            if (currentSelection != null) {
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
                t = (TreeCellInfo)currentNode.getUserObject();
                MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
                if (parent != null && t != null) {

                    //check to see if it is a flat file RDS if it is remove all of the underlying file definitions too.
                    if (t.getRDS().getDataType()==DataAccessFactory.FLAT_FILE){
                        FlatFileDAL.dropAllDataSourceTables(t.getRDS());
                    }
                    rdsList.remove(t.getRDS());
                    int scrID = t.getRDS().getRdsID();
                    ApplicationDatabase.getCurrent().dropDatasource(scrID);
                    
                    treeModel.removeNodeFromParent(currentNode);
                    
                    return;
                }
            }
        }
    }

    synchronized void updateTree(RegisteredDataSource rds, FMFTable table, int event){
        
        TableUpdate update = new TableUpdate(table, event);
        update.setRegisteredDataSource(rds);
        alterTree(update, false);
        
    }


    synchronized void dropTable(RegisteredDataSource rds, FMFTable table){
        TableDrop drop = new TableDrop();
        drop.setRegisteredDataSource(rds);
        drop.setTable(table);
        alterTree(drop, false);

    }


    public synchronized void insertTable(RegisteredDataSource rds, FMFTable table){
        TableInsert insert = new TableInsert();
        insert.setRegisteredDataSource(rds);
        insert.setTable(table);
        alterTree(insert, true);

    }


    synchronized void alterTree(final ITreeUpdate update, final boolean getTablesFolder){

        //this is a really horrible bit of code to fix the flickering of the datasource tree when it is updated
        //first we create an anonymous class in a new thread and run it because we
        //Cannot call invokeAndWait from the event dispatcher thread which sometimes happens if this is called
        //from the main GUI.
        Thread t = new Thread() {

            //this is the new method run() in the annonymous inner thread class
            @Override
            public void run() {

                //we then wrap the call to invokeAndWait in a try catch block and call that from this thread
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {

                            ApplicationInformation ai = ApplicationInformation.getCurrent();
                            DataSourceTree dataList = ai.getDataSourceTree();
                            DefaultTreeModel dtm = (DefaultTreeModel) dataList.getModel();

                            dataList.setShouldRepaint(false);

                            if (getTablesFolder){
                                //get the root node
                                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) dtm.getRoot();

                                DefaultMutableTreeNode rdsNode = (DefaultMutableTreeNode) dtm.getRoot();
                                //find the correct RDS in the root node
                                for (int i = 0; i < rootNode.getChildCount(); i++) {
                                    DefaultMutableTreeNode tn = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                                    TreeCellInfo t = (TreeCellInfo) tn.getUserObject();
                                    if ( t.getRDS().equals( update.getRegisteredDataSource() ) ){
                                        rdsNode = tn;
                                    }
                                }
//System.out.println("1");
                                //check and make sure we have found the correct rds node
                                if ( rdsNode != null ){
                                    //cycle through the folders (properties and tables) to find the tables folder
                                    for (int i = 0; i < rdsNode.getChildCount(); i++) {
                                        DefaultMutableTreeNode tn = (DefaultMutableTreeNode) rdsNode.getChildAt(i);
                                        TreeCellInfo t = (TreeCellInfo) tn.getUserObject();
                                        //we have found the correct tables folder
                                        if ( t.getType() == TreeCellInfo.TABLE_FOLDER ){
                                            update.setDefaultMutableTreeNode(tn);
                                            update.setDefaultTreeModel( dtm );
                                            update.alterTree();
                                        }
                                    }
                                }
//System.out.println("2");
                            }else{
                                update.setDefaultTreeModel( dtm );
                                update.alterTree();
                            }

                            //repaint the tree
                            dataList.setShouldRepaint(true);
                            dataList.repaint();

                        }
                    });
                } catch(Exception e) {
                    e.printStackTrace();
                }

        }
        };
        t.start();

    }
    
    void openTable(){

        TreeCellInfo t = getCurrentSelectedTreeCellInfo();
        openTable(t);

    }

    public void openTable(TreeCellInfo t){

        if( t != null ){
            FMFTable table = t.getRDS().getTable( t.toString() );
            table.loadData(ApplicationInformation.getCurrent(), null);

            if ( !table.isTableOpen() ){
                table.setTableOpen(true);

                TableWindowListener tableWindowListener = new TableWindowListener(table);
                FMFFrame newWindow = ApplicationInformation.getCurrent().getMainPanel( "Table - " + t.toString(), new TableView(table) );
                newWindow.addInternalFrameListener(tableWindowListener);
            }else{
                System.out.println("table" + t.toString() + " is already open or in the process of opening for viewing or editing.");
            }
        }
    }

    void loadTable(){
        
        TreeCellInfo t = getCurrentSelectedTreeCellInfo();
        
        if ( t != null ){
            FMFTable table = t.getRDS().getTable( t.toString() );
            if ( !table.isDataLoaded() ){
                table.clear();
            }
            DataAccessFactory daf = new DataAccessFactory();
            daf.loadData(table, null);
        }
        
    }


    void deleteTable(){

        TreeCellInfo t = getCurrentSelectedTreeCellInfo();

        if ( t != null ){
            FMFTable table = t.getRDS().getTable( t.toString() );
            DataAccessFactory daf = new DataAccessFactory();
            daf.dropTable(table);
        }

    }

  
    
    
    void saveTable(){
        
        TreeCellInfo t = getCurrentSelectedTreeCellInfo();

        if ( t != null ){
            FMFTable table = t.getRDS().getTable( t.toString() );
            if ( table.isDataAltered() | table.isDataInserted() ){
                DataAccessFactory daf = new DataAccessFactory();
                daf.commitInsert(table);
            }
        }
        
    }
    
    
    public TreeCellInfo getCurrentSelectedTreeCellInfo(){
        ApplicationInformation ai = ApplicationInformation.getCurrent();
        DataSourceTree dataList = ai.getDataSourceTree();
        
        TreePath currentSelection = dataList.getSelectionPath();
        TreeCellInfo t = null;

        if (currentSelection != null) {

            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
            t = (TreeCellInfo)currentNode.getUserObject();
            return t;
        }
        return null;
    }

    

    RegisteredDataSource getRDS(FMFTable table){
        for (int i = 0; i < rdsList.size(); i++) {
            if (rdsList.get(i).tableExists(table)){
                return rdsList.get(i);
            }
        }
        return null;
    }
    
    
    @Override
    public void runProcess() {
        setName("Verifying Data Sources");
        loadDataSources();
    }

    @Override
    public void DataChanged(RegisteredDataSource rds, String table, String field) {
//        updateTree(rds, rds.getTable(table), TreeCellInfo.TABLE_ALTERED);
    }
    


    public RegisteredDataSource getRDS(String fileName){
        for (int i = 0; i < rdsList.size(); i++) {
            if ( rdsList.get(i).getFileName().equals(fileName) ){ return rdsList.get(i); }
        }
        return null;
    }

    @Override
    public void callBack() {wakeup();}

    

    
    private void sleep(){
        try {wait();} catch (InterruptedException e) {e.printStackTrace();}
    }

    private void wakeup(){
        synchronized(this){notify();}
    }

    private class AddTables extends FrameworkProcess{

        private RegisteredDataSource rds;
        private DefaultMutableTreeNode tableFolder;
        private DefaultTreeModel treeModel;

        AddTables(RegisteredDataSource rds, DefaultMutableTreeNode tableFolder,
        DefaultTreeModel treeModel){
            this.rds = rds;
            this.tableFolder = tableFolder;
            this.treeModel = treeModel;
        }

        @Override
        public void runProcess() {

            this.setName("Checking "+rds.getAbreviatedName());
            //get the tables and cycle through them
            FMFTable tables[] = rds.getTables();

            if (tables != null) {
                
                this.initilise(tables.length);

                for (int t = 0; t < tables.length; t++) {
                    addTable( treeModel, tables[t], tableFolder, rds, TreeCellInfo.TABLE );
                    this.progress++;
                    if (this.cancelled){break;}
                }
            }

            if(!this.cancelled){this.finished();}
            
        }

    }
}
