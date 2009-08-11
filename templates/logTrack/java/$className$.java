package $packageName$;

import org.hyperic.hq.product.LogFileTailPlugin;
import org.hyperic.hq.product.TrackEvent;
import org.hyperic.sigar.FileInfo;

/**
 * 
 *
 */
public class $className$ 
	extends LogFileTailPlugin {

	/**
	 * 
	 */
	public $className$() {
		
	}
	
	/**
	 * 
	 * @param info
	 * @param line
	 * @return
	 */
	public TrackEvent processLine(FileInfo info, String line) {
		return null;
	}
	
}