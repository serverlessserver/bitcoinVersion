package edu.teco.serverless.model.servicelayer.lambdaruntime.images;

import edu.teco.serverless.model.lambda.AuthKey;
import edu.teco.serverless.model.lambda.Identifier;
import edu.teco.serverless.model.lambda.Language;

/**
 * Contains information about the python image.
 */
public class Python3LambdaImage extends LambdaImage {
    /**
     * constructor
     *
     * @param identifier identifier of the lambda
     * @param authKey auth key of the lambda
     */
    public Python3LambdaImage(Identifier identifier, AuthKey authKey) {
        super(identifier, authKey);
        language = new Language("Python3");
    }

    /**
     * copy constructor
     *
     * @param lambdaImage the lambda to be copied
     */
    public Python3LambdaImage(LambdaImage lambdaImage) {
        super(lambdaImage);
        language = new Language("Python3");
    }
}
