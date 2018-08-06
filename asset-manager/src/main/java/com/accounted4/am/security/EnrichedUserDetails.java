package com.accounted4.am.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Enriches the default UserDetails object provided by Spring with additional application-specific attributes
 * such as the tenant the user belongs to.
 *
 * @author gheinze
 *
 */
public class EnrichedUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final UserDetails decoratedUserDetails;
    private final List<GrantedAuthority> combinedAuthorities;
    private final UserAppDetails userAppDetails;


    public EnrichedUserDetails(
            final UserDetails userDetailsToDecorate,
            final List<GrantedAuthority> combinedAuthorities,
            final UserAppDetails details
    ) {
        this.decoratedUserDetails = userDetailsToDecorate;
        this.combinedAuthorities = combinedAuthorities;
        this.userAppDetails = details;
    }


    // *****************************
    // Application-added attributes
    // *****************************

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

    public String getOrganization() {
        return userAppDetails.getOrganization();
    }
    

    // *****************************
    // Decorated default spring security attributes
    // *****************************

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return combinedAuthorities;
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
