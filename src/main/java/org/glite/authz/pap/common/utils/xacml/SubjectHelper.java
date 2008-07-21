package org.glite.authz.pap.common.utils.xacml;

import java.util.List;

import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xml.Configuration;

public class SubjectHelper extends XACMLHelper<SubjectType> {
    private static SubjectHelper instance = null;

    public static SubjectHelper getInstance() {
	if (instance == null) {
	    instance = new SubjectHelper();
	}
	return instance;
    }

    private SubjectHelper() {
    }

    public static SubjectType build() {
	return (SubjectType) Configuration.getBuilderFactory().getBuilder(
		SubjectType.DEFAULT_ELEMENT_NAME).buildObject(
		SubjectType.DEFAULT_ELEMENT_NAME);
    }

    public static SubjectType build(List<SubjectMatchType> subjectMatchList) {
	SubjectType subject = build();
	for (SubjectMatchType subjectMatch : subjectMatchList) {
	    subject.getSubjectMatches().add(subjectMatch);
	}
	return subject;
    }

}
