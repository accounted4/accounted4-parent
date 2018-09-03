import axios from 'axios';
import store from '@/store.js';


const TOKEN_URL = 'https://localhost:8443/oauth/token';
const API_URL = 'https://localhost:8443/api';


const CLIENT_CREDENTIALS = {
    grant_type: 'password',
    client_id: 'amClient',
    client_secret: 'password',
    scope: 'write',
    username: 'superuser@accounted4.com',
    password: 'r00table'
};


function jsonToQueryString(json) {
    return Object
        .keys(json)
        .map(function(key) {
            return encodeURIComponent(key) + '=' + encodeURIComponent(json[key]);
        })
        .join('&');
}


function createOauthRequestBody(username, password) {
    var body = JSON.parse(JSON.stringify(CLIENT_CREDENTIALS));
    //body.username = username;
    //body.password = password;
    return jsonToQueryString(body);
}


function getAuthHeader() {
    return {
        headers: {
            'Authorization': 'Bearer ' + store.state.usersession.authToken,
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    };
}


function acquireNewToken(username, password) {

    axios.post(TOKEN_URL, createOauthRequestBody(username, password))
        .then(response => {
            store.commit('setAuthenticated', response.data.access_token);
        })
        .catch((error) => {
            console.log('error ' + error);
            store.commit('logout');
        });

}


function userDetails() {
    axios.get(API_URL + '/useraccount', getAuthHeader())
        .then(response => {
            console.log(response);
            var x = JSON.parse(JSON.stringify(response.data));
            console.log(x.data);
            store.commit('setUserDetails', x.data);
            //store.commit('setAuthenticated', response.data.access_token);
        });
}


//axios.defaults.headers.common['Access-Control-Allow-Origin'] = 'https://localhost:8443';

//password=r00table&username=superuser%40accounted4.com&grant_type=password&scope=write
// Content-Type: application/x-www-form-urlencoded

const AmService = {

    acquireNewToken: acquireNewToken,

    userDetails: userDetails,

    login: function(username, password) {
        acquireNewToken(username, password);
    }

    //    login(username, password) {
    // if token exists, get rid of it
    // get token or throw
    //    }
};


export default AmService;


// const AuthStr = 'Bearer '.concat(USER_TOKEN);
// axios.get(URL, {
//         headers: {
//                 Authorization: AuthStr
//         }
// })
// .then(response => {
//         // If request is good...
//         console.log(response.data);
// })
// .catch((error) => {
//         console.log('error ' + error);
// });