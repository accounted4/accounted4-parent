import Vue from 'vue';
import Vuex from 'vuex';

import {
    STORE_MUTATION_INITIALIZE_STORE,
    STORE_MUTATION_LOGOUT,
    STORE_MUTATION_SET_USER_SESSION,
    ACCESS_TOKEN_KEY
 } from '@/js/constants.js';


const VUEX_STATE_KEY = "vuexState";


Vue.use(Vuex)


const store = new Vuex.Store({

  state: {

    userSession: null

  },


  mutations: {

    [STORE_MUTATION_INITIALIZE_STORE](state) {

          // When Vuex is created, trying loading the state from localStorage
          if (localStorage.getItem(VUEX_STATE_KEY)) {
              this.replaceState(
                  Object.assign(
                      state,
                      JSON.parse(localStorage.getItem(VUEX_STATE_KEY))
                  )
              );
          }

    },

    [STORE_MUTATION_SET_USER_SESSION](state, userSession) {
        state.userSession = userSession;
    },

    [STORE_MUTATION_LOGOUT](state) {
        state.userSession = null;
    }

  },


  getters: {
      inSession: state => { return (state.userSession !== null); },
      userSession: state => { return state.userSession; }
  },


  computed: {

  },

  actions: {

  }


});


// If the Vuex store state is changed, persist it to localStorage
store.subscribe((mutation, state) => {
	localStorage.setItem(VUEX_STATE_KEY, JSON.stringify(state));
});


export default store;
