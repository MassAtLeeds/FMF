/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class FieldUndefinedException extends RuntimeException{
    
    private FieldUndefinedException(){}
    public FieldUndefinedException (String fieldName){
        super("Field " + fieldName + " cannot be used because it has not been defined yet!");
    }

}
