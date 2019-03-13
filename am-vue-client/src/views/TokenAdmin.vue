<template>
  <div class="about">
    <h1>Token Admin</h1>
    <div id="app-4">
      <ul>
        <li v-for="token in tokens">
          {{ token.accessToken }}
        </li>
    </ul>
    </div>
  </div>
</template>


<script>

import axios from 'axios';
import { APP_API_URL, OAUTH_CLIENT_ID } from '@/js/constants.js';

export default {

  name: 'tokenAdmin',

  data () {
      return {
          tokens: []
      }
  },


  mounted: function() {
      this.refreshTokenList();
  },


  methods: {

      refreshTokenList: function() {
          console.log("Refreshing token list");
          axios.get(APP_API_URL + '/oauthToken/tokens/' + OAUTH_CLIENT_ID)

          .then(response => {
              this.tokens = response.data.data;
          })

          .catch(error => {
              console.log(error);
          });

      }

  }


}

</script>
