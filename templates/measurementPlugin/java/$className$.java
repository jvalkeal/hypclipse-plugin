package $packageName$;

import org.hyperic.hq.product.MeasurementPlugin;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.MetricNotFoundException;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.MetricValue;
import org.hyperic.hq.product.PluginException;

/**
 * 
 *
 */
public class $className$ extends MeasurementPlugin {

	/**
	 * 
	 */
	public $className$() {
		super();
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws PluginException
	 * @throws MetricNotFoundException
	 * @throws MetricUnreachableException
	 */
	public MetricValue getValue(Metric metric)
		throws 	PluginException,
				MetricNotFoundException,
				MetricUnreachableException {
		
		Double d = f.getValue(metric);
		return new MetricValue(d, System.currentTimeMillis());		
	}
    
	
}