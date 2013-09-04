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
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author Kirk Harland
 */

public class FlatFileFilter extends FileFilter{

        @Override
    public boolean accept(File f){
        String name = f.getName();

	if (f.isDirectory()){
            return true;
	}else if (!name.equals("")){
            if (name.length()>4){
                if ((name.endsWith(".txt")) ||
                    (name.endsWith(".TXT"))){
                    return true;
                }
                if ((name.endsWith(".csv")) ||
                    (name.endsWith(".CSV"))){
                    return true;
                }
            }

            return false;
	}

        return false;

    }

    @Override
    public String getDescription(){
        return "Flat File";
    }


}
