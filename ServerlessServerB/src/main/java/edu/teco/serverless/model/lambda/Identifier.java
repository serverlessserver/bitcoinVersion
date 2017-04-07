package edu.teco.serverless.model.lambda;

/**
 * Name of a function.
 */
public class Identifier {
    private String identifier;

    public Identifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;

        Identifier that = (Identifier) o;

        return getIdentifier().equals(that.getIdentifier());

    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    @Override
    public String toString() {
        return identifier ;
    }
}
