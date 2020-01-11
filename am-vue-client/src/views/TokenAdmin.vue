<template>

  <div>

    <h1 id='heading'>Token Admin</h1>

    <div>
        <Table :columns="tableColumns" :data="tokens"></Table>
    </div>

  </div>

</template>


<script>

import { OAUTH_CLIENT_ID } from '@/js/constants.js';
import { getTokens, revokeToken } from '@/js/tokenManagementService.js';

export default {

  name: 'tokenAdmin',

  data () {
      return {
          tableColumns: [
              {title: 'User Account', key: 'userAccount'},
              {title: 'Access Token', key: 'accessToken'},
              {title: 'Refresh Token', key: 'refreshToken'},
              {title: 'Expiry Time', key: 'expiration', width: 200},
              {
                  title: 'Action',
                  key: 'action',
                  width: 100,
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
                                      this.revoke(params.index);
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
          getTokens(OAUTH_CLIENT_ID).then(restResponse => {
              this.tokens = restResponse.data;
          });
      },

      revoke: function(index) {
          let tokenToRevoke = this.tokens[index].accessToken;
          revokeToken(tokenToRevoke).then(response => {
              this.refreshTokenList();
          });

      }

  }


}

</script>


<style scoped>

#heading {
  padding: 30px;
}

</style>
