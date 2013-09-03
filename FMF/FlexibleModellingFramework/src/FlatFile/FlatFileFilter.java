
package FlatFile;


import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author Kirk Harland
 */

public class FlatFileFilter extends FileFilter{

        @Override
    public boolean accept(File f){
        String name = f.getName();

	if (f.isDirectory()){
            return true;
	}else if (!name.equals("")){
            if (name.length()>4){
                if ((name.endsWith(".txt")) ||
                    (name.endsWith(".TXT"))){
                    return true;
                }
                if ((name.endsWith(".csv")) ||
                    (name.endsWith(".CSV"))){
                    return true;
                }
            }

            return false;
	}

        return false;

    }

    @Override
    public String getDescription(){
        return "Flat File";
    }


}
