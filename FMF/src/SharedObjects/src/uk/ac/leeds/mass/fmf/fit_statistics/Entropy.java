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
 *Calculates the entropy in the model results matrix.
 *
 *@author Kirk Harland
 */
public class Entropy  implements Serializable, IGOF{

	public String fieldName(){return "Entropy";}
	public String testName(){return "Entropy (standard entropy measure)";}
	public boolean calibrateToLessThan(){return false;}

    public double test(double[][] calib, double[][] test, double N){
        return test(calib,test);
    }
    
	public double test(double[][] calib, double[][] test){


		double Nq=0;
		double Hq=0;

		for (int i=0;i<test.length;i++){
			for (int j=0;j<test[i].length;j++){
				//get the total number of trips in the matrix
				Nq+=test[i][j];
			}
		}

		//continue the calculation only if the total is > 0
		if (Nq!=0){
			for (int i=0;i<test.length;i++){
				for (int j=0;j<test[i].length;j++){
					//work out the entropy measure
					if(test[i][j]>0){Hq+=(test[i][j]/Nq)*Math.log(test[i][j]/Nq);}
				}
			}
		}

        double ret = Hq*=-1;
        
        if ( Double.isNaN(ret) ){
            return 10.0;
        }
		return ret;
        
	}

    public boolean isPerfect(double testStat){
        return false;
    }

}
