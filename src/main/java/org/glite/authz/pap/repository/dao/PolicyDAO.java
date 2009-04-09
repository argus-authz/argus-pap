package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.impl.PolicyTypeString;
import org.glite.authz.pap.repository.exceptions.AlreadyExistsException;
import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicyType;

/**
 * This is the interface for the policy DAO.
 */
public interface PolicyDAO {

    /**
     * Deletes a policy of a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policyId <code>id</code> of the policy to remove.
     * 
     * @throws NotFoundException if <code>papId</code> and/or <code>policyId</code> were not found.
     * 
     */
    public void delete(String papId, String policyId);

    /**
     * Delete all policies of a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * 
     * @throws NotFoundException if <code>papId</code> was not found.
     */
    public void deleteAll(String papId);

    /**
     * Checks for the existence of a policy inside a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policyId <code>id</code> of the policy to remove.
     * @return <code>true</code> if the policy exists, <code>false</code> otherwise.
     */
    public boolean exists(String papId, String policyId);

    /**
     * Returns the list of all the policies of a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @return the list of policies of the pap. Each element of the list is a
     *         {@link PolicyTypeString} object.
     * 
     * @throws NotFoundException if <code>papId</code> was not found.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public List<PolicyType> getAll(String papId);

    /**
     * Returns a policy contained inside a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policyId <code>id</code> of the policy to retrieve.
     * @return the policy as {@link PolicyTypeString} object.
     * 
     * @throws NotFoundException if <code>papId</code> and/or <code>policyId</code> were not found.
     * @throws RepositoryException if an error occurred (e.g. a corrupted policy file).
     */
    public PolicyType getById(String papId, String policyId);

    /**
     * Stores a policy in a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param policy the policy to store.
     * 
     * @throws NotFoundException if <code>papId</code> was not found.
     * @throws AlreadyExistsException if the policy id of the given policy already exists.
     */
    public void store(String papId, PolicyType policy);

    /**
     * Updates a policy of a pap. Thread safe method.
     * 
     * @param papId <code>id</code> of the pap.
     * @param version version of the policy to replace.
     * @param policy the new policy replacing the one with the same policy id.
     * 
     * @throws NotFoundException if <code>papId</code> and/or the policy id were not found.
     * @throws InvalidVersionException if the version of the policy in the repository to replace
     *             does not match the given <code>version</code>.
     */
    public void update(String papId, String version, PolicyType policy);

}
