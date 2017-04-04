package edu.teco.serverless.model.lambda;

import java.util.List;

/**
 * Contains configuration of a function - parameters and how many times must be executed.
 */
public class ExecuteConfig {

    private RunCycles runCycles;
    private List<Parameter> parameterList;

    private ExecuteConfig(Builder builder) {
        this.runCycles = builder.runCycles;
        this.parameterList = builder.parameterList;
    }

    @Override
    public String toString() {
        return "ExecuteConfig{" +
                "runCycles=" + runCycles +
                ", parameterList=" + parameterList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecuteConfig)) return false;

        ExecuteConfig that = (ExecuteConfig) o;

        if (!getRunCycles().equals(that.getRunCycles())) return false;
        return getParameterList().equals(that.getParameterList());

    }

    @Override
    public int hashCode() {
        int result = getRunCycles().hashCode();
        result = 31 * result + getParameterList().hashCode();
        return result;
    }

    public static Builder newExecuteConfig() {
        return new Builder();
    }

    public RunCycles getRunCycles() {
        return runCycles;
    }

    public void setRunCycles(RunCycles runCycles) {
        this.runCycles = runCycles;
    }

    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }

    public static final class Builder {
        private RunCycles runCycles;
        private List<Parameter> parameterList;

        private Builder() {
        }

        public ExecuteConfig build() {
            return new ExecuteConfig(this);
        }

        public Builder runCycles(RunCycles runCycles) {
            this.runCycles = runCycles;
            return this;
        }

        public Builder parameterList(List<Parameter> parameterList) {
            this.parameterList = parameterList;
            return this;
        }
    }
}
