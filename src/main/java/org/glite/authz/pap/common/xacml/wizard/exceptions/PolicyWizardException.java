package org.glite.authz.pap.common.xacml.wizard.exceptions;

public class PolicyWizardException extends WizardException {

    private static final long serialVersionUID = 3416164468261906332L;

    public PolicyWizardException() {
    }

    public PolicyWizardException(String message) {
        super(message);
    }

    public PolicyWizardException(Throwable cause) {
        super(cause);
    }

    public PolicyWizardException(String message, Throwable cause) {
        super(message, cause);
    }

}
