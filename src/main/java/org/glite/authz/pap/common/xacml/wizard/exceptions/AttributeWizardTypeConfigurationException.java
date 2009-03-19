package org.glite.authz.pap.common.xacml.wizard.exceptions;

public class AttributeWizardTypeConfigurationException extends WizardException {

    private static final long serialVersionUID = 4907974827334194647L;

    public AttributeWizardTypeConfigurationException() {
    }

    public AttributeWizardTypeConfigurationException(String message) {
        super(message);
    }

    public AttributeWizardTypeConfigurationException(Throwable cause) {
        super(cause);
    }

    public AttributeWizardTypeConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
