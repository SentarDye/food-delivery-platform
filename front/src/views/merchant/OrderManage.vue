<template>
  <div class="order-manage">
    <div class="header">
      <el-page-header @back="router.back" content="订单管理" />
    </div>
    <el-table :data="orderList" v-loading="loading">
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="finalAmount" label="金额" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button
              v-if="row.status === 1"
              type="primary"
              size="small"
              @click="acceptOrder(row.id)"
          >
            接单
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../api/request'

const router = useRouter()
const loading = ref(false)
const orderList = ref<any[]>([])

const statusText = (status: number) => {
  const map: Record<number, string> = { 0: '待支付', 1: '已支付', 2: '商家接单', 3: '配送中', 4: '已完成', 5: '已取消' }
  return map[status] || '未知'
}
const statusTag = (status: number): string => {
  if (status === 1) return 'warning'
  if (status === 2) return 'primary'
  if (status === 3) return 'success'
  return 'info'
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/merchant/list')
    orderList.value = res.data || []
  } catch {
    // error
  } finally {
    loading.value = false
  }
}

const acceptOrder = async (orderId: number) => {
  try {
    await request.post('/order/merchant/accept', null, { params: { orderId } })
    ElMessage.success('已接单')
    fetchOrders()
  } catch {
    // error
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-manage {
  padding: 16px;
}
.header {
  margin-bottom: 16px;
}
</style>