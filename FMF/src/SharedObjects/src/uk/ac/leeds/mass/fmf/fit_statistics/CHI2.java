/*
 *
 * CHI2.java
 *
 * Created on 13 Decemeber 2007 10:58
 *
 */

package uk.ac.leeds.mass.fmf.fit_statistics;

import java.io.Serializable;

/**
 *Calculates a goodness of fit statistic between two matricies.
 * Uses the Chi sq method
 *
 *@author Kirk Harland
 */
public class CHI2 implements Serializable, IGOF{

	public String fieldName(){return "Chi2";}
	public String testName(){return "<html>&chi;<sup>2</sup></html>";}
	public boolean calibrateToLessThan(){return true;}

    public double test(double[][] calib, double[][] test, double N){
        return test(calib,test);
    }

	public double test(double[][] calib, double[][] test){

		//sum of ((Tij-Pij)^2/Pij)
		//where
		//Tij is the number of interactions between origin i and destination j
		//Pij is the predicted number of interactions between origin i and destination j


		double chi=0;

		// Calculate Chi2
		for (int i=0; i<calib.length; i++) {
			for (int j=0; j<calib[i].length; j++) {
				chi += ( Math.pow(calib[i][j]-test[i][j],2) / test[i][j] );
			}
		}


        if ( Double.isNaN(chi) ){
            return 0.0;
        }
		return chi;

	}

    public boolean isPerfect(double testStat){

        //this method needs to be completed
        //should return true if the testStat represents a perfect fit
        return false;
    }

}
