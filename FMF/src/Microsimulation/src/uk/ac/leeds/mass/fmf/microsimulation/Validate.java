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

import uk.ac.leeds.mass.fmf.fit_statistics.IGOF;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
public class Validate extends FrameworkProcess{

    private String[] zoneIDs = null;
    
    private IGOF[] gof = null;
    private RegisteredDataSource outputRDS = null;
    private IApplicationInformation ai = null;
    private FMFTable[] observed = null;
    private FMFTable[] predicted;
    private String[] zoneIDFields;
    private String outputName;

    private Validate(){}

    /**
     *
     * NOTE for this method to work correctly the field names and table structures of the observed and
     * predicted arrays have to be exactly the same!
     *
     * @param ai Current ApplicationInformation object
     * @param outputRDS The RegisterDataSource object for the output tables to be saved in
     * @param gof Array of gof test objects to be performed
     * @param observed array of FMFTables containing the observed counts for the model
     * @param predicted array of FMFTables containing the predicted counts for the model
     * (this array should be a identical in structure to the observed array)
     * @param zoneIDFields String array of field names that contain the zone ids in each of the FMFTables
     * @param outputName Name of the group of tables for output (normally the name of the model)
     */
    public Validate (IApplicationInformation ai, RegisteredDataSource outputRDS, IGOF[] gof,
            FMFTable[] observed, FMFTable[] predicted, String[] zoneIDFields, String outputName){
        this.gof = gof;
        this.ai = ai;
        this.outputRDS = outputRDS;
        this.observed = observed;
        this.predicted = predicted;
        this.zoneIDFields = zoneIDFields;
        this.outputName = outputName;
    }



    void validateTables( ){

        initilise( (observed.length * 3) + 2 );

        double totalPop = 0.0;

        setName("Loading data...");

        for (int i = 0; i < observed.length; i++) {
            observed[i].clear();
            observed[i].loadData(ai, null);
            predicted[i].clear();
            predicted[i].loadData(ai, null);
            progress++;
            if (cancelled){break;}
        }


        setName("Creating zone ids...");

        //create a list of the zoneIDs from the first observed table
        for (int i = 0; i < observed.length; i++) {
            zoneIDs = new String[ observed[i].getRowCount() ];
            observed[i].moveBeforeFirst();
            int j=0;
            while (observed[i].hasMoreRows()){
                observed[i].moveToNextRow();
                zoneIDs[j] = observed[i].getStringValue(zoneIDFields[i]);
                j++;
            }
            break;
        }

        this.updateStages( stages + zoneIDs.length );

        progress++;

        //set up the arrays to hold the values for each of the gof tests
        double obsCounts[][][] = new double[observed.length][zoneIDs.length][];
        double predCounts[][][] = new double[observed.length][zoneIDs.length][];
        int obsRow = -1;
        int predRow = -1;

        String fieldNames[][] = new String[observed.length][];

        setName("Organising data...");
        //cycle through all of the tables
        for (int i = 0; i < observed.length; i++) {

            //cycle through each zone
            for (int j = 0; j < zoneIDs.length; j++) {

                obsCounts[i][j] = new double[observed[i].getFieldCount()-1];
                predCounts[i][j] = new double[observed[i].getFieldCount()-1];

                obsRow = observed[i].getFirstRowID(zoneIDFields[i], zoneIDs[j]);
                predRow = predicted[i].getFirstRowID(zoneIDFields[i], zoneIDs[j]);

                if ( obsRow > -1 && predRow > -1 ){

                    fieldNames[i] = new String[observed[i].getFieldCount()-1];

                    //cycle through the fields
                    int offset = 0;
                    for (int k = 0; k < observed[i].getFieldCount(); k++) {

                        //setup the offset value when we come across the zoneID field
                        if ( observed[i].getFieldName(k).equals(zoneIDFields[i]) ){
                            offset = 1;
                            continue;
                        }

                        //enter the field names into the array in the right order for later use
                        fieldNames[i][k-offset] = observed[i].getFieldName(k);

                        //put the count values into the correct places in the count arrays matching the
                        //fields on their names  THEREFORE THE NAMES HAVE TO MATCH!
                        obsCounts[i][j][k-offset] = observed[i].getDoubleValue(k,obsRow);
                        try{
                            predCounts[i][j][k-offset] = predicted[i].getDoubleValue(observed[i].getFieldName(k), predRow);
                        }catch(Exception e){
                            System.out.println(observed[i].getFieldName(k) + " : " + predRow + " : " +zoneIDs[j]);
                        }

                        if (i == 0){
                            //add up the total population count for the GOF tests.
                            //only do it for the first table though.
                            totalPop += obsCounts[i][j][k-offset];
                        }

                    }
                }else{
                    if ( obsRow == -1 ){
                        System.out.println(zoneIDs[j]+ " does not exist in table "+
                                observed[i].getName() + " field " + zoneIDFields[i]);
                    }
                    if ( predRow == -1 ){
                        System.out.println(zoneIDs[j]+ " does not exist in table "+
                                predicted[i].getName() + " field " + zoneIDFields[i]);
                    }
                }

            }

            progress++;
            if (cancelled){break;}

        }

        FMFTable[][] linkOutput = new FMFTable[observed.length][gof.length];

        for (int i = 0; i < linkOutput.length; i++) {
            for (int j = 0; j < linkOutput[i].length; j++) {
                linkOutput[i][j] = new FMFTable(outputName+"_"+observed[i].getName()+"_"+ gof[j].fieldName() +"_gof",null,null);
                linkOutput[i][j].addFieldToNewTable("ZoneID", FMFTable.FIELD_TYPE_STRING);
            }
        }


        if (!cancelled){

            setName("Testing zones...");

            for (int constraintCount = 0; constraintCount < observed.length; constraintCount++) {
                for (int gofCount = 0; gofCount < gof.length; gofCount++) {

                    linkOutput[constraintCount][gofCount] = new FMFTable(outputName+"_"+observed[constraintCount].getName()+"_"+ gof[gofCount].fieldName() +"_gof",null,null);
                    linkOutput[constraintCount][gofCount].addFieldToNewTable("ZoneID", FMFTable.FIELD_TYPE_STRING);

                    for (int constraintFieldCount = 0; constraintFieldCount < fieldNames[constraintCount].length; constraintFieldCount++) {
                        linkOutput[constraintCount][gofCount].addFieldToNewTable(fieldNames[constraintCount][constraintFieldCount], FMFTable.FIELD_TYPE_DOUBLE);
                    }

                    //create the new table
                    ai.getDataAccessFactory().createTable(outputRDS,linkOutput[constraintCount][gofCount], true);

                    synchronized(linkOutput[constraintCount][gofCount]){
                        linkOutput[constraintCount][gofCount].loadData(ai, null);
                        linkOutput[constraintCount][gofCount].lock();

                        //cycle through the zones
                        for (int zoneCounter = 0; zoneCounter < zoneIDs.length; zoneCounter++) {

                            //insert the data into the link output table
                            //add a new row into the relevant link gof table
                            linkOutput[constraintCount][gofCount].insertValue("ZoneID", zoneIDs[zoneCounter]);

                            for (int categoryCount = 0; categoryCount < obsCounts[constraintCount][zoneCounter].length; categoryCount++) {

                                //create double arrays of the single cell values to be tested
                                double[][] singleCalib = new double[1][1];
                                double[][] singleTest = new double[1][1];
                                singleCalib[0][0] = obsCounts[constraintCount][zoneCounter][categoryCount];
                                singleTest[0][0] = predCounts[constraintCount][zoneCounter][categoryCount];

                                //put the values in the correct table and row
                                linkOutput[constraintCount][gofCount].insertValue(fieldNames[constraintCount][categoryCount],
                                        gof[gofCount].test( singleCalib, singleTest, totalPop ));

                        }

                        linkOutput[constraintCount][gofCount].insertRow();

                        }
                        //commit the inserts
                        linkOutput[constraintCount][gofCount].commitInserts();

                        //unlock the table and clear it
                        linkOutput[constraintCount][gofCount].unlock();
                        linkOutput[constraintCount][gofCount].clear();

                    }

                }

                //update progress
                progress++;
                if (cancelled){break;}
            }

        }

        setName("Creating zone output...");
        //create the zone GOF table
        FMFTable zoneOutput = new FMFTable(outputName+"_zones_gof",null,null);

        //add the fields
        zoneOutput.addFieldToNewTable("ZoneID", FMFTable.FIELD_TYPE_STRING);
        for (int constraintCount = 0; constraintCount < observed.length; constraintCount++) {
            for (int gofCount = 0; gofCount < gof.length; gofCount++) {
                zoneOutput.addFieldToNewTable(
                        observed[constraintCount].getName()+"_"+gof[gofCount].fieldName(),
                        FMFTable.FIELD_TYPE_DOUBLE);
            }
        }

        //create the table
        ai.getDataAccessFactory().createTable(outputRDS,zoneOutput, true);

        synchronized(zoneOutput){
            zoneOutput.loadData(ai, null);
            zoneOutput.lock();

            //insert the data into the table
            for (int zoneCount = 0; zoneCount < zoneIDs.length; zoneCount++) {
                //add a row to the table and put in the
                zoneOutput.insertValue("ZoneID", zoneIDs[zoneCount]);

                //cycle through the varaibles tables
                for (int constraintCount = 0; constraintCount < observed.length; constraintCount++) {

                    double[][] calib = new double[1][obsCounts[constraintCount][zoneCount].length];
                    double[][] test = new double[1][obsCounts[constraintCount][zoneCount].length];
                    calib[0] = obsCounts[constraintCount][zoneCount];
                    test[0] = predCounts[constraintCount][zoneCount];

                    //cycle through the gof tests
                    for (int gofCount = 0; gofCount < gof.length; gofCount++) {
                        zoneOutput.insertValue( observed[constraintCount].getName()+"_"+gof[gofCount].fieldName(),
                                gof[gofCount].test(calib, test, totalPop) );
                    }
                }
                //insert the new row
                zoneOutput.insertRow();

                //increment progress
                progress++;
                if (cancelled){break;}
            }

            //commit the inserts
            zoneOutput.commitInserts();
            //unlock the table
            zoneOutput.unlock();
            //clear the table
            zoneOutput.clear();
        }

        setName("Creating overall output...");
        //create the overall GOF table
        FMFTable overallOutput = new FMFTable(outputName+"_gof",null,null);
        
        //add the fields
        overallOutput.addFieldToNewTable("VariableName", FMFTable.FIELD_TYPE_STRING);
        for (int gofCount = 0; gofCount < gof.length; gofCount++) {
            overallOutput.addFieldToNewTable(gof[gofCount].fieldName(), FMFTable.FIELD_TYPE_DOUBLE);
        }
        
        //create the table
        ai.getDataAccessFactory().createTable(outputRDS,overallOutput, true);

        synchronized(overallOutput){
            overallOutput.loadData(ai, null);
            overallOutput.lock();

            //insert the data into the table
            for (int constraintCount = 0; constraintCount < obsCounts.length; constraintCount++) {

                overallOutput.insertValue("VariableName", observed[constraintCount].getName());

                for (int gofCount = 0; gofCount < gof.length; gofCount++) {
                    overallOutput.insertValue( gof[gofCount].fieldName(), gof[gofCount].test(obsCounts[constraintCount], predCounts[constraintCount]) );
                }

                overallOutput.insertRow();

                progress++;
                if (cancelled){break;}
            }

            //commit the inserts
            overallOutput.commitInserts();
            //unlock the table
            overallOutput.unlock();
            //clear the table
            overallOutput.clear();
        }

        progress++;

        for (int constraintCount = 0; constraintCount < observed.length; constraintCount++) {
            observed[constraintCount].clear();
            predicted[constraintCount].clear();
        }

        if( !cancelled ){finished();}

    }

    @Override
    public void runProcess() {
        validateTables();
    }

}
