package edu.teco.serverless.auth.jwtSpringExtention;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Wrapper for JWT String.
 */
public class JwtAuthTokenStringWrapper extends UsernamePasswordAuthenticationToken {

    private String token;

    /**
     * Creates wrapper for Token >String< to use the JwtAuthTokenFilter.
     * @param token JWT as String
     */
    public JwtAuthTokenStringWrapper(String token) {
        super(null, null);
        this.token = token;
    }

    /**
     * Gets Token.
     * @return Token as String
     */
    public String getToken() {
        return token;
    }

    /**
     * Stub method.
     * @return null
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Stub method.
     * @return null
     */
    @Override
    public Object getPrincipal() {
        return null;
    }
}
