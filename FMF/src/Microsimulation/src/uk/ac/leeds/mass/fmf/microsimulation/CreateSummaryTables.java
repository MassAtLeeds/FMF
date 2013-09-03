/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class CreateSummaryTables extends FrameworkProcess{

//    private LinkConfiguration lc=null;
    private IApplicationInformation ai = null;
    private RegisteredDataSource resultRDS = null;
    private FMFTable resultTable = null;
    private String zoneIDField = "";
    private String personIDField = "";

    private int[][] population = null;
    private String[] populationID = null;
    private String[][] lkp = null;
    private double[][][] consts = null;
    private double[][][] summaryResults = null;
    private String[]   zones = null;
    private String[][] fieldNames = null;
    private RegisteredDataSource rds = null;
    private String[] zoneIDFields = null;
    private FMFTable[] predTables = null;
    private FMFTable[] obsTables = null;

    LinkDataManager dataLoader = null;
    LinkConfiguration lc = null;
    
    CreateSummaryTables(IApplicationInformation ai , LinkDataManager ldm, LinkConfiguration lc,
            RegisteredDataSource resultRDS, FMFTable resultTable, String zoneIDField, String personIDField){
        this.ai = ai;
        this.dataLoader = ldm;
        this.lc = lc;
        this.resultRDS = resultRDS;
        this.resultTable = resultTable;
        this.zoneIDField = zoneIDField;
        this.personIDField = personIDField;
    }

    @Override
    public void runProcess() {

        initilise(2);

        setName("calculating summary tables...");
        loadData();


        if ( !cancelled ){

           stages = zones.length + 2;
           progress++;

           //cycle through all of the zones
            for (int zoneCount = 0; zoneCount < zones.length; zoneCount++) {

                setName("Creating summary for " + zones[zoneCount]);

                resultTable.setFilter(zoneIDField, zones[zoneCount]);

                //cycle through the current population and create the summary predTables
                resultTable.moveBeforeFirst();
                while (resultTable.hasMoreRows()){
                //for (int j = 0; j < resultTable.getRowCount(); j++) {
                    resultTable.moveToNextRow();
                    String person = resultTable.getStringValue(personIDField);
                    for (int personIDCount = 0; personIDCount < populationID.length; personIDCount++) {

                        if ( populationID[personIDCount].equals(person) ){

                            //add the person in and then break
                            for (int categoryCount = 0; categoryCount < population.length; categoryCount++) {
                                summaryResults[zoneCount][categoryCount][population[categoryCount][personIDCount]]++;
                            }

                            break;

                        }

                    }
                }

                progress++;
                if(cancelled){break;}
            }

        }

        resultTable.clearFilter();
        resultTable.clear();

        if(!cancelled){

            //save the summary information
            setName("Saving summary tables...");
            createTables();
            
            progress++;
        }

        if ( !cancelled ){finished();}

    }


    private void loadData(){

        population = dataLoader.getPopulation();
        populationID = dataLoader.getPopulationID();
        lkp = dataLoader.getLkp();
        consts = dataLoader.getConsts();
        zones = dataLoader.getZones();
        fieldNames = dataLoader.getFieldNames();

        zoneIDFields = new String[consts[0].length];
        predTables = new FMFTable[consts[0].length];
        obsTables = new FMFTable[consts[0].length];

        summaryResults = new double [consts.length][consts[0].length][];
        for (int i = 0; i < summaryResults.length; i++) {
            for (int j = 0; j < summaryResults[i].length; j++) {
                summaryResults[i][j] = new double [consts[i][j].length];
            }
        }

    }

    private void createTables(){
        
        rds = ai.getRDSfromFileName(lc.getOutputRDS());

        //cycle through each of the links
        for (int constraintCount = 0; constraintCount < summaryResults[0].length; constraintCount++) {

            String linkTableName = lc.getLinkTable(lc.getPopulationFields().get(constraintCount));

            //create the predTables to save the summary data to

            //get the link Table
            FMFTable linkTab = ai.getRDSfromFileName(lc.getLinkRDS(linkTableName))
                    .getTable(linkTableName);
            FMFTable summary = new FMFTable( lc.getOutputTableGroup()+"_"+linkTab.getName(), null, null );
            for (int k = 0; k < linkTab.getFieldCount(); k++) {
                summary.addFieldToNewTable(linkTab.getFieldName(k), linkTab.getColumnType(k));
            }
            ai.getDataAccessFactory().createTable(rds, summary, true);
            
            zoneIDFields[constraintCount] = lc.getZoneIDField(linkTab.getName());
            obsTables[constraintCount] = linkTab;
            predTables[constraintCount] = summary;
                //synchronize the block so that nothing else can update the table while we are working on it.
                synchronized(predTables[constraintCount]){
                    predTables[constraintCount].loadData(ai, null);
                    predTables[constraintCount].lock();

                    //cycle through all of the zones
                    for (int zoneCount = 0; zoneCount < zones.length; zoneCount++) {

                        //insert the zone in the new table
                        predTables[constraintCount].insertValue(lc.getZoneIDField(linkTableName),zones[zoneCount]);

                        //get the population count in each of the links
                        for (int categoryCount = 0; categoryCount < summaryResults[zoneCount][constraintCount].length; categoryCount++) {

                            predTables[constraintCount].insertValue(fieldNames[constraintCount][categoryCount], summaryResults[zoneCount][constraintCount][categoryCount]);

                        }

                        //insert the new row
                        predTables[constraintCount].insertRow();

                    }
                    //commit the inserts to the new table and unlock and clear it.
                    predTables[constraintCount].commitInserts();
                    predTables[constraintCount].unlock();
                    predTables[constraintCount].clear();
                }
            }
        

    }   
 

    RegisteredDataSource getOutputRDS(){
        return rds;
    }

    String[] getZoneIDFields(){
        return zoneIDFields;
    }

    FMFTable[] getObservedTable(){
        return obsTables;
    }

    FMFTable[] getPredictedTable(){
        return predTables;
    }

}
