import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import { STORE_MUTATION_INITIALIZE_STORE } from '@/js/constants.js';

import iView from 'iview';
import 'iview/dist/styles/iview.css';


Vue.use(iView);


Vue.config.productionTip = false;

new Vue({

  router,
  store,

  beforeCreate() {
      this.$store.commit(STORE_MUTATION_INITIALIZE_STORE);
  },

  render: h => h(App)

})
.$mount('#app');
