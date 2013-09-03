package uk.ac.leeds.mass.fmf.fit_statistics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author geo8kh
 */
public class SAE implements IGOF {

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
            return ( ( ret ) / N );
        } else {
            return 0.0;
        }
    }

    public String testName() {
        return "Standard Absolute Error";
    }

    public String fieldName() {
        return "SAE";
    }

    public boolean calibrateToLessThan() {
        return true;
    }

    public boolean isPerfect(double testStat) {
        if (testStat == 0.0) { return true; } else { return false; }
    }

}
