package FlatFile;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.data_management.DataAccessFactory;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import static org.junit.Assert.*;

/**
 *
 * @author geo8kh
 */
public class FlatFileDALTest {

    private TestLocationAndFiles util = new TestLocationAndFiles();


    public FlatFileDALTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        util.createTestLocation();
    }

    @After
    public void tearDown() {
        
        util.removeTestLocation();

    }

    /**
     * Test getFileFromRDS method, of class FlatFileDAL.
     */


    @Test
    public void testGetFileFromValidRDS() {
        //write out the name of the test
        System.out.println("getFileFromRDS - Valid RDS");

        //construct a valid new registered data source so that we can perform the tests
        RegisteredDataSource rds = createCorrectRDS();

        FlatFileDAL instance = new FlatFileDAL(null);
        File expResult = new File(TestLocationAndFiles.TEST_PATH);
        File result = instance.getFileFromRDS(rds);

        assertEquals(expResult, result);

    }

    @Test
    public void testGetFileFromInvalidRDS() {
        //write out the name of the test
        System.out.println("getFileFromRDS - Invalid RDS");

        //construct a valid new registered data source so that we can perform the tests
        RegisteredDataSource rds = createIncorrectRDS();

        FlatFileDAL instance = new FlatFileDAL(null);
        File result = instance.getFileFromRDS(rds);

        assertNull(result);

    }

    /**
     * Test isConnected method, of class FlatFileDAL.
     */
    @Test
    public void testIsConnectedUsingValidRDS() {
        System.out.println("isConnected - valid RDS");

        FlatFileDAL instance = new FlatFileDAL(createCorrectRDS());
        boolean expResult = true;
        boolean result = instance.isConnected();
        assertEquals(expResult, result);

    }

    @Test
    public void testIsConnectedUsingInvalidRDS() {
        System.out.println("isConnected - Invalid RDS");

        FlatFileDAL instance = new FlatFileDAL(createIncorrectRDS());
        boolean expResult = false;
        boolean result = instance.isConnected();
        assertEquals(expResult, result);

    }

    /**
     * Test the get tables method.  Ensures that all table that have a .fml file and a corresponding
     * flat file of the name in the fml file are returned regardless of case.  Any fml files that do not 
     * have a corresponding base file should be deleted.
     */
    @Test
    public void testGetTables(){

        FMFTable t = null;
        RegistrationHandler fml = null;

        RegisteredDataSource rds = createCorrectRDS();
        FlatFileDAL instance = new FlatFileDAL(rds);

        String path = TestLocationAndFiles.TEST_PATH+TestLocationAndFiles.FILE_SEPARATER;

        //check that the DAL has connected correctly
        assertEquals(true, instance.isConnected());

        //create a file Test1.csv with a Test1.csv.fml file - should get returned
        util.createTestFile(path+"Test1.csv", ",", true, "");
        t = new FMFTable("Test1.csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        int fmlID1 = fml.writeFMLFile();
        //check that the files have been created for the test to run correctly
        assertTrue(new File(path+"Test1.csv").exists());


        //create a file Test2.csv with a Test2.csv.fml file - should get returned
        util.createTestFile(path+"Test2.csv", ",", true, "");
        t = new FMFTable("Test2.csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        int fmlID2 = fml.writeFMLFile();
        //check that the files have been created for the test to run correctly
        assertTrue(new File(path+"Test2.csv").exists());


        //create a Test3 fml file - should get deleted
        t = new FMFTable("Test3.csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        int fmlID3 = fml.writeFMLFile();
        //check that the files have not been created for the test to run correctly
        assertFalse(new File(path+"Test3.csv").exists());

        //create a Test4.csv file - should get ignored
        util.createTestFile(path+"Test4.csv", ",", true, "");
        //check that the files have been created for the test to run correctly
        assertTrue(new File(path+"Test4.csv").exists());

        //create a Test 5 .csv file - should get ignored
        util.createTestFile(path+"Test 5 .csv", ",", true, "");
        //check that the files have been created for the test to run correctly
        assertTrue(new File(path+"Test 5 .csv").exists());

        //create a Test 6 .csv file with a Test 6 fml file - should get returned
        util.createTestFile(path+"Test 6 .csv", ",", true, "");
        t = new FMFTable("Test 6 .csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        int fmlID6 = fml.writeFMLFile();
        //check that the files have been created for the test to run correctly
        assertTrue(new File(path+"Test 6 .csv").exists());

        //create a Test 7 .csv.fml file - should get deleted
        t = new FMFTable("Test 7 .csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        int fmlID7 = fml.writeFMLFile();
        //check that the files have not been created for the test to run correctly
        assertFalse(new File(path+"Test 7 .csv").exists());

        //get the table list from the DAL and add them to an ArrayList
        ArrayList<String> tables = new ArrayList<String>();
        String[] ts = instance.getTables();
        for (int i = 0; i < ts.length; i++) {tables.add(ts[i]);}
        //remove each table that should have been returned
        tables.remove("Test1.csv");
        tables.remove("Test2.csv");
        tables.remove("Test 6 .csv");

        //test to make sure that the size of the ArrayList is now 0 because all of the tables that should
        //have been returned have been deleted.
        Assert.assertEquals(0, tables.size());

        int counter = 0;
        //check and make sure that the two fml files that should have beed deleted have been
        ResultSet rs = ApplicationDatabase.getCurrent().executeQuery(
                "SELECT * FROM " + RegistrationHandler.TABLE_NAME
                + " WHERE " + RegistrationHandler.FILE_ID + " = " + fmlID1);
        try {
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        if (counter == 0){Assert.fail("resultset had no records");}

        counter = 0;
        rs = ApplicationDatabase.getCurrent().executeQuery(
                "SELECT * FROM " + RegistrationHandler.TABLE_NAME
                + " WHERE " + RegistrationHandler.FILE_ID + " = " + fmlID2);
        try {
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        if (counter == 0){Assert.fail("resultset had no records");}

        counter = 0;
        rs = ApplicationDatabase.getCurrent().executeQuery(
                "SELECT * FROM " + RegistrationHandler.TABLE_NAME
                + " WHERE " + RegistrationHandler.FILE_ID + " = " + fmlID3);
        try {
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        if (counter > 0){Assert.fail("resultset had records");}

        counter = 0;
        rs = ApplicationDatabase.getCurrent().executeQuery(
                "SELECT * FROM " + RegistrationHandler.TABLE_NAME
                + " WHERE " + RegistrationHandler.FILE_ID + " = " + fmlID6);
        try {
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        if (counter == 0){Assert.fail("resultset had no records");}

        counter = 0;
        rs = ApplicationDatabase.getCurrent().executeQuery(
                "SELECT * FROM " + RegistrationHandler.TABLE_NAME
                + " WHERE " + RegistrationHandler.FILE_ID + " = " + fmlID7);
        try {
            while (rs.next()) {
                counter++;
            }
        } catch (SQLException ex) {ex.printStackTrace();}
        if (counter > 0){Assert.fail("resultset had no records");}

        System.out.println("fmlID1 " + fmlID1);
        System.out.println("fmlID2 " + fmlID2);
        System.out.println("fmlID3 " + fmlID3);
        System.out.println("fmlID6 " + fmlID6);
        System.out.println("fmlID7 " + fmlID7);

        RegistrationHandler.deleteFMLFile(fmlID1);
        RegistrationHandler.deleteFMLFile(fmlID2);
        RegistrationHandler.deleteFMLFile(fmlID3);
        RegistrationHandler.deleteFMLFile(fmlID6);
        RegistrationHandler.deleteFMLFile(fmlID7);

    }


    @Test
    public void testTableExists(){
        FMFTable t = null;
        RegistrationHandler fml = null;

        RegisteredDataSource rds = createCorrectRDS();
        FlatFileDAL instance = new FlatFileDAL(rds);

        String path = TestLocationAndFiles.TEST_PATH+TestLocationAndFiles.FILE_SEPARATER;

        //check that the DAL has connected correctly
        assertEquals(true, instance.isConnected());

        //create a file Test1.csv with a corresponding information written into application database.
        util.createTestFile(path+"Test1.csv", ",", true, "");
        t = new FMFTable("Test1.csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        int fmlID1 = fml.writeFMLFile();
        //check that the files have been created for the test to run correctly
        assertTrue(new File(path+"Test1.csv").exists());
        //**Actual test, positive test this is what should happen if the table exists
        //both the file and information exist so the tableExists method should return true
        assertTrue(instance.tableExists(t));

        //create a file Test2.csv without the corresponding information in the application database
        util.createTestFile(path+"Test2.csv", ",", true, "");
        t = new FMFTable("Test2.csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        //check that the files have been created for the test to run correctly
        assertTrue(new File(path+"Test2.csv").exists());
        //**Actual test, negative test this is what should happen if the file exists but information does not (file not registered)
        //the file exists but the information does not so the tableExists method should return false
        assertFalse(instance.tableExists(t));

        //create a table Test3.csv without the corresponding file but with information in the application database
        t = new FMFTable("Test3.csv",null,null);
        rds.addTable(t);
        fml = util.createFMLHandler(t, ",", true, "", new Date());
        int fmlID3 = fml.writeFMLFile();
        //check that the file has not been created for the test to run correctly
        assertFalse(new File(path+"Test3.csv").exists());
        //**Actual test, negative test this is what should happen if the file does not exist but information does
        //the file does not exist but the information does so the tableExists method should return false
        assertFalse(instance.tableExists(t));

        RegistrationHandler.deleteFMLFile(fmlID1);
        RegistrationHandler.deleteFMLFile(fmlID3);
    }


    @Test
    public void testLoadData(){

        RegisteredDataSource rds = createCorrectRDS();
        util.createTestFile(rds.getFileName()+TestLocationAndFiles.FILE_SEPARATER+"test.csv", ",", true, "\"");
        FMFTable table = new FMFTable("test.csv",null,null);
        rds.addTable(table);
        RegistrationHandler rh = util.createFMLHandler(table, ",", true, "\"", new Date());
        int fileID = rh.writeFMLFile();

        FlatFileDAL ffdal = new FlatFileDAL(rds);
        ffdal.loadTable(table);

        assertEquals(10,table.getRowCount());

        RegistrationHandler.deleteFMLFile(fileID);
    }


    @Test
    /**
     * The malformed data (null fields at the end) are automatically filled in with default values.
     */
    public void testLoadBadData(){

        System.out.println("");
        System.out.println("testLoadBadData");
        
        boolean exceptionThrown = false;

        RegisteredDataSource rds = createCorrectRDS();
        util.createBadTestFile(rds.getFileName()+TestLocationAndFiles.FILE_SEPARATER+"badTest.csv", ",", true, "\"");
        FMFTable table = new FMFTable("badTest.csv",null,null);
        rds.addTable(table);
        RegistrationHandler rh = util.createFMLHandler(table, ",", true, "\"", new Date());
        int fileID = rh.writeFMLFile();

        FlatFileDAL ffdal = new FlatFileDAL(rds);

        try{
            ffdal.loadTable(table);
        }catch (Exception e){
            e.printStackTrace();
            exceptionThrown = true;
        }

        table.moveBeforeFirst();
        System.out.println(table.getFieldName(0) + " | " + table.getFieldName(1) + " | " + table.getFieldName(2));
        while(table.hasMoreRows()){
            table.moveToNextRow();
            System.out.println(table.getStringValue(0) + " | " + table.getStringValue(1) + " | " + table.getStringValue(2));
        }

        assertFalse(exceptionThrown);

        RegistrationHandler.deleteFMLFile(fileID);
    }

    @Test
    public void testLoadIncorrectDataTypes(){

        System.out.println("");
        System.out.println("testLoadIncorrectDataTypes");

        boolean exceptionThrown = false;

        RegisteredDataSource rds = createCorrectRDS();
        util.createIncorrectTestFileTypes(rds.getFileName()+TestLocationAndFiles.FILE_SEPARATER+"incorrectTypesTest.csv", ",", true, "\"");
        FMFTable table = new FMFTable("incorrectTypesTest.csv",null,null);
        rds.addTable(table);
        RegistrationHandler rh = util.createFMLHandler(table, ",", true, "\"", new Date());
        int fileID = rh.writeFMLFile();

        FlatFileDAL ffdal = new FlatFileDAL(rds);

        try{
            ffdal.loadTable(table);

            table.moveBeforeFirst();
            System.out.println(table.getFieldName(0) + " | " + table.getFieldName(1) + " | " + table.getFieldName(2));
            while(table.hasMoreRows()){
                table.moveToNextRow();
                System.out.println(table.getStringValue(0) + " | " + table.getStringValue(1) + " | " + table.getStringValue(2));
            }

        }catch (Exception e){

            exceptionThrown = true;

        }

        assertTrue(exceptionThrown);

        RegistrationHandler.deleteFMLFile(fileID);
    }

    @Test
    public void testGetUniqueValues(){

        System.out.println("");
        System.out.println("testGetUniqueValues");

        boolean exceptionThrown = false;

        RegisteredDataSource rds = createCorrectRDS();
        util.createDuplicateValuesInFile(rds.getFileName()+TestLocationAndFiles.FILE_SEPARATER+"duplicatesInFields.csv", ",", true, "\"");
        FMFTable table = new FMFTable("duplicatesInFields.csv",null,null);
        rds.addTable(table);
        RegistrationHandler rh = util.createFMLHandler(table, ",", true, "\"", new Date());
        int fileID = rh.writeFMLFile();

        FlatFileDAL ffdal = new FlatFileDAL(rds);

        try{
            ArrayList al = ffdal.getUniqueValues("DoubleField",table);
            System.out.println("table rows = " + table.getRowCount());
            System.out.println("unique values = " + al.size());
            
            assertEquals(6,al.size());

        }catch (Exception e){

            exceptionThrown = true;

        }

        assertFalse(exceptionThrown);

        RegistrationHandler.deleteFMLFile(fileID);
    }

    @Test
    public void testTableFilter(){
        System.out.println("");
        System.out.println("testTableFilter");

        RegisteredDataSource rds = createCorrectRDS();
        util.createDuplicateValuesInFile(rds.getFileName()+TestLocationAndFiles.FILE_SEPARATER+"duplicatesInFields.csv", ",", true, "\"");
        FMFTable table = new FMFTable("duplicatesInFields",null,null);
        table.setAi(ApplicationInformation.getCurrent());
        rds.addTable(table);
        RegistrationHandler rh = util.createFMLHandler(table, ",", true, "\"", new Date());
        int fileID = rh.writeFMLFile();

        FlatFileDAL ffdal = new FlatFileDAL(rds);

        ffdal.loadTable(table);
        assertEquals(9,table.getRowCount());

        System.out.println("   table has "+table.getRowCount()+" rows");

        table.clear();

        table.setFilter("StringField", "STRING3");
        assertEquals(3,table.getRowCount());
        table.moveBeforeFirst();
        System.out.println(table.getFieldName(0)+" | "+table.getFieldName(1)+" | "+table.getFieldName(2));
        while(table.hasMoreRows()){
            table.moveToNextRow();
            System.out.println(table.getStringValue(0)+" | "+table.getStringValue(1)+" | "+table.getStringValue(2));
        }

        System.out.println("");
        
        table.setFilter("IntegerField", "9");
        assertEquals(2,table.getRowCount());
        table.moveBeforeFirst();
        System.out.println(table.getFieldName(0)+" | "+table.getFieldName(1)+" | "+table.getFieldName(2));
        while(table.hasMoreRows()){
            table.moveToNextRow();
            System.out.println(table.getStringValue(0)+" | "+table.getStringValue(1)+" | "+table.getStringValue(2));
        }

        System.out.println("");

        table.clearFilter(true);
        assertEquals(9,table.getRowCount());
        table.moveBeforeFirst();
        System.out.println(table.getFieldName(0)+" | "+table.getFieldName(1)+" | "+table.getFieldName(2));
        while(table.hasMoreRows()){
            table.moveToNextRow();
            System.out.println(table.getStringValue(0)+" | "+table.getStringValue(1)+" | "+table.getStringValue(2));
        }


        RegistrationHandler.deleteFMLFile(fileID);
    }




    private RegisteredDataSource createCorrectRDS(){
        return createRDS(TestLocationAndFiles.TEST_PATH);
    }

    private RegisteredDataSource createIncorrectRDS(){
        return createRDS(TestLocationAndFiles.TEST_PATH_INCORRECT);
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