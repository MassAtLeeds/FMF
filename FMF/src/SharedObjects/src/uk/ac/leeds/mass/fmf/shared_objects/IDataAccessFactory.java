/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import java.util.ArrayList;

/**
 *
 * @author geo8kh
 */
public interface IDataAccessFactory {

    public void loadData(FMFTable table, ICallBack cb);
    public void loadDataInBackground(final FMFTable table, final ICallBack cb);
    public void loadFields(FMFTable table);

    public FMFTable clearCache(FMFTable table);
    public void commitInsert(FMFTable table);
    public void dropTable(FMFTable table);
    public void createTable(RegisteredDataSource rds, FMFTable table, boolean overwrite);

    public ArrayList getUniqueValues(FMFTable table, String fieldName);

}
