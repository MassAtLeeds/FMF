/*
 *   The Flexible Modelling Framework is a Social Science application for 
 *   synthesising individual level populations
 *   Copyright (C) 2013  Kirk Harland
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contact email: k.harland@leeds.ac.uk
 */

package uk.ac.leeds.mass.fmf.fit_statistics;

import java.io.Serializable;

/**
 *Calculates a goodness of fit statistic between two matricies.
 * Uses the R-squared method
 *
 *@author Kirk Harland
 */
public class R2 implements Serializable, IGOF{

	public String fieldName(){return "R2";}
	public String testName(){return "R squared";}
	public boolean calibrateToLessThan(){return false;}

    public double test(double[][] calib, double[][] test, double N){
        return test(calib,test);
    }
    
	public double test(double[][] calib, double[][] test){

		double eq1 = 0;
		double eq2 = 0;
		double eq3 = 0;
		double obsMean;
		double preMean;
		double obsTot=0;
		double preTot=0;

		// Calculate averages
		for (int i=0; i<calib.length; i++) {
			for (int j=0; j<calib[i].length; j++) {
				obsTot += calib[i][j];
				preTot += test[i][j];
			}
		}

		obsMean = obsTot / (calib.length*calib[0].length);
		preMean = preTot / (calib.length*calib[0].length);

		// Calculate R2
		for (int i=0; i<calib.length; i++) {
			for (int j=0; j<calib[i].length; j++) {
				eq1 += (calib[i][j]-obsMean) * (test[i][j]-preMean);
				eq2 += Math.pow((calib[i][j]-obsMean),2);
				eq3 += Math.pow((test[i][j]-preMean),2);
			}
		}

		double R2 = Math.pow((eq1 / Math.pow(eq2*eq3,0.5)),2);

        if ( Double.isNaN(R2) ){
            return 0.0;
        }
		return R2;

	}

    public boolean isPerfect(double testStat){
        if ( testStat == 1.0 ){return true;}else{return false;}
    }
}

