package org.glite.authz.pap.ui.cli;

public class HelpMessageException extends CLIException {

    private static final long serialVersionUID = 1L;

    public HelpMessageException() {
    }

    public HelpMessageException(String message) {
        super(message);
    }

    public HelpMessageException(Throwable cause) {
        super(cause);
    }

    public HelpMessageException(String message, Throwable cause) {
        super(message, cause);
    }

}
