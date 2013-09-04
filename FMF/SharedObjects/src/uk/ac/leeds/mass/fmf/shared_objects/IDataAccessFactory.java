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
