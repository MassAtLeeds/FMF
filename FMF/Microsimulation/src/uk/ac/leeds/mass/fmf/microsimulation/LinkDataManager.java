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

package uk.ac.leeds.mass.fmf.microsimulation;

import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
class LinkDataManager extends FrameworkProcess {

    private FMFTable popTable = null;
    private IApplicationInformation ai = null;
    private LinkConfiguration lc = null;
    private int linkTableForPopulationIndex = 0;

    private int[][] population = null;
    private String[] populationID = null;
    private String[][] lkp = null;
    private double[][][] consts = null;
    private String[]   zones = null;
    private String[][] fieldNames = null;
    private String[] zoneIDFields = null;
    
    LinkDataManager(IApplicationInformation ai, LinkConfiguration lc){
        this.ai = ai;
        this.lc = lc;
        setName("Building data arrays...");
    }

    void loadData(){
        //load the population data
        RegisteredDataSource popRDS = ai.getRDSfromFileName(lc.getPopulationRDS());
        popTable = popRDS.getTable(lc.getPopulationTable());
        popTable.clear();
        popTable.loadData(ai, null);
//        ai.getDataAccessFactory().loadData(getPopTable(), null);


        //load the constraint data
        for (int i = 0; i < lc.getPopulationFields().size(); i++) {
            //get the constraint table associated with the population field
            String tableName = lc.getLinkTable( lc.getPopulationFields().get(i) );
            
            //if the link table is the one marked to be used to calculate total population save the index
            if ( tableName.equalsIgnoreCase(lc.getLinkForPopulationTotal()) ){linkTableForPopulationIndex = i;}

            //get the rds associated with the table
            RegisteredDataSource rds = ai.getRDSfromFileName(lc.getLinkRDS(tableName));

            //load the data
            rds.getTable(tableName).clear();
            rds.getTable(tableName).loadData(ai, null);
        }
        
        buildArrays();

        //clear the population data from the cache
        getPopTable().clear();

        //clear the constraint data from the cache
        for (int i = 0; i < lc.getPopulationFields().size(); i++) {
            //get the constraint table associated with the population field
            String tableName = lc.getLinkTable( lc.getPopulationFields().get(i) );

            //get the rds associated with the table
            RegisteredDataSource rds = ai.getRDSfromFileName(lc.getLinkRDS(tableName));

            //clear the cache
            rds.getTable(tableName).clear();
        }
    }

    
    
    private void buildArrays(){

    //the dimensions of the arrays equal:
    //
    //String zones[]
    //one dimensional array containing all of the zones in the current configuration
    //
    //String lkp[][]
    //first dimension is the total number of constraints or linked population fields in this configuration
    //second dimension is the total number of fields / population values involved in this particular link
    //
    //double consts[][][]
    //first dimension is the total number of zones in the current configuration
    //second dimension is the total number of constraints in this configuration
    //third dimension is the total number of fields in this (second dimension) constraint
    //
    //int population[][]
    //first dimension relates to the link field in the population table (same order as lkp & consts arrays)
    //second dimension relates to the rows (persons) in the population table (same order as populationID array)
    //
    //String populationID[]
    //contains the unique id values relating to the population ids in the same order as the population array
    //
    //String fieldNames[][]
    //contains the field names in each link table.  These are ordered in the same way as the consts array
    //first dimension is the constraint or link (same order as the second dimension of the consts array)
    //second dimension is the field name of the link
    //  
    //String zoneIDFields[]
    //contains the zone field names for the link tables in the same order as the first dimension of fieldNames and
    // second dimension of consts



        initilise( lc.getPopulationFields().size() * popTable.getRowCount() );

        //build the population table array
        //use the number of fields in the population table that have been linked
        population = new int [lc.getPopulationFields().size()][popTable.getRowCount()];

        //build the populationID array.
        populationID = new String[popTable.getRowCount()];


        //size the first dimension of the lkp array
        lkp = new String[lc.getPopulationFields().size()][];

        //cycle throught the population fields
        for (int i = 0; i < lc.getPopulationFields().size(); i++) {

            String linkTableName = lc.getLinkTable(lc.getPopulationFields().get(i));
            //get the link Table
            FMFTable linkTab = ai.getRDSfromFileName(lc.getLinkRDS(linkTableName)).getTable(linkTableName);

            //if this is the first pass through
            if ( i==0 ){
                //size the zones and consts arrays first dimensions
                consts = new double[linkTab.getRowCount()][lc.getPopulationFields().size()][];
                fieldNames = new String[lc.getPopulationFields().size()][];
                zoneIDFields = new String[lc.getPopulationFields().size()];
                zones = new String[linkTab.getRowCount()];
            }

            //set the second dimension for the lkp array
            lkp[i] = new String[lc.getLinkValues(linkTableName).size()-1];
            //set up a temporary array of field names to use further down
            fieldNames[i] = new String[lc.getLinkValues(linkTableName).size()-1];
            //populate the field values
            int offset = 0;
            for (int j = 0; j < lc.getLinkValues(linkTableName).size(); j++) {
                if ( ! lc.getLinkFields(linkTableName).get(j).equals(lc.getZoneIDField(linkTableName)) ){
                    lkp[i][j-offset] = lc.getLinkValues(linkTableName).get(j).toString();
                    fieldNames[i][j-offset] = lc.getLinkFields(linkTableName).get(j);
                }else{
                    zoneIDFields[i] = lc.getZoneIDField(linkTableName);
                    offset = 1;
                }

            }

            //populate the consts array
            for (int j = 0; j < linkTab.getRowCount(); j++) {


                int rowID = -1;

                //if this is the first pass through then set up the zones
                if ( i==0 ){
                    zones[j] = linkTab.getStringValue(lc.getZoneIDField(linkTableName), j+1);
                    rowID = j;

                //else get the table rowID from the zones field
                }else{
                    String matchZone = linkTab.getStringValue(lc.getZoneIDField(linkTableName), j+1);

                    for (int k = 0; k < zones.length; k++) {

                        if ( zones[k].equals(matchZone) ){
                            rowID = k;
                            break;
                        }
                    }
//                    rowID = linkTab.getFirstRowID(lc.getZoneIDField(linkTableName), zones[j]);
                }

                // a match has been found for the zone
                if (rowID > -1){
                    //size the remaining dimensions of the consts array for each zone
                    consts[rowID][i] = new double[fieldNames[i].length];
                    //populate the consts array
                    for (int k = 0; k < fieldNames[i].length; k++) {
                        consts[rowID][i][k] = linkTab.getDoubleValue(fieldNames[i][k], j+1);
                    }
                }

            }


        }

        String tempVal="";
        int pointer;

        //cycle through the population table and populate the population array
        for (int j = 0; j < popTable.getRowCount(); j++) {

            for (int i = 0; i < lc.getPopulationFields().size(); i++) {
            
                //if it is the first pass through populate the personIDs
                if( i==0 ){
                    //put the id field in the last dimension of the array
                    populationID[j] = popTable.getStringValue(lc.getPopulationIDField(), j+1);
                }
                //populate the remaining fields with pointers to the consts array where the values can be found
                tempVal = popTable.getStringValue(lc.getPopulationFields().get(i), j+1);
                pointer = -1;
                for (int k = 0; k < getLkp()[i].length; k++) {
                    if ( tempVal.equals(getLkp()[i][k]) ){
                        pointer = k;
                        break;
                    }
                }
                
                //if there is a problem print out some diagnostics to the screen
                if ( pointer == -1 ){
                    System.out.println("Potential problem with configuration population id = " + 
                            populationID[j] + " Field = " + lc.getPopulationFields().get(i) + " Value = " + tempVal);
                }
                
                population[i][j] = pointer;

                progress++;
                if (cancelled){break;}

            }

            if (cancelled){break;}

        }
        if (!cancelled){finished();}

    }


    /**
     * @return the popTable
     */
    public FMFTable getPopTable() {
        return popTable;
    }

    /**
     * @return the population
     */
    public int[][] getPopulation() {
        return population;
    }

    /**
     * @return the populationID
     */
    public String[] getPopulationID() {
        return populationID;
    }

    /**
     * @return the lkp
     */
    public String[][] getLkp() {
        return lkp;
    }

    /**
     * @return the consts
     */
    public double[][][] getConsts() {
        return consts;
    }

    /**
     * @return the zones
     */
    public String[] getZones() {
        return zones;
    }

    public String[][] getFieldNames(){
        return fieldNames;
    }

    public String[] getZoneIDFields(){
        return zoneIDFields;
    }

    private void printout(){
        
        RegisteredDataSource rds = ai.getRDSfromFileName(lc.getOutputRDS());
        
        System.out.println("Printing zones:");
        FMFTable zonesTab = new FMFTable("test_zones",null,null);
        zonesTab.addFieldToNewTable("zones", FMFTable.FIELD_TYPE_STRING);
        ai.getDataAccessFactory().createTable(rds,zonesTab, true);
        zonesTab.loadData(ai, null);
        for (int i = 0; i < zones.length; i++) {
            zonesTab.insertValue("zones", zones[i]);
            zonesTab.insertRow();
        }
        zonesTab.commitInserts();


        System.out.println("printing lkp:");
        FMFTable lkpTab = new FMFTable("test_lkp",null,null);
        lkpTab.addFieldToNewTable("lkp", FMFTable.FIELD_TYPE_STRING);
        ai.getDataAccessFactory().createTable(rds,lkpTab, true);
        lkpTab.loadData(ai, null);
        for (int i = 0; i < lkp.length; i++) {
            for (int j = 0; j < lkp[i].length; j++) {
                lkpTab.insertValue("lkp", lkp[i][j]);
                lkpTab.insertRow();
            }
        }
        lkpTab.commitInserts();
        
        System.out.println("printing consts:");
        FMFTable constsTab = new FMFTable("test_consts",null,null);
        constsTab.addFieldToNewTable("consts", FMFTable.FIELD_TYPE_DOUBLE);
        ai.getDataAccessFactory().createTable(rds,constsTab, true);
        constsTab.loadData(ai, null);
        for (int i = 0; i < consts.length; i++) {
            for (int j = 0; j < consts[i].length; j++) {
                for (int k = 0; k < consts[i][j].length; k++) {
                    constsTab.insertValue("consts", consts[i][j][k]);
                    constsTab.insertRow();
                }
            }
        }
        constsTab.commitInserts();
        
        System.out.println("printing population:");
        FMFTable populationTab = new FMFTable("test_population",null,null);
        populationTab.addFieldToNewTable("population", FMFTable.FIELD_TYPE_INT);
        ai.getDataAccessFactory().createTable(rds,populationTab, true);
        populationTab.loadData(ai, null);
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[i].length; j++){
                populationTab.insertValue("population", population[i][j]);
                populationTab.insertRow();
            }
        }
        populationTab.commitInserts();
        
        System.out.println("Printing populationID:");
        FMFTable populationIDTab = new FMFTable("test_populationID",null,null);
        populationIDTab.addFieldToNewTable("populationID", FMFTable.FIELD_TYPE_STRING);
        ai.getDataAccessFactory().createTable(rds,populationIDTab, true);
        populationIDTab.loadData(ai, null);
        for (int i = 0; i < populationID.length; i++) {
            populationIDTab.insertValue("populationID", populationID[i]);
            populationIDTab.insertRow();
        }
        populationIDTab.commitInserts();

        
        System.out.println("printing fieldNames:");
        FMFTable fieldNamesTab = new FMFTable("test_fieldNames",null,null);
        fieldNamesTab.addFieldToNewTable("fieldNames", FMFTable.FIELD_TYPE_STRING);
        ai.getDataAccessFactory().createTable(rds,fieldNamesTab, true);
        fieldNamesTab.loadData(ai, null);
        for (int i = 0; i < fieldNames.length; i++) {
            for (int j = 0; j < fieldNames[i].length; j++) {
                fieldNamesTab.insertValue("fieldNames", fieldNames[i][j]);
                fieldNamesTab.insertRow();
            }
        }
        fieldNamesTab.commitInserts();

        System.out.println("printing zoneIDFields:");
        FMFTable zoneIDFieldsTab = new FMFTable("test_zoneIDFields",null,null);
        zoneIDFieldsTab.addFieldToNewTable("zoneIDFields", FMFTable.FIELD_TYPE_STRING);
        ai.getDataAccessFactory().createTable(rds,zoneIDFieldsTab, true);
        zoneIDFieldsTab.loadData(ai, null);
        for (int i = 0; i < zoneIDFields.length; i++) {
            zoneIDFieldsTab.insertValue("zoneIDFields", zoneIDFields[i]);
            zoneIDFieldsTab.insertRow();
        }
        zoneIDFieldsTab.commitInserts();
        
    }

    @Override
    public void runProcess() {
        loadData();
    }

    /**
     * @return the linkTableForPopulationIndex
     */
    public int getLinkTableForPopulationIndex() {
        return linkTableForPopulationIndex;
    }

}
