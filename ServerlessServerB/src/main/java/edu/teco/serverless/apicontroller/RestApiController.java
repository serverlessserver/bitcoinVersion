package edu.teco.serverless.apicontroller;

import edu.teco.serverless.auth.authFacade.AuthFacade;
import edu.teco.serverless.auth.exception.AuthorizationException;
import edu.teco.serverless.model.converter.RequestServerConverter;
import edu.teco.serverless.model.exception.lambda.LambdaDuplicatedNameException;
import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.exception.messages.SemanticRequestException;
import edu.teco.serverless.model.lambda.ExecuteConfig;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.lambda.Lambda;
import edu.teco.serverless.model.messages.ExecuteLambdaRequest;
import edu.teco.serverless.model.messages.ExecuteLambdaResponse;
import edu.teco.serverless.model.messages.UploadLambdaRequest;
import edu.teco.serverless.model.messages.UploadLambdaResponse;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;
import edu.teco.serverless.model.servicelayer.service.LambdaManagerFacadeImpl;
import edu.teco.serverless.payment.InternalPaymentServerException;
import edu.teco.serverless.payment.InvalidPaymentException;
import edu.teco.serverless.payment.PaymentServer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Controller class, provides methods for receiving and controlling the process of REST-requests from user.
 */
@RestController
public class RestApiController {
     final static Logger logger = Logger.getLogger(RestApiController.class);
    /**
     * Service for managing Lambda-functions.
     */
    @Autowired
    private LambdaManagerFacadeImpl lambdaManager;
    /**
     * Service for authorization.
     */
    @Autowired
    private AuthFacade authFacade;

    @Autowired
    private PaymentServer paymentServer;


    /**
     * Uploads lambda into system and send bitcoin link to the user in order to require payment.
     * <p>
     * <p>
     * Postcondition : uploaded lambda can NOT be executed, deleted, showed, can NOT be produced appropriate subtoken
     * for this lambda until user has not paid.
     *
     * @param config JSON with description of function to be uploaded.
     * @return JSON with link of lambda and bitcoin link for payment and HTTP-status "CREATED", if @param config is valid and upload proceed well,
     * HTTP-Status "BAD REQUEST" if @param config is NOT valid,
     * HTTP-Status "Unprocessable Entity" if @param config has semantic errors
     * HTTP-Status "FORBIDDEN", if lambda with input name already exists in system.
     * HTTP-Status "CONFLICT", if  unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas/pay", produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity<UploadLambdaResponse> postAndPayLambda(@RequestBody UploadLambdaRequest config) throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException {

        String token = null;
        Lambda lambda = null;
        try {
            lambda = RequestServerConverter.uploadRequestToLambda(config);
            logger.info(String.format("Get lambda", lambda.toString()));
            token = lambdaManager.addLambda(lambda);
            logger.info(String.format("Get token", token));
        } catch (LambdaDuplicatedNameException e) {
            logger.error("This is error", e);
            throw e;
        } catch (HttpMessageNotReadableException e) {
            logger.error("This is error", e);
            throw e;
        } catch (SemanticRequestException e) {
            logger.error("This is error", e);
            throw e;
        } catch (RuntimeConnectException e) {
            logger.error("This is error", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            logger.error("This is error", e);
            throw e;
        } catch (LanguageNotSupportedException e) {
            logger.error("This is error", e);
            throw e;
        }
        return new ResponseEntity<UploadLambdaResponse>(UploadLambdaResponse.newUploadLambdaResponse()
                .token(paymentServer.getBitcoinURIString(lambda.getName().getIdentifier()))
                .link("/lambdas/" + lambda.getName())
                .build(), HttpStatus.CREATED);
    }

    /**
     * Uploads lambda into system.
     * <p>
     * <p>
     * Postcondition : uploaded lambda can be executed, deleted, showed, can be produced appropriate subtoken
     * for this lambda.
     *
     * @param config JSON with description of function to be uploaded.
     * @return JSON with link and token and HTTP-status "CREATED", if @param config is valid and upload proceed well,
     * HTTP-Status "BAD REQUEST" if @param config is NOT valid,
     * HTTP-Status "Unprocessable Entity" if @param config has semantic errors
     * HTTP-Status "FORBIDDEN", if lambda with input name already exists in system.
     * HTTP-Status "CONFLICT", if  unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas/payment/{name}", method = RequestMethod.GET)
    public byte[] paymentRequest(@PathVariable("name") String name) {
        System.out.println("PAY SOME MONEY, BITCH!!!");
        return paymentServer.sendPaymentRequestToClient(name).toByteArray();
    }

    @RequestMapping(value = "/lambdas/paymentACK", method = RequestMethod.POST)
    public byte[] paymentACK(@RequestBody byte[] paymentMessageEncoded) throws InvalidPaymentException, InternalPaymentServerException {
        String nameOfLambda;
        try {
            nameOfLambda = paymentServer.checkPayment(paymentMessageEncoded);
        } catch (InvalidPaymentException e) {
            throw e;
        }

        String token = null;
        try {
            token = authFacade.generateMasterToken(lambdaManager.getAuthKey(new Identifier(nameOfLambda)), new Identifier(nameOfLambda));
        } catch (UnsupportedEncodingException e) {
            throw new InternalPaymentServerException();
        }

        return paymentServer.getPaymentACK(paymentMessageEncoded, token).toByteArray();
    }

    /**
     * Uploads lambda into system.
     * <p>
     * <p>
     * Postcondition : uploaded lambda can be executed, deleted, showed, can be produced appropriate subtoken
     * for this lambda.
     *
     * @param config JSON with description of function to be uploaded.
     * @return JSON with link and token and HTTP-status "CREATED", if @param config is valid and upload proceed well,
     * HTTP-Status "BAD REQUEST" if @param config is NOT valid,
     * HTTP-Status "Unprocessable Entity" if @param config has semantic errors
     * HTTP-Status "FORBIDDEN", if lambda with input name already exists in system.
     * HTTP-Status "CONFLICT", if  unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas", produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity<UploadLambdaResponse> postLambda(@RequestBody UploadLambdaRequest config) throws RuntimeConnectException, UnsupportedEncodingException, LanguageNotSupportedException {

        String token = null;
        Lambda lambda = null;
        try {
            lambda = RequestServerConverter.uploadRequestToLambda(config);
            logger.info(String.format("Get lambda", lambda.toString()));
            token = lambdaManager.addLambda(lambda);
            logger.info(String.format("Get token", token));
        } catch (LambdaDuplicatedNameException e) {
              logger.error("This is error", e);
            throw e;
        } catch (HttpMessageNotReadableException e) {
            logger.error("This is error", e);
            throw e;
        } catch (SemanticRequestException e) {
            logger.error("This is error", e);
            throw e;
        } catch (RuntimeConnectException e) {
            logger.error("This is error", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            logger.error("This is error", e);
            throw e;
        } catch (LanguageNotSupportedException e) {
            logger.error("This is error", e);
            throw e;
        }
        return new ResponseEntity<UploadLambdaResponse>(UploadLambdaResponse.newUploadLambdaResponse()
                .token(token)
                .link("/lambdas/" + lambda.getName())
                .build(), HttpStatus.CREATED);
    }


    /**
     * Executes lambda.
     * <p>
     * Precondition : executed lambda must be in the system.
     * Postcondition : result of the execution must be extracted, executed lambda can be executed, deleted,
     * showed, can be produced appropriate subtoken for this lambda.
     *
     * @param name   name of the lambda to be executed.
     * @param config JSON with description of execution's features .
     * @return JSON with result of execution and HTTP-status "OK", if @param config and subtoken are valid, lambda with appropriate
     * name exists in system,
     * HTTP-status "NOT FOUND", if lambda with @param nameOfLambda does NOT exist,
     * HTTP-status "BAD REQUEST", if @param config is NOT valid,
     * HTTP-Status "Unprocessable Entity" if @param config has semantic errors
     * HTTP-status "UNAUTHORIZED", if user's subtoken is NOT valid.
     * HTTP-Status "CONFLICT", if  unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas/{name}/execute", produces = {"application/json"}, method = RequestMethod.POST)
    public ResponseEntity<ExecuteLambdaResponse> postLambda(@PathVariable("name") String name,
                                                            @RequestBody ExecuteLambdaRequest config) throws RuntimeConnectException, TimeExceededException {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authFacade.validate(principal, new Identifier(name))) {
            throw new AuthorizationException("subtoken");
        }

        ExecuteConfig runConfig = RequestServerConverter.executeRequestToExecuteConfig(config);
        logger.info(String.format("Get executeConfig",runConfig.toString()));
        String result = null;
        try {
            result = lambdaManager.executeLambda(name, runConfig);
            logger.info(String.format("Get result",result));
        } catch (LambdaNotFoundException e) {
            logger.error("This is error", e);
            throw e;
        } catch (TimeExceededException e) {
            logger.error("This is error", e);
            throw e;
        }

        return new ResponseEntity<>(ExecuteLambdaResponse.newExecuteLambdaResponse()
                .message(result).build(),
                HttpStatus.OK
        );

    }

    /**
     * Updates lambda.
     * <p>
     * Precondition : lambda must be in the system.
     * Postcondition : lambdas's configuration must be updated, updated lambda can be executed, deleted,
     * showed, can be produced appropriate subtoken for this lambda.
     *
     * @param name   name of the lambda to be updated.
     * @param config new configurations of the lambda to be updated.
     * @return JSON with token and HTTP-status "OK", if token and @param config are valid and lambda with @param name exists in system.
     * HTTP-status "NOT FOUND", if lambda with @param name does NOT exist,
     * HTTP-status "BAD REQUEST", if @param config is NOT valid,
     * HTTP-Status "Unprocessable Entity" if @param config has semantic errors
     * HTTP-status "UNAUTHORIZED", if user's token is NOT valid.
     * HTTP-Status "CONFLICT", if unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas/{name}", produces = {"application/json"}, method = RequestMethod.PUT)
    public ResponseEntity<UploadLambdaResponse> putLambda(@PathVariable("name") String name, @RequestBody UploadLambdaRequest config) throws RuntimeConnectException, LanguageNotSupportedException, TimeExceededException {
        Lambda lambda = RequestServerConverter.uploadRequestToLambda(config);
        logger.info(String.format("Get lambda",lambda.toString()));
        boolean auth = authFacade.validate(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), new Identifier(name));
        if (!auth) {
            throw new AuthorizationException("token");
        }
        String token = null;
        try {
            token = lambdaManager.updateLambda(name, lambda);
            logger.info(String.format("Get token",token));
        } catch (LambdaNotFoundException e) {
            logger.error("This is error", e);
            throw e;
        } catch (RuntimeConnectException e) {
            logger.error("This is error", e);
            throw e;
        } catch (LanguageNotSupportedException e) {
            logger.error("This is error", e);
            throw e;
        } catch (TimeExceededException e) {
            logger.error("This is error", e);
            throw e;
        }
        return new ResponseEntity<UploadLambdaResponse>(UploadLambdaResponse.newUploadLambdaResponse()
                .token(token)
                .link("/lambdas/" + lambda.getName())
                .build(), HttpStatus.OK);
    }

    /**
     * Shows lambda's configuration.
     * <p>
     * Precondition : lambda must be in system.
     * Postcondition : lambda's configuration must be extracted, showed lambda can be executed, deleted,
     * showed, can be produced appropriate subtoken for this lambda.
     *
     * @param name name of lambda to be showed
     * @return JSON with configuration and HTTP-status "OK", if token is valid and lambda with @param name exists in system.
     * HTTP-status "NOT FOUND", if lambda with @param name does NOT exist,
     * HTTP-status "UNAUTHORIZED", if user's token is NOT valid.
     * HTTP-Status "CONFLICT", if unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas/{name}", produces = {"application/json"}, method = RequestMethod.GET)
    public ResponseEntity<UploadLambdaRequest> getLambda(@PathVariable("name") String name) throws RuntimeConnectException, FileNotFoundException {
        boolean auth = authFacade.validate(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), new Identifier(name));
        if (!auth) {
            throw new AuthorizationException("token");
        }
        Lambda lambda = null;
        try {
            lambda = lambdaManager.getLambda(name);
            logger.info(String.format("Get lambda",lambda.toString()));
        } catch (LambdaNotFoundException e) {
            logger.error("This is error", e);
            throw e;
        } catch (RuntimeConnectException e) {
            logger.error("This is error", e);
            throw e;
        } catch (FileNotFoundException e) {
            logger.error("This is error", e);
            throw e;
        }
        UploadLambdaRequest uploadRequest = RequestServerConverter.lambdaToUploadRequest(lambda);
        return new ResponseEntity<UploadLambdaRequest>(uploadRequest, HttpStatus.OK);
    }


    /**
     * Deletes lambda.
     * <p>
     * Precondition : lambda must be in system.
     * Postcondition : lambda must be deleted, deleted lambda can NOT be executed, deleted,
     * showed, can NOT be produced appropriate subtoken for this lambda.
     *
     * @param name name of the lambda to be deleted.
     * @return HTTP-status "OK", if token is valid and lambda with @param name exists in system.
     * HTTP-status "NOT FOUND", if lambda with @param name does NOT exist,
     * HTTP-status "UNAUTHORIZED", if user's token is NOT valid.
     * HTTP-Status "CONFLICT", if unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas/{name}", produces = {}, method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteLambda(@PathVariable("name") String name) throws RuntimeConnectException, TimeExceededException {
        boolean auth = authFacade.validate(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), new Identifier(name));
        if (!auth) {
            throw new AuthorizationException("token");
        }
        try {
            lambdaManager.deleteLambda(name);
        } catch (LambdaNotFoundException e) {
            logger.error("This is error", e);
            throw e;
        } catch (RuntimeConnectException e) {
            logger.error("This is error", e);
            throw e;
        } catch (TimeExceededException e) {
            logger.error("This is error", e);
            throw e;
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Produces subtoken for lambda.
     * <p>
     * Precondition : lambda must be in system.
     * Postcondition : anyone with this produced subtoken can execute lambda
     *
     * @param name       name of the lambda, for which subtoken will be created.
     * @param expiryDate time till the subtoken is valid.
     * @return HTTP-status "OK" and subtoken, if token is valid and lambda with @param name exists in system.
     * HTTP-status "NOT FOUND", if lambda with @param name does NOT exist,
     * HTTP-status "UNAUTHORIZED", if user's token is NOT valid.
     * HTTP-Status "CONFLICT", if unexpected error occurred.
     */
    @RequestMapping(value = "/lambdas/{name}/token", produces = {}, method = RequestMethod.GET)
    public ResponseEntity<String> getSubtoken(@PathVariable("name") String name, @RequestParam(value = "expiryDate", required = true) String expiryDate) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean auth = authFacade.validate(principal, new Identifier(name));
        if (!auth) {
            throw new AuthorizationException("token");
        }
        String token = null;
        try {
            token = authFacade.generateSubToken(principal, expiryDate);
            logger.info(String.format("Get token",token));
        } catch (UnsupportedEncodingException e) {
            logger.error("This is error", e);
            e.printStackTrace();
        }
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

}
