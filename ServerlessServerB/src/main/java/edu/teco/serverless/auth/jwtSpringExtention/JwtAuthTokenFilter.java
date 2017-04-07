package edu.teco.serverless.auth.jwtSpringExtention;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filters the JWT from the header of the HTTP Request from user
 */
public class JwtAuthTokenFilter extends AbstractAuthenticationProcessingFilter {

    /**
     *  How the header looks like.
     */
    @Value("${jwt.header}")
    private String tokenHeader;

    /**
     * Creates a filter.
     */
    public JwtAuthTokenFilter() {
        super("/**");
    }

    /**
     * Start authentication.
     * @param request HTTP request from user
     * @param response HTTP response from server
     * @return Authentication of the request
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(this.tokenHeader);
        if (header == null || !header.startsWith("Bearer ")) {
            return new AnonymousAuthenticationToken("0", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
            //            throw new NoJwtGivenException("No valid JWT token found in request headers. Use " + tokenHeader + ": Bearer <token>");
        }
        String authToken = header.substring(7);
        JwtAuthTokenStringWrapper authRequest = new JwtAuthTokenStringWrapper(authToken);
        return getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Continues if authentication was successful
     * @param request HTTP request from user
     * @param response HTTP response from server
     * @param chain chain of request
     * @param authResult result of authentication
     * @throws IOException thrown from superclass
     * @throws ServletException thrown at big exception
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me, Authorization, x-auth-token");

        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(request, response);
        }
    }
}
