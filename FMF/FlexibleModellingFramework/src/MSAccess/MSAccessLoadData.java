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

package MSAccess;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
import java.sql.*;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;



public class MSAccessLoadData extends FrameworkProcess{

	
	private MSAccessDAL dal = null;
	IApplicationInformation ai = null;
    private FMFTable table = null;

    public MSAccessLoadData (FMFTable table, MSAccessDAL dal){
        this.table = table;
        this.dal = dal;
    }

	public void runProcess(){
		
		String s="";
		try{

			Connection con = dal.getConnection();
			//only attempt to load the data if we have a valid connection to the database
			
			if (con != null){
				
				//Create a statment object from the connection
				Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				s=dal.selectStatement();
				//execute the statement and get the results set
				ResultSet rs = stmt.executeQuery(s);

                table.setResultSet(rs, stmt);

                stmt = null;
			}

			if(!cancelled){finished();}


		}catch (SQLException eSQL){ai.writeToStatusWindow(eSQL.toString()+"\n"+s,true);}
	}
	
	void setAI(IApplicationInformation a){ai = a;}
	
	
}
