/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.cluster;

import java.util.ArrayList;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;

/**
 *
 * @author geo8kh
 */
public class Gamk extends FrameworkProcess{
    
    Data data = null;
    
    private double xMin = 0.0;
    private double xMax = 0.0; 
    private double yMin = 0.0;
    private double yMax = 0.0;
    private double height = 0.0;
    private double width = 0.0;
    
    private double radMin = Parameters.getCurrent().getRadiusMin();
    private double radMax = Parameters.getCurrent().getRadiusMax();
    private double radInc = Parameters.getCurrent().getRadiusIncrement();
    private double overlp = Parameters.getCurrent().getRadiusOverlap();
    
    int id =1;

    private PoissonTest sigTest = null;
            
    private ArrayList<SignificantCircle> results = new ArrayList<SignificantCircle>();
    
    private CoordinateFactory geog = null;
    
    // Constructors
    public Gamk(Data data) {
        this.data = data;
        this.processName = "Running Cluster Hunter...";
    }

    
    public ArrayList<SignificantCircle> getResults(){return results;}
    
    @Override
    public String toString() {return "Gamk" + data.toString();}
    
    private void setSearchBounds(){
        double[][] searchData = data.getData();
        for (int i = 0; i < searchData[1].length; i++) {
            //first pass through so set all of the variables
            if ( i == 0 ){
                xMin = searchData[1][i];
                xMax = searchData[1][i];
                yMin = searchData[2][i];
                yMax = searchData[2][i];
            }else{
                if (searchData[1][i] < xMin){xMin = searchData[1][i];}
                if (searchData[1][i] > xMax){xMax = searchData[1][i];}
                if (searchData[2][i] < yMin){yMin = searchData[2][i];}
                if (searchData[2][i] > yMax){yMax = searchData[2][i];}
            }
        }
        
        //get the height
        geog = Parameters.getCurrent().getCoordinateFactory(xMin, yMin);
        height = geog.dist(xMin, yMax);
        //get the width
        width = geog.dist(xMax, yMin);
        
    }           

                
    public void gamAlgorithm(){
        
        // Find max and min x, y values to define search region
        setSearchBounds();
        
        //setup the significance test
        sigTest = new PoissonTest(0,
                data.getData()[0].length,
                Parameters.getCurrent().getStatisticType(),
                Parameters.getCurrent().getMinimumPopulationCount(),
                Parameters.getCurrent().getMinimumCaseCount(),
                Parameters.getCurrent().getMultipleTestReRuns(),
                Parameters.getCurrent().getSignificanceThreshold());
        
        //get the number of times the algorithm has to step through the different size circles
        int rTimes = (int) ((radMax-radMin)/radInc + 1.0);
        initilise( 1 );
        
        //calculate the number of steps to report progress on
        double radius = radMin - radInc;
        for (int loop=0; loop < rTimes; loop++){
            radius += radInc;
            //get the increment required after each search
            double step = radius * overlp;

            //get the number of searches on the y axis
            int yTimes = (int) (height/step + 1.0);
            //get the number of searches on the x axis
            int xTimes = (int) (width/step + 1.0);
            
            this.stages+=(yTimes*xTimes);
        }

        radius = radMin - radInc;
        for (int loop=0; loop < rTimes; loop++){
            if ( cancelled ){break;}
            radius += radInc;
            sequentialSearch(radius);
        }
        
        if(!cancelled){this.finished();}

    }
    
    int nCalc = 0;//,TotalCalc=0;

    public void sequentialSearch(double radius){
        
        //data to be searched
        double[][] searchData = data.getData();
        
        int nDat, nCals, nHy;
        nDat = nCals = nHy = 0;

        //get the increment required after each search
        double step = radius * overlp;

        //get the number of searches on the y axis
        int yTimes = (int) (height/step + 1.0);
        //get the number of searches on the x axis
        int xTimes = (int) (width/step + 1.0);
        
        

        //make sure the Y coordinate is at the minimum of the search area
        geog.setY(yMin);
        
        //set the bouding box variables for the current circle
        double bbYmin = geog.getOffsetXY(-(radius * 1.05 ), false);
        double bbYmax = geog.getOffsetXY((radius * 1.05 ), false);
        
        double bbXmin = 0.0;
        double bbXmax = 0.0;
        
        // Grid search: northern loop
        for (int iRow=0; iRow < yTimes; iRow++){

            geog.setX(xMin);
            bbXmin = geog.getOffsetXY(-(radius * 1.05 ), true);
            bbXmax = geog.getOffsetXY((radius * 1.05 ), true);
          
            // grid search: easting loop
            for (int iCol = 0; iCol < xTimes; iCol++){

                nCalc++;
                //get data for circle
                double obsP = 0.0;
                double obsC = 0.0;
                int pointsInRadius = 0;
                for (int i = 0; i < searchData[0].length; i++) {
                    //only test the distance if the coordinates fall inside the bounding box
                    //of the circle to test
                    if ( searchData[1][i] > bbXmin && searchData[1][i] < bbXmax && searchData[2][i] > bbYmin && searchData[2][i] < bbYmax){
                        //point is inside the bounding box so test the distance
                        if ( geog.dist(searchData[1][i], searchData[2][i]) < radius ){
                           obsP += searchData[4][i];
                           obsC += searchData[3][i];
                           pointsInRadius++;
                        }
                    }
                }
                

                //Point found so try testing
                if(pointsInRadius > 0) {

                    nDat++;

                    if (sigTest.isWorthTesting(obsP, obsC)){
                        nHy++;
                        //debug.print(" tested, ");
                        if (sigTest.isSignificant(obsP, obsC)){

                            nCals ++;

                            results.add(new SignificantCircle(geog.getX(),geog.getY(), radius, sigTest.getStat()));

                        }// end if  significant

                    }// end of if worth testing
                    
                }

                //increment the x coordinate for the search
                geog.offsetXY(step, true);
                bbXmin = geog.getOffsetXY(-(radius * 1.05 ), true);
                bbXmax = geog.getOffsetXY((radius * 1.05 ), true);
                
                progress++;
                if(cancelled){break;}
                
            }//end of x loop
            
            //increment the y coordinate for the search
            geog.offsetXY(step, false);
            bbYmin = geog.getOffsetXY(-(radius * 1.05 ), false);
            bbYmax = geog.getOffsetXY((radius * 1.05 ), false);
            
            if(cancelled){break;}
            
        }// end of y loop


    }

    @Override
    public void runProcess() {
        gamAlgorithm();
    }

   
}