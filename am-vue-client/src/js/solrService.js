import axios from "axios";

import { APP_API_URL } from '@/js/constants.js';


export function autoSuggestFinancialInstruments(searchTerm) {

    const autoSuggestUrl = APP_API_URL + '/finsec/findBySymbol?searchTerm=' + searchTerm;

    return axios.get(autoSuggestUrl)
        .then(axiosResponse => {
            return axiosResponse.data;
        });

}



