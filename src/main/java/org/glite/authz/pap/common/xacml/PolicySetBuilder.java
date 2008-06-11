package org.glite.authz.pap.common.xacml;

import java.io.File;

import org.w3c.dom.Document;

public interface PolicySetBuilder {
	
	public PolicySet build(String policySetId, String policyCombinerAlgorithmId);
	
	public PolicySet buildFromDOM(Document doc);

	public PolicySet buildFromFile(File file);
	
	public PolicySet buildFromFile(String fileName);
	
}
