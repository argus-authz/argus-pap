package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.ActionMatchType;
import org.opensaml.xacml.policy.ActionType;

public class ActionHelper extends XMLObjectHelper<ActionType> {

    private static final javax.xml.namespace.QName elementQName = ActionType.DEFAULT_ELEMENT_NAME;
    private static ActionHelper instance = new ActionHelper();

    private ActionHelper() {}

    public static ActionType build() {
        return (ActionType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static ActionType build(List<ActionMatchType> actionMatchList) {

        if (actionMatchList.isEmpty()) {
            return null;
        }

        ActionType action = build();

        for (ActionMatchType actionMatch : actionMatchList) {
            action.getActionMatches().add(actionMatch);
        }

        return action;
    }

    public static ActionHelper getInstance() {
        return instance;
    }

}
