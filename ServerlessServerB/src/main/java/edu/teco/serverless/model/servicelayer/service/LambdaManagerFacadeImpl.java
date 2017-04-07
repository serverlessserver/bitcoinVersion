package edu.teco.serverless.model.servicelayer.service;

import edu.teco.serverless.auth.authFacade.AuthFacade;
import edu.teco.serverless.model.exception.lambda.LambdaDuplicatedNameException;
import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.ExecuteConfig;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.lambda.Lambda;
import edu.teco.serverless.model.servicelayer.lambdaruntime.RuntimeController;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @see edu.teco.serverless.model.servicelayer.service.LambdaManagerFacade
 */
@Service
public class LambdaManagerFacadeImpl implements LambdaManagerFacade {
    final static Logger logger = Logger.getLogger(LambdaManagerFacadeImpl.class);


    @Autowired
    private RuntimeController runTime;
    @Autowired
    private AuthFacade authenticatior;

    public AuthKey getAuthKey(Identifier identifier) {
        return runTime.getAuthKey(identifier);
    }

    /**
     * @see edu.teco.serverless.model.servicelayer.service.LambdaManagerFacade
     */
    public String addLambda(Lambda lambda) throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException {

        try {
            runTime = RuntimeController.getInstance();
        } catch (IllegalAccessException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (InstantiationException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (IOException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (TimeExceededException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        }


        if (runTime.lambdaExists(lambda.getName())) {
            throw new LambdaDuplicatedNameException();
        }

        String token = null;
        try {
            AuthKey authKey = runTime.buildImage(lambda);

            logger.info(String.format("Get authKey", authKey.getAuthKey()));
            token = authenticatior.generateMasterToken(authKey, lambda.getName());
            logger.info(String.format("Get token", token));
        } catch (UnsupportedEncodingException e) {

            logger.error("This is error", e);
            throw e;
        } catch (LanguageNotSupportedException e) {

            logger.error("This is error", e);
            throw e;
        } catch (IOException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (TimeExceededException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        }
        return token;

    }

    /**
     * @see edu.teco.serverless.model.servicelayer.service.LambdaManagerFacade
     */

    public String executeLambda(String nameOfLambda, ExecuteConfig executeConfig) throws RuntimeConnectException, TimeExceededException {
        try {
            runTime = RuntimeController.getInstance();
        } catch (IllegalAccessException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (InstantiationException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (IOException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        }
        Identifier identifier = new Identifier(nameOfLambda);
        if (!runTime.lambdaExists(identifier)) {

            throw new LambdaNotFoundException();
        }
        String result = null;
        try {
            result = runTime.run(identifier, executeConfig);
            logger.info(String.format("Get result", result));
        } catch (TimeExceededException e) {

            logger.error("This is error", e);
            throw e;
        }
        return result;

    }

    /**
     * @see edu.teco.serverless.model.servicelayer.service.LambdaManagerFacade
     */
    public String updateLambda(String nameOfLambda, Lambda lambda) throws RuntimeConnectException, LanguageNotSupportedException, TimeExceededException {

        try {
            runTime = RuntimeController.getInstance();
        } catch (IllegalAccessException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (InstantiationException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (IOException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (TimeExceededException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        }


        if (!runTime.lambdaExists(new Identifier(nameOfLambda))) {

            throw new LambdaNotFoundException();
        }
        String token = null;
        try {
            AuthKey authKey = runTime.rebuildImage(lambda);
            logger.info(String.format("Get authKey", authKey.getAuthKey()));

            token = authenticatior.generateMasterToken(authKey, lambda.getName());
            logger.info(String.format("Get token", token));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (LanguageNotSupportedException e) {

            logger.error("This is error", e);
            throw e;
        } catch (IOException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (TimeExceededException e) {

            logger.error("This is error", e);
            throw e;
        }
        return token;
    }

    /**
     * @see edu.teco.serverless.model.servicelayer.service.LambdaManagerFacade
     */
    public Lambda getLambda(String nameOfLambda) throws RuntimeConnectException, FileNotFoundException {
        try {
            runTime = RuntimeController.getInstance();
        } catch (IllegalAccessException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (InstantiationException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (IOException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (TimeExceededException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        }

        Lambda lambda;
        Identifier identifier = new Identifier(nameOfLambda);
        if (!runTime.lambdaExists(identifier)) {


            throw new LambdaNotFoundException();
        }
        try {
            lambda = runTime.getLambda(identifier);
            logger.info(String.format("Get lambda ", lambda.toString()));
        } catch (FileNotFoundException e) {

            logger.error("This is error", e);
            throw e;
        }

        return lambda;

    }


    /**
     * @see edu.teco.serverless.model.servicelayer.service.LambdaManagerFacade
     */
    public void deleteLambda(String nameOfLambda) throws RuntimeConnectException, TimeExceededException {
        try {
            runTime = RuntimeController.getInstance();
        } catch (IllegalAccessException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (InstantiationException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (IOException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        } catch (TimeExceededException e) {

            logger.error("This is error", e);
            e.printStackTrace();
        }
        Identifier identifier = new Identifier(nameOfLambda);
        if (!runTime.lambdaExists(identifier)) {

            throw new LambdaNotFoundException();
        }
        runTime.deleteImage(identifier);
    }


}


