package edu.teco.serverless.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Thrown when Token is malformed and cannot be parsed.
 */
public class JwtMalformedException extends AuthenticationException {

    /**
     * Inherits error message.
     * @param s error message
     */
    public JwtMalformedException(String s) {

        super(s);
    }
}
