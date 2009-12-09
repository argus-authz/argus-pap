package org.glite.authz.pap.repository;

import java.util.LinkedList;
import java.util.List;

import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.papmanagement.PapContainer;
import org.glite.authz.pap.papmanagement.PapManager;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains utility functions for the repository.
 */
public class RepositoryUtils {

    private static final Logger log = LoggerFactory.getLogger(RepositoryUtils.class);

    /**
     * Checks the repository (all the local paps) for loops (i.e. policy sets containing circular
     * references.
     * 
     * @param repair if <code>true</code> loops are fixed by deleting lower level references, if
     *            <code>false</code> no action is performed and an error is logged.
     * @return <code>true</code> if the check is successful (i.e. no loops are found or some loop
     *         were found but they were successfully deleleted), <code>false</code> otherwise.
     */
    public static boolean checkForLoops(boolean repair) {
        boolean result = true;

        for (PapContainer papContainer : PapContainer.getContainers(PapManager.getInstance().getAllPaps())) {
            if (checkForLoops(papContainer,
                              papContainer.getRootPolicySet(),
                              new LinkedList<String>(),
                              repair) == false) {
                result = false;
            }
        }

        return result;
    }

    /**
     * Validates the XML of the policies in the repository (for the local paps). No log messages is
     * printed in case of problems.
     * 
     * @return <code>true</code> if the validation is successfully, <code>false</code> otherwise.
     */
    public static boolean checkXMLValidation() {

        List<PapContainer> containerList = PapContainer.getContainers(PapManager.getInstance().getLocalPaps());

        for (PapContainer papContainer : containerList) {

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

    /**
     * Validates the XML of the policies in the repository (for the local paps). In case of problems
     * detailed log messages are printed reporting which file is corrupted and the suggested action
     * to fix the problem.
     * 
     * @param repair if <code>true</code> in case of problems tries to fix the repository by
     *            applying the suggested action (i.e. by removing the corrupted policy and all its
     *            references)..
     * @return @return <code>true</code> if the validation is successfully or if the repository were
     *         successfully fixed, <code>false</code> otherwise.
     */
    public static boolean checkXMLValidationInDetail(boolean repair) {

        boolean result = true;

        for (PapContainer papContainer : PapContainer.getContainers(PapManager.getInstance().getLocalPaps())) {

            PolicySetType rootPS;

            try {
                rootPS = papContainer.getRootPolicySet();
            } catch (RepositoryException e) {
                String rootAlias = papContainer.getPap().getAlias();
                String action = String.format("remove all policies and and policy sets for root policy set %s",
                                              rootAlias);
                log.error(String.format("The root policy set %s (id=%s) is corrupted. Suggested action: %s",
                                        rootAlias,
                                        action));

                if (repair) {
                    log.info("Automatic repair is set. Action: " + action);
                    papContainer.deleteAllPolicies();
                    papContainer.deleteAllPolicySets();
                    papContainer.createRootPolicySet();
                    log.info("Automatic repair successfully completed (deleted root policy set " + rootAlias
                            + ")");
                } else {
                    result = false;
                }
                continue;
            }

            // check referenced policy sets (the root policy set doesn't have references to
            // policies)
            for (String policySetId : PolicySetHelper.getPolicySetIdReferencesValues(rootPS)) {
                PolicySetType policySet;

                try {
                    policySet = papContainer.getPolicySet(policySetId);
                } catch (RepositoryException e) {
                    String action = String.format("remove policy set %s", policySetId);
                    log.error(String.format("The policy set \"%s\" is corrupted. Suggested action: %s",
                                            policySetId,
                                            action));

                    if (repair) {
                        log.info("Automatic repair is set. Action: " + action);
                        PolicySetHelper.deletePolicySetReference(rootPS, policySetId);
                        papContainer.updatePolicySet(rootPS);
                        papContainer.deletePolicySet(policySetId);
                        log.info("Automatic repair successfully completed (deleted policy set " + policySetId
                                + ")");
                    } else {
                        result = false;
                    }
                    continue;
                }

                // check referenced policies (non-root policy sets don't have references to policy
                // sets)
                for (String policyId : PolicySetHelper.getPolicyIdReferencesValues(policySet)) {

                    try {
                        papContainer.getPolicy(policyId);
                    } catch (RepositoryException e) {
                        String action = String.format("remove policy %s", policyId);
                        log.error(String.format("The policy \"%s\" is corrupted. Suggested action: %s",
                                                policyId,
                                                action));

                        if (repair) {
                            log.info("Automatic repair is set. Action: " + action);
                            PolicySetHelper.deletePolicyReference(policySet, policyId);
                            papContainer.updatePolicySet(policySet);
                            papContainer.deletePolicy(policyId);
                            log.info("Automatic repair successfully completed (deleted policy " + policyId
                                    + ")");
                        } else {
                            result = false;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Perform all the consistency checks of the repository (for the local paps), in order:
     * <code>checkXMLValidation</code> (if the result is <code>false</code> then a
     * <code>checkXMLValidationInDetail</code>), <code>checkForLoops</code>,
     * <code>purgeUnreferencedPolicySets</code> and <code>purgeUnreferencedPolicies</code>.
     * 
     * @param repair if <code>true</code> problems are automatically fixed.
     * @return <code>true</code> on success (both no problems found or repository successfully
     *         fixed), <code>false</code> otherwise.
     */
    public static boolean performAllChecks(boolean repair) {

        PersistenceManager.getInstance().getCurrentSession().beginTransaction();
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
            purgeUnreferencedPolicySets();
            purgeUnreferencedPolicies();
        }
        
        PersistenceManager.getInstance().getCurrentSession().getTransaction().commit();

        return success;
    }

    /**
     * Delete policies without rules for all local paps.
     */
    public static void purgeUnreferencedPolicies() {
        for (PapContainer papContainer : PapContainer.getContainers(PapManager.getInstance().getLocalPaps())) {
            papContainer.purgeUnreferencesPolicies();
        }
    }

    /**
     * Delete policy sets without policies for all local paps.
     */
    public static void purgeUnreferencedPolicySets() {
        for (PapContainer papContainer : PapContainer.getContainers(PapManager.getInstance().getLocalPaps())) {
            papContainer.purgeUnreferencedPolicySets();
        }
    }

    /**
     * Checks if there are loops in the policy set of a specific pap.
     * 
     * @param papContainer check the policies of this pap.
     * @param policySet policy set belonging to the policies of <code>papContainer</code> to check
     *            loops for.
     * @param forbiddenIdList list of policy set ids that cannot appear inside this policy set and
     *            inside its children.
     * @param repair if <code>true</code> references to ids contained in the
     *            <code>forbiddenList</code> are deleted.
     * @return <code>true</code> if the check is successful (i.e. no loops are found or some loop
     *         were found but they were successfully deleleted), <code>false</code> otherwise.
     */
    private static boolean checkForLoops(PapContainer papContainer, PolicySetType policySet,
            List<String> forbiddenIdList, boolean repair) {

        boolean result = true;

        String policySetId = policySet.getPolicySetId();

        forbiddenIdList.add(policySetId);

        for (String id : forbiddenIdList) {
            if (PolicySetHelper.hasPolicySetReferenceId(policySet, id)) {
                String action = String.format("remove reference to policy set %s inside policy set %s",
                                              id,
                                              policySetId);
                log.error(String.format("The policy set \"%s\" contains a loop. Suggested action: %s",
                                        policySetId,
                                        action));

                if (repair) {
                    log.info("Automatic repair is set. Action: " + action);
                    PolicySetHelper.deletePolicySetReference(policySet, id);
                    papContainer.updatePolicySet(policySet);
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
            result = result
                    && checkForLoops(papContainer, papContainer.getPolicySet(id), newForbiddenList, repair);
        }

        return result;
    }
}
