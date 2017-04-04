package edu.teco.serverless.model.lambda;

/**
 * A number of executing of a function.
 */
public class RunCycles {
    private int runcycles;

    public RunCycles(int runcycles) {

        this.runcycles = runcycles;
    }

    public int getRuncycles() {
        return runcycles;
    }

    @Override
    public String toString() {
        return "RunCycles{" +
                "runcycles=" + runcycles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunCycles)) return false;

        RunCycles runCycles = (RunCycles) o;

        return getRuncycles() == runCycles.getRuncycles();

    }

    @Override
    public int hashCode() {
        return getRuncycles();
    }

    public void setRuncycles(int runcycles) {
        this.runcycles = runcycles;
    }


}
