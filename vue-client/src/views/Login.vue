<template>

<div id="login">

    <h1>Login</h1>

    <input type="text" name="username" v-model="input.username" placeholder="Username" />
    <input type="password" name="password" v-model="input.password" placeholder="Password" />
    <button type="button" v-on:click="login()">Login</button>

    <div class="alert">
            <transition name="fade">
                <span v-if="msg">{{msg}}</span>
            </transition>
    </div>

</div>

</template>


<script>
import amService from '@/amService.js';

export default {

    name: 'Login',

    data() {
        return {
            input: {
                username: "",
                password: ""
            }
        }
    },

    methods: {
        login() {
            //amService.getToken(this.input.username, this.input.password);
            if (this.input.username != "" && this.input.password != "") {
                amService.login(this.input.username, this.input.password)
                .then(response => {
                    this.$store.commit('setUserSession', response);
                })
                .catch((error) => {
                    console.log('Login error: ' + error);
                    this.$store.commit('logout');
                });
            } else {
                this.$store.commit('setLoginMsg', "A username and password must be present");
            }
        }
    },

    computed: {
        msg() {
            return this.$store.state.userSession.loginMsg;
        }
    },

    watch: {
        'input.username': function(newUsername, oldUsername) {
            if (newUsername != "") {
                this.$store.commit('setLoginMsg', "");
            }
        }
    }

}
</script>

<style scoped>

#login {
    width: 500px;
    border: 1px solid #CCCCCC;
    background-color: #FFFFFF;
    margin: auto;
    margin-top: 200px;
    padding: 20px;
}

.alert {
    color: red;
    font-size: 12px;
    padding: 20px;
}

.fade-enter-active {
  transition: opacity 1s ease;
}

.fade-leave-active {
  transition: opacity 3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

</style>
