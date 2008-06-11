package org.glite.authz.pap.common.xacml;

import java.io.File;

import org.w3c.dom.Document;

public interface PolicyBuilder {
	
	public Policy build(String policyId, String ruleCombinerAlgorithmId);
	
	public Policy buildFromDOM(Document doc);

	public Policy buildFromFile(File file);
	
	public Policy buildFromFile(String fileName);

}
