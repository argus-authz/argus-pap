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

import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.common.xacml.utils.CtxAttributeTypeHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedAttributeException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.impl.OpensslNameUtils;

public class AttributeWizard {

    private static final Logger log = LoggerFactory.getLogger(AttributeWizard.class);

    private static final String X509_SUBJECT_DATA_TYPE = "urn:oasis:names:tc:xacml:1.0:data-type:x500Name";

    private AttributeWizardType attributeWizardType;
    private AttributeWizardTypeConfiguration attributeWizardTypeConfiguration = AttributeWizardTypeConfiguration
	    .getInstance();

    private String value;

    public AttributeWizard(final AttributeAssignmentType attributeAssignment) {
	String xacmlId = attributeAssignment.getAttributeId();
	try {
	    attributeWizardType = attributeWizardTypeConfiguration.getByXACMLId(xacmlId);
	} catch (UnsupportedAttributeException e) {
	    attributeWizardType = attributeWizardTypeConfiguration.getUnrecognizedAttributeWizard(xacmlId,
		    attributeAssignment.getDataType());
	}
	this.value = attributeAssignment.getValue();
    }

    public AttributeWizard(final AttributeType attribute) {
	String xacmlId = attribute.getAttributeID();
	try {
	    attributeWizardType = attributeWizardTypeConfiguration.getByXACMLId(xacmlId);
	} catch (UnsupportedAttributeException e) {
	    attributeWizardType = attributeWizardTypeConfiguration.getUnrecognizedAttributeWizard(xacmlId,
		    attribute.getDataType());
	}
	this.value = CtxAttributeTypeHelper.getFirstValue(attribute);
    }

    public AttributeWizard(final AttributeWizardType attributeWizardType, final String value) {
	this.attributeWizardType = attributeWizardType;

	setValue(value);
    }

    /**
     * Constructor.
     * 
     * @param idEqualValue
     *            a string in the form "id=value".
     * 
     * @throws UnsupportedAttributeException
     *             if the id is not supported or the given string is not in the
     *             right form.
     */
    public AttributeWizard(final String idEqualValue) {
	int index = idEqualValue.indexOf('=');

	if (index == -1) {
	    throw new UnsupportedAttributeException("invalid \"id=value\" string: " + idEqualValue);
	}

	String id = idEqualValue.substring(0, index);
	String value = idEqualValue.substring(index + 1);

	attributeWizardType = attributeWizardTypeConfiguration.getById(id);

	if (attributeWizardType == null) {
	    throw new UnsupportedAttributeException("id=" + id);
	}

	setValue(value);
    }

    public AttributeWizard(final String identifier, final String value) {

	attributeWizardType = attributeWizardTypeConfiguration.getById(identifier);

	if (attributeWizardType == null) {
	    throw new UnsupportedAttributeException("id=" + identifier);
	}

	setValue(value);
    }

    public static boolean isActionAttribute(final AttributeType attribute) {
	String xacmlId = attribute.getAttributeID();
	return AttributeWizardTypeConfiguration.getInstance().xacmlIdMatchesTargetElement(xacmlId,
		AttributeWizardType.TargetElement.ACTION);
    }

    public static boolean isEnvironmentAttribute(final AttributeType attribute) {
	String xacmlId = attribute.getAttributeID();
	return AttributeWizardTypeConfiguration.getInstance().xacmlIdMatchesTargetElement(xacmlId,
		AttributeWizardType.TargetElement.ENVIRONMENT);
    }

    public static boolean isResouceAttribute(final AttributeType attribute) {
	String xacmlId = attribute.getAttributeID();
	return AttributeWizardTypeConfiguration.getInstance().xacmlIdMatchesTargetElement(xacmlId,
		AttributeWizardType.TargetElement.RESOURCE);
    }

    public static boolean isSubjectAttribute(final AttributeType attribute) {
	String xacmlId = attribute.getAttributeID();
	return AttributeWizardTypeConfiguration.getInstance().xacmlIdMatchesTargetElement(xacmlId,
		AttributeWizardType.TargetElement.SUBJECT);
    }

    @Override
    public boolean equals(final Object object) {

	if (!(object instanceof AttributeWizard)) {
	    log.trace("equals(): false. Not an AttributeWizard: " + object.getClass().getName());
	    return false;
	}

	AttributeWizard attributeWizard = (AttributeWizard) object;

	if (!(this.attributeWizardType.equals(attributeWizard.getAttributeWizardType()))) {
	    return false;
	}

	if (!(this.value.equals(attributeWizard.getValue()))) {
	    log.trace("equals(): false. value1=" + this.value + " value2=" + attributeWizard.getValue());
	    return false;
	}

	return true;
    }

    public AttributeWizardType getAttributeWizardType() {
	return attributeWizardType;
    }

    public String getDataType() {
	return attributeWizardType.getDataType();
    }

    public String getId() {
	return attributeWizardType.getId();
    }

    public String getMatchfunction() {
	return attributeWizardType.getMatchFunction();
    }

    public String getMatchFunctionDataType() {
	return attributeWizardType.getMatchFunctionDataType();
    }

    public AttributeWizardType.TargetElement getTargetElementType() {
	return attributeWizardType.getTargetElement();
    }

    public String getValue() {
	return value;
    }

    public AttributeType getXACML() {
	return CtxAttributeTypeHelper.build(attributeWizardType.getXacmlId(), attributeWizardType.getDataType(), value);
    }

    public String getXacmlId() {
	return attributeWizardType.getXacmlId();
    }

    public boolean isActionAttribute() {
	if (AttributeWizardType.TargetElement.ACTION.equals(attributeWizardType.getTargetElement()))
	    return true;
	return false;
    }

    public boolean isEnvironmentAttribute() {
	if (AttributeWizardType.TargetElement.ENVIRONMENT.equals(attributeWizardType.getTargetElement()))
	    return true;
	return false;
    }

    public boolean isResourceAttribute() {
	if (AttributeWizardType.TargetElement.RESOURCE.equals(attributeWizardType.getTargetElement()))
	    return true;
	return false;
    }

    public boolean isSubjectAttribute() {
	if (AttributeWizardType.TargetElement.SUBJECT.equals(attributeWizardType.getTargetElement()))
	    return true;
	return false;
    }

    public String toFormattedString() {
	return attributeWizardType.getId() + "=\"" + value + "\"";
    }

    private void setValue(final String pValue) {
	String lValue = pValue;

	if (attributeWizardType.getDataType().equals(X509_SUBJECT_DATA_TYPE)) {
	    if (lValue.isEmpty()) {
		throw new PAPException("The string passed as argument is empty!");
	    }

	    if (lValue.startsWith("/")) {
		try {
		    lValue = OpensslNameUtils.opensslToRfc2253(pValue);
		} catch (IllegalArgumentException e) {
		    throw new PAPException("The string passed as argument is not a valid certificate subject!", e);
		}
	    }
	}

	this.value = lValue;
    }
}
