package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;

public interface RootPolicySetDAO {

	public void create();

	public boolean exists();

	public PolicySetType get();

	public List<XACMLObject> getTreeAsList();

	public List<XACMLObject> getPartialTreeAsList(String[] papIdList);

	public List<String> listPAPIds();
	
	public void update(PolicySetType newPolicySet);

}
