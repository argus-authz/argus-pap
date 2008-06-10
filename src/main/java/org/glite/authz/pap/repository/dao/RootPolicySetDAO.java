package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;
import org.glite.authz.pap.common.xacml.XACMLObject;

public interface RootPolicySetDAO {

	public void create();

	public boolean exists();

	public PolicySet get();

	public List<XACMLObject> getAll();

	public List<XACMLObject> getAllByPAPId(String[] papIdList);

	public List<String> listPAPs();

}
