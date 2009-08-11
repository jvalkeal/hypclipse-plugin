package $packageName$;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.hq.product.AutoServerDetector;
import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.ProductPlugin;
import org.hyperic.hq.product.ServerDetector;
import org.hyperic.hq.product.ServerResource;
import org.hyperic.util.config.ConfigResponse;

/**
 * 
 *
 */
public class $detectorClassName$ 
	extends ServerDetector
	implements AutoServerDetector {

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hq.product.AutoServerDetector#getServerResources(org.hyperic.util.config.ConfigResponse)
	 */
	public List getServerResources(ConfigResponse config) throws PluginException {
        List servers = new ArrayList();
        servers.add(getServer(config));
        return servers;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hyperic.hq.product.ServerDetector#discoverServices(org.hyperic.util.config.ConfigResponse)
	 */
    protected List discoverServices(ConfigResponse serverConfig) throws PluginException {
    	ArrayList services = new ArrayList();
    	return services;
    }
    
    /**
     * 
     * @param config
     * @return
     */
    private ServerResource getServer(ConfigResponse config) {
        ServerResource server = createServerResource("/");
        String fqdn = config.getValue(ProductPlugin.PROP_PLATFORM_FQDN);
        String type = getTypeInfo().getName();
        server.setName(fqdn + " " + type);
        server.setIdentifier(server.getName());
        server.setProductConfig();
        server.setMeasurementConfig();
        return server;
    }

}
