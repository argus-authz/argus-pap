package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;

public class ResourcesHelper extends XMLObjectHelper<ResourcesType> {

    private static final javax.xml.namespace.QName elementQName = ResourcesType.DEFAULT_ELEMENT_NAME;
    private static ResourcesHelper instance = new ResourcesHelper();

    public static ResourcesType build() {
        return (ResourcesType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ResourcesType build(List<ResourceType> resourceList) {
        
        ResourcesType resources = build();

        for (ResourceType resource : resourceList) {
            resources.getResources().add(resource);
        }

        return resources;
    }

    public static ResourcesType build(ResourceType resource) {
        
        ResourcesType resources = build();
        
        if (resource != null)
            resources.getResources().add(resource);

        return resources;
    }

    public static ResourcesHelper getInstance() {
        return instance;
    }

    private ResourcesHelper() {}

}
