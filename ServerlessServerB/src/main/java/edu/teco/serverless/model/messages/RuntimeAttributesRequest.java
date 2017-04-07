package edu.teco.serverless.model.messages;

import java.util.List;

/**
 * Created by Kristina on 15/02/2017.
 */
public class RuntimeAttributesRequest {
    private String language;
    private List<String> libraries;
    private String code;

    public RuntimeAttributesRequest() {

    }

    public RuntimeAttributesRequest(String language, List<String> libraries, String code) {
        this.language = language;
        this.libraries = libraries;
        this.code = code;
    }

    private RuntimeAttributesRequest(Builder builder) {
        this.language = builder.language;
        this.libraries = builder.libraries;
        this.code = builder.code;
    }

    public static Builder newRuntimeAttributesRequest() {
        return new Builder();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getLibraries() {
        return libraries;
    }

    public void setLibraries(List<String> libraries) {
        this.libraries = libraries;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuntimeAttributesRequest)) return false;

        RuntimeAttributesRequest that = (RuntimeAttributesRequest) o;

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

    @Override
    public String toString() {
        return "RuntimeAttributesRequest{" +
                "language='" + language + '\'' +
                ", libraries=" + libraries +
                ", code='" + code + '\'' +
                '}';
    }

    public static final class Builder {
        private String language;
        private List<String> libraries;
        private String code;

        private Builder() {
        }

        public RuntimeAttributesRequest build() {
            return new RuntimeAttributesRequest(this);
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder libraries(List<String> libraries) {
            this.libraries = libraries;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }
    }
}
