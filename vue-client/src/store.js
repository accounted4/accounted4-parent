import Vue from 'vue';
import Vuex from 'vuex';
import amService from '@/amService.js';
import UserSession from '@/UserSession.js';


Vue.use(Vuex);


const LOGOUT_MSG = 'You have been logged out.';


export default new Vuex.Store({

    state: {
        userSession: UserSession.unauthenticatedUserSession()
    },

    mutations: {

        setUserSession(state, userSession) {
            state.userSession = userSession;
        },

        logout(state) {
            var emptySession = UserSession.unauthenticatedUserSession();
            emptySession.loginMsg = LOGOUT_MSG;
            state.userSession = emptySession;
        },

        setLoginMsg(state, msg) {
            state.userSession.loginMsg = msg;
        }

    }

    // For persistence to cookie, see: https://stackoverflow.com/questions/43581958/handle-login-api-token-in-vue-js

});