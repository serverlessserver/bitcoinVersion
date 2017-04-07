package edu.teco.serverless.payment;

/**
 * Created by Kolner on 3/13/2017.
 */
public class InvalidPaymentException extends Exception {
    public InvalidPaymentException() {
        super();
    }

    public InvalidPaymentException(String message) {
        super(message);
    }
}
