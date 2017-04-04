package edu.teco.serverless.model.servicelayer.lambdaruntime.images;

import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.lambda.Language;

/**
 * Contains information about the image.
 */
public abstract class LambdaImage {
    private Identifier identifier;
    private AuthKey authKey;
    Language language;

    /**
     * constructor
      * @param identifier identifier of the lambda
     * @param authKey auth key of the lambda
     */
    public LambdaImage(Identifier identifier, AuthKey authKey) {
        this.identifier = identifier;
        this.authKey = authKey;
    }

    /**
     * copy constructor
     * @param l the lambda to be copied
     */
    public LambdaImage(LambdaImage l) {
        this.identifier = l.identifier;
        this.authKey = l.authKey;
    }

    /**
     * @returns the identifier
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * @returns the auth key
     */
    public AuthKey getAuthKey() {

        return authKey;
    }

    /**
     * @returns the language of the lambda
     */
    public Language getLanguage() {
        return language;
    }
}
