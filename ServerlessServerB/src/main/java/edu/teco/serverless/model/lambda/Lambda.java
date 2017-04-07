package edu.teco.serverless.model.lambda;

import edu.teco.serverless.model.servicelayer.lambdaruntime.images.LambdaImage;

import java.util.List;

/**
 * Describes lambda function.
 */
public class Lambda {

    private Identifier name;
    private RuntimeAttributes runtimeAttributes;

    public Lambda(Identifier name, RuntimeAttributes runtimeAttributes) {
        this.name = name;
        this.runtimeAttributes = runtimeAttributes;
    }

    private Lambda(Builder builder) {
        this.name = builder.name;
        this.runtimeAttributes = builder.runtimeAttributes;
    }

    public static Builder newLambda() {
        return new Builder();
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public RuntimeAttributes getRuntimeAttributes() {
        return runtimeAttributes;
    }

    public void setRuntimeAttributes(RuntimeAttributes runtimeAttributes) {
        this.runtimeAttributes = runtimeAttributes;
    }

    @Override
    public String toString() {
        return "Lambda{" +
                "name=" + name +
                ", runtimeAttributes=" + runtimeAttributes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lambda)) return false;

        Lambda lambda = (Lambda) o;

        if (!getName().equals(lambda.getName())) return false;
        return getRuntimeAttributes().equals(lambda.getRuntimeAttributes());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getRuntimeAttributes().hashCode();
        return result;
    }

    public static final class Builder {
        private Identifier name;
        private RuntimeAttributes runtimeAttributes;

        private Builder() {
        }

        public Lambda build() {
            return new Lambda(this);
        }

        public Builder name(Identifier name) {
            this.name = name;
            return this;
        }

        public Builder runtimeAttributes(RuntimeAttributes runtimeAttributes) {
            this.runtimeAttributes = runtimeAttributes;
            return this;
        }
    }
}