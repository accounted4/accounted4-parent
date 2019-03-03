import Store from '@/store.js'
import { STORE_MUTATION_LOGOUT } from '@/js/constants.js';
import { STORE_MUTATION_SET_USER_SESSION } from '@/js/constants.js';


const ACCESS_TOKEN_KEY = 'access_token';


export function login() {
  var userSession = createUserSession("TOKEN", "USER_DETAILS");
  localStorage.setItem(ACCESS_TOKEN_KEY, userSession);
  Store.commit(STORE_MUTATION_SET_USER_SESSION, userSession);
}


export function logout() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  Store.commit(STORE_MUTATION_LOGOUT);
}


export function isLoggedIn() {
  return Store.getters.isAuthenticated;
}


function createUserSession(tokenDetails, userDetails) {
  return { tokenDetails: tokenDetails, userDetails: userDetails };
}
