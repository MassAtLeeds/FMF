/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 *
 * @author geo8kh
 */
public interface IRDSListener {
    public void DataChanged(RegisteredDataSource rds, String table, String field);
}
