/*
 * LICENSE INFORMATION TO GO HERE
 */
package uk.ac.leeds.mass.coordinates;

/**
 * Interface for providing standard methods to calculate distances between coordinates in 
 * different projections.
 * 
 * @author Kirk Harland
 * @version 1.0
 */
public interface CoordinateCalculator {
    /**
     * Calculate the distance between this coordinate location and the location coordinates
     * passed in.
     * @param x the x destination coordinate
     * @param y the y destination coordinate
     * @return the distance to the specified point in metres
     */
    public double dist(double x, double y);
    /**
     * @return the current y coordinate of the point as a double 
     */
    public double getY();
    /**
     * @param y adjust the current coordinate y to the specified double value
     */
    public void setY(double y);
    /**
     * @return the current x coordinate of the point as a double
     */
    public double getX();
    /**
     * @param x adjust the current coordinate x to the specified double value
     */
    public void setX(double x);
    
    /**
     * This method will offset either the x or y value of the CoordinateCalculator object.
     * passing in a +ve value for the metres parameter will move the X coordinate to the east if
     * the isX parameter is true or the Y coordinate north if it is false.  Passing in a -ve value 
     * for the metres parameter will have the opposite effect moving the X coordinate west if isX is true 
     * or the Y coordinate south if it is false.  To access the new value for the X or Y coordinate 
     * use the getX or getY method.
     * 
     * @param metres valid double representing the number of metres to offset by
     * @param isX boolean to identify whether it is the x or y axis to be adjusted. 
     * true for x otherwise false
     */
    public void offsetXY(double metres, boolean isX);
    
    /**
     * This method returns either the x or y value of the CoordinateCalculator object offset by
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
    public double getOffsetXY(double metres, boolean isX);

}