package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.Policy;

public interface PolicyDAO {
	
	public void delete(String papId, String policyId);

	public boolean exists(String papId, String policyId);

	public List<Policy> getAll(String papId);

	public Policy getById(String papId, String policyId);

	public void store(String papId, Policy policy);

	public void update(String papId, Policy policy);

}
