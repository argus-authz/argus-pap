package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ActionsType;

public class ActionsHelper extends XMLObjectHelper<ActionsType> {

    private static final javax.xml.namespace.QName elementQName = ActionsType.DEFAULT_ELEMENT_NAME;
    private static ActionsHelper instance = new ActionsHelper();

    private ActionsHelper() {}

    public static ActionsType build() {
        return (ActionsType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ActionsType build(ActionType action) {

        if (action == null) {
            return null;
        }

        ActionsType actions = build();
        actions.getActions().add(action);
        
        return actions;
    }

    public static ActionsType build(List<ActionType> actionList) {
        
        if (actionList.isEmpty()) {
            return null;
        }

        ActionsType actions = build();

        for (ActionType action : actionList) {
            actions.getActions().add(action);
        }
        return actions;
    }

    public static ActionsHelper getInstance() {
        return instance;
    }

}
