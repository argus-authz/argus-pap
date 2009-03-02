package org.glite.authz.pap.common.xacml.wizard.exceptions;

public class TargetWizardException extends WizardException {

    private static final long serialVersionUID = 9057516887952861270L;

    public TargetWizardException() {
    }

    public TargetWizardException(String message) {
        super(message);
    }

    public TargetWizardException(Throwable cause) {
        super(cause);
    }

    public TargetWizardException(String message, Throwable cause) {
        super(message, cause);
    }

}
