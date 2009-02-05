package org.glite.authz.pap.ui.wizard;

public class WizardException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WizardException() {
    }

    public WizardException(String message) {
        super(message);
    }

    public WizardException(Throwable cause) {
        super(cause);
    }

    public WizardException(String message, Throwable cause) {
        super(message, cause);
    }

}
