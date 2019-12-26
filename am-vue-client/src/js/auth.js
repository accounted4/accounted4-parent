import axios from 'axios';
import store from '@/store.js';

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




// ==========================================================

/**
 * The userSession object which is stored in the global Vuex store
 * should have two parts:
 *   o information about the tokens (token, refresh, expiry, ...)
 *   o information about the user account for which the token was created
 */
function createUserSessionObject(tokenDetails, userDetails) {
    return {
        tokenDetails: tokenDetails,
        userDetails: userDetails
    };
}


// ==========================================================

/**
 * All requests with "api" in the url should add an auth token to the header.
 */
function getDefaultHttpRequestHeaders(authToken) {
    return {
        'Authorization': 'Bearer ' + authToken,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };
}


function getDefaultAxiosConfig(authToken) {
    return {
        "headers": getDefaultHttpRequestHeaders(authToken),
        "timeout": APP_API_TIMEOUT_MS
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
 *   becomes =>
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
    let requestData = getOauthTokenPasswordRequestBodyData(username, password);
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
    let requestData = getOauthTokenRefreshRequestBodyData(refreshToken);
    return convertJavascriptObjectToTokenRequestQueryString(requestData);
}


// ==========================================================


function SessionExpiredException() {
   this.message = 'Please log';
   this.toString = function() {
      return this.message;
   };
}


/**
 * We are going to pretend that a token expires 2 minutes before the actual expiry
 * so that we can try using the refresh token instead. Then we might be able to
 * retrieve a new access token without prompting for a login.
 */
const TWO_MINUTES_IN_MS = 2 * 60 * 1000;
const TOKEN_EXPIRY_BUFFER_IN_MS = TWO_MINUTES_IN_MS;

function tokenIsExpired(tokenDetails) {
    if (!tokenDetails || !tokenDetails['access_token']) {
        return true;
    }
    let expirationTime = tokenDetails.retrievalTime + (tokenDetails['expires_in'] * 1000);
    let isExpired = ( Date.now() > (expirationTime - TOKEN_EXPIRY_BUFFER_IN_MS) );
    return isExpired;
}


function getTokenRefreshPromise(refreshToken) {

    const tokenRefreshPromise = axios.post(
        OAUTH_TOKEN_URL,
        createOauthTokenRefreshRequestBody(refreshToken),
        { "timeout": APP_API_TIMEOUT_MS }
    );

    tokenRefreshPromise

        .then(response => {
            let tokenResponse = response.data;
            tokenResponse.retrievalTime = Date.now();
            return tokenResponse;
        })

        .catch(function(error) {
            // If we can't get a token, then you are logged off
            console.log("Please log in again: " + error);
            logout();
            return Promise.reject(error);
        });

    return tokenRefreshPromise;
}


function getRefreshUserSessionPromise(userSession) {

    const refreshUserSessionPromise = getTokenRefreshPromise(userSession.tokenDetails['refresh_token']);

    refreshUserSessionPromise
        .then(response => {
            const newTokenResponse = response.data;
            const currentUser = userSession.userDetails;
            const refreshedUserSession = createUserSessionObject(newTokenResponse, currentUser);
            store.commit(STORE_MUTATION_SET_USER_SESSION, refreshedUserSession);
            return newTokenResponse.tokenDetails['access_token'];
        });

    return refreshUserSessionPromise;
}


function getStateUserSession() {
    let userSession = JSON.parse(JSON.stringify(store.getters.userSession));
    return userSession;
}


function getSessionAuthTokenPromise() {

    let userSession = getStateUserSession();

    if (!userSession) {
        throw "No Session";
    }

    if (!tokenIsExpired(userSession.tokenDetails)) {
        // Just grab the token from session
        return new Promise((resolve, reject) => {resolve(userSession.tokenDetails['access_token'])});
    }

    // Try to use the refresh token to acquire a new access token
    return getRefreshUserSessionPromise(userSession);

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

// TODO: Don't pass in callbacks, change this to return a Promise
export function login(username, password, onSuccess, onFailure) {

    let tokenResponse = '';

    // query for oauth token
    axios.post(
            OAUTH_TOKEN_URL,
            createOauthTokenPasswordRequestBody(username, password),
            { "timeout": APP_API_TIMEOUT_MS }
        )

        .then(response => {
            tokenResponse = response.data;
            tokenResponse.retrievalTime = Date.now();
            // Query for user details. Pass in config object to axios, otherwise the axios interceptor
            // will try to add a token from the user session and that does not yet exist.
            return axios.get(APP_API_URL + '/useraccount', getDefaultAxiosConfig(tokenResponse['access_token']));
        })

        .then(response => {
            // Add to the userSession user detail information
            let userDetails = JSON.parse(JSON.stringify(response.data.data));
            let userSession = createUserSessionObject(tokenResponse, userDetails);
            store.commit(STORE_MUTATION_SET_USER_SESSION, userSession);
            onSuccess();
        })

        .catch(function (error) {
            console.log(error);
            onFailure();
        });
}



export function revokeToken(token) {

    axios.get(APP_API_URL + '/oauthToken/tokens/revoke/' + token)
         .then(response => {
             console.log(response.data);
         })
        .catch(function (error) {
             console.log(error);
        });

}


export function logout() {
    store.commit(STORE_MUTATION_LOGOUT);
}


export function isLoggedIn() {
    return store.getters.inSession;
}


export function getTokens(OAUTH_CLIENT_ID) {

    const tokenUrl = APP_API_URL + '/oauthToken/tokens/' + OAUTH_CLIENT_ID;

    let getTokensPromise = axios.get(tokenUrl);

    getTokensPromise.then(response => {
            let tokenResponse = response.data;
            return tokenResponse;
    });

    return getTokensPromise;

}



axios.interceptors.request.use(

    async function(config) {

        if (config.url.includes('api') && (null == config.headers['Authorization'])) {
            let token = await getSessionAuthTokenPromise();
            config.headers =  getDefaultHttpRequestHeaders(token);
            } else {
            console.log("Interceptor action skipped");
        }
        return config;
    },

    function (error) {
        console.log("ERROR: " + error);
        return Promise.reject(null);
    }

);
