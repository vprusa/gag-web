<template>
  <div id="app">
    <b-navbar toggleable="md" type="dark" variant="info">
      <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
      <b-navbar-brand>RMS</b-navbar-brand>
      <b-collapse is-nav id="nav_collapse" v-if="user">
        <b-navbar-nav>
          <b-nav-item>
            <router-link :to="{ name: 'User', params: {id: user.id} }" tag="nav-item">User</router-link>
          </b-nav-item>


          <b-nav-item>
            <router-link :to="{ name: 'Households' }" tag="nav-item">Households</router-link>
          </b-nav-item>

             <b-nav-item>
            <router-link :to="{ name: 'ShoppingList' }" tag="nav-item">Shopping List</router-link>
          </b-nav-item>
        </b-navbar-nav>
      </b-collapse>
      <b-collapse is-nav id="nav_collapse" v-else>
        <!--<b-navbar-nav class="ml-auto">-->
          <!--<b-nav-item href="/user">Login</b-nav-item>-->
        <!--</b-navbar-nav>-->
      </b-collapse>
    </b-navbar>
    <router-view></router-view>
  </div>
</template>

<script>
  export default {
    name: 'App',

    data() {
      this.$store.commit('setUser', {
        id: 1,
        email: "Adam@gag.com",
        firstName: "Adam",
        lastName: "Mada",
        password: "password"
      });
      // this.$store.commit('loadHouseholds');
      // this.$store.commit('loadShoppingLists');

      return {
        user: this.$store.state.user,
      };
    },

    methods: {}
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
</style>
