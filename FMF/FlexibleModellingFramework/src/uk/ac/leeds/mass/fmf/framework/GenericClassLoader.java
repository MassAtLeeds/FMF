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
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public abstract class GenericClassLoader  extends ClassLoader {
    public GenericClassLoader() {
    }





    /**
     * This is a generic version of the class loader
     * it extends ClassLoader and stores the loaded classes in a local hashtable
     * it also allows the use of a local source for loading and resolving classes stored as byte arrays
     * this is an abstract class and requires instantiating as a concrete class
     */

    public synchronized Class loadClass(String className, boolean resolveIt)
    	throws ClassNotFoundException {

        Class result;
	byte[] classBytes;




	//Check the local source to see if the class has already been loaded and is already stored locally
	//retrieveClassFromLocalStorage is an abstract method and needs to be implemented in the concrete class

	result = retrieveClassFromLocalStorage(className);






        // Check with the super class loader to see if the class can be loaded.
        //If not return null so that the interface will carry on loading other classes without crashing.

	if (result==null){
            try {

                result = super.findSystemClass(className);

            } catch (ClassNotFoundException e) {

                result = null;

            }		//End try-catch

        }	//End if

	







	if (result==null){

            //attempt to load the class from the locally stored repository of classes from the ResourceExtractor

            //loadClassFromBytes is an abstract method and needs to be implemnted in the concrete class
            classBytes = loadClassFromBytes(className);
        
            if (classBytes == null) {throw new ClassNotFoundException();}

            //If the class has been successfully loaded from the local source define it

	    try{

                result = defineClass(className,classBytes,0,classBytes.length);

	    }catch(Exception ex){

		//logs any error thrown by defineClass - abstract method needs implementing in the concrete class
		logError("GenericClassLoader","LoadClass",ex.toString());

	    }
	
        }	//End if
	



	//if class needs resolving resolve it

	if (resolveIt) {resolveClass(result);}




	//put the resolved class in the local hashtable store - abstract method requires implementation in the concrete class

	enterClassToLocalStorage(className,result);




	//return the resolved class

	return result;


    }




    protected abstract void enterClassToLocalStorage (String name, Class c);




    protected abstract Class retrieveClassFromLocalStorage (String name);




    protected abstract void logError(String c, String m, String msg);




    protected abstract byte[] loadClassFromBytes(String className);



}
