import axios from "axios";

import { APP_API_URL } from '@/js/constants.js';


export function getDebenturePage(size, page) {

    const getDebentureUrl = APP_API_URL + '/finsec/debentures';

    return axios.get(
        getDebentureUrl,
        { params: {
                size,
                page
        }})

        .then(axiosResponse => {
            return axiosResponse.data;
        });

}


export function getQuoteHistory(instrumentId) {

    const getQuoteHistoryUrl = APP_API_URL + '/finsec/quotes/' + instrumentId;

    return axios.get(getQuoteHistoryUrl)

        .then(axiosResponse => {
            return axiosResponse.data;
        });

}
