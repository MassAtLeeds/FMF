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

package uk.ac.leeds.mass.fmf.microsimulation;

import java.io.Serializable;
import java.util.ArrayList;
//import java.util.Random;
import ec.util.MersenneTwisterFast;
import uk.ac.leeds.mass.fmf.fit_statistics.IGOF;
import uk.ac.leeds.mass.fmf.fit_statistics.TAE;
import uk.ac.leeds.mass.fmf.generic_algorithms.ISimulatedAnnealing;

/**
 *
 * @author geo8kh
 */
public class PopulationSynthesisSA implements ISimulatedAnnealing, Serializable{

    private final int noProgressThreshold = 10;
    private double[] thresholdValues = new double[noProgressThreshold];
    boolean thresholdReached = false;

    private String zone = "";
    private double constraints[][] = null;
    private int population[][] = null;
    private int zonePopulation[] = null;
    private int returnPopulation[] = null;

    private double predSummary[][] = null;
    private int popSize;

    private double currentFittness = 0.0;
    private double proposedFittness = 0.0;

    private MersenneTwisterFast random = new MersenneTwisterFast();
    private IGOF gof = new TAE();

    private ArrayList<Double> gofStats = new ArrayList<Double>();

    private boolean optimised;
    private boolean done = false;


    private int swapRow=0;
    private int swapID=0;

    PopulationSynthesisSA(){}
    
    PopulationSynthesisSA(long seed){
        random.setSeed(seed);
    }

    void createPopulation(String zoneCode, int[][] population, double[][] constraints, int totalPopulationIndex){
        zone = zoneCode;
        this.constraints = constraints;
        this.population = population;
        
        
        //dimension the predSummary array
        predSummary = new double [this.constraints.length][];
        for (int i = 0; i < this.constraints.length; i++) {
            predSummary[i] = new double[this.constraints[i].length];
        }
        
        //calculate the zones population size
        for (int i = 0; i < this.constraints[totalPopulationIndex].length; i++) {
            popSize += new Double(this.constraints[totalPopulationIndex][i]).intValue();
        }
        //initialise the size of the returnPopulation
        returnPopulation = new int[popSize];
        
        boolean add=true;
        int zonePopCount=0;
        //increment row in population table
        for (int i = 0; i < this.population[0].length; i++) {
            //increment the field in the population table and add up the 
            //number of people that should be present in this zone
            add = true;
            for (int j = 0; j < this.population.length; j++) {

                if ( this.population[j][i] == -1 ) {
                    add=false;
                }else if ( this.constraints[j][this.population[j][i]] == 0.0 ){
                    add =false;
                }

            }
            if ( add ){zonePopCount++;}
        }
        
        //dimension the zonePopulation array
        zonePopulation = new int[zonePopCount];
        //reset the population counter
        zonePopCount = 0;
        
        //increment row in population table
        for (int i = 0; i < this.population[0].length; i++) {
            //increment the field in the population table
            //add the pointers to the population table into the zonePopulation
            add = true;
            for (int j = 0; j < this.population.length; j++) {

                if ( this.population[j][i] == -1 ) {
                    add=false;
                }else if ( this.constraints[j][this.population[j][i]] == 0.0 ){
                    add =false;
                }

            }
            //add the pointer to the person into the zonePopulation array
            if ( add ){
                zonePopulation[zonePopCount] = i;
                zonePopCount++;
            }
        }

        if ( zonePopulation.length == 0 ){
            System.out.println("Data issue: "+zoneCode+" does not have a candidate population, no output created.");
            System.err.println(zoneCode+" does not have a candidate population, no output created.");
        }

        //create the initial population
        for (int i = 0; i < this.returnPopulation.length; i++) {
            returnPopulation[i] = zonePopulation[random.nextInt(zonePopulation.length)];

            //keep the predSummary in synchronization with the population
            for (int j = 0; j < predSummary.length; j++) {
                if( this.population[j][ returnPopulation[i] ] > -1){
                    predSummary[j][ this.population[j][ returnPopulation[i] ] ]++;
                }
            }

        }
        
        currentFittness = getFittness();
        
    }

    private double getFittness(){
        double totFitness = 0.0;

        for (int i = 0; i < constraints.length; i++) {
            double obs[][] = new double[1][constraints[i].length];
            obs[0] = constraints[i];

            double pred[][] = new double[1][predSummary[i].length];
            pred[0] = predSummary[i];

            totFitness += gof.test( obs, pred );
        }

        return totFitness;

    }

    @Override
    public int getSampleSize() {
        return popSize;// * constraints.length;
    }

    @Override
    public double getCurrentFittness() {
        return currentFittness;
    }

    @Override
    public double testChange(double fittnessToTest) {
        //should improvment be when the test stat gets smaller?
        if (gof.calibrateToLessThan()){
            return fittnessToTest - currentFittness;
        }else{
            return currentFittness - fittnessToTest;
        }
    }

    @Override
    public double suggestChange() {

        //find the person to remove
        swapRow = random.nextInt(returnPopulation.length);

        //remove the person
        //store the original person in case we have to reverse
        swapID = returnPopulation[swapRow];

        //keep the predSummary in synchronization with the population
        for (int j = 0; j < predSummary.length; j++) {
            if(population[j][ swapID ] > -1){
                predSummary[j][ population[j][ swapID ] ]--;
            }
        }

        //add another person
        returnPopulation[swapRow] = zonePopulation[random.nextInt(zonePopulation.length)];

        //keep the predSummary in synchronization with the population
        for (int j = 0; j < predSummary.length; j++) {
            if(population[j][ returnPopulation[swapRow] ] > -1){
                predSummary[j][ population[j][ returnPopulation[swapRow] ] ]++;
            }
        }

        proposedFittness = getFittness();

        return proposedFittness;
    }

    @Override
    public void makeChange() {
        currentFittness = proposedFittness;
    }

    @Override
    public void rejectChange() {

        //remove the person
        //keep the predSummary in synchronization with the population
        for (int j = 0; j < predSummary.length; j++) {
            if(population[j][ returnPopulation[swapRow] ] > -1){
                predSummary[j][ population[j][ returnPopulation[swapRow] ] ]--;
            }
        }

        //add original person
        returnPopulation[swapRow] = swapID;

        //keep the predSummary in synchronization with the population
        for (int j = 0; j < predSummary.length; j++) {
            if(population[j][ swapID ] > -1){
                predSummary[j][ population[j][ swapID ] ]++;
            }
        }
    }

    @Override
    public String getMessage() {
        return "Optimising " + zone;
    }

    @Override
    public void printFittness(boolean minorIteration) {
        if ( !minorIteration ){
            gofStats.add(new Double(currentFittness));
            assessThreshold();
        }
    }

    @Override
    public boolean isPerfect() {
        if ( gof.isPerfect(currentFittness) || thresholdReached ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * @return the done
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @param done the done to set
     */
    public void setDone(boolean done) {
        this.done = done;
    }


    /**
     * @return the optimised
     */
    public boolean isOptimised() {
        return optimised;
    }

    /**
     * @param optimised the optimised to set
     */
    @Override
    public void setOptimised(boolean optimised) {
        this.optimised = optimised;
    }

    /**
     * @return the currentZone
     */
    public String getCurrentZone() {
        return zone;
    }

    /**
     * return the current population
     * @return
     */
    public int[] getPopulation(){
        return returnPopulation;
    }

    /**
     * @return the gofStat
     */
    public double[] getGofStat() {
        double[] gofStat = new double[gofStats.size()];
        for (int i = 0; i < gofStat.length; i++) {
            gofStat[i] = gofStats.get(i).doubleValue();
        }
        return gofStat;
    }

    /**
     * @return the predSummary
     */
    public double[][] getPredSummary() {
        return predSummary;
    }

    private void assessThreshold(){
        //check the current fitness is different from the last number of iterations
        thresholdReached = true;
        for (int i = 0; i < thresholdValues.length; i++) {
            if ( thresholdValues[i] != currentFittness){
                thresholdReached = false;
                break;
            }
        }

        //if the thrshold has not been reached shuffle the values and store the latest one
        if ( !thresholdReached ){
            for (int i = 0; i < thresholdValues.length - 1; i++) {
                thresholdValues[i] = thresholdValues[i+1];
            }
            thresholdValues[thresholdValues.length - 1] = currentFittness;
        }

    }


}
