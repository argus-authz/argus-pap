package org.glite.authz.pap.repository.monitoring;

import java.util.EventListener;


public interface PolicySetChangeListener extends EventListener{
    
    public void policySetChange(PolicySetChangeEvent e);

}
