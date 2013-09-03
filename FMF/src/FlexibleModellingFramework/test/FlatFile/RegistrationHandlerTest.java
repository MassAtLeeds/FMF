package FlatFile;

import java.util.ArrayList;
import java.util.Date;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.data_management.DataAccessFactory;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.SystemProperties;

/**
 *
 * @author geo8kh
 */
public class RegistrationHandlerTest {

    private TestLocationAndFiles util = new TestLocationAndFiles();

    Date D = new Date();
    private int scrID;
    private RegisteredDataSource rds = null;

    public RegistrationHandlerTest() {
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
        FlatFileVerifyDataSource instance = new FlatFileVerifyDataSource(ApplicationInformation.getCurrent(),scrID);
        rds = instance.createRDS(scrID);
    }

    @After
    public void tearDown() {
        util.removeTestLocation();

        //remove the configuration from the application database
        if(ApplicationDatabase.getCurrent().datasourceExists(TestLocationAndFiles.TEST_PATH)){
            rds = null;
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

    private void createDataSourceInApplicationDatabase(){

        ApplicationDatabase db = ApplicationDatabase.getCurrent();
        if(db.datasourceExists(TestLocationAndFiles.TEST_PATH)){db.dropDatasource(TestLocationAndFiles.TEST_PATH);}

            scrID = db.getNextDatasourceID();
            System.out.println("Source ID = "+Integer.toString(scrID));

            db.setDatasourceProperty(scrID, SystemProperties.RDS_UNIQUE_NAME, "Test rds Config");
            db.setDatasourceProperty(scrID, SystemProperties.DB_NAME, TestLocationAndFiles.TEST_PATH);
            db.setDatasourceProperty(scrID, SystemProperties.DB_TYPE, Integer.toString(DataAccessFactory.FLAT_FILE));

    }


    private RegistrationHandler setupNewHandler(){
        return setupNewHandler("TestFlatFile.csv");
    }

    private RegistrationHandler setupNewHandler(String tableName){
        FMFTable t = new FMFTable(tableName,null,null);
        if(rds == null){System.out.println("RegisteredDataSource not created!");}
        rds.addTable(t);
        RegistrationHandler fml = new RegistrationHandler(t);

        fml.addField("field 1", FMFTable.FIELD_TYPE_DOUBLE);
        fml.addField("field 2", FMFTable.FIELD_TYPE_INT);
        fml.addField("field 3", FMFTable.FIELD_TYPE_STRING);

        fml.setDelimiter("|");
        fml.setFileDate(D);
        fml.setHeadersInFirstRow(true);
        fml.setRegistered(false);
        fml.setVerified(true);

        return fml;
    }

    
    @Test
    public void testDelimitor(){
        RegistrationHandler fml = setupNewHandler();
        Assert.assertEquals("|", fml.getDelimiter());        
    }
    
    @Test
    public void testHeadersInFirstRow(){
        RegistrationHandler fml = setupNewHandler();
        Assert.assertEquals(true, fml.isHeadersInFirstRow());        
    }
    
    @Test
    public void testIsRegistered(){
        RegistrationHandler fml = setupNewHandler();
        Assert.assertEquals(false, fml.isRegistered());
    }
    
    @Test
    public void testIsVerified(){
        RegistrationHandler fml = setupNewHandler();
        Assert.assertEquals(true, fml.isVerified());
    }

    @Test
    public void testDelimiterInFieldName(){
        RegistrationHandler fml = setupNewHandler();
        //test delimiter in field name
        try{
            fml.addField("new|field", FMFTable.FIELD_TYPE_DOUBLE);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertEquals("Field name cannot contain the deliminator character |. ", e.getMessage());
        }
    }

    @Test
    public void testBlankFieldName(){
        RegistrationHandler fml = setupNewHandler();
        //test blank field name
        try{
            fml.addField("", FMFTable.FIELD_TYPE_DOUBLE);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertEquals("Field name cannot be blank. ", e.getMessage());
        }
    }

    @Test
    public void testUndefinedField(){
        RegistrationHandler fml = setupNewHandler();
        //test undefined field type
        try{
            fml.addField("Field1", FMFTable.FIELD_TYPE_UNDEFINED);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertEquals("Field type is not a valid FMFTable field type. ", e.getMessage());
        }
    }

    @Test
    public void testInvalidFieldType(){
        RegistrationHandler fml = setupNewHandler();
        //test field type invalid
        try{
            fml.addField("Field1", 100);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertEquals("Field type is not a valid FMFTable field type. ", e.getMessage());
        }
    }


    @Test
    public void testMultipleFieldDefinitionErrors(){
        RegistrationHandler fml = setupNewHandler();
        //try multiple errors
        try{
            fml.addField("", 100);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertTrue(true);
            System.out.println("Testing multiple errors in configuration");
            System.out.println("passed with generated message: ");
            System.out.println(e.getMessage());
        }
    }



    @Test
    public void testAddField(){
        RegistrationHandler fml = setupNewHandler();
        //first clear the fields
        fml.clearFields();
        //Add valid fields and make sure they return correctly
        try{
            fml.addField("String Field", FMFTable.FIELD_TYPE_STRING);
            fml.addField("Integer Field", FMFTable.FIELD_TYPE_INT);
            fml.addField("Double Field", FMFTable.FIELD_TYPE_DOUBLE);
        }catch(FlatFileConfigurationException e){
            Assert.fail();
        }
        
        Assert.assertEquals("String Field", fml.getFieldNames(0));
        Assert.assertEquals("Integer Field", fml.getFieldNames(1));
        Assert.assertEquals("Double Field", fml.getFieldNames(2));
        
        Assert.assertEquals(FMFTable.FIELD_TYPE_STRING, fml.getFieldTypes(0));
        Assert.assertEquals(FMFTable.FIELD_TYPE_INT, fml.getFieldTypes(1));
        Assert.assertEquals(FMFTable.FIELD_TYPE_DOUBLE, fml.getFieldTypes(2));
    }

    @Test
    public void testClearFields(){
        RegistrationHandler fml = setupNewHandler();
        Assert.assertEquals(3, fml.getFieldNames().size());
        fml.clearFields();
        Assert.assertEquals(0, fml.getFieldNames().size());
    }

    @Test
    public void testRemoveField(){
        RegistrationHandler fml = setupNewHandler();
        //check that all fields are present to begin with
        Assert.assertEquals(3, fml.getFieldNames().size());
        //remove middle field
        fml.removeField("field 2");
        //check that a field has been removed
        Assert.assertEquals(2, fml.getFieldNames().size());
        //check and make sure correct fields are left in the correct order
        Assert.assertEquals("field 1", fml.getFieldNames(0));
        Assert.assertEquals("field 3", fml.getFieldNames(1));
        //check that the remaining field types are correct
        Assert.assertEquals(FMFTable.FIELD_TYPE_STRING, fml.getFieldTypes(1));
        Assert.assertEquals(FMFTable.FIELD_TYPE_DOUBLE, fml.getFieldTypes(0));
    }

    @Test
    public void testSetFieldsArrayListsDifferentSizes(){
        RegistrationHandler fml = setupNewHandler();
        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<Integer> fieldTypes = new ArrayList<Integer>();

        fieldNames.add("field1");
        fieldNames.add("field2");
        fieldTypes.add(FMFTable.FIELD_TYPE_DOUBLE);
        try{
            fml.setFields(fieldNames, fieldTypes);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertEquals("Parameters fieldNames and fieldTypes must have equivalent size() values. ", e.getMessage());
        }
    }

    @Test
    public void testSetFieldsFieldArrayEmpty(){
        RegistrationHandler fml = setupNewHandler();
        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<Integer> fieldTypes = new ArrayList<Integer>();
        
        fieldTypes.add(FMFTable.FIELD_TYPE_DOUBLE);
        try{
            fml.setFields(fieldNames, fieldTypes);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertEquals("Parameter fieldNames not allowed to be empty. ", e.getMessage());
        }
    }
    
    @Test
    public void testSetFieldsTypesArrayEmpty(){
        RegistrationHandler fml = setupNewHandler();
        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<Integer> fieldTypes = new ArrayList<Integer>();
        
        fieldNames.add("field1");
        fieldNames.add("field2");        
        try{
            fml.setFields(fieldNames, fieldTypes);
            Assert.fail();
        }catch(FlatFileConfigurationException e){
            Assert.assertEquals("Parameter fieldTypes not allowed to be empty. ", e.getMessage());
        }
    }
    
    @Test
    public void testSetFields(){
        RegistrationHandler fml = setupNewHandler();
        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<Integer> fieldTypes = new ArrayList<Integer>();

        fml.clearFields();
        
        fieldNames.add("String Field");
        fieldTypes.add(FMFTable.FIELD_TYPE_STRING);
       
        fieldNames.add("Integer Field");
        fieldTypes.add(FMFTable.FIELD_TYPE_INT);
        
        fieldNames.add("Double Field");
        fieldTypes.add(FMFTable.FIELD_TYPE_DOUBLE);
        
        try{
            fml.setFields(fieldNames, fieldTypes);
        }catch(FlatFileConfigurationException e){
            Assert.fail();
        }
        Assert.assertEquals(fieldNames.get(0), fml.getFieldNames(0));
        Assert.assertEquals(fieldNames.get(1), fml.getFieldNames(1));
        Assert.assertEquals(fieldNames.get(2), fml.getFieldNames(2));
        
        Assert.assertEquals((int)fieldTypes.get(0), (int)fml.getFieldTypes(0));
        Assert.assertEquals((int)fieldTypes.get(1), (int)fml.getFieldTypes(1));
        Assert.assertEquals((int)fieldTypes.get(2), (int)fml.getFieldTypes(2));
    }

    /**
     * Test the setting and reading of the text qualifier
     */
    @Test
    public void testTextQualifier(){
        RegistrationHandler fml = setupNewHandler();
        Assert.assertNull(fml.getTextQualifier());
        fml.setTextQualifier("\"");
        Assert.assertEquals("\"", fml.getTextQualifier());
    }



    /**
     * Test of readXMLFileMetaData method, of class XMLHandler.
     */
    @Test
    public void testReadWriteXMLFile() {
        System.out.println("Testing ReadWriteXMLFile");
        RegistrationHandler fml = this.setupNewHandler();
        int fileID = fml.writeFMLFile();
        System.out.println("The file ID for the flat file is " + fileID);

        RegistrationHandler newfml = new RegistrationHandler(rds.getTable("TestFlatFile.csv"));
        newfml.readFMLFile();

        System.out.println(fml.getFileID() + " - " + newfml.getFileID());
        System.out.println(fml.getRDSID() + " - " + newfml.getRDSID());
        
        Assert.assertEquals(fml.getDelimiter(), newfml.getDelimiter());
        Assert.assertEquals(fml.isHeadersInFirstRow(), newfml.isHeadersInFirstRow());
        Assert.assertEquals(fml.getFileDate().toString(), newfml.getFileDate().toString());
        Assert.assertEquals(fml.getFileName(), newfml.getFileName());
        Assert.assertEquals(fml.getTextQualifier(), newfml.getTextQualifier());
        Assert.assertEquals(fml.getFieldNames(0), newfml.getFieldNames(0));
        Assert.assertEquals(fml.getFieldTypes(0), newfml.getFieldTypes(0));
        Assert.assertEquals(fml.getFieldNames(1), newfml.getFieldNames(1));
        Assert.assertEquals(fml.getFieldTypes(1), newfml.getFieldTypes(1));
        Assert.assertEquals(fml.getFieldNames(2), newfml.getFieldNames(2));
        Assert.assertEquals(fml.getFieldTypes(2), newfml.getFieldTypes(2));
        Assert.assertEquals("TestFlatFile.csv", newfml.getFileName());

        RegistrationHandler readFMLFromFile = new RegistrationHandler(rds.getRdsID(),fileID);
        readFMLFromFile.readFMLFile();

        Assert.assertEquals(fml.getDelimiter(), readFMLFromFile.getDelimiter());
        Assert.assertEquals(fml.isHeadersInFirstRow(), readFMLFromFile.isHeadersInFirstRow());
        Assert.assertEquals(fml.getFileDate().toString(), readFMLFromFile.getFileDate().toString());
        Assert.assertEquals(fml.getFileName(), readFMLFromFile.getFileName());
        Assert.assertEquals(fml.getTextQualifier(), readFMLFromFile.getTextQualifier());
        Assert.assertEquals(fml.getFieldNames(0), readFMLFromFile.getFieldNames(0));
        Assert.assertEquals(fml.getFieldTypes(0), readFMLFromFile.getFieldTypes(0));
        Assert.assertEquals(fml.getFieldNames(1), readFMLFromFile.getFieldNames(1));
        Assert.assertEquals(fml.getFieldTypes(1), readFMLFromFile.getFieldTypes(1));
        Assert.assertEquals(fml.getFieldNames(2), readFMLFromFile.getFieldNames(2));
        Assert.assertEquals(fml.getFieldTypes(2), readFMLFromFile.getFieldTypes(2));
        Assert.assertEquals("TestFlatFile.csv", readFMLFromFile.getFileName());
    }


    @Test
    public void testFileIDIncrements(){
        RegistrationHandler fml = setupNewHandler();
        RegistrationHandler differentfml = setupNewHandler("differentTable.csv");

        System.out.println(fml.getFileID());
        fml.writeFMLFile();
        System.out.println(fml.getFileID());

        System.out.println(differentfml.getFileID());
        differentfml.writeFMLFile();
        System.out.println(differentfml.getFileID());

        Assert.assertNotSame(fml.getFileID(), differentfml.getFileID());
    }

    /**
     * Test the registration of the file
     */
     @Test
     public void testRegistrationFileExists(){
        RegistrationHandler fml = this.setupNewHandler();
        fml.writeFMLFile();
        RegistrationHandler newfml = new RegistrationHandler(rds.getTable("TestFlatFile.csv"));
        newfml.readFMLFile();

        Assert.assertTrue(newfml.isRegistered());
     }

}