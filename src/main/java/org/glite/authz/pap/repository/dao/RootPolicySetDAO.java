package org.glite.authz.pap.repository.dao;

import java.util.List;

import org.glite.authz.pap.common.xacml.PolicySet;

public interface RootPolicySetDAO {

	public void createPAPAsFirst(PolicySet policySet);

	public void createRoot();

	public void deletePAP(String papId);

	public boolean existsPAP(String papId);

	public boolean existsRoot();

	public PolicySet getPAPRoot(String papId);

	public PolicySet getRoot();

	public List<String> listPAPs();

	public void updatePAP(String papId, PolicySet ps);

}
