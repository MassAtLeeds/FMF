package FlatFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author Kirk Harland
 */
public class TestLocationAndFiles {

    boolean removeFilePath = false;

    public final static String FILE_SEPARATER = System.getProperty("file.separator");

    public final static String TEST_PATH = "C:"+FILE_SEPARATER+"Work"+FILE_SEPARATER+"FlatFileTestArea";
    public final static String TEST_PATH_INCORRECT = "C:"+FILE_SEPARATER+"Work"+FILE_SEPARATER+"FlatFleTestArea";

    
    boolean createTestLocation(){
        File f = new File(TEST_PATH);
        if ( !f.exists() ){
            removeFilePath = true;
            if ( !f.mkdir() ) {
                System.out.println("Test location could not be created in setUp");
            }
        }
        return f.exists();
    }
    
    boolean removeTestLocation(){
        File f = new File(TEST_PATH);
        boolean deleted = true;
        if ( removeFilePath && f.exists() ){
            File[] fileList = f.listFiles();
            for (int i = 0; i < fileList.length; i++) {fileList[i].delete();}
            f.delete();
            deleted = !f.exists();
        }
        if(!deleted){System.out.println(TEST_PATH + " not deleted");}
        return deleted;
    }

    
    RegistrationHandler createFMLHandler (FMFTable t, String delimiter, boolean headers, String textQualifier, Date fileDate){
        RegistrationHandler fml = new RegistrationHandler(t);

        fml.setDelimiter(delimiter);
        fml.setHeadersInFirstRow(headers);
        fml.setTextQualifier(textQualifier);
        fml.setFileDate(fileDate);
        fml.addField("StringField", FMFTable.FIELD_TYPE_STRING);
        fml.addField("IntegerField",FMFTable.FIELD_TYPE_INT);
        fml.addField("DoubleField", FMFTable.FIELD_TYPE_DOUBLE);

        return fml;
    }
    
    /**
     * Creates a delimited test file with three fields and 10 rows of data.
     *
     * The example below would be created if headers = true and delimiter = ","
     * StringField,IntegerField,DoubleField
     * string1,1,1.0
     * string2,2,2.0
     * string3,3,3.0
     * string4,4,4.0
     * string5,5,5.0
     * string6,6,6.0
     * string7,7,7.0
     * string8,8,8.0
     * string9,9,9.0
     * string10,10,10.0
     * 
     * @param filename the full path and name of the file to be created
     * @param delimiter the delimiter character to be used in the file.  Generally a , for csv
     * @param headers boolean headers included if true not if false.
     * @param textQualifier is the qualifier for text.
     */
    void createTestFile(String filename, String delimiter, boolean headers, String textQualifier){
        boolean createFile = true;
        if (headers){
            writeToFile(filename,"StringField"+delimiter+"IntegerField"+delimiter+"DoubleField",createFile);
            createFile = false;
        }
        for (int i = 1; i < 11; i++) {
            writeToFile(filename,"string"+i+delimiter+i+delimiter+i+".0",createFile);
            createFile = false;
        }
    }


    /**
     * Creates a delimited test file with three fields and 10 rows of data in the first field
     * but only 9 in all of the other fields.
     *
     * The example below would be created if headers = true and delimiter = ","
     * StringField,IntegerField,DoubleField
     * string1,1,1.0
     * string2,2,2.0
     * string3,3,3.0
     * string4,4,4.0
     * string5,5,5.0
     * string6,6,6.0
     * string7,7,7.0
     * string8,8,8.0
     * string9,9,9.0
     * string10
     * 
     * @param filename the full path and name of the file to be created
     * @param delimiter the delimiter character to be used in the file.  Generally a , for csv
     * @param headers boolean headers included if true not if false.
     * @param textQualifier is the qualifier for text.
     */
    void createBadTestFile(String filename, String delimiter, boolean headers, String textQualifier){
        boolean createFile = true;
        if (headers){
            writeToFile(filename,"StringField"+delimiter+"IntegerField"+delimiter+"DoubleField",createFile);
            createFile = false;
        }
        for (int i = 1; i < 10; i++) {
            writeToFile(filename,"string"+i+delimiter+i+delimiter+i+".0",createFile);
            createFile = false;
        }
        writeToFile(filename,"string10",false);
    }

    /**
     * Creates a delimited test file with three fields and 10 rows of data.  Some of the field types are incorrect.
     *
     * The example below would be created if headers = true and delimiter = ","
     * StringField,IntegerField,DoubleField
     * string1,1,1.0
     * string2,2,2.0
     * string3,3,3.0
     * string4,4,4.0
     * 5,string5,5.0
     * 6.0,6,string6
     * string7,7,7.0
     * string8,8,8.0
     * string9,9,9.0
     * string10,10,10.0
     * 
     * @param filename the full path and name of the file to be created
     * @param delimiter the delimiter character to be used in the file.  Generally a , for csv
     * @param headers boolean headers included if true not if false.
     * @param textQualifier is the qualifier for text.
     */
    void createIncorrectTestFileTypes(String filename, String delimiter, boolean headers, String textQualifier){
        boolean createFile = true;
        if (headers){
            writeToFile(filename,"StringField"+delimiter+"IntegerField"+delimiter+"DoubleField",createFile);
            createFile = false;
        }
        
         writeToFile(filename,"string1,1,1.0",false);
         writeToFile(filename,"string2,2,2.0",false);
         writeToFile(filename,"string3,3,3.0",false);
         writeToFile(filename,"string4,4,4.0",false);
         writeToFile(filename,"5,string5,5.0",false);
         writeToFile(filename,"6.0,6,string6",false);
         writeToFile(filename,"string7,7,7.0",false);
         writeToFile(filename,"string8,8,8.0",false);
         writeToFile(filename,"string9,9,9.0",false);
         writeToFile(filename,"string10,10,10.0",false);

    }

    /**
     * Creates a delimited test file with three fields and 10 rows of data.  Some of the field types are incorrect.
     *
     * The example below would be created if headers = true and delimiter = ","
     * StringField,IntegerField,DoubleField
     * string1,1,1.0
     * string2,2,2.0
     * string3,3,3.0
     * string3,3,3.0
     * string3,3,3.0
     * string7,7,7.0
     * string8,8,8.0
     * string9,9,9.0
     * string9,9,9.0
     * 
     * @param filename the full path and name of the file to be created
     * @param delimiter the delimiter character to be used in the file.  Generally a , for csv
     * @param headers boolean headers included if true not if false.
     * @param textQualifier is the qualifier for text.
     */
    void createDuplicateValuesInFile(String filename, String delimiter, boolean headers, String textQualifier){
        boolean createFile = true;
        if (headers){
            writeToFile(filename,"StringField"+delimiter+"IntegerField"+delimiter+"DoubleField",createFile);
            createFile = false;
        }
        
         writeToFile(filename,"string1,1,1.0",false);
         writeToFile(filename,"string2,2,2.0",false);
         writeToFile(filename,"string3,3,3.0",false);
         writeToFile(filename,"string3,3,3.0",false);
         writeToFile(filename,"string3,3,3.0",false);
         writeToFile(filename,"string7,7,7.0",false);
         writeToFile(filename,"string8,8,8.0",false);
         writeToFile(filename,"string9,9,9.0",false);
         writeToFile(filename,"string9,9,9.0",false);

    }

    void writeToFile(String fileName, String lineToWrite, boolean clear){
        File f = new File(fileName);
        long fileLength;
        String nextLine = System.getProperty("line.separator");

        try{
            if(clear){f.delete();}

            //if the file exists get its length else set length to 0 and create the file.
            if (f.exists()){
                fileLength = f.length();
            }else{
                f.createNewFile();
                fileLength = 0;
            }

            //create a random access file object and move to the appropriate place for insertion of new log entry
            RandomAccessFile r = new RandomAccessFile(f,"rw");
            r.seek(fileLength);

            //write the line to the file
            r.writeBytes(lineToWrite);
            r.writeBytes(nextLine);

            //close the random access file
            r.close();

        }catch(IOException e){
            System.out.println("Problem writing to file: "+f.getAbsolutePath());
            e.printStackTrace();
        }finally{
            f = null;
        }
    }

}
