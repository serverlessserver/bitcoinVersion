package edu.teco.serverless.payment;

/**
 * Created by Kolner on 3/13/2017.
 */
public class InternalPaymentServerException extends Exception {
    public InternalPaymentServerException() {
        super();
    }

    public InternalPaymentServerException(String message) {
        super(message);
    }
}
