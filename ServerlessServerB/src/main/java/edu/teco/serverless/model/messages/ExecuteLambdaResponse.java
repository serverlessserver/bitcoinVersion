package edu.teco.serverless.model.messages;
//TODO check if correct with docker
/**
 * Created by satan on 06/01/2017.
 */
public class ExecuteLambdaResponse {

    private  String message;

    private ExecuteLambdaResponse(Builder builder) {
        this.message = builder.message;
    }

    public String getMessage() {
        return message;
    }

    public static Builder newExecuteLambdaResponse() {
        return new Builder();
    }


    public static final class Builder {
        private String message;


        private Builder() {
        }

        public ExecuteLambdaResponse build() {
            return new ExecuteLambdaResponse(this);
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }
    }
}

