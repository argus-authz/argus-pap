package org.glite.authz.pap.common.xacml.wizard.exceptions;


public class UnsupportedTargetException extends WizardException {

    private static final long serialVersionUID = -1254011749341728142L;

    public UnsupportedTargetException() {
    }

    public UnsupportedTargetException(String message) {
        super(message);
    }

    public UnsupportedTargetException(Throwable cause) {
        super(cause);
    }

    public UnsupportedTargetException(String message, Throwable cause) {
        super(message, cause);
    }

}
