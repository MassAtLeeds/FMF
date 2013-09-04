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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;




public class InputBox extends JDialog implements WindowListener{
    
	public final static String YES = "0";
	public final static String NO = "1";
	public final static String CANCEL = "2";
	
	private JButton yes = new JButton("yes");
	private JButton no = new JButton("no");
	private JButton cancel = new JButton("cancel");
	private JTextField input = new JTextField();
	private JPasswordField pwd = new JPasswordField();
	private ActionListener al = null;
	private String msg = "";
	IApplicationInformation ai = null;
	
	private boolean yesVis = true;
	private boolean noVis = true;
	private boolean cancelVis = true;
	
	private boolean isPassword = false;
	
	private ArrayList msgArray = new ArrayList();
	
	public InputBox (IApplicationInformation ai, String title){
		
		super(ai.getMainApplicationFrame(), title, false);
		this.ai=ai;
		setVisible(false);
                this.setIconImage(ai.getApplicationIcon().getImage());
	}
	
	public void popup(){
		
		JPanel jp = new JPanel();
		
		Font f = ai.getMenuItemFont();
		
		//create an input field to take in new information
		input.setFont(f);
		
		if (al!=null){
			yes.addActionListener(al);
			yes.setActionCommand(YES);
			no.addActionListener(al);
			no.setActionCommand(NO);
			cancel.addActionListener(al);
			cancel.setActionCommand(CANCEL);
		}
		
		yes.setVisible(yesVis);
		no.setVisible(noVis);
		cancel.setVisible(cancelVis);
		
		//add a listener to see if the window is closed
		addWindowListener(this);

		//set the contentpane layout and add the text area
		Container cp = getContentPane();
		jp.setLayout(new GridBagLayout());
		cp.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridwidth = 5;
		int gridy = 0;
		//draw the message on the screen
		for (int msgCount=0;msgCount<msgArray.size();msgCount++){
			gbc.gridy = gridy;
			JLabel lbl = new JLabel((String)msgArray.get(msgCount));
			lbl.setFont(f);
			gridy++;
			jp.add(lbl,gbc);
		}
		
		
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.gridwidth = 5;
		//if the box is for input of a password use a password field
		if (isPassword){
			jp.add(pwd,gbc);
		}else{
			jp.add(input,gbc);
		}
		gridy++;
		
		gbc.gridy = gridy;
		gridy++;
		gbc.gridwidth = 1;
		jp.add(yes,gbc);
		
		gbc.gridx=2;
		jp.add(no,gbc);
		
		gbc.gridx=3;
		jp.add(cancel,gbc);
		
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.insets = new Insets(10,10,10,10);
		cp.add(jp,gbc);
		
		//Make the inputbox non resizable and set the size
		setResizable(false);
		
		pack();
		
		//get the dimension of the main panel and place the popup towards the middle of it
		Dimension popupDimension = this.getSize(null);
		Dimension panelDimension = ai.getMainApplicationFrame().getSize(null);
		
		setLocation((int)((panelDimension.getWidth()-popupDimension.getWidth())/2),
			(int)((panelDimension.getHeight()-popupDimension.getHeight())/1.5));
		
		setVisible(true);
	}
	
	public String getInput(){
	
		String ret="";
		
		if (isPassword){
			char[] c = pwd.getPassword();
			if (c.length>0){ret=new String(c);}
		}else{
			ret=input.getText();
		}
			
		return ret;
	}
	
	public void setButtonYesText(String s){yes.setText(s);}
	public void setButtonNoText(String s){no.setText(s);}
	public void setButtonCancelText(String s){cancel.setText(s);}
	public void setActionListener(ActionListener a){al = a;}
	public void setYesVisible(boolean b){yesVis=b;}
	public void setNoVisible(boolean b){noVis=b;}
	public void setCancelVisible(boolean b){cancelVis=b;}
	public void addMessageLine(String message){msgArray.add(message);}
	public void isPassword(boolean b){isPassword = b;}
    public void enableDragNDrop(TransferHandler th, boolean disableFreeTextTyping){
        input.setEditable(!disableFreeTextTyping);
        input.setTransferHandler(th);
    }
    public void setInputText(String text){input.setText(text);}
	
	
    @Override
	public void windowActivated(WindowEvent e){}
        
    @Override
	public void windowClosed(WindowEvent e){}
        
	//use this listener to fire a cancel event when cross hair clicked
    @Override
	public void windowClosing(WindowEvent e){cancel.doClick();}
        
    @Override
	public void windowDeactivated(WindowEvent e){}
        
    @Override
	public void windowDeiconified(WindowEvent e){}
        
    @Override
	public void windowIconified(WindowEvent e){}
        
    @Override
	public void windowOpened(WindowEvent e){}
	
	
}
