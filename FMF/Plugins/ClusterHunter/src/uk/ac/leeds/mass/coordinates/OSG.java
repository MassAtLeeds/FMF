/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.coordinates;

/**
 *
 * @author geo8kh
 */
public class OSG implements CoordinateCalculator{
    
    private double x;
    private double y;
    
    private OSG(){}
    
    public OSG(double x, double y){
        this.x = x;
        this.y = y;
    }

    public static double dist(double x1, double y1, double x2, double y2){
        double xDiff = Math.abs(x1-x2);
        double yDiff = Math.abs(y1-y2);
        return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
    }
    
    @Override
    public double dist(double x, double y) {
        return OSG.dist(this.x, this.y, x, y);
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    } 
    
    /**
     * This method returns either the x or y value of the OSG object offset by
     * the number of metres in the parameter.  The method does not alter the coordinates stored by the
     * object but rather returns the new coordinate value.  Passing in a +ve value for the 
     * metres parameter will move the X coordinate to the east if the isX parameter is true 
     * or the Y coordinate north if it is false.  Passing in a -ve value for the metres 
     * parameter will have the opposite effect moving the X coordinate west if isX is true 
     * or the Y coordinate south if it is false.  To access the new value for the X or Y coordinate 
     * use the getX or getY method.
     * 
     * @param metres valid double representing the number of metres to offset by
     * @param isX boolean to identify whether it is the x or y axis to be adjusted. 
     * true for x otherwise false
     * 
     * @return double value for the new coordinate location relating to either the x or y coordinate 
     * as specified by the isX parameter.
     */
    @Override
    public double getOffsetXY(double metres, boolean isX){
        //working going east or west
        if ( isX ){
            return x + metres;
        //going north or south
        }else{
            return y + metres;
        }
    }
    
    /**
     * This method will offset either the x or y value of the OSG object.
     * passing in a +ve value for the metres parameter will move the X coordinate to the east if
     * the isX parameter is true or the Y coordinate north if it is false.  Passing in a -ve value 
     * for the metres parameter will have the opposite effect moving the X coordinate west if isX is true 
     * or the Y coordinate south if it is false.  To access the new value for the X or Y coordinate 
     * use the getX or getY method.
     * 
     * @param metres valid integer representing the number of metres to offset by
     * @param isX boolean to identify whether it is the x or y axis to be adjusted. 
     * true for x otherwise false
     */
    @Override
    public void offsetXY(double metres, boolean isX){
        //working going east or west
        if ( isX ){
            x = getOffsetXY(metres,isX);
        //going north or south
        }else{
            y = getOffsetXY(metres,isX);
        }
    }

   
}