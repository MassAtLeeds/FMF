/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fmfstart;

/**
 *
 * @author geo8kh
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int status = 0;
        try{
            Runtime.getRuntime().exec("java -Xmx512M -jar FlexibleModellingFramework.jar");
        }catch(Exception e){
            e.printStackTrace();
            status = 1;
        }finally{
            System.exit(status);
        }
    }

}
