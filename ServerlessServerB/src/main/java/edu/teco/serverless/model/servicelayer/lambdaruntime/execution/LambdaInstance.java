package edu.teco.serverless.model.servicelayer.lambdaruntime.execution;

import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LambdaImage;

import java.util.Date;

/**
 * Represent a running instance of image and contains reference to image, string for know and time to start.
 */
public class LambdaInstance {
    private LambdaImage image;
    private String identifier;
    private Date startedAt;

    /**
     * constructor
     *
     * @param image the image the instance is started from
     */
    public LambdaInstance(LambdaImage image) {
        this.image = image;
        this.identifier = image.getIdentifier().getIdentifier();
        startedAt = new Date();
    }

    /**
     * @returns the start date of the instance
     */
    public Date getStartedAt() {
        return startedAt;
    }

    /**
     * @returns the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
}
