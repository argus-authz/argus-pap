package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.opensaml.xacml.policy.PolicySetType;

public interface PolicySetDAO {

	public void delete(String papId, String policySetId);

	public boolean exists(String papId, String policySetId);

	public List<PolicySetType> getAll(String papId);

	public PolicySetType getById(String papId, String policySetId);

	public void store(String papId, PolicySetType policySet);

	public void update(String papId, PolicySetType newPolicySet);

}
