package org.glite.authz.pap.papmanagement;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.xacml.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.monitoring.MonitoredProperties;
import org.glite.authz.pap.repository.RepositoryManager;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PapContainer {

    public static final Object highLevelOperationLock = new Object();

    private static final Logger log = LoggerFactory.getLogger(PapContainer.class);

    private final Pap pap;
    private final String papId;
    private final PolicyDAO policyDAO;
    private final PolicySetDAO policySetDAO;
    private final String rootPolicySetId;

    public PapContainer(Pap pap) {
        this.pap = pap;
        papId = pap.getId();
        rootPolicySetId = papId;
        policySetDAO = DAOFactory.getDAOFactory().getPolicySetDAO();
        policyDAO = DAOFactory.getDAOFactory().getPolicyDAO();
    }

    public static List<PapContainer> getContainers(List<Pap> papList) {

        List<PapContainer> papContainerList = new ArrayList<PapContainer>(papList.size());

        for (Pap pap : papList) {
            papContainerList.add(new PapContainer(pap));
        }
        return papContainerList;
    }

    public void addPolicy(int index, String policySetId, PolicyType policy) {

        if (!policySetDAO.exists(papId, policySetId)) {
            throw new NotFoundException("PolicySetId \"" + policySetId + "\" not found");
        }

        String policyId = policy.getPolicyId();

        policyDAO.store(papId, policy);
        
        int numberOfRules = policy.getRules().size();
        
        TypeStringUtils.releaseUnneededMemory(policy);

        PolicySetType policySet = policySetDAO.getById(papId, policySetId);

        if (PolicySetHelper.referenceIdExists(policySet, policyId)) {
            throw new AlreadyExistsException("Reference id \"" + policyId + "\" alredy exists");
        }

        if (index < 0) {
            PolicySetHelper.addPolicyReference(policySet, policyId);
        } else {
            PolicySetHelper.addPolicyReference(policySet, index, policyId);
        }

        String oldVersion = policySet.getVersion();
        PolicySetWizard.increaseVersion(policySet);

        try {
            policySetDAO.update(papId, oldVersion, policySet);
        } catch (RepositoryException e) {
            policyDAO.delete(papId, policyId);
            throw e;
        }

        TypeStringUtils.releaseUnneededMemory(policySet);

        updatePAPPolicyLastModificationTime();

        notifyPoliciesAdded(numberOfRules);
    }

    public void addPolicySet(int index, PolicySetType policySet) {

        String policySetId = policySet.getPolicySetId();

        policySetDAO.store(papId, policySet);

        PolicySetType rootPolicySet = policySetDAO.getById(papId, rootPolicySetId);

        if (PolicySetHelper.referenceIdExists(rootPolicySet, policySetId)) {
            throw new AlreadyExistsException("Reference id \"" + policySetId + "\" alredy exists");
        }

        if (index < 0) {
            PolicySetHelper.addPolicySetReference(rootPolicySet, policySetId);
        } else {
            PolicySetHelper.addPolicySetReference(rootPolicySet, index, policySetId);
        }

        String oldVersion = rootPolicySet.getVersion();
        PolicySetWizard.increaseVersion(rootPolicySet);

        try {
            policySetDAO.update(papId, oldVersion, rootPolicySet);
        } catch (RepositoryException e) {
            policySetDAO.delete(papId, policySetId);
            throw e;
        }

        TypeStringUtils.releaseUnneededMemory(rootPolicySet);

        updatePAPPolicyLastModificationTime();
    }

    public void createRootPolicySet() {

        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget(pap.getId(),
                                                                         PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        rootPolicySet.setVersion("0");

        policySetDAO.store(papId, rootPolicySet);
    }

    public void deleteAllPolicies() {
        // get the number of rules
        List<PolicyType> policyList = policyDAO.getAll(papId);
        int numberOfRules = 0;
        for (PolicyType policy : policyList) {
            numberOfRules += policy.getRules().size();
            TypeStringUtils.releaseUnneededMemory(policy);
        }
        policyDAO.deleteAll(papId);
        
        updatePAPPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRules);
    }

    public void deleteAllPolicySets() {
        policySetDAO.deleteAll(papId);
    }

    public void deletePolicy(String id) throws NotFoundException, RepositoryException {
        PolicyType policy = policyDAO.getById(papId, id);
        
        int numberOfRules = policy.getRules().size();
        
        policyDAO.delete(papId, id);
        
        updatePAPPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRules);
    }

    public void deletePolicySet(String id) throws NotFoundException, RepositoryException {
        policySetDAO.delete(papId, id);
    }

    public List<PolicyType> getAllPolicies() {
        return policyDAO.getAll(papId);
    }

    public List<PolicySetType> getAllPolicySets() {
        List<PolicySetType> policySetList = policySetDAO.getAll(papId);

        // place the PAP root PolicySet as the first element
        for (PolicySetType policySetElement : policySetList) {

            if (policySetElement.getPolicySetId().equals(rootPolicySetId)) {

                int currentIndex = policySetList.indexOf(policySetElement);

                if (currentIndex != 0) { // swap elements
                    PolicySetType tempPolicySet = policySetList.get(0);
                    policySetList.set(0, policySetElement);
                    policySetList.set(currentIndex, tempPolicySet);
                }
                break;
            }
        }
        return policySetList;
    }

    public int getNumberOfPolicies() {
        
        List<PolicyType> policyList = policyDAO.getAll(papId);
        
        int numberOfRules = 0;
        
        for (PolicyType policy : policyList) {
            numberOfRules += policy.getRules().size();
            TypeStringUtils.releaseUnneededMemory(policy);
        }
        
        return numberOfRules;
    }

    public Pap getPAP() {
        return this.pap;
    }

    public PolicySetType getPAPRootPolicySet() {
        return policySetDAO.getById(papId, rootPolicySetId);
    }

    public String getPAPRootPolicySetId() {
        return rootPolicySetId;
    }

    public PolicyType getPolicy(String id) {
        return policyDAO.getById(papId, id);
    }

    public PolicySetType getPolicySet(String id) {
        return policySetDAO.getById(papId, id);
    }

    public boolean hasPolicy(String id) {
        return policyDAO.exists(papId, id);
    }

    public boolean hasPolicySet(String id) {
        return policySetDAO.exists(papId, id);
    }
    
    public void purgePoliciesWithNoRules() {
        List<PolicyType> policyList = policyDAO.getAll(papId);
        for (PolicyType policy : policyList) {
            if (policy.getRules().size() == 0) {
                removePolicyAndReferences(policy.getPolicyId());
            }
        }
    }
    
    public void purgePolicySetWithNoPolicies() {
        List<PolicySetType> policySetList = policySetDAO.getAll(papId);
        for (PolicySetType policySet : policySetList) {
            
            String policySetId = policySet.getPolicySetId();
            
            if (rootPolicySetId.equals(policySetId)) {
                continue;
            }
            
            if (policySet.getPolicyIdReferences().size() == 0) {
                removePolicySetAndReferences(policySetId);
            }
        }
    }

    public void purgeUnreferencedPolicySets() {
        
        Set<String> idSet = new HashSet<String>();

        PolicySetType rootPS = policySetDAO.getById(papId, rootPolicySetId);

        idSet.add(rootPS.getPolicySetId());

        for (String id : PolicySetHelper.getPolicySetIdReferencesValues(rootPS)) {
            idSet.add(id);
        }

        TypeStringUtils.releaseUnneededMemory(rootPS);

        for (PolicySetType policySet : policySetDAO.getAll(papId)) {

            String policySetId = policySet.getPolicySetId();

            if (!idSet.contains(policySetId)) {
                log.info("Purging policy set " + policySetId);
                policySetDAO.delete(papId, policySetId);
            }
        }
    }
    
    public void purgeUnreferencesPolicies() {

        Set<String> idSet = new HashSet<String>();

        for (PolicySetType policySet : policySetDAO.getAll(papId)) {

            List<String> idList = PolicySetHelper.getPolicyIdReferencesValues(policySet);

            TypeStringUtils.releaseUnneededMemory(policySet);

            for (String id : idList) {
                idSet.add(id);
            }
        }

        for (PolicyType policy : policyDAO.getAll(papId)) {
            String policyId = policy.getPolicyId();

            if (!idSet.contains(policyId)) {
                log.info("Purging policy " + policyId);
                policyDAO.delete(papId, policyId);
            }
        }
    }

    public void removePolicyAndReferences(String policyId) throws NotFoundException, RepositoryException {

        if (!policyDAO.exists(papId, policyId)) {
            throw new NotFoundException("PolicyId \"" + policyId + "\" does not exists");
        }

        List<PolicySetType> policySetList = policySetDAO.getAll(papId);

        for (PolicySetType policySet : policySetList) {

            if (PolicySetHelper.deletePolicyReference(policySet, policyId)) {

                String oldVersion = policySet.getVersion();
                PolicySetWizard.increaseVersion(policySet);

                policySetDAO.update(papId, oldVersion, policySet);

                TypeStringUtils.releaseUnneededMemory(policySet);
            }
        }

        PolicyType policy = policyDAO.getById(papId, policyId);
        
        int numberOfRules = policy.getRules().size();
        
        policyDAO.delete(papId, policyId);

        updatePAPPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRules);
    }

    /**
     * Remove a policy set from the PAP root policy set. Also the referenced policies are removed.
     * 
     * @param policySetId
     */
    public void removePolicySetAndReferences(String policySetId) {

        if (!policySetDAO.exists(papId, policySetId)) {
            throw new NotFoundException("PolicySetId \"" + policySetId + "\" does not exists");
        }

        PolicySetType rootPolicySet = policySetDAO.getById(papId, rootPolicySetId);

        if (PolicySetHelper.deletePolicySetReference(rootPolicySet, policySetId)) {

            String oldVersion = rootPolicySet.getVersion();
            PolicySetWizard.increaseVersion(rootPolicySet);

            policySetDAO.update(papId, oldVersion, rootPolicySet);

            TypeStringUtils.releaseUnneededMemory(rootPolicySet);
        }

        PolicySetType policySet = policySetDAO.getById(papId, policySetId);
        policySetDAO.delete(papId, policySetId);

        List<String> idList = PolicySetHelper.getPolicyIdReferencesValues(policySet);

        TypeStringUtils.releaseUnneededMemory(policySet);

        for (String policyId : idList) {
            
            PolicyType policy = policyDAO.getById(papId, policyId);
            int numberOfRules = policy.getRules().size();
            TypeStringUtils.releaseUnneededMemory(policy);
            
            policyDAO.delete(papId, policyId);
            notifyPoliciesDeleted(numberOfRules);
        }

        updatePAPPolicyLastModificationTime();
    }

    public void storePolicy(PolicyType policy) {
        policyDAO.store(papId, policy);
        
        int numberOfRules = policy.getRules().size();
        
        TypeStringUtils.releaseUnneededMemory(policy);
        
        updatePAPPolicyLastModificationTime();
        notifyPoliciesAdded(numberOfRules);
    }

    public void storePolicySet(PolicySetType policySet) {
        policySetDAO.store(papId, policySet);
    }

    public void updatePolicy(String version, PolicyType policy) {
        PolicyType oldPolicy = policyDAO.getById(papId, policy.getPolicyId());
        int numberOfRemovedRules = oldPolicy.getRules().size();
        TypeStringUtils.releaseUnneededMemory(oldPolicy);
        
        int numberOfAddedRules = policy.getRules().size();
        
        policyDAO.update(papId, version, policy);
        
        updatePAPPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRemovedRules);
        notifyPoliciesAdded(numberOfAddedRules);
    }

    public void updatePolicySet(String version, PolicySetType newPolicySet) {
        policySetDAO.update(papId, version, newPolicySet);
        updatePAPPolicyLastModificationTime();
    }

    private void notifyPoliciesAdded(int numOfAddedPolicies) {

        String propName;

        if (Pap.DEFAULT_PAP_ALIAS.equals(pap.getAlias())) {
            propName = MonitoredProperties.NUM_OF_LOCAL_POLICIES_PROP_NAME;
        } else {
            propName = MonitoredProperties.NUM_OF_REMOTE_POLICIES_PROP_NAME;
        }

        synchronized (this) {
            Integer numOfPoliciesInteger = (Integer) PAPConfiguration.instance().getMonitoringProperty(propName);

            if (numOfPoliciesInteger == null) {
                return;
            }

            int numOfPolicies = numOfPoliciesInteger.intValue() + numOfAddedPolicies;
            numOfPoliciesInteger = new Integer(numOfPolicies);
            PAPConfiguration.instance().setMonitoringProperty(propName, numOfPoliciesInteger);

            propName = MonitoredProperties.NUM_OF_POLICIES_PROP_NAME;

            numOfPoliciesInteger = (Integer) PAPConfiguration.instance().getMonitoringProperty(propName);
            numOfPolicies = numOfPoliciesInteger.intValue() + numOfAddedPolicies;
            numOfPoliciesInteger = new Integer(numOfPolicies);
            PAPConfiguration.instance().setMonitoringProperty(propName, numOfPoliciesInteger);
        }
    }

    private void notifyPoliciesDeleted(int numOfDeletedPolicies) {

        String propName;

        if (Pap.DEFAULT_PAP_ALIAS.equals(pap.getAlias())) {
            propName = MonitoredProperties.NUM_OF_LOCAL_POLICIES_PROP_NAME;
        } else {
            propName = MonitoredProperties.NUM_OF_REMOTE_POLICIES_PROP_NAME;
        }

        synchronized (this) {
            Integer numOfPoliciesInteger = (Integer) PAPConfiguration.instance().getMonitoringProperty(propName);

            if (numOfPoliciesInteger == null) {
                return;
            }

            int numOfPolicies = numOfPoliciesInteger.intValue() - numOfDeletedPolicies;
            numOfPoliciesInteger = new Integer(numOfPolicies);
            PAPConfiguration.instance().setMonitoringProperty(propName, numOfPoliciesInteger);

            propName = MonitoredProperties.NUM_OF_POLICIES_PROP_NAME;

            numOfPoliciesInteger = (Integer) PAPConfiguration.instance().getMonitoringProperty(propName);
            numOfPolicies = numOfPoliciesInteger.intValue() - numOfDeletedPolicies;
            numOfPoliciesInteger = new Integer(numOfPolicies);
            PAPConfiguration.instance().setMonitoringProperty(propName, numOfPoliciesInteger);
        }
    }

    private void notifyPolicyLastModificationTimeUpdate() {

        if (!Pap.DEFAULT_PAP_ALIAS.equals(pap.getAlias())) {
            return;
        }

        PAPConfiguration.instance().setMonitoringProperty(MonitoredProperties.POLICY_LAST_MODIFICATION_TIME_PROP_NAME,
                                                          pap.getPolicyLastModificationTimeInSecondsString());
    }

    private void updatePAPPolicyLastModificationTime() {
        pap.setPolicyLastModificationTime((new GregorianCalendar()).getTimeInMillis());
        RepositoryManager.getDAOFactory().getPapDAO().update(pap);
        notifyPolicyLastModificationTimeUpdate();
    }

}
