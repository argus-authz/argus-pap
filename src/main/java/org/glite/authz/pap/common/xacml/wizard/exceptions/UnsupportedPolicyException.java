package org.glite.authz.pap.common.xacml.wizard.exceptions;


public class UnsupportedPolicyException extends WizardException {

    private static final long serialVersionUID = 1L;

    public UnsupportedPolicyException() {
    }

    public UnsupportedPolicyException(String message) {
        super(message);
    }

    public UnsupportedPolicyException(Throwable cause) {
        super(cause);
    }

    public UnsupportedPolicyException(String message, Throwable cause) {
        super(message, cause);
    }

}
