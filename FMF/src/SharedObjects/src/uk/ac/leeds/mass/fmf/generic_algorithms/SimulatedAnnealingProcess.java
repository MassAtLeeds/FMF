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

//import java.util.Random;
import ec.util.MersenneTwisterFast;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;

/**
 *
 * @author geo8kh
 */
public class SimulatedAnnealingProcess extends FrameworkProcess{

    private ISimulatedAnnealing sa = null;
    
    private MersenneTwisterFast random = new MersenneTwisterFast();
    
    private int steps;
    private int attempts;
    private int success;
    private double factor;


    private SimulatedAnnealingProcess(){}

    public SimulatedAnnealingProcess(int steps, int attempts, int success, int factor){
        //set up the values for the variables from the sliders
        this.attempts =  attempts;
        this.steps = steps;
        this.success = success;
        this.factor = new Integer( factor ).doubleValue() / 100;
    }

    @Override
    public void runProcess() {
        optimise();
    }
    /**
     * The optimise method is used to run the optimisation routine with the current parameters
     * as many times as are required.
     *
     */
    public void optimise(){

        setName(sa.getMessage());

        //initialise progress bar
        initilise(steps);

        double annealingSchedule = 0.5;

        //cycle through the steps (commonly refered to as temperature) for the optimisation
        //set from the stepsSlider
        for (int i = 0; i < steps; i++) {

            //if cancelled exit
            if(cancelled){break;}

            //reset the counters for attempts and successful changes
            int successCount = 0;

            //cycle through and make changes for the maximum number of attempts
            //set from the improvementAttemptSlider
            for (int j = 0; j < attempts; j++) {

                //suggest a change to the configuration and test to see if the change
                //is better or worse than the current configuration.
                //use the metropolis algorithm to either accept or reject the change
                boolean change = metropolis(sa.testChange( sa.suggestChange() ), annealingSchedule);
                if ( change ) {
                    getSa().makeChange();
                    successCount++;
                } else {
                    getSa().rejectChange();
                }

                //print the current fitness out (minor iterative step)
                sa.printFittness(true);

                //check and see if we have had the maximum number of successes
                //if we have break out of the testing improvement attempt loop
                //success variable is set by the improvementSlider
                if ( successCount >= success ){break;}

                //check and see if we have a perfect fit, if so break
                if ( sa.isPerfect() ){break;}

                //if cancelled exit
                if(cancelled){break;}
            }

            //print the current fitness out (major iterative step)
            sa.printFittness(false);

            //check and see if we have a perfect fit, if so break
            if ( sa.isPerfect() ){break;}

            // if we have not made any successful changes  break out of the algorithm and end
            if ( successCount == 0 ) { break; }

            //adjust the anneaeling schedule
            annealingSchedule *= factor;

            //increment progress bar
            progress++;

        }

        sa.setOptimised(true);

        //end process
        if ( !cancelled ){finished();}

    }

    private boolean metropolis(double fittnessDifference, double annealingSchedule){
        if ( fittnessDifference < 0 ){
            return true;
        }else{
            return random.nextDouble() < Math.exp( (-1*fittnessDifference) /annealingSchedule );
        }
    }


    /**
     * @return the sa
     */
    public ISimulatedAnnealing getSa() {
        return sa;
    }

    /**
     * @param sa the sa to set
     */
    public void setSa(ISimulatedAnnealing sa) {
        this.sa = sa;
        attempts *= sa.getSampleSize();
        success *= sa.getSampleSize();
    }

    public void setRandomSeed(Long seed){
        random.setSeed(seed);
    }

}
