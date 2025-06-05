/* eslint-disable */
import axios from 'axios'
import router from "@/router/index"

const axiosInstance = axios.create({
    // baseURL: window.location.origin,
    timeout: 60*1000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json',
        // 其他默认的头部信息
    }
});

// 添加请求拦截器
axiosInstance.interceptors.request.use(
    config => {
        const jwtToken = localStorage.getItem('token')
        if (jwtToken) {
            config.headers['Authorization'] = jwtToken
        }
        return config;
    },
    error => {
        return Promise.reject(error)
    }
);
axiosInstance.interceptors.response.use(
    config=>{
        if(config.data.code==401){
            setTimeout(()=>{
                localStorage.removeItem('token')
                router.push('/login')
            },1000)
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

export default axiosInstance
