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
 *
 * @author Kirk Harland
 */
public class ASC {
    
    private ArrayList<SignificantCircle> circles = null;
    
    private double xMin = 0.0;
    private double xMax = 0.0;
    private double yMin = 0.0;
    private double yMax = 0.0;
    
    private int cols = 0;
    private int rows = 0;

    private int cellNumber = Parameters.getCurrent().getCellNumber();
    private double cellSize = 0.0;
    private double cellSizeCoords = 0.0;
    private double yCellSize = 0.0;
    private double xCellSize = 0.0;
    
    //[x][][] = rows id
    //[][x][] = columns id
    //[][][0] = X coord
    //[][][1] = Y coord
    //[][][2] = Value
    private double[][][] grid = null;
    
    //variables required for quantize
    private double lastRadius = -1;   
    private int m = 0;   
    private int numb;   
    private double xker[];   
    private double xadr[];   
    private double yadr[];
    private boolean quantizeOn = true;
    
    
    /**
     * Creates a new object for creating an ARCGIS ASCII .asc file.  Two parameters are accepted
     * the first is the results from the gam cluster hunting algorithm and the second is the resolution
     * of the cells for the output grid.
     * 
     * @param results ArrayList of type SignificantCircle holding all of the circles that tested significant
     */
    public ASC(ArrayList<SignificantCircle> results){
        circles = results;
        
        setLowerLeftCoordinatesAndDimensionGrid();
        
        CoordinateFactory geog = Parameters.getCurrent().getCoordinateFactory(xMin, yMin);
        
        for (int row = grid.length-1; row > -1; row--) {

            for (int column = 0; column < grid[row].length; column++) {
                //put in the x and y
                grid[row][column][0] = geog.getX();
                grid[row][column][1] = geog.getY();
                grid[row][column][2] = 0.0;

                geog.setX(geog.getX()+cellSizeCoords);
//                geog.offsetXY(cellSize, true);
            }
            geog.setX(xMin);
            geog.setY(geog.getY()+cellSizeCoords);
//            geog.offsetXY(cellSize, false);
        }
        
        //cycle the circles and calculate the values.
        for (int i = 0; i < circles.size(); i++) {
            geog.setX(circles.get(i).getX());
            geog.setY(circles.get(i).getY());
            quantize(geog,
                    circles.get(i).getRadius(),
                    circles.get(i).getValue());
        }
        
        try{
            writeOutFile();
        }catch(IOException ex){
            Logger.log("Error writing .asc file", Logger.messageSeverity.Error, "ASC");
            ex.printStackTrace(Logger.getPrintStream());
        }
    }
    
    /**
     * Method extracted directly from geotools class uk.ac.leeds.ccg.raster.circleRaster.
     *
     * produce a kernel density surface at x,y with radius r and height value  
     * after Epanechnikov (1969) and Brunsdon (1990)  
     */   

    protected final void quantize(CoordinateFactory geog,double radius,double value){   
        
        if(radius<(cellSize)){   
            addToCell(geog.getX(),geog.getY(),value);   
            return;   
        }   
        if( radius != lastRadius ){   
            lastRadius = radius;   
            double rsq = radius * radius;   
            double min = -radius-cellSize/2.0;   
            double max = radius+cellSize/2.0;   
               
            numb = (int)( Math.ceil( ( 2.0 * ( radius+cellSize ) ) ) / cellSize );   
            xker = new double[numb*numb];   
            xadr = new double[numb*numb];   
            yadr = new double[numb*numb]; 
                        
            double yy, xx, xxsq, dis, sum;   
            
            xx = min;      
            m = 0;
            sum = 0;   
            for( int i=0; i<=numb; i++ ){   
                yy = max;   
                xxsq = xx * xx;   
                for ( int j=0; j<=numb; j++ ){   
                    dis = xxsq + yy * yy;   
                    if( dis <= rsq ){   
                        //System.out.println("CRa->x "+xx+" y "+yy+" "+dis+" "+rsq);   
                        xadr[m] = xx;
                        yadr[m] = yy;   
                        xker[m] = 1.0d - dis / rsq;   
                        sum += xker[m];   
                        m++;   
                    }   
                    yy -= cellSize;   
                }   
                xx += cellSize;   
            }
            
            if( sum > 0.0d ){   
                sum =( 1.0d / sum );   
            }
            
            for(int j=0;j<m;j++){   
                xker[j]*=sum;   
            }   
           
        
        }

        // now apply it to the cells   
        for(int j=0;j<m;j++){   
            if(quantizeOn){
                addToCell(geog.getOffsetXY(xadr[j], true),geog.getOffsetXY(yadr[j], false),(value*xker[j]));   
//                addToCell(geog.getX()+xadr[j],geog.getY()+yadr[j],(value*xker[j]));   
            }else{   
                addToCell(geog.getOffsetXY(xadr[j], true),geog.getOffsetXY(yadr[j], false),value);
                //addToCell(x+xadr[j],y+yadr[j],value);   
            }
        }

    } 

    private void addToCell(double x, double y, double value){
        
        CoordinateFactory geog = Parameters.getCurrent().getCoordinateFactory(x, y);
        double xIndex = Math.floor( geog.dist(xMin,y) / (double)xCellSize );
        double yIndex = Math.floor( geog.dist(x,yMin) / (double)yCellSize );
        
//        double xIndex = Math.floor( geog.dist(xMin,y) / (double)cellSize );
//        double yIndex = Math.floor( geog.dist(x,yMin) / (double)cellSize );
        
        int ix = ((int)xIndex);
        int iy = (grid.length - 1) - ((int)yIndex);
        
        try{
            grid[iy][ix][2] += value;
        }catch(ArrayIndexOutOfBoundsException ex){
            Logger.log("Cell location outside of grid in asc file creation", Logger.messageSeverity.Warning, "ASC");
            ex.printStackTrace(Logger.getPrintStream());
        }
        
    }
    
    //get the coordinate for the lower left of the grid and work out the dimensions
    //required for the array to hold the rest of the grid output results
    private void setLowerLeftCoordinatesAndDimensionGrid(){
        //get the min and max X and Y coordinates adjusted for the radius of the circle (+ 1 cell)
        for (int i = 0; i < circles.size(); i++) {
            //calculate bounds for circle
            CoordinateFactory geog = Parameters.getCurrent().getCoordinateFactory(circles.get(i).getX(), circles.get(i).getY());
            //if we are on the first set of coordinates just set them
            double xmin = geog.getOffsetXY(-circles.get(i).getRadius(), true);
            double xmax = geog.getOffsetXY(circles.get(i).getRadius(), true);
            double ymin = geog.getOffsetXY(-circles.get(i).getRadius(), false);
            double ymax = geog.getOffsetXY(circles.get(i).getRadius(), false);
            if (i==0){
                xMin = xmin;
                xMax = xmax;
                yMin = ymin;
                yMax = ymax;
            }else{
                if ( xmin < xMin ){xMin = xmin;}
                if ( xmax > xMax ){xMax = xmax;}
                if ( ymin < yMin ){yMin = ymin;}
                if ( ymax > yMax ){yMax = ymax;}
            }
        }
        
        //new method
        calculateCellSizeForCoordinateSystem();
//        CoordinateFactory geog = Parameters.getCurrent().getCoordinateFactory(xMin, yMin);
//        double width = geog.dist(xMax, yMin);
//        double height = geog.dist(xMin, yMax);
//        cellSize = (int)Math.ceil(Math.max(width, height)/cellNumber);
        
//        rows = (int)Math.ceil(height / cellSize);
//        cols = (int)Math.ceil(width / cellSize);
        
        grid = new double[rows][cols][3];
        
    }
    
    private void calculateCellSizeForCoordinateSystem(){
        CoordinateFactory geog = Parameters.getCurrent().getCoordinateFactory(xMin, yMin);
        double width = geog.dist(xMax, yMin);
        double height = geog.dist(xMin, yMax);
        
        double widthCellSizeMetres = (int)Math.ceil(width/cellNumber);
        double widthCellSizeCoords = Math.abs(Math.abs(xMin) - Math.abs(geog.getOffsetXY(widthCellSizeMetres, true)));
        //get the distance the width coords relate to in height
        double distHeight = geog.dist(xMin, yMin+widthCellSizeCoords);
        int widthRows = (int)Math.abs(Math.ceil(height/distHeight));
//        int widthRows = (int)Math.abs(Math.ceil((Math.abs(yMax) - Math.abs(yMin))/widthCellSizeCoords))+1;
        int widthColumns = (int)Math.abs(Math.ceil((Math.abs(xMax) - Math.abs(xMin))/widthCellSizeCoords))+1;
        
        double heightCellSizeMetres = (int)Math.ceil(height/cellNumber);
        double heightCellSizeCoords = Math.abs(Math.abs(yMin) - Math.abs(geog.getOffsetXY(heightCellSizeMetres, false)));
        int heightRows = (int)Math.abs(Math.ceil((Math.abs(yMax) - Math.abs(yMin))/heightCellSizeCoords))+1;
        double distWidth = geog.dist(xMin+heightCellSizeCoords, yMin);
        int heightColumns = (int)Math.abs(Math.ceil(width/distWidth));
//        int heightColumns = (int)Math.abs(Math.ceil((Math.abs(xMax) - Math.abs(xMin))/heightCellSizeCoords))+1;        
  
        if( widthRows <= cellNumber && widthColumns <= cellNumber ){
            cellSize = widthCellSizeMetres;
            xCellSize = widthCellSizeMetres;
            yCellSize = distHeight;
            cellSizeCoords = widthCellSizeCoords;
            rows = widthRows;
            cols = widthColumns;
        }else if(heightRows <= cellNumber && heightColumns <= cellNumber){
            cellSize = heightCellSizeMetres;
            xCellSize = distWidth;
            yCellSize = heightCellSizeMetres;
            cellSizeCoords = heightCellSizeCoords;
            rows = heightRows;
            cols = heightColumns;
        }else{
            cellSize = widthCellSizeMetres;
            cellSizeCoords = widthCellSizeCoords;
            rows = widthRows;
            cols = widthColumns;
            Logger.log("maximum cell number cannot be matched over both dimensions using width", 
                    Logger.messageSeverity.Warning, "ASC");
        }
      
    }
    
    private void writeOutFile() throws IOException{
        
        boolean write = true;
        
        File outputDirectory = Parameters.getCurrent().getOutputDirectory();
        
        if ( !outputDirectory.exists() ){
            write = outputDirectory.mkdirs();
        }else if ( !outputDirectory.isDirectory() ){
            write = false;
            Logger.log("Specified output location is not a directory : " + outputDirectory.getAbsolutePath(), 
                    Logger.messageSeverity.Information, "ASC");
        }
        
        //write out the results
        if (write){
            //get the name of the input file
            String csv = Parameters.getCurrent().getCSV().getName();
            
            String outputName = "";
            if ( csv.endsWith(".csv") ){outputName = csv.substring(0, csv.length()-4);}
            File output = new File(outputDirectory + System.getProperty("file.separator") + outputName + ".asc");
            
            if (output.exists()){output.delete();}
            output.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));

            //get the cell size in the correct coordinate system
            CoordinateFactory geog = Parameters.getCurrent().getCoordinateFactory(xMin, yMin);
            double cellsize = Math.abs(Math.abs(xMin) - Math.abs(geog.getOffsetXY(cellSizeCoords, true)));
            
            bw.append("ncols "+cols);
            bw.newLine();
            bw.append("nrows "+rows);
            bw.newLine();
            bw.append("xllcorner "+xMin);
            bw.newLine();
            bw.append("yllcorner "+yMin);
            bw.newLine();
            bw.append("cellsize "+cellSizeCoords);
            bw.newLine();
            bw.append("NODATA_value -9999");

            for (int rowCount = 0; rowCount < grid.length; rowCount++) {
                bw.newLine();
                for (int colCount = 0; colCount < grid[rowCount].length; colCount++) {
                    if(colCount>0){bw.append(" ");}
                    String s = ""+grid[rowCount][colCount][2];
                    bw.append(s);
                }
            }
            
            bw.flush();
            bw.close();
            
        }

    }
    
    
}