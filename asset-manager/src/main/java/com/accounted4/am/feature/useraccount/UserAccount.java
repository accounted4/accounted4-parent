package com.accounted4.am.feature.useraccount;

import java.util.Set;
import lombok.Data;

/**
 * DTO with user account info.
 *
 * @author gheinze
 */
@Data
public class UserAccount {

    private String username;
    private String displayName;
    private String email;
    private String organization;
    private Set<String> roles;

}
