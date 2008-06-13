package org.glite.authz.pap.common.xacml.impl;

import org.glite.authz.pap.common.utils.xacml.XACMLException;
import org.glite.authz.pap.common.xacml.Target;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Node;

public class TargetOpenSAML extends Target {

	private XMLObjectBuilder<TargetType> targetBuilder;
	private TargetType target;

	@SuppressWarnings("unchecked")
	public TargetOpenSAML() {
		targetBuilder = Configuration.getBuilderFactory().getBuilder(
				org.opensaml.xacml.policy.TargetType.DEFAULT_ELEMENT_NAME);
		target = targetBuilder
				.buildObject(org.opensaml.xacml.policy.TargetType.DEFAULT_ELEMENT_NAME);
	}

	public Node getDOM() {
		MarshallerFactory marshallerFactory = Configuration
				.getMarshallerFactory();
		Marshaller marshaller = marshallerFactory.getMarshaller(target);
		try {
			marshaller.marshall(target);
		} catch (MarshallingException e) {
			throw new XACMLException(e);
		}
		return target.getDOM();
	}

	public TargetType getOpenSAMLTargetType() {
		return target;
	}

	public void setOpenSAMLTargetType(TargetType target) {
		this.target = target;
	}
}
