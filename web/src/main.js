import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd, {notification} from 'ant-design-vue';
// 引入 ant-design-vue
import 'ant-design-vue/dist/antd.css';
// 引入图标
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';
import './assets/js/enums'

const app = createApp(App);
app.use(Antd).use(store).use(router).mount('#app')

// 全局使用图标
const icons = Icons;
for (const i in icons) {
    app.component(i, icons[i])
}

/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
    console.log('请求参数：', config);
    // 为请求添加token
    // 获取 sessionStorage中用户信息的token
    const token = store.state.member.token
    if (token){
        config.headers.token = token;
        console.log("请求headers增加token:",token)
    }
    return config;
}, error => {
    return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
    console.log('返回结果：', response);
    return response;
}, error => {
    console.log('返回错误：', error);
    const response = error.response;
    const status = response.status;
    if (status === 401){
        // 如果错误为401则未登录，需要跳转到登录页面
        console.log("未登录或登录超时,跳到登录页");
        store.commit("setMember",{})
        notification.error({description:"未登录或登录超时"});
        router.push('/login');
    }
    return error;
});

//  所有的请求加上 http://ip:port  方便我们同一管理
axios.defaults.baseURL = process.env.VUE_APP_SERVER;
console.log('环境',process.env.NODE_ENV);
console.log('服务端',process.env.VUE_APP_SERVER)

