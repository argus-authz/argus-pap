package org.glite.authz.pap.distribution;

public class DistributionConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DistributionConfigurationException() {}

    public DistributionConfigurationException(String message) {
        super(message);
    }

    public DistributionConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributionConfigurationException(Throwable cause) {
        super(cause);
    }

}
