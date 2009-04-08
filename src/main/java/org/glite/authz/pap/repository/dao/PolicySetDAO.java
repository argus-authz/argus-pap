package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.impl.PolicySetTypeString;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.InvalidVersionException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;

/**
 * This is the interface for the policy set DAO.
 */
public interface PolicySetDAO {

    /**
     * Deletes a policy set in a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policySetId <code>id</code> of the policy set to remove.
     * 
     * @throws NotFoundException if <code>papId</code> and/or <code>policySetId</code> were not
     *             found.
     * 
     */
    public void delete(String papId, String policySetId);

    /**
     * Delete all policy sets of a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * 
     * @throws NotFoundException if <code>papId</code> was not found.
     */
    public void deleteAll(String papId);

    /**
     * Checks for the existence of a policy set inside a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policySetId <code>id</code> of the policy set to remove.
     * @return <code>true</code> if the policy set exists, <code>false</code> otherwise.
     */
    public boolean exists(String papId, String policySetId);

    /**
     * Returns the list of all the policy sets in a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @return the list of policy sets in the pap. Each element of the list is a
     *         {@link PolicySetTypeString} object.
     * 
     * @throws NotFoundException if <code>papId</code> was not found.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy set file).
     */
    public List<PolicySetType> getAll(String papId);

    /**
     * Returns a policy set contained inside a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policySetId <code>id</code> of the policy set to retrieve.
     * @return the policy set as {@link PolicySetTypeString} object.
     * 
     * @throws NotFoundException if <code>papId</code> and/or <code>policySetId</code> were not
     *             found.
     */
    public PolicySetType getById(String papId, String policySetId) throws NotFoundException,
            RepositoryException;

    /**
     * Stores a policy set in a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policySet the policy set to store.
     * 
     * @throws NotFoundException if <code>papId</code> was not found.
     * @throws AlreadyExistsException if the policy set id of the given policy set already exists.
     */
    public void store(String papId, PolicySetType policySet);

    /**
     * Updates a policy set in a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param version version of the policy set to replace.
     * @param newPolicySet the new policy set replacing the one with the same policy set id.
     * 
     * @throws NotFoundException if <code>papId</code> and/or the policy set id were not found.
     * @throws InvalidVersionException if the version of the policy set in the repository to replace
     *             does not match the given <code>version</code>.
     */
    public void update(String papId, String version, PolicySetType newPolicySet);

}
