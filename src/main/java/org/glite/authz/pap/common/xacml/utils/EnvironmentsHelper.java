package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.EnvironmentType;
import org.opensaml.xacml.policy.EnvironmentsType;

public class EnvironmentsHelper extends XMLObjectHelper<EnvironmentsType> {

    private static final javax.xml.namespace.QName elementQName = EnvironmentsType.DEFAULT_ELEMENT_NAME;
    private static EnvironmentsHelper instance = new EnvironmentsHelper();

    private EnvironmentsHelper() {}

    public static EnvironmentsType buildAnyEnvironment() {
        return (EnvironmentsType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static EnvironmentsType build(List<EnvironmentType> environmentList) {

        EnvironmentsType environments = buildAnyEnvironment();

        for (EnvironmentType environment : environmentList) {
            environments.getEnvrionments().add(environment);
        }
        return environments;
    }

    public static EnvironmentsType build(EnvironmentType environment) {

        EnvironmentsType environments = buildAnyEnvironment();

        if (environment != null) {
            environments.getEnvrionments().add(environment);
        }
        return environments;
    }

    public static EnvironmentsHelper getInstance() {
        return instance;
    }

}
