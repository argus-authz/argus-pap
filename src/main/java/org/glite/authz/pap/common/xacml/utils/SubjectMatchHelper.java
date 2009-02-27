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

    public static List<SubjectMatchType> buildListWithDesignator(List<AttributeType> attributeList, String matchFunctionId) {

        List<SubjectMatchType> resultList = new ArrayList<SubjectMatchType>(attributeList.size());

        for (AttributeType attribute : attributeList) {
            resultList.add(buildWithDesignator(attribute, matchFunctionId));
        }
        return resultList;
    }

    public static SubjectMatchType buildWithDesignator(AttributeType attribute, String matchFunctionId) {

        if (attribute == null) {
            return null;
        }

        SubjectMatchType subjectMatch = build();

        AttributeDesignatorType designator = AttributeDesignatorHelper.build(
                AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME, attribute);

        subjectMatch.setSubjectAttributeDesignator(designator);

        AttributeValueType policyAttributeValue = PolicyAttributeValueHelper.build(attribute.getDataType(),
                CtxAttributeTypeHelper.getFirstValue(attribute));

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
