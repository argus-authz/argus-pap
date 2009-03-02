package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EnvironmentsType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

public class TargetHelper extends XMLObjectHelper<TargetType> {

    private static final javax.xml.namespace.QName elementQName = TargetType.DEFAULT_ELEMENT_NAME;
    private static TargetHelper instance = new TargetHelper();

    private TargetHelper() {}

    public static TargetType build() {
        return (TargetType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static TargetType build(SubjectsType subjects, ActionsType actions, ResourcesType resources,
            EnvironmentsType environments) {

        TargetType target = (TargetType) builderFactory.getBuilder(elementQName).buildObject(elementQName);

        target.setSubjects(subjects);
        target.setActions(actions);
        target.setResources(resources);
        target.setEnvironments(environments);
        return target;
    }

    public static TargetHelper getInstance() {
        return instance;
    }
}
