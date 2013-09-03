
package FlatFile;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import uk.ac.leeds.mass.fmf.framework.ApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;

/**
 *
 * @author geo8kh
 */
public class FlatFileWriter extends FrameworkProcess {

    private FMFTable table;
    private RegistrationHandler rh;
    private String textQualifier = "";
    private CSVWriter writer;
    private String nextLine = "";//System.getProperty("line.separator");
    private FlatFileResultSet ffrs = null;

    private boolean forceHeaders = false;
    private boolean writeMainTableBody = false;
    private boolean writeAppendedData = false;

    private FlatFileWriter(){}

    public FlatFileWriter(FMFTable table){
        this.table = table;
        rh = new RegistrationHandler(this.table);
        rh.readFMLFile();

        
        if (this.table.getResultSet() == null){
            this.table.loadData(ApplicationInformation.getCurrent(), null);
        }
        ffrs = (FlatFileResultSet)this.table.getResultSet();

    }

    @Override
    public void runProcess() {
        runProcess(false);
    }

    void runProcess(boolean forceHeaders) {

        this.forceHeaders = forceHeaders;

        FileWriter fr = null;

        if (table != null){

            int steps = 0;

            try {

                File file;
                //check and see if any of the existing values have changed
                if (ffrs.rowUpdated()) {
                    steps += table.getRowCount();
                    writeMainTableBody = true;
                    file = createFile(true);
                }else{
                    file = createFile(false);
                }

                fr = new FileWriter(file,true);
                if ( rh.getTextQualifier() == null ){
                    writer = new CSVWriter(fr, rh.getDelimiter().charAt(0), CSVWriter.NO_QUOTE_CHARACTER);
                }else{
                    writer = new CSVWriter(fr,rh.getDelimiter().charAt(0), rh.getTextQualifier().charAt(0));
                    textQualifier = rh.getTextQualifier();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }

            //we need to add new rows to the existing data
            if (table.insertedRowCount() > 0){
                steps += table.insertedRowCount();
                writeAppendedData = true;
            }

            this.initilise(steps);

            //if the headers are in the first row write out the headers
            if ( rh.isHeadersInFirstRow() & (isWriteMainTableBody() | forceHeaders) ){

                writeHeaders();

            }

            if (isWriteMainTableBody()){
                writeMainValues();
            }

            if (isWriteAppendedData()){
                writeAppendedValues();
            }

            if (!cancelled){finished();}

            try {
                if (writer!=null){
                    writer.flush();
                    writer.close();
                }
                if (fr!=null){fr.close();}
                writer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    void writeHeaders(){
        if (rh.getFieldNames().size()>0){
            String[] headers = new String[rh.getFieldNames().size()];
            for (int i = 0; i < headers.length; i++) {
                headers[i] = textQualifier + rh.getFieldNames(i) + textQualifier;
            }
            //add a line return character to the last entry
            headers[headers.length-1]+=nextLine;
            //write out the headers
            writer.writeNext(headers);
        }
    }

    private void writeMainValues(){
        if (!cancelled){

            if (table.getFieldCount()>0){
                //now write out the main body of data.
                String[] values = new String[table.getFieldCount()];
                table.moveBeforeFirst();
                while (table.hasMoreRows()){
                    table.moveToNextRow();
                    for (int i = 0; i < values.length; i++) {
                        if ( table.getColumnType(i) == FMFTable.FIELD_TYPE_STRING ){
                            values[i] = textQualifier + table.getStringValue(i) + textQualifier;
                        }else{
                            values[i] = table.getStringValue(i);
                        }
                    }
                    //add a return character to the last entry
                    values[values.length-1]+=nextLine;
                    //write out the line
                    writer.writeNext(values);
                    //increment the progress
                    progress++;
                }

                //last thing update the altered status of the resultset
                ffrs.updatesCommited();
            }
        }
    }

    private void writeAppendedValues(){

        if (!cancelled){

            if(table.getFieldCount()>0){
                if(!endOfFileEqualsNewLine()){
                    String[] newLine = {""};
                    writer.writeNext(newLine);
                }
                
                String[] values = new String[table.getFieldCount()];

                //loop through all of the data and execute insert statements
                for(int i=0; i<table.insertedRowCount(); i++){

                    //if cancelled flag has been set terminate process and return
                    if(cancelled){break;}

                    for (int j = 0; j < table.getFieldCount(); j++) {
                        if ( table.getColumnType(j) == FMFTable.FIELD_TYPE_STRING ){
                            values[j] = textQualifier + table.getInsertedValue(i, j).toString() + textQualifier;
                        }else {
                            values[j] = table.getInsertedValue(i, j).toString();
                        }
                    }

                    //add a return character to the last entry
                    values[values.length-1]+=nextLine;
                    //write out the line
                    writer.writeNext(values);
                    //increment the progress
                    progress++;

                }
            }

        }

    }

    File createFile(boolean overwrite) throws IOException{

        File file = new File(table.getRDS().getFileName() +System.getProperty("file.separator")+
                FlatFileManager.appendExtention(table));

        if ( overwrite && file.exists() ){file.delete();}

        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IOException("Counld not create dir: " + parent);
        }
        file.createNewFile();

        return file;
    }

    void deleteFile(){

        File file = new File(table.getRDS().getFileName() +System.getProperty("file.separator")+
                FlatFileManager.appendExtention(table));
        if ( file.exists() ){ file.delete(); }

    }

    private boolean endOfFileEqualsNewLine(){
        File file = new File(table.getRDS().getFileName() +System.getProperty("file.separator")+table.getName());
        return endOfFileEqualsNewLine(file);
    }

    public boolean endOfFileEqualsNewLine(File file){

        if (file.length()>=CSVWriter.DEFAULT_LINE_END.length()){
            RandomAccessFile fr = null;
            try {
                fr = new RandomAccessFile(file,"r");
                fr.seek(file.length()-CSVWriter.DEFAULT_LINE_END.length());
                byte[] b = new byte[CSVWriter.DEFAULT_LINE_END.length()];
                fr.read(b);
                return new String(b).equalsIgnoreCase(CSVWriter.DEFAULT_LINE_END);
            } catch (FileNotFoundException x) {
                x.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            }finally{
                if (fr!=null){try{fr.close();}catch(IOException e){}}
            }

        }else{
            if (file.length()==0){
                //it is an empty file!
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    /**
     * @return the forceHeaders
     */
    public boolean isForceHeaders() {
        return forceHeaders;
    }

    /**
     * @return the writeMainTableBody
     */
    public boolean isWriteMainTableBody() {
        return writeMainTableBody;
    }

    /**
     * @return the writeAppendedData
     */
    public boolean isWriteAppendedData() {
        return writeAppendedData;
    }


    private synchronized void sleep(){
        try {
            wait(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
