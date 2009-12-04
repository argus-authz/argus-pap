package org.glite.authz.pap.repository.dao.hibernate;

import java.util.List;

import javax.persistence.EntityTransaction;

import org.glite.authz.pap.common.xacml.impl.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.wizard.PolicySetWizard;
import org.glite.authz.pap.repository.dao.PolicySetDAO;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemRepositoryManager;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.opensaml.xacml.policy.PolicySetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filesystem implementation of the {@link PolicySetDAO} interface.
 * <p>
 * This DAO stores information about the policy sets of a pap. The name of the file of the policy sets follows that
 * form: <i>prefix</i> + <i>policySetId</i> + .<i>extension</i><br>
 * The value for <i>prefix</i> is: {@link FileSystemRepositoryManager#POLICYSET_FILENAME_PREFIX}.<br>
 * The value for <i>extension</i> is: {@link FileSystemRepositoryManager#XACML_FILENAME_EXTENSION}. <br>
 */
public class PolicySetDAOHibernate extends GenericDAOJpa implements PolicySetDAO {

    private static final Logger log = LoggerFactory.getLogger(PolicySetDAOHibernate.class);

    private PolicySetDAOHibernate() {
    }

    public static PolicySetDAOHibernate getInstance() {
        return new PolicySetDAOHibernate();
    }

    private static String policySetExceptionMsg(String policySetId) {
        return String.format("policySetId=\"%s\"", policySetId);
    }

    private static String policySetNotFoundExceptionMsg(String policySetId) {
        String msg = "Not found: " + policySetExceptionMsg(policySetId);
        return msg;
    }

    /**
     * {@Inherited}
     */
    public void delete(String papId, String policySetId) {

        EntityTransaction tx = manageTransaction();

        PolicySetType policySet = getById(papId, policySetId);

        if (policySet == null) {
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
        }

        getEntityManager().remove(policySet);

        commitManagedTransaction(tx);
    }

    /**
     * {@Inherited}
     */
    public void deleteAll(String papId) {

        EntityTransaction tx = manageTransaction();

        List<PolicySetType> policySetList = getAll(papId);

        for (PolicySetType policySet : policySetList) {
            getEntityManager().remove(policySet);
        }

        commitManagedTransaction(tx);
    }

    /**
     * {@Inherited}
     */
    public boolean exists(String papId, String policySetId) {

        // TODO: remove papId input parameter

        PolicySetTypeString policySet = getEntityManager().find(PolicySetTypeString.class, policySetId);

        return !(policySet == null);
    }

    /**
     * {@Inherited}
     */
    @SuppressWarnings("unchecked")
    public List<PolicySetType> getAll(String papId) {

        // TODO: change the name in getByPapId

        List<PolicySetType> policySetList = getEntityManager().createQuery("select p from PolicySetTypeString p where p.papId = :papId")
                                                              .setParameter("papId", papId)
                                                              .getResultList();
        return policySetList;
    }

    /**
     * {@Inherited}
     */
    public PolicySetType getById(String papId, String policySetId) {

        // TODO: remove papId input parameter

        PolicySetTypeString policySet = getEntityManager().find(PolicySetTypeString.class, policySetId);

        if (policySet == null) {
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
        }

        return policySet;
    }

    /**
     * {@Inherited}
     */
    public void store(String papId, PolicySetType policySet) {

        PolicySetTypeString policySetTypeString = TypeStringUtils.cloneAsPolicySetTypeString(policySet);
        TypeStringUtils.releaseUnneededMemory(policySet);

        policySetTypeString.setPapId(papId);

        String policySetId = policySetTypeString.getPolicySetId();

        EntityTransaction tx = manageTransaction();

        try {

            getEntityManager().persist(policySetTypeString);

        } catch (ConstraintViolationException e) {
            rollbakManagedTransaction(tx);
            throw new AlreadyExistsException("Already exists: policySetId=" + policySetId);
        }

        commitManagedTransaction(tx);

        log.debug("Stored policy set: " + policySetId);
    }

    public void update(String papId, String policySetVersion, PolicySetType newPolicySet) {

        PolicySetTypeString newPolicySetTypeString = TypeStringUtils.cloneAsPolicySetTypeString(newPolicySet);
        TypeStringUtils.releaseUnneededMemory(newPolicySet);
        
        String policySetId = newPolicySetTypeString.getPolicySetId();
        String newPolicySetVersion = newPolicySetTypeString.getVersion();


        if (!policySetVersion.equals(newPolicySetVersion)) {
            throw new InvalidVersionException(String.format("Invalide version of the policy set to be updated. PolicySetId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                                                            policySetId,
                                                            policySetVersion,
                                                            newPolicySetVersion));
        }
        
        PolicySetWizard.increaseVersion(newPolicySetTypeString);
        newPolicySetTypeString.setPapId(papId);
        
        TypeStringUtils.releaseUnneededMemory(newPolicySetTypeString);

        EntityTransaction tx = manageTransaction();

        PolicySetTypeString repoPolicySet = (PolicySetTypeString) getById(papId, policySetId);

        if (repoPolicySet == null) {
            rollbakManagedTransaction(tx);
            throw new NotFoundException(policySetNotFoundExceptionMsg(policySetId));
        }

        if (!(repoPolicySet.getVersion().equals(policySetVersion))) {
            throw new InvalidVersionException(String.format("Attempting to update the wrong version of PolicySetId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                                                            policySetId,
                                                            policySetVersion,
                                                            repoPolicySet.getVersion()));
        }

        TypeStringUtils.releaseUnneededMemory(repoPolicySet);

        if (repoPolicySet != newPolicySetTypeString) {
            repoPolicySet.setPolicySetString(newPolicySetTypeString.getPolicySetId(), newPolicySetTypeString.getPolicySetString());
        }
        
        getEntityManager().persist(repoPolicySet);
        
        commitManagedTransaction(tx);
        
//        getEntityManager().createNamedQuery("SHUTDOWN COMPACT");
    }
}
