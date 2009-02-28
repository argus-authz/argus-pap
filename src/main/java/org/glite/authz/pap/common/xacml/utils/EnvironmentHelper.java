package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.EnvironmentMatchType;
import org.opensaml.xacml.policy.EnvironmentType;

public class EnvironmentHelper extends XMLObjectHelper<EnvironmentType> {

    private static final javax.xml.namespace.QName elementQName = EnvironmentType.DEFAULT_ELEMENT_NAME;
    private static EnvironmentHelper instance = new EnvironmentHelper();

    private EnvironmentHelper() {}

    public static EnvironmentType build() {
        return (EnvironmentType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static EnvironmentType build(List<EnvironmentMatchType> actionMatchList) {
        
        if (actionMatchList.isEmpty()) {
            return null;
        }
        
        EnvironmentType environment = build();
        
        for (EnvironmentMatchType environmentMatch : actionMatchList) {
            environment.getEnvrionmentMatches().add(environmentMatch);
        }
        
        return environment;
    }

    public static EnvironmentHelper getInstance() {
        return instance;
    }

}
