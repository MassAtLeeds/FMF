/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * This class is a singleton and provides the methods for extracting all of the 
 * information contained in the application .jar files.
 * </p>
 * <p><b>IT DOES NOT PROVIDE METHODS FOR READING/WRITING OR FINDING FILES</b></p>
 * 
 * <p>
 * The ResourceExtractor is mainly used in two ways:
 * <ol>
 * <li>The first use is during the class loading process. As the module menus are 
 * loaded during the initial start, up each class in each jar file in the directories
 * and sub-directories in the application class-path and the locations 
 * specified in the Init.txt are extracted by the ResourceExtractor and placed in
 * local storage.  These locally stored classes can be loaded up from the local storage
 * by the class loader GenericClassLoader.
 * </li>
 * <li>
 * The second use is to interogate .jar files to locate resources required by any of
 * loaded modules.  This process is also used to load the splash screen on application
 * startup.
 * </li>
 * </ol>
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 * @version 3.0
 */
public class ResourceExtractor {
	/**
         * Private class scope variables for persistance in loading required classes
         */
	private String jarFile;


        /**
	* Class scope variable required to allow output to the log file for 
        * debugging and error tracking and for writing and reading locally stored
        * class files.
        */
	ApplicationInformation ai = ApplicationInformation.getCurrent();


        /**
         * Private static variable for saving the one and only version of this class.
         * Returning the current instantiation of this class is achieved by calling the
         * package protected method .getCurrent().
         */
         private static ResourceExtractor re;




        /**
         * Private constructor to ensure that this class is not instantiated multiple
         * times.  The only way to return an instantiated version of this class is through
         * the .getCurrent() method, which manages and ensures that there is only
         * one ResourceExtractor object exists.
         */
	private ResourceExtractor () {}	//constructor



        
        /**
         * 
         * This method locates and extracts an image from the specified jar file.
         * Because there is the possibility that more than one thread may attempt to
         * access resources at the same time, this method is synchronized.
         * 
         * @param jar String name of the jar file to search, with or without .jar extension
         * @param imageName Sting name of the image with fully qualified with file type extension
         * @return The requested image from the jar file. If there is an error accessing the 
         * resource the method returns null.
         */
        synchronized Image getImage(String jar, String imageName){
            URL url = getResourceURL(jar,imageName,true);
            if (url!=null){
                return Toolkit.getDefaultToolkit().getImage(url);
            }else{
                return null;
            }
        }
        
        
        
        
        
        /**
         * This method can be used to locate resource bundles in module jar files that are
         * loose coupled to the Flexible Modelling Framework.
         * Because there is the possibility that more than one thread may attempt to
         * access resources at the same time, this method is synchronized.
         * 
         * @param jar String name of the jar file to search, with or without .jar extension
         * @param resourceBundleName String name of the Resource Bundle
         * @param locale Locale for the resource bundle to be returned.
         * @return a ResourceBundle from the appropriate .jar file. If there is an error accessing the 
         * ResourceBundle the method returns null.
         */
        
        synchronized ResourceBundle getResourceBundle(String jar, String resourceBundleName, Locale locale){
            String s = "";
            if (resourceBundleName.contains(".properties")){
                s = resourceBundleName.substring(0,resourceBundleName.lastIndexOf(".properties"));
            }else{
                s = resourceBundleName;
            }
            URL url = getResourceURL(jar,resourceBundleName+".properties",false);

            if (url!=null){
                URL urls[] = new URL[ ]{ url };
                ClassLoader aCL = Thread.currentThread().getContextClassLoader();
                URLClassLoader aUrlCL = new URLClassLoader(urls,aCL);
                return java.util.ResourceBundle.getBundle(resourceBundleName, locale, aUrlCL);
            }else{
                return null;
            }
        }
        
        
        
        
        /**
         * 
         * This method searches amongst all of the jar files on the class-path for the
         * specified jar file.  Once found the jar file is opened and the requested resource
         * located.  The required url is then returned.
         * Because there is the possibility that more than one thread may attempt to
         * access resources at the same time, this method is synchronized.
         * 
         * @param jar is the name of the jar file to search with or without the 
         * .jar extension.
         * @param resourceName is the string name of the resource to find, this 
         * should have the file type extension on the end.
         * @param urlToFile is a boolean flag to indicate whether to return a url 
         * to the jar file containing the resource (for resource bundles) or a fully qualified url to the 
         * resource itself (for images and other resources)
         * @return URL to either the jar file or the resource. if their is a problem
         * with reading the jar file or the resource then it will be logged and the method
         * will return null.
         */
        
        public synchronized URL getResourceURL(String jar, String resourceName, boolean urlToFile){
            
            //first of all we need to find the correct jar file.
            //check and see if the jar file has the .jar on the end...
            if (!jar.endsWith(".jar")){jar+=".jar";}
            
            
            //create an array of strings that contain the .jar file names
            FileManager fm = new FileManager();
            String s[] = fm.getFileNames(true,true);
            
            String j=null;
            //find the right .jar file
            for(int i=0;i<s.length;i++){
                if(s[i].substring(s[i].lastIndexOf("/")+1).equalsIgnoreCase(jar)){
                    j=s[i];
                    break;
                }
            }
            
            try{

                    //set up required variables

                    FileInputStream fis=new FileInputStream(j);

                    BufferedInputStream bis=new BufferedInputStream(fis);

                    ZipInputStream is=new ZipInputStream(bis);

                    ZipEntry entry=null;


                    URL url = null;

                    //cycle through all of the zip files entries

                    while ((entry=is.getNextEntry())!=null) {


                            //if the entry is a directory ignore it
                            if (entry.isDirectory()) {continue;}

                            if(resourceName.equalsIgnoreCase(entry.getName().substring(entry.getName().lastIndexOf("/")+1))){
                                
                                try {
                                    if (urlToFile){
                                        url = new URL("jar:file:" + j + "!/"+entry.getName());
                                    }else{
                                        url = new URL("jar:file:" + j + "!/");
                                    }
                                } catch (MalformedURLException murle) {
                                    //throw new IOException("Jar Graph.jar not found");
                                    reportError("getResourceURL","Jar " + j + " not found");
                                } catch (IOException ioe) {
                                    //throw new IOException("Jar Graph.jar not readable");
                                    reportError("getResourceURL","Jar " + j + " not readable");
                                }
                                
                                return url;
                            }


                    }//End while

                    return url;

            }catch(Exception ex){
                reportError("getResourceURL",ex.toString());
            }//End try-catch
            
            return null;
        }

        
        

        /**
         * Protected method to return the single instantiated object from this class
         * @return The current instatiated ResourceExtractor
         */
        public static synchronized ResourceExtractor getCurrent(){
            if (re==null){re = new ResourceExtractor();}
            return re;
        }






	/**
         * Package protected method. Sets the fileName and calls populateLocalStore()
         * 
         * @param fileName name of the jar file as a string. This is the fully resolved
         * path <br>
         * (test example C:/Work/Flexible Modelling Framework/Version3/FlexibleModellingFramework/dist/TestModule.jar).
         */

	public void setJarName(String fileName) {

		jarFile = fileName;
		populateLocalStore();

	}//End setJarName method










	/**
	*finds and returns the required file as a byte array
         * 
         * @param fileName as String
         * @return byte array
         */

	public byte[] getFile(String fileName) {

		return ai.retrieveFile(fileName);


	}//End getFile method














	/**
	*This method cycles through the requested zip file and 
	*calls readZipEntry for every file found
	*/

	private void populateLocalStore() {							//Start populateHashTable method


		try{

			//set up required variables

		        FileInputStream fis=new FileInputStream(jarFile);

          		BufferedInputStream bis=new BufferedInputStream(fis);

          		ZipInputStream is=new ZipInputStream(bis);

          		ZipEntry entry=null;



			//cycle through all of the zip files entries

			while ((entry=is.getNextEntry())!=null) {


				//if the entry is a directory ignore it
             			if (entry.isDirectory()) {continue;}



				//else read the entry into the hashtable
				readZipEntry(entry,is);


			}//End while


			
		}catch(Exception ex){
                    reportError("populateLocalStore",ex.toString());
		}//End try-catch


	}//End populateLocalStore method













	/**
	*This method reads the requested entry one byte at a time 
	*it then creates a byte array of the file and stores it in a hashtable
	*/

	private void readZipEntry(ZipEntry entry,ZipInputStream is) {


		try{

			//set up variables required

			int bytesRead = 0;
			int dataPoint = 0;
			byte b[] = new byte[1];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();



			//cycle through the file one byte at a time until we get all the data
			//we have to do this because the file size is not known 
			//so it is easier and safer to get the file one byte at a time

			while(bytesRead >= 0) {

				bytesRead = is.read(b,0,b.length);

				dataPoint = dataPoint + bytesRead;

				if (bytesRead != -1) {baos.write(b[0]);}


			}



			//create a byte array from the byte array output stream

			byte[] newBytes = baos.toByteArray();



			//put the data bytes into the local store with the name of the file as the marker

			ai.enterFile(entry.getName(),newBytes);


		}catch(Exception e){

			reportError("readZipEntry",e.toString());

		}//End try-catch


	}//End readZipEntry method


        
        /**
         * Reports errors to the screen through the ApplicationInformation object.  If the
         * status window has not yet opened then the errors are logged in the Application Log file
         * which is located in the root of the classpath directory (where the FlexibleModellingFramework.jar 
         * is located).
         * 
         * @param methodName String name of the method where the error occured.
         * @param msg the message for the error which can be got from an Exception e using e.toString().
         */
        private void reportError(String methodName, String msg){
            String nextLine = System.getProperty("line.separator");
            ai.writeToStatusWindow("CLASS: ResourceExtractor" + nextLine + "METHOD: " + methodName + 
                    nextLine + msg,true);		

        }


}
