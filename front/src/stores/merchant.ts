// 技术栈：Pinia, Axios, TypeScript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken } from '../utils/auth'
import request from '../api/request'

export const useMerchantStore = defineStore('merchant', () => {
    const token = ref(getToken('merchant_token') || '')
    const merchantInfo = ref<any>(null)

    // 商家登录
    const login = async (phone: string, password: string) => {
        const res = await request.post('/merchant/login', null, { params: { phone, password } })
        token.value = res.data
        setToken('merchant_token', res.data)
        await fetchInfo()
    }

    // 商家注册
    const register = async (name: string, phone: string, password: string, address?: string) => {
        return await request.post('/merchant/register', null, { params: { name, phone, password, address } })
    }

    // 获取商家信息
    const fetchInfo = async () => {
        if (!token.value) return
        const res = await request.get('/merchant/info')
        merchantInfo.value = res.data
    }

    // 退出
    const logout = () => {
        token.value = ''
        merchantInfo.value = null
        removeToken('merchant_token')
    }

    return { token, merchantInfo, login, register, fetchInfo, logout }
})