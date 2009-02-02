package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicySetType;

public interface PolicySetDAO {

    public void delete(String papId, String policySetId);

    public void deleteAll(String papId);

    public boolean exists(String papId, String policySetId);

    public List<PolicySetType> getAll(String papId);

    public PolicySetType getById(String papId, String policySetId) throws NotFoundException, RepositoryException;

    public void store(String papId, PolicySetType policySet);

    public void update(String papId, PolicySetType newPolicySet);

}
