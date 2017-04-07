package edu.teco.serverless.model.messages;

/**
 * Created by Kristina on 13/01/2017.
 */
public class RestErrorInfo {
    private final String message;

    public RestErrorInfo(String message) {
        this.message = message;

    }

    public String getMessage() {
        return message;
    }
}

