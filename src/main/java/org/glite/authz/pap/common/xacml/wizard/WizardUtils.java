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

package org.glite.authz.pap.common.xacml.wizard;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.opensaml.xacml.ctx.AttributeType;

public class WizardUtils {

    public static String generateId(String prefix) {

        String id = generateUUID();

        if (prefix == null) {
            return id;
        }

        if (prefix.length() == 0) {
            return id;
        }

        return prefix + "-" + generateUUID();
    }

    public static List<AttributeType> getAttributes(List<AttributeWizard> list, AttributeWizardType.TargetElement type) {

        List<AttributeType> resultList = new LinkedList<AttributeType>();

        for (AttributeWizard attribute : list) {
            if (type.equals(attribute.getTargetElementType())) {
                resultList.add(attribute.getXACML());
            }
        }
        return resultList;
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
