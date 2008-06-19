package org.glite.authz.pap.common.utils.xacml;

import java.util.List;

import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xml.Configuration;

public class SubjectsHelper extends XACMLHelper<SubjectsType> {
	private static SubjectsHelper instance = null;

	public static SubjectsHelper getInstance() {
		if (instance == null) {
			instance = new SubjectsHelper();
		}
		return instance;
	}

	private SubjectsHelper() {
	}

	public static SubjectsType buildAnysubject() {
		return (SubjectsType) Configuration.getBuilderFactory().getBuilder(
				SubjectsType.DEFAULT_ELEMENT_NAME).buildObject(
				SubjectsType.DEFAULT_ELEMENT_NAME);
	}
	
	public static SubjectsType build(List<SubjectType> subjectList) {
		SubjectsType subjects = buildAnysubject();
		for (SubjectType subject:subjectList) {
			subjects.getSubjects().add(subject);
		}
		return subjects;
	}
	
	public static SubjectsType build(SubjectType subject) {
		SubjectsType subjects = buildAnysubject();
		subjects.getSubjects().add(subject);
		return subjects;
	}

}
