package org.glite.authz.pap.papmanagement;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.common.Pap;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.PolicySetHelper;
import org.glite.authz.pap.monitoring.MonitoredProperties;
import org.glite.authz.pap.repository.dao.DAOFactory;
import org.glite.authz.pap.repository.dao.PapDAO;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the content (the policies) of a <code>Pap</code>.
 * <p>
 * A pap has a root policy set which <i>id</i> is the same as the <i>pap id</i> (retrieved with {@link Pap#getId()}).
 * This policy set is the root of the tree of policies of the pap. There are specific methods to create and retrieve
 * this policy set ( {@link PapContainer#createRootPolicySet(), {@link PapContainer#getRootPolicySet()}).
 * 
 * @see PapManager
 * @see Pap
 * @see PapDAO
 */
public class PapContainer {

    private static final Logger log = LoggerFactory.getLogger(PapContainer.class);
    private static final Object notificationLock = new Object();

    private final Pap pap;
    private final String papId;
    private final PolicyDAO policyDAO;
    private final PolicySetDAO policySetDAO;
    private final String rootPolicySetId;

    /**
     * Constructor.
     * 
     * @param pap the <code>Pap</code> to be used as container.
     */

    public PapContainer(Pap pap) {
        this.pap = pap;
        papId = pap.getId();
        rootPolicySetId = papId;
        policySetDAO = DAOFactory.getDAOFactory().getPolicySetDAO();
        policyDAO = DAOFactory.getDAOFactory().getPolicyDAO();
    }

    /**
     * Convenience method to get a List of containers from a List of paps.
     * 
     * @param papList List of paps.
     * @return the List of containers of the given paps.
     */
    public static List<PapContainer> getContainers(List<Pap> papList) {

        List<PapContainer> papContainerList = new ArrayList<PapContainer>(papList.size());

        for (Pap pap : papList) {
            papContainerList.add(new PapContainer(pap));
        }
        return papContainerList;
    }

    /**
     * Adds a policy into a policy set.
     * 
     * @param index position of the policy to added inside the list of policies already present in the policy set.
     * @param policySetId the policy set id.
     * @param policy the policy to be added.
     * @throws NotFoundException if the policy set id was not found.
     * @throws AlreadyExistsException if there's already a policy with the same id of the given one.
     * @throws InvalidVersionException if there was a concurrent modification of the policy set. Getting this exception
     *             means that no policy has been added and the repository hasn't been modified nor corrupted.
     */
    public void addPolicy(int index, String policySetId, PolicyType policy) {

        if (!policySetDAO.exists(papId, policySetId)) {
            throw new NotFoundException("PolicySetId \"" + policySetId + "\" not found");
        }

        String policyId = policy.getPolicyId();

        policyDAO.store(papId, policy);

        int numberOfRules = policy.getRules().size();

        TypeStringUtils.releaseUnneededMemory(policy);

        PolicySetType policySet = getPolicySet(policySetId);

        if (PolicySetHelper.referenceIdExists(policySet, policyId)) {
            throw new AlreadyExistsException("Reference id \"" + policyId + "\" alredy exists");
        }

        if (index < 0) {
            PolicySetHelper.addPolicyReference(policySet, policyId);
        } else {
            PolicySetHelper.addPolicyReference(policySet, index, policyId);
        }

        TypeStringUtils.releaseUnneededMemory(policySet);

        updatePolicySet(policySet);

        updatePapPolicyLastModificationTime();

        notifyPoliciesAdded(numberOfRules);
    }

    /**
     * Adds a policy set into the root policy set of the pap.
     * 
     * @param index
     * @param policySet
     * @throws InvalidVersionException if there was a concurrent modification of the root policy set. Getting this
     *             exception means that no policy has been added and the repository hasn't been modified nor corrupted.
     */
    public void addPolicySet(int index, PolicySetType policySet) {

        String policySetId = policySet.getPolicySetId();

        policySetDAO.store(papId, policySet);

        PolicySetType rootPolicySet = getPolicySet(rootPolicySetId);

        if (PolicySetHelper.referenceIdExists(rootPolicySet, policySetId)) {
            throw new AlreadyExistsException("Reference id \"" + policySetId + "\" alredy exists");
        }

        if (index < 0) {
            PolicySetHelper.addPolicySetReference(rootPolicySet, policySetId);
        } else {
            PolicySetHelper.addPolicySetReference(rootPolicySet, index, policySetId);
        }

        TypeStringUtils.releaseUnneededMemory(rootPolicySet);
        
        updatePolicySet(rootPolicySet);

        updatePapPolicyLastModificationTime();
    }

    /**
     * Creates the pap root policy set.
     * 
     * @throws AlreadyExistsException if the pap root policy set already exists.
     */
    public void createRootPolicySet() {

        PolicySetType rootPolicySet = PolicySetHelper.buildWithAnyTarget(pap.getId(),
                                                                         PolicySetHelper.COMB_ALG_FIRST_APPLICABLE);
        rootPolicySet.setVersion("0");

        policySetDAO.store(papId, rootPolicySet);
    }

    /**
     * Delete all the policies of the pap.
     * <p>
     * Important: references of the deleted policies in policy sets are not deleted. See method
     * {@link PapContainer#removePolicyAndReferences(String)}.
     */
    public void deleteAllPolicies() {
        // get the number of rules
        List<PolicyType> policyList = getAllPolicies();
        int numberOfRules = 0;
        for (PolicyType policy : policyList) {
            numberOfRules += policy.getRules().size();
            TypeStringUtils.releaseUnneededMemory(policy);
        }

        policyDAO.deleteAll(papId);

        updatePapPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRules);
    }

    /**
     * Delete all the policy sets of the pap.
     * <p>
     * Important: references of the deleted policy sets in policy sets (i.e. the root policy set) are not deleted. See
     * method {@link PapContainer#removePolicySetAndReferences(String)}.
     */
    public void deleteAllPolicySets() {
        policySetDAO.deleteAll(papId);
    }

    public void deletePolicy(String id) throws NotFoundException, RepositoryException {
        
        PolicyType policy = getPolicy(id);

        int numberOfRules = policy.getRules().size();

        policyDAO.delete(papId, id);

        updatePapPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRules);
    }

    /**
     * Delete a policy of the pap.
     * <p>
     * Important: references of the deleted policy in policy sets are not deleted. See method
     * {@link PapContainer#removePolicyAndReferences(String)}.
     * 
     * @param id policy id of the policy to be deleted.
     * @throws NotFoundException if the given policy id was not found.
     */
    public void deletePolicySet(String id) throws NotFoundException, RepositoryException {
        policySetDAO.delete(papId, id);
    }

    /**
     * Returns a List of all the policies of the pap.
     * 
     * @return a List of all the policies of the pap.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public List<PolicyType> getAllPolicies() {
        
        List<PolicyType> policyList = new LinkedList<PolicyType>(); 
        List<PolicyType> repoPolicyList = policyDAO.getAll(papId);
        
        for (PolicyType repoPolicy : repoPolicyList) {
            policyList.add(TypeStringUtils.cloneAsPolicyTypeString(repoPolicy));
        }
        
        return policyList;
    }

    /**
     * Return a list of all the policy sets of the pap. The root policy set is the first element of the list.
     * 
     * @return a list of all the policy sets of the pap where the first element is the root policy set.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public List<PolicySetType> getAllPolicySets() {
        List<PolicySetType> repoPolicySetList = policySetDAO.getAll(papId);
        List<PolicySetType> policySetList = new LinkedList<PolicySetType>();
        
        // Book the first position for the root policy set
        policySetList.add(null);

        // place the root policy set as the first element
        for (PolicySetType repoPolicySet : repoPolicySetList) {

            PolicySetType policySet = TypeStringUtils.cloneAsPolicySetTypeString(repoPolicySet);
            
            if (rootPolicySetId.equals(policySet.getPolicySetId())) {

                policySetList.set(0, policySet);
                
            } else {
                
                policySetList.add(policySet);
            }
        }
        return policySetList;
    }

    /**
     * Returns the number of policies (i.e. number of rules) in the pap.
     * 
     * @return the number of policies (i.e. number of rules) in the pap.
     */
    public int getNumberOfPolicies() {

        List<PolicyType> policyList = getAllPolicies();

        int numberOfRules = 0;

        for (PolicyType policy : policyList) {
            numberOfRules += policy.getRules().size();
            TypeStringUtils.releaseUnneededMemory(policy);
        }

        return numberOfRules;
    }

    /**
     * Return the Pap object this container was build with.
     * 
     * @return the Pap associated to this container.
     */
    public Pap getPap() {
        return this.pap;
    }

    /**
     * Returns the root policy set of this pap.
     * 
     * @return the root policy set of this pap.
     */
    public PolicySetType getRootPolicySet() {
        return TypeStringUtils.cloneAsPolicySetTypeString(policySetDAO.getById(papId, rootPolicySetId));
    }

    /**
     * Returns the <i>id</i> of the root policy set of this pap.
     * 
     * @return the <i>id</i> of the root policy set.
     */
    public String getRootPolicySetId() {
        return rootPolicySetId;
    }

    /**
     * Get a policy by <i>id</i>.
     * 
     * @param id policy id to search for.
     * @return the policy with the given <i>id</i>.
     * @throws NotFoundException if no policy with the given id was found.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public PolicyType getPolicy(String id) {
        return TypeStringUtils.cloneAsPolicyTypeString(policyDAO.getById(papId, id));
    }

    /**
     * Get a policy set by <i>id</i>.
     * 
     * @param id policy set id to search for.
     * @return the policy set with the given <i>id</i>.
     * @throws NotFoundException if no policy set with the given id was found.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy set file).
     */
    public PolicySetType getPolicySet(String id) {
        return TypeStringUtils.cloneAsPolicySetTypeString(policySetDAO.getById(papId, id));
    }

    /**
     * Checks for the existence of a policy with the given id.
     * 
     * @param id policy id to search for.
     * @return <code>true</code> if a policy with the given id was found, <code>false</code> otherwise.
     */
    public boolean hasPolicy(String id) {
        return policyDAO.exists(papId, id);
    }

    /**
     * Checks for the existence of a policy set with the given id.
     * 
     * @param id policy set id to search for.
     * @return <code>true</code> if a policy set with the given id was found, <code>false</code> otherwise.
     */
    public boolean hasPolicySet(String id) {
        return policySetDAO.exists(papId, id);
    }

    /**
     * Delete all the policies with no rules. References to that policies are deleted too.
     * 
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public void purgePoliciesWithNoRules() {
        List<PolicyType> policyList = getAllPolicies();
        for (PolicyType policy : policyList) {
            if (policy.getRules().size() == 0) {
                removePolicyAndReferences(policy.getPolicyId());
            }
        }
    }

    /**
     * Delete all the policy sets with no policies. References to that policy sets are deleted too.
     * 
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public void purgePolicySetWithNoPolicies() {
        List<PolicySetType> policySetList = getAllPolicySets();
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

    /**
     * Delete all the policy sets found to be unreferenced (obviously the root policy set is not deleted).
     */
    public void purgeUnreferencedPolicySets() {

        Set<String> idSet = new HashSet<String>();

        PolicySetType rootPS = getRootPolicySet();

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

    /**
     * Delete all the policies found to be unreferenced.
     */
    public void purgeUnreferencesPolicies() {

        Set<String> idSet = new HashSet<String>();

        for (PolicySetType policySet : getAllPolicySets()) {

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

    /**
     * Delete a policy and all its references.
     * 
     * @param policyId the policy id of the policy to remove.
     * @throws NotFoundException if no policy with the given id was found.
     * @throws InvalidVersionException if there was a concurrent modification of the policy set. Getting this exception
     *             means that no policy has been added and the repository hasn't been modified nor corrupted.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public void removePolicyAndReferences(String policyId) {

        if (!policyDAO.exists(papId, policyId)) {
            throw new NotFoundException("PolicyId \"" + policyId + "\" does not exists");
        }

        boolean policyAlreadyRemoved = false;

        List<PolicySetType> policySetList = getAllPolicySets();

        for (PolicySetType policySet : policySetList) {

            if (PolicySetHelper.deletePolicyReference(policySet, policyId)) {

                if (policySet.getPolicyIdReferences().size() == 0) {

                    removePolicySetAndReferences(policySet.getPolicySetId());

                    policyAlreadyRemoved = true;

                } else {

                    String oldVersion = policySet.getVersion();

                    policySetDAO.update(papId, oldVersion, policySet);
                }

                TypeStringUtils.releaseUnneededMemory(policySet);
            }
        }

        if (policyAlreadyRemoved) {
            return;
        }

        PolicyType policy = getPolicy(policyId);

        int numberOfRules = policy.getRules().size();

        policyDAO.delete(papId, policyId);

        updatePapPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRules);
    }

    /**
     * Delete a policy set and all its references.
     * 
     * @param policySetId the policy set id of the policy set to remove.
     * @throws NotFoundException if no policy set with the given id was found.
     * @throws InvalidVersionException if there was a concurrent modification of the root policy set. Getting this
     *             exception means that no policy has been added and the repository hasn't been modified nor corrupted.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy set file).
     */
    public void removePolicySetAndReferences(String policySetId) {

        if (!policySetDAO.exists(papId, policySetId)) {
            throw new NotFoundException("PolicySetId \"" + policySetId + "\" does not exists");
        }

        PolicySetType rootPolicySet = getRootPolicySet();

        if (PolicySetHelper.deletePolicySetReference(rootPolicySet, policySetId)) {

            String oldVersion = rootPolicySet.getVersion();

            policySetDAO.update(papId, oldVersion, rootPolicySet);

            TypeStringUtils.releaseUnneededMemory(rootPolicySet);
        }

        PolicySetType policySet = getPolicySet(policySetId);
        policySetDAO.delete(papId, policySetId);

        List<String> idList = PolicySetHelper.getPolicyIdReferencesValues(policySet);

        TypeStringUtils.releaseUnneededMemory(policySet);

        for (String policyId : idList) {

            PolicyType policy = getPolicy(policyId);
            int numberOfRules = policy.getRules().size();
            TypeStringUtils.releaseUnneededMemory(policy);

            policyDAO.delete(papId, policyId);
            notifyPoliciesDeleted(numberOfRules);
        }

        updatePapPolicyLastModificationTime();
    }

    /**
     * Store a policy.
     * 
     * @param policy the policy to be stored.
     * @throws AlreadyExistsException if a policy with the same id was found.
     */
    public void storePolicy(PolicyType policy) {
        policyDAO.store(papId, policy);

        int numberOfRules = policy.getRules().size();

        TypeStringUtils.releaseUnneededMemory(policy);

        updatePapPolicyLastModificationTime();
        notifyPoliciesAdded(numberOfRules);
    }

    /**
     * Store a policy set.
     * 
     * @param policySet the policy set to be stored.
     * @throws AlreadyExistsException if a policy set with the same id was found.
     */
    public void storePolicySet(PolicySetType policySet) {
        policySetDAO.store(papId, policySet);
    }

    /**
     * Update a policy. The version of the policy must be the same as the one of the corresponding policy in the
     * repository. Before updating the version is increased.
     * 
     * @param version version of the policy in the repository to be updated.
     * @param policy new policy replacing the one with the same id.
     * @throws NotFoundException if a policy with the same id was not found.
     * @throws InvalidVersionException if there was a concurrent modification of the policy. Getting this exception
     *             means that no policy has been updated and the repository hasn't been modified nor corrupted.
     */
    public void updatePolicy(PolicyType policy) {

        String version = policy.getVersion();
        TypeStringUtils.releaseUnneededMemory(policy);

        PolicyType repoPolicy = getPolicy(policy.getPolicyId());

        int numberOfRemovedRules = repoPolicy.getRules().size();
        TypeStringUtils.releaseUnneededMemory(repoPolicy);

        int numberOfAddedRules = policy.getRules().size();

        policyDAO.update(papId, version, policy);

        updatePapPolicyLastModificationTime();
        notifyPoliciesDeleted(numberOfRemovedRules);
        notifyPoliciesAdded(numberOfAddedRules);
    }

    /**
     * Update a policy set. The version of the policy set must be the same as the one of the corresponding policy set in
     * the repository. Before updating the version is increased.
     * 
     * @param version version of the policy set in the repository to be updated.
     * @param policySet new policy set replacing the one with the same id.
     * @throws NotFoundException if a policy set with the same id was not found.
     * @throws InvalidVersionException if there was a concurrent modification of the policy set. Getting this exception
     *             means that no policy set has been updated and the repository hasn't been modified nor corrupted.
     */
    public void updatePolicySet(PolicySetType policySet) {
        
        String version = policySet.getVersion();
        TypeStringUtils.releaseUnneededMemory(policySet);
        
        policySetDAO.update(papId, version, policySet);
        
        updatePapPolicyLastModificationTime();
    }

    /**
     * Notifies that some policies have been added.
     * 
     * @param numOfAddedPolicies number of added policies.
     */
    private void notifyPoliciesAdded(int numOfAddedPolicies) {

        String propName;

        if (pap.isLocal()) {
            propName = MonitoredProperties.NUM_OF_LOCAL_POLICIES_PROP_NAME;
        } else {
            propName = MonitoredProperties.NUM_OF_REMOTE_POLICIES_PROP_NAME;
        }

        synchronized (notificationLock) {
            Integer numOfPoliciesInteger = (Integer) PAPConfiguration.instance()
                                                                     .getMonitoringProperty(propName);

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

    /**
     * Notifies that some policies have been deleted.
     * 
     * @param numOfDeletedPolicies number of deleted policies.
     */
    private void notifyPoliciesDeleted(int numOfDeletedPolicies) {

        String propName;

        if (pap.isLocal()) {
            propName = MonitoredProperties.NUM_OF_LOCAL_POLICIES_PROP_NAME;
        } else {
            propName = MonitoredProperties.NUM_OF_REMOTE_POLICIES_PROP_NAME;
        }

        synchronized (notificationLock) {
            Integer numOfPoliciesInteger = (Integer) PAPConfiguration.instance()
                                                                     .getMonitoringProperty(propName);

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

    /**
     * Notifies the time of last policy modification.
     */
    private void notifyPolicyLastModificationTimeUpdate() {

        if (pap.isRemote()) {
            return;
        }

        synchronized (notificationLock) {
            PAPConfiguration.instance()
                            .setMonitoringProperty(MonitoredProperties.POLICY_LAST_MODIFICATION_TIME_PROP_NAME,
                                                   pap.getPolicyLastModificationTimeInSecondsString());
        }
    }

    /**
     * Update the last policy modification time of the pap in the repository.
     */
    private void updatePapPolicyLastModificationTime() {
        pap.setPolicyLastModificationTime((new GregorianCalendar()).getTimeInMillis());
        synchronized (notificationLock) {
            DAOFactory.getDAOFactory().getPapDAO().update(pap);
        }
        notifyPolicyLastModificationTimeUpdate();
    }
}
