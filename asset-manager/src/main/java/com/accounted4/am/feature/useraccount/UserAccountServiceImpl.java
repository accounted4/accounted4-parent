package com.accounted4.am.feature.useraccount;

import com.accounted4.am.common.Utils;
import com.accounted4.am.security.EnrichedUserDetails;
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
        return userAccount;
    }

}
