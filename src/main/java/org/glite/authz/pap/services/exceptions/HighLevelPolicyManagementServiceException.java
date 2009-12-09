package org.glite.authz.pap.services.exceptions;

public class HighLevelPolicyManagementServiceException extends RuntimeException {

    private static final long serialVersionUID = 1589233385276916830L;

    public HighLevelPolicyManagementServiceException() {
    }

    public HighLevelPolicyManagementServiceException(String message) {
        super(message);
    }

    public HighLevelPolicyManagementServiceException(Throwable cause) {
        super(cause);
    }

    public HighLevelPolicyManagementServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
