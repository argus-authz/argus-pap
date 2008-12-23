package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.repository.exceptions.NotFoundException;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.opensaml.xacml.policy.PolicyType;

public interface PolicyDAO {

    public void delete(String papId, String policyId) throws NotFoundException, RepositoryException;

    public void deleteAll(String papId);

    public boolean exists(String papId, String policyId);

    public List<PolicyType> getAll(String papId);

    public PolicyType getById(String papId, String policyId);

    public void store(String papId, PolicyType policy);

    public void update(String papId, PolicyType policy);

}
