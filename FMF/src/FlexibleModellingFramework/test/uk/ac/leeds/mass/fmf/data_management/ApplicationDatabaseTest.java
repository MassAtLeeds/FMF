package uk.ac.leeds.mass.fmf.data_management;

import FlatFile.RegistrationHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author geo8kh
 */
public class ApplicationDatabaseTest {

    ApplicationDatabase ad = ApplicationDatabase.getCurrent();

    public ApplicationDatabaseTest() {
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

//    /**
//     * Test of getCurrent method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetCurrent() {
//    }
//
//    /**
//     * Test of getAllTables method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetAllTables() {
//    }

    /**
     * Test of tableExists method, of class ApplicationDatabase.
     */
    @Test
    public void testTableExists() {
        assertTrue(ad.tableExists(RegistrationHandler.TABLE_NAME.toUpperCase()));
    }

//    /**
//     * Test of ExecuteStatment method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testExecuteStatment() {
//    }
//
//    /**
//     * Test of setSystemProperty method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testSetSystemProperty() {
//    }
//
//    /**
//     * Test of getSystemProperty method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetSystemProperty() {
//    }
//
//    /**
//     * Test of datasourceExists method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testDatasourceExists() {
//    }
//
//    /**
//     * Test of dropDatasource method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testDropDatasource_String() {
//    }
//
//    /**
//     * Test of dropDatasource method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testDropDatasource_int() {
//    }
//
//    /**
//     * Test of getNextDatasourceID method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetNextDatasourceID() {
//    }
//
//    /**
//     * Test of setDatasourceProperty method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testSetDatasourceProperty() {
//    }
//
//    /**
//     * Test of getDatasourceProperty method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetDatasourceProperty() {
//    }
//
//    /**
//     * Test of getAllDataSourceIDs method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetAllDataSourceIDs() {
//    }
//
//    /**
//     * Test of setModelProperty method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testSetModelProperty() {
//    }
//
//    /**
//     * Test of getModelProperty method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetModelProperty() {
//    }
//
//    /**
//     * Test of getAllModelProperty method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetAllModelProperty() {
//    }
//
//    /**
//     * Test of getAllModelNames method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testGetAllModelNames() {
//    }
//
//    /**
//     * Test of deleteModel method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testDeleteModel() {
//    }
//
//    /**
//     * Test of modelExists method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testModelExists() {
//    }
//
//    /**
//     * Test of finalize method, of class ApplicationDatabase.
//     */
//    @Test
//    public void testFinalize() {
//    }

}