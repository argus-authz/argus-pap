package org.glite.authz.pap.repository.monitoring.impl;

import org.glite.authz.pap.repository.monitoring.PolicyChangeEvent;
import org.glite.authz.pap.repository.monitoring.PolicyChangeListener;
import org.glite.authz.pap.repository.monitoring.PolicySetChangeEvent;
import org.glite.authz.pap.repository.monitoring.PolicySetChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogPolicyChangeMonitor implements PolicyChangeListener, PolicySetChangeListener{

    
    private static LogPolicyChangeMonitor instance = new LogPolicyChangeMonitor();
        
    public static LogPolicyChangeMonitor instance() {

        return instance;
    }
    Logger logger = LoggerFactory.getLogger( LogPolicyChangeMonitor.class );
    
    
    public void policyChange( PolicyChangeEvent ev ) {

        logger.info( "Policy changed: {}", ev );
        
        
    }

    public void policySetChange( PolicySetChangeEvent e ) {

        // TODO Auto-generated method stub
        
    }
    
    private LogPolicyChangeMonitor() {    
    }

}
