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

package FlatFile;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author Kirk Harland
 */
public class FlatFileReader {

    private final int MAXIMUM_RECORDS_TO_LOAD = 1000000;

    RegisteredDataSource rds;

    private FlatFileReader(){}

    FlatFileReader(RegisteredDataSource rds){
        this.rds = rds;
    }


    void loadData(FMFTable table){

        CSVReader reader = null;
        RegistrationHandler rh = new RegistrationHandler(rds.getRdsID(), table.getName());
        rh.readFMLFile();

        int filterFieldIndex = -1;
        String filterFieldName = "";
        String filterValue = "";

        int increment = 0;
        //check if headers are in the file if so set increment to 1
        if ( table.isFiltered() ){
            filterFieldName = table.getFilterField();
            filterValue = table.getFilterValue();
            for (int i = 0; i < rh.getFieldNames().size(); i++) {
                if ( filterFieldName.equalsIgnoreCase(rh.getFieldNames(i)) ){
                    filterFieldIndex = i;
                    break;
                }
            }
        }else{
            if (rh.isHeadersInFirstRow()){increment = 1;}
        }

        String stringVal = "";
        int intVal = 0;
        double doubleVal = 0.0;

        //set up an ArrayList to hold the fields
        ArrayList readFields = new ArrayList();

        //set up the CSV reader
        try {

            FileReader fr =  new FileReader( rds.getFileName() +System.getProperty("file.separator")+
                    FlatFileManager.appendExtention(table) );

            if ( fr!= null ){
                if ( rh.getTextQualifier() == null ){
                    reader = new CSVReader(
                        new FileReader( rds.getFileName() +System.getProperty("file.separator")+
                        FlatFileManager.appendExtention(table) ),
                            rh.getDelimiter().charAt(0), CSVWriter.NO_QUOTE_CHARACTER
                        );
                }else{
                    reader = new CSVReader(
                        new FileReader( rds.getFileName() +System.getProperty("file.separator")+
                        FlatFileManager.appendExtention(table) ),
                        rh.getDelimiter().charAt(0), rh.getTextQualifier().charAt(0)
                        );
                }

            }

            int recordCount = 0;
            String [] nextLine;
            String [] fieldCountTestLine = null;
            if ( !table.isFiltered() ){
                while ((nextLine = reader.readNext()) != null && recordCount <= MAXIMUM_RECORDS_TO_LOAD) {
                    if (recordCount == 0){fieldCountTestLine=nextLine;}
                    recordCount++;
                }
            }else if ( filterFieldIndex > -1 ){
                while ((nextLine = reader.readNext()) != null && recordCount <= MAXIMUM_RECORDS_TO_LOAD) {
                    if (recordCount == 0){
                        fieldCountTestLine=nextLine;
                        if ( filterFieldIndex >= fieldCountTestLine.length ){break;}
                    }
                    if (nextLine[filterFieldIndex].equalsIgnoreCase(filterValue)){recordCount++;}
                }
            }

            reader.close();
            reader = null;

            //check and make sure we have some data
            if(recordCount > 0){

                if (recordCount >= MAXIMUM_RECORDS_TO_LOAD){
                    System.out.println("maximum number of records ("+ MAXIMUM_RECORDS_TO_LOAD +") reached for table " + table.getName() );
                }

                //test that the number of fields read and expected match
                if(fieldCountTestLine.length == rh.getFieldNames().size()){

                    //if they do start and set up the fields to be read in
                    FlatFileResultSet ffrs = new FlatFileResultSet(false);

                    //initialise the arrays to hold the data to the correct sizes and types
                    for (int i = 0; i < rh.getFieldNames().size(); i++) {
                        int type = rh.getFieldTypes(i);
                        switch (type){

                            case FMFTable.FIELD_TYPE_STRING:
                                readFields.add(new String[recordCount-increment]);
                                break;

                            case FMFTable.FIELD_TYPE_INT:
                                readFields.add(new int[recordCount-increment]);
                                break;

                            case FMFTable.FIELD_TYPE_DOUBLE:
                                readFields.add(new double[recordCount-increment]);
                                break;
                        }
                    }

                    //reset the reader
                    if ( rh.getTextQualifier() == null ){
                        reader = new CSVReader(
                            new FileReader( rds.getFileName() +System.getProperty("file.separator")+
                            FlatFileManager.appendExtention(table) ),
                            rh.getDelimiter().charAt(0), CSVWriter.NO_QUOTE_CHARACTER
                            );
                    }else{
                        reader = new CSVReader(
                            new FileReader( rds.getFileName() +System.getProperty("file.separator")+
                            FlatFileManager.appendExtention(table) ),
                            rh.getDelimiter().charAt(0), rh.getTextQualifier().charAt(0)
                            );
                    }


                    //skip the first row if it contains the headers
                    if ( increment==1 ){nextLine = reader.readNext();}
                    //read in the data going down the records List and reading the data into the readFields ArrayList
                    //replace nulls with default values as we go
                    boolean addRecord = true;
                    int recordPosition = 0;
                    while ((nextLine = reader.readNext()) != null  && recordPosition < (recordCount-increment) ) {
                        if (table.isFiltered()){
                            addRecord = nextLine[filterFieldIndex].equalsIgnoreCase(filterValue);
                        }
                        if (addRecord){
                            for (int j = 0; j < nextLine.length; j++) {

                                int type = rh.getFieldTypes(j);
                                switch (type){

                                    case FMFTable.FIELD_TYPE_STRING:

                                        if(nextLine[j]==null){stringVal = "";}else{stringVal = nextLine[j];}
                                        ( (String[])readFields.get(j) )[recordPosition] = stringVal;
                                        break;

                                    case FMFTable.FIELD_TYPE_INT:
                                        if(nextLine[j]==null){intVal = 0;}else{intVal = Integer.parseInt(nextLine[j]);}
                                        ( (int[])readFields.get(j) )[recordPosition] = intVal;
                                        break;

                                    case FMFTable.FIELD_TYPE_DOUBLE:
                                        if(nextLine[j]==null){doubleVal = 0.0;}else{doubleVal = Double.parseDouble(nextLine[j]);}
                                        ( (double[])readFields.get(j) )[recordPosition] = doubleVal;
                                        break;
                                }
                            }
                            recordPosition++;
                        }
                	}

                    //add the fields to the result set we are creating
                    for (int j = 0; j < rh.getFieldNames().size(); j++) {
                        int type = rh.getFieldTypes(j);

                        switch (type){
                            case FMFTable.FIELD_TYPE_STRING:
                                ffrs.addField((String[])readFields.get(j), rh.getFieldNames(j));
                                break;

                            case FMFTable.FIELD_TYPE_INT:
                                ffrs.addField((int[])readFields.get(j), rh.getFieldNames(j));
                                break;

                            case FMFTable.FIELD_TYPE_DOUBLE:
                                ffrs.addField((double[])readFields.get(j), rh.getFieldNames(j));
                                break;
                        }

                    }

                    //now add the resultset and an empty statement to the table
                    table.clear();
                    table.setResultSet(ffrs, new FlatFileStatement());

                }else{
                    System.out.println("The file you are trying to load " + table.getName() +" is not registered correctly. "+
                            "fields = "+fieldCountTestLine.length+" registration = "+rh.getFieldNames().size());
                    throw new RuntimeException("The file you are trying to load " + table.getName() +" is not registered correctly. "+
                            "fields = "+fieldCountTestLine.length+" registration = "+rh.getFieldNames().size());
                }
            }

            reader.close();
            
            } catch (NumberFormatException nfe){
                System.out.println("The input file format for table " + table.getName() + " is incorrect.  ");
                System.out.println("Possible cause text characters in fields defined as number fields.");
                throw nfe;
            } catch (SQLException sql){
                sql.printStackTrace();
            } catch (FileNotFoundException ex) {
                table.clear();
                table.setResultSet(new FlatFileResultSet(false), new FlatFileStatement());
            } catch (IOException e){
                e.printStackTrace();
            }
    }
}
