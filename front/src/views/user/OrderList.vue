<!-- 技术栈：Vue 3, Element Plus, Axios, TypeScript -->
<template>
  <div class="order-list-page">
    <div class="header">
      <el-page-header @back="router.back" content="我的订单" />
    </div>

    <!-- 状态筛选栏 -->
    <div class="tabs-bar">
      <el-radio-group v-model="statusFilter" size="small" @change="fetchOrders">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button :value="0">待支付</el-radio-button>
        <el-radio-button :value="1">进行中</el-radio-button>
        <el-radio-button :value="4">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-loading="loading">
      <div v-if="filteredOrders.length === 0 && !loading" class="empty-tip">
        <el-empty description="暂无订单" />
      </div>
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="merchant-name">{{ order.merchantName }}</span>
          <el-tag :type="statusTagType(order.status)" size="small">
            {{ statusText(order.status) }}
          </el-tag>
        </div>
        <div class="order-body" @click="goDetail(order.id)">
          <div class="items-summary">
            <span v-for="(item, idx) in order.items" :key="idx">
              {{ item.itemName }} x{{ item.quantity }}
              <template v-if="idx < order.items.length - 1">、</template>
            </span>
          </div>
          <div class="order-info">
            <span class="total">¥{{ order.finalAmount }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="time">{{ order.createTime }}</span>
          <div class="actions">
            <el-button
                v-if="order.status === 0"
                type="warning"
                size="small"
                @click="handlePay(order.id)"
            >
              去支付
            </el-button>
            <el-button
                v-if="order.status === 4"
                type="primary"
                size="small"
                @click="goRating(order.id)"
            >
              评价
            </el-button>
            <el-button
                v-if="order.status === 4"
                size="small"
                @click="reorder(order)"
            >
              再来一单
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { orderApi } from '../../api/order'

const router = useRouter()

const loading = ref(false)
const orders = ref<any[]>([])
const statusFilter = ref('') // 空字符串表示全部

// 模拟数据（对接真实接口后替换）
const mockOrders = [
  {
    id: 1,
    merchantName: '肯德基宅急送',
    status: 4,
    finalAmount: 45.50,
    createTime: '2026-06-06 12:30',
    items: [
      { itemName: '香辣鸡腿堡', quantity: 1 },
      { itemName: '薯条（中）', quantity: 2 }
    ]
  },
  {
    id: 2,
    merchantName: '麦当劳麦乐送',
    status: 0,
    finalAmount: 32.00,
    createTime: '2026-06-06 13:00',
    items: [
      { itemName: '巨无霸套餐', quantity: 1 }
    ]
  }
]

// 状态文字映射
const statusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '商家接单',
    3: '配送中',
    4: '已完成',
    5: '已取消',
    6: '退款中'
  }
  return map[status] || '未知'
}

// 状态标签颜色
const statusTagType = (status: number): 'warning' | 'success' | 'info' | 'danger' => {
  if (status === 0) return 'warning'
  if (status === 4) return 'success'
  if (status === 5 || status === 6) return 'danger'
  return 'info'
}

// 过滤后的订单
const filteredOrders = computed(() => {
  if (statusFilter.value === '') return orders.value
  const target = Number(statusFilter.value)
  if (target === 1) {
    // 进行中：1已支付 2商家接单 3配送中
    return orders.value.filter(o => [1, 2, 3].includes(o.status))
  }
  return orders.value.filter(o => o.status === target)
})

// 获取订单数据
const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.list()
    orders.value = (res.data || []).map((order: any) => ({
      id: order.id,
      merchantName: order.merchantName || '商家',
      status: order.status,
      finalAmount: order.finalAmount || 0,
      createTime: order.createTime || '',
      items: order.items || []
    }))
  } catch {
    // 对接失败时使用模拟数据
    orders.value = mockOrders
  } finally {
    loading.value = false
  }
}

// 查看订单详情
const goDetail = (orderId: number) => {
  router.push(`/order/${orderId}`)
}

// 去支付
const handlePay = async (orderId: number) => {
  try {
    await orderApi.pay(orderId)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch {
    // 错误已提示
  }
}

// 去评价
const goRating = (orderId: number) => {
  router.push(`/rating?orderId=${orderId}`)
}

// 再来一单
const reorder = (order: any) => {
  // 将订单菜品重新加入购物车，此处简化跳转到商家页
  // 实际需调用批量加购接口，暂先提示
  ElMessage.info('再来一单功能开发中')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 60px;
}
.header {
  background: #fff;
  padding: 10px 16px;
  margin-bottom: 8px;
}
.tabs-bar {
  background: #fff;
  padding: 10px 16px;
  margin-bottom: 8px;
  overflow-x: auto;
}
.order-card {
  background: #fff;
  margin-bottom: 8px;
  padding: 12px 16px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.merchant-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
.items-summary {
  flex: 1;
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.total {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
  margin-left: 12px;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.time {
  font-size: 12px;
  color: #909399;
}
.actions {
  display: flex;
  gap: 8px;
}
.empty-tip {
  padding-top: 100px;
}
</style>