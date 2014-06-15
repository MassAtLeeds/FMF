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

package uk.ac.leeds.mass.fmf.generic_algorithms;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public interface ISimulatedAnnealing {

    /**
     * The size of the sample currently being optimised
     *
     * @return integer value representing the current sample size
     */
    public int getSampleSize();

    /**
     * A statistic that represents the current fitness of the configuration.
     * This is the value that will either be minimised or maximised by the
     * optimisation routine
     *
     * @return double value representing the current fitness statistic
     */
    public double getCurrentFittness();
    
    
    /**
     * Tests the fittness parameter passed in to see if it is better than the 
     * current fitteness statistic
     * 
     * @param fittnessToTest double value representing the statistic to test
     * @return a double value representing how much better or worse the fittnessToTest statistic is than the
     * current fittness statistic. The return value should be negative if the parameter is better than the current
     * fitness parameter and positive if equal or worse than the current value
     */
    public double testChange( double fittnessToTest );


    /**
     * Makes a 'non-permantent' change to the suggested result sample and tests the fittness of the new sample
     * incorporating the suggested change.
     *
     * @return a double value representing the test statistic for the sample including the suggested change
     */
    public double suggestChange();


    /**
     * Make the current suggested change permanent
     */
    public void makeChange();


    /**
     * Reject the current suggested change.
     */
    public void rejectChange();


    /**
     * message to display in progress bar while optimising
     * @return
     */
    public String getMessage();
    
    /**
     * print the fittness to the screen
     * @param minorIteration if the step taken is a minor iteration this will be true otherwise false
     */
    public void printFittness(boolean minorIteration);

    /**
     * test the current fit statistic to see if the data is a perfect fit or not
     * 
     * @return true if the data is a perfect fit, false if the data is not.
     */
    public boolean isPerfect();

    /**
     * set the process as optimised (called at the end of the SA algorithm)
     * @param optimised true if optimised successfully
     */
     public void setOptimised(boolean optimised);



}
