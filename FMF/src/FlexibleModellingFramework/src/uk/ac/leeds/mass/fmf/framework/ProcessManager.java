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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcess;
import uk.ac.leeds.mass.fmf.shared_objects.FrameworkProcessArray;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.IProcessManager;

/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class ProcessManager implements IProcessManager{
	private IApplicationInformation ai;

	private JPanel c;
	private int processesRunning = 0;
	private javax.swing.Timer progressTimer;
	private ThreadArray ta = new ThreadArray();
	
	//this constructor is declared private to stop the default constructor being called
	private ProcessManager(){}
	
	
	/**
	*This is the package protected constructor that can be used to instantiate the object.
	*However, this should only ever be called once from the ApplicationInformation object
	*/
	ProcessManager(IApplicationInformation a) {
		
		//set up the ApplicationInformatin object
		ai = a;
		
		//get the progress JPanel from the right hand side of the main screen
		c = ai.getProgressPanel();

		//set up the layout object of the progress Panel
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		
		//Create a Timer object (javax.swing.Timer NOT java.util.Timer) to check on progress of threads
		progressTimer = new javax.swing.Timer(500, new TimerListener());
		progressTimer.start();
		
	}
	
	
	
	/**
	* Public method for adding processes to the process monitoring service.  
	* <p>
	* This method adds a process to the process monitoring service.
	*
	* @param p - a FrameworkProcess to be added to the process monitoring service and started in a new thread.
	*/
    @Override
	public void addProcess(FrameworkProcess p){

		createPanel cp = new createPanel();
		
		//adds the thread object to the thread array
		ta.addThread(p);
		
		//Sets up the screen stuff
		if (p.isInitialised()){
			c.add(cp.createProgressPanel(p,"",true,0));
			c.add(Box.createRigidArea(new Dimension(0,10)));
			c.validate();
			c.update(c.getGraphics());
		}
		processesRunning++;



		
		Thread t = new Thread(p,p.getName());
        t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}




	/**
	* Public method for adding process groups to the process monitoring service.  
	* <p>
	* This method takes a process array and adds it to the list of processes to be run through calling the addProcess method.
	*
	* @param fpa - a FrameworkProcessArray to be added to the process monitoring service and started in new threads.
	*/
	public void addProcessArray(FrameworkProcessArray fpa){
		FrameworkProcess p[] = fpa.getProcessArray();
		
		for (int pc = 0; pc < p.length; pc++){
			addProcess(p[pc]);
		}
	}
	


	/**
	*Class to create the individual progress bars for the progress panel and attach a 
	*cancel button to each.
	*The class attaches itself as the action listener for the cancel button.
	*/
	
	class createPanel implements ActionListener{
	
		JPanel createProgressPanel(FrameworkProcess p, String s, boolean enabled,int progressValue){
		
			//Create a JPanel to encapsulate the new progress controls
			JPanel jp = new JPanel();
			jp.setPreferredSize (new Dimension((int)(200),(int)(100)));
			jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));
			
			//a little padding just to make things look nice
			jp.add(Box.createRigidArea(new Dimension(0,5)));
			
			//Create the progress bar and add it to the JPanel
			JProgressBar pb = new JProgressBar(0,100);
			if (s.equals("")){s=p.getName();}
            pb.setString(s);
            pb.setToolTipText("Progress bar for " + s);
			pb.setStringPainted(true);
			Dimension dim = new Dimension(200,30);
			pb.setPreferredSize(dim);
			pb.setValue(progressValue);
			jp.add(pb);
			
			//a little padding just to make things look nice
			jp.add(Box.createRigidArea(new Dimension(0,5)));
			
			//Create a cancel button attach a button listener to it and add it to the JPanel
			JButton cancelBut = new JButton("Cancel " + p.getName());
			cancelBut.setPreferredSize(dim);
			cancelBut.setActionCommand(Integer.toString(p.getThreadID()));
			cancelBut.setEnabled(enabled);
			cancelBut.addActionListener(this);
			jp.add(cancelBut);
			
			//a little padding just to make things look nice
			jp.add(Box.createRigidArea(new Dimension(0,5)));
			
			jp.setVisible(true);
			
			return jp;
		}
		
		
		/**
		*The implemented action performed method for the action listener interface.
		*when a cancel button is clicked this method checks to see which thread it belongs to 
		*and then cancels the thread in the ThreadArray object.
		*/
		public void actionPerformed (ActionEvent ae) {
		
			int action = (Integer.valueOf(ae.getActionCommand())).intValue();
			ta.cancelThread(action);

		}
	}
	



	/**
	*This class is the timer that is fired to monitor the progress of each process.
	*/
	class TimerListener implements ActionListener {					//Start class TimerListener


		/**
		*Implementation of the action performed method of the action event listener.
		*This one listens only to the timer set up in the constructor of the 
		*main ProcessManager object.
		*/
		public void actionPerformed(ActionEvent evt) {
                       
                            
            //clear the progress panel
            c.removeAll();
            //make a new create panel object
            //(so that we can make all the individual progress bars easily).
            createPanel cp = new createPanel();

            //Check and make sure we have processes to check on.
            if (processesRunning>0){
                    int pos = 0;
                    int progressValue = 0;

                    //get the FrameworkProcess array from the ThreadArray object.
                    FrameworkProcess p[] = ta.getThreadArray();

                    //Check again to make sure that we got processes back
                    if (p!=null){
                            //create an array to populate with threads to be removed.
                            int i[] = new int[p.length];
                            String s = "";

                            //cycle through the FrameworkProcess array.
                            for (int x=0; x<p.length; x++){

                                    //used to set whether the cancel button is enabled.
                                    boolean en=true;
                                    //get the progress value for this process.
                                    progressValue = getPercentage(p[x]);

                                    //check if process has been cancelled.
                                    if (p[x].getCancel()){
                                            //mark thread for removal
                                            i[x]=p[x].getThreadID();
                                            //display that thread has been cancelled.
                                            s = p[x].getName() + " Cancelled";
                                            //disable the cancel button.
                                            en=false;

                                    //check if there has been an exception thrown.
                                    }else if (p[x].getError()!=null){
                                            if (!p[x].errorReported()){
                                                //print error to the Status Window.
                                                System.out.println(p[x].getName() + System.getProperty("line.separator"));
                                                p[x].getError().printStackTrace();
                                                p[x].setErrorReported(true);
                                            }

                                            //display the error in the progress pain
                                            s = p[x].getName() + " ERROR";

                                            //Mark thread to stay in progress display.
                                            i[x] = -1;

                                    //check and see if process is finished.
                                    }else if (p[x].getFinished()){
                                            //mark thread for removal
                                            i[x]=p[x].getThreadID();
                                            //display that it is finished.
                                            s = p[x].getName() + " Done";
                                            //disable the cancel button.
                                            en=false;

                                    //Otherwise everything is OK.
                                    }else{
                                            //Mark thread to stay in progress display.
                                            i[x] = -1;

                                            //Display the progress of the thread.
                                            s = p[x].getName() + ":  " + Integer.toString(progressValue) + "%";
                                    }

                                    //Check to see if thread has been initialised.
                                    if (p[x].isInitialised()){
                                            //create the display for the panel.
                                            c.add(cp.createProgressPanel(p[x],s,en,progressValue));
                                            c.add(Box.createRigidArea(new Dimension(0,10)));
                                    }

                                    pos++;
                            }


                            //cycle through and remove all the threads marked for removal.
                            for (int x=0; x<i.length; x++){

                                    //check to see if value of i is a valid threadID
                                    //(if it is then the thread needs removing)
                                    if (i[x]>-1){

                                            //remove a thread from the ThreadArray.
                                            ta.removeThread(i[x]);
                                            //must decrement this value when removing threads!
                                            processesRunning--;
                                    }
                            }
                    }


                }

            if (c.isShowing()){
                //Validate all the display objects
                c.validate();
                //update the graphics.
                c.update(c.getGraphics());
            }
			
			
		}
		
		
		//calculate the percentage of the process progression and return as an int
		private int getPercentage(FrameworkProcess p){
			double d = (100/(double)p.getStages()) * (double)p.getProgress();
			return (int)d;
		}


		

		
		
		
	}
}
