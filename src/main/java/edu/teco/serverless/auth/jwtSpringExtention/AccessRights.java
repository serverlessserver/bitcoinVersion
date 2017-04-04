package edu.teco.serverless.auth.jwtSpringExtention;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * Implementation of UserDetails to parse JWT into it
 */
public class AccessRights implements UserDetails {

    private final String lambdaName;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Date expiryDate;
    private final String authKey;

    /**
     * Constructor to initialise.
     * @param lambdaName Name of Lambda
     * @param expiryDate expiryDate, null if mastertoken
     * @param authorities authorities, SUB or MASTER
     * @param authKey authkey of lambda in runtime
     */
    public AccessRights(String lambdaName, Date expiryDate, Collection<? extends GrantedAuthority> authorities, String authKey) {
        this.lambdaName = lambdaName;
        this.authorities = authorities;
        this.expiryDate = expiryDate;
        this.authKey = authKey;
    }

    /**
     * Getter for authkey
     * @return authkey as String
     */
    public String getAuthKey() {
        return authKey;
    }

    /**
     * Getter for LambdaName
     * @return lambdaname as string
     */
    public String getLambdaName() {
        return lambdaName;
    }

    /**
     * Getter for expirydate
     * @return expirydate as Date object
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Stub method
     * @return null
     */
    @Override
    public String getUsername() {
        return null;
    }

    /**
     * Stub method
     * @return true
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Stub methods
     * @return true
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * If expiryDate is before current date.
     * @return false if this is the case, else false
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        if (expiryDate == null) {
            return true;
        }
        return expiryDate.after(new Date());
    }

    /**
     * Stub method
     * @return true
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    /**
     * Getter for Authorities
     * @return authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Stub method
     * @return null
     */
    @Override
    public String getPassword() {
        return null;
    }

}
