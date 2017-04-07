package edu.teco.serverless.model.servicelayer.lambdaruntime.images;

import edu.teco.serverless.model.lambda.*;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.RuntimeConnectException;
import edu.teco.serverless.model.servicelayer.lambdaruntime.communication.TimeExceededException;

import java.io.File;

/**
 * Manages the creating, updating und deleting of python images.
 */
public class Python3LambdaFactory extends AbstractLambdaFactory {

	public Python3LambdaFactory() {
	    language = new Language("Python3");
	    runtimeImageName = "python:latest";
		extension = "py";
	}

	@Override
	public Python3LambdaImage buildImage(Lambda lambda, File code) throws RuntimeConnectException, TimeExceededException {
	    return new Python3LambdaImage(super.buildImage(lambda, code));
	}

	@Override
	protected LambdaImage createImage(Identifier name, AuthKey key) {
		return new Python3LambdaImage(name, key);
	}

	@Override
	protected String generateRuntimeConfigFile(Lambda lambda, File codeFile) {
	    StringBuilder sb = new StringBuilder();

	    // base image
	    sb.append("FROM " + runtimeImageName + "\n");

	    // libraries
	    for (Library l : lambda.getRuntimeAttributes().getLibraries()) {
	        sb.append("RUN pip install " + l.getLibrary() + "\n");
        }

        // copy lambda into container
        String filePath = codeFile.toString();
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            filePath = filePath.replaceAll("\\\\", "/");
        }
		sb.append("COPY " + filePath + " /src/" + codeFile.getName() + "\n");
	    sb.append("ENTRYPOINT [\"python\", " + "\"/src/" + codeFile.getName() + "\"]\n");

	    return sb.toString();
	}
}
