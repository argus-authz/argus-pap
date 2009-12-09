package org.glite.authz.pap.repository.dao.hibernate;

import java.util.List;

import org.glite.authz.pap.common.xacml.impl.PolicySetTypeString;
import org.glite.authz.pap.common.xacml.impl.TypeStringUtils;
import org.glite.authz.pap.common.xacml.utils.XMLObjectHelper;
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
public class PolicySetDAOHibernate extends GenericDAOHibernate implements PolicySetDAO {

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

        PolicySetType policySet = getById(papId, policySetId);

        getSession().delete(policySet);
    }

    /**
     * {@Inherited}
     */
    public void deleteAll(String papId) {
        
        // TODO: change the name in deleteByPapId

        List<PolicySetType> policySetList = getAll(papId);

        for (PolicySetType policySet : policySetList) {
            getSession().delete(policySet);
        }
    }

    /**
     * {@Inherited}
     */
    public boolean exists(String papId, String policySetId) {

        // TODO: remove papId input parameter

        PolicySetTypeString policySet = (PolicySetTypeString) getSession().get(PolicySetTypeString.class,
                                                                               policySetId);

        return !(policySet == null);
    }

    /**
     * {@Inherited}
     */
    @SuppressWarnings("unchecked")
    public List<PolicySetType> getAll(String papId) {

        // TODO: change the name in getByPapId

        List<PolicySetType> policySetList = getSession().createQuery("select p from PolicySetTypeString p where p.papId = :papId")
                                                        .setParameter("papId", papId)
                                                        .list();
        return policySetList;
    }

    /**
     * {@Inherited}
     */
    public PolicySetType getById(String papId, String policySetId) {

        // TODO: remove papId input parameter

        PolicySetTypeString policySet = (PolicySetTypeString) getSession().get(PolicySetTypeString.class,
                                                                               policySetId);

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

        try {

            getSession().save(policySetTypeString);

        } catch (ConstraintViolationException e) {
            throw new AlreadyExistsException("Already exists: policySetId=" + policySetId);
        }

        log.debug("Stored policy set: " + policySetId);
    }

    public void update(String papId, String version, PolicySetType policySet) {

        // TODO: remove version in punt parameter

        PolicySetTypeString policySetTypeString = TypeStringUtils.cloneAsPolicySetTypeString(policySet);
        TypeStringUtils.releaseUnneededMemory(policySet);

        String policySetId = policySetTypeString.getPolicySetId();
        String policySetVersion = policySetTypeString.getVersion();

        PolicySetWizard.increaseVersion(policySetTypeString);

        TypeStringUtils.releaseUnneededMemory(policySetTypeString);

        PolicySetTypeString repoPolicySet = (PolicySetTypeString) getById(papId, policySetId);

        if (!(repoPolicySet.getVersion().equals(policySetVersion))) {
            throw new InvalidVersionException(String.format("Attempting to update the wrong version of PolicySetId=\"%s\" (requestedVersion=\"%s\", repositoryVersion=\"%s\")",
                                                            policySetId,
                                                            policySetVersion,
                                                            repoPolicySet.getVersion()));
        }

        TypeStringUtils.releaseUnneededMemory(repoPolicySet);

        if (repoPolicySet != policySetTypeString) {
            repoPolicySet.setPolicySetString(policySetTypeString.getPolicySetId(),
                                             policySetTypeString.getPolicySetString());
        }
        
        if (log.isTraceEnabled()) {
            log.trace("Updating policy set with:\n" + XMLObjectHelper.toString(repoPolicySet));
            TypeStringUtils.releaseUnneededMemory(repoPolicySet);
        }
        
        getSession().save(repoPolicySet);
    }
}
