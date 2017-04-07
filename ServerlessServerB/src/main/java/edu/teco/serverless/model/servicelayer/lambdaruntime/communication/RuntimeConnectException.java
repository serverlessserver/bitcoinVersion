package edu.teco.serverless.model.servicelayer.lambdaruntime.communication;

/**
 * Exception class for errors when sending requests to the runtime
 */
public class RuntimeConnectException extends Exception {
    public RuntimeConnectException() {
        super();
    }

    public RuntimeConnectException(String message) {
        super(message);
    }
}
