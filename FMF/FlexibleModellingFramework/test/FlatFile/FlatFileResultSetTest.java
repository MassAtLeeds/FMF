
package FlatFile;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import static org.junit.Assert.*;

/**
 *
 * @author geo8kh
 */
public class FlatFileResultSetTest {

    private FlatFileResultSet rs = null;

    public FlatFileResultSetTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        rs = new FlatFileResultSet(false);
        String[] s = {"one","two","three","four","five","six"};
        int[] i = {1,2,3,4,5,6};
        double[] d = {1.1,2.2,3.3,4.4,5.5,6.6};

        try{
            rs.addField(s, "String Field");
            rs.addField(i, "Integer Field");
            rs.addField(d, "Double Field");
        }catch(SQLException sql){
            sql.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testResultSetSetup(){

        ResultSetMetaData ffrsmd = null;

        try{
            ffrsmd = rs.getMetaData();

            assertEquals(3,ffrsmd.getColumnCount());
            assertEquals("String Field", ffrsmd.getColumnName(1));
            assertEquals(Types.VARCHAR, ffrsmd.getColumnType(1));

            assertEquals("Integer Field", ffrsmd.getColumnName(2));
            assertEquals(Types.INTEGER, ffrsmd.getColumnType(2));

            assertEquals("Double Field", ffrsmd.getColumnName(3));
            assertEquals(Types.DOUBLE, ffrsmd.getColumnType(3));

        }catch(SQLException sql){
            System.out.println(sql.getMessage());
            fail();
        }


    }

    @Test
    public void testBeforeFirst(){
        try{
            rs.beforeFirst();
            assertEquals(0,rs.getRow());
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testAfterLast(){
        try{
            rs.afterLast();
            assertEquals(7,rs.getRow());
        }catch(SQLException e){
            e.printStackTrace();
        }        
    }
    @Test
    public void testFirst(){
        try{
            assertTrue(rs.first());
            assertEquals(1,rs.getRow());
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testLast(){
        try{
            assertTrue(rs.last());
            assertEquals(6,rs.getRow());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testNext(){
        try{
            int i = 0;
            rs.beforeFirst();
            while(rs.next()){i++;}
            assertEquals(6,i);
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testAbsolute(){
        try{
            rs.absolute(3);
            assertEquals(3,rs.getRow());
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testFindColumn(){
        try{
            assertEquals(3,rs.findColumn("Double Field"));
        }catch(SQLException e){
            e.printStackTrace();
        }

    }


    @Test
    public void testGetString(){
        try{
            rs.absolute(3);
            assertEquals( "three",rs.getString("String Field") );
            assertEquals( "3",rs.getString("Integer Field") );
            assertEquals( "3.3",rs.getString("Double Field") );

            assertEquals( "three",rs.getString(1) );
            assertEquals( "3",rs.getString(2) );
            assertEquals( "3.3",rs.getString(3) );

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testGetObject(){
        try{
            rs.absolute(3);
            assertEquals( "three",rs.getObject("String Field") );
            assertEquals( 3,rs.getObject("Integer Field") );
            assertEquals( 3.3,rs.getObject("Double Field") );

            assertEquals( "three",rs.getObject(1) );
            assertEquals( 3,rs.getObject(2) );
            assertEquals( 3.3,rs.getObject(3) );

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testIsBeforeFirst(){
        try{
            rs.beforeFirst();
            assertTrue(rs.isBeforeFirst());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testIsAfterLast(){
        try{
            rs.afterLast();
            assertTrue(rs.isAfterLast());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testIsFirst(){
        try{
            rs.absolute(1);
            assertTrue(rs.isFirst());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testIsLast(){
        try{
            rs.absolute(6);
            assertTrue(rs.isLast());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateNull(){
        try{
            //move to row
            rs.absolute(3);
            //check that the original values are there
            assertEquals("three", rs.getObject(1));
            assertEquals(3, rs.getObject(2));
            assertEquals(3.3, rs.getObject(3));

            //update the values
            rs.updateNull(1);
            rs.updateNull(2);
            rs.updateNull(3);

            //check they have changed
            assertEquals("", rs.getObject(1));
            assertEquals(0, rs.getObject(2));
            assertEquals(0.0, rs.getObject(3));

            //move to row
            rs.absolute(5);
            //check that the original values are there
            assertEquals("five", rs.getObject("String Field"));
            assertEquals(5, rs.getObject("Integer Field"));
            assertEquals(5.5, rs.getObject("Double Field"));

            //update the values
            rs.updateNull("String Field");
            rs.updateNull("Integer Field");
            rs.updateNull("Double Field");

            //check they have changed
            assertEquals("", rs.getObject("String Field"));
            assertEquals(0, rs.getObject("Integer Field"));
            assertEquals(0.0, rs.getObject("Double Field"));
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateValues(){
        try{
            //move to row
            rs.absolute(3);
            //check that the original values are there
            assertEquals("three", rs.getObject(1));
            assertEquals(3, rs.getObject(2));
            assertEquals(3.3, rs.getObject(3));

            //update the values
            rs.updateString(1,"Updated using index");
            rs.updateInt(2, 300);
            rs.updateDouble(3,0.3214);

            //check they have changed
            assertEquals("Updated using index", rs.getObject(1));
            assertEquals(300, rs.getObject(2));
            assertEquals(0.3214, rs.getObject(3));

            //move to row
            rs.absolute(5);
            //check that the original values are there
            assertEquals("five", rs.getObject("String Field"));
            assertEquals(5, rs.getObject("Integer Field"));
            assertEquals(5.5, rs.getObject("Double Field"));

            //update the values
            rs.updateString("String Field", "Updated using label");
            rs.updateInt("Integer Field", 500);
            rs.updateDouble("Double Field",0.5327);

            //check they have changed
            assertEquals("Updated using label", rs.getObject("String Field"));
            assertEquals(500, rs.getObject("Integer Field"));
            assertEquals(0.5327, rs.getObject("Double Field"));
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteFirstRow(){
        try{

            //check the row count
            rs.last();
            assertEquals(6,rs.getRow());

            System.out.println("Original Delete First Row (rows = " + rs.getRow()+")");
            int rows = rs.getRow();
            for (int i = 1; i <= rows; i++) {
                rs.absolute(i);
                System.out.println(rs.getObject(1)+ " | " + rs.getObject(2)+ " | " + rs.getObject(3));
            }

            //move to row
            rs.first();

            //check that the original values are there
            assertEquals("one", rs.getObject(1));
            assertEquals(1, rs.getObject(2));
            assertEquals(1.1, rs.getObject(3));

            //delete the row
            rs.deleteRow();

            System.out.println("Deleted");
            while(rs.next()){
                System.out.println(rs.getObject(1)+ " | " + rs.getObject(2)+ " | " + rs.getObject(3));
            }

            //check the row count
            rs.last();
            assertEquals(5,rs.getRow());

            //move to row deleted, should now contain the values from the next row...
            rs.first();
            //check that the values have changed
            assertEquals("two", rs.getObject(1));
            assertEquals(2, rs.getObject(2));
            assertEquals(2.2, rs.getObject(3));

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    @Test
    public void testDeleteLastRow(){
        try{

            //check the row count
            rs.last();
            assertEquals(6,rs.getRow());

            System.out.println("Original Delete Last Row (rows = " + rs.getRow()+")");
            int rows = rs.getRow();
            for (int i = 1; i <= rows; i++) {
                rs.absolute(i);
                System.out.println(rs.getObject(1)+ " | " + rs.getObject(2)+ " | " + rs.getObject(3));
            }

            //move to row
            rs.last();

            //check that the original values are there
            assertEquals("six", rs.getObject(1));
            assertEquals(6, rs.getObject(2));
            assertEquals(6.6, rs.getObject(3));

            //delete the row
            rs.deleteRow();

            System.out.println("Deleted");
            while(rs.next()){
                System.out.println(rs.getObject(1)+ " | " + rs.getObject(2)+ " | " + rs.getObject(3));
            }

            //check the row count
            rs.last();
            assertEquals(5,rs.getRow());

            //check that the values have changed
            assertEquals("five", rs.getObject(1));
            assertEquals(5, rs.getObject(2));
            assertEquals(5.5, rs.getObject(3));

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteMiddleRow(){
        try{

            //check the row count
            rs.last();
            assertEquals(6,rs.getRow());

            System.out.println("Original Delete Row 3 (rows = " + rs.getRow()+")");
            int rows = rs.getRow();
            for (int i = 1; i <= rows; i++) {
                rs.absolute(i);
                System.out.println(rs.getObject(1)+ " | " + rs.getObject(2)+ " | " + rs.getObject(3));
            }

            //move to row
            rs.absolute(3);

            //check that the original values are there
            assertEquals("three", rs.getObject(1));
            assertEquals(3, rs.getObject(2));
            assertEquals(3.3, rs.getObject(3));

            //delete the row
            rs.deleteRow();

            System.out.println("Deleted");
            while(rs.next()){
                System.out.println(rs.getObject(1)+ " | " + rs.getObject(2)+ " | " + rs.getObject(3));
            }

            //check the row count
            rs.last();
            assertEquals(5,rs.getRow());

            //move to row deleted, should now contain the values from the next row...
            rs.absolute(3);
            //check that the values have changed
            assertEquals("four", rs.getObject(1));
            assertEquals(4, rs.getObject(2));
            assertEquals(4.4, rs.getObject(3));

            //move to previous row this should have remained the same
            rs.absolute(2);
            //check that the values have changed
            assertEquals("two", rs.getObject(1));
            assertEquals(2, rs.getObject(2));
            assertEquals(2.2, rs.getObject(3));

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testIsClosed(){
        try{
            assertFalse(rs.isClosed());
            rs.close();
            assertTrue(rs.isClosed());
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testUsingFMFTable(){
        FMFTable table = new FMFTable("test",rs,new FlatFileStatement());

        assertEquals("no such field name",table.getFieldName(-1));
        assertEquals("String Field",table.getFieldName(0));
        assertEquals("Integer Field",table.getFieldName(1));
        assertEquals("Double Field",table.getFieldName(2));
        assertEquals("no such field name",table.getFieldName(3));

        assertEquals(6,table.getRowCount());
        table.moveBeforeFirst();
        System.out.println(table.getFieldName(0)+" | "+table.getFieldName(1)+" | "+table.getFieldName(2));
        while(table.hasMoreRows()){
            table.moveToNextRow();
            System.out.println(table.getStringValue(0)+" | "+table.getStringValue(1)+" | "+table.getStringValue(2));
        }

        table.deleteRow(3);
        assertEquals(5,table.getRowCount());
        table.moveBeforeFirst();
        System.out.println(table.getFieldName(0)+" | "+table.getFieldName(1)+" | "+table.getFieldName(2));
        while(table.hasMoreRows()){
            table.moveToNextRow();
            System.out.println(table.getStringValue(0)+" | "+table.getStringValue(1)+" | "+table.getStringValue(2));
        }

        table.editField("Double Field", 7.912, 4);
        double val = 7.912;
        assertEquals( val, table.getDoubleValue("Double Field", 4).doubleValue(), 0.0 );

        table.moveBeforeFirst();
        System.out.println(table.getFieldName(0)+" | "+table.getFieldName(1)+" | "+table.getFieldName(2));
        while(table.hasMoreRows()){
            table.moveToNextRow();
            System.out.println(table.getStringValue(0)+" | "+table.getStringValue(1)+" | "+table.getStringValue(2));
        }

    }



}