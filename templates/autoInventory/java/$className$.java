package $packageName$;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.hyperic.hq.product.AutoServerDetector;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.ServerDetector;
import org.hyperic.hq.product.ServerResource;
import org.hyperic.hq.product.ServiceResource;
import org.hyperic.util.config.ConfigResponse;

/**
 * This class implements simplest form of autodiscovery
 * process. Classes which implements AutoServerDetector
 * interface are used if plug-in xml descriptor contains
 * 'plugin' tag with autodiscovery attribute type and
 * class attribute points to this class.
 * 
 * This implementation is using PTQL query to find collection
 * of processes. If at least one process is found, server is
 * created with multiprocess metrics.
 * 
 * To monitor more specifically these processes, process
 * service type is created with every unique process. Unique
 * process is determined by comparing process name. If more 
 * than one process is found with similar names, those processes
 * are not used to create underlying process service.
 */

public class $className$ 
	extends ServerDetector
	implements AutoServerDetector {
	
	/** PTQL query to find matching processes */
    private static final String PTQL_QUERY = "$ptqlName$";

    /**
     * Default constructor.
     */
	public $className$() {
		super();
		// nothing to do
	}

	/**
     * Returns list of ServerResource objects. 
     * 
     * @param config Handle to platform configuration.
     * @return List of ServerResource objects.
     * @throws PluginException Throws PluginException if error occurs during resource creation.
	 *
	 * @see org.hyperic.hq.product.AutoServerDetector#getServerResources(org.hyperic.util.config.ConfigResponse)
	 */
	public List getServerResources(ConfigResponse config) throws PluginException {
        List servers = new ArrayList();
        List paths = getServerProcessList();

        // if no processes found, return empty list.
        // this means we couldn't discover anything.
        if(paths.size() < 1)
        	return servers;
        
        // using empty installation path,
        // since we don't need it anywhere
        String installPath = "";
        
        ConfigResponse productConfig = new ConfigResponse();
        
        // need to save original query to config.
        // this can be later altered through hq gui.
        productConfig.setValue("process.query", PTQL_QUERY);
        ServerResource server = createServerResource(installPath);
        setProductConfig(server, productConfig);
        server.setMeasurementConfig();
        servers.add(server);
        
        return servers;
	}
	
	/**
     * Returns list of ServiceResource objects. 
     * 
     * @param config Handle to platform configuration.
     * @return List of ServiceResource objects.
     * @throws PluginException Throws PluginException if error occurs during resource creation.
	 * @see org.hyperic.hq.product.ServerDetector#discoverServices(org.hyperic.util.config.ConfigResponse)
	 */
    protected List discoverServices(ConfigResponse config) throws PluginException {
    	List services = new ArrayList();
    	
    	// this hashtable is used to store information how many
    	// processes are found with similar name.
    	Hashtable<String, Integer>processes = new Hashtable<String, Integer>();
    	long[] pids = getPids(PTQL_QUERY);
		for (int i=0; i<pids.length; i++) {
            String args[] = getProcArgs(pids[i]);
			if(args != null && args.length > 0) {
				
				// we get null if key not found
				Integer value = processes.get(args[0]);
				int count = 1;
				
				// if we got a value, increment by one
				if(value != null)
					count = value.intValue() + 1;
				
				// insert/update hashtable
				processes.put(args[0], new Integer(count));				
			}
		}

		// we go through hashtable to skip processes which
		// were discovered as duplicates. we need to do this
		// since process metrics are assuming single process
		// returned by PTQL query.
		Enumeration<String> keys = processes.keys();
		while(keys.hasMoreElements()) {
			String exeName = keys.nextElement();
			
			// only unique processes
			if(processes.get(exeName) == 1) {
	            ServiceResource service = new ServiceResource();
	            
	            // setting type which match name from xml description.
	            // this is a service type name.
	            service.setType(this, "Process");
	            service.setServiceName(exeName);
	            ConfigResponse productConfig = new ConfigResponse();
	            
	            // set PTQL query to match unique process name
	            String ptql = "State.Name.eq="+exeName;
	            productConfig.setValue("process.query", ptql);
	            service.setProductConfig(productConfig);
	            service.setMeasurementConfig();
	            services.add(service);
			}
			
		}

    	return services;
    }
    
	/**
	 * Helper method to query pids and translating
	 * it to real exe paths.
	 * 
	 * @return Exe paths found by PTQL query
	 */
	private List getServerProcessList() {
		List servers = new ArrayList();
		long[] pids = getPids(PTQL_QUERY);
		for (int i=0; i<pids.length; i++) {
			String exe = getProcExe(pids[i]);
			if (exe == null) {
				continue;
			}
			File binary = new File(exe);
			if (!binary.isAbsolute()) {
				continue;
			}
			servers.add(binary.getAbsolutePath());
		}
		return servers;
	}

}
