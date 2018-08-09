// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Login from './Login.vue'
import router from './router'
import { Button,Message} from 'element-ui'

Vue.config.productionTip = false
Vue.use(Button)
Vue.prototype.$message = Message;
Vue.prototype.$ELEMENT = { size: 'small', zIndex: 3000 };


/* eslint-disable no-new */
new Vue({
  el: '#login',
  render: h => h(Login),
  router,
  components: { Login },
  template: '<Login/>'
})
