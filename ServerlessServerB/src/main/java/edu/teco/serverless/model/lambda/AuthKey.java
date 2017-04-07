package edu.teco.serverless.model.lambda;

/**
 * Created by satan on 17/01/2017.
 */
public class AuthKey {
    private String authKey;

    public AuthKey(String authKey) {
        this.authKey = authKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthKey)) return false;

        AuthKey authKey1 = (AuthKey) o;

        return getAuthKey().equals(authKey1.getAuthKey());

    }

    @Override
    public int hashCode() {
        return getAuthKey().hashCode();
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
}
