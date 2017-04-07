package edu.teco.serverless.model.messages;

/**
 * Created by satan on 06/01/2017.
 */
public class UploadLambdaResponse {
    private final String token;
    private final String link;

    public UploadLambdaResponse(String token, String link) {
        this.token = token;
        this.link = link;
    }

    private UploadLambdaResponse(Builder builder) {
        this.token = builder.token;
        this.link = builder.link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadLambdaResponse)) return false;

        UploadLambdaResponse response = (UploadLambdaResponse) o;

        if (getToken() != null ? !getToken().equals(response.getToken()) : response.getToken() != null) return false;
        return getLink() != null ? getLink().equals(response.getLink()) : response.getLink() == null;

    }

    @Override
    public int hashCode() {
        int result = getToken() != null ? getToken().hashCode() : 0;
        result = 31 * result + (getLink() != null ? getLink().hashCode() : 0);
        return result;
    }

    public static Builder newUploadLambdaResponse() {
        return new Builder();
    }

    public String getLink() {
        return link;
    }

    public String getToken() {
        return token;
    }

    public static final class Builder {
        private String token;
        private String link;

        private Builder() {
        }

        public UploadLambdaResponse build() {
            return new UploadLambdaResponse(this);
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder link(String link) {
            this.link = link;
            return this;
        }
    }
}
