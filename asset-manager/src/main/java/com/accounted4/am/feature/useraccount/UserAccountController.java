package com.accounted4.am.feature.useraccount;

import com.accounted4.am.common.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints for user account administration.
 *
 * @author gheinze
 */
@RestController
@RequestMapping("/api/useraccount")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;


    @GetMapping
    public RestResponse<UserAccount> current() {
        UserAccount userAccount = userAccountService.getCurrentUser();
        RestResponse response = new RestResponse(userAccount);
        return response;
    }


}
