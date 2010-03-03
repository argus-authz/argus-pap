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

import org.opensaml.xacml.policy.EnvironmentType;
import org.opensaml.xacml.policy.EnvironmentsType;

public class EnvironmentsHelper extends XMLObjectHelper<EnvironmentsType> {

    private static final javax.xml.namespace.QName elementQName = EnvironmentsType.DEFAULT_ELEMENT_NAME;
    private static EnvironmentsHelper instance = new EnvironmentsHelper();

    private EnvironmentsHelper() {}

    public static EnvironmentsType build(EnvironmentType environment) {

        if (environment == null) {
            return null;
        }

        EnvironmentsType environments = buildAnyEnvironment();

        if (environment != null) {
            environments.getEnvrionments().add(environment);
        }
        return environments;
    }

    public static EnvironmentsType build(List<EnvironmentType> environmentList) {

        if (environmentList.isEmpty()) {
            return null;
        }

        EnvironmentsType environments = buildAnyEnvironment();

        for (EnvironmentType environment : environmentList) {
            environments.getEnvrionments().add(environment);
        }
        return environments;
    }

    public static EnvironmentsType buildAnyEnvironment() {
        return (EnvironmentsType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static EnvironmentsHelper getInstance() {
        return instance;
    }
}
