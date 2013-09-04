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


package FlatFile;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.ResultSet;
import java.sql.SQLException;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author geo8kh
 */
public class FlatFileManager {

    private static FlatFileManager ffm = null;
    private int fileID = -1;

    private FlatFileManager(){
        fileID = 0;
        try {
            ResultSet rs = ApplicationDatabase.getCurrent().executeQuery("SELECT " + RegistrationHandler.FILE_ID +
                    " FROM " + RegistrationHandler.TABLE_NAME + " ORDER BY " + RegistrationHandler.FILE_ID + " DESC");
            if ( rs != null ){
                while(rs.next()){
                    fileID = rs.getInt(RegistrationHandler.FILE_ID);
                    break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static synchronized FlatFileManager getCurrent(){
        if ( ffm == null ){ffm = new FlatFileManager();}
        return ffm;
    }

    synchronized int allocateNextFileID(){
        fileID++;
        return fileID;
    }

    static FMFTable createTableFromHandler(RegistrationHandler rh){
        FMFTable table = new FMFTable(rh.getFileName(),null, null);
        table.setAi(ApplicationInformation.getCurrent());

        return table;
    }

    static String stripFileExtention(String fileName){
        int position = fileName.lastIndexOf(".");
        if (position>-1){
            return fileName.substring(0, position);
        }else{
            return fileName;
        }
    }

    static String appendExtention(FMFTable table){

        return appendExtention ( table.getRDS().getFileName(), table.getName() );

    }

    static String appendExtention(final String path, final String tableName){
        
        File file = new File(path);
        if (file.exists()){
            File[] files = file.listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(tableName);
                }
            });

            if (files.length==1){
                return files[0].getName();
            }else{
                return tableName+".csv";
            }

        }else{
            return tableName+".csv";
        }        
    }


}
