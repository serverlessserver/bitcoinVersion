package edu.teco.serverless.model.servicelayer.lambdaruntime.images;

import edu.teco.serverless.model.lambda.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.CommandType;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeCommand;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeCommunicator;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the creating, updating und deleting of images.
 */
public abstract class AbstractLambdaFactory {
    protected Language language;
    protected String runtimeImageName;
    protected RuntimeCommunicator communicator;
    protected String extension;

    protected AbstractLambdaFactory() {
        communicator = RuntimeCommunicator.getInstance();
    }

    /**
     * builds an image for a lambda
     *
     * @param lambda the lambda
     * @param code the file, that contains the code for the lambda
     * @returns a reference to the image
     * @throws RuntimeConnectException if an error occurs when building the image
     */
    public LambdaImage buildImage(Lambda lambda, File code) throws RuntimeConnectException, TimeExceededException {
        String fileContent = generateRuntimeConfigFile(lambda, code);
        File file = new File(code.getParent() + "/Dockerfile");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContent);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter(lambda.getName().toString()));
        params.add(new Parameter(file.toString()));
        RuntimeCommand cmd = new RuntimeCommand(CommandType.BUILD, params);
        AuthKey key = new AuthKey(communicator.executeCommand(cmd));

        return createImage(lambda.getName(), key);
    }

    protected abstract LambdaImage createImage(Identifier name, AuthKey key);

    protected abstract String generateRuntimeConfigFile(Lambda lambda, File file);

    /**
     * initializes the factory, pulls the base image for a specific language
     *
     * @throws RuntimeConnectException if an error occurs when pulling the image
     */
    public void init() throws RuntimeConnectException, TimeExceededException {
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter(runtimeImageName));
         RuntimeCommand cmd = new RuntimeCommand(CommandType.PULL, params);
        communicator.executeCommand(cmd);
    }

    public String getExtension() {
        return extension;
    }
}
