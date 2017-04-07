package edu.teco.serverless.model.lambda;

import java.util.List;

/**
 * Created by Kristina on 09/02/2017.
 */
public class RuntimeAttributes {
    private Language language;
    private List<Library> libraries;
    private Code code;

    public RuntimeAttributes(Language language, List<Library> libraries, Code code) {
        this.language = language;
        this.libraries = libraries;
        this.code = code;
    }

    private RuntimeAttributes(Builder builder) {
        this.language = builder.language;
        this.libraries = builder.libraries;
        this.code = builder.code;
    }

    public static Builder newRuntimeAttributes() {
        return new Builder();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "RuntimeAttributes{" +
                "language=" + language +
                ", libraries=" + libraries +
                ", code=" + code +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuntimeAttributes)) return false;

        RuntimeAttributes that = (RuntimeAttributes) o;

        if (!getLanguage().equals(that.getLanguage())) return false;
        if (!getLibraries().equals(that.getLibraries())) return false;
        return getCode().equals(that.getCode());
    }

    @Override
    public int hashCode() {
        int result = getLanguage().hashCode();
        result = 31 * result + getLibraries().hashCode();
        result = 31 * result + getCode().hashCode();
        return result;
    }

    public static final class Builder {
        private Language language;
        private List<Library> libraries;
        private Code code;

        private Builder() {
        }

        public RuntimeAttributes build() {
            return new RuntimeAttributes(this);
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder libraries(List<Library> libraries) {
            this.libraries = libraries;
            return this;
        }

        public Builder code(Code code) {
            this.code = code;
            return this;
        }
    }
}
