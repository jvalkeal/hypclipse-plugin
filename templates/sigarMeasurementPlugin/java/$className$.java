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
public class $className$ 
	extends SigarMeasurementPlugin {

	/**
	 * 
	 */
	public $className$() {
		super();
	}

	/**
	 * 
	 * @param props
	 * @throws MetricNotFoundException
	 */
    public void getQueryInfo(Properties props) throws MetricNotFoundException {
    	
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
		
		String domain = metric.getDomainName();
		String attr = metric.getAttributeName();

		if (domain.equals("sigar.ptql")) {
			return super.getValue(metric);
		}

		// rndc statistic
		Double val = (Double)queryInfo.get(attr);
		if (val == null) {
			// not yet cached
			getQueryInfo(metric.getProperties());
			val = (Double)queryInfo.get(attr);
			if (val == null) {
				throw new MetricNotFoundException("No metric mapped to " +
						" metric: " + attr);
			}
		}

		// remove the metric from the cache to force a refresh
		// next time around
		queryInfo.remove(attr);

		return new MetricValue(val.doubleValue(), System.currentTimeMillis());
	}
    
	
}