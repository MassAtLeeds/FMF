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

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */

import java.util.ArrayList;

public abstract class FrameworkProcess implements Runnable {
	protected volatile int progress;
	protected volatile int stages;
	protected volatile boolean done=false;
	protected volatile boolean init=false;
	protected volatile boolean cancelled = false;
	protected String processName="No name allocated";
	protected int threadID;
	protected volatile int threadGroupID;
	protected FrameworkProcessArray fpa= null;
	protected boolean boolError = false;
	protected Exception ex = null;
	private ArrayList cb = new ArrayList();
	
	
	
	public void run(){
		
		if (fpa!=null){
			if (threadGroupID>0){
				boolean waitForTurn = true;
				while (waitForTurn){
					waitForTurn = fpa.shouldWait(threadGroupID);
				}
			}
			cancelled = fpa.getAddnormalExit();
		}
		
		try{
			if (!cancelled){runProcess();}
			if (fpa!=null){fpa.setAddnormalExit(cancelled);}
		}catch (Exception e){
			if (fpa!=null){fpa.setAddnormalExit(true);}
			ex=e;
		}finally{
			if (!cb.isEmpty()){
				for (int i=0;i<cb.size();i++){
					ICallBack c = (ICallBack)cb.get(i);
					c.callBack();
				}
			}
			if (fpa!=null){fpa.threadFinished(threadGroupID);}
		}
		
	}
	
	
	public abstract void runProcess();
	
	
	public Exception getError(){
		return ex;
	}
	
	public boolean errorReported(){
		return boolError;
	}
	
	public void setErrorReported(boolean b){
		boolError = b;
	}
	
	
	public int getProgress(){
		return progress;
	}
	
	public int getStages(){
		return stages;
	}
	
	public boolean getFinished(){
		return done;
	}
	
	public void setFinished(boolean b){
		done=b;
	}
	
	public boolean isInitialised(){
		return init;
	}
	
	public void setName(String s){
		processName = s;
	}
	
	public String getName(){
		return processName;
	}
	
	public void setThreadID(int threadID){
		this.threadID=threadID;
	}
	
	public int getThreadID(){
		return threadID;
	}
	
	
	public boolean getCancel(){
		return cancelled;
	}
	
	public void setCancel(){
		cancelled=true;
	}
	
	public void setThreadGroupID(int id){
		threadGroupID = id;
	}
	
	public void setThreadGroup(FrameworkProcessArray f){
		fpa = f;
	}
	
	
	public void setCallBack(ICallBack icb){
		cb.add(icb);
	}
	
	
	protected void finished(){
		progress=stages;
		done=true;
	}
	
	
    protected void updateStages(int processStages){
        stages=processStages;
    }

	protected void initilise(int processStages){
		stages=processStages;
		progress=0;
		init = true;
	}
}
