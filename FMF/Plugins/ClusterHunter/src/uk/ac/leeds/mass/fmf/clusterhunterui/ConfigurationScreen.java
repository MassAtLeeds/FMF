/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.mass.fmf.clusterhunterui;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import uk.ac.leeds.mass.cluster.Data;
import uk.ac.leeds.mass.cluster.Gamk;
import uk.ac.leeds.mass.cluster.Parameters;
import uk.ac.leeds.mass.fmf.shared_objects.DataSourceHandler;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcessArray;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author kirkharland
 */
public class ConfigurationScreen extends javax.swing.JPanel{

    private FMFTable input = null;
    private RegisteredDataSource outputRDSObject = null;
    private String windowTitle = "";
    
    private IApplicationInformation ai = null;
    
    /**
     * Creates new form ConfigurationScreen
     */
    public ConfigurationScreen(IApplicationInformation ai) {
        initComponents();
        this.ai = ai;
        customiseComponents();
    }

    private void customiseComponents(){
        
        idField.removeAllItems();
        xField.removeAllItems();
        yField.removeAllItems();
        populationField.removeAllItems();
        eventField.removeAllItems();
        
        this.inputTable.setTransferHandler(new DataSourceHandler(){
            
            @Override
            public boolean shouldImport(TreeCellInfo t) {
                return t.isTable();
            }

            @Override
            public void doAction(TreeCellInfo t) {

                //set the input table name in the text box
                inputTable.setText( t.toString() );
                //get the actual table object
                input = t.getRDS().getTable( t.toString() );
                
                if (outputRDSObject == null){
                    setOutputRDS(t.getRDS());
                }
                
                //remove all the items from each of the field lists
                idField.removeAllItems();
                xField.removeAllItems();
                yField.removeAllItems();
                populationField.removeAllItems();
                eventField.removeAllItems();
                
                //cycle through and add all of the valid numeric fields to the field lists
                for (int i = 0; i < input.getFieldCount(); i++) {
                    if (input.isNumeric(i)){
                        idField.addItem(input.getFieldName(i));
                        xField.addItem(input.getFieldName(i));
                        yField.addItem(input.getFieldName(i));
                        populationField.addItem(input.getFieldName(i));
                        eventField.addItem(input.getFieldName(i));
                    }
                }
                
            }

        });
        
        this.outputRDS.setTransferHandler(new DataSourceHandler(){
            
            @Override
            public boolean shouldImport(TreeCellInfo t) {
                //if the dragged object represents a datasource
                return t.isRDS();
            }

            @Override
            public void doAction(TreeCellInfo t) {
                //set the output data source name in the text box
                setOutputRDS(t.getRDS());
                
            }

        });
        
        //verify that the values entered into the text fields are valid for the parameter
        radiusMin.setInputVerifier( new integerVerifyer() );
        radiusMax.setInputVerifier( new integerVerifyer() );
        radiusInc.setInputVerifier( new integerVerifyer() );
        populationMin.setInputVerifier( new integerVerifyer() );
        eventsMin.setInputVerifier( new integerVerifyer() );
        
        significanceThreshold.setInputVerifier(new doubleVerifyer() );
        
    }
    
    
    private void setOutputRDS(RegisteredDataSource r){
        //set the output data source name in the text box
        outputRDSObject = r;
        outputRDS.setText( outputRDSObject.getAbreviatedName() );

        //set the output table name if it already exists
        outputTable.setText(checkTableName(outputRDSObject,outputTable.getText()));
    }
    
    private String checkTableName(RegisteredDataSource r, String tableName){
        boolean checkName = true;
        int counter = 1;
        String tab = tableName;
        while (checkName){
            checkName = false;
            if ( r.tableExists(tab) ){
                tab = tableName+"_"+Integer.toString(counter);
                counter++;
                checkName = true;
            }
        }
        return tab;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        run = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        inputTable = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        idField = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        xField = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        yField = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        populationField = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        eventField = new javax.swing.JComboBox();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel18 = new javax.swing.JLabel();
        outputRDS = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        outputTable = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        coordinateType = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        radiusMin = new javax.swing.JTextField();
        radiusMax = new javax.swing.JTextField();
        radiusInc = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        eventsMin = new javax.swing.JTextField();
        populationMin = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        significanceThreshold = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        standardise = new javax.swing.JCheckBox();

        run.setText("run");
        run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Input Table:");

        inputTable.setEditable(false);
        inputTable.setToolTipText("Drag the input table here from the data sources tab.");
        inputTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel2.setText("id field");

        idField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        idField.setToolTipText("The field name containing the unique identifier for each row in the data to be searched.  Must be numeric");

        jLabel3.setText("x field");

        xField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        xField.setToolTipText("The field containing the geographical x coordinate (longitude).");

        jLabel4.setText("y field");

        yField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        yField.setToolTipText("The field containing the geographical y coordinate (latitude).");

        jLabel5.setText("event field");

        populationField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        populationField.setToolTipText("Field containing the event count at the location.");

        jLabel6.setText("population field");

        eventField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        eventField.setToolTipText("Field containing the population count at the specified location.");

        jLabel18.setText("Output Table:");

        outputRDS.setEditable(false);
        outputRDS.setToolTipText("Drag the data source you would like to use for the output to this text box.  This will automatically default to the same location as the input table if left unchanged.");
        outputRDS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel19.setText("data source");

        outputTable.setText("significant clusters");
        outputTable.setToolTipText("Enter a valid name for the output table.");
        outputTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        outputTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                outputTableFocusLost(evt);
            }
        });

        jLabel20.setText("table name");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .add(0, 0, Short.MAX_VALUE)
                                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jLabel3)
                                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(jLabel4)
                                            .add(jLabel5)
                                            .add(jLabel6))))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(idField, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, eventField, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, populationField, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, yField, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, xField, 0, 171, Short.MAX_VALUE)))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                                .add(0, 0, Short.MAX_VALUE)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, inputTable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 277, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 288, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel18)
                                .add(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel19)
                            .add(jLabel20))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(outputTable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                            .add(outputRDS))
                        .add(16, 16, 16))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel1)
                .add(7, 7, 7)
                .add(inputTable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(idField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(xField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(yField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .add(4, 4, 4)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(populationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(eventField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel18)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outputRDS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel19))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(outputTable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("Algorithm Parameters:");

        jLabel8.setText("coordinate type");

        coordinateType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "WGS84 Lat lon", "British National Grid" }));
        coordinateType.setToolTipText("Select the coordinate type for the x, y fields in the input table.");

        jLabel9.setText("Search Circle Radius (metres)");

        jLabel10.setText("minimum");

        jLabel11.setText("maximum");

        jLabel12.setText("increment");

        radiusMin.setText("500");
        radiusMin.setToolTipText("Enter the minimum value for the search radius of the algorithm in metres.");
        radiusMin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        radiusMax.setText("5000");
        radiusMax.setToolTipText("Enter the maximum value for the search radius of the algorithm in metres.");
        radiusMax.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        radiusInc.setText("500");
        radiusInc.setToolTipText("Enter the incremental steps to increase the search radius by after each sweep of the search area in metres.");
        radiusInc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel13.setText("Statistic");

        jLabel14.setText("minimum events");

        jLabel15.setText("minimum population");

        eventsMin.setText("1");
        eventsMin.setToolTipText("The minimum number of events required before a significance test is performed to establish if the difference between the observed and expected is significant.");
        eventsMin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        populationMin.setText("1");
        populationMin.setToolTipText("The minimum population count found in the search circle before a significance test is performed.");
        populationMin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel17.setText("significance threshold");

        significanceThreshold.setText("0.0099");
        significanceThreshold.setToolTipText("The significance threshold used in the Poisson test.  The lower the value the more stringent the test.");
        significanceThreshold.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel16.setText("standardise");

        standardise.setSelected(true);
        standardise.setToolTipText("When selected the cluster hunter algorithm will automatically calculate the number of expected events in the search circle based on the population.  Uncheck this box if the events field already contains a standardised count of events expected at the location.");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(jPanel2Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel13)
                            .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jLabel8)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(coordinateType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(14, 14, 14)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel15)
                                    .add(jLabel14)
                                    .add(jLabel17)
                                    .add(jLabel16))
                                .add(18, 18, 18)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(eventsMin)
                                    .add(populationMin)
                                    .add(significanceThreshold, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                                    .add(standardise))))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel10)
                            .add(jLabel11)
                            .add(jLabel12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(radiusMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, radiusMax, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, radiusInc)))
                        .add(27, 27, 27))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 260, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 260, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(coordinateType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(radiusMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(radiusMax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(22, 22, 22))
                            .add(radiusInc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel12)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(eventsMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel15)
                    .add(populationMin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(significanceThreshold, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(standardise)
                    .add(jLabel16))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 291, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(run)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(run)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
        String[] args = new String[9];
        
        //setup the parameters from the screen input
        args[0] = Parameters.UI+"|1";
        args[1] = Parameters.RADIUS_MIN+"|"+radiusMin.getText();
        args[2] = Parameters.RADIUS_MAX+"|"+radiusMax.getText();
        args[3] = Parameters.RADIUS_INCREMENT+"|"+radiusInc.getText();
        args[4] = Parameters.MINIMUM_CASES+"|"+eventsMin.getText();
        args[5] = Parameters.MINIMUM_POPULATION+"|"+populationMin.getText();
        args[6] = Parameters.SIGNIFICANCE_THRESHOLD+"|"+significanceThreshold.getText();
        if ( standardise.isSelected() ){
            args[7] = Parameters.STANDARDISE+"|1";
        }else{
            args[7] = Parameters.STANDARDISE+"|0";
        }
        args[8] = Parameters.COORDINATE_TYPE+"|"+Integer.toString(coordinateType.getSelectedIndex()+1);
        
        //load the data into the input table FMFTable object
        input.loadData(ai, null);
        //create a two dimensional double array to hold the values
        //this step is taken to keep consistency with how the original 
        //cluster hunter algorithm worked for backwards compatibility of the
        //algorithm
        double[][] data = new double[5][input.getRowCount()];
        
        //move right to the beginning of the input dataset
        input.moveBeforeFirst();
        //create a record counter to keep track of the row we are on in the table
        int i = 0;
        //cycle through the table while it has more rows
        while (input.hasMoreRows()){
            //move to the next row in the table
            input.moveToNextRow();
            //read the data as double values into the correct place in the array
            //using the record tracker to position the record in the array
            data[0][i] = input.getDoubleValue(idField.getSelectedItem().toString());
            data[1][i] = input.getDoubleValue(xField.getSelectedItem().toString());
            data[2][i] = input.getDoubleValue(yField.getSelectedItem().toString());
            data[3][i] = input.getDoubleValue(eventField.getSelectedItem().toString());
            data[4][i] = input.getDoubleValue(populationField.getSelectedItem().toString());
            //increment the record tracking variable
            i++;
        }
        //clear the input table when we have finished reading from it
        input.clear();
        
        //get the current Data object from the GAM algorithm
        //the data object is a singleton so every object that accesses it
        //will get the same instance.  Set the data to be the array built above
        //from the input table
        Data.getCurrent().setData(data);
        
        //Parameters is also a singleton.  We access that through the getCurrent
        //method which ensures only one of these objects is available.  The parseParameters
        //method will set the algorithm up acording to the setting selected on screen.
        Parameters.getCurrent().parseParameters(args);
        
        //create a new instance of the Gamk (cluster hunter) algorithm and pass it the data object to be used
        Gamk gam = new Gamk(Data.getCurrent());
        //create a new framework process array which will take care of queing our
        //processes.  It will also make sure that each process has a progress bar and
        //a cancel button in the processes tab on the screen
        FrameworkProcessArray fpa = new FrameworkProcessArray();
        //add the gam job to the process array first so that it is executed first
        fpa.addFrameworkProcess(gam);
        //add the save results job to the process array second so that it can read the results from the
        //gam job to save into the specified table.
        fpa.addFrameworkProcess(new SaveSignificantCircles( ai, gam, outputRDSObject, outputTable.getText() ));
        //Get the process manager from the application object and submit the process array to it 
        //this will start the processes running sequentially in their own threads which will be 
        //monitored and tracked in the processes tab on screen
        ai.getProcessManager().addProcessArray(fpa);
        
        //now the jobs have been submitted close the configuration screen so that 
        //the job is not accidentally submitted twice.
        ai.closeMainPanel(this.getWindowTitle());
        
    }//GEN-LAST:event_runActionPerformed

    private void outputTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_outputTableFocusLost
        //set the output table name if it already exists
        RegisteredDataSource r = ai.getRDSfromFileName(outputRDS.getText());
        if (r != null){
            outputTable.setText(checkTableName(ai.getRDSfromFileName(outputRDS.getText()),outputTable.getText()));
        }
    }//GEN-LAST:event_outputTableFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox coordinateType;
    private javax.swing.JComboBox eventField;
    private javax.swing.JTextField eventsMin;
    private javax.swing.JComboBox idField;
    private javax.swing.JTextField inputTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField outputRDS;
    private javax.swing.JTextField outputTable;
    private javax.swing.JComboBox populationField;
    private javax.swing.JTextField populationMin;
    private javax.swing.JTextField radiusInc;
    private javax.swing.JTextField radiusMax;
    private javax.swing.JTextField radiusMin;
    private javax.swing.JButton run;
    private javax.swing.JTextField significanceThreshold;
    private javax.swing.JCheckBox standardise;
    private javax.swing.JComboBox xField;
    private javax.swing.JComboBox yField;
    // End of variables declaration//GEN-END:variables


    
    class doubleVerifyer extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            JTextField tf = (JTextField) input;
            double d;
            try{
                d = Double.parseDouble(tf.getText());
            }catch (Exception e){
                return false;
            }
            return ( !Double.isInfinite(d) && !Double.isNaN(d) && d > 0.0);
        }
    }
    
    class integerVerifyer extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            JTextField tf = (JTextField) input;
            int i;
            try{
                i = Integer.parseInt(tf.getText());
            }catch (Exception e){
                return false;
            }
            return ( i > 0 );
        }
    }
    
    void setWindowTitle(String title){
        windowTitle = title;
    }
    String getWindowTitle(){
        return windowTitle;
    }
    
}
