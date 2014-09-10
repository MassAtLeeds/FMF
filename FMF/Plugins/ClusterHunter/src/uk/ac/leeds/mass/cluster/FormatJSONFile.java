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
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Kirk Harland
 */
public class FormatJSONFile {

    
    public File createCSV(){
        
        File JSONFile = Parameters.getCurrent().getJSONFile();
        
        File outputDirectory = Parameters.getCurrent().getOutputDirectory();

        boolean write = true;
        if ( !outputDirectory.exists() ){
            write = outputDirectory.mkdirs();
        }else if ( !outputDirectory.isDirectory() ){
            write = false;
            Logger.log("Specified output location is not a directory : " + outputDirectory.getAbsolutePath(), 
                    Logger.messageSeverity.Information, "FormatJSONFile");
        }
        
        //write out the results
        if (write){
            
            String name = JSONFile.getName().substring(0, JSONFile.getName().indexOf("."));
            String outputFullFilePath = outputDirectory.getAbsolutePath() + 
                    System.getProperty("file.separator")+name + "_GAM.csv";
            
            ConvertJSONToCoordinates jsonReader = new ConvertJSONToCoordinates(JSONFile);
            ArrayList<Coordinates> base = jsonReader.getBasePopulation();
            ArrayList<Coordinates> events = jsonReader.getEventPopulation();

            String[] s = FormatJSONFile.joinCoordinates(base, events);

            File f = null;
            
            try{
                f = FormatJSONFile.createFile(outputFullFilePath, s);
            }catch(IOException ex){
                Logger.log(ex.getMessage(), Logger.messageSeverity.Error, "FormatJSONFile");
                ex.printStackTrace(Logger.getPrintStream());
            }

            return f;
            
        }else{
            return null;
        }
        
    }
    
        private static File createFile(String filePath, String[] output) throws IOException{
        File f = new File(filePath);
        if ( !f.isDirectory() ){
            if (f.exists()){f.delete();}
            f.createNewFile();
            FileWriter fw = new FileWriter(f);
            BufferedWriter bfw = new BufferedWriter(fw);
            for (int i = 0; i < output.length; i++) {
                if ( i>0 ){bfw.newLine();}
                bfw.append(output[i]);
            }
            bfw.flush();
            bfw.close();
        }
        return f;
    }
    
    private static ArrayList<Coordinates> aggregateCoordinates(Coordinates[] pop){

        Arrays.sort(pop);

        ArrayList<Coordinates> coords = new ArrayList<Coordinates>();
        coords.ensureCapacity(pop.length);

        for (int i = 0; i < pop.length; i++) {

            boolean aggregationTested = false;
            for (int j = i+1; j < pop.length; j++) {
                aggregationTested = true;
                if ( (pop[i].isAggregated() && !pop[i].shouldUse()) ){
                    break;
                }
                pop[i].aggregate(pop[j]);
                if ( pop[i].compareTo(pop[j]) != 0 ){
                    break;
                }
            }
            if ( pop[i].isAggregated() && pop[i].shouldUse() ){
                coords.add(pop[i]);
            }else if ( !aggregationTested && !pop[i].isAggregated() ){
                coords.add(pop[i]);
            }

        }

        coords.trimToSize();

        return coords;

    }


    private static String[] joinCoordinates (ArrayList<Coordinates> base, ArrayList<Coordinates> events  ){
        String[] output = new String[base.size()];

        Coordinates b = null;
        Coordinates e = null;

        Collections.sort(events);
        Collections.sort(base);

        int eventStartPosition = 0;
        //cycle through each of the base population
        for (int i = 0; i < base.size(); i++) {
            boolean tryToMatch = true;
            e = null;
            b = base.get(i);

            //find the start position for events
            for (int j = eventStartPosition; j < events.size(); j++) {
                //the x oordinates are equal
                int comp = b.compareTo(events.get(j));
                if (comp < 0 ){
                    tryToMatch = false;
                    break;
                } else if ( comp == 0 ){
                    eventStartPosition = j;
                    break;
                }
            }

            //should we try and match?
            if ( tryToMatch ){
                //check and see if we have a match
                for (int j = eventStartPosition; j < events.size(); j++) {
                    //make sure we have not moved out of the x coordinate match range
                    if ( b.compareTo(events.get(j)) != 0 ){break;}

                    if ( b.equals( events.get(j) ) ){
                        //matched
                        e = events.get(j);
                        break;
                    }
                }
            }

            if ( e != null ){
                output[i] = i + ", " + base.get(i).getX() + ", " + base.get(i).getY() + ", " + e.getCount() + ", " + base.get(i).getCount();
            }else{
                output[i] = i + ", " + base.get(i).getX() + ", " + base.get(i).getY() + ", 0, " + base.get(i).getCount();
            }
        }

        return output;
    }
    
}