// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import BootstrapVue from "bootstrap-vue"
import App from './App'
import router from './router'
import store from './store'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import services from '@/app-service'

Vue.use(BootstrapVue);
Vue.config.productionTip = false;

// services.getHouseholds().then((h) => console.log(h));

new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: {App},
  methods: {},
});
