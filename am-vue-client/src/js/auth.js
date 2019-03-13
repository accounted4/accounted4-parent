import axios from 'axios';
import Store from '@/store.js';
import {
    STORE_MUTATION_LOGOUT,
    STORE_MUTATION_SET_USER_SESSION,
    OAUTH_TOKEN_URL,
    OAUTH_GRANT_TYPE_PASSWORD,
    OAUTH_GRANT_TYPE_REFRESH,
    OAUTH_CLIENT_ID,
    OAUTH_CLIENT_SECRET,
    OAUTH_SCOPE,
    APP_API_URL,
    APP_API_TIMEOUT_MS
} from '@/js/constants.js';


const ACCESS_TOKEN_KEY = 'access_token';



// ==========================================================

function createUserSession(tokenDetails, userDetails) {
    return {
        tokenDetails: tokenDetails,
        userDetails: userDetails
    };
}


// ==========================================================

/**
 * All api requests should add an auth token to the header.
 */
function getAuthHeader(token) {
    return {
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    };
}


// ==========================================================

/**
 * Gather the fields required in the HTTP Request body when requesting
 * an oauth token from an Authorization Server using a username and password.
 */
function getOauthTokenPasswordRequestBodyData(username, password) {
    return {
        grant_type: OAUTH_GRANT_TYPE_PASSWORD,
        client_id: OAUTH_CLIENT_ID,
        client_secret: OAUTH_CLIENT_SECRET,
        scope: OAUTH_SCOPE,
        username: username,
        password: password
    };
}


/**
 * Gather the fields required in the HTTP Request body when requesting
 * an oauth token from an Authorization Server using a a refresh token.
 */
function getOauthTokenRefreshRequestBodyData(refreshToken) {
    return {
        refresh_token: refreshToken,
        grant_type: OAUTH_GRANT_TYPE_REFRESH,
        scope: OAUTH_SCOPE
    };
}

/**
 * Convert a json string into the format required for a request body being
 * sent to the oauth server. e.g.
 *
 * {"grant_type": "password", "client_id": "0123", ...}
 *   =>
 * grant_type=password&client_id=0123&...
 *
 */
function jsonToQueryString(json) {
    return Object
        .keys(json)
        .map(function (key) {
            return encodeURIComponent(key) + '=' + encodeURIComponent(json[key]);
        })
        .join('&');
}


function convertJavascriptObjectToTokenRequestQueryString(requestData) {
    return jsonToQueryString(JSON.parse(JSON.stringify(requestData)));
}


/**
 * Create the request body to be sent to the oauth server for a token request
 * using a username and password.
 *
 * Example token request content body:
 *   password=myUserPassword&username=myuser%40example.com&grant_type=password&scope=write
 */
function createOauthTokenPasswordRequestBody(username, password) {
    var requestData = getOauthTokenPasswordRequestBodyData(username, password);
    return convertJavascriptObjectToTokenRequestQueryString(requestData);
}

/**
 * Create the request body to be sent to the oauth server for a token request
 * using a refresh token.
 *
 * Example token request content body, refresh method:
 *   refresh_token=264173de-c80d-48f1-9dc0-40875cce8c0&egrant_type=refresh_tokens&cope=write
 */
function createOauthTokenRefreshRequestBody(refreshToken) {
    var requestData = getOauthTokenRefreshRequestBodyData(refreshToken);
    return convertJavascriptObjectToTokenRequestQueryString(requestData);
}


// ==========================================================


function SessionExpiredException() {
   this.message = 'Please log';
   this.toString = function() {
      return this.message;
   };
}


const TWO_MINUTES_IN_MS = 2 * 60 * 1000;
const TOKEN_EXPIRY_BUFFER_IN_MS = TWO_MINUTES_IN_MS;

function tokenIsExpired(tokenDetails) {
    var expirationTime = tokenDetails.retrievalTime + tokenDetails.expires_in;
    return  expirationTime + TOKEN_EXPIRY_BUFFER_IN_MS > Date.now();
}


/**
 * A Promise to retrieve a token:
 *  1. from vuex storage if possible
 *  2. from localStorage if possible
 *  3. from a token refresh query, if expired token
 */

function getSessionAuthToken() {

    return new Promise((resolve, reject) => {

        var userSession = Store.getters.getUserSession;

        if (userSession) {
            resolve(userSession.tokenDetails.access_token);
            return;
        }

        userSession = localStorage.getItem(ACCESS_TOKEN_KEY, userSession);

        if (!userSession) {
            throw "No Session";
        }

        if (!tokenIsExpired(userSession.tokenDetails)) {
            Store.commit(STORE_MUTATION_SET_USER_SESSION, userSession);
            resolve(userSession.tokenDetails.access_token);
            return;
        }

        axios.post(
                OAUTH_TOKEN_URL,
                createOauthTokenRefreshRequestBody(userSession.tokenDetails.refresh_token),
                { timeout: APP_API_TIMEOUT_MS }
            )

            .then(response => {
                tokenResponse = response.data;
                tokenResponse.retrievalTime = Date.now();
                userSession.tokenDetails = tokenResponse;
                localStorage.setItem(ACCESS_TOKEN_KEY, userSession);
                Store.commit(STORE_MUTATION_SET_USER_SESSION, userSession);
                resolve(userSession.tokenDetails.access_token);
            });

    });

}

/*


    Example token response:

{
    "access_token": "283d41bb-2d2b-41fa-87f7-45b70a9c3e25",
    "token_type": "bearer",
    "refresh_token": "264173de-c80d-48f1-9dc0-40875cce8c0e",
    "expires_in": 43122,
    "scope": "write"
}

*/

export function login(username, password, onSuccess, onFailure) {

    var tokenResponse = '';

    // query for oauth token
    axios.post(
            OAUTH_TOKEN_URL,
            createOauthTokenPasswordRequestBody(username, password),
            { timeout: APP_API_TIMEOUT_MS }
        )

        .then(response => {
            tokenResponse = response.data;
            tokenResponse.retrievalTime = Date.now();
            // query for user details
            return axios.get(APP_API_URL + '/useraccount', getAuthHeader(tokenResponse.access_token));
        })

        .then(response => {
            // Then add to the userSession user detail information
            var userDetails = JSON.parse(JSON.stringify(response.data.data));
            var userSession = createUserSession(tokenResponse, userDetails);
            localStorage.setItem(ACCESS_TOKEN_KEY, userSession);
            Store.commit(STORE_MUTATION_SET_USER_SESSION, userSession);
            onSuccess();
        })

        .catch(function (error) {
            console.log(error);
            onFailure();
        });
}


export function logout() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    Store.commit(STORE_MUTATION_LOGOUT);
}


export function isLoggedIn() {

    // First check local state
    if (Store.getters.isAuthenticated) {
        return true;
    }

    // If a page is refreshed, session may be gone, but we may be able to
    // reconstitute from local store
    var userSession = localStorage.getItem(ACCESS_TOKEN_KEY, userSession);
    if (userSession) {
        Store.commit(STORE_MUTATION_SET_USER_SESSION, userSession);
        return true;
    }

    return false;

}

axios.interceptors.request.use(async function (config) {
    config.timeout = APP_API_TIMEOUT_MS;
    if (config.url.includes('api') && !config.headers['Authorization']) {
        config.headers['Authorization'] = 'Bearer ' + await getSessionAuthToken();
        config.headers['Content-Type']  = 'application/json';
        config.headers['Accept']        = 'application/json';
    }
    return config;
  }, function (error) {
    return Promise.reject(error);
  });
