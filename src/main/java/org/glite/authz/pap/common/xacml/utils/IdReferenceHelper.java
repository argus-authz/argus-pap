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

import org.opensaml.xacml.policy.IdReferenceType;

public class IdReferenceHelper extends XMLObjectHelper<IdReferenceType> {

    public static enum Type {
        POLICY_ID_REFERENCE, POLICYSET_ID_REFERENCE;
    }

    private static IdReferenceHelper instance = new IdReferenceHelper();
    private static final javax.xml.namespace.QName policyIdReferenceQName = IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME;
    private static final javax.xml.namespace.QName policyIdSetReferenceQName = IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME;

    private IdReferenceHelper() {}

    public static IdReferenceType build(Type type, String value) {
        IdReferenceType idReference;
        if (type == Type.POLICYSET_ID_REFERENCE) {
            idReference = (IdReferenceType) builderFactory.getBuilder(policyIdSetReferenceQName)
                    .buildObject(policyIdSetReferenceQName);
        } else {
            idReference = (IdReferenceType) builderFactory.getBuilder(policyIdReferenceQName)
                    .buildObject(policyIdReferenceQName);
        }
        idReference.setValue(value);
        return idReference;
    }

    public static IdReferenceHelper getInstance() {
        return instance;
    }

    public static boolean isPolicyIdReference(IdReferenceType idReference) {
        if (idReference.getElementQName().equals(policyIdReferenceQName)) {
            return true;
        }
        return false;
    }

    public static boolean isPolicySetIdReference(IdReferenceType idReference) {
        if (idReference.getElementQName().equals(policyIdSetReferenceQName)) {
            return true;
        }
        return false;
    }

}
