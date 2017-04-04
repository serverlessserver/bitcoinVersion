package edu.teco.serverless.model.servicelayer.service;


import edu.teco.serverless.model.exception.lambda.LambdaDuplicatedNameException;

import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.ExecuteConfig;
import edu.teco.serverless.model.lambda.Lambda;

//TODO what should be done in bad cases.

import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * This interface describes methods for lambda's managment. Implemented as a Facade-pattern.
 */
public interface LambdaManagerFacade {

    /**
     * Adds lambda into the system.
     * <p>
     * Precondition : valid lambda.
     * Postcondition : received token of added lambda, added lambda can be updated, deleted, extracted and executed.
     *
     * @param lambda lambda to be added.
     * @return token, if other lambdas in the system do NOT have the same name.
     * @throws LambdaDuplicatedNameException if lambda with the @param name already exists in the system.
     */
    public String addLambda(Lambda lambda) throws LambdaDuplicatedNameException, RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException;

    /**
     * Updates lambda's configuration in the system.
     * <p>
     * Precondition : lambda to be updated must exist in the system, new lambda must be valid.
     * Postcondition : received new token of updated lambda, updated lambda can be updated, deleted,
     * extracted and executed.
     *
     * @param name   name of the lambda to be updated.
     * @param lambda new lambda, which will substitute old lambda.
     * @return new token.
     * @throws LambdaNotFoundException if lambda to be updated do NOT exist in system.
     */
    public String updateLambda(String name, Lambda lambda) throws LambdaNotFoundException, RuntimeConnectException, LanguageNotSupportedException, TimeExceededException;

    /**
     * Obtains lambda from the system.
     * <p>
     * Precondition : lambda to be obtained must exist in the system.
     * Postcondition : obtained lambda, obtained lambda can be updated, deleted, extracted and executed.
     *
     * @param name name of the lambda to be obtained.
     * @return lambda.
     * @throws LambdaNotFoundException if lambda to be obtained do NOT exist in system.
     */
    public Lambda getLambda(String name) throws LambdaNotFoundException, RuntimeConnectException, FileNotFoundException;

    /**
     * Runs lambda.
     * <p>
     * Precondition : lambda to be run must exist in the system, execution's configuration must be valid.
     * Postcondition : result of the lambda's execution must be extracted, executed lambda can be updated, deleted,
     * extracted and executed.
     *
     * @param name          name of the lambda to be executed.
     * @param executeConfig input for the lambda if needed and number of run cycles.
     * @return result of lambda's execution.
     * @throws LambdaNotFoundException if lambda to be executed do NOT exist in system.
     */
    public String executeLambda(String name, ExecuteConfig executeConfig) throws LambdaNotFoundException, RuntimeConnectException, TimeExceededException;

    /**
     * Deletes lambda from the system.
     * <p>
     * Precondition : lambda to be deleted must exist in system.
     * Postcondition : deleted lambda can NOT be executed, extracted, updated or deleted.     *
     *
     * @param name name of the lambda to be deleted.
     * @throws LambdaNotFoundException if lambda to be deleted do NOT exist in system.
     */
    public void deleteLambda(String name) throws LambdaNotFoundException, RuntimeConnectException, TimeExceededException;


}
