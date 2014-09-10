/**
 * TextAreaOutputStream.java                                         
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

import javax.swing.*;
import java.io.*;

/**
 * This class provides an output stream for writing to a JTextArea.<P>
 * As one possible use is to redirct the standard outputs to a GUI, 
 * convience Constructors are provided for turning on the standard outputs 
 * as well.
 * @author  <A href="http://www.geog.leeds.ac.uk/people/a.evans/">Andy Evans</A>
 * @version 1.0
 */
public class TextAreaOutputStream extends OutputStream {

    /**
     * The text area to write to.
     */
    private JTextArea jTextArea = null;
    /**
     * The standard output.
     */
    private PrintStream originalSystemDotOut = null;
    /**
     * The standard error output.
     */
    private PrintStream originalSystemDotErr = null;

    
    
    
    
    /** 
     * Creates a new instance of TextAreaOutputStream.
     * @param jTextArea  a javax.swing.JTextArea object to write to.
     */
    public TextAreaOutputStream(JTextArea jTextArea) {
        this.jTextArea = jTextArea;
    }

    
    
    
    
    /** 
     * Creates a new instance of TextAreaOutputStream with additional standard output.<P>
     * Writes the stream to the TextArea, but also to a second PrintStream. This is in 
     * place so that you can redirect the System.out to the JTextArea, but also 
     * use the default System.out or System.err for debugging this class.
     * @param jTextArea  a javax.swing.JTextArea object to write to.
     * @param originalSystemDotOut usually the original System.out or System.err PrintStream. 
     */
    public TextAreaOutputStream(JTextArea jTextArea, PrintStream originalSystemDotOut) {

        this.jTextArea = jTextArea;
        this.originalSystemDotOut = originalSystemDotOut;

    }

    
    
    
    
    /** 
     * Creates a new instance of TextAreaOutputStream with additional standard output.<P>
     * Writes the stream to the JTextArea, but also to two additional PrintStreams. This is in 
     * place so that you can redirect the System.out and System.err to the JTextArea, but also 
     * use the default System.out and System.err for debugging this class.
     * @param jTextArea  a javax.swing.JTextArea object to write to.
     * @param originalSystemDotOut usually the original System.out PrintStream. 
     * @param originalSystemDotErr usually the original System.err PrintStream. 
     */
    public TextAreaOutputStream(JTextArea jTextArea, PrintStream originalSystemDotOut, PrintStream originalSystemDotErr) {

        this.jTextArea = jTextArea;
        this.originalSystemDotOut = originalSystemDotOut;
        this.originalSystemDotErr = originalSystemDotErr;

    }

    
    
    
    
    /**
     * Writes an int to the JTextArea.<P>
     * Converts all ints to Strings.
	 * @param i int byte to write
     **/
    public void write(int i) {

        String text = new Integer(i).toString();
        if (originalSystemDotOut != null) {
            originalSystemDotOut.println(text);
        }
        if (originalSystemDotErr != null) {
            originalSystemDotErr.println(text);
        }
        jTextArea.append(text);

    }

    
    
    
    
    /**
     * Writes a set of bytes to the JTextArea.<P>
     * Converts all bytes to Strings.
	 * @param b byte array to write
     **/
    public void write(byte[] b) {

        String text = new String(b);
        if (originalSystemDotOut != null) {
            originalSystemDotOut.println(text);
        }
        if (originalSystemDotErr != null) {
            originalSystemDotErr.println(text);
        }
        jTextArea.append(text);

    }

    
    
    
    
    /**
     * Writes a set of bytes to the JTextArea.<P>
     * Converts all bytes to Strings. Used by System.out/err PrintStreams.
	 * @param b byte array to write
	 * @param off ofset
	 * @param len length
     **/
    public void write(byte[] b, int off, int len) {

        byte[] a = new byte[len];
        System.arraycopy(b, off, a, 0, len);
        String text = new String(a);
        if (originalSystemDotOut != null) {
            originalSystemDotOut.println(text);
        }
        if (originalSystemDotErr != null) {
            originalSystemDotErr.println(text);
        }
        jTextArea.append(text);

    }

    
    
    
    
    
    /** 
     * Closes the stream. To reopen the stream you need to make a new one.
     **/
    public void close() {
        jTextArea = null;
        originalSystemDotOut = null;
        originalSystemDotErr = null;
    }

    
    
    
    
    /** 
     * There is no flush method on this stream as there is no storage.
     **/
    public void flush() {
    }// End of class.   
}
