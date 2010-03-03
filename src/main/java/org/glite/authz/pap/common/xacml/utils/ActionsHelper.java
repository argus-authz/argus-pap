/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
