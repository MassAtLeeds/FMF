/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.cluster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Write out a .txt file tab delimited to the specified location containing the 
 * results from the GAM algorithm with the fields:
 * <ul>
 * <li>unique identifier</li>
 * <li>x</li>
 * <li>y</li>
 * <li>radius</li>
 * <li>value</li>
 * </ul>
 * 
 * Kirk Harland
 */
public class TXT {
    
    private TXT(){}
    public TXT(ArrayList<SignificantCircle> results) throws IOException{
        boolean write = true;
        
        File outputDirectory = Parameters.getCurrent().getOutputDirectory();
        
        if ( !outputDirectory.exists() ){
            write = outputDirectory.mkdirs();
        }else if ( !outputDirectory.isDirectory() ){
            write = false;
            Logger.log("Specified output location is not a directory : " + outputDirectory.getAbsolutePath(), 
                    Logger.messageSeverity.Information, "TXT");
        }
        
        //write out the results
        if (write){
            //get the name of the input file
            String csv = Parameters.getCurrent().getCSV().getName();
            
            String outputName = "";
            if ( csv.endsWith(".csv") ){outputName = csv.substring(0, csv.length()-4);}
            File output = new File(outputDirectory + System.getProperty("file.separator") + outputName + ".txt");
            
            if (output.exists()){output.delete();}
            output.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));

            for (SignificantCircle significantCircle : results) {
                bw.append(significantCircle.toString());
                bw.newLine();
            }
            
            bw.flush();
            bw.close();
            
        }

    }
    
}