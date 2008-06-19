package org.glite.authz.pap.common.utils.xacml;

import java.util.List;

import org.opensaml.xacml.policy.ResourceMatchType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xml.Configuration;

public class ResourceHelper extends XACMLHelper<ResourceType> {
	private static ResourceHelper instance = null;

	public static ResourceHelper getInstance() {
		if (instance == null) {
			instance = new ResourceHelper();
		}
		return instance;
	}

	private ResourceHelper() {
	}

	public static ResourceType build() {
		return (ResourceType) Configuration.getBuilderFactory().getBuilder(
				ResourceType.DEFAULT_ELEMENT_NAME).buildObject(
						ResourceType.DEFAULT_ELEMENT_NAME);
	}

	public static ResourceType build(List<ResourceMatchType> resourceMatchList) {
		ResourceType resource = build();
		for (ResourceMatchType resourceMatch : resourceMatchList) {
			resource.getResourceMatches().add(resourceMatch);
		}
		return resource;
	}

}
