/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import java.util.Date;
import javax.management.timer.Timer;

/**
 *
 * @author geo8kh
 */
public class ProcessTimer {

    Date start = null;
    Date end = null;
    boolean started = false;
    boolean ended = false;
    long timeTaken = 0;
    
    long weeks = 0;
    long days = 0;
    long hours = 0;
    long minutes = 0;
    long seconds = 0;
    long millieseconds = 0;
    
    public void start(){
        if (!started){
            start = new Date();
            started = true;
        }else{
            System.out.println("Process Timer is already ticking!");
        }
    }
    
    private void end(){
        
        //only end the timer if it has already been started, else report that it hasn't been started already to the screen
        if (started){
            
            //only end the timer if it has not already been ended
            if(!ended){
                
                end = new Date();
                ended = true;

                timeTaken = end.getTime() - start.getTime();

                double temp = timeTaken / (Timer.ONE_DAY * 7);
                if ( temp > 1 ) { 
                    weeks = new Double(Math.floor(temp)).longValue();
                    timeTaken -= (weeks * (Timer.ONE_DAY * 7));
                }

                temp = timeTaken / Timer.ONE_DAY;
                if ( temp > 1 ) { 
                    days = new Double(Math.floor(temp)).longValue();
                    timeTaken -= (days * Timer.ONE_DAY);
                }

                temp = timeTaken / Timer.ONE_HOUR;
                if ( temp > 1 ) { 
                    hours = new Double(Math.floor(temp)).longValue();
                    timeTaken -= (hours * Timer.ONE_HOUR);
                }

                temp = timeTaken / Timer.ONE_MINUTE;
                if ( temp > 1 ) { 
                    minutes = new Double(Math.floor(temp)).longValue();
                    timeTaken -= (minutes * Timer.ONE_MINUTE);
                }

                temp = timeTaken / Timer.ONE_SECOND;
                if ( temp > 1 ) { 
                    seconds = new Double(Math.floor(temp)).longValue();
                    timeTaken -= (seconds * Timer.ONE_SECOND);
                }

                millieseconds = timeTaken;
                timeTaken = 0;

            }
            
        }else{
            System.out.println("Process Timer has not been started!");
        }
        
    }

    public void printProcessTime(){

        if (!ended){
            end();
        }

        System.out.println("Processing Time is Weeks = " + weeks + " | days = " + days + " | hours = " + hours +
                " | minutes = " + minutes + " | seconds = " + seconds + " | millie-seconds = " + millieseconds);

    }

    public void printProcessTimeInMinutes(){

        if (!ended){
            end();
        }

        timeTaken = (weeks * 60 * 24 * 7);
        timeTaken += (days * 60 * 24);
        timeTaken += (hours * 60);
        timeTaken += minutes;

        if ( seconds > 30 ){ timeTaken += 1; }

        System.out.println("Processing Time is " + timeTaken + " minutes.");

    }

}
