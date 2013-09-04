
package FlatFile;


import au.com.bytecode.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.leeds.mass.fmf.data_management.DataAccessFactory;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import static org.junit.Assert.*;

/**
 *
 * @author geo8kh
 */
public class FlatFileWriterTest {

    private static TestLocationAndFiles util = new TestLocationAndFiles();
    private RegisteredDataSource rds = createRDS(TestLocationAndFiles.TEST_PATH);
    private int file_ID;

    public FlatFileWriterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        util.createTestLocation();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        File file = new File(TestLocationAndFiles.TEST_PATH);
        for (File f : file.listFiles()) {
            System.out.println("Removed file "+ f.getName() + " " +  f.delete() );
        }
        util.removeTestLocation();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        File file = new File(TestLocationAndFiles.TEST_PATH);
        for (File f : file.listFiles()) {
            System.out.println("Removed file "+ f.getName() + " " +  f.delete() );
        }
    }


    /**
     * Test the recognition of the end of line character
     */
    @Test
    public void testEndOfFileEqualsNewLine(){
        System.out.println("testEndOfFileEqualsNewLine");

        File file = null;
        FlatFileWriter ffw = null;
        BufferedReader fr = null;
        FileWriter fw = null;

        try{
            //Set up the table testing to make sure it is set up correctly
            FMFTable table = createTable("testEndOfFileEqualsNewLine.csv", false);
            ffw = new FlatFileWriter(table);
            file = new File(TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER + "testEndOfFileEqualsNewLine.csv");

            file.createNewFile();
            assertTrue(file.exists());
            fw = new FileWriter(file);
            fw.append(new String("foobar!"));
            fw.close();

            assertFalse(ffw.endOfFileEqualsNewLine(file));

            file.delete();
            assertFalse(file.exists());
            file.createNewFile();
            assertTrue(file.exists());
            fw = new FileWriter(file);
            fw.append(new String("foobar!"+CSVWriter.DEFAULT_LINE_END));
            fw.close();

            assertTrue(ffw.endOfFileEqualsNewLine(file));

            file.delete();
            assertFalse(file.exists());
            file.createNewFile();
            assertTrue(file.exists());
            fw = new FileWriter(file);
            fw.append(CSVWriter.DEFAULT_LINE_END);
            fw.close();

            assertTrue(ffw.endOfFileEqualsNewLine(file));

        }catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }finally{
            if (fr != null){
                try {fr.close();} catch (IOException ex) {}
            }
            if ( fw != null ){ try {fw.close();} catch (IOException ex) {} }
            RegistrationHandler.deleteFMLFile(file_ID);
        }

    }


    /**
     * Test of runProcess method, of class FlatFileWriter.
     */
    @Test
    public void testRunProcess_NewDataOnly() {
        System.out.println("");
        System.out.println("testRunProcess_NewDataOnly");

        File file = null;
        FlatFileWriter ffw = null;
        BufferedReader fr = null;

        try{
            //Set up the table testing to make sure it is set up correctly
            FMFTable table = createTable("NewDataOnly", false);
            ffw = new FlatFileWriter(table);
            ffw.runProcess(true);

            file = new File(TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER + "NewDataOnly.csv");
            assertTrue(file.exists());

            assertEquals(0, table.insertedRowCount());
            table.insertValue("StringField", new String("new one"));
            table.insertValue("IntegerField", new Integer(1));
            table.insertValue("DoubleField", new Double(1));
            table.insertRow();
            table.insertValue("StringField", new String("new two"));
            table.insertValue("IntegerField", new Integer(2));
            table.insertValue("DoubleField", new Double(2));
            table.insertRow();
            assertEquals(2, table.insertedRowCount());

            ffw = new FlatFileWriter(table);
            ffw.runProcess();

            file = new File(TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER + "NewDataOnly.csv");

            fr = new BufferedReader( new FileReader(file) );
            
            String nextLine;
            while( (nextLine = fr.readLine() ) != null){
                System.out.println(nextLine);
            }

            assertEquals(65,(int)file.length());

            assertFalse(ffw.isForceHeaders());
            assertFalse(ffw.isWriteMainTableBody());
            assertTrue(ffw.isWriteAppendedData());

            fr.close();


        }catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }finally{
            if (fr != null){
                try {fr.close();} catch (IOException ex) {}
            }
            RegistrationHandler.deleteFMLFile(file_ID);
        }
    }

    /**
     * Test of runProcess method, of class FlatFileWriter.
     */
    @Test
    public void testRunProcess_ExistingDataWithUpdate() {
        System.out.println("");
        System.out.println("testRunProcess_ExistingDataWithUpdate");

        File file = null;
        FlatFileWriter ffw = null;
        BufferedReader fr = null;

        try{
            //Set up the table testing to make sure it is set up correctly
            FMFTable table = createTable("ExistingDataWithUpdate", true);

            file = new File(TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER + "ExistingDataWithUpdate.csv");
            assertTrue(this.createFile(file));
            assertTrue(file.exists());

            System.out.println("Before Update");
            fr = new BufferedReader( new FileReader(file) );
            String nextLine;
            while( (nextLine = fr.readLine() ) != null){
                System.out.println(nextLine);
            }

            assertEquals(68,(int)file.length());
            fr.close();


            assertEquals(3,table.getRowCount());

            table.editField("StringField", "New Value", 2);
            table.editField("DoubleField", new Double(9.9), 3);
            table.editField("IntegerField", new Integer(8), 1);
            ffw = new FlatFileWriter(table);
            ffw.runProcess();

            System.out.println("After Update");
            fr = new BufferedReader( new FileReader(file) );
            nextLine="";
            while( (nextLine = fr.readLine() ) != null){
                System.out.println(nextLine);
            }

            assertEquals(3,table.getRowCount());
            
            assertFalse(ffw.isForceHeaders());
            assertTrue(ffw.isWriteMainTableBody());
            assertFalse(ffw.isWriteAppendedData());

            assertEquals(75,(int)file.length());
            fr.close();

        }catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }finally{
            if (fr != null){
                try {fr.close();} catch (IOException ex) {}
            }
            RegistrationHandler.deleteFMLFile(file_ID);
        }


    }

    /**
     * Test of runProcess method, of class FlatFileWriter.
     */
    @Test
    public void testRunProcess_NewDataAddedToExistingData() {
        System.out.println("");
        System.out.println("testRunProcess_NewDataAddedToExistingData");


        File file = null;
        FlatFileWriter ffw = null;
        BufferedReader fr = null;

        try{
            //Set up the table testing to make sure it is set up correctly
            FMFTable table = createTable("NewDataAddedToExistingData", true);

            file = new File(TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER + "NewDataAddedToExistingData.csv");
            assertTrue(this.createFile(file));
            assertTrue(file.exists());

            System.out.println("Before Update");
            fr = new BufferedReader( new FileReader(file) );
            String nextLine;
            while( (nextLine = fr.readLine() ) != null){
                System.out.println(nextLine);
            }

            assertEquals(68,(int)file.length());
            fr.close();


            assertEquals(3,table.getRowCount());

            assertEquals(0, table.insertedRowCount());
            table.insertValue("StringField", new String("new one"));
            table.insertValue("IntegerField", new Integer(10));
            table.insertValue("DoubleField", new Double(1.11));
            table.insertRow();
            table.insertValue("StringField", new String("new two"));
            table.insertValue("IntegerField", new Integer(20));
            table.insertValue("DoubleField", new Double(2.22));
            table.insertRow();
            assertEquals(2, table.insertedRowCount());

            ffw = new FlatFileWriter(table);
            ffw.runProcess();

            System.out.println("After Update");
            fr = new BufferedReader( new FileReader(file) );
            nextLine="";
            while( (nextLine = fr.readLine() ) != null){
                System.out.println(nextLine);
            }

            assertEquals(3,table.getRowCount());

            assertFalse(ffw.isForceHeaders());
            assertFalse(ffw.isWriteMainTableBody());
            assertTrue(ffw.isWriteAppendedData());

            assertEquals(100,(int)file.length());
            fr.close();

        }catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }finally{
            if (fr != null){
                try {fr.close();} catch (IOException ex) {}
            }
            RegistrationHandler.deleteFMLFile(file_ID);
        }
    }

    /**
     * Test of runProcess method, of class FlatFileWriter.
     */
    @Test
    public void testRunProcess_NewDataAddedToExistingDataWithUpdate() {
        System.out.println("");
        System.out.println("testRunProcess_NewDataAddedToExistingDataWithUpdate");

        File file = null;
        FlatFileWriter ffw = null;
        BufferedReader fr = null;

        try{
            //Set up the table testing to make sure it is set up correctly
            FMFTable table = createTable("NewDataAddedToExistingDataWithUpdate", true);

            file = new File(TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER + "NewDataAddedToExistingDataWithUpdate.csv");
            assertTrue(this.createFile(file));
            assertTrue(file.exists());

            System.out.println("Before Update");
            fr = new BufferedReader( new FileReader(file) );
            String nextLine;
            while( (nextLine = fr.readLine() ) != null){
                System.out.println(nextLine);
            }

            assertEquals(68,(int)file.length());
            fr.close();


            assertEquals(3,table.getRowCount());
            table.editField("StringField", "New Value", 2);
            table.editField("DoubleField", new Double(9.9), 3);
            table.editField("IntegerField", new Integer(8), 1);
            assertEquals(3,table.getRowCount());

            assertEquals(0, table.insertedRowCount());
            table.insertValue("StringField", new String("new one"));
            table.insertValue("IntegerField", new Integer(10));
            table.insertValue("DoubleField", new Double(1.11));
            table.insertRow();
            table.insertValue("StringField", new String("new two"));
            table.insertValue("IntegerField", new Integer(20));
            table.insertValue("DoubleField", new Double(2.22));
            table.insertRow();
            assertEquals(2, table.insertedRowCount());

            ffw = new FlatFileWriter(table);
            ffw.runProcess();

            System.out.println("After Update");
            fr = new BufferedReader( new FileReader(file) );
            nextLine="";
            while( (nextLine = fr.readLine() ) != null){
                System.out.println(nextLine);
            }

            assertEquals(3,table.getRowCount());

            assertFalse(ffw.isForceHeaders());
            assertTrue(ffw.isWriteMainTableBody());
            assertTrue(ffw.isWriteAppendedData());

            assertEquals(107,(int)file.length());
            fr.close();

        }catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }finally{
            if (fr != null){
                try {fr.close();} catch (IOException ex) {}
            }
            RegistrationHandler.deleteFMLFile(file_ID);
        }
    }

    /**
     * Test of writeHeaders method, of class FlatFileWriter.
     */
    @Test
    public void testWriteHeaders() {
        System.out.println("");
        System.out.println("testWriteHeaders");
        File file = null;
        FlatFileWriter ffw = null;
        try{
            FMFTable table = createTable("WriteHeaders", true);
            ffw = new FlatFileWriter(table);
            ffw.runProcess(true);

            file = new File(TestLocationAndFiles.TEST_PATH + TestLocationAndFiles.FILE_SEPARATER + "WriteHeaders.csv");
            assertTrue(file.exists());

            FileReader fr = new FileReader(file);
            assertEquals(37,(int)file.length());
            char[] c = new char[(int)file.length()];
            fr.read(c);
            String s = new String(c);
            System.out.println(s);
            fr.close();

        }catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown");
        }finally{
            if (ffw!=null){ffw.deleteFile();}
            if (file != null){
                assertFalse(file.exists());
            }else{
                fail("file null");
            }
            RegistrationHandler.deleteFMLFile(file_ID);
        }
    }

    
    private FMFTable createTable(String tableName, boolean withData) throws SQLException{
        FlatFileResultSet ffrs = new FlatFileResultSet(false);
        if ( withData ){
            ffrs.addField(new String[]{"one","two","three"}, "StringField");
            ffrs.addField(new int[]{1,2,3}, "IntegerField");
            ffrs.addField(new double[]{1.1,2.2,3.3}, "DoubleField");
        }else{
            ffrs.addField(new String[]{""}, "StringField");
            ffrs.addField(new int[]{0}, "IntegerField");
            ffrs.addField(new double[]{0}, "DoubleField");
        }
        FMFTable table = new FMFTable(tableName,ffrs,new FlatFileStatement());
        
        rds.addTable(table);
        table.setAi(ApplicationInformation.getCurrent());
        
        RegistrationHandler rh = new RegistrationHandler(table);
        rh.setHeadersInFirstRow(true);
        rh.setDelimiter(",");
        for (int i = 0; i < table.getFieldCount(); i++) {
            rh.addField(table.getFieldName(i), table.getColumnType(i));
        }
        file_ID = rh.writeFMLFile();
        
        return table;
    }

    private boolean createFile(File file){
        try{
            file.createNewFile();

            FileWriter fw = new FileWriter(file);
            String str = "StringField,IntegerField,DoubleField\none,1,1.1\ntwo,2,2.2\nthree,3,3.3";
            fw.write(str);
            fw.close();
            return true;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }

    private RegisteredDataSource createRDS(String fileName){
        RegisteredDataSource r = new RegisteredDataSource();
        r.setDataType(DataAccessFactory.FLAT_FILE);
        r.setDSN(false);
        r.setFileName(fileName);
        r.setPassword("");
        r.setRdsID(1);
        r.setUserName("");

        return r;
    }

}