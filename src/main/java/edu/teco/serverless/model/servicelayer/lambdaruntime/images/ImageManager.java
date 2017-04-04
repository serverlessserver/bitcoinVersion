package edu.teco.serverless.model.servicelayer.lambdaruntime.images;

import edu.teco.serverless.model.exception.lambda.LambdaNotFoundException;
import edu.teco.serverless.model.lambda.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.CommandType;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeCommand;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeCommunicator;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Component
public class ImageManager {
    private static ImageManager instance;

	private RuntimeCommunicator runtimeCommunicator;
	final static String path = "target/lambdas/";
	final static String factoriesReference = "edu.teco.serverless.model.servicelayer.lambdaruntime.images.";
    private List<AbstractLambdaFactory> factories;
    private List<LambdaImage> images;

    private ImageManager() {
    }

    private void loadImageFactories() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
	    File file = new File("src/main/java/edu/teco/serverless/model/servicelayer/lambdaruntime/images");
	    String[] files = file.list();
	    for (String f : files) {
		    if (Class.forName(factoriesReference + f.replace(".java", "")).getSuperclass().equals(AbstractLambdaFactory.class)) {
			    factories.add((AbstractLambdaFactory) Class.forName(factoriesReference + f.replace(".java", "")).newInstance());
		    }
	    }
    }

    /**
     * initializes the image manager
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public void init() throws IllegalAccessException, InstantiationException, ClassNotFoundException, RuntimeConnectException, IOException, TimeExceededException {
	    File directory = new File("target/lambdas");
	    directory.mkdir();
	    directory.createNewFile();
	    factories = new ArrayList<>();
	    images = new ArrayList<>();
        loadImageFactories();
	    runtimeCommunicator = RuntimeCommunicator.getInstance();
	    for (AbstractLambdaFactory factory : factories) {
	        factory.init();
        }
    }

    /**
     * @returns the only instance of the ImageManager
     */
    public static ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    /**
     * @returns a list of all factories
     */
    public List<AbstractLambdaFactory> getFactories() {
        return factories;
    }

	/**
	 * Returns lambda image with identifier id if exist.
	 * @param id identifier of lambda image
	 * @return true if exist
	 * @throws LambdaNotFoundException
	 */
    public LambdaImage getLambdaImageByIdentifier(Identifier id) throws LambdaNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException();
        }

	    for (LambdaImage lambdaImage : images) {
		    if (lambdaImage.getIdentifier().equals(id)) return lambdaImage;
	    }
	    throw new LambdaNotFoundException();
    }

	/**
	 * Check if lamda image with identifier id exist.
	 * @param id identifier of lambda image
	 * @return true if exist
	 */
	public boolean lambdaExists(Identifier id) {
		if (id == null) {
		    throw new IllegalArgumentException();
        }

        for (LambdaImage lambdaImage : images) {
            if (lambdaImage.getIdentifier().equals(id)) return true;
        }
        return false;
    }

	/**
	 * Look for lambda image with identifier id and return authentication key
	 * @param id identifier of lambda image
	 * @return authentication key
	 * @throws LambdaNotFoundException
	 */
	public AuthKey getAuthKey(Identifier id) throws LambdaNotFoundException {
	    if (id == null) {
	        throw new IllegalArgumentException();
        }

		LambdaImage lambdaImage = getLambdaImageByIdentifier(id);
	    return lambdaImage.getAuthKey();
    }

	/**
	 * Removes lambda image with identifier id
	 * @param id identifier of lambda image
	 */
	public void deleteImage(Identifier id) throws RuntimeConnectException, TimeExceededException {
	    if (id == null) {
	        throw new IllegalArgumentException();
        }

		LambdaImage lambdaImage = getLambdaImageByIdentifier(id);
	    images.remove(lambdaImage);
	    File lambdaFile = new File(path + id.getIdentifier());
		for (File file : lambdaFile.listFiles()) {
			file.delete();
		}
	    lambdaFile.delete();
	    ArrayList<Parameter> parameters = new ArrayList<>();
	    parameters.add(new Parameter(id.getIdentifier()));
	    RuntimeCommand runtimeCommand = new RuntimeCommand(CommandType.REMOVE, parameters);
	    runtimeCommunicator.executeCommand(runtimeCommand);
    }

	/**
	 * Delete old lambda image and build new.
	 * @param lambda function
	 * @return authorisation key
	 */
	public AuthKey rebuildImage(Lambda lambda) throws RuntimeConnectException, LanguageNotSupportedException, IOException, TimeExceededException {
	    deleteImage(lambda.getName());
	    return buildImage(lambda);
    }

	/**
	 * Calls the method execute in the class RuntimeCommunicator.
	 * Save the function on the hard drive.
	 * Put a lambda image into list.
	 * @param lambda function
	 * @return authentication key
	 */
	public AuthKey buildImage(Lambda lambda) throws RuntimeConnectException, LanguageNotSupportedException, IOException, TimeExceededException {
		if (lambda == null) {
		    throw new IllegalArgumentException();
        }

		AbstractLambdaFactory lambdaFactory = null;
		for (AbstractLambdaFactory factory : factories) {
			if (factory.language.equals(lambda.getRuntimeAttributes().getLanguage())) lambdaFactory = factory;
		}

		if (lambdaFactory == null) throw new LanguageNotSupportedException();

		File directory = new File(path + lambda.getName().getIdentifier());
		directory.mkdir();

		File lambdaFile = new File(path + lambda.getName().getIdentifier() + "/" + lambda.getName().getIdentifier() + "." + lambdaFactory.getExtension());
		File libraries = new File(path + lambda.getName().getIdentifier() + "/libraries");

		lambdaFile.createNewFile();
		FileWriter lambdaFileWriter = new FileWriter(lambdaFile);
		lambdaFileWriter.write(lambda.getRuntimeAttributes().getCode().getCode());
		lambdaFileWriter.close();

		libraries.createNewFile();
		FileWriter librariesFileWriter = new FileWriter(libraries);
		for (Library library : lambda.getRuntimeAttributes().getLibraries()) {
			librariesFileWriter.write(library.getLibrary());
		}
		librariesFileWriter.close();

	    LambdaImage lambdaImage = lambdaFactory.buildImage(lambda, lambdaFile);
		images.add(lambdaImage);
		return lambdaImage.getAuthKey();
    }

    /**
     * searches a lambda by its identifier
     *
     * @param identifier the identifier
     * @returns the lambda
     * @throws FileNotFoundException if there is no such lambda
     */
	public Lambda getLambdaByIdentifier(Identifier identifier) throws FileNotFoundException {
	    if (identifier == null) {
	        throw new IllegalArgumentException();
        }

		if (!lambdaExists(identifier)) throw new LambdaNotFoundException();
		LambdaImage lambdaImage = getLambdaImageByIdentifier(identifier);
		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader in = new BufferedReader(new FileReader(path + identifier.getIdentifier() + "/" + identifier.getIdentifier() + ".py"));
			try {
				String s;
				while ((s = in.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
				}
			} finally {
				in.close();
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		ArrayList<Library> libraries = new ArrayList<>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path + identifier.getIdentifier() + "/libraries"));
			try {
				String s;
				while ((s = in.readLine()) != null) {
					libraries.add(new Library(s));
				}
			} finally {
				in.close();
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}


		RuntimeAttributes attributes = new RuntimeAttributes(lambdaImage.getLanguage(), libraries, new Code(new String(sb)));
		Lambda lambda = new Lambda(identifier, attributes);
		return lambda;
	}
}
