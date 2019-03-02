<template>
  <div id="app">
    <div id="nav">
      <router-link to="/">Home</router-link> |
      <router-link v-bind:class="{disabled: isDisabled}" to="/about">About</router-link> |
      <router-link v-show="!isLoggedIn()" to="/login">Login</router-link>
      <button v-show="isLoggedIn()" @click="handleLogout()">Log In</button>
    </div>
    <router-view/>
  </div>
</template>


<script>

export default {

  name: 'app-nav',

  methods: {

    handleLogout() {
        this.$store.commit('logout');;
    },

    isLoggedIn() {
      return this.$store.getters.isAuthenticated;
    }

  },

  computed: {
    isDisabled: function() {
        return !this.isLoggedIn();
    }
  }

};

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
