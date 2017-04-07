package edu.teco.serverless.apicontroller.exception;

/**
 * Created by Kolner on 1/12/2017.
 */
import edu.teco.serverless.auth.exception.AuthorizationException;
import edu.teco.serverless.auth.exception.JwtMalformedException;
import edu.teco.serverless.auth.exception.NoJwtGivenException;
import edu.teco.serverless.model.exception.lambda.LambdaDuplicatedNameException;
import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.exception.messages.SemanticRequestException;
import edu.teco.serverless.model.messages.RestErrorInfo;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.payment.InternalPaymentServerException;
import edu.teco.serverless.payment.InvalidPaymentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(JwtMalformedException.class)
    public ResponseEntity handleJwtMalformedException(JwtMalformedException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RestErrorInfo("Error, the JWT file has wrong format."));
    }


    @ExceptionHandler(NoJwtGivenException.class)
    public ResponseEntity handleNoJwtGivenException(NoJwtGivenException ex) {


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RestErrorInfo( "Error, no JWT was given."));
    }
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity handleAuthorizationException(AuthorizationException ex) {


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RestErrorInfo( "Error, Authorization failed."));
    }
    @ExceptionHandler(LanguageNotSupportedException.class)
    public ResponseEntity handleLanguageNotSupportedException(LanguageNotSupportedException ex) {


       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RestErrorInfo( "Error, the given language is not supported."));

    }
    @ExceptionHandler(RuntimeConnectException.class)
    public ResponseEntity handleRuntimeConnectException(RuntimeConnectException ex) {
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RestErrorInfo( "Internal error."));

    }
    @ExceptionHandler(TimeExceededException.class)
    public ResponseEntity handleTimeExceededException(TimeExceededException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RestErrorInfo( "Error, time limit exceeded."));

    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {//

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RestErrorInfo("Error, the JSON file has wrong format."));
    }
    @ExceptionHandler(SemanticRequestException.class)
    public ResponseEntity handleSemanticRequestException(SemanticRequestException ex) {//

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new RestErrorInfo("Error, the JSON file"+ex.getLocalizedMessage()+"."));
    }
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity handleFileNotFoundException(FileNotFoundException ex) {


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RestErrorInfo( "Error, this Lambda-function could not be found."));

    }

    @ExceptionHandler(LambdaNotFoundException.class)
    public ResponseEntity handleLambdaNotFoundException(LambdaNotFoundException ex) {


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RestErrorInfo( "Error, this Lambda-function could not be found."));

    }
    @ExceptionHandler(LambdaDuplicatedNameException.class)
    public ResponseEntity handleLambdaDuplicatedNameException(LambdaDuplicatedNameException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RestErrorInfo( "Error, a lambda with this name already exists."));

    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity handleInvalidPaymentException(InvalidPaymentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RestErrorInfo( "Error, a payment amount is to low"));

    }

    @ExceptionHandler(InternalPaymentServerException.class)
    public ResponseEntity handleInternalPaymentServerException(InternalPaymentServerException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RestErrorInfo( "Error, an internal error on payment server occurred"));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleExceptions(Exception ex) {


        return ResponseEntity.status(HttpStatus.CONFLICT).body(new RestErrorInfo(ex.getClass().getName()+" "+ex.getLocalizedMessage()));
    }


}
