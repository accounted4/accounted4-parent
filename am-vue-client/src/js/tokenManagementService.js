import axios from "axios";

import { APP_API_URL } from '@/js/constants.js';


export function getTokens(oauthClientId) {

    const getTokenUrl = APP_API_URL + '/oauthToken/tokens/' + oauthClientId;

    return axios.get(getTokenUrl)
        .then(axiosResponse => {
            return axiosResponse.data;
        });

}


export function revokeToken(token) {
    const revokeTokenUrl = APP_API_URL + '/oauthToken/tokens/revoke/' + token;
    return axios.post(revokeTokenUrl);
}

