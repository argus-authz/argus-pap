package org.glite.authz.pap.common.xacml.wizard;

import org.glite.authz.pap.common.xacml.utils.CtxAttributeTypeHelper;
import org.glite.authz.pap.common.xacml.wizard.exceptions.UnsupportedAttributeException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeWizard {

    private static final Logger log = LoggerFactory.getLogger(AttributeWizard.class);

    private AttributeWizardType attributeWizardType;
    private AttributeWizardTypeConfiguration attributeWizardTypeConfiguration = AttributeWizardTypeConfiguration.getInstance();

    private String value;

    public AttributeWizard(AttributeAssignmentType attributeAssignment) {
        String xacmlId = attributeAssignment.getAttributeId();
        try {
            attributeWizardType = attributeWizardTypeConfiguration.getByXACMLId(xacmlId);
        } catch (UnsupportedAttributeException e) {
            attributeWizardType = attributeWizardTypeConfiguration.getUnrecognizedAttributeWizard(xacmlId,
                                                                                                  attributeAssignment.getDataType());
        }
        this.value = attributeAssignment.getValue();
    }

    public AttributeWizard(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        try {
            attributeWizardType = attributeWizardTypeConfiguration.getByXACMLId(xacmlId);
        } catch (UnsupportedAttributeException e) {
            attributeWizardType = attributeWizardTypeConfiguration.getUnrecognizedAttributeWizard(xacmlId,
                                                                                                  attribute.getDataType());
        }
        this.value = CtxAttributeTypeHelper.getFirstValue(attribute);
    }

    public AttributeWizard(AttributeWizardType attributeWizardType, String value) {
        this.attributeWizardType = attributeWizardType;
        this.value = value;
    }

    /**
     * Constructor.
     * 
     * @param idEqualValue a string in the form "id=value".
     * 
     * @throws UnsupportedAttributeException if the id is not supported or the given string is not
     *             in the right form.
     */
    public AttributeWizard(String idEqualValue) {
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

        this.value = value;
    }

    public AttributeWizard(String identifier, String value) {

        attributeWizardType = attributeWizardTypeConfiguration.getById(identifier);

        if (attributeWizardType == null) {
            throw new UnsupportedAttributeException("id=" + identifier);
        }

        this.value = value;
    }

    public static boolean isActionAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardTypeConfiguration.getInstance()
                                               .xacmlIdMatchesTargetElement(xacmlId,
                                                                            AttributeWizardType.TargetElement.ACTION);
    }

    public static boolean isEnvironmentAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardTypeConfiguration.getInstance()
                                               .xacmlIdMatchesTargetElement(xacmlId,
                                                                            AttributeWizardType.TargetElement.ENVIRONMENT);
    }

    public static boolean isResouceAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardTypeConfiguration.getInstance()
                                               .xacmlIdMatchesTargetElement(xacmlId,
                                                                            AttributeWizardType.TargetElement.RESOURCE);
    }

    public static boolean isSubjectAttribute(AttributeType attribute) {
        String xacmlId = attribute.getAttributeID();
        return AttributeWizardTypeConfiguration.getInstance()
                                               .xacmlIdMatchesTargetElement(xacmlId,
                                                                            AttributeWizardType.TargetElement.SUBJECT);
    }

    public boolean equals(Object object) {

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

    public AttributeWizardType.TargetElement getTargetElementType() {
        return attributeWizardType.getTargetElement();
    }

    public String getValue() {
        return value;
    }

    public AttributeType getXACML() {
        return CtxAttributeTypeHelper.build(attributeWizardType.getXacmlId(),
                                            attributeWizardType.getDataType(),
                                            value);
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
}
