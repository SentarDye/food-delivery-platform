<!-- 技术栈：Vue 3, Element Plus, Axios, Vue Router, TypeScript -->
<template>
  <div class="checkout-page" v-loading="loading">
    <!-- 顶部返回 -->
    <div class="header">
      <el-page-header @back="router.back" content="确认订单" />
    </div>

    <!-- 收货地址 -->
    <div class="section address-section">
      <div class="section-title">收货地址</div>
      <div v-if="addresses.length === 0" class="empty-address" @click="goAddAddress">
        + 添加收货地址
      </div>
      <div v-else class="address-card" @click="showAddressPicker = true">
        <div class="address-info">
          <span class="contact">{{ selectedAddress?.receiverName }} {{ selectedAddress?.receiverPhone }}</span>
          <span class="detail">{{ selectedAddress?.province }}{{ selectedAddress?.city }}{{ selectedAddress?.district }} {{ selectedAddress?.detail }}</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 订单明细 -->
    <div class="section">
      <div class="section-title">订单明细</div>
      <div v-for="item in cartItems" :key="item.cartId" class="order-item">
        <el-checkbox v-model="item.checked" />
        <el-image :src="item.image || defaultFoodImg" class="food-img" fit="cover" />
        <div class="food-info">
          <div class="food-name">{{ item.name }}</div>
          <div class="food-price">¥{{ item.price }}</div>
        </div>
        <span class="qty">x{{ item.quantity }}</span>
        <span class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
      </div>
      <div v-if="cartItems.length === 0 && !loading" class="empty-tip">暂无可结算的商品</div>
    </div>

    <!-- 优惠券 -->
    <div class="section coupon-section" v-if="coupons.length > 0">
      <div class="section-title">优惠券</div>
      <div class="coupon-list">
        <div
            v-for="coupon in coupons"
            :key="coupon.id"
            :class="['coupon-card', { selected: selectedCouponId === coupon.id }]"
            @click="selectCoupon(coupon.id)"
        >
          <div class="coupon-name">{{ coupon.name }}</div>
          <div class="coupon-desc">{{ coupon.desc }}</div>
        </div>
      </div>
      <div v-if="selectedCouponId" class="selected-coupon">
        已选：{{ coupons.find(c => c.id === selectedCouponId)?.name }}
        <el-button type="danger" link @click="selectedCouponId = null">不使用</el-button>
      </div>
    </div>

    <!-- 备注 -->
    <div class="section">
      <div class="section-title">备注</div>
      <el-input
          v-model="remark"
          type="textarea"
          :rows="2"
          placeholder="选填，如口味偏好"
      />
    </div>

    <!-- 价格明细 -->
    <div class="section price-summary">
      <div class="price-row">
        <span>商品总额</span>
        <span>¥{{ goodsTotal }}</span>
      </div>
      <div class="price-row">
        <span>配送费</span>
        <span>¥{{ deliveryFee }}</span>
      </div>
      <div class="price-row" v-if="discountAmount > 0">
        <span>优惠</span>
        <span class="discount">-¥{{ discountAmount }}</span>
      </div>
      <div class="price-row total">
        <span>实付金额</span>
        <span class="final">¥{{ finalAmount }}</span>
      </div>
    </div>

    <!-- 底部提交 -->
    <div class="submit-bar">
      <div class="total-info">
        <span>合计：</span><span class="final-price">¥{{ finalAmount }}</span>
      </div>
      <el-button
          type="warning"
          round
          size="large"
          :disabled="!canSubmit"
          :loading="submitting"
          @click="submitOrder"
      >
        提交订单
      </el-button>
    </div>

    <!-- 地址选择弹窗 -->
    <el-drawer
        v-model="showAddressPicker"
        title="选择收货地址"
        direction="btt"
        size="50%"
    >
      <div class="address-list">
        <div
            v-for="addr in addresses"
            :key="addr.id"
            :class="['addr-item', { active: selectedAddress?.id === addr.id }]"
            @click="selectAddress(addr)"
        >
          <div class="addr-contact">{{ addr.receiverName }} {{ addr.receiverPhone }}</div>
          <div class="addr-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</div>
          <el-icon v-if="selectedAddress?.id === addr.id" color="#409eff"><Check /></el-icon>
        </div>
        <el-button type="primary" round class="add-addr-btn" @click="goAddAddress">新增地址</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight, Check } from '@element-plus/icons-vue'
import { addressApi } from '../../api/address'
import { cartApi } from '../../api/cart'
import { orderApi } from '../../api/order'
// import { couponApi } from '../../api/coupon' // 暂时模拟

const router = useRouter()

const loading = ref(false)
const submitting = ref(false)

// 地址
const addresses = ref<any[]>([])
const selectedAddress = ref<any>(null)
const showAddressPicker = ref(false)

// 购物车商品（结算页展示）
interface CartItemEx {
  cartId: number
  menuItemId: number
  merchantId: number
  name: string
  price: number
  image?: string
  quantity: number
  checked: boolean
}
const cartItems = ref<CartItemEx[]>([])

// 优惠券（模拟）
const coupons = ref<any[]>([
  { id: 1, name: '新用户满20减5', desc: '满20元可用', value: 5, minAmount: 20 },
  { id: 2, name: '全场9折', desc: '无门槛', value: 10, minAmount: 0 }
])
const selectedCouponId = ref<number | null>(null)

// 备注
const remark = ref('')

// 配送费（应取商家信息，这里模拟）
const deliveryFee = ref(3)

const defaultFoodImg = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9djpeg.jpeg'

// 计算选中商品总价
const goodsTotal = computed(() => {
  let sum = 0
  cartItems.value.forEach(item => {
    if (item.checked) sum += item.price * item.quantity
  })
  return sum.toFixed(2)
})

// 计算优惠金额
const discountAmount = computed(() => {
  if (!selectedCouponId.value) return '0.00'
  const coupon = coupons.value.find(c => c.id === selectedCouponId.value)
  if (!coupon) return '0.00'
  const total = parseFloat(goodsTotal.value)
  if (total < coupon.minAmount) return '0.00'
  if (coupon.value <= 1) {
    // 折扣
    return (total * (1 - coupon.value)).toFixed(2)
  }
  // 固定减
  return Math.min(coupon.value, total).toFixed(2)
})

const finalAmount = computed(() => {
  const total = parseFloat(goodsTotal.value) + parseFloat(deliveryFee.value.toString())
  const discount = parseFloat(discountAmount.value)
  return Math.max(0, total - discount).toFixed(2)
})

const canSubmit = computed(() => {
  return selectedAddress.value !== null && cartItems.value.some(item => item.checked)
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 1. 获取地址
    const addrRes = await addressApi.list()
    addresses.value = addrRes.data || []
    if (addresses.value.length > 0) {
      const defaultAddr = addresses.value.find((a: any) => a.isDefault === 1) || addresses.value[0]
      selectedAddress.value = defaultAddr
    }

    // 2. 获取购物车（全部）
    const cartRes = await cartApi.list()
    cartItems.value = (cartRes.data || []).map((item: any) => ({
      cartId: item.id,
      menuItemId: item.menuItemId,
      merchantId: item.merchantId,
      name: item.itemName || item.name || '菜品',
      price: item.price || 0,
      image: item.image || '',
      quantity: item.quantity,
      checked: true
    }))

    // 3. 获取可用优惠券（暂用模拟）
    // const couponRes = await couponApi.getAvailable()
    // coupons.value = couponRes.data || []
  } catch (e) {
    // 忽略
  } finally {
    loading.value = false
  }
}

// 选择地址
const selectAddress = (addr: any) => {
  selectedAddress.value = addr
  showAddressPicker.value = false
}

// 选择优惠券
const selectCoupon = (couponId: number) => {
  if (selectedCouponId.value === couponId) {
    selectedCouponId.value = null
  } else {
    selectedCouponId.value = couponId
  }
}

// 新增地址
const goAddAddress = () => {
  router.push('/address')
}

// 提交订单
const submitOrder = async () => {
  if (!canSubmit.value) return
  try {
    submitting.value = true
    await orderApi.place(
        selectedAddress.value.id,
        selectedCouponId.value || undefined,
        remark.value || undefined
    )
    ElMessage.success('下单成功')
    router.push('/orders')
  } catch (error) {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.checkout-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px;
}
.header {
  background: #fff;
  padding: 10px 16px;
  margin-bottom: 8px;
}
.section {
  background: #fff;
  margin-bottom: 10px;
  padding: 12px 16px;
}
.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}
.address-section {
  display: flex;
  justify-content: space-between;
}
.address-card {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex: 1;
}
.address-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.contact {
  font-size: 14px;
  color: #303133;
}
.detail {
  font-size: 12px;
  color: #909399;
}
.empty-address {
  text-align: center;
  color: #409eff;
  padding: 10px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
}
.order-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 8px;
}
.food-img {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  flex-shrink: 0;
}
.food-info {
  flex: 1;
}
.food-name {
  font-size: 14px;
}
.food-price {
  font-size: 12px;
  color: #909399;
}
.qty {
  margin: 0 8px;
  color: #909399;
}
.item-total {
  color: #f56c6c;
  font-weight: 500;
}
.coupon-section {
}
.coupon-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.coupon-card {
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
}
.coupon-card.selected {
  border-color: #409eff;
  background: #ecf5ff;
}
.coupon-name {
  font-size: 14px;
  color: #303133;
}
.coupon-desc {
  font-size: 12px;
  color: #909399;
}
.selected-coupon {
  margin-top: 8px;
  font-size: 13px;
  color: #409eff;
}
.price-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.price-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}
.price-row.total {
  font-weight: 600;
  font-size: 16px;
}
.discount {
  color: #67c23a;
}
.final {
  color: #f56c6c;
}
.submit-bar {
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
.total-info {
  display: flex;
  align-items: baseline;
}
.final-price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}
.add-addr-btn {
  width: 100%;
  margin-top: 16px;
}
.address-list {
  padding: 16px;
}
.addr-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}
.addr-item.active {
  background: #f0f9eb;
}
.addr-contact {
  font-weight: 500;
}
.addr-detail {
  font-size: 13px;
  color: #909399;
}
.empty-tip {
  text-align: center;
  color: #909399;
  padding: 20px;
}
</style>