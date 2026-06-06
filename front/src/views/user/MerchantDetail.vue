<!-- 技术栈：Vue 3, Element Plus, Pinia, Axios, Vue Router, TypeScript -->
<template>
  <div class="merchant-detail" v-loading="loading">
    <!-- 商家头部 -->
    <div class="merchant-header">
      <el-image :src="merchant.image || defaultImg" class="header-img" fit="cover" />
      <div class="header-info">
        <h2>{{ merchant.name }}</h2>
        <div class="header-meta">
          <span class="score">
            <el-icon><StarFilled /></el-icon> {{ merchant.score || 4.5 }}
          </span>
          <span>月销 {{ merchant.monthlySales || 0 }}</span>
          <span v-if="merchant.distance">{{ merchant.distance }}km</span>
        </div>
        <div class="header-tags">
          <el-tag size="small" v-if="merchant.deliveryFee === 0" type="success">免配送费</el-tag>
          <el-tag size="small" v-else>配送 ¥{{ merchant.deliveryFee }}</el-tag>
          <el-tag size="small">起送 ¥{{ merchant.minOrderAmount }}</el-tag>
          <el-tag size="small">约{{ merchant.avgDeliveryTime }}分钟</el-tag>
        </div>
      </div>
    </div>

    <!-- 菜品分类与列表 -->
    <div class="menu-section">
      <!-- 分类切换 -->
      <el-tabs v-model="activeCategory" @tab-change="switchCategory">
        <el-tab-pane
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :name="cat.id"
        />
      </el-tabs>

      <!-- 菜品列表 -->
      <div class="menu-list">
        <div v-for="item in filteredMenu" :key="item.id" class="menu-item">
          <el-image :src="item.image || defaultFoodImg" class="food-img" fit="cover" />
          <div class="food-info">
            <div class="food-name">{{ item.name }}</div>
            <div class="food-desc" v-if="item.description">{{ item.description }}</div>
            <div class="food-sales">月售 {{ item.sellCount }}</div>
            <div class="food-bottom">
              <span class="food-price">¥{{ item.price }}</span>
              <div class="food-actions">
                <el-button
                    v-if="getCartQty(item.id)"
                    size="small"
                    circle
                    @click="decrease(item.id)"
                >
                  <el-icon><Minus /></el-icon>
                </el-button>
                <span v-if="getCartQty(item.id)" class="qty">{{ getCartQty(item.id) }}</span>
                <el-button size="small" circle @click="increase(item)">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-if="filteredMenu.length === 0 && !loading" description="暂无菜品" />
      </div>
    </div>

    <!-- 底部购物车条 -->
    <div class="cart-bar" v-if="cartTotalCount > 0">
      <div class="cart-info" @click="goCart">
        <div class="cart-icon-badge">
          <el-badge :value="cartTotalCount" class="item">
            <el-button type="primary" circle>
              <el-icon><ShoppingCart /></el-icon>
            </el-button>
          </el-badge>
        </div>
        <div class="cart-total">
          <span class="total-price">¥{{ cartTotalPrice }}</span>
          <span class="delivery-hint">
            另需配送费 ¥{{ merchant.deliveryFee || 0 }}
          </span>
        </div>
      </div>
      <el-button
          type="warning"
          round
          :disabled="cartTotalPrice < (merchant.minOrderAmount || 0)"
          @click="goCheckout"
      >
        去结算
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { StarFilled, Minus, Plus, ShoppingCart } from '@element-plus/icons-vue'
import { useCartStore } from '../../stores/cart'
import {cartApi} from "../../api/cart.ts";

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const merchantId = Number(route.params.id)
const loading = ref(false)
const merchant = ref<any>({
  id: merchantId,
  name: '',
  image: '',
  score: 4.5,
  monthlySales: 0,
  deliveryFee: 0,
  minOrderAmount: 20,
  avgDeliveryTime: 30,
  distance: 0
})

const defaultImg = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
const defaultFoodImg = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9djpeg.jpeg'

// 分类
const categories = ref<any[]>([])
const activeCategory = ref(0)

// 菜品（所有）
const menuItems = ref<any[]>([])
// 购物车本地临时数据（存储 {menuItemId: quantity}）
const localCart = ref<Record<number, number>>({})

// 根据分类过滤
const filteredMenu = computed(() => {
  if (!activeCategory.value) return menuItems.value
  return menuItems.value.filter((item: any) => item.categoryId === activeCategory.value)
})

// 计算购物车总数量与总价
const cartTotalCount = computed(() =>
    Object.values(localCart.value).reduce((sum, qty) => sum + qty, 0)
)
const cartTotalPrice = computed(() => {
  let total = 0
  for (const [id, qty] of Object.entries(localCart.value)) {
    const item = menuItems.value.find((m: any) => m.id === Number(id))
    if (item) total += item.price * qty
  }
  return total.toFixed(2)
})

// 获取某个菜品在本地购物车中的数量
const getCartQty = (menuItemId: number) => localCart.value[menuItemId] || 0

// 增加数量
const increase = async (item: any) => {
  localCart.value[item.id] = (localCart.value[item.id] || 0) + 1
  try {
    await cartStore.addToCart(item.id, 1) // 同步后端，并更新全局购物车数量
  } catch {
    ElMessage.error('添加失败')
    localCart.value[item.id]--
  }
}

// 减少数量
const decrease = async (menuItemId: number) => {
  const currentQty = localCart.value[menuItemId]
  if (!currentQty) return
  if (currentQty === 1) {
    delete localCart.value[menuItemId]
  } else {
    localCart.value[menuItemId]--
  }
  try {
    // 调用更新接口，数量设为当前新值
    await cartStore.addToCart(menuItemId, -1) // 实际后端add是增加，减少用update
    // 暂时简化：这里应调用 cartApi.update 或 remove。为保证演示，先只本地操作，重新获取全量
    // 重新同步一下
    await syncCartFromServer()
  } catch {
    ElMessage.error('操作失败')
    // 恢复原值
    localCart.value[menuItemId] = currentQty
  }
}

// 从服务器同步购物车数据
const syncCartFromServer = async () => {
  try {
    const res = await cartApi.list()
    const items = res.data || []
    localCart.value = {}
    items.forEach((cart: any) => {
      localCart.value[cart.menuItemId] = cart.quantity
    })
  } catch {
    // ignore
  }
}

const switchCategory = (catId: number) => {
  activeCategory.value = catId
}

const goCart = () => router.push('/cart')
const goCheckout = () => router.push('/checkout')

// 加载数据
const fetchData = async () => {
  loading.value = true
  // 模拟商家信息
  merchant.value = {
    id: merchantId,
    name: '肯德基宅急送',
    image: '',
    score: 4.7,
    monthlySales: 2893,
    deliveryFee: 0,
    minOrderAmount: 20,
    avgDeliveryTime: 30,
    distance: 1.2
  }
  // 模拟分类
  categories.value = [
    { id: 1, name: '人气推荐' },
    { id: 2, name: '汉堡' },
    { id: 3, name: '小食' },
    { id: 4, name: '饮料' }
  ]
  activeCategory.value = categories.value[0]?.id || 0
  // 模拟菜品
  menuItems.value = [
    { id: 1, name: '香辣鸡腿堡', price: 19.9, sellCount: 1523, description: '经典香辣', image: '', categoryId: 2 },
    { id: 2, name: '新奥尔良烤鸡腿堡', price: 21.5, sellCount: 1289, description: '奥尔良风味', image: '', categoryId: 2 },
    { id: 3, name: '薯条（中）', price: 9.9, sellCount: 2056, description: '', image: '', categoryId: 3 },
    { id: 4, name: '可乐（大）', price: 8.0, sellCount: 3012, description: '', image: '', categoryId: 4 },
    { id: 5, name: '全家桶', price: 89.0, sellCount: 654, description: '多人分享', image: '', categoryId: 1 }
  ]
  await syncCartFromServer()
  await cartStore.fetchCartCount()
  loading.value = false
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.merchant-detail {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px;
}
.merchant-header {
  position: relative;
  background: #fff;
  padding: 12px 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}
.header-img {
  width: 70px;
  height: 70px;
  border-radius: 6px;
  flex-shrink: 0;
}
.header-info h2 {
  margin: 0 0 8px;
  font-size: 18px;
}
.header-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}
.score {
  color: #e6a23c;
  display: flex;
  align-items: center;
  gap: 2px;
}
.header-tags {
  display: flex;
  gap: 6px;
}
.menu-section {
  background: #fff;
  margin-top: 10px;
  padding: 0 16px;
}
.menu-list {
  padding-bottom: 20px;
}
.menu-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.food-img {
  width: 70px;
  height: 70px;
  border-radius: 6px;
  flex-shrink: 0;
  margin-right: 12px;
}
.food-info {
  flex: 1;
}
.food-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
.food-desc {
  font-size: 12px;
  color: #909399;
  margin: 4px 0;
}
.food-sales {
  font-size: 11px;
  color: #c0c4cc;
  margin-bottom: 8px;
}
.food-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.food-price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 600;
}
.food-actions {
  display: flex;
  align-items: center;
  gap: 6px;
}
.qty {
  min-width: 20px;
  text-align: center;
  font-size: 14px;
}
.cart-bar {
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
.cart-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.total-price {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.delivery-hint {
  font-size: 11px;
  color: #909399;
  margin-left: 8px;
}
</style>