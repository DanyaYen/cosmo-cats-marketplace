package com.cosmocats.marketplace.exception;

public class FeatureNotAvailableException extends RuntimeException {
    public FeatureNotAvailableException(String featureName) {
        super("Feature '" + featureName + "' is currently disabled");
    }
}
