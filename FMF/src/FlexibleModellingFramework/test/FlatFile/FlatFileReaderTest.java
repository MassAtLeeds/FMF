package FlatFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.leeds.mass.fmf.data_management.DataAccessFactory;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;

/**
 *
 * @author geo8kh
 */
public class FlatFileReaderTest {

    public FlatFileReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of loadData method, of class FlatFileReader.
     */
    @Test
    public void testLoadData() {
        //zones = 6000, persons = 10000 takes 140 seconds to create the file
        //but two to three days to test the load...
        int zones = 100;
        int persons = 200;
        String path = TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER;

        TestLocationAndFiles util = new TestLocationAndFiles();
        if ( !util.createTestLocation() ) {Assert.fail("Failed to create test location");}

        RegisteredDataSource rds = createRDS(TestLocationAndFiles.TEST_PATH);

        FMFTable t = new FMFTable("LargeFile.csv",null,null);
        t.setAi(ApplicationInformation.getCurrent());
        rds.addTable(t);

        RegistrationHandler fml = new RegistrationHandler(t);
        fml.setDelimiter(",");
        fml.setHeadersInFirstRow(true);
        fml.setTextQualifier("\"");
        fml.setFileDate(new Date());
        fml.addField("ZoneID", FMFTable.FIELD_TYPE_STRING);
        fml.addField("PersonID",FMFTable.FIELD_TYPE_STRING);
        int fmlID = fml.writeFMLFile();

        File f = new File(path+"LargeFile.csv");
        String nextLine = System.getProperty("line.separator");

        try{
            if (f.exists()){f.delete();}    
            f.createNewFile();

            //create a random access file object and move to the appropriate place for insertion of new log entry
            FileWriter r = new FileWriter(f);
            r.write("ZoneID,PersonID"+nextLine);

            for (int i = 0; i < zones; i++) {
                for (int j = 0; j < (persons - i); j++) {
                    r.write("Zone"+i+",Person"+j+nextLine);
                }
            }

            //close the random access file
            r.flush();
            r.close();

            System.out.println("LargeFile.csv created");

        }catch(IOException e){
            System.out.println("Problem writing to file: "+f.getAbsolutePath());
            e.printStackTrace();
        }finally{
            f = null;
        }


        for (int i = 0; i < zones; i++) {
            FlatFileReader reader = new FlatFileReader(rds);
            t.setFilter("ZoneID", "Zone"+i);

            System.out.println("ZoneID: Zone" + i + " - Records loaded: "+t.getRowCount());
            Assert.assertEquals(persons - i, t.getRowCount());
        }
        
        RegistrationHandler.deleteFMLFile(fmlID);
        util.removeTestLocation();

    }

    private RegisteredDataSource createRDS(String fileName){
        RegisteredDataSource rds = new RegisteredDataSource();
        rds.setDataType(DataAccessFactory.FLAT_FILE);
        rds.setDSN(false);
        rds.setFileName(fileName);
        rds.setPassword("");
        rds.setRdsID(1);
        rds.setUserName("");

        return rds;
    }

}