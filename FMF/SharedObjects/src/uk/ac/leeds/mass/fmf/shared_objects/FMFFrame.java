/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class FMFFrame extends JInternalFrame implements InternalFrameListener{
    
    private IApplicationInformation ai;
    private boolean frameClosing = false;
    
    private FMFFrame(){}
    public FMFFrame(IApplicationInformation ai, String windowTitle){
        
        super (windowTitle, true, true, false);
        this.ai = ai;
        setFrameIcon(ai.getApplicationIcon());

        setMaximizable(true); 
        addInternalFrameListener(this);
                
        setTitle( windowTitle );

    }
    
    
    @Override
    public void setTitle(String windowTitle){
        super.setTitle(windowTitle);
    }

    public String getWindowTitle(){
        return super.getTitle();
    }
    
    public void internalFrameOpened(InternalFrameEvent e) {
        
    }

    public void internalFrameClosing(InternalFrameEvent e) {
        if (!isFrameClosing()){
            ai.closeMainPanel(super.getTitle());
        }
        frameClosing = true;
    }

    public void internalFrameClosed(InternalFrameEvent e) {
        
    }

    public void internalFrameIconified(InternalFrameEvent e) {
        
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {
        
    }

    public void internalFrameActivated(InternalFrameEvent e) {
        ai.setWindowSelection(this.getTitle());
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
        
    }

    public boolean isFrameClosing() {
        return frameClosing;
    }

    public void setFrameClosing(boolean b){
        frameClosing = b;
    }

}
