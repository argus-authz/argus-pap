/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.ResourceMatchType;
import org.opensaml.xacml.policy.ResourceType;

public class ResourceHelper extends XMLObjectHelper<ResourceType> {

    private static final javax.xml.namespace.QName elementQName = ResourceType.DEFAULT_ELEMENT_NAME;
    private static ResourceHelper instance = new ResourceHelper();

    private ResourceHelper() {}

    public static ResourceType build() {
        return (ResourceType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ResourceType build(List<ResourceMatchType> resourceMatchList) {
        
        if (resourceMatchList.isEmpty()) {
            return null;
        }
        
        ResourceType resource = build();
        
        for (ResourceMatchType resourceMatch : resourceMatchList) {
            resource.getResourceMatches().add(resourceMatch);
        }
        
        return resource;
    }

    public static ResourceHelper getInstance() {
        return instance;
    }

}
