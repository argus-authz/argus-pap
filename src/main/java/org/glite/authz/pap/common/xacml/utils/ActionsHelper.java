package org.glite.authz.pap.common.xacml.utils;

import org.opensaml.xacml.policy.ActionsType;

public class ActionsHelper extends XMLObjectHelper<ActionsType> {

    private static final javax.xml.namespace.QName elementQName = ActionsType.DEFAULT_ELEMENT_NAME;
    private static ActionsHelper instance = new ActionsHelper();

    public static ActionsType buildAnyAction() {
        return (ActionsType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ActionsHelper getInstance() {
        return instance;
    }

    private ActionsHelper() {}

}
