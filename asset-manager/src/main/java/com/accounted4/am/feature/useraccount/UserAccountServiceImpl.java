package com.accounted4.am.feature.useraccount;

import com.accounted4.am.common.Utils;
import com.accounted4.am.security.EnrichedUserDetails;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 *
 * @author gheinze
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    /**
     * Extract the user information from the principal related to the authentication token.
     *
     * @return
     */
    @Override
    public UserAccount getCurrentUser() {
        EnrichedUserDetails principal = Utils.getUserDetails();
        UserAccount userAccount = new UserAccount();
        BeanUtils.copyProperties(principal, userAccount);
        Set<String> roles = principal.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet())
                ;
        userAccount.setRoles(roles);
        return userAccount;
    }

}
