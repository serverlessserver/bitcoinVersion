package edu.teco.serverless.model.servicelayer.lambdaruntime.communication;

/**
 * Represents the type of an operation to execute.
 */
public enum CommandType {
    BUILD,
    REMOVE,
    RUN,
    PULL,
    INFO;

    @Override
    public String toString() {
        switch (this) {
            case RUN:
                return "run";
            case BUILD:
                return "build";
            case INFO:
                return "info";
            case PULL:
                return "pull";
            case REMOVE:
                return "rmi";
            default:
                return null;
        }
    }
}
