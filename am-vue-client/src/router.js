import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import { isLoggedIn } from '@/js/auth.js';
import { VIEW_NAME_ABOUT, VIEW_NAME_HOME, VIEW_NAME_LOGIN } from '@/js/constants.js';


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




function routeRequiresAuthentication(route) {
    return route.matched.some(record => record.meta.requiresAuth);
}




// Navigation Guard
router.beforeEach((to, from, next) => {

    if (routeRequiresAuthentication(to) && !isLoggedIn()) {
      console.log("guard");
        next({
            path: '/login',
            query: { redirect: to.fullPath }
        });
    } else {
        next();
    }

});
