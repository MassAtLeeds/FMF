/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.cluster;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author geo8kh
 */
public class ConvertJSONToCoordinates extends JSONReader{
    
    private ArrayList<Coordinates> base = new ArrayList<Coordinates>();
    private ArrayList<Coordinates> events = new ArrayList<Coordinates>();
    
    private ConvertJSONToCoordinates(){}
    
    public ConvertJSONToCoordinates(File file){
        super(file);
        this.readFile();
    }

    @Override
    protected void doActionWithValidCoordinates(double x, double y) {
            
        Coordinates c = new Coordinates(x, y);
        base.add(c);

        //Check the tweet text to see if it contains the search string
        if ( isEvent ){
            //if it does add the coordinates to the events
            events.add(c);
        }

    }
    
    
    public ArrayList<Coordinates> getBasePopulation(){
        return base;
    }
    
    public ArrayList<Coordinates> getEventPopulation(){
        return events;
    }
    
}
