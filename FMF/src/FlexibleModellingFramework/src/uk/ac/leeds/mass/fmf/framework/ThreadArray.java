/*
 * This is the new license...
 * It has been edited
 */

package uk.ac.leeds.mass.fmf.framework;

import java.util.ArrayList;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class ThreadArray {
	private ArrayList threads = new ArrayList();
	private int threadCount =0;
	
	public void addThread(FrameworkProcess p){
		threadCount++;
		p.setThreadID(threadCount);
		threads.add(p);
	}
	
	
	
	
	public void removeThread(FrameworkProcess p){
		removeThread(p.getThreadID());
	}
	
	
	public void removeThread(int threadID){
		int x=getElementID(threadID);
		if (x>-1){threads.remove(x);}
	}
	
	
	
	
	public FrameworkProcess[] getThreadArray(){
		
		FrameworkProcess pt[] = null;
		
		if (!threads.isEmpty()){
			pt = new FrameworkProcess[threads.size()];
			threads.toArray(pt);
		}
		
		return pt;
	}
	
	
	
	public void cancelThread(int threadID){
		int x=getElementID(threadID);
		FrameworkProcess pt = (FrameworkProcess)threads.get(x);
		pt.setCancel();
	}
	
	
	private int getElementID(int threadID){
		int x=-1;
		
		for (int i = 0; i<threads.size();i++){
			FrameworkProcess pt = (FrameworkProcess)threads.get(i);
			if (threadID == pt.getThreadID()){x=i;}
		}
		
		return x;
	}
}
