<template>
  <div class="dashboard">
    <div class="header">
      <h2>商家中心</h2>
      <el-button @click="logout">退出登录</el-button>
    </div>
    <div class="stats">
      <el-card>
        <template #header>今日订单</template>
        <div class="stat-value">{{ todayOrders }}</div>
      </el-card>
      <el-card>
        <template #header>今日营业额</template>
        <div class="stat-value">¥{{ todayAmount }}</div>
      </el-card>
    </div>
    <div class="actions">
      <el-button type="primary" @click="router.push('/merchant/menu')">菜品管理</el-button>
      <el-button type="warning" @click="router.push('/merchant/orders')">订单管理</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMerchantStore } from '../../stores/merchant'

const router = useRouter()
const merchantStore = useMerchantStore()
const todayOrders = ref(0)
const todayAmount = ref('0.00')

const logout = () => {
  merchantStore.logout()
  router.push('/merchant/login')
}

// TODO: 从后端获取真实统计数据，暂时模拟
onMounted(() => {
  todayOrders.value = 12
  todayAmount.value = '328.50'
  merchantStore.fetchInfo()
})
</script>

<style scoped>
.dashboard {
  padding: 16px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stats {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}
.stats .el-card {
  flex: 1;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
}
.actions {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}
</style>