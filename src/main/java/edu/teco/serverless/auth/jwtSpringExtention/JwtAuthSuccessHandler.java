package edu.teco.serverless.auth.jwtSpringExtention;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Stub class.
 */
public class JwtAuthSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * Stub method
     * @param request not used
     * @param response not used
     * @param authentication not used
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    }
}
