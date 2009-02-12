package org.glite.authz.pap.monitoring;

import java.util.Hashtable;
import java.util.Map;

import org.glite.authz.pap.common.PAPConfiguration;


public class MonitoredProperties {
       
    public static final String NUM_OF_POLICIES_PROP_NAME = "NumOfPolicies";
    public static final String NUM_OF_LOCAL_POLICIES_PROP_NAME = "NumOfLocalPolicies";
    public static final String NUM_OF_REMOTE_POLICIES_PROP_NAME = "NumOfRemotePolicies";
    public static final String POLICY_LAST_MODIFICATION_TIME_PROP_NAME = "PolicyLastModificationTime";
    
    public static final String SERVICE_STARTUP_TIME_PROP_NAME = "ServiceStartupTime";
    public static final String STATUS_PROP_NAME = "Status";
    public static final String USED_MEMORY_PROP_NAME = "UsedMemory";
    public static final String MAX_MEMORY_PROP_NAME = "MaxMemory";
    
    
    Hashtable <String, String> props;
    
    
    private MonitoredProperties() {

        props = new Hashtable <String, String>();
        
        PAPConfiguration conf = PAPConfiguration.instance();
        
        props.put( STATUS_PROP_NAME, "OK" );
        props.put(SERVICE_STARTUP_TIME_PROP_NAME, conf.getMonitoringProperty( SERVICE_STARTUP_TIME_PROP_NAME ).toString());
        
        // FIXME: Set a meaningful values here
        props.put( NUM_OF_POLICIES_PROP_NAME, conf.getMonitoringProperty( NUM_OF_POLICIES_PROP_NAME ).toString()  );
        props.put( NUM_OF_LOCAL_POLICIES_PROP_NAME, "??" );
        props.put( NUM_OF_REMOTE_POLICIES_PROP_NAME, "??" );
        props.put( POLICY_LAST_MODIFICATION_TIME_PROP_NAME, "??" );
        
        Runtime r = Runtime.getRuntime();
        props.put( USED_MEMORY_PROP_NAME, ""+r.totalMemory()+"" );
        props.put( MAX_MEMORY_PROP_NAME, ""+r.maxMemory()+"");
        
        
    }
        
    public static MonitoredProperties instance() {

        return new MonitoredProperties();
    }
    
    @Override
    public String toString() {
    
        StringBuilder builder = new StringBuilder();
        
        for (Map.Entry <String, String> e: props.entrySet())
            builder.append( e.getKey()+": "+e.getValue()+"\n" );
        
        return builder.toString();
    }
    

}
