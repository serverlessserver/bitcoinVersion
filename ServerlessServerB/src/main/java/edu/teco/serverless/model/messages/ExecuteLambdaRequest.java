package edu.teco.serverless.model.messages;

import java.util.List;
//TODO check if correct with docker
/**
 * Created by satan on 06/01/2017.
 */
public class ExecuteLambdaRequest {

    private int times;
    private List<String> parameters;

    public ExecuteLambdaRequest() {
    }

    private ExecuteLambdaRequest(Builder builder) {
        this.times = builder.times;
        this.parameters = builder.parameters;
    }

    public static Builder newExecuteLambdaRequest() {
        return new Builder();
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public static final class Builder {
        private int times;
        private List<String> parameters;

        private Builder() {
        }

        public ExecuteLambdaRequest build() {
            return new ExecuteLambdaRequest(this);
        }

        public Builder times(int times) {
            this.times = times;
            return this;
        }

        public Builder parameters(List<String> parameters) {
            this.parameters = parameters;
            return this;
        }
    }
}
