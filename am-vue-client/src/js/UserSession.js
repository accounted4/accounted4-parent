class UserSession {

    static unauthenticatedUserSession() {
        return {
            authToken: '',
            user: '',
            isAuthenticated: false
        };
    }

    static userSession(token, userDetails) {
        return {
            authToken: token,
            user: userDetails,
            isAuthenticated: true
        };

    }

}

export default UserSession;
