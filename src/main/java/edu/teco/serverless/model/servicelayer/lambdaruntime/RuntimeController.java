package edu.teco.serverless.model.servicelayer.lambdaruntime;

import edu.teco.serverless.model.exception.lambda.LambdaDuplicatedNameException;
import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeCommunicator;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.execution.InstanceManager;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.ImageManager;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LambdaImage;
import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LanguageNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages the running of programs, building/updating/deleting of images.
 *
 * The class is a facade to hide the implementation of the runtime module;
 * all communication with the runtime module should be done with this class.
 *
 * The class is implemented as a singleton pattern.
 *
 * @author Steffen Gufler, Anastasia Kryzhanovskaya
 */
@Component
public class RuntimeController {

    private static RuntimeController instance;
    private RuntimeCommunicator communicator;
    private int globalTimeLimit;
    private InstanceManager instanceManager;
    private ImageManager imageManager;
	private static String propertiesPath = "runtime";


    private RuntimeController() throws RuntimeConnectException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, TimeExceededException {

        communicator = RuntimeCommunicator.getInstance();
        instanceManager = InstanceManager.getInstance();
        imageManager = ImageManager.getInstance();
		ResourceBundle resourceBundle = ResourceBundle.getBundle(propertiesPath, Locale.ENGLISH);
	   	globalTimeLimit = Integer.parseInt(resourceBundle.getString("runtime.global.time.limit"));
	    init();
    }

	/**
	 * Initializes RuntimeController and other classes from the module
	 * Should be called before calling any other method
	 */
	public void init() throws RuntimeConnectException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, TimeExceededException {
		communicator.init();
		instanceManager.init();
		imageManager.init();
		setGlobalTimeLimit(globalTimeLimit);
	}

    /**
     * Calls the method run() in the class InstanceManager
     * @param identifier the name of the lambda to be executed
     * @param config the configuration of the lambda needs to start
     * @return the result of the execution of the lambda
     */
    public String run(Identifier identifier, ExecuteConfig config) throws LambdaNotFoundException, RuntimeConnectException, TimeExceededException {
	    LambdaImage lambdaImage = imageManager.getLambdaImageByIdentifier(identifier);
	    return instanceManager.run(lambdaImage, config);
    }

	/**
	 * Calls the method buildImage() in the class ImageManager
	 * @param lambda function
	 * @return authentication key
	 */
	public AuthKey buildImage(Lambda lambda) throws RuntimeConnectException, LanguageNotSupportedException, IOException, TimeExceededException {
		if (lambda == null) {
		    throw new IllegalArgumentException();
        }

		if (lambdaExists(lambda.getName())) throw new LambdaDuplicatedNameException();
		return imageManager.buildImage(lambda);
    }

    public AuthKey rebuildImage(Lambda lambda) throws RuntimeConnectException, LanguageNotSupportedException, IOException, TimeExceededException {
        return imageManager.rebuildImage(lambda);
    }

    public void deleteImage(Identifier id) throws RuntimeConnectException, TimeExceededException {
        imageManager.deleteImage(id);
    }

	/**
	 * Set how long is function execution.
	 * @param seconds time
	 */
	public void setGlobalTimeLimit(int seconds) {
        instanceManager.setGlobalTimeLimit(seconds);
    }

    public int getGlobalTimeLimit() {
	    return instanceManager.getGlobalTimeLimit();
    }

	/**
	 * Get only instance.
	 * @return instance of unique object of the class.
	 * @throws RuntimeConnectException
	 */
	public static RuntimeController getInstance() throws RuntimeConnectException, IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, TimeExceededException {
        if (instance == null) {
            instance = new RuntimeController();
        }
        return instance;
    }

	/**
	 *
	 * @param id identifier for lambda
	 * @return true if exist
	 */
	public boolean lambdaExists(Identifier id) {
        return imageManager.lambdaExists(id);
    }


	/**
	 * Give authentication key by identifier
	 * @param id identifier
	 * @return authentication key
	 * @throws LambdaNotFoundException
	 */
	public AuthKey getAuthKey(Identifier id) throws LambdaNotFoundException {
        return imageManager.getAuthKey(id);
    }

    public Lambda getLambda(Identifier identifier) throws FileNotFoundException {
	    return imageManager.getLambdaByIdentifier(identifier);
    }
}