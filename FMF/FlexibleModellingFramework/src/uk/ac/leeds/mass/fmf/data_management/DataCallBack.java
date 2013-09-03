/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.data_management;

import uk.ac.leeds.mass.fmf.shared_objects.ICallBack;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class DataCallBack implements ICallBack{

    private IThreadWakeUp wu;
    
    
    private DataCallBack(){}
    
    public DataCallBack(IThreadWakeUp wu){
        this.wu = wu;
    }
    
    public void callBack() {
        wu.wakeUp();
    }

}
