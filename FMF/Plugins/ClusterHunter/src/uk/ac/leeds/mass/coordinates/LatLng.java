/**
 * <p>
 * Calculates the distance on the earths surface of points defined in decimal degrees latitude and longitude.
 * This class wraps the calculations implemented in the geodesy library which implements Thaddeus Vincenty's 
 * algorithms to solve the direct and inverse geodetic problems. 
 * </p>
 * <p>
 * See http://www.gavaghan.org/blog/free-source-code/geodesy-library-vincentys-formula/
 * </p>
 * 
 * 
 * This code may be freely used and modified on any personal or professional
 * project.  It comes with no warranty.
 */

package uk.ac.leeds.mass.coordinates;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.gavaghan.geodesy.GlobalPosition;

/**
 *
 * @author Kirk Harland
 */
public class LatLng implements CoordinateCalculator{
    
    private double lat;
    private double lng;

    //Ellipsoid required to calculate the distance across the earths surface.
    //Default ellipsoid used for all calculations is WGS84    
    private Ellipsoid ellipsoid = Ellipsoid.WGS84;
    
    //declare the default constructor private so that it cannot be called
    private LatLng(){}
    
    /**
     * Constructs a lightweight object based on the coordinates passed into the constructor
     * 
     * @param lat the latitude coordinate in Decimal Degrees 
     * @param lng the longitude coordinate in Decimal Degrees
     */
    public LatLng(double lng, double lat){
        this.lat = lat;
        this.lng = lng;
    }
    
    /**
     * <p>
     * Calculates the distance between this point and the point specified by the coordinates lat and lng.  
     * All coordinates should be in decimal degrees.  The calculation will be performed on the currently
     * set ellipsoid the default is WGS84. The default can be changed using the setEllipsoid method.
     * </p>
     * 
     * @param lat latitude of the point for which the distance is required
     * @param lng longitude of the point for which the distance is required
     * 
     * @return the distance over the specified ellipsoid in metres 
     */
    @Override
    public double dist(double lng, double lat){
        return LatLng.dist(this.lng, this.lat, lng, lat, ellipsoid);
    }
   
    /**
     * <p>
     * Calculates the distance between the point specified by the coordinates lat1 and lng1 and
     * the point specified by the coordinates lat2 and lng2.  All coordinates should be in decimal
     * degrees.
     * </p>
     * <p>
     * This is a convenience method for calculating distances based on the default ellipsoid WGS84.  
     * It is equivalent to calling 
     * LatLng.dist(double lat1, double lng1, double lat2, double lng2,org.gavaghan.geodesy.Ellipsoid.WGS84);
     * </p>
     * 
     * @param lat1 latitude for location 1
     * @param lng1 longitude for location 1
     * @param lat2 latitude for location 2
     * @param lng2 longitude for location 2
     * 
     * @return the distance over the WGS84 ellipsoid in metres 
     */
    public static double dist(double lng1, double lat1, double lng2, double lat2){
        return dist(lng1, lat1, lng2, lat2, Ellipsoid.WGS84);
    }
    
    /**
     * <p>
     * Calculates the distance between the point specified by the coordinates lat1 and lng1 and
     * the point specified by the coordinates lat2 and lng2.  All coordinates should be in decimal
     * degrees.
     * </p>
     * 
     * @param lat1 latitude for location 1
     * @param lng1 longitude for location 1
     * @param lat2 latitude for location 2
     * @param lng2 longitude for location 2
     * @param ellip the ellipsoid (org.gavaghan.geodesy.Ellipsoid) to be used for the distance calculation
     * 
     * @return the distance over the specified ellipsoid in metres
     */
    public static double dist(double lng1, double lat1, double lng2, double lat2, Ellipsoid ellip){
        GeodeticCalculator geoCalc = new GeodeticCalculator(); 
        GlobalPosition location1 = new GlobalPosition(lat1, lng1, 0.0);
        GlobalPosition location2 = new GlobalPosition(lat2, lng2, 0.0);
        return geoCalc.calculateGeodeticCurve(ellip, location1, location2).getEllipsoidalDistance();
    }

    /**
     * @return the latitude coordinate value in Decimal Degrees
     */
    @Override
    public double getY() {
        return lat;
    }

    /**
     * @param lat the latitude coordinate in Decimal Degrees
     */
    @Override
    public void setY(double lat) {
        this.lat = lat;
    }

    /**
     * @return the longitude coordinate value in Decimal Degrees
     */
    @Override
    public double getX() {
        return lng;
    }

    /**
     * @param lng the longitude coordinate in Decimal Degrees
     */
    @Override
    public void setX(double lng) {
        this.lng = lng;
    }

    /**
     * @return the Ellipsoid object which all calculations are based
     */
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }
    
    /**
     * This method will offset either the x or y value of the CoordinateCalculator object.
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
        if ( metres != 0.0){
            
            if ( isX ){
                lng = getOffsetXY(metres, isX);
            }else{
                lat = getOffsetXY(metres, isX);
            }
        }
    }

    /**
     * This method returns either the x or y value of the LatLng object offset by
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
        
        //set up the current coordinate in the return value
        double ret = 0.0;
        
        if ( isX ){
            ret = lng;
        }else{
            ret = lat;
        }
        
        if ( metres != 0.0){
            GeodeticCalculator geoCalc = new GeodeticCalculator(); 
            GlobalPosition location = new GlobalPosition(lat, lng, 0.0);
            double bearing = 0.0;
            //working going east or west
            if ( isX ){
                //going east
                if ( metres > 0 ){ 
                    bearing = 90.0;
                //going west
                }else{
                    bearing = 270.0;
                }
            //going north or south
            }else{
                //going north
                if ( metres > 0 ){ 
                    bearing = 0.0;
                //going south
                }else{
                    bearing = 180.0;
                }
            }
            GlobalCoordinates newLocation = 
                    geoCalc.calculateEndingGlobalCoordinates(ellipsoid, location, bearing, Math.abs(metres));
            
            //set the return value to the correct coordinate
            if ( isX ){
                ret = newLocation.getLongitude();
            }else{
                ret = newLocation.getLatitude();
            }
        }
        
        return ret;
    }
    
    /**
     * <p>
     * The Ellipsoid is the representation of the earths shape which all distance
     * calculations are based. The default ellipsoid used for coordinate calculations is WGS84.  
     * The ellipsoid object used is of the type org.gavaghan.geodesy.Ellipsoid.
     * </p>
     * 
     * <p>
     * Several common ellipsoid definitions are contained in this Ellipsoid implementation including: 
     * GRS80
     * GRS67
     * ANS
     * WGS72
     * Clarke1858
     * Clarke1880
     * Sphere
     * </p>
     * 
     * <p>
     * User defined ellipsoid can be created by calling the static helper methods in Ellipsoid
     * .fromAAndF Build an Ellipsoid from the semi major axis measurement and the flattening
     * .fromAAndInverseF Build an Ellipsoid from the semi major axis measurement and the inverse flattening
     * </p>
     * 
     * @param ellipsoid the ellipsoid (org.gavaghan.geodesy.Ellipsoid) to set.  
     */
    public void setEllipsoid(Ellipsoid ellipsoid) {
        this.ellipsoid = ellipsoid;
    }
}
