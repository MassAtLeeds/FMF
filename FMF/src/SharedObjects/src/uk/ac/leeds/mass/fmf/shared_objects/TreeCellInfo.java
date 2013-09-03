/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class TreeCellInfo implements Transferable{

    public final static int RDS = 1;
    public final static int TABLE = 2;
    public final static int TEXT_FIELD = 3;
    public final static int INVALID_RDS = 4;
    public final static int NUMERIC_FIELD = 5;
    public final static int PROPERTIES_FOLDER = 6;
    public final static int TABLE_FOLDER = 7;
    public final static int PROPERTY = 8;
    public final static int TABLE_CACHED = 9;
    public final static int TABLE_ALTERED = 10;
    
    private String value = "";
    private int type=-1;
    private RegisteredDataSource rds;
    private String table = "";
    
    private TreeCellInfo(){}
    
    public TreeCellInfo(String value, int type, RegisteredDataSource rds){
        initialise(value,type,rds,"");
    }

    public TreeCellInfo(String value, int type, RegisteredDataSource rds, String table){
        initialise(value,type,rds,table);
    }

    private void initialise(String value, int type, RegisteredDataSource rds, String table){
        this.value = value;
        this.type = type;
        this.rds = rds;
        this.table = table;
    }

    public void setType(int treeCellType){
        this.type = treeCellType;
    }

    public int getType(){
        return type;
    }
    
    public RegisteredDataSource getRDS(){
        return rds;
    }
    
    @Override
    public String toString(){
        return value;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] df = new DataFlavor[1];
        try {
            df[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }finally{
            return df;
        }
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        try {
            return flavor.equals(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo"));
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if ( isDataFlavorSupported(flavor) ){
            return this;
        }else{
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    public boolean isField(){
        if ( type == TEXT_FIELD | type == NUMERIC_FIELD ){
            return true;
        }else{
            return false;
        }
    }

    public boolean isRDS(){
        if ( type == RDS | type == INVALID_RDS ){
            return true;
        }else{
            return false;
        }
    }

    public boolean isTable(){
        if ( type == TABLE | type == TABLE_CACHED | type == TABLE_ALTERED ){
            return true;
        }else{
            return false;
        }
    }

}
