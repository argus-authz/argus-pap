package org.glite.authz.pap.common.utils.xacml;

import java.util.List;

import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xml.Configuration;

public class ResourcesHelper extends XACMLHelper<ResourcesType> {
    private static ResourcesHelper instance = null;

    public static ResourcesHelper getInstance() {
	if (instance == null) {
	    instance = new ResourcesHelper();
	}
	return instance;
    }

    private ResourcesHelper() {
    }

    public static ResourcesType buildAnyResource() {
	return (ResourcesType) Configuration.getBuilderFactory().getBuilder(
		ResourcesType.DEFAULT_ELEMENT_NAME).buildObject(
		ResourcesType.DEFAULT_ELEMENT_NAME);
    }

    public static ResourcesType build(List<ResourceType> resourceList) {
	ResourcesType resources = buildAnyResource();
	for (ResourceType resource : resourceList) {
	    resources.getResources().add(resource);
	}
	return resources;
    }

    public static ResourcesType build(ResourceType resource) {
	ResourcesType resources = buildAnyResource();
	resources.getResources().add(resource);
	return resources;
    }

}
