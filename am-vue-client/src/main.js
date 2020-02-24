import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import { STORE_MUTATION_INITIALIZE_STORE } from '@/js/constants.js';

import ViewUI from 'view-design';
import locale from 'view-design/dist/locale/en-US';
import 'view-design/dist/styles/iview.css';


Vue.use(ViewUI, { locale });


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
