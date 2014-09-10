package uk.ac.leeds.mass.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.*;

/**
 *
 * @author Kirk Harland
 */
public abstract class JSONReader{
    
    protected String line="";
    protected String tweetText = "";
    protected File file = null;
    protected boolean isEvent = false;
    protected String tweetID = "";
    protected JSONObject o = null;
    
    protected JSONReader(){}
    
    public JSONReader(File file){
        this.file = file;        
    }
    
    protected void readFile(){
        BufferedReader fr = null;
        try {
            
            //create the file reader
            fr = new BufferedReader(new FileReader(file));
            
            while ( (line=fr.readLine()) != null ){

                try{
                    o = new JSONObject(line);
                    
                    if (o!=null){

                        //get the coordinates
                        JSONObject geo = o.getJSONObject("geo");
                        JSONArray coordinates = geo.getJSONArray("coordinates");

                        Double x = null;
                        if ( coordinates.get(1) instanceof Double ){
                            x = (Double)coordinates.get(1);
                        }
                        Double y = null;
                        if ( coordinates.get(0) instanceof Double ){
                            y = (Double)coordinates.get(0);
                        }
                        
                        //check and make sure we have valid coordinates
                        if (x!=null && y!=null && !x.isNaN() && !y.isNaN()){
                            
                            tweetID = o.get("id").toString();
                         
                            tweetText = o.get("text").toString();
                            
                            isEvent = testEventStatus();
                                    
                            doActionWithValidCoordinates(x.doubleValue(), y.doubleValue());
                            
                        }
                    }
                    
                }catch(JSONException ex){
                    //if the code catches this exception, there are no geographical coordinates for this record
                    //so swallow the error and continue.
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.log("File not found: " + file.getAbsolutePath(), Logger.messageSeverity.Error, "JSONReader");
            ex.printStackTrace(Logger.getPrintStream());
        } catch (IOException ex) {
            Logger.log("IOException thrown", Logger.messageSeverity.Error, "JSONReader");
            ex.printStackTrace(Logger.getPrintStream());
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.log("Cannot close file reader for file: " + file.getAbsolutePath(), 
                        Logger.messageSeverity.Error, "JSONReader");
                ex.printStackTrace(Logger.getPrintStream());
            }
        }
    }
    
    
    private boolean testEventStatus(){
        for (int i = 0; i < Parameters.getCurrent().getSearchTerm().length; i++) {
           if(tweetText.toUpperCase().contains(Parameters.getCurrent().getSearchTerm()[i].toUpperCase())){
               return true;
           }
        }
        return false;
    }
    
    
    protected abstract void doActionWithValidCoordinates(double x, double y);
    
    
    
}