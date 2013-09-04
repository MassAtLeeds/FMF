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
