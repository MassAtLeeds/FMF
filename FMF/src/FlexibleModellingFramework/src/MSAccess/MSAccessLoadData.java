/*
 * This is the new license...
 * It has been edited
 */

package MSAccess;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
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
