package org.glite.authz.pap.common.utils.xacml;

import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EnvironmentsType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xml.Configuration;

public class TargetHelper extends XACMLHelper<TargetType> {
    private static TargetHelper instance = null;

    public static TargetType buildAnyTarget() {
	return (TargetType) Configuration.getBuilderFactory().getBuilder(
		TargetType.DEFAULT_ELEMENT_NAME).buildObject(
		TargetType.DEFAULT_ELEMENT_NAME);
    }

    public static TargetType build(SubjectsType subjects, ActionsType actions,
	    ResourcesType resources, EnvironmentsType environments) {
	TargetType target = (TargetType) Configuration.getBuilderFactory()
		.getBuilder(TargetType.DEFAULT_ELEMENT_NAME).buildObject(
			TargetType.DEFAULT_ELEMENT_NAME);
	if (subjects == null) {
	    subjects = SubjectsHelper.buildAnysubject();
	}
	if (actions == null) {
	    actions = ActionsHelper.buildAnyAction();
	}
	if (resources == null) {
	    resources = (ResourcesType) Configuration.getBuilderFactory()
		    .getBuilder(ResourcesType.DEFAULT_ELEMENT_NAME)
		    .buildObject(ResourcesType.DEFAULT_ELEMENT_NAME);
	}
	if (environments == null) {
	    environments = (EnvironmentsType) Configuration.getBuilderFactory()
		    .getBuilder(EnvironmentsType.DEFAULT_ELEMENT_NAME)
		    .buildObject(EnvironmentsType.DEFAULT_ELEMENT_NAME);
	}
	target.setSubjects(subjects);
	target.setActions(actions);
	target.setResources(resources);
	target.setEnvironments(environments);
	return target;
    }

    public static TargetHelper getInstance() {
	if (instance == null) {
	    instance = new TargetHelper();
	}
	return instance;
    }

    private TargetHelper() {
    }
}
