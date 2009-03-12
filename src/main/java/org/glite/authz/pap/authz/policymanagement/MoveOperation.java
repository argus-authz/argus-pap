package org.glite.authz.pap.authz.policymanagement;

import java.util.List;

import org.glite.authz.pap.authz.BasePAPOperation;
import org.glite.authz.pap.authz.PAPPermission;
import org.glite.authz.pap.authz.PAPPermission.PermissionFlags;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.distribution.PAPManager;
import org.glite.authz.pap.repository.PAPContainer;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveOperation extends BasePAPOperation<Object> {
    
    private static final Logger log = LoggerFactory.getLogger(MoveOperation.class);
    
    String id;
    String pivotId;
    boolean moveAfter;

    protected MoveOperation(String id, String pivotId, boolean moveAfter) {
        this.id = id;
        this.pivotId = pivotId;
        this.moveAfter = moveAfter;
    }

    public static MoveOperation instance(String id, String pivotId, boolean moveAfter) {
        return new MoveOperation(id, pivotId, moveAfter);
    }

    protected Object doExecute() {
        
        PAPContainer localPAP = PAPManager.getInstance().getLocalPAPContainer();
        
        if (localPAP.hasPolicySet(id)) {
            
            movePolicySetId(localPAP);
            
        } else {
            
            movePolicyId(localPAP);
            
        }
        
        return null;
    }
    
    private void movePolicySetId(PAPContainer papContainer) {
        
        // now we have only two levels so... all the policy sets (resource <id>) are referenced by the PAP root policy set
        PolicySetType rootPAPPolicySet = TypeStringUtils.cloneAsPolicySetTypeString(papContainer.getPAPRootPolicySet());
        
        int pivotIndex = PolicySetHelper.getPolicySetIdReferenceIndex(rootPAPPolicySet, pivotId);
        
        if (pivotIndex == -1) {
            throw new RepositoryException("Id not found: " + pivotId);
        }
        
        log.debug("PolicySet pivot index is " + pivotIndex);
        
        if (!(PolicySetHelper.deletePolicySetReference(rootPAPPolicySet, id))) {
            throw new RepositoryException("Id not found: " + id);
        }
        
        if (moveAfter) {
            pivotIndex++; 
        }
        
        PolicySetHelper.addPolicySetReference(rootPAPPolicySet, pivotIndex, id);
        
        String version = rootPAPPolicySet.getVersion();
        
        PolicySetWizard.increaseVersion(rootPAPPolicySet);
        
        papContainer.updatePolicySet(version, rootPAPPolicySet);
    }
    
    private void movePolicyId(PAPContainer papContainer) {
        // get the target policy set
        PolicySetType targetPolicySet = null;
        List<PolicySetType> policySetList = papContainer.getAllPolicySets();
        int pivotIndex = -1;
        
        for (PolicySetType policySet : policySetList) {
            PolicySetType tempPolicySet = TypeStringUtils.cloneAsPolicySetTypeString(policySet);
            for (String s:PolicySetHelper.getPolicyIdReferencesValues(tempPolicySet)) {
                log.debug("PIPPO: " + s);
            }
            pivotIndex = PolicySetHelper.getPolicyIdReferenceIndex(tempPolicySet, pivotId);
            if (pivotIndex != -1) {
                log.debug("Policy pivot index is " + pivotIndex);
                targetPolicySet = tempPolicySet;
            }
        }
        
        if (targetPolicySet == null) {
            throw new RepositoryException("Id not found: " + pivotId);
        }

        if (!(PolicySetHelper.deletePolicyReference(targetPolicySet, id))) {
            throw new RepositoryException("Id not found: " + id);
        }
        
        if (moveAfter) {
            pivotIndex++;
        }
        
        PolicySetHelper.addPolicyReference(targetPolicySet, pivotIndex, id);
        
        String version = targetPolicySet.getVersion();
        
        PolicySetWizard.increaseVersion(targetPolicySet);
        
        papContainer.updatePolicySet(version, targetPolicySet);
    }

    @Override
    protected void setupPermissions() {
        addRequiredPermission(PAPPermission.of(PermissionFlags.POLICY_WRITE));
    }
}
