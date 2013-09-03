/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JMenu;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.IMenu;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class MainMenuLoader {
   //Constants
    final String SHARED_OBJECTS = "SharedObjects.jar";
    final String FRAMEWORK = "/framework/";
    final String CLASS_EXTENSION = ".class";
    
    //This creates an object to implement the interface of the ApplicationInformation singleton
    IApplicationInformation ai;

    
    
    /** 
     * Creates a new instance of MainMenuLoader 
     * taking in an instance of the Application Information singleton object
     */

    MainMenuLoader(IApplicationInformation a) {

        ai = a;

    }
    




   


    


    
    /** 
    *builds and returns an array of JMenus in the order that they should be added to the menu bar on the main screen.
    */

    JMenu[] getMenus(){											//Start getMenus method


        JMenu allMenus[] = getMenuClasses();
        

        return allMenus;
    }													//End getMenus method


    








    /**
    *Cycles through all of the jar files, except for SharedObjects.jar, 
    *in the current application path and loads the classes placing instances
    *of IMenu in the array for return
    */

    private JMenu[] getMenuClasses() {									//Start getMenuClasses method

    FileManager fm = new FileManager();
	ArrayList al = new ArrayList();
    ResourceExtractor re = ResourceExtractor.getCurrent();
    MenuClassLoader loader = new MenuClassLoader();

	//create an array of strings that contain the .jar file names in the current application path
    String s[] = fm.getFileNames(true,true);

	//Add the Framework required menus first
	ApplicationMenu am = new ApplicationMenu();
	al.add (am.getMenu(ai));

        //cycle through all the .jar files in the current application path
        for(int i = 0; i<s.length;i++) {

            //display the jar file being loaded in the splash screen
            SplashScreen.setStatus("loading module..." + s[i].substring(s[i].lastIndexOf("/")+1));
            
            //if the .jar is SharedObjects.jar do not get the classes as they are not required
            if (s[i].substring(s[i].length()-SHARED_OBJECTS.length()).equalsIgnoreCase(SHARED_OBJECTS)) {
                continue;
            }

            //set the jar name in the resource extracter - this will extract the classes into binary arrays and store them locally
            re.setJarName(s[i]);

            //get the class names in the current jar file
            String classes[] = getClassNames(s[i]);

            //cycle through all the returned classes
            for(int classCount = 0;classCount<classes.length; classCount++) {

            try{
                //load the class and resolve it
                Class c = loader.loadClass(classes[classCount],true);
                //create an instance of the class in Object o
                Object o = c.newInstance();
                //check and make sure that the class instantiated
                if (o != null) {
                    //examine the object and see if it is a IMenu class if it is add it to the arraylist
    		        if (o instanceof IMenu) {al.add(((IMenu)o).getMenu(ai));}
        	    }

            }catch (InstantiationException ie){
                //we expect some instantiation exceptions so don't do anything with these
            
            }catch (IllegalAccessException iae){
                //we expect some illegal access exceptions so don't do anything with these
                
                //but if we find any other type of exception report it!
        	}catch(Exception e){e.printStackTrace();}	//End try-catch

        }	//End for

	}	//End for


	//create an array of JMenus the correct size
	JMenu jm[] = new JMenu[al.size()];

	//transfer the contents of the arraylist into the JMenu array
    al.toArray(jm);
	
	//return the JMenu array
	return jm;

    }													//End getMenuClasses method










    /**
    *gets all the class names in a jar file
    *replaces the forward hashes with stops to fall in line with class path convention
    *and removes the .class extension
    */

    private String[] getClassNames(String jarFile) {							//Start getClassNames method


	ArrayList al = new ArrayList();
	String s;


        try{


            ZipFile j = new ZipFile(jarFile);
	    Enumeration en = j.entries();

            while (en.hasMoreElements()) {


                ZipEntry z = (ZipEntry)en.nextElement();
                s = z.getName();


		//do not get the classnames of the classes in the current framwork
		if (s.indexOf(FRAMEWORK)>0){continue;}


		//if the file ends with .class we assume that it is a class that needs loading
                if (s.endsWith(CLASS_EXTENSION)) {

			//remove the .class extension from the end of the file
			s=s.substring(0,s.lastIndexOf('.'));

			//replavce all the forward hashes with dots to be consistent with a classpath
			s=s.replace('/','.');

			//add the modified name to the array list
			al.add(s);


		}	//End if


            }	//End while

        }catch(IOException e){

            e.printStackTrace();

        }	//End try-catch




	//instantiate an array of strings the correct size of the arraylist
	String classNames[] = new String[al.size()];

	//transfer the contents of the array list to the string array
	al.toArray(classNames);


	//return the string array
        return classNames;



    }													//End getClassNames method



}
