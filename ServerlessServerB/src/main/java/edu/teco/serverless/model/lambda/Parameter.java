package edu.teco.serverless.model.lambda;

/**
 * A parameter of a function to execute.
 */
public class Parameter {
    private String parameter;

    public Parameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "parameter='" + parameter + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parameter)) return false;

        Parameter parameter1 = (Parameter) o;

        return getParameter().equals(parameter1.getParameter());

    }

    @Override
    public int hashCode() {
        return getParameter().hashCode();
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }


}
