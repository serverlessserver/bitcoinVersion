package edu.teco.serverless.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown when no JWT is given when it is needed.
 */
public class NoJwtGivenException extends AuthenticationException {
    /**
     * Inherits error message.
     * @param s error message
     */
    public NoJwtGivenException(String s) {
        super(s);
    }
}
