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

package uk.ac.leeds.mass.fmf.framework;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 * <p>
 * This is the class to create the splash screen while the rest of the application loads.
 * </p>
 * <p>
 * It is a singleton as only one splash screen should be used.  The current instance
 * of the instantiated object can be retrieved by calling the method getSplashScreen.
 * Some of the methods in this class are synchronized because the main thread passes
 * through and initialises the load, but also threads created in the JVM to observe 
 * the loading of the image pass through these methods, therefore it is safest to synchronize
 * the methods to make sure that the internal variables are in the correct state at 
 * the right point in time. The design of this class was inspired by the 
 * example given by Roy Ratcliffe at <br> <b>http://www.randelshofer.ch/oop/javasplash/Java%20Splash%20Screen.pdf</b><br>
 * although it has some significant differences in it internal working.
 * </p>
 * <p>
 * To create and display a splash screen the following example code can be used:<br><br>
 *         
 *       //Load the splash screen<br>
 *       SplashScreen splash = SplashScreen.getSplashScreen("Splash.jpg");<br><br>
 *
 *       //if you want the application loading to wait for the splash screen to load<br>
 *       //this method can be called but this is optional.  Note that if a problem is<br> 
 *       //encountered loading the splash screen the main loading thread will not wait.<br>
 *       splash.waitForSplashScreen();<br><br>
 *       
 *       //code to load the main application<br><br>
 * 
 *       //while loading the application the static method  setStatus can be used<br>
 *       //to display messages indicating the load progress to the screen.<br>
 *       SplashScreen.setStatus(progressMessage);<br><br>
 * 
 *       //set the main application screen visible<br><br>
 * 
 *       //dispose of the splash screen<br>
 *       splash.dispose();<br><br>
 * 
 * It is important that the splash screen is disposed of after the application has 
 * loaded to make sure that resources are released. SplashScreen does not use any 
 * Swing components to minimise in the overheads of loading the splash screen up.
 * </p>
 * 
 * @author Kirk Harland k.harland98@leeds.ac.uk
 * @version version 1.0
 */
class SplashScreen implements ImageObserver{

    /**
     * Private static SplashScreen variable to store the one instance of the splash screen.
     */
    private static SplashScreen ss;
    
    /**
     * Private boolean flag used to indicate if the main thread should wait or not.
     */
    private boolean shouldWait = true;
    
    /**
     * private variable to load the splash screen image into.
     */
    private Image image;
    
    /**
     * private static variable for displaying the loading progress to the screen
     */
    private static Label label = new Label ( " Loading . . . " , Label .CENTER) ;
    
    /**
     * private frame is the window that is eventually displayed and contains both the label and image.
     */
    private Frame frame;
    
    /**
     * private volatile boolean flag to indicate when the image has loaded.
     */
    private volatile boolean imageLoaded = false;
    
    
    
    
    
    /**
     * Private constructor ensures that this class cannot be directly instantiated.
     * 
     * @param splashImage is the name of the splash screen image including the file type extention.
     */
    private SplashScreen (String splashImage){
        
        //get the ResourceExctractor object
        ResourceExtractor re = ResourceExtractor.getCurrent();
        //use the ResourceExtractor to find and fetch the splash screen image 
        //from the jar file
        image = re.getImage(JarInfo.JAR_NAME, splashImage);

        splash();
    }
    
    
    
    
    
    /**
     * This is the method to be used to get the current instance of the SplashScreen.
     * The method is static so an instance does not need to exist for this method to
     * be called.  If an instance has not been created this method will create a new 
     * instance and return it. If an instance already exists, that will be returned.
     * 
     * @param splashImage name of the splash screen image including the file type extention.
     * @return the current instantiated SplashScreen object, or a new one if one does not already
     * exist.
     */
    static synchronized SplashScreen getSplashScreen(String splashImage){

        //check to see if we already have a SplashScreen object, if not create one
       if (ss == null) {
            ss = new SplashScreen(splashImage);
        }
       
       //return the splashscreen object
       return ss;        

    }
    
    
    
    
    
    
    /**
     * This static method sets the status of the load message on the SplashScreen.
     * Because the method is static the current instantiated object for this class
     * does not need to be obtained to update the progress label, reference to the
     * SplashScreen class will provide access to the progress label. For example:
     * SplashScreen.setStatus(<b>your message here</b>); will update the progress label.
     * 
     * @param s is the message to be displayed in the progress label.
     */
    synchronized static void setStatus(String s ) {
        //set the progress label text
        label.setText(s);
    }
    
    
    
    
    
    
    /**
     * <p>
     * This method starts the splash screen loading and displaying.  
     * It creates the undecorated frame and starts the image loading process.
     * If the image has not loaded when the image preparation is called then the
     * method exists and allows the image observer to carry on loading the image.
     * If the image can be prepared, the drawSplashScreen method is called without
     * waiting for the image observer!
     * </p>
     * <p>
     * If the image cannot be found then the prepareImage method returns null, BUT
     * it does not fail. This means that the drawSplashScreen method is called with
     * the image variable set to null. The drawSplashScreen method checks to see if 
     * the image is null before manipulating it.
     * </p>
     */
    void splash () {
        
        //create a new frame
        frame = new Frame();
        //take all of the decoration off of the frame 
        frame.setUndecorated(true);

        //check to see if the image is ready for display.  If it isn't exit the method
        if(Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this)) {
           //draw the screen only if the image has already loaded
            drawSplashScreen();
        }

    }

    
    
    
    
    
    
    
    /**
     * Implemented interface for the ImageObserver.
     * If the image is in the process of loading then this method is repeatedly 
     * called by the JVM.  When the infoflags parameter is equal to the ALLBITS
     * constant the image loading has completed and it can be displayed.
     * 
     * @param img image being loaded.
     * @param infoflags status of the image loading.
     * @param x should be ignored.
     * @param y should be ignored.
     * @param width should be ignored.
     * @param height should be ignored.
     * @return boolean false when the image has loaded, true otherwise.
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        
        //check the progress of the image loading.  If infoflags is not equal to the
        //constant ALLBITS the image has finished loading
        boolean loaded = infoflags == ImageObserver.ALLBITS;
        
        //if the image has loaded draw the screen
        if (loaded){
            drawSplashScreen();
        }
        return !loaded;
    }
    
    
    
    
    
    
    
    
    
    /**
     * This method puts all the components together and draws the splash screen.
     * 
     * It includes fixes to stop the screen from flickering by Werner Randelshofer.
     * if the frame is null nothing will be drawn to the screen.  If the image is null
     * the loading message will be displayed without an image. The last thing this method
     * executes is a notifyAll() to ensure that all threads that have been waiting 
     * for the splash screen are woken up.
     */
    private synchronized void drawSplashScreen(){
        
        //wrap all of the code in a try block so that if there is an error
        //the shouldWait variable is set to false and notifyAll is called 
        //in the finally block.  If there is an error we want to make sure 
        //that the main thread is woken up or not made to wait.
        try{
            //only draw the splash screen if we have a frame to draw it in...
            if (frame != null){
                
                //don't try to change any image settings or display it if it is null
                if (image!=null){
                    
                    //get the dimensions of the image
                    final int splashWidth=image.getWidth(null);
                    final int spalshHeight=image.getHeight(null);


                    //========================================================================================
                    //fixes by Werner Randelshofer to stop flickering screen on
                    //both MAC and Windows OS
                    Canvas canvas = new Canvas() {

                        @Override
                        public void update(Graphics g) {paint(g);}
                        @Override
                        public void paint(Graphics g) {g.drawImage(image, 0, 0, this);}
                        @Override
                        public Dimension getPreferredSize() {return new Dimension(splashWidth,spalshHeight);}
                    };
                    //========================================================================================

                    //add the canvas to the frame in the centre
                    frame.add(canvas, BorderLayout.CENTER);
                }//end if
                
                
                //set the background colour of the label to black
                label.setBackground(new Color(0,0,0));
                //set the test colour of the label to white
                label.setForeground(new Color(255,255,255));
                //put the label on the frame at the bottom
                frame.add(label, BorderLayout.SOUTH);
                //pack the frame to make sure that everything is laid out correctly
                frame.pack();

                
                //get the size of the screen to display on
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                //get the size of the final packed frame.
                Dimension frameSize = frame.getSize();
                
                //set the location of the frame to be the middle of the screen
                //the >> opreators are bit shift operators... if we had a byte
                //10101010 = 170 using >> shifts the bits one place right to 
                //give 01010101 = 85 effectively dividing by 2.
                frame.setLocation((screenSize.width - frameSize.width) >> 1, // /2
                                    (screenSize.height - frameSize.height) >> 1 ) ; // /2
                
                //set the splach screen to be visible
                frame.setVisible(true);
            }
        }finally{
            shouldWait=false;
            notifyAll();
        }
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * This method tidies up all of the screen bits and releases all of the resources
     * it should be called at the end of the application initialisation process
     */
    synchronized void dispose() {
        frame.remove(label);
        frame.dispose();
        frame = null;
    }
    
    
    
    
    
    
    
    /**
     * This method can be optionally called to make the main thread wait for the 
     * splash screen to load.  If there is a problem loading the splash screen the
     * shouldWait variable if set to false and all threads are woken up, ensuring 
     * that the main thread does not freeze if there is a problem with the image loading.
     */
    synchronized void waitForSplashScreen(){
        //try to make the thread wait.
        try {
            //check to see if the thread should wait before it is made to wait
            if (shouldWait){wait();}
        } catch (InterruptedException ex) {
            
            //if there has been an error log it.
            
            //get line separator for current operating system
            String nextLine = System.getProperty("line.separator");
            
            //Get the current ApplicationInformation object
            ApplicationInformation ai = ApplicationInformation.getCurrent();
            //log the error through the ai object
            ai.writeToStatusWindow("CLASS: SplashScreen" + nextLine + "METHOD: waitForSplashScreen"+ 
                    nextLine + ex.toString(),true);
        }//end try
        
    }//end waitForSplashScreen
    
    
}
