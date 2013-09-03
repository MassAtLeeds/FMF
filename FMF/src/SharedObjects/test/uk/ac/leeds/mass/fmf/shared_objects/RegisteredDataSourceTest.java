/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author geo8kh
 */
public class RegisteredDataSourceTest {

    public RegisteredDataSourceTest() {
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
     * Test of setDSN method, of class RegisteredDataSource.
     */
    @Test
    public void testSetDSN() {
        System.out.println("");
        System.out.println("Testing setDSN");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getDSN method, of class RegisteredDataSource.
     */
    @Test
    public void testGetDSN() {
        System.out.println("");
        System.out.println("Testing getDSN");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of setFileName method, of class RegisteredDataSource.
     */
    @Test
    public void testSetFileName() {
        System.out.println("");
        System.out.println("Testing setFileName");

        String fileSep = System.getProperty("file.separator");
        RegisteredDataSource rds = new RegisteredDataSource();


        //set up the file name to be a file two folders down
        String filepath = "C:"+fileSep+"test path"+fileSep+"second level";
        String filename = "file name.mdb";
        System.out.println("setting file name = "+filepath+fileSep+filename);
        rds.setFileName(filepath+fileSep+filename);
        Assert.assertEquals(filepath,rds.getPath());
        Assert.assertEquals(filename, rds.getAbreviatedName());

        //set up the file name to be one folder down
        filepath = "C:"+fileSep+"test path";
        filename = "file name.mdb";
        System.out.println("setting file name = "+filepath+fileSep+filename);
        rds.setFileName(filepath+fileSep+filename);
        Assert.assertEquals(filepath,rds.getPath());
        Assert.assertEquals(filename, rds.getAbreviatedName());

        //set up the file name to be on the root
        filepath = "C:";
        filename = "file name.mdb";
        System.out.println("setting file name = "+filepath+fileSep+filename);
        rds.setFileName(filepath+fileSep+filename);
        Assert.assertEquals(filepath,rds.getPath());
        Assert.assertEquals(filename, rds.getAbreviatedName());

        //set up file name so that it does not contain a file seperator
        filepath = "C:";
        filename = "file name.mdb";
        System.out.println("setting file name = "+filepath+filename);
        rds.setFileName(filepath+filename);
        Assert.assertEquals("",rds.getPath());
        Assert.assertEquals("", rds.getAbreviatedName());
        
    }

    /**
     * Test of addDataListener method, of class RegisteredDataSource.
     */
    @Test
    public void testAddDataListener() {
        System.out.println("");
        System.out.println("Testing addDataListener");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of removeDataListener method, of class RegisteredDataSource.
     */
    @Test
    public void testRemoveDataListener() {
        System.out.println("");
        System.out.println("Testing removeDataListener");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getAllDataListeners method, of class RegisteredDataSource.
     */
    @Test
    public void testGetAllDataListeners() {
        System.out.println("");
        System.out.println("Testing getAllDataListeners");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of clearAllDataListeners method, of class RegisteredDataSource.
     */
    @Test
    public void testClearAllDataListeners() {
        System.out.println("");
        System.out.println("Testing clearAllDataListeners");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getFileName method, of class RegisteredDataSource.
     */
    @Test
    public void testGetFileName() {
        System.out.println("");
        System.out.println("Testing getFileName");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of setPassword method, of class RegisteredDataSource.
     */
    @Test
    public void testSetPassword() {
        System.out.println("");
        System.out.println("Testing setPassword");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getPassword method, of class RegisteredDataSource.
     */
    @Test
    public void testGetPassword() {
        System.out.println("");
        System.out.println("Testing getPassword");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of setUserName method, of class RegisteredDataSource.
     */
    @Test
    public void testSetUserName() {
        System.out.println("");
        System.out.println("Testing setUserName");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getUserName method, of class RegisteredDataSource.
     */
    @Test
    public void testGetUserName() {
        System.out.println("");
        System.out.println("Testing getUserName");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getAbreviatedName method, of class RegisteredDataSource.
     */
    @Test
    public void testGetAbreviatedName() {
        System.out.println("");
        System.out.println("Testing getAbreviatedName");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getPath method, of class RegisteredDataSource.
     */
    @Test
    public void testGetPath() {
        System.out.println("");
        System.out.println("Testing getPath");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getRdsID method, of class RegisteredDataSource.
     */
    @Test
    public void testGetRdsID() {
        System.out.println("");
        System.out.println("Testing getRdsID");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of setRdsID method, of class RegisteredDataSource.
     */
    @Test
    public void testSetRdsID() {
        System.out.println("");
        System.out.println("Testing setRdsID");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of isValid method, of class RegisteredDataSource.
     */
    @Test
    public void testIsValid() {
        System.out.println("");
        System.out.println("Testing isValid");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of setValid method, of class RegisteredDataSource.
     */
    @Test
    public void testSetValid() {
        System.out.println("");
        System.out.println("Testing setValid");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getDataType method, of class RegisteredDataSource.
     */
    @Test
    public void testGetDataType() {
        System.out.println("");
        System.out.println("Testing getDataType");
        System.out.println("Not yet implemented");
    }


    /**
     * Test of setDataType method, of class RegisteredDataSource.
     */
    @Test
    public void testSetDataType() {
        System.out.println("");
        System.out.println("Testing setDataType");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of addTable method, of class RegisteredDataSource.
     */
    @Test
    public void testAddTable() {
        System.out.println("");
        System.out.println("Testing addTable");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getTable method, of class RegisteredDataSource.
     */
    @Test
    public void testGetTable() {
        System.out.println("");
        System.out.println("Testing getTable");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of getTables method, of class RegisteredDataSource.
     */
    @Test
    public void testGetTables() {
        System.out.println("");
        System.out.println("Testing getTables");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of tableExists method, of class RegisteredDataSource.
     */
    @Test
    public void testTableExists_FMFTable() {
        System.out.println("");
        System.out.println("Testing tableExists_FMFTable");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of tableExists method, of class RegisteredDataSource.
     */
    @Test
    public void testTableExists_String() {
        System.out.println("");
        System.out.println("Testing tableExists_String");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of dropTable method, of class RegisteredDataSource.
     */
    @Test
    public void testDropTable() {
        System.out.println("");
        System.out.println("Testing dropTable");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of clearCache method, of class RegisteredDataSource.
     */
    @Test
    public void testClearCache() {
        System.out.println("");
        System.out.println("Testing clearCache");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of tableChanged method, of class RegisteredDataSource.
     */
    @Test
    public void testTableChanged() {
        System.out.println("");
        System.out.println("Testing tableChanged");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of tableIsDirty method, of class RegisteredDataSource.
     */
    @Test
    public void testTableIsDirty() {
        System.out.println("");
        System.out.println("Testing tableIsDirty");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of loadFields method, of class RegisteredDataSource.
     */
    @Test
    public void testLoadFields() {
        System.out.println("");
        System.out.println("Testing loadFields");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of loadTable method, of class RegisteredDataSource.
     */
    @Test
    public void testLoadTable_3args_1() {
        System.out.println("");
        System.out.println("Testing loadTable_3args_1");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of loadTable method, of class RegisteredDataSource.
     */
    @Test
    public void testLoadTable_3args_2() {
        System.out.println("");
        System.out.println("Testing loadTable_3args_2");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of loadTableInBackground method, of class RegisteredDataSource.
     */
    @Test
    public void testLoadTableInBackground_3args_1() {
        System.out.println("");
        System.out.println("Testing loadTableInBackground_3args_1");
        System.out.println("Not yet implemented");
    }

    /**
     * Test of loadTableInBackground method, of class RegisteredDataSource.
     */
    @Test
    public void testLoadTableInBackground_3args_2() {
        System.out.println("");
        System.out.println("Testing loadTableInBackground_3args_2");
        System.out.println("Not yet implemented");
    }

}