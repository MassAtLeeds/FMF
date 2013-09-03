/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.leeds.mass.fmf.shared_objects;

import java.util.ArrayList;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class FrameworkProcessArray {
	ArrayList fp = new ArrayList();
	boolean done[];
	private int groupID = 0;
	private boolean addnormalExit = false;
	
	public void addFrameworkProcess(FrameworkProcess p){
		p.setThreadGroupID(groupID);
		p.setThreadGroup(this);
		
		done = new boolean [groupID+1];
		for (int i = 0; i<done.length;i++){
			done[i]=false;
		}			
		
		fp.add(groupID,p);
		
		groupID++;
	}
	
	public synchronized void setAddnormalExit(boolean b){
		addnormalExit = b;
	}
	
	public synchronized boolean getAddnormalExit(){
		return addnormalExit;
	}
	
	public synchronized boolean shouldWait(int threadID){
		if (done[threadID-1]){
			return false;
		}else{
			try{wait();}catch(Exception e){}
			return true;			
		}
		
	}
	
	public synchronized void threadFinished(int threadID){
		done[threadID]=true;
		notifyAll();
	}
	
	
	
	public FrameworkProcess[] getProcessArray(){
		
		FrameworkProcess pt[] = null;
		
		if (!fp.isEmpty()){
			pt = new FrameworkProcess[fp.size()];
			fp.toArray(pt);
		}
		
		return pt;
	}
	
	public void setCallBack(ICallBack cb){
		if (!fp.isEmpty()){
			FrameworkProcess f = (FrameworkProcess)fp.get(fp.size()-1);
			f.setCallBack(cb);
		}
	}
}
