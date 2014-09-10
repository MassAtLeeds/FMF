package uk.ac.leeds.mass.cluster;

/**
 *
 * @author Kirk Harland
 */
public class Coordinates implements Comparable<Coordinates> {

    private double x;
    private double y;

    private int hash = 7;

    private boolean aggregated = false;
    private boolean use = false;
    private int count = 1;

    //create null instance
    public Coordinates(){
        this(0.0,0.0);
        count = 0;
    }

    //create useable instance
    public Coordinates(double x, double y){
        this.x = x;
        this.y = y;
        
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));

    }
    
    public void aggregate(Coordinates c){
        //check and see if this has already been combined with another object
        if ( !this.isAggregated() ){
            this.Aggregated();
            this.use = true;
        }
        
        if ( this.toString().equals(c.toString()) ){
            count += c.getCount();
            c.Aggregated();
        }
    }
    
    public boolean shouldUse(){
        return use;
    }
    
    public int getCount(){
        return count;
    }
    
    public boolean isAggregated(){
        return aggregated;
    }
    
    protected void Aggregated(){
        aggregated = true;
    }

    protected double getX(){return x;}
    protected double getY(){return y;}

    @Override
    public boolean equals(Object o){
       if ( o instanceof uk.ac.leeds.mass.cluster.Coordinates && ( ((Coordinates)o).getX() == x && ((Coordinates)o).getY() == y ) ){
           return true;
       } else {
           return false;
       }
    }

    @Override
    public int hashCode() {
        return hash;
    }
    
    @Override
    public String toString(){
        return x + " - " + y;
    }

    public int compareTo(Coordinates c) {
        if ( this.x < c.x ){
            return -1;
        }else if ( this.x > c.x ){
            return 1;
        }else{
            return 0;
        }
    }

}