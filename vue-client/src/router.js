import Vue from 'vue';
import Router from 'vue-router';
import store from './store.js';
import HomeComponent from './views/Home.vue';
import LoginComponent from './views/Login.vue';


Vue.use(Router);


const router = new Router({

    routes: [

        {
            path: '/',
            redirect: {
                name: "home"
            }
        },

        {
            path: '/home',
            name: 'home',
            component: HomeComponent,
            meta: {
                requiresAuth: true
            }
        },

        {
            path: "/login",
            name: "login",
            component: LoginComponent
        },

        {
            path: '/about',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (about.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () =>
                import ( /* webpackChunkName: "about" */ './views/About.vue')
        }

    ]

});


export default router;



function routeRequiresAuthentication(route) {
    return route.matched.some(record => record.meta.requiresAuth);
}


function isAuthenticated() {
    return store.state.usersession.authenticated;
}


// Navigation Guard
router.beforeEach((to, from, next) => {

    if (routeRequiresAuthentication(to) && !isAuthenticated()) {
        next({
            path: '/login',
            query: {
                redirect: to.fullPath,
            },
        });

    } else {
        next();
    }

});