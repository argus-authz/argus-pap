package org.glite.authz.pap.repository.dao;


import java.util.List;

import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;

public interface PAPPolicySetDAO {

	public void add(int index, PolicySetType policySet);
	
	public void add(PolicySetType policySet);

	public void delete(String papId);
	
	public void deleteRemoteAll();

	public boolean exists(String papId);

	public PolicySetType get(String papId);

	public List<XACMLObject> getTree(String papId);

	public void update(String papId, PolicySetType newPolicySet);

}
