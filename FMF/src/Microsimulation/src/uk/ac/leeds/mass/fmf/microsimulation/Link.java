/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.microsimulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;

/**
 *
 * @author Kirk Harland
 */
public class Link implements Serializable{

    private LinkConfiguration lc = null;
    private FMFTable linkTable = null;
    private FMFTable pTable = null;

    private String     zoneNameField = "";

    private String     name = "";

    private String[]   categories = null;
    private String[]   fieldNames = null;
    
    private String[][] ids = null;

    private String[]   fields = null;
    private double[][] predValues = null;
    private double[][] obsValues = null;

    private double     fittness = 0.0;

    protected Link (Link l){
        this.name = l.getName();
        this.lc = l.getLc();
        this.pTable = l.getpTable();
        this.linkTable = l.getLinkTable();

        String[] s = l.getCategories();
        this.categories = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            categories[i] = new String(s[i]);
        }

        s = l.getFieldNames();
        this.fieldNames = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            fieldNames[i] = new String(s[i]);
        }

        s = l.getFields();
        this.fields = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            fields[i] = new String(s[i]);
        }

        double[][] d = l.getObs();
        this.obsValues = new double[1][d[0].length];
        this.predValues = new double[1][d[0].length];
        for (int i = 0; i < d[0].length; i++) {
            obsValues[0][i] = d[0][i];
            predValues[0][i] = l.getPred()[0][i];
        }

        String[][] ss = l.getIds();
        this.ids = new String[ss.length][];
        for (int i = 0; i < ss.length; i++) {
            ids[i] = new String[ss[i].length];
            for (int j = 0; j < ss.length; j++) {
                ids[i][j] = ss[i][j];
            }
        }
    }

    public Link( LinkConfiguration lc, String PopulationFieldName, 
            FMFTable popTable, FMFTable linkTable ){
        name = PopulationFieldName;
        this.lc = lc;
        pTable = popTable;
        this.linkTable = linkTable;
        initialise(popTable);
    }


    private void initialise(FMFTable popTable){

        String tab = linkTable.getName();

        //set up the link field to categories arrays in sync
        categories = new String[lc.getLinkFields(tab).size()-1];
        fieldNames = new String[lc.getLinkFields(tab).size()-1];
        int offset = 0;
        for (int i = 0; i < lc.getLinkFields(tab).size(); i++) {
            if ( ! lc.getLinkFields(tab).get(i).equals(lc.getZoneIDField(tab)) ){
                fieldNames[i-offset] = lc.getLinkFields(tab).get(i);
                categories[i-offset] = lc.getLinkValues(tab).get(i).toString();
            }else{
                zoneNameField = lc.getLinkFields(tab).get(i);
                offset = 1;
            }
        }

        //set up the fields array
        HashSet<String> f = new HashSet<String>();
        for (int i = 0; i < fieldNames.length; i++) {
            if ( fieldNames[i] != null ){f.add(fieldNames[i]);}
        }
        if ( f.contains(lc.getZoneIDField(tab)) ){
            f.remove(lc.getZoneIDField(tab));
        }
        fields = new String[f.size()];
        f.toArray(fields);

        //set up the sizes of the observed and predicted arrays
        predValues = new double[1][fields.length];
        obsValues = new double[1][fields.length];

        //set up the id array first dimension to be the same as the fields array
        ids = new String[fields.length][];

        //set up an array of array lists to put the person ids in each category in
        ArrayList[] arrays = new ArrayList[fields.length];
        for (int j = 0; j < arrays.length; j++) {
            arrays[j] = new ArrayList<String>();
        }

        //populate the array lists
        String category = "";
        for (int i = 0; i < popTable.getRowCount(); i++) {
            category = pTable.getStringValue(name,i);
            for (int j = 0; j < categories.length; j++) {
                if ( category.equals(categories[j]) ){

                    for (int k = 0; k < fields.length; k++) {
                        if ( fields[k].equals(fieldNames[j]) ){
                            arrays[k].add( popTable.getStringValue(lc.getPopulationIDField(), i) );
                            break;
                        }
                    }

                    break;
                }
            }
        }

        //put the results into the ids array
        for (int i = 0; i < arrays.length; i++) {
            String s[] = new String[arrays[i].size()];
            arrays[i].toArray(s);
            ids[i] = s;
        }

    }


    boolean isPersonValid(int row){
        String category = getCategory(row);
        for (int i = 0; i < categories.length; i++) {
            if ( categories[i].equals(category) ){
                for (int j = 0; j < fields.length; j++) {
                    if ( fields[j].equals(fieldNames[i])){
                        if( obsValues[0][j] > 0.0 ){
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }


    String[] getIDs(String category){
        String s[] = null;
        for (int i = 0; i < categories.length; i++) {
            if ( category.equals(categories[i]) ){
                
                for (int j = 0; j < fields.length; j++) {
                   if ( fields[j].equals(fieldNames[i]) ){
                       s = ids[j];
                       break;
                   }                    
                }
                
                break;
            }
        }
        
        return s;
    }


    void setZone(String zone){
        //set up the observed matrix
        int row = linkTable.getFirstRowID(lc.getZoneIDField(linkTable.getName()), zone);
        for (int i = 0; i < fields.length; i++) {
            obsValues[0][i] = linkTable.getDoubleValue(fields[i], row);
            predValues[0][i] = 0.0;
        }
    }

    void addRemovePerson( int rowPop, int personCount, boolean addPerson ){
        String cat = pTable.getStringValue(name, rowPop);

        for (int i = 0; i < categories.length; i++) {
            if ( categories[i].equals(cat) ){
                for (int j = 0; j < fields.length; j++) {
                    if ( fields[j].equals(fieldNames[i]) ){
                        if (addPerson){
                            predValues[0][j] += (double)personCount;
                        }else{
                            predValues[0][j] -= (double)personCount;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    void print(){
        System.out.println("Population field: "+name);
        System.out.println("Link Table: "+linkTable.getName());
        for (int i = 0; i < fields.length; i++) {
            System.out.println("Field: "+fields[i]+" | obs: "+ obsValues[0][i]+" | pred: "+ predValues[0][i]);
        }
    }

    FMFTable getLinkTable(){
        return linkTable;
    }

    double[][] getPred(){
        return predValues;
    }

    double[][] getObs(){
        return obsValues;
    }
    
    String getName(){
        return name;
    }

    /**
     * @return the fittness
     */
    public double getFittness() {
        return fittness;
    }

    /**
     * @param fittness the fittness to set
     */
    public void setFittness(double fittness) {
        this.fittness = fittness;
    }

    /**
     * @return the categories
     */
    public String[] getCategories() {
        return categories;
    }


    /**
     * Gets the category for this link from the row id
     *
     * @param row to query in the population table
     * @return the category of the link from the row to be quieried
     */
    public String getCategory(int row){
        return pTable.getStringValue(name,row);
    }


    /**
     * @return the fieldNames
     */
    public String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * @return the fields
     */
    public String[] getFields() {
        return fields;
    }


    @Override
    public Link clone(){
        Link l = new Link(this);
        return l;
    }

    /**
     * @return the lc
     */
    protected LinkConfiguration getLc() {
        return lc;
    }


    protected FMFTable getpTable(){
        return pTable;
    }


    /**
     * @return the ids
     */
    protected String[][] getIds() {
        return ids;
    }

    /**
     * Gets the zone ids for the link table
     * @return String array of all of the zone ids
     */
    public String[] getZoneIDs(){
        String[] s = new String[linkTable.getRowCount()];
        for (int i = 0; i < linkTable.getRowCount(); i++) {
            s[i] = linkTable.getStringValue(zoneNameField, i);
        }
        return s;
    }

}
