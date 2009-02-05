package org.glite.authz.pap.repository.monitoring;

import java.util.EventListener;


public interface PolicyChangeListener extends EventListener{
    
    public void policyChange(PolicyChangeEvent ev);

}
