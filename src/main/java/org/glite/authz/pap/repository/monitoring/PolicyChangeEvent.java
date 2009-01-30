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
