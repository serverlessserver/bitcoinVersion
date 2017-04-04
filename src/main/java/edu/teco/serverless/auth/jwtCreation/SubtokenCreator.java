package edu.teco.serverless.auth.jwtCreation;

import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creates Subtokens.
 */
@Component
public class SubtokenCreator {
    @Autowired
    private TokenCreator tokenCreator ;

    /**
     * Constructor
     */
    public SubtokenCreator() {
    }

    /**
     * Sets the Tokencreator. Mainly used for tests.
     * @param tokenCreator
     */
    public SubtokenCreator(TokenCreator tokenCreator) {
        this.tokenCreator = tokenCreator;
    }

    /**
     * Generates Subtokens. Parses date into Date object and then generates a subtoken with an expirydate
     * @param accessRights mastertoken parsed in a Object
     * @param expiryDate expiryDate in form yyyy-MM-dd hh-mm-ss
     * @return subtoken as String if everything works, else null
     * @throws UnsupportedEncodingException
     */
    public String generateSubToken(AccessRights accessRights, String expiryDate) throws UnsupportedEncodingException {
        String name = accessRights.getLambdaName();
        String hash = accessRights.getAuthKey();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh-mm-ss");
        Date exp;
        try {
            exp = ft.parse(expiryDate);
        } catch (ParseException e) {
            return null;
        }
        AccessRights rights = new AccessRights(name, exp, AuthorityUtils.createAuthorityList("ROLE_SUB"), hash);
        return tokenCreator.generateToken(rights);
    }
}
