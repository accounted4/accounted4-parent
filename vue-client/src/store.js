import Vue from 'vue';
import Vuex from 'vuex';
import amService from '@/amService.js';

Vue.use(Vuex);


const LOGOUT_MSG = 'You have been logged out.';

const NO_USER_SESSION = {
    authenticated: false,
    authToken: '',
    loginMsg: '',
    user: ''
};


export default new Vuex.Store({

    state: {
        usersession: JSON.parse(JSON.stringify(NO_USER_SESSION))
    },

    mutations: {
        setAuthenticated(state, authToken) {
            state.usersession.authenticated = true;
            state.usersession.authToken = authToken;
            amService.userDetails();
        },
        setUserDetails(state, userDetails) {
            state.usersession.user = userDetails;
        },
        logout(state) {
            var emptySession = JSON.parse(JSON.stringify(NO_USER_SESSION));
            emptySession.loginMsg = LOGOUT_MSG;
            state.usersession = emptySession;
        },
        setLoginMsg(state, msg) {
            state.usersession.loginMsg = msg;
        }
    }

    // For persistence to cookie, see: https://stackoverflow.com/questions/43581958/handle-login-api-token-in-vue-js

});

export const LOGIN = 'login';
export const LOGOUT = 'logout';