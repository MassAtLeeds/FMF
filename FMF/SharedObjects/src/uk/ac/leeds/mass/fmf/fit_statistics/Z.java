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
 * tij = observed proportion in a cell (cell count / total count)
 * pij = expected or projected proportion in a cell (cell count / total count)
 * N = total count
 * for the exact reasoning behind these statistics see Voas and Williamson 2001
 * Geographical and Environmental Modelling, Vol. 5, No. 2, 177-200
 * @author Kirk Harland
 */
public class Z implements Serializable, IGOF{

	public String fieldName(){return "Z";}
	public String testName(){return "<html>Z</html>";}
	public boolean calibrateToLessThan(){return true;}

    public double test(double[][] calib, double[][] test, double N){
        double Z = 0.0;

        for (int i=0; i<calib.length; i++) {
			for (int j=0; j<calib[i].length; j++) {
                Z = calcZScore( test[i][j], calib[i][j], N);
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
