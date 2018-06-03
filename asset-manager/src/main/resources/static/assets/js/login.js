$(document).ready(function () {

    "use strict";

    new Vue({
        el: '#app',
        data: {
            showAuthFailure: true,
            showLogout: true
        },
        mounted() {
            setTimeout(() => {
                this.showAuthFailure = false;
                this.showLogout = false;
            }, 2000);
        }
    });


});
