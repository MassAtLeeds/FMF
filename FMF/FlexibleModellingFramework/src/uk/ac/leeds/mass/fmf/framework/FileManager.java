/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class FileManager {

	//Final Constants
	final String LOG_PATH = "/Log.txt";
	final String INIT_PATH = "/Init.txt";


	//Class scope variables

	ApplicationInformation ai = ApplicationInformation.getCurrent();
	





	/**
	* Create a new instance of FileManager
	*/
	FileManager() {
	}






	String getIconPath(){return getResolvedClassPath() + "/Application.jpg";}


	/**
	*used to write out messages to the log file if the application is set to debug mode
	*/

	void writeToLog(String className,String methodName,String msg) {					//Method writeToLog Start


		//If the application is set up to debug the code write out any debugging information to the log file
		if (ai.getDebugMode()==true){

			try{
	
				String s = getResolvedClassPath() + LOG_PATH;
				boolean b;
				File f = new File(s);
				long fileLength;
				String nextLine = System.getProperty("line.separator");


				//if the file exists get its length else set length to 0 and create the file
				//set b to true if the file exists and false if the file is new

				if (f.exists()){
		
					fileLength = f.length();
					if (fileLength == 0) {b = false;} else {b = true;}

				}else{
					
					f.createNewFile();
					fileLength = 0;
					b = false;

				}		//End if




				//create a random access file object and move to the appropriate place for insertion of new log entry

				RandomAccessFile r = new RandomAccessFile(f,"rw");
				r.seek(fileLength);



				//if the file is an existing file place an empty line between entries

				if (b) {
					r.writeBytes(nextLine);
					r.writeBytes(nextLine);
				}


				//write the current log entry to the file
                                
                                if(!className.equals("")){
                                    r.writeBytes("Class: " + className);
                                    r.writeBytes(nextLine);
                                }
                                if(!methodName.equals("")){
                                    r.writeBytes("Method: " + methodName);
                                    r.writeBytes(nextLine);
                                }
                                if(!msg.equals("")){
                                    r.writeBytes(msg);
                                }

				//close the random access file

				r.close();
				


			}catch(IOException e){
			
				e.printStackTrace();

			}	//End try-catch


		}	//End if

		
	}									//Method writeToLog End











	/**
	*Clears the log file for fresh input
	*/
	
	void clearLogFile(){

		boolean b = clearFile(getResolvedClassPath() + LOG_PATH);

	}



    File getLogFile(){
        return new File(getResolvedClassPath() + LOG_PATH);
    }

	
	
	/**
	*Clears the ini file for fresh input
	*/
	
	void clearIniFile(){

		boolean b = clearFile(getResolvedClassPath() + INIT_PATH);
	}
	



	void writeToIniFile(String lineToWrite){
		writeToFile(getResolvedClassPath() + INIT_PATH, lineToWrite);
	}
	
	
	
	void writeToFile(String fileName, String lineToWrite) {					//Method writeToFile Start




			try{
				File f = new File(fileName);
				long fileLength;
				String nextLine = System.getProperty("line.separator");


				//if the file exists get its length else set length to 0 and create the file.
				if (f.exists()){
		
					fileLength = f.length();

				}else{
					f.createNewFile();
					fileLength = 0;
				}		//End if


				//create a random access file object and move to the appropriate place for insertion of new log entry
				RandomAccessFile r = new RandomAccessFile(f,"rw");
				r.seek(fileLength);

				//write the line to the file
				r.writeBytes(lineToWrite);
				r.writeBytes(nextLine);

				//close the random access file
				r.close();
				


			}catch(IOException e){
			
                System.out.println("Problem writing to file: "+fileName);
                e.printStackTrace();

			}	//End try-catch

		
	}									//Method writeToFile End



	
	





	/**
	*Delete a file and create a new version with the same name
	*returns true if the delete was successful and false if not
	*/
	
	boolean clearFile(String fileName) {					//Start clearFile method

		File f = new File(fileName);
		boolean b;


		try{	
	
			if (f.exists()){	
				//the file already exists so we need to delete it before we can create a new one
				FileWriter fw = new FileWriter(f,false);
				fw.write("");
				fw.close();
				f=null;

			}else{
	
				//create a new file using the path passed into the method
				f.createNewFile();
			}
			b = true;

		}catch (IOException e){
			b = false;
			e.printStackTrace();
		
		}		//End try-catch

	
		return b;

	}									//End clearFile method











	/**
	*Reads in the initialisaion file and passes the text back as a string array.
	*/

	String[] readIniFile() {							//Start readIniFile method


		//set up the variables for the file reader to read into

		String s = getResolvedClassPath() + INIT_PATH;
		int i = linesInFile(s);
		String iniText[] = new String[i];
			

	
		try{



			//set up a file reader for the initialisation file

			
			FileReader fr = new FileReader(s);
			BufferedReader br = new BufferedReader(fr);
			int count = 0;
			String text;

			//cycle through the initialisation file reading into the return variable

			while ((text = br.readLine()) != null) {

				iniText[count] = text;
				count++;

			}		//End while
		


		}catch (IOException e){

			e.printStackTrace();

		}		//End try-catch
	


		return iniText;


	}									//End readIniFile method










	/**
	*gets all the file names in the current application execution directory
	*/

	String[] getFileNames(boolean fullyQualified){
		
		String s[];
		File f = new File(getResolvedClassPath());
		

		if (f.isDirectory()){
			
			s = f.list();

		}else{

			s = null;

		}

		if (fullyQualified) {qualifyPath(s);}

		return s;

	}  



	/**
	*overloads getFileNames returning all the jar file names in the current application execution directory
	*/

	String[] getFileNames(boolean JarFilter,boolean fullyQualified){
		
		String s[];
		FilenameFilter filterJAR = new JARFilter();
		File f = new File(getResolvedClassPath());
		

		if (f.isDirectory()){
			
			s = f.list(filterJAR);

		}else{

			s = null;

		}
 
		if (fullyQualified) {qualifyPath(s);}

		return s;

	}



	/**
	*Appends the fully qualified path of the file to it
	*/

	private void qualifyPath(String[] s){
		
		//get the qualified path
		String path = getResolvedClassPath();
		path = path.replace('\\','/') + '/';
	
		//cycle through all of the elements and concat the path to the front
		for (int i=0; i<s.length;i++){
			s[i] = path + s[i];
		}

	}



	/**
	*overloads getFileNames and returns all the file names in the specified directory
	*/

	String[] getFileNames(String dirName){
		
		String s[];
		File f = new File(dirName);
		

		if (f.isDirectory()){
			
			s = f.list();

		}else{

			s = null;

		}

		return s;

	} 













	
	private int linesInFile(String fileName) {				//Sart linesInFile method


		int i = 0;

		try{

			FileReader f = new FileReader(fileName);
			BufferedReader bfr = new BufferedReader(f);
			String s = "";


			while ((s = bfr.readLine()) != null) {

				i++;

			}		//End while


		}catch (IOException e){

			e.printStackTrace();

		}		//End try-catch


		return i;

	}									//End linesInFile method









	/**
	*Returns the resolved path of the application root
	*/

	String getResolvedClassPath(){					//Method getResolvedClassPath Start


		String sReturn = "";
		
		//get the string representations of the user directory and relative class path
		String sDir = System.getProperty("user.dir").replace('\\','/');
		String sClass = System.getProperty("java.class.path").replace('\\','/');


		//Check to see if the sClass variable ends with the name of the main .jar file
		//if it does then remove the name of the file from the string
		if (sClass.indexOf(".jar") > 0){ 
            if (sClass.indexOf("/") > 0){
                sClass = sClass.substring(0,sClass.lastIndexOf("/"));
            }
        }

		//set up the return variable as a concatination of the directory and the classpath
		sReturn = sDir + sClass;


		//create a file object using the sReturn variable
		File f = new File(sReturn);


		//Check to see if the file exists
		//If it does then the application has been started using a batch file and the variable is a valid path and can be returned
		//If it doesn't then the application has been started using a shortcut and the sDir variable holds the correct path so return that
		if (f.exists()){

			return sReturn;

		}else{

			return sDir;

		}




	}									//Method getResolvedClassPath End



}



class JARFilter implements FilenameFilter{

	public boolean accept(File dir,String name) {
		return (name.endsWith(".jar"));
	}

}