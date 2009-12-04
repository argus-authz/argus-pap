package org.glite.authz.pap.repository.dao.hibernate;

import java.util.List;

import javax.persistence.EntityTransaction;

import org.glite.authz.pap.common.xacml.impl.PolicyTypeString;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.PolicyWizard;
import org.glite.authz.pap.repository.dao.PolicyDAO;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemRepositoryManager;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filesystem implementation of the {@link PolicyDAO} interface.
 * <p>
 * This DAO stores information about the policies of a pap. The name of the file of the policies follows that form:
 * <i>prefix</i> + <i>policyId</i> + .<i>extension</i> The value for <i>prefix</i> is:
 * {@link FileSystemRepositoryManager#POLICYSET_FILENAME_PREFIX}.<br>
 * The value for <i>extension</i> is: {@link FileSystemRepositoryManager#XACML_FILENAME_EXTENSION}.<br>
 */
public class PolicyDAOHibernate extends GenericDAOJpa implements PolicyDAO {

    private static final Logger log = LoggerFactory.getLogger(PolicyDAOHibernate.class);

    private static String policyExceptionMsg(String policyId) {
        return String.format("policyId=%s", policyId);
    }

    private static String policyNotFoundExceptionMsg(String policyId) {
        String msg = "Not found: " + policyExceptionMsg(policyId);
        return msg;
    }

    private PolicyDAOHibernate() {
    }

    public static PolicyDAOHibernate getInstance() {
        return new PolicyDAOHibernate();
    }

    /**
     * {@Inherited}
     */
    public void delete(String papId, String policyId) {

        EntityTransaction tx = manageTransaction();

        PolicyType policy = getById(papId, policyId);

        if (policy == null) {
            throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
        }

        getEntityManager().remove(policy);

        commitManagedTransaction(tx);
    }

    /**
     * {@Inherited}
     */
    public void deleteAll(String papId) {

        EntityTransaction tx = manageTransaction();

        List<PolicyType> policyList = getAll(papId);

        for (PolicyType policy : policyList) {
            getEntityManager().remove(policy);
        }

        commitManagedTransaction(tx);
    }

    /**
     * {@Inherited}
     */
    public boolean exists(String papId, String policyId) {
        // TODO: remove papId input parameter

        PolicyTypeString policy = getEntityManager().find(PolicyTypeString.class, policyId);

        return !(policy == null);
    }

    /**
     * {@Inherited}
     */
    @SuppressWarnings("unchecked")
    public List<PolicyType> getAll(String papId) {
        // TODO: change the name in getByPapId

        List<PolicyType> policyList = getEntityManager().createQuery("select p from PolicyTypeString p where p.papId = :papId")
                                                        .setParameter("papId", papId)
                                                        .getResultList();
        return policyList;
    }

    /**
     * {@Inherited}
     */
    public PolicyType getById(String papId, String policyId) {
        // TODO: remove papId input parameter

        PolicyTypeString policy = getEntityManager().find(PolicyTypeString.class, policyId);

        if (policy == null) {
            throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
        }

        return policy;
    }

    /**
     * {@Inherited}
     */
    public void store(String papId, PolicyType policy) {

        PolicyTypeString policyTypeString = TypeStringUtils.cloneAsPolicyTypeString(policy);
        TypeStringUtils.releaseUnneededMemory(policy);
        
        policyTypeString.setPapId(papId);
        
        String policyId = policyTypeString.getPolicyId();

        EntityTransaction tx = manageTransaction();

        try {
            
            getEntityManager().persist(policyTypeString);
            
        } catch (ConstraintViolationException e) {
            rollbakManagedTransaction(tx);
            throw new AlreadyExistsException("Already exists: policyId=" + policyId);
        }

        commitManagedTransaction(tx);

        log.debug("Stored policy: " + policyId);
    }

    /**
     * {@Inherited}
     */
    public void update(String papId, String version, PolicyType newPolicy) {

        PolicyTypeString newPolicyTypeString = TypeStringUtils.cloneAsPolicyTypeString(newPolicy);
        TypeStringUtils.releaseUnneededMemory(newPolicy);
        
        String policyId = newPolicyTypeString.getPolicyId();
        String newPolicyVersion = newPolicyTypeString.getVersion();
        
        if (!version.equals(newPolicyVersion)) {
            throw new InvalidVersionException(String.format("Invalide version of the policy to be updated. PolicyId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                                                            policyId,
                                                            version,
                                                            newPolicyVersion));
        }
        
        PolicyWizard.increaseVersion(newPolicyTypeString);
        newPolicyTypeString.setPapId(papId);
        
        TypeStringUtils.releaseUnneededMemory(newPolicyTypeString);
        
        EntityTransaction tx = manageTransaction();
        
        PolicyTypeString repoPolicy = (PolicyTypeString) getById(papId, policyId);

        if (repoPolicy == null) {
            rollbakManagedTransaction(tx);
            throw new NotFoundException(policyNotFoundExceptionMsg(policyId));
        }

        if (!(repoPolicy.getVersion().equals(version))) {
            throw new InvalidVersionException(String.format("Attempting to update the wrong version of PolicyId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                                                            policyId,
                                                            version,
                                                            repoPolicy.getVersion()));
        }

        TypeStringUtils.releaseUnneededMemory(repoPolicy);
        
        if (repoPolicy != newPolicyTypeString) {
            repoPolicy.setPolicyString(newPolicyTypeString.getPolicyId(), newPolicyTypeString.getPolicyString());
        }

        getEntityManager().persist(repoPolicy);
        
        commitManagedTransaction(tx);
        
//        getEntityManager().createNamedQuery("SHUTDOWN COMPACT");
    }
}
