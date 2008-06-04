package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;

public interface PolicySetDAO {

	public void delete(String papId, String policySetId);

	public boolean exists(String papId, String policySetId);

	public List<PolicySet> getAll(String papId);

	public PolicySet getById(String papId, String policySetId);

	public void store(String papId, PolicySet ps);

	public void update(String papId, PolicySet ps);

}
