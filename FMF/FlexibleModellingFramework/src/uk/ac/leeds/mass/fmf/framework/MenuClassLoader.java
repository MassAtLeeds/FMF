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

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class MenuClassLoader  extends GenericClassLoader{
    //object only used for debugging and error tracking
    ApplicationInformation ai = ApplicationInformation.getCurrent();





    public MenuClassLoader() {
    }





    //implementation of the abstract methods from the generic class loader

    protected void enterClassToLocalStorage (String name, Class c){

	//place a class in local storage
	ai.enterClass(name,c);

    }




    protected Class retrieveClassFromLocalStorage (String name){

	//retreive a resolved class from local storage
        return ai.retrieveClass(name);

    }




        /**
         * Reports errors to the screen through the ApplicationInformation object.  If the
         * status window has not yet opened then the errors are logged in the Application Log file
         * which is located in the root of the classpath directory (where the FlexibleModellingFramework.jar 
         * is located).
         * 
         * @param c String name of the class in which the error occured.
         * @param m String name of the method where the error occured.
         * @param msg the message for the error which can be got from an Exception e using e.toString().
         */

    protected void logError(String c, String m, String msg){

	String nextLine = System.getProperty("line.separator");
        ai.writeToStatusWindow("CLASS: " + c + nextLine + "METHOD: " + m + 
                 nextLine + msg,true);

    }





    protected byte[] loadClassFromBytes(String className){


	//replace the . with forward hashs and append the .class extension then attempt to retrieve it from local storage

	String s = className.replace('.','/');	

	return ai.retrieveFile(s + ".class");

    }



        private void reportError(String methodName, String msg){
            String nextLine = System.getProperty("line.separator");
            ai.writeToStatusWindow("CLASS: ResourceExtractor" + nextLine + "METHOD: " + methodName + 
                    nextLine + msg,true);		

        }

}
