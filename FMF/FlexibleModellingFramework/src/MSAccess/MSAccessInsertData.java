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



public class MSAccessInsertData extends FrameworkProcess{


    private FMFTable table = null;
	private MSAccessDAL dal = null;
	IApplicationInformation ai = null;

    public MSAccessInsertData (MSAccessDAL dal, FMFTable table){
        this.dal = dal;
        this.table = table;
    }

	public void runProcess(){
        StringBuilder values = new StringBuilder();

		try{

			Connection con = dal.getConnection();
			//only attempt to make the table if we have a valid connection to the database
			if (con != null){

				//get the data array and initialise the progress bar
				initilise(table.insertedRowCount()+1);

				if(!cancelled){
					//Create a statment object from the connection
					Statement stmt = con.createStatement();

					progress=1;

					//loop through all of the data and execute insert statements
					for(int i=0; i<table.insertedRowCount(); i++){

						//if cancelled flag has been set terminate process and return
						if(cancelled){break;}

                        for (int j = 0; j < table.getFieldCount(); j++) {
                            if ( j>0 ){ values.append(","); }
                            if ( table.getColumnType(j) == FMFTable.FIELD_TYPE_STRING ){
                                values.append("'" + table.getInsertedValue(i, j).toString() + "'");
                            }else {
                                values.append( table.getInsertedValue(i, j).toString() );
                            }
                        }

						stmt.execute( dal.insert() + values + ");" );
                        values.delete(0, values.length());

						//update progress
						progress=i+1;

					}

					//close the statement
					stmt.close();

				}

			}else{
				initilise(1);
			}

			if(!cancelled){finished();}


		}catch (SQLException eSQL){
            System.out.println( dal.insert() +  values + ");" );
            ai.writeToStatusWindow(eSQL.toString(),true);
        }

        table.setDataSaving(false);
    }
	
	void setAI(IApplicationInformation a){ai = a;}
	
	
}
