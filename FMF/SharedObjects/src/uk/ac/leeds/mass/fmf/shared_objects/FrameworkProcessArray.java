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

import java.util.ArrayList;

/**
 *
 * @author Kirk Harland k.harland98@leeds.ac.uk
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
