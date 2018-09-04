import axios from 'axios';
import store from '@/store.js';
import UserSession from '@/UserSession.js';


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


function getAuthHeader(token) {
    return {
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    };
}


//axios.defaults.headers.common['Access-Control-Allow-Origin'] = 'https://localhost:8443';

//password=r00table&username=superuser%40accounted4.com&grant_type=password&scope=write
// Content-Type: application/x-www-form-urlencoded

const AmService = {

    login: function(username, password) {
        var authToken = '';
        return axios.post(TOKEN_URL, createOauthRequestBody(username, password))
            .then(response => {
                return response.data.access_token;
            })
            .then(token => {
                authToken = token;
                return axios.get(API_URL + '/useraccount', getAuthHeader(token));
            })
            .then(response => {
                var userDetails = JSON.parse(JSON.stringify(response.data.data));
                var userSession = UserSession.userSession(authToken, userDetails);
                return userSession;
            });
    }

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