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

package MSAccess;

import uk.ac.leeds.mass.fmf.data_management.*;
import java.io.File;
import javax.swing.JFileChooser;
import uk.ac.leeds.mass.fmf.shared_objects.IApplicationInformation;
import uk.ac.leeds.mass.fmf.shared_objects.ICallBack;
import uk.ac.leeds.mass.fmf.shared_objects.IProcessManager;
import uk.ac.leeds.mass.fmf.shared_objects.RegisteredDataSource;
import uk.ac.leeds.mass.fmf.shared_objects.SystemProperties;

/**
 *
 * @author  Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class MSAccessDataLocator extends javax.swing.JPanel implements ICallBack{

    private IApplicationInformation ai;
    
    private RegisteredDataSource rds;
    MSAccessVerifyDataSource verify = null;
    
    
    private MSAccessDataLocator(){}
    
    /** 
     * Creates new form MSAccessDataLocator
     * @param a : IApplicationInformation object
     */
    
    public MSAccessDataLocator(IApplicationInformation a) {
        initComponents();
        ai=a;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        databasePath = new javax.swing.JTextField();
        browse = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        connect = new javax.swing.JButton();
        savePassword = new javax.swing.JCheckBox();

        databasePath.setText("<<no database selected>>");
        databasePath.setToolTipText("Full path of where to location the database.");
        databasePath.setBorder(null);

        browse.setText("...");
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseActionPerformed(evt);
            }
        });

        jLabel1.setText("Type in or select the Microsoft Access database to connect to:");

        username.setToolTipText("User name to use for connecting to the database if required.");
        username.setBorder(null);

        jLabel2.setText("username:");

        jLabel3.setText("password:");

        password.setToolTipText("Password for the database if required.");
        password.setBorder(null);

        connect.setText("connect...");
        connect.setToolTipText("Attempt to connect to the specified database.");
        connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectActionPerformed(evt);
            }
        });

        savePassword.setText("remember password");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(databasePath, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browse, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(savePassword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 293, Short.MAX_VALUE)
                        .addComponent(connect))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                            .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browse)
                    .addComponent(databasePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connect)
                    .addComponent(savePassword))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {browse, databasePath, password, username});

    }// </editor-fold>//GEN-END:initComponents

private void browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseActionPerformed
    String s = launchFileBrowser();
    if ((s!=null) && (!s.equals(""))){databasePath.setText(s);}
}//GEN-LAST:event_browseActionPerformed

private void connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectActionPerformed
    
    if ( (!databasePath.getText().equals("<<no database selected>>") && !databasePath.getText().equals("")) ){
        
        rds = new RegisteredDataSource();
//        rds.setUniqueName( uniqueName.getText() );
        rds.setDSN(false);
        rds.setFileName(databasePath.getText());
        rds.setUserName(username.getText());
        char[] c = password.getPassword();
        if (c.length>0){
            rds.setPassword(new String(password.getPassword()));
        }else{
            rds.setPassword("");
    	}
        
        verify = new MSAccessVerifyDataSource(ai,rds);
        
        verify.setCallBack(this);
        verify.setName("Access Verification");
        IProcessManager pm = ai.getProcessManager();
        pm.addProcess(verify);
        
        databasePath.setText("<<no database selected>>");
        
    }
}//GEN-LAST:event_connectActionPerformed

private String launchFileBrowser(){
    
    JFileChooser fc = null;
    
    String dataDIR = ApplicationDatabase.getCurrent().getSystemProperty(SystemProperties.CURRENT_DATA_DIR);
    if(!dataDIR.equals("")){
        //get a file and set it to be the chosen data directory
        File f = new File(dataDIR);
        
        if ((f.exists())&&(f.isDirectory())){
                //Create a file chooser at the prefered data directory
                fc = new JFileChooser(f);
        }else{
                //Create a new file chooser at the root
                fc = new JFileChooser();
        }
    }else{
        //Create a new file chooser at the root
        fc = new JFileChooser();
    }
    
    //set the file chooser to only accept the supported data sources
    fc.setFileFilter(new MSAccessFileFilter());

    //Get a return value to evaluate
    int returnVal = fc.showOpenDialog(ai.getMainApplicationFrame());
    //Set up a return string for the data source
    String sPath="";

    //Check to see if a selection was made.
    if(returnVal == JFileChooser.APPROVE_OPTION) {
        sPath=fc.getSelectedFile().getPath();
        ApplicationDatabase.getCurrent().setSystemProperty(SystemProperties.CURRENT_DATA_DIR,
                fc.getCurrentDirectory().getPath());
    }

    return sPath;
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browse;
    private javax.swing.JButton connect;
    private javax.swing.JTextField databasePath;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField password;
    private javax.swing.JCheckBox savePassword;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables

    @Override
    public void callBack() {

        if (verify.getSuccess().isValid()){
            ApplicationDatabase db = ApplicationDatabase.getCurrent();
            if(db.datasourceExists(rds.getFileName())){db.dropDatasource(rds.getFileName());}
            
            int scrID = db.getNextDatasourceID();
//            db.setDatasourceProperty(scrID, SystemProperties.RDS_UNIQUE_NAME, rds.getUniqueName());
            db.setDatasourceProperty(scrID, SystemProperties.DB_NAME, rds.getFileName());
            db.setDatasourceProperty(scrID, SystemProperties.DB_USER, rds.getUserName());
            db.setDatasourceProperty(scrID, SystemProperties.DB_TYPE, Integer.toString(DataAccessFactory.MS_ACCESS));
            if (rds.getPassword().equals("") || (savePassword.isSelected())){
                db.setDatasourceProperty(scrID, SystemProperties.DB_PSWD, rds.getPassword());
            }else if(!rds.getPassword().equals("") && (!savePassword.isSelected())){
                db.setDatasourceProperty(scrID, SystemProperties.DB_PSWD, SystemProperties.DB_PASSWORD_REQUIRED);
            }
            rds.setRdsID(scrID);
            rds.setDataType(DataAccessFactory.MS_ACCESS);
            
            RegisteredDataSources.getCurrent().addDataSource(rds);
            
        }else{
            if(verify.getCancel()){ai.writeToStatusWindow("Operation cancelled...",false);}
            ai.writeToStatusWindow("could not connect to datasource '" + rds.getFileName() + "'", false);
            
        }
    }

}
