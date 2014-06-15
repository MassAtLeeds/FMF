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

package uk.ac.leeds.mass.fmf.shared_objects;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
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
