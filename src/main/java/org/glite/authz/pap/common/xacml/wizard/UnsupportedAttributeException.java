package org.glite.authz.pap.common.xacml.wizard;

public class UnsupportedAttributeException extends WizardException {

    private static final long serialVersionUID = 1L;

    public UnsupportedAttributeException() {
        super();
    }

    public UnsupportedAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedAttributeException(String message) {
        super(message);
    }

    public UnsupportedAttributeException(Throwable cause) {
        super(cause);
    }

}
