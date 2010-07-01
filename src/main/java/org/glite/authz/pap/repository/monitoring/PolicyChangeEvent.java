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
import org.opensaml.xacml.policy.PolicyType;

public class PolicyChangeEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    PolicyType oldPolicyValue;

    PolicyType newPolicyValue;

    PolicySetType parentPolicySet;

    String papId;

    public PolicyChangeEvent( Object source, PolicyType oldValue,
            PolicyType newValue, PolicySetType parentPolicySet, String papId ) {

        super( source );

        setOldPolicyValue( oldValue );
        setNewPolicyValue( newValue );
        setParentPolicySet( parentPolicySet );
        setPapId( papId );

    }

    /**
     * @return the oldPolicyValue
     */
    public PolicyType getOldPolicyValue() {

        return oldPolicyValue;
    }

    /**
     * @param oldPolicyValue
     *            the oldPolicyValue to set
     */
    protected void setOldPolicyValue( PolicyType oldPolicyValue ) {

        this.oldPolicyValue = oldPolicyValue;
    }

    /**
     * @return the newPolicyValue
     */
    public PolicyType getNewPolicyValue() {

        return newPolicyValue;
    }

    /**
     * @param newPolicyValue
     *            the newPolicyValue to set
     */
    protected void setNewPolicyValue( PolicyType newPolicyValue ) {

        this.newPolicyValue = newPolicyValue;
    }

    /**
     * @return the parentPolicySet
     */
    public PolicySetType getParentPolicySet() {

        return parentPolicySet;
    }

    /**
     * @param parentPolicySet
     *            the parentPolicySet to set
     */
    protected void setParentPolicySet( PolicySetType parentPolicySet ) {

        this.parentPolicySet = parentPolicySet;
    }

    /**
     * @return the papId
     */
    public String getPapId() {

        return papId;
    }

    /**
     * @param papId
     *            the papId to set
     */
    protected void setPapId( String papId ) {

        this.papId = papId;
    }

    @Override
    public String toString() {

        return String
                .format(
                        "PolicyChangeEvent(source: %s,oldPolicyId: %s, newPolicyId: %s, parentPolicySetId: %s, papId: %s)",
                        oldPolicyValue.getPolicyId(), newPolicyValue
                                .getPolicyId(), parentPolicySet
                                .getPolicySetId(), papId );

    }

}
