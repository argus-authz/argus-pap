package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.AbstractPolicy;

public interface RootPolicySetDAO {

	public void create();

	public boolean exists();

	public PolicySet get();

	public List<AbstractPolicy> getAll();

	public List<AbstractPolicy> getByPAPId(String[] papIdList);

	public List<String> listPAPs();

}
