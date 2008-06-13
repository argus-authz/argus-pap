package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.AbstractPolicy;

public interface RootPolicySetDAO {

	public void create();

	public boolean exists();

	public PolicySet get();

	public List<AbstractPolicy> getTreeAsList();

	public List<AbstractPolicy> getPartialTreeAsList(String[] papIdList);

	public List<String> listPAPIds();
	
	public void update(PolicySet newPolicySet);

}
