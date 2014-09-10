/**
 * GraphRunProcess.java                                         
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
 * The Standard Version source code, and associated documentation can be found at... 
 * http://www.geog.leeds.ac.uk/groups/mass/
 *
 * --End of Copyright notice-- 
 *
 **/
package uk.ac.leeds.mass.fmf.graph;

import uk.ac.leeds.mass.fmf.shared_objects.*;
import javax.script.*;

/**
 * Runs a scripting engine as a threaded process.
 * @author <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 * @version 1.0
 * @see uk.ac.leeds.mass.fmf.graph.GraphGUI
 */
public class GraphRunProcess extends FrameworkProcess {

    /**
     * Script to run.
     */
    private String script = null;
    /**
     * Script engine to run the script.
     */
    private ScriptEngine engine = null;

    
    
    
    /**
     * Just sets the script and script engine. 
     * The process is run by calling runProcess.
     * @param script  Script to run.
     * @param engine  Script engine to run the script.
     */
    public GraphRunProcess(String script, ScriptEngine engine) {

        this.script = script;
        this.engine = engine;

		initilise(1);
		setName("Graphing...");
		this.updateStages(1);
		progress++;
		
    }

    
    
    
    
    /**
     * Run this to run the process within a threading framework. 
     */
	@Override
    public void runProcess() {

        try {
            engine.eval(script);
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }

    }
}
