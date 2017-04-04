package edu.teco.serverless.model.lambda;

/**
 * A name of a function's language.
 */
public class Language {
    String language;

    public Language(String language) {

        this.language = language;
    }

    @Override
    public String toString() {
        return "Language{" +
                "language='" + language + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Language)) return false;

        Language language1 = (Language) o;

        return getLanguage().equals(language1.getLanguage());

    }

    @Override
    public int hashCode() {
        return getLanguage().hashCode();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}
