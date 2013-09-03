/*
 * This is the new license...
 * It has been edited
 */

package MSAccess;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author Kirk Harland <k.harland98@leeds.ac.uk>
 */
public class MSAccessFileFilter extends FileFilter{

    public boolean accept(File f){
        String name = f.getName();

	if (f.isDirectory()){
            return true;
	}else if (!name.equals("")){
            if (name.length()>4){
                if ((name.endsWith(".mdb")) ||
                    (name.endsWith(".MDB"))){
                    return true;
                }
            }
            
            if (name.length()>6){
                if ((name.endsWith(".accdb")) ||
                     (name.endsWith(".AACDB"))){
                    return true;
                }
            }
           
            return false;
	}
        
        return false;
        
    }
    
    public String getDescription(){
        return "Microsoft Access Databases";
    }
            
    
}
