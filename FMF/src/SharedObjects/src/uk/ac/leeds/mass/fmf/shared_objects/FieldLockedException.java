/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class FieldLockedException  extends RuntimeException{
    
    private FieldLockedException(){}
    /**
     * Constructor for the Exception
     * @param fieldName : name of the field that has caused the exception to be thrown
     */
    public FieldLockedException(String fieldName){
        super("Field " + fieldName + " cannot be updated because it is locked!");
    }

}
