class UserSession {

    static unauthenticatedUserSession() {
        return {
            authenticated: false,
            authToken: '',
            loginMsg: '',
            user: ''
        };
    }

    static userSession(token, userDetails) {
        return {
            authenticated: true,
            authToken: token,
            loginMsg: '',
            user: userDetails
        };

    }

}

export default UserSession;