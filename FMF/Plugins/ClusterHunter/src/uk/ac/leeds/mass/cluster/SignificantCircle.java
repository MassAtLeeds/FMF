/*
 * LICENSE INFORMAITON TO GO HERE
 */
package uk.ac.leeds.mass.cluster;

import java.util.ArrayList;

/**
 * Simple wrapper to collect all of the information together for a significant circle.
 * Contains the coordinates, the value and the radius of the circle.
 * 
 * @author Kirk Harland
 * @version 1.0
 */
public class SignificantCircle {
    private double x = 0.0;
    private double y = 0.0;
    private double radius = 0.0;
    private double value = 0.0;
    private int id = 0;
    private ArrayList<Long> tweetIDs = new ArrayList<Long>();
    
    private static int counter = 1;
    
    private SignificantCircle() {}
    public SignificantCircle(double x, double y, double radius, double value) {
        this.radius = radius;
        this.value = value;
        this.x = x;
        this.y = y;
        this.id = SignificantCircle.counter;
        SignificantCircle.counter++;
    }
    
    public double getX(){return x;}
    public double getY(){return y;}
    public double getRadius(){return radius;}
    public double getValue(){return value;}
    public int getID(){return id;}
    
    public void addTweet(Long tweetID){
        if(!tweetIDs.contains(tweetID)){
            tweetIDs.add(tweetID);
        }
    }
    
    public void removeTweet(Long tweetID){
        tweetIDs.remove(tweetID);
    }
    
    public ArrayList<Long> getTweets(){
        return tweetIDs;
    }
    
    @Override
    public String toString(){
        return (id + "\t" + x + "\t" + y + "\t" + radius + "\t" + value);
//        return (id + "," + x + "," + y + "," + radius + "," + value);
    }
    
}