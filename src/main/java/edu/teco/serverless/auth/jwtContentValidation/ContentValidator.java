package edu.teco.serverless.auth.jwtContentValidation;

import edu.teco.serverless.auth.jwtSpringExtention.AccessRights;
import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.servicelayer.lambdaruntime.RuntimeController;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * Class to validate authkeys and content.
 */
@Component
public class ContentValidator {
@Autowired
    private RuntimeController runtimeController;

    /**
     * Constructor to initialise RuntimeController
     * @throws RuntimeConnectException at runtime error
     */
    public ContentValidator() throws RuntimeConnectException, TimeExceededException {
        try {
            runtimeController = RuntimeController.getInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeExceededException e) {
            throw e;
        }
    }


    /**
     * Constructor to set RuntimeController. This is mostly used for tests.
     * @param runtimeController
     */
    public ContentValidator(RuntimeController runtimeController) {
        this.runtimeController = runtimeController;
    }

    /**
     * Checks if a lambda exists in Runtime
     * @param name Identifier object of Lambda
     * @return true, if lambda exists, else false
     */
    public boolean lambdaExists(Identifier name) {
        return runtimeController.lambdaExists(name);
    }

    /**
     * Validates authkeys from sub and mastertokens.
     * @param accessRights parsed token in a Object
     * @return true, if authkey is valid, else false
     * @throws LambdaNotFoundException if lambda does not exist in runtime
     */
    public boolean validate(AccessRights accessRights, Identifier id) throws LambdaNotFoundException {
        if(!(accessRights.getExpiryDate()==null)) {
            if (accessRights.getExpiryDate().before(new Date())) {
                return false;
            }
        }
        //authhandler and runtime stuff
        boolean lex = runtimeController.lambdaExists(id);

        AuthKey authKey = runtimeController.getAuthKey(id);
        boolean hok = false;
        if (authKey.getAuthKey().equals(accessRights.getAuthKey())) {
            hok = true;
        }
        return (lex & hok);
    }

}
