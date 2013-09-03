/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.fit_statistics;

/**
 *
 * @author geo8kh
 */
public class TotalError implements IGOF{

    public double test(double[][] calib, double[][] test, double N){
        return test(calib,test);
    }
    
    public double test(double[][] calib, double[][] test) {

        double ret = 0.0;

        for (int i = 0; i < calib.length; i++) {

            for (int j = 0; j < calib[i].length; j++) {

                ret += Math.abs(calib[i][j] - test[i][j]);

            }

        }

        return ( ret / 2.0 );
    }

    public String testName() {
        return "Total Error";
    }

    public String fieldName() {
        return "TE";
    }

    public boolean calibrateToLessThan() {
        return true;
    }

    public boolean isPerfect(double testStat) {
        if (testStat == 0.0) { return true; } else { return false; }
    }

}
