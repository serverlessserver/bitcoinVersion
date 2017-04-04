package edu.teco.serverless.model.servicelayer.lambdaruntime.communication;

import edu.teco.serverless.model.lambda.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to do by lambdaruntime. Contains type of operation and parameters.
 *
 * Parameters needed for Types of commands:
 * BUILD: directory of runtimefile
 * RUN: name or identifier of an image, arguments for the call
 * INFO: none
 * PULL: name of the image to be pulled
 * DELETE: name or identifier of an image
 *
 */
public class RuntimeCommand {
    private List<Parameter> parameters = new ArrayList<>();
    private CommandType type;

    /**
     * constructor
     * @param type type of the command
     * @param parameters list of parameters for the command
     */
    public RuntimeCommand(CommandType type, List<Parameter> parameters) {
        this.parameters = parameters;
        this.type = type;
    }

    /**
     * @returns a list of the parameters
     */
	public List<Parameter> getParameters() {
		return parameters;
	}

    /**
     * @returns the type of the command
     */
	public CommandType getType() {
		return type;
	}
}
