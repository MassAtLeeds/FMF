/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.microsimulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import uk.ac.leeds.mass.fmf.shared_objects.DataSourceHandler;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.InputBox;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author geo8kh
 */
public class LinkConfiguration implements Serializable, ActionListener{


    public final static String PROPERTY_POP_TABLE = "population table";
    public final static String PROPERTY_POP_RDS = "population rds";
    public final static String PROPERTY_POP_ID = "population id";
    public final static String PROPERTY_LINK_TABLE = "link table";
    public final static String PROPERTY_LINK_RDS = "link rds";
    public final static String PROPERTY_POP_FIELD = "population field";
    public final static String PROPERTY_FIELD_VALUE = "link field and pop value";
    public final static String PROPERTY_OUTPUT_RDS = "output rds";
    public final static String PROPERTY_OUTPUT_TABLE = "output table";
    public final static String PROPERTY_POPULATION_TOTAL_LINK_TABLE = "link table for pop total";

    private String linkForPopulationTotal = "";

    private String modelName = "";

    private String populationTableName = "";
    private String populationRDSPath = "";

    private String populationIDField = "";

    private String outputRDS = "";
    private String outputTableGroup = "";

    //these two array lists keep an ordered list of links between the population
    //fields and the names of the tables used for the links
    private ArrayList<String> populationFields = new ArrayList<String>();
    private ArrayList<String> linkTables = new ArrayList<String>();
    private ArrayList<String> linkRDSPath = new ArrayList<String>();

    //these two arraylists contain the individual links between the values from
    //the populations table and the field names from the link table
    private ArrayList<ArrayList>populationValues = new ArrayList<ArrayList>();
    private ArrayList<ArrayList>linkFields = new ArrayList<ArrayList>();

    //these two array lists contain the individual links between each population
    //field value and the corresponding link field.  These are stored in populationValues
    //and linkFields above when a link is saved.
    private ArrayList values = new ArrayList();
    private ArrayList<String> fields  = new ArrayList<String>();

    private boolean cancelled = false;
    private boolean shouldSleep = true;
    private InputBox input;

    private boolean useSeed = false;
    private long seed = 10;

    private void clearArrays(){
        populationFields.clear();
        linkTables.clear();
        linkRDSPath.clear();
        for (int i = 0; i < populationValues.size(); i++) {
            populationValues.get(i).clear();
            linkFields.get(i).clear();
        }
        populationValues.clear();
        linkFields.clear();
        values.clear();
        fields.clear();
    }

    /**
     * Sets the population table name and RDS
     * @param tableName : String name of table
     * @param RDSPath : String unique name of the RDS
     */
    void setPopulationTable(String tableName, String RDSPath){
        populationTableName = tableName;
        populationRDSPath = RDSPath;
        clearArrays();
    }

    /**
     * Gets the population table name.
     *
     * @return the String name of the population table.
     */
    public String getPopulationTable(){
        return populationTableName;
    }

    /**
     * Gets the unique name of the population RDS
     * @return : the unique name of the population RDS (equal to rds.getUniqueName()).
     */
    public String getPopulationRDS(){
        return populationRDSPath;
    }

    /**
     * Gets an array list of all the fields in the population table that have been linked
     * @return
     */
    public ArrayList<String> getPopulationFields(){
        return populationFields;
    }

    /**
     * Sets the population ID field.  This is the field that is used to identify individuals in the table
     * and should consist of unique values for eah person.
     *
     * @param field String name of the field
     */
    void setPopulationIDField(String field){
        populationIDField = field;
    }

    /**
     * get the popualtion Id field
     * @return :the String name of the field used to identify individuals in the population table.
     */
    public String getPopulationIDField(){
        return populationIDField;
    }

    /**
     * get the associated link table with the population field.
     * @param populationField String name of the population field to query on
     * @return String name of the link table associated with this popualtiion field.
     */
    public String getLinkTable(String populationField){
        for (int i = 0; i < populationFields.size(); i++) {
            if (populationFields.get(i).equals(populationField)){
                return linkTables.get(i);
            }
        }
        return "";
    }

    /**
     * Gets the unique name of the RDS associated with this link Table
     *
     * @param linkTable String name of the table to query on
     * @return String unique name of the associated RDS
     */
    public String getLinkRDS(String linkTable){
        for (int i = 0; i < linkTables.size(); i++) {
            if (linkTables.get(i).equals(linkTable)){
                return linkRDSPath.get(i);
            }
        }
        return "";
    }

    /**
     * Gets the fields that has been specified as the zone ID field in the link table
     *
     * @param linkTable : String name of table to query on.
     * @return String name of the field that is the zone id field.
     */
    public String getZoneIDField(String linkTable){
        for (int i = 0; i < linkTables.size(); i++) {
            if (linkTables.get(i).equals(linkTable)){
                for (int j = 0; j < populationValues.get(i).size(); j++) {
                    if ( populationValues.get(i).get(j).equals(ConfigurationScreen.ZONE_ID_FIELD) ){
                        return linkFields.get(i).get(j).toString();
                    }
                }
            }
        }
        return "";
    }


    /**
     * Gets the ArrayList<String> of the link fields associated with this link table
     * The values from the population table associated with these fields can be obtained using
     * getLinkValues method.
     *
     * @param linkTable String name of the link table to query.
     * @return an ArrayList<String> object containing the list of fields to link
     */
    public ArrayList<String> getLinkFields(String linkTable){
        for (int i = 0; i < linkTables.size(); i++) {
            if (linkTables.get(i).equals(linkTable)){
                return linkFields.get(i);
            }
        }
        return null;
    }

    /**
     * Gets the ArrayList of the link values associated with this link table.  The fields associated
     * with the values can be obrtained using the getLinkFields method
     *
     * @param linkTable String name of the link table to query.
     * @return an ArrayList object containing the list of values to link
     */
    public ArrayList getLinkValues(String linkTable){
        for (int i = 0; i < linkTables.size(); i++) {
            if (linkTables.get(i).equals(linkTable)){
                return populationValues.get(i);
            }
        }
        return null;
    }

    /**
     * Adds a link between the link field name entered and the popualtion table value.
     *
     * @param linkFieldName String name of the link table field to associate with the popFieldValue.
     * @param popFieldValue Object representing the value to associate with the linkFieldValue.
     */
    void addLink(String linkFieldName, Object popFieldValue){
        values.add(popFieldValue);
        fields.add(linkFieldName);
    }


    /**
     * Saves the current stored links (stored using addLink method) against the
     * population field and link table specified.
     *
     * @param populationField String name of the field associated with this link in the population table.
     * @param linkTable String name of the link table associated with this link.
     * @param rdsUniqueName String unique name of the RDS associated with the link table.
     */
    void saveLink(String populationField, String linkTable, String rdsUniqueName){

        //remove any existing links before adding the new ones
        removeLink(populationField);

        //add the new links in for this population field
        populationValues.add(values);
        linkFields.add(fields);
        populationFields.add(populationField);
        linkTables.add(linkTable);
        linkRDSPath.add(rdsUniqueName);
        values = new ArrayList();
        fields = new ArrayList<String>();
        
    }

    /**
     * Removes any links associated with the popualtion field.
     * @param populationField : String name of field to remove the links from.
     */
    void removeLink(String populationField){
       //check and see if the population field has already been linked
        if ( populationFields.contains(populationField) ){

            //if it does find its location and remove all of the links
            int index = populationFields.indexOf(populationField);
            populationFields.remove(index);
            linkTables.remove(index);
            linkRDSPath.remove(index);
            populationValues.get(index).clear();
            linkFields.get(index).clear();
            populationValues.remove(index);
            linkFields.remove(index);
        }

    }

    /**
     * Load the specified saved configuration into this object.
     * @param ai :  the current ApplicationInformation object.
     * @param modelType :  the type of model to be loaded JarInfo.MICROSIMULATION_MODEL or JarInfo.MICROSIMULATION_VALIDATION.
     * @param modelName :  the name of the model to be loaded.
     */
    boolean loadConfiguration(IApplicationInformation ai, String modelType, String modelName){
        setModelName(modelName);
        String[] s = ai.getAllModelProperty(modelType, modelName);
        StringTokenizer st;
        String key = "";
        String value = "";

        for (int i = 0; i<s.length;i++){
            st = new StringTokenizer(s[i],"<>");

            if (st.hasMoreTokens()){key = st.nextToken();} else {key = "";}
            if (st.hasMoreTokens()){value = st.nextToken();} else {value = "";}

            //set up the population table
            if ( key.equals(LinkConfiguration.PROPERTY_POP_TABLE) ){
                populationTableName = value;
                
            //set up the population RDS
            }else if( key.equals(LinkConfiguration.PROPERTY_POP_RDS) ){
                populationRDSPath = value;
                
            //set up the population id field
            }else if( key.equals(LinkConfiguration.PROPERTY_POP_ID) ){
                populationIDField = value;
                
            //set up the output table
            }else if( key.equals(LinkConfiguration.PROPERTY_OUTPUT_TABLE) ){
                outputTableGroup = value;

            //set up the output RDS
            }else if( key.equals(LinkConfiguration.PROPERTY_OUTPUT_RDS) ){
                outputRDS = value;
                
            //set up the link table name to use for the population total
            }else if( key.equals(LinkConfiguration.PROPERTY_POPULATION_TOTAL_LINK_TABLE) ){
                linkForPopulationTotal = value;

            //set up the link table
            }else if( key.equals(LinkConfiguration.PROPERTY_LINK_TABLE) ){
                setupLinkTable(value, s);
            }

        }

        return checkConfiguration(ai, modelType);

    }

    private void setupLinkTable(String tableName, String[] s){
        StringTokenizer st;
        String key = "";
        String value = "";
        
        String linkRDS = "";
        String popField = "";
        
        for (int i = 0; i<s.length;i++){
            st = new StringTokenizer(s[i],"<>");
            
            if (st.hasMoreTokens()){key = st.nextToken();} else {key = "";}
            if (st.hasMoreTokens()){value = st.nextToken();} else {value = "";}
            
            String secKey = "";
            String secValue = "";
            StringTokenizer secTok = new StringTokenizer(value, "[]");

            if (secTok.hasMoreTokens()){secKey = secTok.nextToken();} else {secKey = "";}
            if (secTok.hasMoreTokens()){secValue = secTok.nextToken();} else {secValue = "";}

            if ( secKey.equals(tableName) ){
                if ( key.equals(LinkConfiguration.PROPERTY_LINK_RDS) ){
                    linkRDS = secValue;
                }else if ( key.equals(LinkConfiguration.PROPERTY_POP_FIELD) ){
                    popField = secValue;
                }else if ( key.equals(LinkConfiguration.PROPERTY_FIELD_VALUE) ){
                    StringTokenizer fieldTok = new StringTokenizer(secValue, "{}");
                    String field = "";
                    String popvalue = "";
                    if (fieldTok.hasMoreTokens()){field = fieldTok.nextToken();} else {field = "";}
                    if (fieldTok.hasMoreTokens()){popvalue = fieldTok.nextToken();} else {popvalue = "";}
                    this.addLink(field, popvalue);
                }
            }
            
        }
           
        this.saveLink(popField, tableName, linkRDS);
    }

    
    private boolean checkConfiguration(IApplicationInformation ai, String modelType){

        //first check the population table
        boolean populationTableFound = true;
        boolean allPopulationFieldsCorrect = true;
        boolean changesMade = false;
        FMFTable popTable = null;

        //create an arraylist to keep track of the links to be removed
        HashSet<String> linksToRemove = new HashSet<String>();

        //is the RDS valid?
        RegisteredDataSource populationRDS = ai.getRDSfromFileName(getPopulationRDS());
        if ( populationRDS == null  || !populationRDS.isValid()){
            //RDS could not be found
            populationTableFound = false;
        }

        //if the RDS is valid is the population table contained in it?
        if ( populationTableFound ){
            populationTableFound = populationRDS.tableExists(populationTableName);
        }

        //if either of these has return false get the user to select where the population table is
        if ( !populationTableFound ){
            String[] message = new String[3];
            message[0] = "Table '"+populationTableName+"' cannot be found in";
            message[1] = "data source '"+getPopulationRDS()+"'.";
            message[2] = "Please select a valid table";
            popTable = checkTable(ai, message);
            if ( popTable == null ){
                //cannot be found or was cancelled so return false
                return false;
            } else {
                //set up the correct population table
                populationTableName = popTable.getName();
                populationRDSPath = popTable.getRDS().getFileName();
            }
        }

        //are all the fields used in the population table contained in the popualtion table?
        if (!populationFields.isEmpty()){
            //population table has not been changed so get the original one
            if ( popTable == null ){
                popTable = ai.getRDSfromFileName(getPopulationRDS()).getTable(populationTableName);
            }
            
            //check and make sure that the population ID field exists
            if ( !popTable.fieldExists(populationIDField) ){
                allPopulationFieldsCorrect = false;
                String[] message = new String[3];
                message[0] = "Field '"+populationIDField+"' cannot be found in";
                message[1] = "table '"+populationTableName+"'.";
                message[2] = "Please select the correct field";
                populationIDField = checkField(ai, message, popTable);
                if ( populationIDField.equals("") ){
                    //cannot be found or was cancelled so return false
                    return false;
                }
            }
            for (int i = 0; i < populationFields.size(); i++) {
                //field cannot be found!
                String field = populationFields.get(i);
                if ( !popTable.fieldExists(field) ){
                    allPopulationFieldsCorrect = false;
                    String[] message = new String[3];
                    message[0] = "Field '"+field+"' cannot be found in";
                    message[1] = "table '"+populationTableName+"'.";
                    message[2] = "Please select the correct field";
                    populationFields.set(i, checkField(ai, message, popTable));
                    if ( populationFields.get(i).equals("") ){
                        //cannot be found or was cancelled so return false
                        return false;
                    }
                }
            }
            
        }


        //check all of the link tables and fields do they exist?
        for (int linkTableCount = 0; linkTableCount< linkTables.size(); linkTableCount++){
            boolean linkFound = true;
            //check the link RDS exists?
            RegisteredDataSource linkRDS = ai.getRDSfromFileName(linkRDSPath.get(linkTableCount));
            if ( linkRDS == null  || !linkRDS.isValid() ){
                linkFound = false;
            }else{
                //check that the link table is in the link RDS?
                linkFound = linkRDS.tableExists(linkTables.get(linkTableCount));
            }

            FMFTable newLinkTable = null;
            //ask the user to enter the table
            if ( !linkFound ){
                changesMade = true;
                String[] message = new String[3];
                message[0] = "Table '"+linkTables.get(linkTableCount)+"' cannot be found in";
                message[1] = "data source '"+linkRDSPath.get(linkTableCount)+"'.";
                message[2] = "Please select a valid table";
                newLinkTable = checkTable(ai, message);
                if ( newLinkTable == null ){
                    //cannot be found or was cancelled so remove the link from the configuration
                    linksToRemove.add(populationFields.get(linkTableCount));

                } else {
                    //set up the correct population table
                    linkTables.set(linkTableCount, newLinkTable.getName());
                    linkRDSPath.set(linkTableCount, newLinkTable.getRDS().getFileName());

                    //does the link table contain all of the fields required?
                    ArrayList<String> fieldNames = linkFields.get(linkTableCount);
                    for (int fieldCount = 0; fieldCount < fieldNames.size(); fieldCount++) {
                        if ( !newLinkTable.fieldExists( fieldNames.get(fieldCount) ) ) {
                            //field cannot be found!
                            message = new String[3];
                            message[0] = "Field '"+fieldNames.get(fieldCount)+"' cannot be found in";
                            message[1] = "table '"+newLinkTable.getName()+"'.";
                            message[2] = "Please select the correct field";
                            fieldNames.set(fieldCount, checkField(ai, message, newLinkTable));
                            if ( fieldNames.get(fieldCount).equals("") ){
                                //cannot be found or was cancelled so mark the link to be removed
                                linksToRemove.add(populationFields.get(linkTableCount));
                            }
                        }
                    }
                }
            }
        }


        //does the population field contain all of the categories required?
        if ( !populationTableFound | !allPopulationFieldsCorrect ){
            changesMade = true;
            //load the data
            popTable.loadData(ai, null);
            //load the population table and check the population field categories

            for (int fieldCount = 0; fieldCount < populationFields.size(); fieldCount++) {
                ArrayList categories = popTable.getUniqueValues(populationFields.get(fieldCount));
                ArrayList originalCategories = (ArrayList) getLinkValues( getLinkTable( populationFields.get(fieldCount) ) ).clone();
                boolean linkBroken = true;
                //remove the default zone id field to make sure that it is not included in the comparisons
                originalCategories.remove(ConfigurationScreen.ZONE_ID_FIELD);
                for (int categoryCount = 0; categoryCount < categories.size(); categoryCount++) {

                    linkBroken = true;
                    String newCategory = categories.get(categoryCount).toString();
                    for (int originalCategoryCount = 0; originalCategoryCount < originalCategories.size(); originalCategoryCount++) {
                        if ( newCategory.equalsIgnoreCase( originalCategories.get(originalCategoryCount).toString() ) ){
                            originalCategories.remove(originalCategoryCount);
                            linkBroken = false;
                            break;
                        }
                    }

                    if ( linkBroken ){break;}
                }

                //check and see if all of the categories were found
                if ( originalCategories.size()>0 | linkBroken ){
                    //report the problem and remove the link
                    linksToRemove.add(populationFields.get(fieldCount));
                }

            }

        }


        //remove all of the links marked to be removed
        if ( !linksToRemove.isEmpty() ){
            System.out.println("WARNING - Not all existing value to field pairs could be found." );

            for (String popField : linksToRemove) {
                System.out.println("   link between " + popField + " and table " + getLinkTable( popField ) +" has been removed.");
                removeLink ( popField );
            }
        }

        //Is the output RDS valid?
        RegisteredDataSource outputRDSCheck = ai.getRDSfromFileName(outputRDS);
        if ( outputRDSCheck == null || !outputRDSCheck.isValid() ){
            setOutputRDS("");
        }

        if ( changesMade ){
            //if changes have been made instruct the user to check and save the configuration.
            System.out.println("Changes have been made to the configuration '"+modelName+"' save the configuration to ensure changes are available in the future.");
        }

        return true;
    }

    private FMFTable checkTable(IApplicationInformation ai, String[] message){
        //reset the cancelled status
        cancelled = false;

        input = new InputBox(ai, "Select correct table");
        for (int i = 0; i < message.length; i++) {
            input.addMessageLine(message[i]);
        }
        input.setYesVisible(true);
        input.setNoVisible(false);
        input.setCancelVisible(true);
        input.setButtonYesText("change");
        input.setActionListener(this);
        DataSourceHandler dsh = new DataSourceHandler(){

            @Override
            public boolean shouldImport(TreeCellInfo t) {
                return t.isTable();
            }

            @Override
            public void doAction(TreeCellInfo t) {
                input.setInputText( t.toString() );
            }

        };

        input.enableDragNDrop(dsh, true);
        input.popup();
        //put the current thread to sleep and wait for a response
        shouldSleep = true;
        while(shouldSleep){sleep();}

        FMFTable table = null;
        if (!cancelled){
            RegisteredDataSource rds = dsh.getDataSource();
            table = rds.getTable(input.getInput());
        }
        input.dispose();
        return table;
    }

    private String checkField(IApplicationInformation ai,
            String[] message, final FMFTable table){

        //reset the cancelled status
        cancelled = false;

        input = new InputBox(ai, "Select correct field");
        for (int i = 0; i < message.length; i++) {
            input.addMessageLine(message[i]);
        }
        input.setYesVisible(true);
        input.setNoVisible(false);
        input.setCancelVisible(true);
        input.setButtonYesText("change");
        input.setActionListener(this);
        DataSourceHandler dsh = new DataSourceHandler(){

            @Override
            public boolean shouldImport(TreeCellInfo t) {
                if (t.isField() ){
                    if ( t.getRDS() == table.getRDS() && t.getTable().equalsIgnoreCase(t.getTable()) ){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }

            @Override
            public void doAction(TreeCellInfo t) {
                input.setInputText( t.toString() );
            }

        };

        input.enableDragNDrop(dsh, true);
        input.popup();
        //put the current thread to sleep and wait for a response
        shouldSleep = true;
        while(shouldSleep){sleep();}

        String returnField = "";
        if (!cancelled){
            returnField = input.getInput();
        }
        input.dispose();
        return returnField;
    }



    /**
     * Save the current configuration stored in this object.
     * @param ai The current ApplicationInformation object.
     * @param modelType The model type JarInfo.MICROSIMULATION_MODEL or JarInfo.MICROSIMULATION_VALIDATION
     */
    void saveConfiguration(IApplicationInformation ai, String modelType){
        //delete any other model configurations by this name
        ai.deleteModel(modelType, modelName);
        
        //save the population table configuration settings
        ai.setModelProperty(modelType, modelName, 
                LinkConfiguration.PROPERTY_POP_TABLE, populationTableName,true);

        ai.setModelProperty(modelType, modelName, 
                LinkConfiguration.PROPERTY_POP_RDS, this.populationRDSPath,true);

        ai.setModelProperty(modelType, modelName, 
                LinkConfiguration.PROPERTY_POP_ID, this.getPopulationIDField(),true);
        
        if ( !outputRDS.equals("") ){
            ai.setModelProperty(modelType, modelName, 
                LinkConfiguration.PROPERTY_OUTPUT_RDS, outputRDS,true);
        }

        if ( !outputTableGroup.equals("") ){
            ai.setModelProperty(modelType, modelName,
                LinkConfiguration.PROPERTY_OUTPUT_TABLE, outputTableGroup,true);
        }

        //save the link table and links settings
        for (int i = 0; i < linkTables.size(); i++) {
            //save link table information and links information
            ai.setModelProperty(modelType, modelName, 
                    LinkConfiguration.PROPERTY_LINK_TABLE, linkTables.get(i),false);

            ai.setModelProperty(modelType, modelName, 
                    LinkConfiguration.PROPERTY_LINK_RDS, "["+linkTables.get(i)+"]"+linkRDSPath.get(i),false);

            ai.setModelProperty(modelType, modelName, 
                    LinkConfiguration.PROPERTY_POP_FIELD, "["+linkTables.get(i)+"]"+populationFields.get(i),false);


            //save the individual link information
            for (int j = 0; j < linkFields.get(i).size(); j++) {

                ai.setModelProperty(modelType, modelName, LinkConfiguration.PROPERTY_FIELD_VALUE,
                        "["+linkTables.get(i)+"]"+"{"+linkFields.get(i).get(j)+"}"+populationValues.get(i).get(j).toString(),false);
                
            }

        }

        //save the link table to calculate total population from
        ai.setModelProperty(modelType, modelName,
                LinkConfiguration.PROPERTY_POPULATION_TOTAL_LINK_TABLE, linkForPopulationTotal,true);


        ai.refreshMenu(JarInfo.MENU_NAME);
    }

    /**
     * print the current content to the reporting panel.
     */
    void printContent(){
        System.out.println("Population table - "+populationTableName + ": "+populationRDSPath);
        System.out.println("    Population ID field - " + populationIDField);
        System.out.println("    LINKS:");
        for (int i = 0; i < populationFields.size(); i++) {
            System.out.println("    Field - " + populationFields.get(i) + " linked to table - " +
                    linkTables.get(i) + " in " + linkRDSPath.get(i) );

            for (int j = 0; j < linkFields.get(i).size(); j++) {
                System.out.println("        Value - " + populationValues.get(i).get(j).toString() +
                        " to field " + linkFields.get(i).get(j) );

            }

        }


    }

    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @param modelName the modelName to set
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return the unique name of the data source.
     */
    public String getOutputRDS() {
        return outputRDS;
    }

    /**
     * @param outputRDS the outputRDS to set
     */
    public void setOutputRDS(String outputRDS) {
        this.outputRDS = outputRDS;
    }

    /**
     * @return the outputTableGroup
     */
    public String getOutputTableGroup() {
        return outputTableGroup;
    }

    /**
     * @param outputTableGroup the outputTableGroup to set
     */
    public void setOutputTableGroup(String outputTableGroup) {
        this.outputTableGroup = outputTableGroup;
    }

    
    private synchronized void wakeup(){
        notifyAll();
    }

    private synchronized void sleep(){
        try {
            wait();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals(InputBox.CANCEL)){
			cancelled=true;
		}

        shouldSleep = false;
        wakeup();
    }

    /**
     * @return the useSeed
     */
    public boolean useSeed() {
        return useSeed;
    }

    /**
     * @param useSeed the useSeed to set
     */
    public void setUseSeed(boolean useSeed) {
        this.useSeed = useSeed;
    }

    /**
     * @return the seed
     */
    public long getSeed() {
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(long seed) {
        this.seed = seed;
    }

    /**
     * @return the linkForPopulationTotal
     */
    public String getLinkForPopulationTotal() {
        return linkForPopulationTotal;
    }

    /**
     * @param linkForPopulationTotal the linkForPopulationTotal to set
     */
    public void setLinkForPopulationTotal(String linkForPopulationTotal) {
        this.linkForPopulationTotal = linkForPopulationTotal;
    }


}
