import axios from 'axios';
import Store from '@/store.js';
import {
    STORE_MUTATION_LOGOUT,
    STORE_MUTATION_SET_USER_SESSION,
    OAUTH_TOKEN_URL,
    OAUTH_GRANT_TYPE,
    OAUTH_CLIENT_ID,
    OAUTH_CLIENT_SECRET,
    OAUTH_SCOPE,
    APP_API_URL
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

function getOauthTokenRequestBodyData(username, password) {
    return {
        grant_type: OAUTH_GRANT_TYPE,
        client_id: OAUTH_CLIENT_ID,
        client_secret: OAUTH_CLIENT_SECRET,
        scope: OAUTH_SCOPE,
        username: username,
        password: password
    };
}


function jsonToQueryString(json) {
    return Object
        .keys(json)
        .map(function (key) {
            return encodeURIComponent(key) + '=' + encodeURIComponent(json[key]);
        })
        .join('&');
}


function createOauthTokenRequestBody(username, password) {
    var tokenRequestBodyData = getOauthTokenRequestBodyData(username, password);
    var tokenRequestBodyAsJson = JSON.parse(JSON.stringify(tokenRequestBodyData));
    var tokenRequestQueryString = jsonToQueryString(tokenRequestBodyAsJson);
    return tokenRequestQueryString;
}


// ==========================================================


/*
    Example token request content body:

    password=myUserPassword&username=myuser%40example.com&grant_type=password&scope=write


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
    return axios.post(
            OAUTH_TOKEN_URL,
            createOauthTokenRequestBody(username, password),
            { timeout: 5000 }
        )

        .then(response => {
            tokenResponse = response.data;
            tokenResponse.retrievalTime = Date.now();
            // query for user details
            return axios.get(APP_API_URL + '/useraccount', getAuthHeader(tokenResponse.access_token));
        })

        .then(response => {
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
    return Store.getters.isAuthenticated;
}
