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

import org.opensaml.xacml.policy.DescriptionType;

public class DescriptionTypeHelper extends XMLObjectHelper<DescriptionType> {

    private static final javax.xml.namespace.QName elementQName = DescriptionType.DEFAULT_ELEMENT_NAME;
    private static DescriptionTypeHelper instance = new DescriptionTypeHelper();

    private DescriptionTypeHelper() {}

    public static DescriptionType build(String value) {

        DescriptionType description = (DescriptionType) builderFactory.getBuilder(elementQName).buildObject(elementQName);

        description.setValue(value);

        return description;

    }

    public static DescriptionTypeHelper getInstance() {
        return instance;
    }

}
