/*
 * LICENSING INFORMATION TO GO HERE
 */
package uk.ac.leeds.mass.cluster;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;

/**
 * <p>
 * Singleton object to hold all of the parameters required by the GAM cluster hunting algorithm.
 * The object first parses any command line arguments passed to the algorithm and sets these in the
 * Parameters instance to be used by the algorithm.  Any missing parameters will be left at the default 
 * values.
 * </p>
 * <p>
 * To call from the command line pass in valid parameter key | value pairs delimited by a | character.
 * A valid file parameter would be "file|C:/SomePath/SomeFileName.csv".  Valid parameter keys and their 
 * explanations are:
 * <ul>
 * <li>"File" as the key and any valid path to the csv file to be used for the GAM search</li>
 * <li>"JSONFile" as the key and any valid path to the JSON file to be used for the GAM search.  
 * This option requires that a search term to identify events to be searched for clusters is also 
 * passed in through the "SearchTerm" parameter.  If the "SearchTerm" is not set the "JSONFile" parameter
 * will be ignored.  If this option is used it will override any parameter passed in for "File".</li>
 * <li>"SearchTerm" is a series of comma delimited text phrases to be searched for in the JSON file.</li>
 * <li>"CoordinateType" as the key and a valid integer constant from the list below
 * <ul>
 * <li>1 WGS84 Latitude and Longitude (the default setting)</li>
 * <li>2 British National Grid</li>
 * </ul>
 * </li>
 * <li>"RadiusMax" as the key and any valid double value representing the maximum radius in metres</li>
 * <li>"RadiusMin" as the key and any valid double value representing the minimum radius in metres</li>
 * <li>"RadiusIncrement" as the key and any valid double value representing the increment steps for the search circle radius in metres</li>
 * <li>"Overlap" as the key and a valid numeric value greater than 0 and less than 1 representing the amount of overlap between search circles
 * 0.99 being 99% overlap, very detailed search and 0.01 being 1% overlap, the default is 0.5.</li>
 * <li>"OutputDirectory" as the key and any valid path to a directory where the results from the algorithm are to be stored.  
 * The default will be path to the input file</li>
 * <li>"SignificanceThreshold" as the key and a valid value between 0.99999 and 0.00001 </li>
 * <li>"MinimumPopulation" as the key and any valid double value representing the minimum population required for significance testing (default 1.0)</li>
 * <li>"MinimumCases" as the key and any valid double value representing the minimum number of cases required for significance testing (default 1.0)</li>
 * <li>"StatisticType" as the key and either 1, 2 or 3
 * <ul>
 * <li>1 number of cases above the expected (cases - expected cases standardised to population) (the default setting)</li>
 * <li>2 ratio of cases over expected (cases / expected cases standardised to population)</li>
 * <li>3 probability of cases exceeding expected</li>
 * </ul>
 * </li>
 * <li>"CellNumer" as the key and any valid integer value representing the number of cells required on the longest
 * side of the output .asc file (default 100)</li>
 * <li>"Standardise" as the key and either 1 to standardise the population by the overall average event rate or 0 to perform 
 * no standardisation and use the population column as is (if standardisation has already taken place).</li>
 * <li>"UI" as the key and either 1 to indicate that a user interface is attached or 0 if it is not.  If a UI is attached then it should 
 * both serve and save the input and output data.  The default value for this parameter is 0.
 * </ul>
 *
 * @author Kirk Harland
 * @version 1.0
 */
public class Parameters {
    
    //the instance of Parameters to be instantiated and accessed by all other objects
    private static Parameters instance = null;
    
    //the file to be run against
    File csv = null;
    File outputDIR = null;
    File json = null;
    String[] searchTerm = null;
    
    //input parameters for the search algorithm
    private double radMax = 5000.0;
    private double radMin = 500.0;
    private double radIncrement = 500.0;
    private double overlp = 0.5;
    private int coordinateType = 1;
    
    //parameters for significance test
    private double sigThreshold = 0.0099;
    private double minPopulation = 1.0;
    private double minCases = 1.0;
    private int multipleTestReRuns = 0;
    private int statType = 1;
    private int cellNumber = 100;
    private int standardise = 1;
    private int ui = 0;
    
    public final static String FILE = "File";
    public final static String JSONFILE = "JSONFile";
    public final static String SEARCH_TERM = "SearchTerm";
    public final static String RADIUS_MAX = "RadiusMax";
    public final static String RADIUS_MIN = "RadiusMin"; 
    public final static String RADIUS_INCREMENT = "RadiusIncrement"; 
    public final static String OVERLAP = "Overlap";
    public final static String COORDINATE_TYPE = "CoordinateType";
    public final static String SIGNIFICANCE_THRESHOLD = "SignificanceThreshold";
    public final static String MINIMUM_POPULATION = "MinimumPopulation";
    public final static String MINIMUM_CASES = "MinimumCases";
    public final static String MULTIPLE_TEST_RERUNS = "MultipleTestReRuns";
    public final static String STATISTIC_TYPE = "StatisticType";
    public final static String OUTPUT_DIRECTORY = "OutputDirectory";
    public final static String CELL_NUMBER = "CellNumber";
    public final static String STANDARDISE = "Standardise";
    public final static String UI = "UI";
    
    public final static String FINISHED = "finished";
    
    private static ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
    
    private Parameters(){}
    
    public static void main(String[] args){
        
        //set the location where the logger should write to
        Logger.setPrintStream(System.out);
        
        //create the instance of Parameters that will be used by everything else
        Parameters pars = getCurrent();

        //set the parameters here that have been passed in.  All other parameters are
        //left at the default setting.
        //TODO: any problems parsing exit with message logged for users
        pars.parseParameters(args);
        
        boolean useJSON = false;
        boolean dataLoaded = false;
        Data data = Data.getCurrent();


        //check and see if we have a JSON file and search term
        if (pars.getJSONFile()!=null && pars.getSearchTerm()!=null){
            pars.setCSV( new FormatJSONFile().createCSV() );
            useJSON = true;
        }

        //test data file to be used if that is ok run algorithm otherwise report issue to user
        try{
            data.setData(pars.getCSV());
            dataLoaded = true;
        }catch(Exception e){
            Logger.log("Data Loading", Logger.messageSeverity.Error, pars.getClass().getName() );
            e.printStackTrace(Logger.getPrintStream());
        }

        
        //if all of the data loaded ok run the GAM algorithm
        if ( dataLoaded ){
            //run the algorithm
            Gamk gam = new Gamk(data);
            gam.gamAlgorithm();
            
            //now save the asc file
            //commented out at the moment because it is not working correctly 
            //coordinate locations are distorting
//            ASC asc = new ASC(gam.getResults());

            //identify the tweets contributing to the significant circles and write them to a JSON file
            if ( useJSON ){
                try{
                    IdentifyRecordsFromClusters json = new IdentifyRecordsFromClusters(pars.getJSONFile(),gam.getResults());
                }catch (IOException e){
                   Logger.log("Could not write results to text file in " + Parameters.getCurrent().outputDIR,
                            Logger.messageSeverity.Error, "IdentifyRecordsFromClusters");
                   e.printStackTrace(Logger.getPrintStream());
               }catch (JSONException e){
                   Logger.log("Could not write results to text file in " + Parameters.getCurrent().outputDIR,
                            Logger.messageSeverity.Error, "IdentifyRecordsFromClusters");
                   e.printStackTrace(Logger.getPrintStream());
               }
            }else{
                //save the txt file
                try{
                    TXT txt = new TXT(gam.getResults());
                }catch (IOException e){
                    Logger.log("Could not write results to text file in " + Parameters.getCurrent().outputDIR,
                        Logger.messageSeverity.Error, "TXT");
                    e.printStackTrace(Logger.getPrintStream());
                }
            }

        
        
        }
        
    }
    
    public static Parameters getCurrent(){
        if ( instance == null ){instance = new Parameters();}
        return instance;
    }
    
    public void parseParameters(String[] args){
        for (int i = 0; i < args.length; i++) {
            String[] valKey = args[i].split("\\|");
            
            //only try and use the arguments if it appears to be a key and value pair
            if ( valKey.length == 2 ){
                
                //set the file to be loaded if present in the parameters
                if ( valKey[0].equalsIgnoreCase(Parameters.FILE) ){ 
                    csv = new File(valKey[1]); 
                    if ( outputDIR == null ){ outputDIR = csv.getParentFile(); }
                }
                
                //set the JSON file to be loaded if present in the parameters
                else if ( valKey[0].equalsIgnoreCase(Parameters.JSONFILE) ){ 
                    json = new File(valKey[1]); 
                    if ( outputDIR == null ){outputDIR = json.getParentFile();}
                    //check and see if the file has been set and has set the output DIR is so override
                    else if(csv!=null && csv.getParentFile() == outputDIR){outputDIR = json.getParentFile();}
                }
                
                //set the search term
                else if ( valKey[0].equalsIgnoreCase(Parameters.SEARCH_TERM) ){ 
                    String[] terms = valKey[1].split(",");
                    if (terms.length>0){
                        searchTerm = new String[terms.length];
                        
                        for (int j = 0; j < terms.length; j++) {
                            searchTerm[j] = terms[j].trim();
                        }
                    }
                     
                }
                
                //set the radius max
                else if ( valKey[0].equalsIgnoreCase(Parameters.RADIUS_MAX) ){ radMax = new Double(valKey[1]).doubleValue(); }
                
                //set the radius min
                else if ( valKey[0].equalsIgnoreCase(Parameters.RADIUS_MIN) ){ radMin = new Double(valKey[1]).doubleValue(); }
                
                //set the radius increment
                else if ( valKey[0].equalsIgnoreCase(Parameters.RADIUS_INCREMENT) ){ radIncrement = new Double(valKey[1]).doubleValue(); }
                
                //set the radius overlap
                else if ( valKey[0].equalsIgnoreCase(Parameters.OVERLAP) ){ 
                    overlp = new Double(valKey[1]).doubleValue(); 
                    if ( !(overlp > 0.0 && overlp < 1.0) ){overlp = 0.5;}
                }
                
                //set the coordinate type
                else if ( valKey[0].equalsIgnoreCase(Parameters.COORDINATE_TYPE) ){ coordinateType = new Integer(valKey[1]).intValue(); }
                
                //set the significance threshold
                else if ( valKey[0].equalsIgnoreCase(Parameters.SIGNIFICANCE_THRESHOLD) ){ sigThreshold = new Double(valKey[1]).doubleValue(); }
                
                //set the minimum population count
                else if ( valKey[0].equalsIgnoreCase(Parameters.MINIMUM_POPULATION) ){ minPopulation = new Double(valKey[1]).doubleValue(); }
                
                //set the minimum case count
                else if ( valKey[0].equalsIgnoreCase(Parameters.MINIMUM_CASES) ){ minCases = new Double(valKey[1]).doubleValue(); }
                
                //set multiple test re runs
                else if ( valKey[0].equalsIgnoreCase(Parameters.MULTIPLE_TEST_RERUNS) ){ multipleTestReRuns = new Integer(valKey[1]).intValue(); }

                //set the statistic type
                else if ( valKey[0].equalsIgnoreCase(Parameters.STATISTIC_TYPE) ){ statType = new Integer(valKey[1]).intValue(); }
                
                //set the output directory
                else if ( valKey[0].equalsIgnoreCase(Parameters.OUTPUT_DIRECTORY) ){ outputDIR = new File(valKey[1]); }
                
                //set the asc cell size
                else if ( valKey[0].equalsIgnoreCase(Parameters.CELL_NUMBER) ){ cellNumber = new Integer(valKey[1]).intValue(); }
                
                //set whether automatic standardisation should take place or not.
                else if ( valKey[0].equalsIgnoreCase(Parameters.STANDARDISE) ){ this.standardise = new Integer(valKey[1]).intValue(); }
                
                //set whether a UI is attached or not.
                else if ( valKey[0].equalsIgnoreCase(Parameters.UI) ){ this.ui = new Integer(valKey[1]).intValue(); }
                
            }
            
        }
    }
    
    //Input file
    public File getCSV(){return csv;}
    //Allow this class to set the csv after initialisation
    protected void setCSV(File csv){this.csv = csv;}
    //Output directory
    public File getOutputDirectory(){return outputDIR;}
    //Input JSON File
    public File getJSONFile(){return json;}
    //search term to be used
    public String[] getSearchTerm(){return searchTerm;}
    
    //search algorithm parameters
    public double getRadiusMax(){return radMax;}
    public double getRadiusMin(){return radMin;}
    public double getRadiusIncrement(){return radIncrement;}
    public double getRadiusOverlap(){return overlp;}
    
    //significance test parameters
    public double getSignificanceThreshold(){return sigThreshold;}
    public double getMinimumPopulationCount(){return minPopulation;}
    public double getMinimumCaseCount(){return minCases;}
    public int getMultipleTestReRuns(){return multipleTestReRuns;}
    public int getStatisticType(){return statType;}
    public int getCellNumber(){return cellNumber;}
    public int getStandardise(){return this.standardise;}
    public int getUI() {return this.ui;}
    
    
    //geographic calculation object
    public CoordinateFactory getCoordinateFactory(double x, double y){return new CoordinateFactory(x,y,coordinateType);}

    
}