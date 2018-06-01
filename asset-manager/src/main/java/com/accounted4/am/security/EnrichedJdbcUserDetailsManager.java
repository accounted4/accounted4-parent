package com.accounted4.am.security;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;


/**
 * A UserDetailsService which retrieves user details from a jdbc datasource as provided by a
 * default Spring implementation, but also supplemented with some application-specific attributes.
 *
 * @author gheinze
 *
 */
public class EnrichedJdbcUserDetailsManager extends JdbcUserDetailsManager {


    private static final String  USER_APP_DETAILS_BY_USERNAME_QUERY =
            "select status, tenant, display_name, email from spring_security.user_app_details where username = ?";



    /**
     * The hook that allows modifying the final UserDetails object returned to the Spring security framework
     * when loading a user by username.
     *
     * @return
     */
    @Override
    protected UserDetails createUserDetails(
            final String username,
            final UserDetails userFromUserQuery,
            final List<GrantedAuthority> combinedAuthorities
    ) {
        List<UserAppDetails> userAppDetails =  loadUserAppDetailsByUsername(username);
        EnrichedUserDetails enrichedUserDetails = new EnrichedUserDetails(
                userFromUserQuery,
                userAppDetails.get(0)
        );
        return enrichedUserDetails;
    }


    protected List<UserAppDetails> loadUserAppDetailsByUsername(final String username) {

        return getJdbcTemplate().query(USER_APP_DETAILS_BY_USERNAME_QUERY,
              new String[] { username }, (ResultSet rs, int rowNum) -> {
                  String status = rs.getString(1);
                  String tenant = rs.getString(2);
                  String displayName = rs.getString(3);
                  String email = rs.getString(4);
                  return new UserAppDetails(status, tenant, displayName, email);
        });
    }


}
