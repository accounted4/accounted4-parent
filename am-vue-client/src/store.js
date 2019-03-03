import Vue from 'vue'
import Vuex from 'vuex'

import { STORE_MUTATION_LOGOUT, STORE_MUTATION_SET_USER_SESSION } from '@/js/constants.js';


Vue.use(Vuex)


export default new Vuex.Store({

  state: {

    userSession: null,

  },


  getters: {

    isAuthenticated: state => {
      return (state.userSession !== null);
    }

  },


  mutations: {

    [STORE_MUTATION_SET_USER_SESSION](state, userSession) {
        state.userSession = userSession;
    },

    [STORE_MUTATION_LOGOUT](state) {
        state.userSession = null;
    }

  },


  actions: {

  }


});
