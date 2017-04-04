package edu.teco.serverless.model.lambda;

/**
 * A name of a library which a function need for executing.
 */
public class Library {
    private String library;

    public Library(String library) {

        this.library = library;
    }

    @Override
    public String toString() {
        return "Library{" +
                "library='" + library + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Library)) return false;

        Library library1 = (Library) o;

        return getLibrary().equals(library1.getLibrary());

    }

    @Override
    public int hashCode() {
        return getLibrary().hashCode();
    }


    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }


}
