package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.ResourcesType;

public class ResourcesHelper extends XMLObjectHelper<ResourcesType> {

    private static final javax.xml.namespace.QName elementQName = ResourcesType.DEFAULT_ELEMENT_NAME;
    private static ResourcesHelper instance = new ResourcesHelper();

    private ResourcesHelper() {}

    public static ResourcesType build() {
        return (ResourcesType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ResourcesType build(List<ResourceType> resourceList) {
        
        if (resourceList.isEmpty()) {
            return null;
        }

        ResourcesType resources = build();

        for (ResourceType resource : resourceList) {
            resources.getResources().add(resource);
        }

        return resources;
    }

    public static ResourcesType build(ResourceType resource) {

        if (resource == null) {
            return null;
        }

        ResourcesType resources = build();
        resources.getResources().add(resource);

        return resources;
    }

    public static ResourcesHelper getInstance() {
        return instance;
    }

}
