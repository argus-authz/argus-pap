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

package org.glite.authz.pap.repository.monitoring;

import java.util.EventObject;

import org.opensaml.xacml.policy.PolicySetType;


public class PolicySetChangeEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    PolicySetType parentPolicySet;
    PolicySetType oldPolicySetValue;
    PolicySetType newPolicySetValue;
    
    public PolicySetChangeEvent( Object source, PolicySetType oldPolicySet, PolicySetType newPolicySet, PolicySetType parentPolicySet) {

        super( source );
        setOldPolicySetValue( oldPolicySet );
        setNewPolicySetValue( newPolicySet );
        setParentPolicySet( parentPolicySet );
    }

    
    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
    
        return serialVersionUID;
    }

    
    /**
     * @return the parentPolicySet
     */
    public PolicySetType getParentPolicySet() {
    
        return parentPolicySet;
    }

    
    /**
     * @return the oldPolicySetValue
     */
    public PolicySetType getOldPolicySetValue() {
    
        return oldPolicySetValue;
    }

    
    /**
     * @return the newPolicySetValue
     */
    public PolicySetType getNewPolicySetValue() {
    
        return newPolicySetValue;
    }


    
    /**
     * @param parentPolicySet the parentPolicySet to set
     */
    protected void setParentPolicySet( PolicySetType parentPolicySet ) {
    
        this.parentPolicySet = parentPolicySet;
    }


    
    /**
     * @param oldPolicySetValue the oldPolicySetValue to set
     */
    protected void setOldPolicySetValue( PolicySetType oldPolicySetValue ) {
    
        this.oldPolicySetValue = oldPolicySetValue;
    }


    
    /**
     * @param newPolicySetValue the newPolicySetValue to set
     */
    protected void setNewPolicySetValue( PolicySetType newPolicySetValue ) {
    
        this.newPolicySetValue = newPolicySetValue;
    }
    
    

}
