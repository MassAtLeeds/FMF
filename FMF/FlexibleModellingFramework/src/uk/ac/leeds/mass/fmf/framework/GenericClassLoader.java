/*
 * This is the new license...
 * It has been edited
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
