package edu.teco.serverless.model.messages;
//TODO check if correct with docker

import java.util.List;

/**
 * Created by satan on 06/01/2017.
 */
public class UploadLambdaRequest {
    private String name;
    private RuntimeAttributesRequest runtimeAttributes;

    public UploadLambdaRequest() {

    }
    public UploadLambdaRequest(String name, RuntimeAttributesRequest runtimeAttributes) {
        this.name = name;
        this.runtimeAttributes = runtimeAttributes;
    }

    private UploadLambdaRequest(Builder builder) {
        this.name = builder.name;
        this.runtimeAttributes = builder.runtimeAttributes;
    }

    public static Builder newUploadLambdaRequest() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuntimeAttributesRequest getRuntimeAttributes() {
        return runtimeAttributes;
    }

    public void setRuntimeAttributes(RuntimeAttributesRequest runtimeAttributes) {
        this.runtimeAttributes = runtimeAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadLambdaRequest)) return false;

        UploadLambdaRequest that = (UploadLambdaRequest) o;

        if (!getName().equals(that.getName())) return false;
        return getRuntimeAttributes().equals(that.getRuntimeAttributes());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getRuntimeAttributes().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UploadLambdaRequest{" +
                "name='" + name + '\'' +
                ", runtimeAttributes=" + runtimeAttributes +
                '}';
    }

    public static final class Builder {
        private String name;
        private RuntimeAttributesRequest runtimeAttributes;

        private Builder() {
        }

        public UploadLambdaRequest build() {
            return new UploadLambdaRequest(this);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder runtimeAttributes(RuntimeAttributesRequest runtimeAttributes) {
            this.runtimeAttributes = runtimeAttributes;
            return this;
        }
    }
}
