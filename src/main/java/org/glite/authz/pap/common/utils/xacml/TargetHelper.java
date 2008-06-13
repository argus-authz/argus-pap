package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.Configuration;

public class TargetHelper extends XACMLHelper<TargetType> {
	private static TargetHelper instance = null;

	public static TargetType build() {
		return (TargetType) Configuration.getBuilderFactory().getBuilder(
				TargetType.DEFAULT_ELEMENT_NAME).buildObject(
				TargetType.DEFAULT_ELEMENT_NAME);
	}

	public static TargetHelper getInstance() {
		if (instance == null) {
			instance = new TargetHelper();
		}
		return instance;
	}

	private TargetHelper() {
	}
}
