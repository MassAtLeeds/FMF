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

package uk.ac.leeds.mass.fmf.data_management;

import FlatFile.FlatFileListener;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;
import uk.ac.leeds.mass.fmf.shared_objects.Menu;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class DataSourcePopup extends Menu{
    
    public final static int REMOVE_DATA_SOURCE = 10;
    
    public final static int OPEN_TABLE = 20;
    
    private int popupType;
    private RegisteredDataSource rds;
    private FMFTable table;

    DataSourcePopup(int popupType){this.popupType = popupType;}

    public DataSourcePopup(int popupType, RegisteredDataSource rds){
        this.popupType = popupType;
        this.rds = rds;
    }

    public DataSourcePopup(int popupType, FMFTable table){
        this(popupType, table.getRDS());
        this.table = table;
    }
    
    @Override
    protected Object[][] getItems(){
        
        Object[][] o = null;
        boolean flatFileRDS = false;

        if ( popupType > -1 && popupType!=TreeCellInfo.INVALID_RDS && rds.getDataType()== DataAccessFactory.FLAT_FILE ){
            flatFileRDS = true;
        }

        if ((popupType==TreeCellInfo.RDS) || (popupType==TreeCellInfo.INVALID_RDS)
                || (popupType==TreeCellInfo.PROPERTIES_FOLDER) || (popupType==TreeCellInfo.PROPERTY) ){

            DataAccessFactory daf = new DataAccessFactory();
            //sets up the entries for the datasource tree popup menu.
            int menuLocation = 0;
            if ( flatFileRDS ){
                o = new  Object[DataAccessFactory.DATA_TYPE_COUNT+3][6];
                o[menuLocation][0] = "Register file";
                o[menuLocation][1] = new FlatFileListener(ai, FlatFileListener.REGISTER_FILE, rds);
                menuLocation++;
            } else {
                o = new Object[DataAccessFactory.DATA_TYPE_COUNT+2][6];
            }
            
            o[menuLocation][0] = "Remove data source";
            o[menuLocation][1] = RegisteredDataSources.getCurrent(REMOVE_DATA_SOURCE);
            menuLocation++;

            //create the add datasource group
            o[menuLocation][0] = "Add data source";
            o[menuLocation][1] = null;
            o[menuLocation][5] = 1;
            menuLocation++;
            for (int i=0; i<DataAccessFactory.DATA_TYPE_COUNT;i++){
                o[i+menuLocation][0] = daf.getMenuName(i+1);
                o[i+menuLocation][1] = daf.getMenuAction(i+1,ai);
                o[i+menuLocation][5] = 1;
            }


        }else if(popupType==TreeCellInfo.TABLE_FOLDER){

            //sets up the entries for the datasource tree popup menu.
            int menuLocation = 0;
            if ( flatFileRDS ){
                o = new  Object[2][6];
                o[menuLocation][0] = "Register file";
                o[menuLocation][1] = new FlatFileListener(ai, FlatFileListener.REGISTER_FILE, rds);
                menuLocation++;
            } else {
                //sets up the entries for the table folder tree popup menu.
                o = new Object[1][6];
            }

//            o[menuLocation][0] = "Create new table";
//            o[menuLocation][1] = new CreateTableAction(rds);
//            menuLocation++;

            o[menuLocation][0] = "Clear all table caches";
            o[menuLocation][1] = new ClearAllTablesAction();
            menuLocation++;
            
        }else if(popupType==TreeCellInfo.TABLE){
            //sets up the entries for the datasource tree popup menu.
            o = new Object[4][6];
            
            o[0][0] = "Open table";
            o[0][1] = new LoadTableAction(true);
            o[1][0] = "Load data";
            o[1][1] = new LoadTableAction(false);
            o[2][0] = null;
            o[2][1] = null;
            o[3][0] = "Drop table";
            o[3][1] = new DropTableAction();


        }else if(popupType==TreeCellInfo.TABLE_CACHED){
            //sets up the entries for the datasource tree popup menu.
            o = new Object[4][6];

            o[0][0] = "Open table";
            o[0][1] = new LoadTableAction(true);
            o[1][0] = "Clear cache";
            o[1][1] = new ClearCacheAction();
            o[2][0] = null;
            o[2][1] = null;
            o[3][0] = "Drop table";
            o[3][1] = new DropTableAction();


        }else if(popupType==TreeCellInfo.TABLE_ALTERED){
            //sets up the entries for the datasource tree popup menu.
            o = new Object[5][6];

            o[0][0] = "Open table";
            o[0][1] = new LoadTableAction(true);
            o[1][0] = "Save changes";
            o[1][1] = new SaveTableAction();
            o[2][0] = "Discard changes and clear cache";
            o[2][1] = new ClearCacheAction();
            o[3][0] = null;
            o[3][1] = null;
            o[4][0] = "Drop table";
            o[4][1] = new DropTableAction();


        }else{
            DataAccessFactory daf = new DataAccessFactory();
            //sets up the entries for the datasource tree popup menu.
            o = new Object[DataAccessFactory.DATA_TYPE_COUNT+1][6];
            
            //create the add datasource group
            o[0][0] = "Add data source";
            o[0][1] = null;
            o[0][5] = 1;
            for (int i=0; i<DataAccessFactory.DATA_TYPE_COUNT;i++){
                o[i+1][0] = daf.getMenuName(i+1);
                o[i+1][1] = daf.getMenuAction(i+1,ai);
                o[i+1][5] = 1;
            }
        }

        
        return o;
    }
    
}
