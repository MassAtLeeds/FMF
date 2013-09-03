/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public interface IFMFTableListener {
    public void tableChanged(String tableName, String fieldName);
    public void tableIsDirty(String tableName, String fieldName);
}
