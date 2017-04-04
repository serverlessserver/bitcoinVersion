package edu.teco.serverless.auth.jwtSpringExtention;

import edu.teco.serverless.auth.exception.JwtMalformedException;
import edu.teco.serverless.auth.jwtCreation.TokenCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Provides Authentication. Checks if JWT is valid and creates a AccessRights object if so.
 */
@Component
public class JwtAuthProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private TokenCreator tokenCreator;

    /**
     * Returns support for JwtAuthTokenStringWrapper.class
     * @param authentication which class should be used for authentication
     * @return true if authentication is JwtAuthTokenStringWrapper
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthTokenStringWrapper.class.isAssignableFrom(authentication));
    }

    /**
     * Stub method
     *
     * @param userDetails no use
     * @param authentication no use
     * @throws AuthenticationException not thrown
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    /**
     * Parses a String token to AccessRights Object, checks if valid
     *
     * @param username not used, only defined as stub, null should be passed
     * @param authentication JwtAuthTokenStringWrapper token (extends UsernamePasswordAuthenticationToken)
     * @return AccessRights Object if valid token is passed at authentication
     * @throws AuthenticationException if String token is not valid
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        JwtAuthTokenStringWrapper jwtAuthenticationToken = (JwtAuthTokenStringWrapper) authentication;
        String token = jwtAuthenticationToken.getToken();
        AccessRights rights = tokenCreator.parseToken(token);
        if (rights == null) {
            throw new JwtMalformedException("Invalid JWT.");
        }
        return rights;
    }
}
