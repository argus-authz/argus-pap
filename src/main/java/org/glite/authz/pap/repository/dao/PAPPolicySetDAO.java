package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.AbstractPolicy;

public interface PAPPolicySetDAO {

	public void add(int index, PolicySet policySet);
	
	public void add(PolicySet policySet);

	public void delete(String papId);
	
	public void deleteRemoteAll();

	public boolean exists(String papId);

	public PolicySet get(String papId);

	public List<AbstractPolicy> getTree(String papId);

	public void update(String papId, PolicySet newPolicySet);

}
