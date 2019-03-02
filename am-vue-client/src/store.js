import Vue from 'vue'
import Vuex from 'vuex'
import UserSession from './js/UserSession.js'


Vue.use(Vuex)


export default new Vuex.Store({

  state: {

    userSession: UserSession.unauthenticatedUserSession(),

  },


  getters: {

    isAuthenticated: state => {
      return (state.userSession.isAuthenticated);
    }

  },


  mutations: {

    setUserSession(state, userSession) {
        state.userSession = userSession;
    },

    logout(state) {
        var emptySession = UserSession.unauthenticatedUserSession();
        state.userSession = emptySession;
    }

  },


  actions: {

  }


});
