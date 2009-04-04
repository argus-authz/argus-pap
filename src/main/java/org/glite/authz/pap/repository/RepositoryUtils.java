package org.glite.authz.pap.repository;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.papmanagement.PAPContainer;
import org.glite.authz.pap.papmanagement.PAPManager;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryUtils {

    private static final Logger log = LoggerFactory.getLogger(RepositoryUtils.class);

    public static boolean checkForLoops(boolean repair) {
        boolean result = true;
        
        for (PAPContainer papContainer : PAPContainer.getContainers(PAPManager.getInstance().getAllPAPs())) {
            if (checkForLoops(papContainer, papContainer.getPAPRootPolicySet(), new LinkedList<String>(), repair) == false) {
                result = false;
            }
        }
        
        return result;
    }
    
    public static boolean checkXMLValidation() {

        List<PAPContainer> containerList = PAPContainer.getContainers(PAPManager.getInstance().getLocalPAPs());

        for (PAPContainer papContainer : containerList) {

            try {
                papContainer.getAllPolicySets();
                papContainer.getAllPolicies();
            } catch (RepositoryException e) {
                log.error("Repository checkXMLValidation() failed: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static boolean checkXMLValidationInDetail(boolean repair) {
        
        boolean result = true;

        for (PAPContainer papContainer : PAPContainer.getContainers(PAPManager.getInstance().getLocalPAPs())) {

            PolicySetType rootPS;
            
            try {
                rootPS = papContainer.getPAPRootPolicySet();
            } catch (RepositoryException e) {
                String rootAlias = papContainer.getPAP().getAlias();
                String action = String.format("remove all policies and and policy sets for root policy set %s", rootAlias);
                log.error(String.format("The root policy set %s (id=%s) is corrupted. Suggested action: %s", rootAlias, action));
                
                if (repair) {
                    log.info("Automatic repair is set. Action: " + action);
                    papContainer.deleteAllPolicies();
                    papContainer.deleteAllPolicySets();
                    papContainer.createRootPolicySet();
                    log.info("Automatic repair successfully completed (deleted root policy set " + rootAlias + ")");
                } else {
                    result = false;
                }
                continue;
            }

            // check referenced policy sets (the root policy set doesn't have references to policies)
            for (String policySetId : PolicySetHelper.getPolicySetIdReferencesValues(rootPS)) {
                PolicySetType policySet;
                
                try {
                    policySet = papContainer.getPolicySet(policySetId);
                } catch (RepositoryException e) {
                    String action = String.format("remove policy set %s", policySetId);
                    log.error(String.format("The policy set \"%s\" is corrupted. Suggested action: %s", policySetId, action));
                    
                    if (repair) {
                        log.info("Automatic repair is set. Action: " + action);
                        PolicySetHelper.deletePolicySetReference(rootPS, policySetId);
                        String version = rootPS.getVersion();
                        PolicySetWizard.increaseVersion(rootPS);
                        papContainer.updatePolicySet(version, rootPS);
                        papContainer.deletePolicySet(policySetId);
                        log.info("Automatic repair successfully completed (deleted policy set " + policySetId + ")");
                    } else {
                        result = false;
                    }
                    continue;
                }
                
                // check referenced policies (non-root policy sets don't have references to policy sets)
                for (String policyId : PolicySetHelper.getPolicyIdReferencesValues(policySet)) {
                    
                    try {
                        papContainer.getPolicy(policyId);
                    } catch (RepositoryException e) {
                        String action = String.format("remove policy %s", policyId);
                        log.error(String.format("The policy \"%s\" is corrupted. Suggested action: %s", policyId, action));
                        
                        if (repair) {
                            log.info("Automatic repair is set. Action: " + action);
                            PolicySetHelper.deletePolicyReference(policySet, policyId);
                            String version = policySet.getVersion();
                            PolicySetWizard.increaseVersion(policySet);
                            papContainer.updatePolicySet(version, policySet);
                            papContainer.deletePolicy(policyId);
                            log.info("Automatic repair successfully completed (deleted policy " + policyId + ")");
                        } else {
                            result = false;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static boolean performAllChecks(boolean repair) {
        
        boolean success = true;
        
        if (checkXMLValidation() == false) {
            log.info("Starting detailed check for XML validation");
            success = checkXMLValidationInDetail(repair);
        }
        
        if (success == true) {
            log.info("Starting check for loops");
            success = checkForLoops(repair);
        }
        
        if (success == true) {
            log.info("Starting purge of unreferenced objects");
            purgeUnreferencesPolicySets();
            purgeUnreferencesPolicies();
        }
        
        return success;
    }
    
    public static void purgeUnreferencesPolicies() {
        for (PAPContainer papContainer : PAPContainer.getContainers(PAPManager.getInstance().getLocalPAPs())) {
            papContainer.purgeUnreferencesPolicies();
        }
    }

    public static void purgeUnreferencesPolicySets() {
        for (PAPContainer papContainer : PAPContainer.getContainers(PAPManager.getInstance().getLocalPAPs())) {
            papContainer.purgeUnreferencedPolicySets();
        }
    }
    
    private static boolean checkForLoops(PAPContainer papContainer, PolicySetType policySet, List<String> forbiddenIdList, boolean repair) {
        
        boolean result = true;
        
        String policySetId = policySet.getPolicySetId();
        
        forbiddenIdList.add(policySetId);
        
        for (String id : forbiddenIdList) {
            if (PolicySetHelper.hasPolicySetReferenceId(policySet, id)) {
                String action = String.format("remove reference to policy set %s inside policy set %s", id, policySetId);
                log.error(String.format("The policy set \"%s\" contains a loop. Suggested action: %s", policySetId, action));
                
                if (repair) {
                    log.info("Automatic repair is set. Action: " + action);
                    PolicySetHelper.deletePolicySetReference(policySet, id);
                    String version = policySet.getVersion();
                    PolicySetWizard.increaseVersion(policySet);
                    papContainer.updatePolicySet(version, policySet);
                    log.info("Automatic repair successfully completed for policy set " + policySetId);
                } else {
                    result = false;
                }
            }
        }
        
        List<String> policySetIdList = PolicySetHelper.getPolicySetIdReferencesValues(policySet);
        
        TypeStringUtils.releaseUnneededMemory(policySet);
        
        for (String id : policySetIdList) {
            List<String> newForbiddenList = new LinkedList<String>();
            for (String s : forbiddenIdList) {
                newForbiddenList.add(s);
            }
            result = result && checkForLoops(papContainer, papContainer.getPolicySet(id), newForbiddenList, repair);
        }
        
        return result;
    }
}
