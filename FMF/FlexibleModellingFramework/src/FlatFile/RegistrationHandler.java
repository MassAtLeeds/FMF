
package FlatFile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import uk.ac.leeds.mass.fmf.data_management.ApplicationDatabase;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author Kirk Harland
 * @Date 9/10/2012
 */
public class RegistrationHandler {

    final static String VERSION = "FML Version";
    final static String DELIMITER = "Delimiter";
    final static String HEADERS_IN_FIRST_ROW = "Headers";
    final static String FILE_DATE_TIME = "Date";
    final static String TEXT_QUALIFIER = "TextQualifier";
    final static String FILE_NAME = "FileName";
    final static String FIELD_NAME = "FieldName";
    final static String FIELD_TYPE = "FieldType";

    public final static String TABLE_NAME = "FILE_REGISTRATION";
    final static String FILE_ID = "FILE_ID";
    final static String DATA_SOURCE_ID = "SRC_ID";
    final static String PROPERTY = "PROPERTY";
    final static String VALUE = "VALUE";

    private Date fileDate;
    private String fileName = "";
    private String delimiter = "";
    private boolean headersInFirstRow;
    private String textQualifier = null;
    private ArrayList<String> fieldNames = new ArrayList<String>();
    private ArrayList<Integer>fieldTypes = new ArrayList<Integer>();

    private boolean registered = false;
    private boolean verified = false;

    private SimpleDateFormat simple = new SimpleDateFormat();
    private double FMLVersionID = 1.0;
    private int fileID = -1;
    private int rdsID = -1;

    private static ApplicationDatabase ad = ApplicationDatabase.getCurrent();

    /**
     * Declared as private to avoid accidental access and instgatiation of an incorrect object.
     */
    private RegistrationHandler(){}

    /**
     * This constructor takes in a FMFTable and reads the name from the table.
     * The fileName for this Handler is set to the same name with .fml appended to it.
     *
     * @param table
     */

    RegistrationHandler(int rdsID, String fileName){
        this(rdsID, getFileIDFromName(fileName, rdsID));
        this.fileName = fileName;
    }

    RegistrationHandler(FMFTable table){
        this(table.getRDS().getRdsID(),getFileIDFromName(table.getName(),table.getRDS().getRdsID()));
        fileName = table.getName();
    }

    RegistrationHandler(int rdsID, int fileID){
        this.rdsID = rdsID;
        if ( fileID > -1 ) {this.fileID = fileID;} else {this.fileID = FlatFileManager.getCurrent().allocateNextFileID();}
        simple.applyPattern("yyyy.MM.dd'T'HH:mm:ss");
    }
    
    static void createTable(){
        if ( !ad.tableExists(TABLE_NAME.toUpperCase()) ){
            ad.executeStatment("CREATE TABLE " + TABLE_NAME + " ( " + FILE_ID +
                    " SMALLINT, " + DATA_SOURCE_ID + " SMALLINT,  " + PROPERTY +
                    "  VARCHAR(50),  " + VALUE + "  VARCHAR(250))");
        }
    }

    /**
     * Uses the current file name and rdsID and searchs to see if the file name is already registered as a datasource.
     *
     *
     * @return If the file name is found returns the file id otherwise -1
     */
    static int getFileIDFromName(String file, int dataSourceID){
        int fID = -1;
        try {
            ResultSet rs = ApplicationDatabase.getCurrent().executeQuery("SELECT DISTINCT " +
                    RegistrationHandler.FILE_ID + ", " + RegistrationHandler.DATA_SOURCE_ID +
                    " FROM " + RegistrationHandler.TABLE_NAME + 
                    " WHERE " + RegistrationHandler.PROPERTY + " = '" + RegistrationHandler.FILE_NAME + "' " +
                    " AND " + RegistrationHandler.VALUE + " = '" + file + "'");
            if ( rs != null ){
                while(rs.next()){
                    if ( dataSourceID == rs.getInt(RegistrationHandler.DATA_SOURCE_ID) ){
                        fID = rs.getInt(RegistrationHandler.FILE_ID);
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }finally{
            return fID;
        }
    }

    void deleteFMLFile(){
        RegistrationHandler.deleteFMLFile(fileID);
    }

    static void deleteFMLFile(int fID){
        if (fID > -1){
            ApplicationDatabase.getCurrent().executeStatment("delete from " + RegistrationHandler.TABLE_NAME + " where " + RegistrationHandler.FILE_ID + " = " + fID);
        }
    }

    boolean readFMLFile(){

        createTable();

        try {
            ResultSet rs = ApplicationDatabase.getCurrent().executeQuery("SELECT " + RegistrationHandler.PROPERTY + ", " + RegistrationHandler.VALUE +
                    " FROM " + RegistrationHandler.TABLE_NAME +
                    " WHERE " + RegistrationHandler.FILE_ID + " = " + fileID +
                    " AND " + RegistrationHandler.DATA_SOURCE_ID + " = " + rdsID);
            if ( rs != null ){
                ResultSet fields = ApplicationDatabase.getCurrent().executeQuery("SELECT *" +
                       " FROM " + RegistrationHandler.TABLE_NAME +
                       " WHERE " + RegistrationHandler.FILE_ID + " = " + fileID +
                       " AND SUBSTR("+RegistrationHandler.PROPERTY+",1,"+RegistrationHandler.FIELD_NAME.length()+") = '" +
                       RegistrationHandler.FIELD_NAME + "'");
                if ( fields !=null ){
                    while(fields.next()){
                        fieldNames.add(null);
                        fieldTypes.add(null);
                    }
                }

                while(rs.next()){
                    setupAttributes(rs.getString(RegistrationHandler.PROPERTY), rs.getString(RegistrationHandler.VALUE));
                }
                this.setRegistered(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return this.isRegistered();

    }

    private void setupAttributes(String Key, String value){
        
        //check the version ID
        if (Key.equalsIgnoreCase(RegistrationHandler.VERSION)){
            //do something with the version...
            //to implement
            
        //setup the file name
        }else if (Key.equalsIgnoreCase(RegistrationHandler.FILE_NAME)){
            fileName = value;

        //setup the delimiter
        }else if (Key.equalsIgnoreCase(RegistrationHandler.DELIMITER)){
            if (value.equals("NULL")){
                this.setDelimiter(null);
            }else{
                this.setDelimiter(value);
            }
            
        //setup the Headers
        }else if (Key.equalsIgnoreCase(RegistrationHandler.HEADERS_IN_FIRST_ROW)){
            this.setHeadersInFirstRow(Boolean.parseBoolean(value));
        
        //setup file date
        }else if(Key.equalsIgnoreCase(RegistrationHandler.FILE_DATE_TIME)){
            if (value.equals("NULL")){
                this.setFileDate(null);
            }else{
                try {
                    Date dt = simple.parse(value);
                    this.setFileDate(dt);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
            
        //setup text qualifier  
        }else if(Key.equalsIgnoreCase(RegistrationHandler.TEXT_QUALIFIER)){
            if(value.equals("NULL")){
                this.setTextQualifier(null);
            }else{
                this.setTextQualifier(value);
            }
   
        //setup fields
        }else if (Key.substring(0,RegistrationHandler.FIELD_NAME.length()).equalsIgnoreCase(RegistrationHandler.FIELD_NAME)){
            int location = Integer.parseInt(Key.substring(RegistrationHandler.FIELD_NAME.length()));
            try{ fieldNames.set(location, value); }catch(Exception e){e.printStackTrace();    System.out.println(fileID);}
        }else if (Key.substring(0,RegistrationHandler.FIELD_TYPE.length()).equalsIgnoreCase(RegistrationHandler.FIELD_TYPE)){
            int location = Integer.parseInt(Key.substring(RegistrationHandler.FIELD_TYPE.length()));
            try{ fieldTypes.set(location,new Integer(value)); }catch(Exception e){e.printStackTrace();    System.out.println(fileID);}
        }

    }


    int writeFMLFile(){

        createTable();

        if ( fileID == -1 ){
            fileID = FlatFileManager.getCurrent().allocateNextFileID();
        }else{
            deleteFMLFile();
        }

        ad.executeStatment("INSERT INTO " + TABLE_NAME +
            " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
            ") VALUES ("+fileID+", " + rdsID +
            ", '" + RegistrationHandler.VERSION + "', '" + FMLVersionID + "' )");

        ad.executeStatment("INSERT INTO " + TABLE_NAME +
            " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
            ") VALUES ("+fileID+", " + rdsID +
            ", '" + RegistrationHandler.FILE_NAME + "', '" + fileName + "' )");

        //Write out the delimiter
        ad.executeStatment("INSERT INTO " + TABLE_NAME +
            " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
            ") VALUES ("+fileID+", " + rdsID +
            ", '" + RegistrationHandler.DELIMITER + "', '" + this.getDelimiter() + "' )");

        //write out the headers in first row information
        String header = Boolean.toString(this.isHeadersInFirstRow());
        ad.executeStatment("INSERT INTO " + TABLE_NAME +
            " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
            ") VALUES ("+fileID+", " + rdsID +
            ", '" + RegistrationHandler.HEADERS_IN_FIRST_ROW + "', '" + header + "' )");

        //write out the Date information
        String date = "";
        if (this.getFileDate()==null){date = "NULL";}else{date = simple.format(this.getFileDate());}
        ad.executeStatment("INSERT INTO " + TABLE_NAME +
            " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
            ") VALUES ("+fileID+", " + rdsID +
            ", '" + RegistrationHandler.FILE_DATE_TIME + "', '" + date + "' )");

        //write out the Text Qualifier
        String textQ = "";
        if (this.getTextQualifier()==null){textQ = "NULL";}else{textQ = this.getTextQualifier();}
        ad.executeStatment("INSERT INTO " + TABLE_NAME +
            " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
            ") VALUES ("+fileID+", " + rdsID +
            ", '" + RegistrationHandler.TEXT_QUALIFIER + "', '" + textQ + "' )");


        //write out all of the fields
        if (!fieldNames.isEmpty()){
            for (int i = 0; i < fieldNames.size(); i++) {
                //write out the field name
                ad.executeStatment("INSERT INTO " + TABLE_NAME +
                    " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
                    ") VALUES ("+fileID+", " + rdsID +
                    ", '" + RegistrationHandler.FIELD_NAME + i + "', '" + fieldNames.get(i) + "' )");

                //write out the field type
                ad.executeStatment("INSERT INTO " + TABLE_NAME +
                    " ( " + FILE_ID + ", " + DATA_SOURCE_ID + ", " + PROPERTY + ", " + VALUE +
                    ") VALUES ("+fileID+", " + rdsID +
                    ", '" + RegistrationHandler.FIELD_TYPE + i +"', '" + fieldTypes.get(i) + "' )");
            }
        }

        return fileID;

    }


    /**
     * @return the delimiter
     */
    String getDelimiter() {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @return the headersInFirstRow
     */
    boolean isHeadersInFirstRow() {
        return headersInFirstRow;
    }

    /**
     * @param headersInFirstRow the headersInFirstRow to set
     */
    void setHeadersInFirstRow(boolean headersInFirstRow) {
        this.headersInFirstRow = headersInFirstRow;
    }

    /**
     * @return the fieldNames
     */
    ArrayList<String> getFieldNames() {
        return fieldNames;
    }

    /**
     * The size() of the two parameters must be the same.  Neither of the parameters can be empty.
     * The fieldNames parameter cannot contain field names with the set deliminator in or empty strings.  
     * The field types must all be valid field types as specified in the FMFTable class.
     * Calls the addField method to add each field in the parameters.
     *
     * @param fieldNames String ArrayList of the field names to add.
     * @param fieldTypes Integer ArrayList of the field types to add.
     */
    void setFields(ArrayList<String> fieldNames, ArrayList<Integer> fieldTypes) throws FlatFileConfigurationException {

        String msg = "";

        if ( fieldNames.isEmpty() || fieldTypes.isEmpty() || fieldNames.size() != fieldTypes.size() ){
            if ( fieldNames.isEmpty() ) {msg += "Parameter fieldNames not allowed to be empty. ";}
            if ( fieldTypes.isEmpty() ) {msg += "Parameter fieldTypes not allowed to be empty. ";}
            if ( msg.equals("") && fieldNames.size() != fieldTypes.size() ) {
                msg += "Parameters fieldNames and fieldTypes must have equivalent size() values. ";
            }
            throw new FlatFileConfigurationException(msg);
        }else{
            for (int i = 0; i < fieldNames.size(); i++) {
                this.addField(fieldNames.get(i), fieldTypes.get(i));
            }
        }
    }

    /**
     * @return the fieldTypes
     */
    ArrayList<Integer> getFieldTypes() {
        return fieldTypes;
    }


    /**
     * @return a fieldName at the specified index from the fieldNames ArrayList
     */
    String getFieldNames(int index) {
        if ( index > fieldNames.size() ) {
            System.out.println("only " + fieldNames.size() + " in file " + fileName);
            return "";
        }else{
            return fieldNames.get(index);
        }
    }

    /**
     * Adds the parameter field to the ArrayList of field names fieldNames.
     * Adds the parameter type to the ArrayList of field types fieldTypes.
     * The field parameter cannot contain the set deliminator or be an empty string.
     * The type parameter must be a valid field type as specified in the FMFTable class.
     *
     * @param field
     */
    void addField(String field, int type){
        String msg = "";
        String del = this.getDelimiter();

        //check the field type exists
        boolean validFieldType = false;
        for (int j = 1; j < FMFTable.FIELD_TYPE_COUNT; j++) {
            if(type == j){
                validFieldType = true;
                break;
            }
        }

        //check that the field name is valid
        boolean validFieldName = true;
        //it is not blank
        if ( field.equals("") ){
            msg += "Field name cannot be blank. ";
            validFieldName = false;
        //it doesn't contain the delimitor
        }else if ( !del.equals("") && field.contains(del) ){
            msg += "Field name cannot contain the deliminator character " + del + ". ";
            validFieldName = false;
        }

        //if the field type is valid add it other wise throw an error
        if ( validFieldType && validFieldName ){
            fieldNames.add(field);
            fieldTypes.add(type);
        }else{
            if ( !validFieldType ){msg += "Field type is not a valid FMFTable field type. ";}
            throw new FlatFileConfigurationException(msg);
        }

    }


    /**
     * If the field is found it is removed along with its corresponding type.
     *
     * @param field String name of the field to be removed.
     */

    void removeField(String field){
        for (int i = 0; i < fieldNames.size(); i++) {
            if ( field.equalsIgnoreCase(fieldNames.get(i)) ){
                fieldNames.remove(i);
                fieldTypes.remove(i);
            }
        }
    }

    void clearFields(){
        fieldNames.clear();
        fieldTypes.clear();
    }

    /**
     * @return a fieldType at the specified index from the fieldTypes ArrayList
     */
    int getFieldTypes(int index) {
        if ( index > fieldTypes.size() ) {
            System.out.println("only " + fieldTypes.size() + " in file " + fileName);
            return FMFTable.FIELD_TYPE_UNDEFINED;
        }else{
            return new Integer(fieldTypes.get(index)).intValue();
        }
    }


    /**
     * @return the fileDate
     */
    Date getFileDate() {
        return fileDate;
    }

    /**
     * @param fileDate the fileDate to set
     */
    void setFileDate(Date fileDate) {
        this.fileDate = fileDate;
    }

    /**
     * @return the registered status of the underlying flat file
     */
    boolean isRegistered() {
        return registered;
    }

    /**
     * The status of being registered means that the file has been imported into the framework previously and
     * an fml file exists with information about the file in it.  If the information in the file does not
     * match the information in the associated fml file the file can still be registered but it is not valid.
     *
     * @param registered boolean relating to whether the associated file with the table has been registered or not.
     */
    void setRegistered(boolean registered) {
        this.registered = registered;
    }

    /**
     * @return a boolean indicating if the associated file with this fml file has been verisified or not.
     */
    boolean isVerified() {
        return verified;
    }

    /**
     * Verified indicates if the file associated with this fml file can be found and matches the
     * information within the fml file.  The fml file can be valid, the underlying file matches the information in this
     * object but it is not yet registered because the fml file to be created by this FMLHandler object has
     * not yet been saved.
     *
     * @param verified boolean relating to whether the required file has been verified as valid.
     */
    void setVerified(boolean verified) {
        this.verified = verified;
    }


    /**
     * @return the textQualifier.  returns the text qualifier or null if no qualifier is specified.
     */
    public String getTextQualifier() {
        return textQualifier;
    }

    /**
     * @param textQualifier the textQualifier to set
     */
    public void setTextQualifier(String textQualifier) {
        this.textQualifier = textQualifier;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public int getFileID(){return fileID;}
    public int getRDSID(){return rdsID;}

}
