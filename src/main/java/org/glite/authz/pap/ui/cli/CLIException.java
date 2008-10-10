package org.glite.authz.pap.ui.cli;

public class CLIException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CLIException() {
    }

    public CLIException(String message) {
        super(message);
    }

    public CLIException(Throwable cause) {
        super(cause);
    }

    public CLIException(String message, Throwable cause) {
        super(message, cause);
    }

}
