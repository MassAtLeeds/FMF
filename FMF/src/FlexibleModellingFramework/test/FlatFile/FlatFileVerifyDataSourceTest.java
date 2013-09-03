package FlatFile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.data_management.DataAccessFactory;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.SystemProperties;
import static org.junit.Assert.*;

/**
 *
 * @author geo8kh
 */
public class FlatFileVerifyDataSourceTest {

    private int scrID;
    private TestLocationAndFiles util = new TestLocationAndFiles();

    public FlatFileVerifyDataSourceTest() {
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
        createDataSourceInApplicationDatabase();
    }

    @After
    public void tearDown() {

        //tidy up and remove the file location from the file structure if it was created by the test
        util.removeTestLocation();

        //remove the configuration from the application database
        if(ApplicationDatabase.getCurrent().datasourceExists(TestLocationAndFiles.TEST_PATH)){
            System.out.println("rds Config exists for " + TestLocationAndFiles.TEST_PATH);
            System.out.println("Deleteing");
            ApplicationDatabase.getCurrent().dropDatasource(TestLocationAndFiles.TEST_PATH);

            if(ApplicationDatabase.getCurrent().datasourceExists(TestLocationAndFiles.TEST_PATH)){
                System.out.println("Deletion failed");
            }else{
                System.out.println("Delete completed successfully");
            }

        }

    }

    /**
     * Test of runProcess method, of class FlatFileVerifyDataSource.
     */
    @Test
    public void testRunProcess() {
        //Could do with a test here at some point.
    }

    /**
     * Test of createRDS method, of class FlatFileVerifyDataSource.
     */
    @Test
    public void testCreateRDSValidSource() {
        System.out.println("createRDS - Valid Source");

        FlatFileVerifyDataSource instance = new FlatFileVerifyDataSource(ApplicationInformation.getCurrent(),scrID);
        
        RegisteredDataSource result = instance.createRDS(scrID);
        assertNotNull(result);
        assertEquals(scrID,result.getRdsID());
        assertEquals(result.getFileName(),TestLocationAndFiles.TEST_PATH);
    }

    /**
     * Test of getSuccess method, of class FlatFileVerifyDataSource.
     */
    @Test
    public void testGetSuccessValidSource() {
        System.out.println("getSuccess - Valid Source");

        FlatFileVerifyDataSource instance = new FlatFileVerifyDataSource(ApplicationInformation.getCurrent(),scrID);

        instance.runProcess();
        RegisteredDataSource result = instance.getSuccess();
        assertTrue(result.isValid());

    }

    private void createDataSourceInApplicationDatabase(){

        ApplicationDatabase db = ApplicationDatabase.getCurrent();
        if(db.datasourceExists(TestLocationAndFiles.TEST_PATH)){db.dropDatasource(TestLocationAndFiles.TEST_PATH);}

            scrID = db.getNextDatasourceID();
            System.out.println("Source ID = "+Integer.toString(scrID));

            db.setDatasourceProperty(scrID, SystemProperties.RDS_UNIQUE_NAME, "Test rds Config");
            db.setDatasourceProperty(scrID, SystemProperties.DB_NAME, TestLocationAndFiles.TEST_PATH);
            db.setDatasourceProperty(scrID, SystemProperties.DB_TYPE, Integer.toString(DataAccessFactory.FLAT_FILE));

    }
}