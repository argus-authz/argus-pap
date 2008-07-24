package org.glite.authz.pap.common.utils.xacml;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xml.Configuration;

public class SubjectMatchHelper extends XMLObjectHelper<SubjectMatchType> {

    private static final SubjectMatchHelper instance = new SubjectMatchHelper();
    private static final javax.xml.namespace.QName elementQName = SubjectMatchType.DEFAULT_ELEMENT_NAME;

    public static SubjectMatchType build() {
        return (SubjectMatchType) Configuration.getBuilderFactory().getBuilder(elementQName)
                .buildObject(elementQName);
    }

    public static List<SubjectMatchType> buildListWithDesignator(List<AttributeType> attributeList,
            String matchFunctionId) {

        List<SubjectMatchType> resultList = new ArrayList<SubjectMatchType>(attributeList.size());

        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId));
        }
        return resultList;
    }

    public static SubjectMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId) {
        SubjectMatchType subjectMatch = build();

        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);

        subjectMatch.setSubjectAttributeDesignator(designator);

        org.opensaml.xacml.ctx.AttributeValueType ctxAttributeValue = (org.opensaml.xacml.ctx.AttributeValueType) attribute
                .getAttributeValues().get(0);

        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attribute
                .getDataType(), ctxAttributeValue.getValue());

        subjectMatch.setAttributeValue(policyAttributeValue);
        subjectMatch.setMatchId(matchFunctionId);

        return subjectMatch;
    }

    public static SubjectMatchHelper getInstance() {
        return instance;
    }

    private SubjectMatchHelper() {}

}
