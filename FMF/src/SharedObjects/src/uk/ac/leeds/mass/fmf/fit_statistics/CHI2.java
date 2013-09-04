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
