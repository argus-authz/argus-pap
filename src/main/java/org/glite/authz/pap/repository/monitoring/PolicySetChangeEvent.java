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
