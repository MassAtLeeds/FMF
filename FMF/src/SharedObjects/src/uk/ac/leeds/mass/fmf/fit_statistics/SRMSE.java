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
 * Uses the Standardized Root Mean Square Error method
 *
 *@author Kirk Harland
 */
public class SRMSE implements Serializable, IGOF{

	public String fieldName(){return "SRMSE";}
	public String testName(){return "Standardized Root Mean Square Error";}
	public boolean calibrateToLessThan(){return true;}

    public double test(double[][] calib, double[][] test, double N){
        return test(calib,test);
    }
    
	public double test(double[][] calib, double[][] test){


		double m = (double)calib.length * (double)calib[0].length;
		double ss = 0;
		double T = 0;

		for (int i=0;i<calib.length;i++){
			for (int j=0;j<calib[i].length;j++){
				ss += (Math.pow(calib[i][j]-test[i][j],2)/m);
				T += (calib[i][j]/m);
			}
		}


		double d = Math.pow(ss,0.5)/T;

        if ( Double.isNaN(d) || Double.isInfinite(d)){
            return 99999.9999;
        } 
		return d;

	}

    public boolean isPerfect(double testStat){
        if ( testStat == 0.0 ){return true;}else{return false;}
    }

}