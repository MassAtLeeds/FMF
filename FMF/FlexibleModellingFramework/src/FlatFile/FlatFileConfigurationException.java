
package FlatFile;

/**
 *
 * @author geo8kh
 */
public class FlatFileConfigurationException extends RuntimeException{
    FlatFileConfigurationException(){ super(); }

    FlatFileConfigurationException(String message){ super(message); }

 	FlatFileConfigurationException (String message, Throwable cause){ super(message, cause); }

 	FlatFileConfigurationException(Throwable cause){ super(cause); }

}
