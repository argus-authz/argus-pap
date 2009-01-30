package org.glite.authz.pap.monitoring;

import java.util.Hashtable;
import java.util.Map;

import org.glite.authz.pap.common.PAPConfiguration;


public class MonitoredProperties {
       
    Hashtable <String, String> props;
    
    
    private MonitoredProperties() {

        props = new Hashtable <String, String>();
        
        props.put( "Status", "OK" );
        props.put("ServiceStartupTime", PAPConfiguration.instance().getMonitoringProperty( "ServiceStartupTime" ).toString());
        
        // FIXME: Set a meaningful values here
        props.put( "NumOfPolicies", "??" );
        props.put( "NumOfLocalPolicies", "??" );
        props.put( "NumOfRemotePolicies", "??" );
        props.put( "PolicyLastModificationTime", "??" );
        
        Runtime r = Runtime.getRuntime();
        props.put( "UsedMemory", ""+r.totalMemory()+"" );
        props.put( "MaxMemory", ""+r.maxMemory()+"");
        
        
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
