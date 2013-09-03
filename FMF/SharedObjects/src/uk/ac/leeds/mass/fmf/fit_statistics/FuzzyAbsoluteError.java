/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.fit_statistics;

/**
 *
 * @author geo8kh
 */
public class FuzzyAbsoluteError  implements IGOF{

    @Override
    public double test(double[][] calib, double[][] test, double N){
        return test(calib,test);
    }

    @Override
    public double test(double[][] calib, double[][] test) {

        double ret = 0.0;

        for (int i = 0; i < calib.length; i++) {

            for (int j = 0; j < calib[i].length; j++) {

                if ( calib[i][j] > 3 || test[i][j] > 3 ){
                    ret += Math.abs(calib[i][j] - test[i][j]);
                }

            }

        }

        return  ret ;
    }

    @Override
    public String testName() {
        return "Fuzzy Absolute Error";
    }

    @Override
    public String fieldName() {
        return "FAE";
    }

    @Override
    public boolean calibrateToLessThan() {
        return true;
    }

    @Override
    public boolean isPerfect(double testStat) {
        if (testStat == 0.0) { return true; } else { return false; }
    }


}
