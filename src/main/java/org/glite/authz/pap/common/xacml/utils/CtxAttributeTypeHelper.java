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

import org.opensaml.xacml.ctx.AttributeType;

public class CtxAttributeTypeHelper extends XMLObjectHelper<AttributeType> {

    private static final javax.xml.namespace.QName elementQName = AttributeType.DEFAULT_ELEMENT_NAME;
    private static CtxAttributeTypeHelper instance = new CtxAttributeTypeHelper();

    private CtxAttributeTypeHelper() {}
    
    public static AttributeType build(String attributeId, String dataType, String value) {

        AttributeType attribute = (AttributeType) builderFactory.getBuilder(elementQName).buildObject(
                elementQName);

        attribute.setAttributeID(attributeId);
        attribute.setDataType(dataType);
        attribute.getAttributeValues().add(CtxAttributeValueHelper.build(value));

        return attribute;

    }

    public static String getFirstValue(AttributeType attribute) {
        
        org.opensaml.xacml.ctx.AttributeValueType ctxAttributeValue = (org.opensaml.xacml.ctx.AttributeValueType) attribute
        .getAttributeValues().get(0);
        
        return ctxAttributeValue.getValue();
        
    }

    public static CtxAttributeTypeHelper getInstance() {
        return instance;
    }

}
