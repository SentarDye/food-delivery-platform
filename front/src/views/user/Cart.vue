<!-- 技术栈：Vue 3, Element Plus, Pinia, Axios, TypeScript -->
<template>
  <div class="cart-page" v-loading="loading">
    <div class="header">
      <el-page-header @back="router.back" content="购物车" />
    </div>

    <!-- 购物车列表 -->
    <div class="cart-list" v-if="cartItems.length > 0 && !loading">
      <div
          v-for="item in cartItems"
          :key="item.cartId"
          class="cart-item"
      >
        <el-checkbox
            :model-value="isChecked(item.cartId)"
            @change="(val: boolean) => toggleCheck(item.cartId, val)"
        />
        <el-image :src="item.image || defaultFoodImg" class="food-img" fit="cover" />
        <div class="food-detail">
          <div class="food-name">{{ item.name }}</div>
          <div class="food-price">¥{{ item.price }}</div>
        </div>
        <div class="qty-ctrl">
          <el-button size="small" circle @click="changeQty(item.cartId, item.quantity - 1)">
            <el-icon><Minus /></el-icon>
          </el-button>
          <span class="qty">{{ item.quantity }}</span>
          <el-button size="small" circle @click="changeQty(item.cartId, item.quantity + 1)">
            <el-icon><Plus /></el-icon>
          </el-button>
        </div>
        <el-button
            type="danger"
            size="small"
            circle
            @click="removeItem(item.cartId)"
        >
          <el-icon><Delete /></el-icon>
        </el-button>
      </div>
    </div>
    <el-empty v-if="cartItems.length === 0 && !loading" description="购物车是空的" />

    <!-- 底部结算栏 -->
    <div class="settle-bar" v-if="cartItems.length > 0">
      <div class="total">
        <span class="total-label">合计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
        <span class="delivery-hint" v-if="selectedItemsCount > 0">
          已选{{ selectedItemsCount }}件
        </span>
      </div>
      <el-button
          type="warning"
          round
          :disabled="selectedItemsCount === 0"
          @click="goCheckout"
      >
        去结算
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Minus, Plus, Delete } from '@element-plus/icons-vue'
import { cartApi } from '../../api/cart'
import { useCartStore } from '../../stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const defaultFoodImg = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9djpeg.jpeg'

const loading = ref(false)
// 购物车项结构（需后端返回带菜品详情的列表）
interface CartItem {
  cartId: number
  menuItemId: number
  merchantId: number
  name: string
  price: number
  image?: string
  quantity: number
}
const cartItems = ref<CartItem[]>([])
// 选中的购物车项ID集合
const checkedIds = ref<Set<number>>(new Set())

// 计算选中总价
const totalPrice = computed(() => {
  let sum = 0
  cartItems.value.forEach(item => {
    if (checkedIds.value.has(item.cartId)) {
      sum += item.price * item.quantity
    }
  })
  return sum.toFixed(2)
})
const selectedItemsCount = computed(() => checkedIds.value.size)

const isChecked = (cartId: number) => checkedIds.value.has(cartId)
const toggleCheck = (cartId: number, val: boolean) => {
  if (val) {
    checkedIds.value.add(cartId)
  } else {
    checkedIds.value.delete(cartId)
  }
}

// 修改数量
const changeQty = async (cartId: number, newQty: number) => {
  if (newQty < 0) return
  if (newQty === 0) {
    removeItem(cartId)
    return
  }
  try {
    await cartApi.update(cartId, newQty)
    const item = cartItems.value.find(i => i.cartId === cartId)
    if (item) item.quantity = newQty
    await cartStore.fetchCartCount()
  } catch {
    ElMessage.error('更新失败')
  }
}

// 删除商品
const removeItem = async (cartId: number) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await cartApi.remove(cartId)
    cartItems.value = cartItems.value.filter(i => i.cartId !== cartId)
    checkedIds.value.delete(cartId)
    await cartStore.fetchCartCount()
    ElMessage.success('删除成功')
  } catch {
    ElMessage.error('删除失败')
  }
}

// 加载购物车数据
const fetchCart = async () => {
  loading.value = true
  try {
    // 预期后端返回包含菜品详情的列表，当前可能只返回基本字段，需后续对接真实接口
    const res = await cartApi.list()
    // 假设接口返回的是带 name, price 的对象，如果没有则需前端补充（暂时做空值保护）
    cartItems.value = (res.data || []).map((item: any) => ({
      cartId: item.id,          // 购物车记录 ID
      menuItemId: item.menuItemId,
      merchantId: item.merchantId,
      name: item.itemName || item.name || '菜品',
      price: item.price || 0,
      image: item.image || '',
      quantity: item.quantity
    }))
    // 默认全选
    checkedIds.value = new Set(cartItems.value.map(i => i.cartId))
  } catch {
    // 忽略
  } finally {
    loading.value = false
  }
}

const goCheckout = () => {
  // 将选中的 cartId 传递给下单页（通过路由 query 或 store）
  // 简单起见：将选中的项 id 存入 store 或使用路由参数，这里直接跳转，下单页重新拉取选中的数据
  router.push('/checkout')
}

onMounted(() => {
  fetchCart()
})
</script>

<style scoped>
.cart-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 70px;
}
.header {
  background: #fff;
  padding: 10px 16px;
  margin-bottom: 8px;
}
.cart-list {
  background: #fff;
  padding: 0 16px;
}
.cart-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 10px;
}
.food-img {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  flex-shrink: 0;
}
.food-detail {
  flex: 1;
}
.food-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
.food-price {
  font-size: 14px;
  color: #f56c6c;
  margin-top: 4px;
}
.qty-ctrl {
  display: flex;
  align-items: center;
  gap: 6px;
}
.qty {
  min-width: 20px;
  text-align: center;
  font-size: 14px;
}
.settle-bar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 480px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  border-radius: 12px 12px 0 0;
  z-index: 100;
}
.total {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.total-label {
  font-size: 14px;
  color: #303133;
}
.total-price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}
.delivery-hint {
  font-size: 11px;
  color: #909399;
  margin-left: 6px;
}
</style>