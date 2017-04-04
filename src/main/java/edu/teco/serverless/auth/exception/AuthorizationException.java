package edu.teco.serverless.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Kristina on 17/02/2017.
 */
public class AuthorizationException extends AuthenticationException {

    /**
     * Inherits error message.
     * @param s error message
     */
    public AuthorizationException(String s) {

        super(s);
    }
}
