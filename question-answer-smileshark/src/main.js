import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import hljs from 'highlight.js'
import 'highlight.js/styles/default.css'

Vue.prototype.$http = axios
Vue.config.productionTip = false
Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.directive('highlight', (el) => {
  const blocks = el.querySelectorAll('pre code')
  blocks.forEach((block) => {
    hljs.highlightBlock(block)
  })
})

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
