/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.util.StringTokenizer;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class AppInitialisation {
    /**
     * Creates a new instance of AppInitialisation 
     */
    AppInitialisation() {
    }
    
    




    ApplicationInformation ai = ApplicationInformation.getCurrent();




    void InitialiseApplication() {							//Start InitialiseApplication method

	//Empty the current log
	ai.clearLog();


	//read the initialisation file
	FileManager fm = new FileManager();
	String s[] = fm.readIniFile();

	setCurrentConfiguration(s);

    }											//End InitialiseApplication method
    
    



    
    
    void writeInit(){
	    FileManager fm = new FileManager();
	    fm.clearIniFile();
	    fm.writeToIniFile("<DEBUG>" + new Boolean(ai.getDebugMode()).toString());
	    fm.writeToIniFile("<LOAD FROM LOGON>" + new Boolean(ai.getLoadMode()).toString());
	    fm.writeToIniFile("<APPLICATION TITLE>" + ai.getApplicationTitle());
    }
    
    




    private void setCurrentConfiguration(String configText[]) {				//Start setCurrentConfiguration method

	StringTokenizer st;
	String key = "";
	String value = "";	

	for (int i = 0; i<configText.length;i++){
		st = new StringTokenizer(configText[i],"<>");

		if (st.hasMoreTokens()){key = st.nextToken();} else {key = "";}
		if (st.hasMoreTokens()){value = st.nextToken();} else {value = "";}

		setVariables(key,value);


	}	//End for


	ai.setConfigChanged(false);

    }											//End setCurrentConfiguration method









    private void setVariables(String key, String value){				//Start setVariables method

	Boolean B = new Boolean(value);
	boolean b = B.booleanValue();

        if (key.equalsIgnoreCase("DEBUG")){

        	ai.setDebugMode(b);
	
        }else if (key.equalsIgnoreCase("LOAD FROM LOGON")){

        	ai.setLoadMode(b);

        }else if (key.equalsIgnoreCase("APPLICATION TITLE")){

        	ai.setApplicationTitle(value);


        }	//End if


    }											//End setVariables mehtod

}
