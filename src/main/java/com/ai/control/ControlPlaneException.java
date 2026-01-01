package com.ai.control;

/** Exception thrown when control plane operations fail. */
public class ControlPlaneException extends RuntimeException {

    public ControlPlaneException(String message) {
        super(message);
    }

    public ControlPlaneException(String message, Throwable cause) {
        super(message, cause);
    }
}
