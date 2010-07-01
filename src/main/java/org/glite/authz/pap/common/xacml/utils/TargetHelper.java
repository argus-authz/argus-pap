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
