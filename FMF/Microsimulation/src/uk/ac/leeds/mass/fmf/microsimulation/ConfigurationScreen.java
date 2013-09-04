/*
 *   The Flexible Modelling Framework is a Social Science application for 
 *   synthesising individual level populations
 *   Copyright (C) 2013  Kirk Harland
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contact email: k.harland@leeds.ac.uk
 */

package uk.ac.leeds.mass.fmf.microsimulation;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import uk.ac.leeds.mass.fmf.generic_algorithms.SimulatedAnnealing;
import uk.ac.leeds.mass.fmf.shared_objects.DataSourceHandler;
import uk.ac.leeds.mass.fmf.shared_objects.FMFDialog;
import uk.ac.leeds.mass.fmf.shared_objects.FMFTable;
import uk.ac.leeds.mass.fmf.shared_objects.GenericTableModel;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.ICallBack;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.TreeCellInfo;

/**
 *
 * @author geo8kh
 */
public class ConfigurationScreen extends javax.swing.JPanel {

    GenericTableModel populationModel = new GenericTableModel();
    GenericTableModel constraintModel = new GenericTableModel();

    private final String FIELD_NAMES = "Fields";
    private final String POP_FIELD_ACTIONS = "Action";
    private final String POP_FIELD_VALUES = "Value";

    //labels displayed on the configuration screen
    final static String POPULATION_ID_FIELD = "Pop id field";
    final static String LINK_FIELD = "Linked field";
    final static String ZONE_ID_FIELD = "Zone ID";

    private boolean loading = false;

    private JComboBox linkFields = new JComboBox();
    private JComboBox linkValues = new JComboBox();

    private String linkTable = "";
    private String linkRDS = "";

    private boolean populationLoaded = false;

    private FMFTable tablePop = null;

    private SimulatedAnnealing sa = new SimulatedAnnealing();
    private ValidationPanel vp = null;
    private IApplicationInformation ai = null;
    private String modelType="";

    private String windowTitle = "";

    private LinkConfiguration linkConfig = new LinkConfiguration();

    private LinkDataManager dataLoader = null;

    /** Creates new form ConfigurationScreen
     * @param ai current ApplicationInformation object
     * @param modelType The JarInfo property for this type of model either JarInfo.MICROSIMULATION_MODEL or JarInfo.MICROSIMULATION_VALIDATION
     *
     */
    public ConfigurationScreen(IApplicationInformation ai, String modelType) {
        this.ai = ai;
        this.modelType = modelType;
        preinit();
        initComponents();
        customiseComponents();

    }


    void loadConfiguration(LinkConfiguration lc){
        loading = true;

        linkConfig = lc;
        setWindowTitle(JarInfo.MENU_NAME + " - " + lc.getModelName());
        RegisteredDataSource rds = ai.getRDSfromFileName(lc.getPopulationRDS());

        tablePop = rds.getTable(lc.getPopulationTable());
        loadPopulationTable(rds);

        String fieldName = "";
        ArrayList<String> popFields = lc.getPopulationFields();
        for (int i = 0; i < populationModel.getRowCount(); i++) {
            for (int j = 0; j < popFields.size(); j++) {
                fieldName = populationModel.getValueAt(i, populationModel.findColumn(FIELD_NAMES)).toString();
                if ( fieldName.equals(popFields.get(j)) ){
                    populationModel.setValueAt( LINK_FIELD, i, populationModel.findColumn(POP_FIELD_ACTIONS) );
                    //add the link table to the combo box
                    this.linkForTotal.addItem( lc.getLinkTable( popFields.get(j) ) );
                }else if( fieldName.equals(lc.getPopulationIDField()) ){
                    populationModel.setValueAt( POPULATION_ID_FIELD, i, populationModel.findColumn(POP_FIELD_ACTIONS) );
                }
            }
        }
        
        this.linkForTotal.setSelectedItem(lc.getLinkForPopulationTotal());

        if ( lc.getOutputRDS().equals("") ){
            outputRDS.setText("<<output data source>>");
        }else{
            outputRDS.setText(ai.getRDSfromFileName(lc.getOutputRDS()).getAbreviatedName());
        }

        
        if ( lc.getOutputTableGroup().equals("") ){
            outputTable.setText("<<output table group name>>");
        }else{
            outputTable.setText( lc.getOutputTableGroup() );
        }
        
        modelName.setText(lc.getModelName());

        loading = false;
    }

    private void preinit(){
        String s = new String("");
        populationModel.addColumn(FIELD_NAMES, false, s);
        populationModel.addColumn(POP_FIELD_ACTIONS, false, s);
        
        constraintModel.addColumn(FIELD_NAMES, true, s);
        constraintModel.addColumn(POP_FIELD_VALUES, true, s);


    }

    private void customiseComponents(){

        //add in  the extra listeners for the mouse pressed
        constraintTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                constraintTableMousePressed(evt);
            }
        });
        populationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                populationTableMousePressed(evt);
            }
        });

        if ( modelType.equals(JarInfo.MICROSIMULATION_MODEL) ){
            //remove all items from the lint table combo box
            this.linkForTotal.removeAllItems();
            // set up the optimisation pane
            this.actionPane.setViewportView( sa );
            //set up the randomise options
            this.randomise.setVisible(true);
            this.randomise.setSelected(!linkConfig.useSeed());
            this.seed.setVisible(true);
            this.seed.setText( Long.toString( linkConfig.getSeed() ) );
            this.seed.setEnabled(!this.randomise.isSelected());
        }else{
            //hide the link table combo box
            this.linkForTotal.setVisible(false);
            this.jLabel1.setVisible(false);
            // set the optimisation pane to be a validation pane :)
            this.actionLabel.setText("Validation settings");
            vp = new ValidationPanel(ai);
            this.actionPane.setViewportView( vp );
            this.seed.setVisible(false);
            this.randomise.setVisible(false);
        }

        this.btCreateTables.setVisible(modelType.equals(JarInfo.MICROSIMULATION_VALIDATION));
        this.btLoadData.setVisible(modelType.equals(JarInfo.MICROSIMULATION_VALIDATION));
        this.btCreateTables.setEnabled(!modelType.equals(JarInfo.MICROSIMULATION_VALIDATION));
        this.run.setEnabled(true);
        
        //set up the population table
        this.populationTable.setFillsViewportHeight(true);
        this.populationTable.setColumnSelectionAllowed(false);
        this.populationTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        this.populationTable.setTransferHandler(new DataSourceHandler(){

            @Override
            public boolean shouldImport(TreeCellInfo t) {

                if (!populationTable.isEnabled()){return false;}

                if ( t.getType() == TreeCellInfo.TABLE | t.getType() == TreeCellInfo.TABLE_ALTERED |
                            t.getType() == TreeCellInfo.TABLE_CACHED ){
                    return true;
                }
                return false;
            }

            @Override
            public void doAction(TreeCellInfo t) {

                populationModel.clearData();
                populationTable.clearSelection();
                populationLoaded = false;
                populationTable.repaint();

                tablePop = t.getRDS().getTable( t.toString() );

                //set up the link configuration object
                linkConfig.setPopulationTable( tablePop.getName(), t.getRDS().getFileName() );
                
                loadPopulationTable(t.getRDS());

            }

        });


        //set up the constraint table component
        this.constraintTable.setFillsViewportHeight(true);
        this.constraintTable.setColumnSelectionAllowed(false);
        this.constraintTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        this.constraintTable.setTransferHandler(new DataSourceHandler(){

            @Override
            public boolean shouldImport(TreeCellInfo t) {
                
                if (!constraintTable.isEnabled()){return false;}

                if ( t.getType() == TreeCellInfo.TABLE | t.getType() == TreeCellInfo.TABLE_ALTERED |
                            t.getType() == TreeCellInfo.TABLE_CACHED ){
                    return true;
                }
                return false;
            }

            @Override
            public void doAction(TreeCellInfo t) {

                constraintModel.clearData();
                constraintTable.clearSelection();

                linkFields.removeAllItems();

                FMFTable table = t.getRDS().getTable( t.toString() );

                linkTable = table.getName();
                linkRDS = t.getRDS().getFileName();
                table.loadData(ai, null);

                String fieldName = "";
                for (int i = 0; i < table.getFieldCount(); i++) {

                    fieldName = table.getFieldName(i);
                    linkFields.addItem(fieldName);
                    constraintModel.rowAdded();
                    constraintTable.setValueAt(fieldName, i, constraintModel.findColumn( FIELD_NAMES ));

                    //try and match the unique values to the field names
                    String popValue;
                    for (int valueCount = 0; valueCount < linkValues.getItemCount(); valueCount++) {
                        popValue = linkValues.getItemAt(valueCount).toString();
                        if (fieldName.equalsIgnoreCase(popValue)){
                            constraintTable.setValueAt(popValue, i, constraintModel.findColumn( POP_FIELD_VALUES ));
                        }                        
                    }

                }

                constraintTable.repaint();
                linkLabel.setText( "Link Table - " + linkTable );

            }

        });

        TableColumn col = constraintTable.getColumnModel().getColumn(constraintModel.findColumn(FIELD_NAMES));
        col.setCellEditor(new DefaultCellEditor(linkFields));
        col = constraintTable.getColumnModel().getColumn(constraintModel.findColumn(POP_FIELD_VALUES));
        col.setCellEditor(new DefaultCellEditor(linkValues));

        constraintTable.setEnabled(populationLoaded);

        this.outputRDS.setTransferHandler(new DataSourceHandler(){

            @Override
            public boolean shouldImport(TreeCellInfo t) {
                if ( t.getType() == TreeCellInfo.RDS ){
                    return true;
                }
                return false;
            }

            @Override
            public void doAction(TreeCellInfo t) {
                outputRDS.setText(t.getRDS().getAbreviatedName());
                linkConfig.setOutputRDS(t.getRDS().getFileName());
            }

        });

    }


    private void loadPopulationTable(RegisteredDataSource rds){
        boolean runEnabled = run.isEnabled();
        run.setEnabled(false);
        tablePop.loadData(ai, null);


        populationLoaded = true;
        run.setEnabled(runEnabled);

        for (int i = 0; i < tablePop.getFieldCount(); i++) {

            populationModel.rowAdded();
            populationModel.setValueAt(tablePop.getFieldName(i),
                    populationModel.getRowCount()-1, populationModel.findColumn(FIELD_NAMES));
            populationModel.setValueAt("",
                    populationModel.getRowCount()-1, populationModel.findColumn(POP_FIELD_ACTIONS));
        }

        populationLabel.setText("Population Table - " + tablePop.getName());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        populationPopup = new javax.swing.JPopupMenu();
        setPopulationID = new javax.swing.JMenuItem();
        addLink = new javax.swing.JMenuItem();
        removeLink = new javax.swing.JMenuItem();
        editLink = new javax.swing.JMenuItem();
        linkPopupMenu = new javax.swing.JPopupMenu();
        addRow = new javax.swing.JMenuItem();
        removeRow = new javax.swing.JMenuItem();
        saveLink = new javax.swing.JMenuItem();
        actionPane = new javax.swing.JScrollPane();
        actionLabel = new javax.swing.JLabel();
        populationPane = new javax.swing.JScrollPane();
        populationTable = new javax.swing.JTable();
        constraintPane = new javax.swing.JScrollPane();
        constraintTable = new javax.swing.JTable();
        populationLabel = new javax.swing.JLabel();
        linkLabel = new javax.swing.JLabel();
        saveConfig = new javax.swing.JButton();
        modelName = new javax.swing.JTextField();
        outputRDS = new javax.swing.JTextField();
        outputTable = new javax.swing.JTextField();
        outputLabel = new javax.swing.JLabel();
        run = new javax.swing.JButton();
        btLoadData = new javax.swing.JButton();
        btCreateTables = new javax.swing.JButton();
        randomise = new javax.swing.JCheckBox();
        seed = new javax.swing.JTextField();
        linkForTotal = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();

        setPopulationID.setText("Set as ID field");
        setPopulationID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPopulationIDActionPerformed(evt);
            }
        });
        populationPopup.add(setPopulationID);

        addLink.setText("Add a link");
        addLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLinkActionPerformed(evt);
            }
        });
        populationPopup.add(addLink);

        removeLink.setText("Remove link");
        removeLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLinkActionPerformed(evt);
            }
        });
        populationPopup.add(removeLink);

        editLink.setText("Edit link");
        editLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLinkActionPerformed(evt);
            }
        });
        populationPopup.add(editLink);

        addRow.setText("Add row");
        addRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRowActionPerformed(evt);
            }
        });
        linkPopupMenu.add(addRow);

        removeRow.setText("Remove row");
        removeRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRowActionPerformed(evt);
            }
        });
        linkPopupMenu.add(removeRow);

        saveLink.setText("Save link");
        saveLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLinkActionPerformed(evt);
            }
        });
        linkPopupMenu.add(saveLink);

        actionLabel.setText("Optimisation settings");

        populationTable.setModel(populationModel);
        populationTable.setToolTipText("Drag the population table from the data source tree and use the context menu to create and edit the links.");
        populationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                populationTableMouseReleased(evt);
            }
        });
        populationPane.setViewportView(populationTable);

        constraintTable.setModel(constraintModel);
        constraintTable.setToolTipText("Drag a table from the data source tree to start linking");
        constraintTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                constraintTableMouseReleased(evt);
            }
        });
        constraintPane.setViewportView(constraintTable);

        populationLabel.setText("Population Table");

        linkLabel.setText("Link Table");

        saveConfig.setText("Save configuration");
        saveConfig.setToolTipText("Save the current configuration settings.");
        saveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigActionPerformed(evt);
            }
        });

        modelName.setText("<<model name>>");
        modelName.setToolTipText("Enter the name for this configuration without using the ' character.");
        modelName.setBorder(null);

        outputRDS.setEditable(false);
        outputRDS.setText("<<output data source>>");
        outputRDS.setToolTipText("Drag a data source from the data source tree where the model output is to be saved.");
        outputRDS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        outputTable.setText("<<output table group name>>");
        outputTable.setToolTipText("<html>Type in a prefix for the group of tables that will be created to save the output (without using the character ').<br><b>  Note that any existing tables by the same name will be replaced.</b>");
        outputTable.setBorder(null);

        outputLabel.setText("Output to:");

        run.setText("Run");
        run.setToolTipText("Run the current configuration.");
        run.setEnabled(false);
        run.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });

        btLoadData.setText("Load Data");
        btLoadData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLoadDataActionPerformed(evt);
            }
        });

        btCreateTables.setText("Create Tables");
        btCreateTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCreateTablesActionPerformed(evt);
            }
        });

        randomise.setText("Randomise");
        randomise.setToolTipText("If checked the model will be randomised, if unchecked the seed value to the left will be used.");
        randomise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomiseActionPerformed(evt);
            }
        });

        seed.setText("0");
        seed.setToolTipText("Enter a seed to make model results reproducable");
        seed.setBorder(null);

        linkForTotal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        linkForTotal.setToolTipText("Select the constraint table to be used for calculating the total population for each zone");
        linkForTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkForTotalActionPerformed(evt);
            }
        });

        jLabel1.setText("Table for zone totals ");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(populationLabel)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(linkLabel)
                            .add(populationPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                            .add(constraintPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(linkForTotal, 0, 266, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(actionLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(layout.createSequentialGroup()
                                        .add(seed)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(randomise))
                                    .add(saveConfig))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(btLoadData)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(btCreateTables)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(run))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, modelName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 241, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(layout.createSequentialGroup()
                                .add(outputLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(outputRDS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(outputTable))
                            .add(actionPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(populationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(populationPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(linkLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(constraintPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(actionLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(actionPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(outputLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(outputRDS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(outputTable, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(modelName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                            .add(saveConfig))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(run)
                    .add(btLoadData)
                    .add(btCreateTables)
                    .add(randomise)
                    .add(seed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(linkForTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void populationTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_populationTableMouseReleased
        populationTableAction(evt);
    }//GEN-LAST:event_populationTableMouseReleased

    private void populationTableMousePressed(java.awt.event.MouseEvent evt) {                                              
        populationTableAction(evt);
    }

    private void populationTableAction(java.awt.event.MouseEvent evt){
        if (evt.isPopupTrigger() && populationTable.isEnabled()){

            addLink.setEnabled( populationLoaded  & !isPopLinkField() & !isPopIDField() );
            removeLink.setEnabled( populationLoaded & isPopLinkField() & !isPopIDField() );
            setPopulationID.setEnabled( populationLoaded & !isPopLinkField() & !isPopIDField() );
            editLink.setEnabled(populationLoaded & isPopLinkField());

            populationPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private void setPopulationIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPopulationIDActionPerformed
        int selRow = this.populationTable.getSelectedRow();
        if ( selRow > -1 ){
            int col = populationModel.findColumn( POP_FIELD_ACTIONS );
            for (int i = 0; i < populationModel.getRowCount(); i++) {
                if ( populationModel.getValueAt(i, col).equals(POPULATION_ID_FIELD) ){
                    this.populationTable.setValueAt( "", i, col );
                }
            }

            this.populationTable.setValueAt( POPULATION_ID_FIELD, selRow, col );
            linkConfig.setPopulationIDField( (String) populationTable.getValueAt(selRow,
                    populationModel.findColumn(FIELD_NAMES)) );

            this.populationTable.repaint();
        }
    }//GEN-LAST:event_setPopulationIDActionPerformed

    private void addLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLinkActionPerformed
        int selRow = this.populationTable.getSelectedRow();
        if ( selRow > -1 ){
            int col = populationModel.findColumn( POP_FIELD_ACTIONS );
            this.populationTable.setValueAt( LINK_FIELD, selRow, col );

            col = populationModel.findColumn( FIELD_NAMES );
            linkValues.removeAllItems();
            ArrayList a = tablePop.getUniqueValues( populationTable.getValueAt( selRow, col ).toString() );
            Collections.sort(a);

            linkValues.addItem(ZONE_ID_FIELD);
            for (int i = 0; i < a.size(); i++) {
                linkValues.addItem(a.get(i));
            }

            this.populationTable.repaint();

            this.constraintTable.setEnabled(populationLoaded);
            this.populationTable.setEnabled(false);
            saveConfig.setEnabled(false);
            run.setEnabled(false);

        }
    }//GEN-LAST:event_addLinkActionPerformed

    private void constraintTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_constraintTableMouseReleased
        if (evt.isPopupTrigger() && constraintTable.isEnabled()){
            linkPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_constraintTableMouseReleased

    private void constraintTableMousePressed(java.awt.event.MouseEvent evt) {
        if (evt.isPopupTrigger() && constraintTable.isEnabled()){
            linkPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private void addRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRowActionPerformed
        constraintModel.rowAdded();
        constraintTable.repaint();
    }//GEN-LAST:event_addRowActionPerformed

    private void removeRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRowActionPerformed
        int selRow = this.constraintTable.getSelectedRow();
        if ( selRow > -1 ){
            constraintModel.rowDeleted(selRow);
            constraintTable.repaint();
        }
    }//GEN-LAST:event_removeRowActionPerformed

    private void saveLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLinkActionPerformed

        // save link code to go here
        for (int i = 0; i < constraintModel.getRowCount(); i++) {

            if ( constraintModel.getValueAt( i, constraintModel.findColumn(FIELD_NAMES) ) != null &&
                    constraintModel.getValueAt(i, constraintModel.findColumn(POP_FIELD_VALUES) ) != null ){

                linkConfig.addLink(
                        constraintModel.getValueAt( i, constraintModel.findColumn(FIELD_NAMES) ).toString(),
                        constraintModel.getValueAt(i, constraintModel.findColumn(POP_FIELD_VALUES) ).toString()
                        );
                
            }
            
        }
        //add the value to the link table selection combo box
        this.linkForTotal.removeItem( linkTable.toString() );
        this.linkForTotal.addItem( linkTable.toString() );
        
        linkConfig.saveLink( (String) populationTable.getValueAt(
                populationTable.getSelectedRow(), populationModel.findColumn( FIELD_NAMES ) ),
                linkTable, linkRDS);

        linkValues.removeAllItems();
        linkFields.removeAllItems();

        linkLabel.setText( "Link Table - ");
        
        constraintModel.clearData();
        constraintTable.repaint();
        constraintTable.setEnabled(false);
        saveConfig.setEnabled(true);
        run.setEnabled(true);

        populationTable.setEnabled(true);
        
    }//GEN-LAST:event_saveLinkActionPerformed

    private void removeLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLinkActionPerformed
        int selRow = this.populationTable.getSelectedRow();
        if ( selRow > -1 ){

            int col = populationModel.findColumn( POP_FIELD_ACTIONS );
            if ( LINK_FIELD.equals(this.populationTable.getValueAt(selRow, col)) ){
                this.populationTable.setValueAt( "", selRow, col );
                //remove the entry from the link table selection box
                this.linkForTotal.removeItem(linkConfig.getLinkTable((String) populationTable.getValueAt( selRow, populationModel.findColumn(FIELD_NAMES) )));
                //remove the link from the configuration object
                linkConfig.removeLink( (String) populationTable.getValueAt( selRow, populationModel.findColumn(FIELD_NAMES) ));
                //repaint the population table
                this.populationTable.repaint();
            }

        }
    }//GEN-LAST:event_removeLinkActionPerformed

    private void saveConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigActionPerformed
        if ( modelName.getText().equals("<<model name>>") || modelName.getText().equals("") ){
            FMFDialog.showInformationDialog(ai, JarInfo.MENU_NAME,
                                    "Please enter a valid model name.",
                                    JOptionPane.INFORMATION_MESSAGE,
                                    JOptionPane.OK_CANCEL_OPTION);
        }else{
            int answer = JOptionPane.YES_OPTION;
            if ( ai.modelExists(modelType, modelName.getText()) ){
                answer = FMFDialog.showInformationDialog(ai, JarInfo.MENU_NAME,
                                    "A model by this name already exists, overwrite?",
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION);
            }
            if ( answer == JOptionPane.YES_OPTION ){
                linkConfig.setModelName( modelName.getText() );
                if ( !outputTable.getText().equals("<<output table group name>>") ){
                    linkConfig.setOutputTableGroup( outputTable.getText() );
                }
                linkConfig.saveConfiguration( ai, modelType );
                updateWindowName( modelName.getText() );
            }

        }
    }//GEN-LAST:event_saveConfigActionPerformed

    private void editLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLinkActionPerformed

        int selRow = this.populationTable.getSelectedRow();
        if ( selRow > -1 ){
            String popField = populationTable.getValueAt(selRow, populationModel.findColumn(FIELD_NAMES)).toString();

            linkTable = linkConfig.getLinkTable(popField);
            linkRDS = linkConfig.getLinkRDS(linkTable);
            ArrayList<String> fields = linkConfig.getLinkFields(linkTable);
            ArrayList values = linkConfig.getLinkValues(linkTable);

            for (int i = 0; i < fields.size(); i++) {

                constraintModel.rowAdded();
                constraintTable.setValueAt(fields.get(i), i, constraintModel.findColumn( FIELD_NAMES ));
                constraintTable.setValueAt(values.get(i), i, constraintModel.findColumn( POP_FIELD_VALUES ));

            }


            //add all the fields from the link table
            linkFields.removeAllItems();
            FMFTable table = ai.getRDSfromFileName(linkRDS).getTable(linkTable);
            for (int i = 0; i < table.getFieldCount(); i++) {
                linkFields.addItem( table.getFieldName(i) );
            }
            

            //add all the values from the popuplation field
            linkValues.removeAllItems();
            ArrayList a = tablePop.getUniqueValues(popField);

            linkValues.addItem(ZONE_ID_FIELD);
            for (int i = 0; i < a.size(); i++) {
                linkValues.addItem(a.get(i));
            }

            constraintTable.repaint();
            linkLabel.setText( "Link Table - " + linkTable );

            this.constraintTable.setEnabled(populationLoaded);
            this.populationTable.setEnabled(false);
            saveConfig.setEnabled(false);
            run.setEnabled(false);
        }
    }//GEN-LAST:event_editLinkActionPerformed

    private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
        
        if ( !this.outputRDS.getText().equals("<<output data source>>") ){

            linkConfig.setOutputTableGroup(this.outputTable.getText());
            
            if ( modelType.equals(JarInfo.MICROSIMULATION_MODEL) ){

                boolean runModel = false;

                //set up the randomisation options
                if ( this.randomise.isSelected() ){
                    linkConfig.setUseSeed(false);
                    runModel = true;
                }else{
                    linkConfig.setUseSeed(true);
                    try{
                        long l = Long.parseLong(this.seed.getText());
                        linkConfig.setSeed(l);
                        runModel = true;
                    }catch(NumberFormatException nfe){
                        runModel = false;
                        System.out.println("The seed is not a valid Long number please correct and try again.");
                    }

                }

                if ( runModel ){

                    //make sure the last currently selected value on the screen is used for the
                    //population total calculations
                    setChosenLinkForPopulationTotal();

                    ai.getProcessManager().addProcess(
                            new OptimisePopulationUsingSA(ai, linkConfig, sa)
                            );
                    ai.closeMainPanel(this.getWindowTitle());
                }
            }else if ( modelType.equals(JarInfo.MICROSIMULATION_VALIDATION) ){
                if ( vp.getPopTable() !=null && !vp.getZoneIDField().equals("") ){

                    String prefix = linkConfig.getOutputTableGroup()+"_";

                    ArrayList<String> popfields = linkConfig.getPopulationFields();
                    FMFTable[] observedTables = new FMFTable[popfields.size()];
                    FMFTable[] predictedTables = new FMFTable[popfields.size()];
                    String[] zonesIDs = new String[popfields.size()];
                    int counter = 0;
                    for (String popfield : popfields) {
                        String linktable = linkConfig.getLinkTable(popfield);
                        observedTables[counter] = ai.getRDSfromFileName( linkConfig.getLinkRDS(linktable) ).getTable(linktable);
                        predictedTables[counter] = ai.getRDSfromFileName( linkConfig.getOutputRDS() ).getTable(prefix+linktable);
                        zonesIDs[counter] = linkConfig.getZoneIDField(linktable);
                        counter++;
                    }

                    Validate v = new Validate(ai, ai.getRDSfromFileName(linkConfig.getOutputRDS()),
                            vp.getSelectedGOFTests(), observedTables, predictedTables,
                            zonesIDs, linkConfig.getOutputTableGroup());

                    this.run.setEnabled(false);

                    ai.getProcessManager().addProcess(v);

                }
            }
        }else{
            FMFDialog.showInformationDialog(ai, "Microsimulation", "Please set the output datasource",
                    JOptionPane.INFORMATION_MESSAGE , JOptionPane.OK_CANCEL_OPTION);
        }

    }//GEN-LAST:event_runActionPerformed

    private void btLoadDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLoadDataActionPerformed
        this.btLoadData.setEnabled(false);

        dataLoader = new LinkDataManager(ai,linkConfig);

        dataLoader.setCallBack(new ICallBack(){

            @Override
            public void callBack() {
                btCreateTables.setEnabled(true);
            }

        });
        ai.getProcessManager().addProcess(dataLoader);
      
}//GEN-LAST:event_btLoadDataActionPerformed

    private void btCreateTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCreateTablesActionPerformed
        if ( !this.outputRDS.getText().equals("<<output data source>>") ){
            this.run.setEnabled(false);
            this.btCreateTables.setEnabled(false);
            CreateSummaryTables cst = new CreateSummaryTables(ai, dataLoader, linkConfig, vp.getPopRDS(),
                vp.getPopTable(), vp.getZoneIDField(), vp.getPersonIDField());

            cst.setCallBack(new ICallBack(){

                @Override
                public void callBack() {
                    run.setEnabled(true);
                }

            });
            ai.getProcessManager().addProcess(cst);
        }else{
            FMFDialog.showInformationDialog(ai, "Microsimulation", "Please set the output datasource",
                    JOptionPane.INFORMATION_MESSAGE , JOptionPane.OK_CANCEL_OPTION);
        }

    }//GEN-LAST:event_btCreateTablesActionPerformed

    private void randomiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomiseActionPerformed
        this.seed.setEnabled(!this.randomise.isSelected());
    }//GEN-LAST:event_randomiseActionPerformed

    private void linkForTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkForTotalActionPerformed
        setChosenLinkForPopulationTotal();
    }//GEN-LAST:event_linkForTotalActionPerformed

    private void updateWindowName(String name){
        this.setWindowTitle( ai.upDateMainPanelTitle(this.getWindowTitle(), JarInfo.MENU_NAME + " - " + name) );
    }

    private boolean isPopLinkField(){
        int selRow = this.populationTable.getSelectedRow();
        if ( selRow > -1 ){
            int col = populationModel.findColumn( POP_FIELD_ACTIONS );
            return populationModel.getValueAt(selRow, col).equals(LINK_FIELD);
        }
        return false;
    }

    private boolean isPopIDField(){
        int selRow = this.populationTable.getSelectedRow();
        if ( selRow > -1 ){
            int col = populationModel.findColumn( POP_FIELD_ACTIONS );
            return populationModel.getValueAt(selRow, col).equals(POPULATION_ID_FIELD);
        }
        return false;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel actionLabel;
    private javax.swing.JScrollPane actionPane;
    private javax.swing.JMenuItem addLink;
    private javax.swing.JMenuItem addRow;
    private javax.swing.JButton btCreateTables;
    private javax.swing.JButton btLoadData;
    private javax.swing.JScrollPane constraintPane;
    private javax.swing.JTable constraintTable;
    private javax.swing.JMenuItem editLink;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox linkForTotal;
    private javax.swing.JLabel linkLabel;
    private javax.swing.JPopupMenu linkPopupMenu;
    private javax.swing.JTextField modelName;
    private javax.swing.JLabel outputLabel;
    private javax.swing.JTextField outputRDS;
    private javax.swing.JTextField outputTable;
    private javax.swing.JLabel populationLabel;
    private javax.swing.JScrollPane populationPane;
    private javax.swing.JPopupMenu populationPopup;
    private javax.swing.JTable populationTable;
    private javax.swing.JCheckBox randomise;
    private javax.swing.JMenuItem removeLink;
    private javax.swing.JMenuItem removeRow;
    private javax.swing.JButton run;
    private javax.swing.JButton saveConfig;
    private javax.swing.JMenuItem saveLink;
    private javax.swing.JTextField seed;
    private javax.swing.JMenuItem setPopulationID;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the windowTitle
     */
    public String getWindowTitle() {
        return windowTitle;
    }

    /**
     * @param windowTitle the windowTitle to set
     */
    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    private void setChosenLinkForPopulationTotal(){
        if ( !loading ){
            if ( this.linkForTotal.getSelectedItem() != null ){
                linkConfig.setLinkForPopulationTotal(this.linkForTotal.getSelectedItem().toString());
            }else{
                linkConfig.setLinkForPopulationTotal("");
            }
        }
    }


}
