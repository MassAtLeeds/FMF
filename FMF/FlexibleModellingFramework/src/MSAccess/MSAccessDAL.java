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

import uk.ac.leeds.mass.fmf.data_management.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.ICallBack;
import uk.ac.leeds.mass.fmf.shared_objects.IProcessManager;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;




/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
 */
public class MSAccessDAL implements IThreadWakeUp, ICallBack{ //extends DataAccess


    private RegisteredDataSource rds = null;
    private ApplicationInformation ai = ApplicationInformation.getCurrent();
    private Connection con = null;
    private FMFTable table = null;
    private boolean shouldSleep = false;

    public String selectAll="";
	public String openEncap="";
	public String closeEncap="";

    private boolean replaceAll = false;

    private ArrayList<ICallBack> cb= new ArrayList<ICallBack>();

    public MSAccessDAL(FMFTable table){
        this(table.getRDS(),table);
    }

	public MSAccessDAL(RegisteredDataSource rds, FMFTable table){
        this.rds = rds;
        this.table = table;
        createConnection();
        setup();
    }

    RegisteredDataSource getRDS(){
        return this.rds;
    }


    private void createConnection(){
        //create the connection to the access database
        String conString = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="+rds.getFileName();
        try {
            con = DriverManager.getConnection(conString, rds.getUserName(), rds.getPassword());
        } catch (SQLException ex) {
            ai.writeToStatusWindow(ex.toString(), true);
        }
    }

    protected Connection getConnection(){
        return con;
    }

	public void setup(){
		selectAll="SELECT * FROM ";
		openEncap="[";
		closeEncap="]";
	}


    public ArrayList getUniqueValues(String fieldName){

        try{
			//only attempt to load the data if we have a valid connection to the database

			if (con != null){

                ArrayList al = new ArrayList();

				//Create a statment object from the connection
				Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

				//execute the statement and get the results set
				ResultSet rs = stmt.executeQuery("SELECT DISTINCT [" + fieldName + "] FROM [" + table.getName() + "];");

                while (rs.next()){
                    al.add(table.getObject(rs,fieldName));
                }

                rs.close();
                stmt.close();
                
                return al;

            }

		}catch (SQLException eSQL){
            eSQL.printStackTrace();
        }

        return null;

    }


	public String fieldType(int fieldType){
		String s ="";
		switch(fieldType){
		case FMFTable.FIELD_TYPE_STRING:
			s = "TEXT (250)";
			break;
		case FMFTable.FIELD_TYPE_INT:
			s = "INTEGER";
			break;
		case FMFTable.FIELD_TYPE_DOUBLE:
			s = "DOUBLE";
			break;
		}
		return s;
	}
        
    protected boolean checkNotSystemTable(String tableName){
        if (tableName.startsWith("~") || tableName.startsWith("MSys")){return false;}
        return true;
    }
	
	
    public void createNewTable(){

		try{

			if (con != null){

                //Create the start of the create table statement
                String s = "CREATE TABLE " +openEncap+ table.getName() +closeEncap+ " (";

                //cycle through the fields and add them to the SQL string in encapsulation characters
                for (int i=0; i < table.getNewTableFieldCount(); i++){

                    if (i > 0) { s += ","; }

                    s += openEncap + table.getNewTableFieldName(i) + closeEncap + " " + fieldType( table.getNewTableFieldType(i) );
                }

                //append the final part of the SQL string
                s += ");";
                
                //Create a statment object from the connection and create the table
                Statement stmt = con.createStatement();
                stmt.execute(s);

                //close the statement
                stmt.close();

			}

		}catch (SQLException eSQL){
            eSQL.printStackTrace();
        }

	}

    public void loadFields(FMFTable table){
        loadTable(table, emptySelectStatement());
    }

    public void loadTable(FMFTable table){
        loadTable(table, selectStatement());
    }

	private void loadTable(FMFTable table, String statement){
		try{
			//only attempt to load the data if we have a valid connection to the database

			if (con != null){

				//Create a statment object from the connection
				Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

				//execute the statement and get the results set
				ResultSet rs = stmt.executeQuery(statement);

                table.setResultSet(rs, stmt);

            }

		}catch (SQLException eSQL){
            eSQL.printStackTrace();
        }
	}


    public void saveData(FMFTable table){
        
        replaceAll(true);
        MSAccessInsertData insert = new MSAccessInsertData(this, table);
		insert.setName("Saving to " + table.getName());
		insert.setAI(ai);
        insert.setCallBack(this);
        insert.setCallBack(new DataCallBack(this));
		IProcessManager pm = ai.getProcessManager();
        shouldSleep = true;
		pm.addProcess(insert);
        sleep();

    }

	//allows a callback to be set so that any unfinished business can be cleared before progressing
	public void setCallBack(ICallBack c){cb.add(c);}

	/**
	*Implemented interface that is called as the last part of a FrameworkProcess
	*/
	public void callBack(){
		closeConnection();
		//if a callback has been set on this object execute it
		if (!cb.isEmpty()){
			for(int i=0;i<cb.size();i++){
				cb.get(i).callBack();
			}
		}
	}


    private String emptySelectStatement(){
		String s  = "SELECT * ";
		s += " FROM " + openEncap + table.getName() + closeEncap;
        s += " WHERE 1=3;";
		return s;
	}

	public String selectStatement(){
		String s  = "SELECT * ";

		s += " FROM " + openEncap + table.getName() + closeEncap;

        if ( table.isFiltered() ){
            String textQual = "";
            if ( !table.isNumeric( table.getFilterField() ) ){textQual = "'";}
            s += " WHERE " + openEncap + table.getFilterField() + closeEncap + 
                    " = " + textQual + table.getFilterValue() + textQual;
        }

		s += ";";
		return s;
	}

	public String insert(){
		//create the first part of the insert statement
		StringBuilder s = new StringBuilder();
        s.append("INSERT INTO " + openEncap + table.getName() + closeEncap + " (");

		//cycle through and append all the fields onto the string
		for (int i=0; i < table.getFieldCount(); i++){
			if ( i>0 ) {s.append(",");}
			s.append(openEncap + table.getFieldName(i) + closeEncap);
		}

		//append the final part of the SQL statement
		s.append(") VALUES (");

		return s.toString();
	}

	public boolean tableExists(){
		boolean b = false;

		try{
			//test and make sure that their is a valid connection
			if (con != null){

				//get a meta data object for the database
				DatabaseMetaData md = con.getMetaData();

				//get a results set of the metadata for the table
				ResultSet rs = md.getTables(null,null,table.getName(),null);

				//cycle through the results in case more than one row is returned
				while(rs.next()){
					//check to see if the table name is the same as the one we are looking for
					//if it is break the while loop and return true
					if (rs.getString("TABLE_NAME").equalsIgnoreCase(table.getName())){
						b = true;
						break;
					}
				}
			}
		}catch (SQLException eSQL){ai.writeToStatusWindow(eSQL.toString(),true);}

		return b;

	}




	public boolean dropTable(){
		try{
			//only attempt to make the table if we have a valid connection to the database
			if (con != null){
				//Create a statment object from the connection
				Statement stmt = con.createStatement();
				//Execute the statement to drop the specified table
				stmt.execute("DROP TABLE " +openEncap+ table.getName() +closeEncap+ ";");
				//close the statement object
				stmt.close();

                return true;
			}

            return false;
            
		}catch (SQLException eSQL){
            ai.writeToStatusWindow(eSQL.toString(),true);
            return false;
        }

	}


   /**
	* Closes the current connection to the database.
	*/
	public void closeConnection(){
		//if connection exists close connection
		try{if (con!=null){con.close();}}catch(SQLException e){}
	}


	//these are the methods for getting information about table and fields in a database
	public String[] getTables(){
		ArrayList tabs = new ArrayList();

		try{

			//test and make sure that their is a valid connection

			if (con != null){


				//get a meta data object for the database

				DatabaseMetaData md = con.getMetaData();


				//get a results set of the metadata for the table
                String[] tableFilter = {"TABLE"};
				ResultSet rs = md.getTables(null,null,null,tableFilter);

				//cycle through the results in case more than one row is returned

				while(rs.next()){
                    String tableName = rs.getString("TABLE_NAME");

                    //check and make sure that this is not a system table before adding it.
                    if (checkNotSystemTable(tableName)){tabs.add(tableName);}
                }

				String s[] = new String[tabs.size()];
				tabs.toArray(s);

				return s;


			}else{
				return null;

			}	//End if


		}catch (SQLException eSQL){

			return null;

		}	//End try-catch
	}


 	public void replaceAll(boolean b){replaceAll=b;}
	public boolean replaceAll(){return replaceAll;}

    public boolean isConnected(){
        if ( con != null ){return true;} else { return false;}
    }

    private synchronized void sleep(){
        try {
            if (shouldSleep){
                wait();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public synchronized void wakeUp(){
        shouldSleep = false;
        notifyAll();
    }

    
}

