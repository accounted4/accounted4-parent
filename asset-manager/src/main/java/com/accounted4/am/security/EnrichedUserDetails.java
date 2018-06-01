package com.accounted4.am.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Enriches the default UserDetails object provided by Spring with additional application-specific attributes.
 *
 * @author gheinze
 *
 */
public class EnrichedUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final UserDetails decoratedUserDetails;
    private final UserAppDetails userAppDetails;

    public EnrichedUserDetails(UserDetails userDetailsToDecorate, UserAppDetails details) {
        this.decoratedUserDetails = userDetailsToDecorate;
        this.userAppDetails = details;
    }

    public String getTenant() {
        return userAppDetails.getTenant();
    }

    public String getStatus() {
        return userAppDetails.getStatus();
    }

    public String getDisplayName() {
        return userAppDetails.getDisplayName();
    }

    public String getEmail() {
        return userAppDetails.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return decoratedUserDetails.getAuthorities();
    }

    @Override
    public String getPassword() {
        return decoratedUserDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return decoratedUserDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return decoratedUserDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return decoratedUserDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return decoratedUserDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return decoratedUserDetails.isEnabled();
    }

}
