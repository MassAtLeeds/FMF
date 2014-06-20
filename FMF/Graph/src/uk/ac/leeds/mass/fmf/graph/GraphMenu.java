/**
 * GraphMenu.java                                         
 *
 * --Copyright notice-- 
 *
 * Copyright (c) MASS Group. 
 * http://www.geog.leeds.ac.uk/groups/mass/
 * This software is licensed under 'The Artistic License' which can be found at 
 * the Open Source Initiative website at... 
 * http://www.opensource.org/licenses/artistic-license.php
 * Please note that the optional Clause 8 does not apply to this code.
 *
 * The Standard Version source code, and associated documentation can be found at... 
 * http://www.geog.leeds.ac.uk/groups/mass/
 *
 * --End of Copyright notice-- 
 *
**/
package uk.ac.leeds.mass.fmf.graph;

import java.io.*;
import java.net.*;
import java.util.jar .*;
import java.util.*;

/**
 * Menu for starting Graph tool.
 * @author <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 * @version 1.0
 * @see uk.ac.leeds.mass.fmf.framework.StartUp
 */
public class GraphMenu extends uk.ac.leeds.mass.fmf.shared_objects.Menu {
    
    /**
     * Singleton-like variable. 
     * Use getResourceBundle() to obtain.
     * Used to get properties from containing jar file when not in classpath because 
     * application run from another jar.
     */
    private static ResourceBundle resources = null;
    /**
     * Currently set to en_GB by default.
     */
    private static Locale locale = new Locale("en_GB");    
    /**
     * Used to check if locale has changed.
     */
    private static Locale oldLocale = new Locale("en_GB");
    
    
    /** 
     * Creates a new instance of GraphMenu. 
     */
    public GraphMenu() {
        
        try {           
            setMenuName(GraphMenu.getResourceBundle().getString("MenuName"));  
        } catch (Exception e) {   
            setMenuName("Graphing");
        }
    }
    
    
    
    
    
    /**
     * Gives a ResourceBundle suitable for reading Graph.properties.
     * This is used to get the properties file from Graph.jar when the application 
     * is run from a different jar and therefore classpath. 
     * By default the properties Locale is "en_GB" but this can be set.
     * @see setLocale
     * @return ResourceBundle representing a properties file associated with this jar.
     */
    public static ResourceBundle getResourceBundle () {//throws IOException {

        // If the ResourceBundle isn't yet set up, or if 
        // the locale has been changed, we need a new ResourceBundle.
        if ((resources == null) || (!locale.equals(oldLocale)))  {

            // First we need to find where the main application is running from.
            // Should be running from a jar in the same directory as the jar we need to access.
            String path = null;
            try {
                URL url = ClassLoader.getSystemResource("uk/ac/leeds/mass/fmf/shared_objects/IApplicationInformation.class");
                path = url.getPath();
            } catch (Exception e) {
                //throw new IOException("Classpath issue");
                return null;
            }

            // Having found the path to that jar, strip out the directory.
            path = path.substring(0, path.lastIndexOf("!"));
            String separator = File.pathSeparator;

            // For some reason, java doesn't seem to give the path with the system 
            // separators, but just incase it ever does...
            if (!path.contains(separator)) {
                separator = "/";
            }
            path = path.substring(0, path.lastIndexOf(separator));
			path = path.substring(0, path.lastIndexOf(separator));
            path = path.substring(path.indexOf(separator) + 1);
 
            // Having found the directory, we can now use it construct an 
            // alternative classpath and load the resource. Rather than do this 
            // each time, we create a static variable to hold it.
            URL url = null;
            
            try {
                url = new URL("jar:file:" + path + "/Graph.jar!/");
                JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                Manifest manifest = jarConnection.getManifest();
                // Following line can be used to check if jar reading ok.
				// System.err.println("hi :" + manifest.getMainAttributes().getValue("Class-Path"));

            } catch (MalformedURLException murle) {
                //throw new IOException("Jar Graph.jar not found");
                return null;
            } catch (IOException ioe) {
                //throw new IOException("Jar Graph.jar not readable");
                return null;
            }

            URL urls[] = new URL[ ]{ url };
            ClassLoader aCL = Thread.currentThread().getContextClassLoader();
            URLClassLoader aUrlCL = new URLClassLoader(urls, aCL);

            resources = java.util.ResourceBundle.getBundle("uk.ac.leeds.mass.fmf.graph.Graph", locale, aUrlCL);
            oldLocale = locale;
            return resources;
            
        } else {
             return resources;
        } 

    } 

    
    
    

    /**
     * Used to set the Locale associated with the Graph.properties file.
     * @param locale Locale
     */
    public void setLocale (Locale locale) {
        this.locale = locale;
    }
    
    
    
    
    
    /**
     *This supplys items for the menu connected to listeners.
     */
	@Override
    protected Object[][] getItems(){
        
        Object[][] items = new Object[1][6]; // See docs for  uk.ac.leeds.mass.fmf.shared_objects.Menu
        try {
        items[0][0] = GraphMenu.getResourceBundle().getString("ScriptingWindow");
        items[0][1] = new GraphMenuListener(ai);
        } catch (Exception e) {
			System.err.println("error");
            e.printStackTrace();
        }
        
        return items;
        
    }
    
}
