/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 * 
 * Interface to be implemented by the ProcessManager.
 * 
 */
public interface IProcessManager {
    public void addProcess(FrameworkProcess p);
    public void addProcessArray(FrameworkProcessArray fpa);
}
