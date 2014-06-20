/**
 * GraphGUI.java                                         
 *
 * --Copyright notice-- 
 *
 * Copyright (c) MASS Group. 
 * http://www.geog.leeds.ac.uk/groups/mass/
 * This software is licensed under 'The Artistic License' which can be found at 
 * the Open Source Initiative website at... 
 * http://www.opensource.org/licenses/artistic-license.php
 * Please note that the optional Clause 8 does not apply to this code.
 *
 * The Graph package in its original form is distributed under the 
 * GPL, compatible with this license.
 * 
 * The Standard Version source code, and associated documentation can be found at... 
 * http://www.geog.leeds.ac.uk/groups/mass/
 *
 * --End of Copyright notice-- 
 *
 **/
package uk.ac.leeds.mass.fmf.graph;

import javax.swing.*;
import uk.ac.leeds.mass.fmf.shared_objects.*;
import java.awt.*;
import java.awt.event.*;
import javax.script.*;
import java.util.*;
import java.io.*;
import graph.*;

/**
 * Provides and interface for scripting and graphing the results.<P>
 * todo TextArea to JTextArea.
 * todo Properly register ScriptEngine.
 * @author <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 * @version 1.0
 * @see uk.ac.leeds.mass.fmf.graph.GraphMenu
 */
 
public class GraphGUI extends javax.swing.JPanel {


	public GraphGUI(IApplicationInformation ai) {

		setLayout(new BorderLayout());
        GraphTabbedGUI gtg = new GraphTabbedGUI(ai);
		setPreferredSize(gtg.getPreferredSize());
		add(gtg, BorderLayout.NORTH);
		
	}

}




class GraphTabbedGUI extends JTabbedPane implements ActionListener {

    /**
     * Main source of all info on the application.
     */
    private IApplicationInformation ai;
    /**
     * Area for editing scripts.
     */
    private TextArea scriptArea;
    /**
     * Area for showing results on graph.
     */
    private JPanel resultsPanel;
    /**
     * List of all avaliable scripting engines.
     */
    private JComboBox aliasBox;
    /**
     * All avaliable scripting engines keyed by aliasBox entry.
     */
    private Hashtable engines;
    /**
     * Script saved when in help mode.
     */
    private String savedText;
    /**
     * Text when in help mode.
     */
    private String helpText;
    /**
     * Button to evaluate script.
     */
    private JButton runButton;
    /**
     * Button to show help.
     */
    private JButton helpButton;
    /**
     * Button to save script to file.
     */
    private JButton saveButton;
    /**
     * Button to load script from file.
     */
    private JButton loadButton;

    
    
    
    
    /**
     * Sets the whole thing rolling.
     * @param ai IApplicationInformation used for getting hold of GUI, files, etc.
     */
    public GraphTabbedGUI(IApplicationInformation ai) {

        // Take over main panel of the application.
        this.ai = ai;
        JDesktopPane jp = ai.getMainPanel();
        //jp.removeAll();

        // Add in our GUI. This is a tabbed layout. 

        Dimension prefDimension = new Dimension((int) (jp.getWidth() * 0.97), (int) (jp.getHeight() * 0.94));
        setPreferredSize(prefDimension);
		
        // First tab is a script editing and running interface.

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 9;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        prefDimension = new Dimension((int) prefDimension.getWidth() - 8, (int) prefDimension.getHeight() - 84);

        JPanel scriptingPanel = new JPanel();
        scriptingPanel.setPreferredSize(prefDimension);
        scriptingPanel.setLayout(new GridBagLayout());

        // Text editing area.
        scriptArea = new TextArea();
        scriptArea.setPreferredSize(prefDimension);

        // Start with the help text showing.
        helpText = GraphMenu.getResourceBundle().getString("HelpText");
        scriptArea.setText(helpText);
        scriptingPanel.add(scriptArea, gbc);

        // Various buttons etc.
        gbc.ipadx = 15;
        gbc.ipady = 10;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;

        scriptingPanel.add(new JLabel(" " + GraphMenu.getResourceBundle().getString("LanguageLabel")), gbc);

        // Potential engines for processing
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // JComboBox will contain the most sensible names that we can find for 
        // the engines, and the hashtable will let us look them up on this basis.
        aliasBox = new JComboBox();
        engines = new Hashtable();

        ScriptEngineManager sem = new ScriptEngineManager();
        java.util.List<ScriptEngineFactory> factories = sem.getEngineFactories();
        String tempName = null;

        // Get sane names.
        for (ScriptEngineFactory factory : factories) {
            if (factory.getNames().toString().toLowerCase().contains("javascript")) {
                tempName = "JavaScript: " + factory.getEngineName();
                aliasBox.addItem(tempName);
                engines.put(tempName, (Object) factory.getScriptEngine());
                continue;
            }
            if (factory.getNames().toString().toLowerCase().contains("python")) {
                tempName = "Python: " + factory.getEngineName();
                aliasBox.addItem(tempName);
                engines.put(tempName, (Object) factory.getScriptEngine());
                continue;
            }
            // Add additional names here as languages come in.

            // Default.
            tempName = "Scripting: " + factory.getEngineName();
            aliasBox.addItem(tempName);
            engines.put(tempName, (Object) factory.getScriptEngine());
            
        }

        // Add a new Engine to parse equations. This uses the parsing 
        // built into the Java Graph package.
        // Should undoubtably register this properly, but this does the job ok 
        // and is simpler.
        ScriptEngine gpe = new GraphParseEngine();
        tempName = "Equations: " + gpe.getFactory().getEngineName();
        aliasBox.addItem(tempName);
        engines.put(tempName, (Object) gpe);
        
        scriptingPanel.add(aliasBox, gbc);

        // Various buttons. Spaced with labels as insets appear not to work.
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        scriptingPanel.add(new JLabel(""), gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;

        runButton = new JButton(GraphMenu.getResourceBundle().getString("RunButtonText"));
        runButton.addActionListener(this);
        scriptingPanel.add(runButton, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;

        scriptingPanel.add(new JLabel(""), gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;

        helpButton = new JButton(GraphMenu.getResourceBundle().getString("HelpButtonText"));
        helpButton.addActionListener(this);
        scriptingPanel.add(helpButton, gbc);

        gbc.gridx = 6;
        gbc.gridy = 1;

        saveButton = new JButton(GraphMenu.getResourceBundle().getString("SaveButtonText"));
        saveButton.addActionListener(this);
        scriptingPanel.add(saveButton, gbc);

        gbc.gridx = 7;
        gbc.gridy = 1;

        loadButton = new JButton(GraphMenu.getResourceBundle().getString("LoadButtonText"));
        loadButton.addActionListener(this);
        scriptingPanel.add(loadButton, gbc);

        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

        scriptingPanel.add(new JLabel(""), gbc);

        addTab(GraphMenu.getResourceBundle().getString("ScriptingName"), scriptingPanel);

        // Second tab shows graph on a JPanel.
        // Graphs are added in actionPerformed.
        prefDimension = new Dimension((int) prefDimension.getWidth() - 8, (int) prefDimension.getHeight() - 84);

        resultsPanel = new JPanel();
        resultsPanel.setPreferredSize(prefDimension);
        resultsPanel.setLayout(new BorderLayout());
        resultsPanel.setBackground(Color.WHITE);

        addTab(GraphMenu.getResourceBundle().getString("ResultsName"), resultsPanel);


        // Add to framework GUI.
        //jp.add(this);

    }

    
    
    
    
    /**
     * Listens to pretty much everything.
     * @param ae ActionEvent from one of the GraphGUI elements. 
     */
    public void actionPerformed(ActionEvent ae) {

        // Work out which button is activated. The JComboBox isn't listened to.
        JButton tempButton = (JButton) ae.getSource();

        // Run script button.
        if (tempButton.equals(runButton)) {

            // Clear any previous results from interface.
            resultsPanel.removeAll();

            // Get the engine and script.
            ScriptEngine engine = (ScriptEngine) engines.get(aliasBox.getSelectedItem());
            String script = scriptArea.getText();

            // Make a new Graph2D ready to take results and 
            // bind for use in  the script.
            Graph2D graph = new Graph2D();
            graph.drawzero = false;
            graph.drawgrid = false;
            graph.borderTop = 50;

            // Make a new DataSet ready to hold data and 
            // bind for use in  the script.
            DataSet dataset = new DataSet();
            dataset.linestyle = 1;
            dataset.linecolor = new Color(255, 0, 0);
            dataset.marker = 1;
            dataset.markerscale = 1;
            dataset.markercolor = new Color(255, 0, 0);

            // Make a new axes, ready to display data and 
            // bind for use in  the script.
            Axis xAxis = graph.createAxis(Axis.BOTTOM);
            xAxis.attachDataSet(dataset);

            Axis yAxis = graph.createAxis(Axis.LEFT);
            yAxis.attachDataSet(dataset);

            // Bind graph to word "graph" in the script, etc.
            engine.put("graph", graph);
            engine.put("dataset", dataset);
            engine.put("xAxis", xAxis);
            engine.put("yAxis", yAxis);

            // Redirect standard out and err to the interface's reporting panel.
            JTextArea reportingTextArea = ai.getReportingPanel();
            reportingTextArea.setText("");

            PrintStream originalSystemDotOut = System.out;
            PrintStream originalSystemDotErr = System.err;

            PrintStream systemDotOutStream = new PrintStream(new TextAreaOutputStream(reportingTextArea, null, null)); //originalSystemDotOut, originalSystemDotErr));  
            PrintStream systemDotErrStream = new PrintStream(new TextAreaOutputStream(reportingTextArea, null, null)); //originalSystemDotOut, originalSystemDotErr));  

            System.setOut(systemDotOutStream);
            System.setErr(systemDotErrStream);

            // Submit the process to the process manager, 
            // running the script and hopefully adding some data to the dataset.
            GraphRunProcess grp = new GraphRunProcess(script, engine);
            IProcessManager pm = ai.getProcessManager();
            FrameworkProcessArray fpa = new FrameworkProcessArray();
            fpa.addFrameworkProcess(grp);
            pm.addProcessArray(fpa);

            // If the script does any graphing, add a results graph.
            if ((script.contains("graph") || script.contains("dataset")) || script.contains("y=")) {
                resultsPanel.add("Center", graph);
            }

            // Attach the dataset to the graph, hopefully the process 
            // has now added data to the dataset so we can display something 
            // at this point.
            graph.attachDataSet(dataset);

            // Return standard out and err.
            System.setOut(originalSystemDotOut);
            System.setErr(originalSystemDotErr);

            return;

        } // End of run button code.

        // Help button pressed.
        if (tempButton.equals(helpButton)) {

            // Swap the help text for whatever is in the scripting window, but 
            // save whatever is there for return if the button is pushed again.
            // Most of the complexity below is because the first time it runs, 
            // we show the help text. This contains an example people can start their 
            // code with, so we don't want to loose this text if this is how people start.
            if (tempButton.getText().equals(GraphMenu.getResourceBundle().getString("HelpButtonText"))) {

                // First run.
                if (savedText == null) {

                    if (scriptArea.getText().equals(GraphMenu.getResourceBundle().getString("HelpText"))) {
                        savedText = GraphMenu.getResourceBundle().getString("NoSavedText");
                    } else {
                        savedText = scriptArea.getText();
                        scriptArea.setText(GraphMenu.getResourceBundle().getString("HelpText"));
                    }

                    tempButton.setText(GraphMenu.getResourceBundle().getString("BackButtonText"));

                // Every other run.
                } else {

                    savedText = scriptArea.getText();
                    scriptArea.setText(helpText);
                    tempButton.setText(GraphMenu.getResourceBundle().getString("BackButtonText"));
                }

            } else {

                scriptArea.setText(savedText);
                tempButton.setText(GraphMenu.getResourceBundle().getString("HelpButtonText"));

            }

            return;
        } // End of help button.

        if (tempButton.equals(saveButton)) {
            save();
            return;
        }

        if (tempButton.equals(loadButton)) {
            load();
            return;
        }

    }

    
    
    
    
    /**
     * Allows the user to save scripts to a file.
     */
    private void save() {

        File file = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = chooser.showDialog(this, GraphMenu.getResourceBundle().getString("SaveDialogText"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        } else {
            return;
        }
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(file));
            if (helpButton.getText().equals(GraphMenu.getResourceBundle().getString("HelpButtonText"))) {
                bfw.write(scriptArea.getText());
            } else {
                bfw.write(savedText);
            }
            bfw.flush();
            bfw.close();
        } catch (IOException ioe) {
            ai.getReportingPanel().setText(GraphMenu.getResourceBundle().getString("FileProblemText"));
        }
    }

    
    
    
    
    /**
     * Allows the user to load scripts from a file.
     */
    private void load() {

        File file = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = chooser.showDialog(this, GraphMenu.getResourceBundle().getString("LoadDialogText"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        } else {
            return;
        }

        String text = "";

        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));

            String s;
            while ((s = bfr.readLine()) != null) {
                text = text + s + "\n";
            }


            bfr.close();
        } catch (IOException ioe) {
            ai.getReportingPanel().setText(GraphMenu.getResourceBundle().getString("FileProblemText"));
            return;
        }

        if (text.trim() != null) {
            if (helpButton.getText().equals(GraphMenu.getResourceBundle().getString("BackButtonText"))) {
                helpButton.setText(GraphMenu.getResourceBundle().getString("HelpButtonText"));
            }
            scriptArea.setText(text);
        }

    }
}
