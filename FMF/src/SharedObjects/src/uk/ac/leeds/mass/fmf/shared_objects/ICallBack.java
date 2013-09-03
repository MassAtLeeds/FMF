/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public interface ICallBack {
	/**
	* If an object implements this interface and registers itself with a Framework process
	* this method will be called as the last call of a FrameworkProcess thread.
	*/
	public void callBack();
}
