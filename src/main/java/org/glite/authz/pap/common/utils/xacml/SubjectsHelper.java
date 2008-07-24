package org.glite.authz.pap.common.utils.xacml;

import java.util.List;

import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;

public class SubjectsHelper extends XMLObjectHelper<SubjectsType> {

    private static final javax.xml.namespace.QName elementQName = SubjectsType.DEFAULT_ELEMENT_NAME;
    private static final SubjectsHelper instance = new SubjectsHelper();

    public static SubjectsType build(List<SubjectType> subjectList) {
        SubjectsType subjects = build();
        
        for (SubjectType subject : subjectList) {
            subjects.getSubjects().add(subject);
        }
        
        return subjects;
    }

    public static SubjectsType build(SubjectType subject) {
        SubjectsType subjects = build();
        subjects.getSubjects().add(subject);
        
        return subjects;
    }

    public static SubjectsType build() {
        return (SubjectsType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static SubjectsHelper getInstance() {
        return instance;
    }

    private SubjectsHelper() {}

}
