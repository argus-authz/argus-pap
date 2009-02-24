package org.glite.authz.pap.common.xacml.wizard.exceptions;

public class PolicySetWizardException extends WizardException {

    private static final long serialVersionUID = 4023263237141085535L;

    public PolicySetWizardException() {
    }

    public PolicySetWizardException(String message) {
        super(message);
    }

    public PolicySetWizardException(Throwable cause) {
        super(cause);
    }

    public PolicySetWizardException(String message, Throwable cause) {
        super(message, cause);
    }

}
