
package FlatFile;

import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.SystemProperties;

/**
 *
 * @author Kirk Harland
 */
public class FlatFileVerifyDataSource extends FrameworkProcess {

    private IApplicationInformation ai;
    private RegisteredDataSource rds;


    private FlatFileVerifyDataSource(){}

    public FlatFileVerifyDataSource(IApplicationInformation ai, RegisteredDataSource rds){
        this.ai = ai;
        this.rds = rds;
    }

    public FlatFileVerifyDataSource(IApplicationInformation ai, int scrID){
        this.ai = ai;
        this.rds = createRDS(scrID);
    }

    @Override
    public void runProcess() {

        //create a flat file data access layer object and connect to the file system location
        FlatFileDAL d = new FlatFileDAL(rds);

        //See if the connection can be made
        if (d.isConnected()){

            RegistrationHandler.createTable();

            //need to get a list of all the tables in the datasource and verify that they have not changed in structure
            //get a list of all the tables in the data source
            String tables[] = d.getTables();
            if (tables!=null){

                //set up the length of the process and the fields array
              initilise(tables.length);

                //loop through all of the tables and get the fields for each one
                for(int i=0;i<tables.length;i++){
                    RegistrationHandler rh = new RegistrationHandler(rds.getRdsID(),tables[i]);
                    FMFTable t = FlatFileManager.createTableFromHandler(rh);

                    //update progress
                    progress=i;
                    //if cancelled flag has been set terminate process and return
                    if(cancelled){
                        rds.setValid(false);
                        break;
                    }

                    rds.addTable(t);

                }

            }

            if(!cancelled){rds.setValid(true);}
        }else{
            rds.setValid(false);
            initilise(1);
        }

        if(!cancelled){finished();}


    }

    RegisteredDataSource createRDS(int scrID){

        RegisteredDataSource r =  new RegisteredDataSource();
        ApplicationDatabase db = ApplicationDatabase.getCurrent();

        r.setRdsID(scrID);
        r.setFileName(db.getDatasourceProperty(scrID, SystemProperties.DB_NAME));
        r.setUserName(db.getDatasourceProperty(scrID, SystemProperties.DB_USER));
        r.setDSN(false);
        String pwd = db.getDatasourceProperty(scrID, SystemProperties.DB_PSWD);
        
        r.setPassword(pwd);

        return r;

    }

    public RegisteredDataSource getSuccess(){return rds;}

}
