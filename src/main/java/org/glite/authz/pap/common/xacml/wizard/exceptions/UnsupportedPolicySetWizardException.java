package org.glite.authz.pap.common.xacml.wizard.exceptions;

public class UnsupportedPolicySetWizardException extends PolicySetWizardException {

    private static final long serialVersionUID = -5103620818639643691L;

    public UnsupportedPolicySetWizardException() {
    }

    public UnsupportedPolicySetWizardException(String message) {
        super(message);
    }

    public UnsupportedPolicySetWizardException(Throwable cause) {
        super(cause);
    }

    public UnsupportedPolicySetWizardException(String message, Throwable cause) {
        super(message, cause);
    }

}
