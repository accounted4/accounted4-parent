import Vue from 'vue';
import Router from 'vue-router';
import Home from '@/views/Home.vue';
import Login from '@/views/Login.vue';
import store from '@/store.js';
import TokenAdmin from '@/views/TokenAdmin.vue';
import { isLoggedIn } from '@/js/auth.js';
import { VIEW_NAME_ABOUT, VIEW_NAME_HOME, VIEW_NAME_LOGIN, VIEW_NAME_TOKEN_ADMIN } from '@/js/constants.js';


Vue.use(Router)

const route_names = {}
const router = new Router({

  mode: 'history',

  base: process.env.BASE_URL,

  routes: [
    {
      path: '/',
      name: VIEW_NAME_HOME,
      component: Home
    },
    {
      path: '/tokenAdmin',
      name: VIEW_NAME_TOKEN_ADMIN,
      component: TokenAdmin,
      meta: {
          requiresRole: 'ROLE_ADMIN'
      }
    },
    {
      path: '/login',
      name: VIEW_NAME_LOGIN,
      component: Login
    },
    {
      path: '/about',
      name: VIEW_NAME_ABOUT,
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import(/* webpackChunkName: "about" */ './views/About.vue'),
      meta: {
          requiresAuth: true
      }
    }
  ]

});


export default router;
