
package FlatFile;

import java.io.File;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.leeds.mass.fmf.data_management.DataAccessFactory;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import static org.junit.Assert.*;

/**
 *
 * @author geo8kh
 */
public class FlatFileManagerTest {

    private static TestLocationAndFiles util = new TestLocationAndFiles();

    public FlatFileManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        util.createTestLocation();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        util.removeTestLocation();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        File file = new File(TestLocationAndFiles.TEST_PATH);
        for (File f : file.listFiles()) {
            System.out.println("Removed file '"+ f.getName() + "' = " +  f.delete() );
        }
    }


    @Test
    public void testStripFileExtention(){
        assertEquals("file", FlatFileManager.stripFileExtention("file.csv"));
        assertEquals("file.txt", FlatFileManager.stripFileExtention("file.txt.csv"));
        assertEquals("file.txt", FlatFileManager.stripFileExtention("file.txt.bhjdkle"));
        assertEquals("file", FlatFileManager.stripFileExtention("file"));
        assertEquals("f", FlatFileManager.stripFileExtention("f.csv"));
        assertEquals("", FlatFileManager.stripFileExtention(".csv"));
        assertEquals("file ", FlatFileManager.stripFileExtention("file .csv"));

    }

    @Test
    public void testAppendExtention(){

        RegisteredDataSource rds = createRDS(TestLocationAndFiles.TEST_PATH);

        try{
            //create standard new file
            File f = new File(rds.getFileName() + TestLocationAndFiles.FILE_SEPARATER + "new file 1.csv");
            f.createNewFile();
            assertTrue(f.exists());
            //create the table
            FMFTable table = new FMFTable(FlatFileManager.stripFileExtention(f.getName()),null,null);
            rds.addTable(table);
            assertEquals("new file 1",table.getName());
            //test appending the extention back on
            assertEquals("new file 1.csv",FlatFileManager.appendExtention(table));

            //create a file without an extention
            f = new File(rds.getFileName() + TestLocationAndFiles.FILE_SEPARATER + "no extention");
            f.createNewFile();
            assertTrue(f.exists());
            //create the table
            table = new FMFTable(FlatFileManager.stripFileExtention(f.getName()),null,null);
            rds.addTable(table);
            assertEquals("no extention",table.getName());
            //test appending the extention back on
            assertEquals("no extention",FlatFileManager.appendExtention(table));
            
            //create standard new file but keep the extention on the table name return should only have existing extention
            f = new File(rds.getFileName() + TestLocationAndFiles.FILE_SEPARATER + "double extention.csv");
            f.createNewFile();
            assertTrue(f.exists());
            //create the table
            table = new FMFTable("double extention.csv",null,null);
            rds.addTable(table);
            assertEquals("double extention.csv",table.getName());
            //test appending the extention back on
            assertEquals("double extention.csv",FlatFileManager.appendExtention(table));

            //create a file with a none standard extention
            f = new File(rds.getFileName() + TestLocationAndFiles.FILE_SEPARATER + "new file 2.svxcd");
            f.createNewFile();
            assertTrue(f.exists());
            //create the table
            table = new FMFTable(FlatFileManager.stripFileExtention(f.getName()),null,null);
            rds.addTable(table);
            assertEquals("new file 2",table.getName());
            //test appending the extention back on
            assertEquals("new file 2.svxcd",FlatFileManager.appendExtention(table));

            //create two files with different extentions - should return a .csv extention regardless
            f = new File(rds.getFileName() + TestLocationAndFiles.FILE_SEPARATER + "new duplicate file.svxcd");
            f.createNewFile();
            assertTrue(f.exists());
            f = new File(rds.getFileName() + TestLocationAndFiles.FILE_SEPARATER + "new duplicate file.svx");
            f.createNewFile();
            assertTrue(f.exists());
            //create the table
            table = new FMFTable("new duplicate file",null,null);
            rds.addTable(table);
            //test appending the extention back on
            assertEquals("new duplicate file.csv",FlatFileManager.appendExtention(table));

            //create a table without a file, should return a .csv extention
            //create the table
            table = new FMFTable("fileless table",null,null);
            rds.addTable(table);
            //test appending the extention back on
            assertEquals("fileless table.csv",FlatFileManager.appendExtention(table));
            
            //create a table with a space at the end of the file name before the extention
            f = new File(rds.getFileName() + TestLocationAndFiles.FILE_SEPARATER + "spaced out file .bob");
            f.createNewFile();
            assertTrue(f.exists());
            //create the table
            table = new FMFTable(FlatFileManager.stripFileExtention(f.getName()),null,null);
            rds.addTable(table);
            assertEquals("spaced out file ",table.getName());
            //test appending the extention back on
            assertEquals("spaced out file .bob",FlatFileManager.appendExtention(table));

        }catch(IOException ioe){
            ioe.printStackTrace();
        }

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