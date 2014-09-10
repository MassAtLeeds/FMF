/**
 * GraphParseEngine.java                                         
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

import java.io.*;
import java.util.*;
import javax.script.*;
import graph.*;
import java.lang.reflect.*;
import java.awt.*;

/**
 * Engine for parsing Java Graph Equations.
 * @author <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 * @version 1.0
 * @see uk.ac.leeds.mass.fmf.framework.StartUp
 */
public class GraphParseEngine extends AbstractScriptEngine {

    /**
     * Binding used to attach names to variables in scripts.
     */
    SimpleBindings bindings = null;
    /**
     * Object exposed to scripting.
     */
    DataSet dataset = null;
    /**
     * Object exposed to scripting.
     */
    Graph2D graph = null;
    /**
     * Object exposed to scripting.
     */
    Axis xAxis = null;
    /**
     * Object exposed to scripting.
     */
    Axis yAxis = null;

    
    /**
     * Constructor; just sets up a SimpleBinding.
     */
    public GraphParseEngine() {
        bindings = new SimpleBindings();
    }

    
    
    
    
    /**
     * Call this method with a script to evaluate it.
     * Script must include an equation in the form:<BR>
     * y=2*x<BR>
     * A minimum and maximum x and a number of points. The names for these 
     * are set in Graph.properties, but would usually be in the form:<BR>
     * minX=-1.0<BR>
     * maxX=10<BR>
     * numberOfPoints=100<BR>
     * The Graph2D ("graph"), DataSet ("dataset"), and Axis ("xAxis"; "yAxis") 
     * objects exposed to scripts can be programmed via their fields:<BR>
     * dataset.linecolor = new Color (255,255,0);<BR>
     * Currently it isn't possible to call methods within these objects.
     * @param script - String to parse
     * @return - null
     */
    public Object eval(String script) {

        // Get the bound variables and attach them to objects here.        
        Iterator iterator = bindings.keySet().iterator();

        Object key = null;
        Object object = null;

        while (iterator.hasNext()) {

            key = iterator.next();
            object = bindings.get(key);
            if (object != null) {
                if (object instanceof graph.DataSet) {
                    dataset = (DataSet) object;
                } else if (object instanceof graph.Graph2D) {
                    graph = (Graph2D) object;
                } else if (object instanceof graph.Axis) {
                    xAxis = (Axis) object;
                    if (xAxis.getAxisPos() == Axis.LEFT) {
                        yAxis = xAxis;
                        xAxis = null;
                    }
                }
            }
        }

        // Only object we absolutely need is the dataset, as we need it to append data.
        if (dataset == null) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("DataSetMissingException"));
        }

        // Any useful bits of the script will be in the form a=b so we can take the 
        // Script and load it into a Properties variable, which is usually used to 
        // load this format of data from a file. We need a stream to do this 
        // (if we want to write a parser for method calls, this won't pick them up).
        byte[] bytes = script.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Properties properties = new Properties();
        try {
            properties.load(bais);
        } catch (IOException ex) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("ScriptFormatException"));
        }

        // We now check all the important elements are there, viz. y=; minX=; maxX=; numberOfPoints.
        String maxXString = properties.getProperty(GraphMenu.getResourceBundle().getString("MaxX"));
        double maxX = 0.0;
        try {
            maxX = Double.parseDouble(maxXString);
        } catch (NumberFormatException nfe) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("MaxXFormatException"));
        }

        String minXString = properties.getProperty(GraphMenu.getResourceBundle().getString("MinX"));
        double minX = 0.0;
        try {
            minX = Double.parseDouble(minXString);
        } catch (NumberFormatException nfe) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("MinXFormatException"));
        }

        if (maxX <= minX) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("RangeException"));
        }
        String quantityString = properties.getProperty(GraphMenu.getResourceBundle().getString("Quantity"));
        int quantity = 0;
        try {
            quantity = Integer.parseInt(quantityString);
        } catch (NumberFormatException nfe) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("QuantityFormatException"));
        }
        if (quantity <= 0) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("QuantityFormatException"));
        }
        String equation = properties.getProperty("y");
        if (equation == null) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("YFormatException"));
        }

        // Parse the equation.
        ParseFunction function = new ParseFunction(equation);

        if (!function.parse()) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("ParseException"));
        }

        // Calculate the y values for the x's.
        double data[] = new double[2 * quantity];
        double x = 0.0;
        int count = 0;

        for (int i = 0; i < quantity; i++) {

            x = minX + (double) i * (maxX - minX) / (quantity - 1.0);
            data[count] = x;
            try {
                data[count + 1] = function.getResult(x);
                count += 2;
            } catch (Exception e) {
                throw new NullPointerException(GraphMenu.getResourceBundle().getString("CalcException"));
            }

        }

        if (count <= 2) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("NoPointsException"));
        }

        // Append the data to the datset.
        dataset.deleteData();

        try {
            dataset.append(data, count / 2);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // That's the data part done. Now we need to parse any field-setting associated with bound objects.

        // Run through all the properties and process any that seem relevant.
        Enumeration keys = properties.keys();
        String propKey = "";

        while (keys.hasMoreElements()) {
            propKey = (String) keys.nextElement();
            if ((propKey.contains("dataset") || propKey.contains("graph")) ||
                    (propKey.contains("xAxis") || propKey.contains("yAxis"))) {
                setField(properties, propKey);
            }
        }

        // Not quite sure what an Engine should return, so here we return null.
        return null;
    }

    
    
    
    
    /**
     * Parses and enacts field alteration requests.
     * @param properties - containing variable fields = their values, all as Strings.
     * @param propKey - a particular variable name.
     */
    private void setField(Properties properties, String propKey) {

        // Check the request is for a bound object and get its class. 
        // Should be possible to do this without hardwring by using the 
        // bindings, but not so important here as the engine is 
        // very specific to Graph2D etc.
        Class c = null;
        if (propKey.contains("dataset")) {
            c = dataset.getClass();
        } else if (propKey.contains("graph")) {
            c = graph.getClass();
        } else if (propKey.contains("xAxis")) {
            c = xAxis.getClass();
        } else if (propKey.contains("yAxis")) {
            c = yAxis.getClass();
        } else {
            return;
        }

        // Get the field from the appropriate class.
        String field = propKey.substring(propKey.indexOf(".") + 1);
        Field f = null;
        try {
            f = c.getField(field);
        } catch (NoSuchFieldException nsfe) {
            throw new NullPointerException(GraphMenu.getResourceBundle().getString("NoFieldException") + propKey);
        }

        // Convert value being set from string to appropriate type and invoke using Field methods 
        // on the specific bound object being used.
        Type type = f.getGenericType();
        String value = (String) properties.get(propKey);

        if (type.toString().equals("double")) {
            try {
                double tempDouble = Double.parseDouble(value);
                if (propKey.contains("dataset")) {
                    f.setDouble(dataset, tempDouble);
                } else if (propKey.contains("graph")) {
                    f.setDouble(graph, tempDouble);
                } else if (propKey.contains("xAxis")) {
                    f.setDouble(xAxis, tempDouble);
                } else if (propKey.contains("yAxis")) {
                    f.setDouble(yAxis, tempDouble);
                }
            } catch (NumberFormatException nfe) {
                throw new NullPointerException(GraphMenu.getResourceBundle().getString("DoubleFormatException"));
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
        } else if (type.toString().equals("int")) {
            try {
                int tempInt = Integer.parseInt(value);
                if (propKey.contains("dataset")) {
                    f.setInt(dataset, tempInt);
                } else if (propKey.contains("graph")) {
                    f.setInt(graph, tempInt);
                } else if (propKey.contains("xAxis")) {
                    f.setInt(xAxis, tempInt);
                } else if (propKey.contains("yAxis")) {
                    f.setInt(yAxis, tempInt);
                }
            } catch (NumberFormatException nfe) {
                throw new NullPointerException(GraphMenu.getResourceBundle().getString("IntFormatException"));
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
        } else if (type.toString().contains("Color")) {
            try {
                Color tempColor = getColor(value);
                if (propKey.contains("dataset")) {
                    f.set(dataset, tempColor);
                } else if (propKey.contains("graph")) {
                    f.set(graph, tempColor);
                } else if (propKey.contains("xAxis")) {
                    f.set(xAxis, tempColor);
                } else if (propKey.contains("yAxis")) {
                    f.set(yAxis, tempColor);
                }
            } catch (NumberFormatException nfe) {
                throw new NullPointerException(GraphMenu.getResourceBundle().getString("IntFormatException"));
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
        }
    }

    
    
    
    
    /** 
     * Parses a java Color construction to return a Color object.
     * @param param - Standard construction call for a rgb Color, eg "new Color (255, 0, 125);" 
     * @return Color
     */
    private Color getColor(String param) {


        String r = param.substring(param.indexOf("(") + 1, param.indexOf(","));
        param = param.substring(param.indexOf(",") + 1);
        String g = param.substring(0, param.indexOf(","));
        param = param.substring(param.indexOf(",") + 1);
        String b = param.substring(0, param.indexOf(")"));


        r.trim();
        g.trim();
        b.trim();

        try {

            int red = (Integer.parseInt(r));
            int green = (Integer.parseInt(g));
            int blue = (Integer.parseInt(b));

            // Build a new Color.

            return new Color(red, green, blue);

        } catch (NumberFormatException nfe) {

            throw new NullPointerException(GraphMenu.getResourceBundle().getString("ColorFormatException"));

        }


    } // End of getColor(String param).

    

    
    
    /**
     * Creates a new SimpleBindings object to store name:object relationships.
     * @return - a new SimpleBindings
     * @see javax.script.SimpleBindings
     */
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    
    
    
    
    /**
     * Adds name:object pairs to the bindings
     * @param key Key
     * @param object Referenced object
     */
    public void put(String key, Object object) {

        bindings.put(key, object);

    }

    
    
    
    
    /**
     * Makes a ScriptEngineFactory with which to get a ScriptEngine of this type.
     * Also allows you to find out various information about the Engine type.
     * @return a factory suitable for making a ScriptEngine of this type.
     */
    public ScriptEngineFactory getFactory() {

        return new ScriptEngineFactory() {

            public String getEngineName() {
                return "Graph Parsing Engine";
            }

            public String getEngineVersion() {
                return "1.0";
            }

            public java.util.List<String> getExtensions() {
                java.util.List<String> ex = new ArrayList<String>();
                ex.add("Not Applicable");
                return ex;
            }

            public java.util.List<String> getMimeTypes() {
                java.util.List<String> ex = new ArrayList<String>();
                ex.add("Not Applicable");
                return ex;
            }

            public java.util.List<String> getNames() {
                java.util.List<String> ex = new ArrayList<String>();
                ex.add("Java Graph Parser");
                return ex;
            }

            public String getLanguageName() {
                return "Java Graph Equations";
            }

            public String getLanguageVersion() {
                return "1.0";
            }

            public Object getParameter(String key) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public String getMethodCallSyntax(String obj, String m, String... args) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public String getOutputStatement(String toDisplay) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public String getProgram(String... statements) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public ScriptEngine getScriptEngine() {
                return new GraphParseEngine();
            }
        }; // End of anonymous ScriptEngineFactory.

    }
    
    
    
    /**
     * Unsupported ScriptEngine method.
	 * @param script Script type
	 * @param context Script context
     */
    public Object eval(String script, ScriptContext context) throws ScriptException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Unsupported ScriptEngine method.
	 * @param reader Reader
	 * @param context Script context
     */
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
