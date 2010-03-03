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

import org.opensaml.xacml.policy.ApplyType;

public class ApplyHelper extends XMLObjectHelper<ApplyType> {

    public static final String RULE_COMBALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
    public static final String RULE_COMBALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
    public static final String RULE_COMBALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";

    private static final javax.xml.namespace.QName elementQName = ApplyType.DEFAULT_ELEMENT_NAME;

    private static ApplyHelper instance = new ApplyHelper();

    private ApplyHelper() {}

    public static ApplyType buildFunctionAnd() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.AND);

        return apply;
    }

    public static ApplyType buildFunctionAnyOf() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.ANY_OF);

        return apply;
    }

    public static ApplyType buildFunctionAnyOfAll() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.ANY_OF_ALL);

        return apply;
    }

    public static ApplyType buildFunctionAnyOfAny() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.ANY_OF_ANY);

        return apply;
    }

    public static ApplyType buildFunctionNot() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.NOT);
        return apply;
    }

    public static ApplyType buildFunctionOr() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.OR);

        return apply;
    }

    public static ApplyType buildFunctionStringBag() {

        ApplyType apply = (ApplyType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        apply.setFunctionId(Functions.STRING_BAG);

        return apply;
    }

    public static ApplyHelper getInstance() {
        return instance;
    }

}
