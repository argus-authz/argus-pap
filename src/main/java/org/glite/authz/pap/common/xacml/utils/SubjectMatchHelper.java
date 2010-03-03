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
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xml.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectMatchHelper extends XMLObjectHelper<SubjectMatchType> {

    private static final javax.xml.namespace.QName elementQName = SubjectMatchType.DEFAULT_ELEMENT_NAME;
    private static final SubjectMatchHelper instance = new SubjectMatchHelper();
    private final static Logger log = LoggerFactory.getLogger(SubjectMatchHelper.class);

    private SubjectMatchHelper() {}

    public static SubjectMatchType build() {
        return (SubjectMatchType) Configuration.getBuilderFactory().getBuilder(elementQName).buildObject(elementQName);
    }

    public static List<SubjectMatchType> buildListWithDesignator(List<AttributeType> attributeList, String matchFunctionId, String matchFunctionDatatype) {

        List<SubjectMatchType> resultList = new ArrayList<SubjectMatchType>(attributeList.size());

        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId, matchFunctionDatatype));
        }
        return resultList;
    }

    public static SubjectMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId, String matchFunctionDatatype) {

        if (attribute == null) {
            return null;
        }

        SubjectMatchType subjectMatch = build();

        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);

        
        String attributeDataType =  (matchFunctionDatatype == null ?attribute.getDataType() : matchFunctionDatatype);
        
        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attributeDataType,
                CtxAttributeTypeHelper.getFirstValue(attribute));

        subjectMatch.setSubjectAttributeDesignator(designator);
        subjectMatch.setAttributeValue(policyAttributeValue);
        subjectMatch.setMatchId(matchFunctionId);

        return subjectMatch;
    }

    public static AttributeType getAttribute(SubjectMatchType subjectMatch) {

        AttributeValueType policyAttributeValue = subjectMatch.getAttributeValue();

        AttributeDesignatorType designator = subjectMatch.getSubjectAttributeDesignator();
        if (designator == null) { // TODO: throw exception
            log.error("DESIGNATOR IS MISSING");
        }

        return CtxAttributeTypeHelper.build(designator.getAttributeId(), policyAttributeValue.getDataType(), policyAttributeValue
                .getValue());

    }

    public static List<AttributeType> getAttributeList(List<SubjectMatchType> subjectMatchList) {

        List<AttributeType> attributeList = new LinkedList<AttributeType>();

        if (subjectMatchList == null)
            return attributeList;

        for (SubjectMatchType subjectMatch : subjectMatchList) {
            attributeList.add(getAttribute(subjectMatch));
        }
        return attributeList;
    }

    public static SubjectMatchHelper getInstance() {
        return instance;
    }
}
