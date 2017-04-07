package edu.teco.serverless.auth.jwtCreation;

import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Creates and parses Tokens.
 */

@Component

public class TokenCreator {
    /**
     * Secret from application.yml, mastersecret for all tokens
     */
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private Environment env;
    public String generateToken(AccessRights rights) throws UnsupportedEncodingException {
        Claims claims = Jwts.claims().setSubject(rights.getLambdaName());
        claims.put("role", String.join(",", AuthorityUtils.authorityListToSet(rights.getAuthorities())));
        if (rights.getExpiryDate() != null) {
            claims.put("expiryDate", rights.getExpiryDate().getTime() + "");
        }
        claims.put("dockerHash", rights.getAuthKey());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret.getBytes("UTF-8"))
                .compact();
    }

    /**
     * Helper method for retrieveUser
     *
     * @param token JWT
     * @return AccessRights object
     */
    public AccessRights parseToken(String token) {
        AccessRights rights = null;
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret.getBytes("UTF-8"))
                    .parseClaimsJws(token)
                    .getBody();
            List<GrantedAuthority> authorityList =
                    AuthorityUtils.commaSeparatedStringToAuthorityList((String) body.get("role"));
            Date expiryDate = null;
            if (body.get("expiryDate") != null) {
                expiryDate = new Date(Long.parseLong((String) body.get("expiryDate")));
            }
            rights = new AccessRights(body.getSubject(), expiryDate, authorityList, (String) body.get("dockerHash"));
        } catch (JwtException e) {
            //e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }
        return rights;
    }
}
