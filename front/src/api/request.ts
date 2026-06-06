// 技术栈：Axios, TypeScript, Element Plus
import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from '../utils/auth'
import router from '../router'

const instance: AxiosInstance = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 10000
})

// 请求拦截器
instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 尝试获取用户 token，如果没有则尝试商户 token
        let token = getToken()
        if (!token) {
            token = getToken('merchant_token')
        }
        if (token && config.headers) {
            config.headers['token'] = token
        }
        return config
    },
    (error) => Promise.reject(error)
)

// 响应拦截器
instance.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            if (res.code === 401) {
                removeToken()
                router.push('/login')
            }
            return Promise.reject(new Error(res.message))
        }
        return res
    },
    (error) => {
        ElMessage.error('网络错误')
        return Promise.reject(error)
    }
)

export default instance