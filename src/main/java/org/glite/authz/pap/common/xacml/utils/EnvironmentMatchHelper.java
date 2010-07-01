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
import org.opensaml.xacml.policy.EnvironmentMatchType;

public class EnvironmentMatchHelper extends XMLObjectHelper<EnvironmentMatchType> {

    private static final javax.xml.namespace.QName elementQName = EnvironmentMatchType.DEFAULT_ELEMENT_QNAME;
    private static EnvironmentMatchHelper instance = new EnvironmentMatchHelper();

    private EnvironmentMatchHelper() {}

    public static EnvironmentMatchType build() {
        return (EnvironmentMatchType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static EnvironmentMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId, String matchFunctionDatatype) {
        
        if (attribute == null) {
            return null;
        }
        
        EnvironmentMatchType environmentMatch = build();
        
        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);
        
        String attributeDataType =  (matchFunctionDatatype == null ?attribute.getDataType() : matchFunctionDatatype);
        
        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attributeDataType, 
        		CtxAttributeTypeHelper.getFirstValue(attribute));
        
        environmentMatch.setEnvironmentAttributeDesignator(designator);
        environmentMatch.setAttributeValue(policyAttributeValue);
        environmentMatch.setMatchId(matchFunctionId);
        
        return environmentMatch;
    }
    
    public static List<EnvironmentMatchType> buildWithDesignator(List<AttributeType> attributeList,
            String matchFunctionId, String matchFunctionDatatype) {
        
        List<EnvironmentMatchType> resultList = new ArrayList<EnvironmentMatchType>(attributeList.size());
        
        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId, matchFunctionDatatype));
        }
        
        return resultList;
    }
    
    public static AttributeType getAttribute(EnvironmentMatchType environmentMatch) {

        AttributeValueType policyAttributeValue = environmentMatch.getAttributeValue();
        String attributeId = environmentMatch.getEnvironmentAttributeDesignator().getAttributeId();
        return CtxAttributeTypeHelper.build(attributeId, policyAttributeValue.getDataType(),
                policyAttributeValue.getValue());
        
    }

    public static List<AttributeType> getAttributeList(List<EnvironmentMatchType> environmentMatchList) {
        
        List<AttributeType> attributeList = new LinkedList<AttributeType>();
        
        if (environmentMatchList == null) {
            return attributeList;
        }
        
        for (EnvironmentMatchType subjectMatch:environmentMatchList) {
            attributeList.add(getAttribute(subjectMatch));
        }
        
        return attributeList;
        
    }

    public static EnvironmentMatchHelper getInstance() {
        return instance;
    }
}
