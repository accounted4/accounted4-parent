<template>
  <div id="app">
    <div id="nav">
      <router-link to="/">Home</router-link> |
      <router-link v-bind:class="{disabled: !isLoggedIn}" to="/tokenAdmin">Token Admin</router-link> |
      <router-link v-bind:class="{disabled: !isLoggedIn}" to="/about">About</router-link> |
      <router-link v-show="!isLoggedIn" to="/login">Login</router-link>
      <Button v-show="isLoggedIn" type="primary" @click="handleLogout()">Log out</Button>
    </div>
    <router-view/>
  </div>
</template>


<script>

import { logout } from '@/js/auth.js';
import { VIEW_NAME_HOME } from '@/js/constants.js';


export default {

  name: 'app-nav',

  methods: {

    handleLogout() {
      logout();
      this.$router.push({ name: VIEW_NAME_HOME });
    }

  },

  computed: {

    isLoggedIn() {
      return this.$store.getters.isAuthenticated;
    }

  }

}


</script>


<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}

.disabled {
    pointer-events:none;
    opacity:0.6;
}

</style>
