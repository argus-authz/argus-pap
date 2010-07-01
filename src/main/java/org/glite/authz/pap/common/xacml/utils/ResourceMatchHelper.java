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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ResourceMatchType;

public class ResourceMatchHelper extends XMLObjectHelper<ResourceMatchType> {

    private static final javax.xml.namespace.QName elementQName = ResourceMatchType.DEFAULT_ELEMENT_NAME;
    private static ResourceMatchHelper instance = new ResourceMatchHelper();

    private ResourceMatchHelper() {}

    public static ResourceMatchType build() {
        return (ResourceMatchType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ResourceMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId, String matchFunctionDatatype) {

        if (attribute == null) {
            return null;
        }

        ResourceMatchType resourceMatch = build();

        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);

        String attributeDataType =  (matchFunctionDatatype == null ?attribute.getDataType() : matchFunctionDatatype);
        
        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attributeDataType,
                CtxAttributeTypeHelper.getFirstValue(attribute));

        resourceMatch.setResourceAttributeDesignator(designator);
        resourceMatch.setAttributeValue(policyAttributeValue);
        resourceMatch.setMatchId(matchFunctionId);

        return resourceMatch;
    }

    public static List<ResourceMatchType> buildWithDesignator(List<AttributeType> attributeList, String matchFunctionId, String matchFunctionDatatype) {

        List<ResourceMatchType> resultList = new ArrayList<ResourceMatchType>(attributeList.size());

        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId, matchFunctionDatatype));
        }

        return resultList;
    }

    public static AttributeType getAttribute(ResourceMatchType resourceMatch) {

        AttributeValueType policyAttributeValue = resourceMatch.getAttributeValue();
        String attributeId = resourceMatch.getResourceAttributeDesignator().getAttributeId();
        return CtxAttributeTypeHelper.build(attributeId, policyAttributeValue.getDataType(), policyAttributeValue.getValue());

    }

    public static List<AttributeType> getAttributeList(List<ResourceMatchType> resourceMatchList) {

        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        if (resourceMatchList == null) {
            return attributeList;
        }

        for (ResourceMatchType subjectMatch : resourceMatchList) {
            attributeList.add(getAttribute(subjectMatch));
        }

        return attributeList;

    }

    public static ResourceMatchHelper getInstance() {
        return instance;
    }

}
