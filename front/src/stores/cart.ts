// 技术栈：Pinia, Axios, TypeScript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { cartApi } from '../api/cart'

export const useCartStore = defineStore('cart', () => {
    const totalCount = ref(0)

    // 获取购物车总数量
    const fetchCartCount = async () => {
        try {
            const res = await cartApi.list()
            const items = res.data || []
            totalCount.value = items.reduce((sum: number, item: any) => sum + item.quantity, 0)
        } catch {
            totalCount.value = 0
        }
    }

    // 添加商品到购物车
    const addToCart = async (menuItemId: number, quantity: number = 1) => {
        await cartApi.add(menuItemId, quantity)
        await fetchCartCount()
    }

    // 清空本地计数（退出登录时）
    const resetCart = () => {
        totalCount.value = 0
    }

    return { totalCount, fetchCartCount, addToCart, resetCart }
})