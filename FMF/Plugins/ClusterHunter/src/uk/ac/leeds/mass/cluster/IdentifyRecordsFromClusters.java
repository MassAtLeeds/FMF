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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author geo8kh
 */
public class IdentifyRecordsFromClusters extends JSONReader{

    private CoordinateFactory geog = Parameters.getCurrent().getCoordinateFactory(0.0, 0.0);
    private ArrayList<SignificantCircle> results;
    
    private BufferedWriter bw = null;
    private BufferedWriter bwTweets = null;
    private BufferedWriter bwTweetsSummary = null;
    private boolean write = true;
    private boolean firstLine = true;
    
    private JSONObject objTweets = new JSONObject();
    private JSONObject objTweetsSummary = new JSONObject();
    
    public IdentifyRecordsFromClusters(File file, ArrayList<SignificantCircle> results) throws IOException, JSONException{
        
        super(file);
        
        this.results = results;

        File outputDirectory = Parameters.getCurrent().getOutputDirectory();
        
        if ( !outputDirectory.exists() ){
            write = outputDirectory.mkdirs();
        }else if ( !outputDirectory.isDirectory() ){
            write = false;
            Logger.log("Specified output location is not a directory : " + outputDirectory.getAbsolutePath(), 
                    Logger.messageSeverity.Information, "IdentifyRecordsFromClusters");
        }

        //get the name of the input file
        String csv = Parameters.getCurrent().getCSV().getName();

        String outputName = "";
        if ( csv.endsWith(".csv") ){outputName = csv.substring(0, csv.length()-4);}
        File output = new File(outputDirectory + System.getProperty("file.separator") + outputName + ".json");
        File tweets = new File(outputDirectory + System.getProperty("file.separator") + outputName + "_tweets.json");
        File tweetsSummary = new File(outputDirectory + System.getProperty("file.separator") + outputName + "_tweets_summary.json");

        if (tweetsSummary.exists()){tweetsSummary.delete();}
        tweetsSummary.createNewFile();
        bwTweetsSummary = new BufferedWriter(new FileWriter(tweetsSummary));
        
        if (tweets.exists()){tweets.delete();}
        tweets.createNewFile();
        bwTweets = new BufferedWriter(new FileWriter(tweets));
        
        if (output.exists()){output.delete();}
        output.createNewFile();
        bw = new BufferedWriter(new FileWriter(output));
        
        super.readFile();
        
        JSONObject master = new JSONObject();
        
        //add the parameter values to the object.
//        JSONArray searchTerms = new JSONArray();
//        searchTerms.put(Parameters.getCurrent().getSearchTerm());
        master.put(Parameters.SEARCH_TERM,Parameters.getCurrent().getSearchTerm());
        master.put(Parameters.RADIUS_MIN,Parameters.getCurrent().getRadiusMin());
        master.put(Parameters.RADIUS_MAX,Parameters.getCurrent().getRadiusMax());
        master.put(Parameters.RADIUS_INCREMENT,Parameters.getCurrent().getRadiusIncrement());
        master.put(Parameters.OVERLAP,Parameters.getCurrent().getRadiusOverlap());
        master.put(Parameters.SIGNIFICANCE_THRESHOLD,Parameters.getCurrent().getSignificanceThreshold());
        
        JSONArray out = new JSONArray();
        //for each significant circle write out a JSON object to the output file
        for (SignificantCircle significantCircle : results) {
            JSONObject obj = new JSONObject();
            obj.put("clusterID", significantCircle.getID());
            obj.put("lat", significantCircle.getY());
            obj.put("lon", significantCircle.getX());
            obj.put("radius", significantCircle.getRadius());
            obj.put("sign", significantCircle.getValue());
            ArrayList<Long> points = significantCircle.getTweets();
            JSONArray a = new JSONArray();
            //create an array of points for all of the tweet IDs contributing to this circle
            for (Long long1 : points) {
                a.put(long1.toString());
            }
            obj.put("points", a);

            out.put(obj);
        }                
        
        master.put("clusters", out);
        master.write(bw);
        
        bw.flush();
        bw.close();
        
        //check and make sure we have written something to the file to be closed off...
        if (!firstLine){
            try {
                bwTweetsSummary.append("]");
                bwTweets.append("]");
            } catch (IOException ex) {
                Logger.log("Error wrtiing tweet " + tweetID, Logger.messageSeverity.Error, "IdentifyRecordsFromClusters");
            }
        }
        
        bwTweets.flush();
        bwTweets.close();
        
        bwTweetsSummary.flush();
        bwTweetsSummary.close();
        
    }
    
    @Override
    protected void doActionWithValidCoordinates(double x, double y) {
        
        boolean firstAdd = true;
        
        geog.setX(x);
        geog.setY(y);
        
        
        //write out the results
        if (write && isEvent){
            

             double radius = 0.0;
             boolean radiusChanged = true;
             double bbYmin = 0.0;
             double bbYmax = 0.0;
             double bbXmin = 0.0;
             double bbXmax = 0.0;

             for (SignificantCircle significantCircle : results) {

                 double rad = significantCircle.getRadius();
                 radiusChanged = (rad!=radius);
                 if ( radiusChanged ){
                     radius = rad;
                     bbYmin = geog.getOffsetXY(-(radius * 1.05 ), false);
                     bbYmax = geog.getOffsetXY((radius * 1.05 ), false);
                     bbXmin = geog.getOffsetXY(-(radius * 1.05 ), true);
                     bbXmax = geog.getOffsetXY((radius * 1.05 ), true);
                 }

                 if ( significantCircle.getX() > bbXmin && significantCircle.getX() < bbXmax && 
                         significantCircle.getY() > bbYmin && significantCircle.getY() < bbYmax){
                     //point is inside the bounding box so test the distance
                     if ( geog.dist(significantCircle.getX(), significantCircle.getY()) < radius ){
                        //add the tweet ID to the significant circle
                         significantCircle.addTweet(new Long(tweetID));
                         
                         //add the tweet to a subset output
                         if ( firstAdd ){
                             try {
                                 //no longer the first time this tweet has been examined
                                 //so don't add any duplicates
                                 firstAdd =false;
                                 
                                 //create the output for the summary file
                                 JSONObject tweetSummary = new JSONObject();
                                 tweetSummary.put("text", tweetText);
                                 
                                 JSONArray coords = new JSONArray();
                                 coords.put(0, y);
                                 coords.put(1, x);
                                 JSONObject geo = new JSONObject();
                                 geo.put("coordinates", coords);
                                 geo.put("type", "Point");
                                 tweetSummary.put("geo", geo);
                                 
                                 tweetSummary.put("id",tweetID.toString());
                                 
                                 if (!firstLine){
                                    bwTweetsSummary.append(",");
                                    bwTweets.append(",");
//                                     bwTweetsSummary.newLine();
//                                     bwTweets.newLine();
                                 }else{
                                     bwTweetsSummary.append("[");
                                     bwTweets.append("[");
                                     firstLine = false;
                                 }
                                 
//                                 tweetSummary.write(bwTweetsSummary);
                                 bwTweetsSummary.write(tweetSummary.toString());
                                 
                                 //create the output all of the tweet information in
                                 bwTweets.append(o.toString());
//                                 o.write(bwTweets);
                                 
                             } catch (JSONException ex) {
                                 Logger.log("Error wrtiing tweet " + tweetID, Logger.messageSeverity.Error, "IdentifyRecordsFromClusters");
                                 ex.printStackTrace(Logger.getPrintStream());
                             } catch (IOException ex){
                                 Logger.log("Error wrtiing tweet " + tweetID, Logger.messageSeverity.Error, "IdentifyRecordsFromClusters");
                                 ex.printStackTrace(Logger.getPrintStream());
                             }
                         }
                         
                     }
                 }
             }
            
        }
    }
    
}