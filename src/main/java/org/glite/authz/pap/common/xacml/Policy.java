package org.glite.authz.pap.common.xacml;

import java.io.File;

public interface Policy extends AbstractPolicy {
	public void setId(String policyId);
	public String getId();
	public void printXACMLDOMToFile(File file);
	public void printXACMLDOMToFile(String fileName);

}
