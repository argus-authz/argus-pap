package org.glite.authz.pap.common.xacml.impl;

import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilder;

public class TargetOpenSAML {
	
	private XMLObjectBuilder<TargetType> targetBuilder;
	private TargetType target;

	@SuppressWarnings("unchecked")
	public TargetOpenSAML() {
		targetBuilder = Configuration.getBuilderFactory().getBuilder(org.opensaml.xacml.policy.TargetType.DEFAULT_ELEMENT_NAME);
		target = targetBuilder.buildObject(org.opensaml.xacml.policy.TargetType.DEFAULT_ELEMENT_NAME);
	}

	public TargetType getOpenSAMLTargetType() {
		return target;
	}

	public void setOpenSAMLTargetType(TargetType target) {
		this.target = target;
	}
}
