/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.fit_statistics;

import java.io.Serializable;

/**
 * tij = observed proportion in a cell (cell count / total count)
 * pij = expected or projected proportion in a cell (cell count / total count)
 * N = total count
 * for the exact reasoning behind these statistics see Voas and Williamson 2001
 * Geographical and Environmental Modelling, Vol. 5, No. 2, 177-200
 * @author Kirk Harland
 */
public class ZSquared implements Serializable, IGOF{

	public String fieldName(){return "Z sq";}
	public String testName(){return "<html>Z<sup>2</sup></html>";}
	public boolean calibrateToLessThan(){return true;}

    public double test(double[][] calib, double[][] test, double N){
        double Z = 0.0;

        for (int i=0; i<calib.length; i++) {
			for (int j=0; j<calib[i].length; j++) {
                double z = calcZScore( test[i][j], calib[i][j], N);
				Z += (z*z);
			}
		}

        if ( Double.isNaN(Z) ){
            return 0.0;
        }

        return Z;
    }

	public double test(double[][] calib, double[][] test){

        double N=0.0;

		// Calculate total count
		for (int i=0; i<calib.length; i++) {
			for (int j=0; j<calib[i].length; j++) {
				N += calib[i][j];
			}
		}

        return test(calib, test, N);

	}

    public boolean isPerfect(double testStat){

        //this method needs to be completed
        //should return true if the testStat represents a perfect fit
        return false;
    }

    private double calcZScore(double T, double P, double N){

        double pij = P / N;
        double tij = T / N;
        double numerator = 0.0;

        //if pij = 0 then the pij value in the denominator is altered to be a
        //suitably small value 1/N.
        if ( pij == 0 ){
            pij = 1 / N;
            return tij / ( Math.pow( ((pij*(1-pij)) / N) , 0.5) );

        //if tij - pij > 0 (is a positive number)
        }else if (tij-pij > 0){
            numerator = (tij - pij) - (1 / (2*N));

        //if tij - pij < 0 (is a negative number)
        }else if (tij-pij < 0){
            numerator = (tij - pij) + (1 / (2*N));

        //catch everything else (should be where tij - pij = 0) and return 0
        }else{
            return 0;
        }

        return numerator / ( Math.pow( ( (pij*(1-pij)) / N ) , 0.5) );

    }

}