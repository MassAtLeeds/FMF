/*
 * LICENSING INFORMATION TO GO HERE
 */
package uk.ac.leeds.mass.cluster;

import uk.ac.leeds.mass.coordinates.CoordinateCalculator;
import uk.ac.leeds.mass.coordinates.LatLng;
import uk.ac.leeds.mass.coordinates.OSG;
        
/**
 *
 * @author Kirk Harland
 * @version 1.0
 */
public class CoordinateFactory implements CoordinateCalculator{
    
    public final static int WGS_84_LAT_LON = 1;
    public final static int BRITISH_NATIONAL_GRID = 2;
    
    private CoordinateCalculator calculator = null;
    
    public CoordinateFactory(double x, double y, int method){
        setCalculator(x,y,method);
    }

    @Override
    public double dist(double x, double y) {
        return calculator.dist(x, y);
    }

    @Override
    public double getY() {
        return calculator.getY();
    }

    @Override
    public void setY(double y) {
        calculator.setY(y);
    }

    @Override
    public double getX() {
        return calculator.getX();
    }

    @Override
    public void setX(double x) {
        calculator.setX(x);
    }
    
    private void setCalculator(double x, double y, int method){
        switch(method){
            case CoordinateFactory.WGS_84_LAT_LON:
                calculator = new LatLng(x,y);
                break;
            case CoordinateFactory.BRITISH_NATIONAL_GRID:
                calculator = new OSG(x,y);
                break;
            default:
                calculator = new LatLng(x,y);
        }
    }


    @Override
    public void offsetXY(double metres, boolean isX) {
        calculator.offsetXY(metres, isX);
    }

    @Override
    public double getOffsetXY(double metres, boolean isX) {
        return calculator.getOffsetXY(metres, isX);
    }
    
}
