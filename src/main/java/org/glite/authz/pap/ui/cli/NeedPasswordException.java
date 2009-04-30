package org.glite.authz.pap.ui.cli;

public class NeedPasswordException extends CLIException {

    private static final long serialVersionUID = 5836692431027875569L;

    public NeedPasswordException() {
    }

    public NeedPasswordException(String message) {
        super(message);
    }

    public NeedPasswordException(Throwable cause) {
        super(cause);
    }

    public NeedPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

}
