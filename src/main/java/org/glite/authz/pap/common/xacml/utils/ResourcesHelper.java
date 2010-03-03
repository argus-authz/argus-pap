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
