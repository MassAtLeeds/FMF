/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.fit_statistics;

/**
 *
 * @author geo8kh
 */
public class CellPercentageError implements IGOF {

    public double test(double[][] calib, double[][] test, double N){
        return test(calib,test);
    }
    
    public double test(double[][] calib, double[][] test) {

        double ret = 0.0;
        double N = 0.0;

        for (int i = 0; i < calib.length; i++) {

            for (int j = 0; j < calib[i].length; j++) {

                N += calib[i][j];

                ret += Math.abs(calib[i][j] - test[i][j]);

            }

        }

        if ( N > 0.0 && ret > 0.0 ) {
            return ( ( ret ) / N * 100.0 );
        } else if ( N == 0.0 && ret > 0.0 ) {
            return Math.abs( ret ) * 100.0;
        } else {
            return 0.0;
        }
    }

    public String testName() {
        return "Cell Percentage Error";
    }

    public String fieldName() {
        return "CPE";
    }

    public boolean calibrateToLessThan() {
        return true;
    }

    public boolean isPerfect(double testStat) {
        if (testStat == 0.0) { return true; } else { return false; }
    }

}
