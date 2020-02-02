<template>

  <div class="layout">

    <Layout>

      <Header>
        <Menu mode="horizontal" theme="dark" active-name="1" @on-select="menuHandler">

          <div class="layout-logo">
            <img src="a4.svg" width="30" height="30" title="Accounted4" >
          </div>

          <div class="layout-nav">

            <MenuItem name="home" v-show="isLoggedIn">
              <Icon type="ios-navigate"></Icon>
              <router-link to="/">Home</router-link>
            </MenuItem>

            <MenuItem name="about">
              <Icon type="ios-information-circle-outline" />
              <router-link to="/about">about</router-link>
            </MenuItem>

            <Submenu name="securities">
              <template slot="title">
                <Icon type="md-analytics" />
                Stocks
              </template>
              <MenuGroup title="Portfolios">
                <MenuItem name="portfolio">
                  <router-link to="/portfolio">Portfolio</router-link>
                </MenuItem>
              </MenuGroup>
              <MenuGroup title="Markets">
                <MenuItem name="debentures">
                  <router-link to="/finsec/debentures">Debentures</router-link>
                </MenuItem>
              </MenuGroup>
            </Submenu>

            <Submenu name="admin">
              <template slot="title">
                <Icon type="ios-construct" />
                Admin
              </template>
              <MenuGroup title="Token Management">
                <MenuItem name="tokenManagement">
                  <router-link to="/tokenAdmin">Manage Tokens</router-link>
                </MenuItem>
              </MenuGroup>
              <MenuGroup title="User Management">
                <MenuItem name="addUser">Add User</MenuItem>
              </MenuGroup>
            </Submenu>

            <MenuItem name="logout" v-show="isLoggedIn" >
              <Icon type="ios-log-out"></Icon>
              Log out
            </MenuItem>

            <MenuItem name="login" v-show="!isLoggedIn">
              <Icon type="ios-log-in"></Icon>
              <router-link v-show="!isLoggedIn" to="/login">Login</router-link>
            </MenuItem>

          </div>
        </Menu>
      </Header>

      <Content :style="{padding: '0 19px'}">
<!--        <Breadcrumb :style="{margin: '20px 0'}">-->
<!--          <BreadcrumbItem v-model="path"></BreadcrumbItem>-->
<!--          <BreadcrumbItem>Components</BreadcrumbItem>-->
<!--          <BreadcrumbItem>Layout</BreadcrumbItem>-->
<!--        </Breadcrumb>-->
        <Card>
          <div style="min-height: 200px;">
            <router-view/>
          </div>
        </Card>
      </Content>

      <Footer class="layout-footer-right">&copy; {{getYear()}} Accounted4</Footer>

    </Layout>

  </div>

</template>


<script>

import { logout } from '@/js/auth.js';


export default {

  name: 'app-nav',

  data() {
    return {
      path: "Hello"
    }
  } ,

  methods: {

    menuHandler: function(event) {
      switch (event) {
        case 'logout':
          logout();
          //this.$router.push({ name: VIEW_NAME_HOME });
          break;
      }
      console.log(event);
    },

    getYear: function() {
       return (new Date()).getFullYear();
    }

  },

  computed: {

    isLoggedIn() {
      return this.$store.getters.inSession;
    }

  }

}


</script>


<style scoped>
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

.layout{
     border: 1px solid #d7dde4;
     background: #f5f7f9;
     position: relative;
     border-radius: 4px;
   }

.layout-logo{
  width: 30px;
  height: 38px;
  background: #ffffff;
  border-radius: 3px;
  float: left;
  position: relative;
  top: 12px;
  left: 5px;
}

.layout-nav{
  display: flex;
  justify-content: flex-end
}

.layout-footer-right{
  text-align: right;
}


</style>
