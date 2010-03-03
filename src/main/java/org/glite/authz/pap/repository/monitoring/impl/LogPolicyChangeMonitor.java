/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
