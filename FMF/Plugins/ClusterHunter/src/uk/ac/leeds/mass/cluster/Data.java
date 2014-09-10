/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class wraps the loading of the required file format for the clustering algorithm.
 * The file structure is comma delimited with no header row and 5 columns containing
 * <ol>
 * <li>Unique ID : numeric</li>
 * <li>X coordinate : double</li>
 * <li>Y coordinate : double</li>
 * <li>event count : numeric</li>
 * <li>population count : numeric"</li>
 * </ol>
 * 
 * @author Kirk Harland
 * @version 1.0
 */
public class Data {
    
    File csv = null;
    //double array to hold the data from the csv file
    //data[0][x] = unique identifier
    //data[1][x] = x
    //data[2][x] = y
    //data[3][x] = events
    //data[4][x] = population
    double[][] data = new double[5][];
    
    private double totEvents = 0.0;
    private double totPopulation = 0.0;
    
    private static Data d = null;
    
    private Data(){}
    
    public static Data getCurrent(){
        if (d == null){
            d = new Data();
        }
        return d;
    }
    
    public void setData (String csvPath) throws IOException{
        setData(new File(csvPath));
    }
    
    public void setData(File csv) throws IOException{
        this.csv = csv;
        //check and make sure the file exists and is not a directory
        if (csv.exists() && csv.isFile()){
            //load the data, if it is not successful throw an exception.
            try{
                loadData(csv);
            }catch(NumberFormatException nfe){
                throw new IOException("Incorrect values found in " + csv.getAbsolutePath());
            }
        } else {
            //if the file does not exist or is a directory throw an exception.
            throw new IOException("csv file " + csv.getAbsolutePath() + " could not be found." 
                    + System.getProperty("line.seperater")+
                    "check the file location is correct and it is not a directory");
        }
    }
    
    private void loadData(File csv) throws IOException, NumberFormatException{
        //open the file and get the number of rows
        BufferedReader fr = new BufferedReader(new FileReader(csv));
        int fileLength = 0;
        while (fr.readLine()!=null){fileLength++;}
        fr.close();
        
        //if we have no records throw an IOException stating this
        if ( fileLength == 0){ throw new IOException(csv.getAbsolutePath()+" has no readable records.");}
        
        //dimension the data array
        data = new double[5][fileLength];
        
        //reopen the file and load the data into the double array
        fr = new BufferedReader(new FileReader(csv));
        fileLength = 0;
        String line = "";
        while ( (line = fr.readLine()) != null ){
            String[] s = line.split(",");
            if ( s.length != 5 ){
                throw new IOException("Incorrect file structure for file " + csv.getAbsolutePath());
            }else{
                for (int i = 0; i < s.length; i++) {
                    Double D = new Double(s[i]);
                    if ( D.isNaN() ){
                        throw new NumberFormatException(D.toString() + " is not valid in this context check file "+ csv.getAbsolutePath());
                    }else{
                        data[i][fileLength] = Double.valueOf(s[i]).doubleValue();
                    }
                }
                //calculate the total events and population as we read in the file so we can standardise the population below
                totEvents += data[3][fileLength];
                totPopulation += data[4][fileLength];
            }
            fileLength++;
        }
        fr.close();
        
        if (Parameters.getCurrent().getStandardise() == 1){
            standardisePopulationRates();
        }

    }
    
    private void standardisePopulationRates(){
        double standardisationRate = totEvents / totPopulation;
        for (int i = 0; i < data[4].length; i++) {
            data[4][i] = data[4][i] * standardisationRate;
        }
    }
    
    public void setData(double[][] data){
        this.data = data;
        
        if (Parameters.getCurrent().getStandardise() == 1){
            //calculate the total events and population as we read in the file so we can standardise the population below
            for (int i = 0; i < this.data[3].length; i++) {
                totEvents += this.data[3][i];
                totPopulation += this.data[4][i];
            }
            standardisePopulationRates();
        }
    }
    
    /**
     * The file data is returned in a two dimensional array of doubles.  The first dimension
     * contains the five columns of data in the following order:
     * <ul>
     * <li>data[0][x] = unique identifier</li>
     * <li>data[1][x] = x</li>
     * <li>data[2][x] = y</li>
     * <li>data[3][x] = events</li>
     * <li>data[4][x] = population</li>
     * </ul>
     * 
     * @return a two dimensional array of doubles.  The first dimension has 5 elements relating to
     * the 5 columns of data and the second dimension is the file length
     */
    public double[][] getData(){return data;}
    
    @Override
    public String toString(){
        return csv.getAbsolutePath();
    }
}