package edu.teco.serverless.auth.authFacade;

import edu.teco.serverless.auth.jwtContentValidation.ContentValidator;
import edu.teco.serverless.auth.jwtCreation.SubtokenCreator;
import edu.teco.serverless.auth.jwtCreation.TokenCreator;
import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Implementation of the AuthFacade.
 */
@Component
public class AuthFacadeImpl implements AuthFacade {


    private TokenCreator tokenCreator;
    private SubtokenCreator subtokenCreator;
    private ContentValidator contentValidator;

    /**
     * Autowired constructor to initialize Objects.
     * @param tokenCreator TokenCreator to create mastertokens
     * @param subtokenCreator SubtokenCreator to create subtokens
     * @param contentValidator ContentValidator to validate tokens
     */
    @Autowired
    public AuthFacadeImpl(TokenCreator tokenCreator, SubtokenCreator subtokenCreator, ContentValidator contentValidator) {
        this.tokenCreator = tokenCreator;
        this.subtokenCreator = subtokenCreator;
        this.contentValidator = contentValidator;
    }

    /**
     * Implementation of generateSubToken
     * @param principal Object given from Spring
     * @param expiryDate expiryDate given from User through Spring.
     * @return subtoken if valid inputs, else null
     * @throws UnsupportedEncodingException
     */
    @Override
    public String generateSubToken(Object principal, String expiryDate) throws UnsupportedEncodingException {
        AccessRights rights = (AccessRights) principal;
        return subtokenCreator.generateSubToken(rights, expiryDate);
    }

    /**
     * Implementation of generateMasterToken
     * @param authKey AuthKey Object from Runtime
     * @param id Identifier Object of the Lambda matched to the AuthKey of Runtime
     * @return mastertoken if valid inputs, else null
     * @throws UnsupportedEncodingException
     */
    @Override
    public String generateMasterToken(AuthKey authKey, Identifier id) throws UnsupportedEncodingException {
        AccessRights rights = new AccessRights(id.getIdentifier(), null, AuthorityUtils.createAuthorityList("ROLE_MASTER"), authKey.getAuthKey());
        String token = tokenCreator.generateToken(rights);
        return token;
    }

    /**
     * Implementation of validate authkeys
     * @param principal Object given from Spring
     * @param id
     * @return true if authkey is valid, else false
     * @throws LambdaNotFoundException
     */
    @Override
    public boolean validate(Object principal, Identifier id) throws LambdaNotFoundException {
        AccessRights accessRights = (AccessRights) principal;
        return contentValidator.validate(accessRights, id);
    }
}
