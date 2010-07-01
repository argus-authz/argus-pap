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
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;

public class ObligationHelper extends XMLObjectHelper<ObligationType> {

    private static final javax.xml.namespace.QName elementQName = ObligationType.DEFAULT_ELEMENT_QNAME;
    private static final ObligationHelper instance = new ObligationHelper();

    private ObligationHelper() {}

    public static ObligationType build(String obligationId, EffectType effect) {
        ObligationType obligation = (ObligationType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
        obligation.setObligationId(obligationId);
        obligation.setFulfillOn(effect);
        return obligation;
    }
    
    public static void addAttributeAssignment(ObligationType obligation, String attributeId, String value, String dataType) {
        AttributeAssignmentType attributeAssignment = AttributeAssignmentHelper.build(attributeId, value, dataType);
        obligation.getAttributeAssignments().add(attributeAssignment);
    }

    public static ObligationHelper getInstance() {
        return instance;
    }

}
