/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class ScreenOutputStream extends OutputStream{

    public ScreenOutputStream(){
        super();
    }
    
    @Override
    public void write(int b) throws IOException {
        byte[] c = new byte[1];
        c[0] = (byte)b;
        String msg  = new String(c);
        ApplicationInformation.getCurrent().printToStatusWindow(msg);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String msg  = new String(b,off,len);
        ApplicationInformation.getCurrent().printToStatusWindow(msg);
    }
    
    @Override
    public void write(byte[] b) throws IOException {
        
        String msg  = new String(b);
        ApplicationInformation.getCurrent().printToStatusWindow(msg);
        
    }

}
