package edu.teco.serverless.auth.authFacade;

import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;

import java.io.UnsupportedEncodingException;

/**
 * Facade to define the autheentication.
 */
public interface AuthFacade {
    /**
     * Defines the interface for the generation of subtokens.
     * @param principal Object given from Spring
     * @param expiryDate expiryDate given from User through Spring.
     * @return valid subtoken if principal and expiryDate are valid, else null
     * @throws UnsupportedEncodingException
     */
    public String generateSubToken(Object principal, String expiryDate) throws UnsupportedEncodingException;

    /**
     * Defines the interface for the generation of mastertokens.
     * @param authKey AuthKey Object from Runtime
     * @param id Identifier Object of the Lambda matched to the AuthKey of Runtime
     * @return valid mastertokeen if authkey and id are valid, else null
     * @throws UnsupportedEncodingException
     */
    public String generateMasterToken(AuthKey authKey, Identifier id) throws UnsupportedEncodingException;

    /**
     * Defines the interface for validating authkeys.
     * @param principal Object given from Spring
     * @param id
     * @return true if toauthkey is valid, else false
     * @throws LambdaNotFoundException if lambda does not exist in Runtime
     */
    public boolean validate(Object principal, Identifier id) throws LambdaNotFoundException;
}
