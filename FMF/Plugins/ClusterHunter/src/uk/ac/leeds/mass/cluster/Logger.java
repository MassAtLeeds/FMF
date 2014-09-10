package uk.ac.leeds.mass.cluster;

import java.io.PrintStream;

/**
 *
 * @author Kirk Harland
 * @version 1.0
 */
public class Logger {
    
    public enum messageSeverity {Information, Warning, Error}

    static PrintStream ps = System.out;
    
    public static void setPrintStream ( PrintStream ps ){Logger.ps = ps;}
    
    public static PrintStream getPrintStream(){return ps;}
    
    public static void log(String message, messageSeverity type, String origin){
        ps.println(type + " : " + origin);
        ps.println(message);
        ps.flush();
    }
    
}
