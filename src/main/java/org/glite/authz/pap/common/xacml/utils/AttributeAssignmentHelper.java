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

import org.opensaml.xacml.policy.AttributeAssignmentType;

public class AttributeAssignmentHelper extends XMLObjectHelper<AttributeAssignmentType> {

    private static final javax.xml.namespace.QName elementQName = AttributeAssignmentType.DEFAULT_ELEMENT_NAME;
    private static final AttributeAssignmentHelper instance = new AttributeAssignmentHelper();

    private AttributeAssignmentHelper() {}

    public static AttributeAssignmentType build(String attributeId, String value, String dataType) {
        AttributeAssignmentType attributeAssignment = (AttributeAssignmentType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        attributeAssignment.setAttributeId(attributeId);
        attributeAssignment.setValue(value);
        attributeAssignment.setDataType(dataType);
        return attributeAssignment;
    }
    
    public static AttributeAssignmentHelper getInstance() {
        return instance;
    }

}
