package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.ResourceMatchType;
import org.opensaml.xacml.policy.ResourceType;

public class ResourceHelper extends XMLObjectHelper<ResourceType> {

    private static final javax.xml.namespace.QName elementQName = ResourceType.DEFAULT_ELEMENT_NAME;
    private static ResourceHelper instance = new ResourceHelper();

    public static ResourceType build() {
        return (ResourceType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ResourceType build(List<ResourceMatchType> resourceMatchList) {
        
        if (resourceMatchList.isEmpty())
            return null;
        
        ResourceType resource = build();
        
        for (ResourceMatchType resourceMatch : resourceMatchList) {
            resource.getResourceMatches().add(resourceMatch);
        }
        
        return resource;
    }

    public static ResourceHelper getInstance() {
        return instance;
    }

    private ResourceHelper() {}

}
