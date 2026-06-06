// 技术栈：Pinia, TypeScript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken } from '../utils/auth'
import { userApi } from '../api/user'

export const useUserStore = defineStore('user', () => {
    const token = ref(getToken() || '')
    const userInfo = ref<any>(null)

    const login = async (phone: string, password: string) => {
        const res = await userApi.login(phone, password)
        token.value = res.data
        setToken(res.data)
        await fetchUserInfo()
    }

    const register = async (phone: string, password: string, nickname?: string) => {
        return await userApi.register(phone, password, nickname)
    }

    const fetchUserInfo = async () => {
        if (!token.value) return
        const res = await userApi.getInfo()
        userInfo.value = res.data
    }

    const logout = () => {
        token.value = ''
        userInfo.value = null
        removeToken()
    }

    return { token, userInfo, login, register, fetchUserInfo, logout }
})