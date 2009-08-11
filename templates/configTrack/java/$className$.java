package $packageName$;

import java.io.File;
import org.hyperic.hq.product.ConfigFileTrackPlugin;

/**
 * 
 *
 */
public class $className$ 
	extends ConfigFileTrackPlugin {

	/**
	 * 
	 */
	public $className$() {
		
	}
	
    public String getDefaultConfigFile(String installPath) {
        String conf = getTypeProperty("DEFAULT_CONF");
        return new File(installPath, conf).getAbsolutePath();
    }
    
    
	
}