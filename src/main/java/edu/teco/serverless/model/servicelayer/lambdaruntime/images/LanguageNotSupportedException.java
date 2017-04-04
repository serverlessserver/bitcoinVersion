package edu.teco.serverless.model.servicelayer.lambdaruntime.images;

/**
 * Exception class if a lambda in a not supported language is uploaded
 */
public class LanguageNotSupportedException extends Exception{
    public LanguageNotSupportedException() {
        super();
    }
}
