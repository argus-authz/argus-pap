package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xacml.policy.SubjectType;

public class SubjectHelper extends XMLObjectHelper<SubjectType> {
    
    private static final javax.xml.namespace.QName elementQName = SubjectType.DEFAULT_ELEMENT_NAME;
    private static SubjectHelper instance = new SubjectHelper();

    private SubjectHelper() {}

    public static SubjectType build() {
        return (SubjectType) builderFactory.getBuilder(
                elementQName).buildObject(elementQName);
    }

    public static SubjectType build(List<SubjectMatchType> subjectMatchList) {
        
        if (subjectMatchList.isEmpty()) {
            return null;
        }
        
        SubjectType subject = build();
        
        for (SubjectMatchType subjectMatch : subjectMatchList) {
            subject.getSubjectMatches().add(subjectMatch);
        }
        
        return subject;
    }

    public static SubjectHelper getInstance() {
        return instance;
    }

}
