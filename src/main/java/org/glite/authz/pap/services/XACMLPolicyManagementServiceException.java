package org.glite.authz.pap.services;

public class XACMLPolicyManagementServiceException extends RuntimeException {

    private static final long serialVersionUID = 1589233385276916830L;

    public XACMLPolicyManagementServiceException() {
    }

    public XACMLPolicyManagementServiceException(String message) {
        super(message);
    }

    public XACMLPolicyManagementServiceException(Throwable cause) {
        super(cause);
    }

    public XACMLPolicyManagementServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
