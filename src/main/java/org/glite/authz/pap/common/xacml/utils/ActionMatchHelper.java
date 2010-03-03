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
import org.opensaml.xacml.policy.ActionMatchType;

public class ActionMatchHelper extends XMLObjectHelper<ActionMatchType> {

    private static final javax.xml.namespace.QName elementQName = ActionMatchType.DEFAULT_ELEMENT_NAME;
    private static ActionMatchHelper instance = new ActionMatchHelper();

    private ActionMatchHelper() {}

    public static ActionMatchType build() {
        return (ActionMatchType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ActionMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId, String matchFunctionDatatype) {
        
        if (attribute == null) {
            return null;
        }
        
        ActionMatchType actionMatch = build();
        
        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);
        
        String attributeDataType =  (matchFunctionDatatype == null ?attribute.getDataType() : matchFunctionDatatype);
        
        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attributeDataType, CtxAttributeTypeHelper.getFirstValue(attribute));
        
        actionMatch.setActionAttributeDesignator(designator);
        actionMatch.setAttributeValue(policyAttributeValue);
        actionMatch.setMatchId(matchFunctionId);
        
        return actionMatch;
    }
    
    public static List<ActionMatchType> buildWithDesignator(List<AttributeType> attributeList,
            String matchFunctionId, String matchFunctionDatatype) {
        
        List<ActionMatchType> resultList = new ArrayList<ActionMatchType>(attributeList.size());
        
        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId, matchFunctionDatatype));
        }
        
        return resultList;
    }
    
    public static AttributeType getAttribute(ActionMatchType actionMatch) {

        AttributeValueType policyAttributeValue = actionMatch.getAttributeValue();
        String attributeId = actionMatch.getActionAttributeDesignator().getAttributeId();
        return CtxAttributeTypeHelper.build(attributeId, policyAttributeValue.getDataType(),
                policyAttributeValue.getValue());
        
    }

    public static List<AttributeType> getAttributeList(List<ActionMatchType> actionMatchList) {
        
        List<AttributeType> attributeList = new LinkedList<AttributeType>();
        
        if (actionMatchList == null) {
            return attributeList;
        }
        
        for (ActionMatchType subjectMatch:actionMatchList) {
            attributeList.add(getAttribute(subjectMatch));
        }
        return attributeList;
    }

    public static ActionMatchHelper getInstance() {
        return instance;
    }
}
