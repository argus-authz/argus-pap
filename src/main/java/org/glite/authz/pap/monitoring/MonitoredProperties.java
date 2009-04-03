package org.glite.authz.pap.monitoring;

import java.util.Map;
import java.util.TreeMap;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.PAPVersion;

/**
 * 
 * Creates a runtime snapshot of pap monitored properties.
 *
 */
public class MonitoredProperties {

    /**
     * Number of policies property name
     */
    public static final String NUM_OF_POLICIES_PROP_NAME = "NumOfPolicies";
    /**
     * Number of local policies property name
     */
    public static final String NUM_OF_LOCAL_POLICIES_PROP_NAME = "NumOfLocalPolicies";
    
    /**
     * Number of remote policies property name
     */
    public static final String NUM_OF_REMOTE_POLICIES_PROP_NAME = "NumOfRemotePolicies";
    /**
     * Policy last modification time property name
     */
    public static final String POLICY_LAST_MODIFICATION_TIME_PROP_NAME = "PolicyLastModificationTime";
    /**
     * Service startup time property name
     */
    public static final String SERVICE_STARTUP_TIME_PROP_NAME = "ServiceStartupTime";
    /**
     * Status property name
     */
    public static final String STATUS_PROP_NAME = "Status";
    /**
     * Used memory property name
     */
    public static final String USED_MEMORY_PROP_NAME = "UsedMemory";
    /**
     * Max memory property name
     */
    public static final String MAX_MEMORY_PROP_NAME = "MaxMemory";
    /**
     * Pap version property name
     */
    public static final String PAP_VERSION_PROP_NAME = "PapVersion";
    

    /**
     * The map that holds the monitored properties
     */
    TreeMap<String, String> props;

    /**
     * Constructor
     */
    private MonitoredProperties() {

        props = new TreeMap<String, String>();

        PAPConfiguration conf = PAPConfiguration.instance();

        props.put( PAP_VERSION_PROP_NAME, PAPVersion.instance().getVersion() );
        props.put(STATUS_PROP_NAME, "OK");
        props.put(SERVICE_STARTUP_TIME_PROP_NAME, conf.getMonitoringProperty(SERVICE_STARTUP_TIME_PROP_NAME).toString());

        props.put(NUM_OF_POLICIES_PROP_NAME, conf.getMonitoringProperty(NUM_OF_POLICIES_PROP_NAME).toString());
        props.put(NUM_OF_LOCAL_POLICIES_PROP_NAME, conf.getMonitoringProperty(NUM_OF_LOCAL_POLICIES_PROP_NAME).toString());
        props.put(NUM_OF_REMOTE_POLICIES_PROP_NAME, conf.getMonitoringProperty(NUM_OF_REMOTE_POLICIES_PROP_NAME).toString());
        props.put(POLICY_LAST_MODIFICATION_TIME_PROP_NAME, conf.getMonitoringProperty(POLICY_LAST_MODIFICATION_TIME_PROP_NAME)
                .toString());

        Runtime r = Runtime.getRuntime();
        props.put(USED_MEMORY_PROP_NAME, "" + r.totalMemory() + " bytes");
        props.put(MAX_MEMORY_PROP_NAME, "" + r.maxMemory() + " bytes");

    }

    /**
     * 
     * @return an updated instance of {@link MonitoredProperties}
     */
    public static MonitoredProperties instance() {

        return new MonitoredProperties();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String> e : props.entrySet())
            builder.append(e.getKey() + ": " + e.getValue() + "\n");

        return builder.toString();
    }

}
