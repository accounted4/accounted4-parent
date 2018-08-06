package com.accounted4.am.security;

import java.io.Serializable;

import lombok.Data;

/**
 * A DAO to supply additional application-specific user detail information to the standard spring security
 * UserDetails object.
 *
 * @author gheinze
 *
 */
@Data
public class UserAppDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String status;
    private final String tenant;
    private final String displayName;
    private final String email;
    private final String organization;

}
