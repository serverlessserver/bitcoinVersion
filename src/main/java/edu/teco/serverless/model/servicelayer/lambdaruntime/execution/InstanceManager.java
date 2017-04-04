package edu.teco.serverless.model.servicelayer.lambdaruntime.execution;

import edu.teco.serverless.model.lambda.ExecuteConfig;
import edu.teco.serverless.model.lambda.Parameter;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LambdaImage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *  Manages the creating, und deleting of container. Contains reference auf all containers.
 */
@Component
public class InstanceManager {
    private static InstanceManager instance;
    private RuntimeCommunicator runtimeCommunicator;
    private List<LambdaInstance> instances;
    private int globalTimeLimit;

    private InstanceManager() {
        instances = new ArrayList<>();
    }


    /**
     * Calls the method execute in the class RuntimeCommunicator
     * @param image lambda image
     * @param config configuration of running
     * @return result of program running
     */
    public String run(LambdaImage image, ExecuteConfig config) throws RuntimeConnectException, TimeExceededException {
        if (image == null || config == null) {
            throw new IllegalArgumentException();
        }

        List<Parameter> parameters = config.getParameterList();
	    parameters.add(0, new Parameter(image.getIdentifier().getIdentifier()));

        RuntimeCommand runtimeCommand = new RuntimeCommand(CommandType.RUN, parameters);

        LambdaInstance lambdaInstance = new LambdaInstance(image);
        instances.add(lambdaInstance);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < config.getRunCycles().getRuncycles(); i++) {
            String result = runtimeCommunicator.executeCommand(runtimeCommand, globalTimeLimit);
            sb.append(result);
        }

        instances.remove(lambdaInstance);

        return sb.toString();
    }

    /**
     * @returns the only instance of the InstanceManager
     */
    public static InstanceManager getInstance() {
        if (instance == null) {
            instance = new InstanceManager();
        }
        return instance;
    }

    /**
     * initializes the InstanceManager
     */
    public void init() {
        runtimeCommunicator = RuntimeCommunicator.getInstance();
        instances = new ArrayList<>();
    }

    /**
     * sets a time limit for the execution of a lambda
     *
     * @param seconds the time limit in seconds
     * @throws IllegalArgumentException if a not-positive value is passed
     */
    public void setGlobalTimeLimit(int seconds) throws IllegalArgumentException {
        if (seconds <= 0) {
            throw new IllegalArgumentException();
        }

        globalTimeLimit = seconds;
    }

    public int getGlobalTimeLimit() {
        return globalTimeLimit;
    }
}
