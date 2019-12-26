<template>

  <div>

    <h1 id='heading'>Token Admin</h1>

    <div>
        <Table :columns="tableColumns" :data="tokens"></Table>
    </div>

  </div>

</template>


<script>

import axios from 'axios';
import { APP_API_URL, OAUTH_CLIENT_ID } from '@/js/constants.js';
import { getTokens } from '@/js/auth.js';

export default {

  name: 'tokenAdmin',

  data () {
      return {
          tableColumns: [
              {title: 'User Account', key: 'userAccount'},
              {title: 'Access Token', key: 'accessToken'},
              {title: 'Refresh Token', key: 'refreshToken'},
              {title: 'Expiry Time', key: 'expiration'},
              {
                  title: 'Action',
                  key: 'action',
                  width: 150,
                  align: 'center',
                  render: (h, params) => {
                      return h('div', [
                          h('Button', {
                              props: {
                                  type: 'error',
                                  size: 'small'
                              },
                              on: {
                                  click: () => {
                                      console.log(params.index);
                                  }
                              }
                          }, 'Revoke')
                      ]);
                  }
              }

          ],
          tokens: []
      }
  },


  mounted: function() {
      this.refreshTokenList();
  },


  methods: {

      refreshTokenList: function() {
          getTokens(OAUTH_CLIENT_ID).then(response => {
              console.log("Auth get tokens then...");
              let tokenResponse = response.data;
              console.log(tokenResponse.data);
              this.tokens = tokenResponse.data;
          });
      }

  }


}

</script>


<style>

#heading {
  padding: 30px;
}

</style>
