<!-- 技术栈：Vue 3, Element Plus, Axios, TypeScript -->
<template>
  <div class="order-detail-page" v-loading="loading">
    <div class="header">
      <el-page-header @back="router.back" content="订单详情" />
    </div>

    <!-- 订单状态大卡片 -->
    <div class="status-card" v-if="order">
      <div class="status-icon">
        <el-icon :size="32" :color="statusIconColor">
          <component :is="statusIcon" />
        </el-icon>
      </div>
      <div class="status-text">{{ statusText(order.status) }}</div>
      <div class="status-desc" v-if="statusDesc">{{ statusDesc }}</div>
    </div>

    <!-- 配送地址 -->
    <div class="section" v-if="order">
      <div class="section-title">配送地址</div>
      <div class="address-info" v-if="order.address">
        <div class="contact">{{ order.address.receiverName }} {{ order.address.receiverPhone }}</div>
        <div class="detail">{{ order.address.province }}{{ order.address.city }}{{ order.address.district }} {{ order.address.detail }}</div>
      </div>
      <div v-else class="no-address">未关联地址</div>
    </div>

    <!-- 商品明细 -->
    <div class="section" v-if="order && items.length > 0">
      <div class="section-title">商品明细</div>
      <div class="item-list">
        <div v-for="item in items" :key="item.id" class="item">
          <el-image :src="item.image || defaultFoodImg" class="food-img" fit="cover" />
          <div class="food-info">
            <div class="food-name">{{ item.itemName }}</div>
            <div class="food-price">¥{{ item.price }}</div>
          </div>
          <span class="qty">x{{ item.quantity }}</span>
          <span class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <!-- 费用明细 -->
    <div class="section" v-if="order">
      <div class="section-title">费用明细</div>
      <div class="fee-row">
        <span>商品总额</span>
        <span>¥{{ order.totalAmount || 0 }}</span>
      </div>
      <div class="fee-row">
        <span>配送费</span>
        <span>¥{{ order.deliveryFee || 0 }}</span>
      </div>
      <div class="fee-row" v-if="order.discountAmount > 0">
        <span>优惠</span>
        <span class="discount">-¥{{ order.discountAmount }}</span>
      </div>
      <div class="fee-row total">
        <span>实付金额</span>
        <span class="final">¥{{ order.finalAmount || 0 }}</span>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="section" v-if="order">
      <div class="section-title">订单信息</div>
      <div class="info-row">
        <span>订单编号</span>
        <span>{{ order.orderNo || '-' }}</span>
      </div>
      <div class="info-row">
        <span>创建时间</span>
        <span>{{ order.createTime || '-' }}</span>
      </div>
      <div class="info-row" v-if="order.payTime">
        <span>支付时间</span>
        <span>{{ order.payTime }}</span>
      </div>
      <div class="info-row" v-if="order.deliveryTime">
        <span>送达时间</span>
        <span>{{ order.deliveryTime }}</span>
      </div>
      <div class="info-row" v-if="order.remark">
        <span>备注</span>
        <span>{{ order.remark }}</span>
      </div>
    </div>

    <!-- 底部操作 -->
    <div class="bottom-bar" v-if="order && order.status === 0">
      <el-button type="warning" round size="large" @click="handlePay">立即支付</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, Timer, Van, CircleCheck, CloseBold } from '@element-plus/icons-vue'
import { orderApi } from '../../api/order'

const route = useRoute()
const router = useRouter()
const orderId = Number(route.params.id)

const loading = ref(false)
const order = ref<any>(null)
const items = ref<any[]>([])
const defaultFoodImg = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9djpeg.jpeg'

// 状态文本
const statusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待支付', 1: '已支付', 2: '商家接单', 3: '配送中', 4: '已完成', 5: '已取消', 6: '退款中'
  }
  return map[status] || '未知'
}

// 状态图标
const statusIcon = computed(() => {
  const map: Record<number, any> = {
    0: Clock,
    1: Timer,
    2: Timer,
    3: Van,
    4: CircleCheck,
    5: CloseBold
  }
  return order.value ? (map[order.value.status] || Clock) : Clock
})

const statusIconColor = computed(() => {
  const map: Record<number, string> = {
    0: '#e6a23c',
    1: '#409eff',
    2: '#409eff',
    3: '#67c23a',
    4: '#67c23a',
    5: '#909399'
  }
  return order.value ? (map[order.value.status] || '#909399') : '#909399'
})

const statusDesc = computed(() => {
  if (!order.value) return ''
  if (order.value.status === 0) return '请尽快完成支付'
  if (order.value.status === 1) return '商家正在备餐'
  if (order.value.status === 2) return '骑手正在赶来'
  if (order.value.status === 3) return '骑手正在配送'
  if (order.value.status === 4) return '订单已完成，欢迎再次光临'
  if (order.value.status === 5) return '订单已取消'
  return ''
})

// 模拟数据
const mockOrder = {
  id: orderId,
  orderNo: 'a1b2c3d4e5f67890',
  status: 3,
  totalAmount: 38.9,
  deliveryFee: 3,
  discountAmount: 0,
  finalAmount: 41.9,
  createTime: '2026-06-06 12:30',
  payTime: '2026-06-06 12:35',
  deliveryTime: null,
  remark: '少辣',
  address: {
    receiverName: '张三',
    receiverPhone: '13800001111',
    province: '江苏省',
    city: '镇江市',
    district: '京口区',
    detail: '学府路301号'
  }
}
const mockItems = [
  { id: 1, itemName: '香辣鸡腿堡', price: 19.9, quantity: 1, image: '' },
  { id: 2, itemName: '薯条（中）', price: 9.9, quantity: 2, image: '' }
]

const fetchOrder = async () => {
  loading.value = true
  try {
    const res = await orderApi.detail(orderId)
    // 后端返回 { data: [orderObject, itemsArray] }
    const data = res.data
    if (Array.isArray(data) && data.length >= 2) {
      order.value = data[0]
      items.value = data[1]
    } else if (data && typeof data === 'object') {
      // 也可能直接返回对象，自适应
      order.value = data.order || data
      items.value = data.items || []
    }
  } catch {
    // 使用模拟数据
    order.value = mockOrder
    items.value = mockItems
  } finally {
    loading.value = false
  }
}

const handlePay = async () => {
  try {
    await orderApi.pay(orderId)
    ElMessage.success('支付成功')
    fetchOrder()
  } catch {
    // 错误提示在拦截器
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.order-detail-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px;
}
.header {
  background: #fff;
  padding: 10px 16px;
  margin-bottom: 8px;
}
.status-card {
  background: #fff;
  text-align: center;
  padding: 20px;
  margin-bottom: 10px;
}
.status-icon {
  margin-bottom: 8px;
}
.status-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.status-desc {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
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
  margin-bottom: 10px;
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
  font-size: 13px;
  color: #606266;
}
.no-address {
  color: #909399;
  font-size: 13px;
}
.item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 8px;
}
.food-img {
  width: 45px;
  height: 45px;
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
.subtotal {
  color: #f56c6c;
  font-weight: 500;
}
.fee-row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 14px;
}
.fee-row.total {
  font-weight: 600;
  font-size: 16px;
  border-top: 1px solid #f0f0f0;
  margin-top: 4px;
  padding-top: 10px;
}
.discount {
  color: #67c23a;
}
.final {
  color: #f56c6c;
}
.info-row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 13px;
  color: #606266;
}
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 480px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.1);
  padding: 10px 16px;
  border-radius: 12px 12px 0 0;
  z-index: 100;
}
.bottom-bar .el-button {
  width: 100%;
}
</style>