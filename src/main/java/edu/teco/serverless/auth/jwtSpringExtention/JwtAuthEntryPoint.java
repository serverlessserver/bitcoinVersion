package edu.teco.serverless.auth.jwtSpringExtention;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * Entry Point for Tokens. Used by JwtAuthProvider if authentication fails.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
     * Invoked when user tries to access a secured REST resource without supplying any credentials. 401 Unauthorized is sent.
     *
     * @param request HTTP request sent by user
     * @param response HTTP response sent by server
     * @param authException Specific authentication exception with more information
     * @throws IOException handled by spring
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
