<!-- 技术栈：Vue 3, Element Plus, Pinia, Vue Router, TypeScript -->
<template>
  <div class="profile-page">
    <!-- 头部个人信息 -->
    <div class="profile-header">
      <el-avatar :size="64" :src="userInfo?.avatar || defaultAvatar" />
      <div class="user-name" v-if="userInfo">{{ userInfo.nickname || userInfo.phone }}</div>
      <div class="user-phone" v-if="userInfo">{{ userInfo.phone }}</div>
      <el-button
          v-else
          type="primary"
          round
          @click="router.push('/login')"
      >
        登录 / 注册
      </el-button>
    </div>

    <!-- 我的钱包/余额 -->
    <div class="wallet-card" v-if="userInfo">
      <div class="balance-label">我的余额</div>
      <div class="balance-value">¥{{ userInfo.balance || '0.00' }}</div>
    </div>

    <!-- 功能入口 -->
    <div class="menu-section">
      <div class="menu-item" @click="router.push('/orders')">
        <div class="menu-left">
          <el-icon><Document /></el-icon>
          <span>我的订单</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
      <div class="menu-item" @click="router.push('/address')">
        <div class="menu-left">
          <el-icon><Location /></el-icon>
          <span>收货地址</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
      <div class="menu-item" @click="router.push('/cart')">
        <div class="menu-left">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
    </div>

    <!-- 退出登录 -->
    <div class="logout-section" v-if="userInfo">
      <el-button
          type="danger"
          round
          plain
          class="logout-btn"
          @click="handleLogout"
      >
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document, ArrowRight, Location, ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref<any>(null)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 加载用户信息
const loadUserInfo = async () => {
  try {
    await userStore.fetchUserInfo()
    userInfo.value = userStore.userInfo
  } catch {
    // 未登录或请求失败
  }
}

const handleLogout = async () => {
  userStore.logout()
  userInfo.value = null
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 40px;
}
.profile-header {
  background: linear-gradient(135deg, #409eff, #2d6cdf);
  padding: 30px 20px 25px;
  text-align: center;
  color: #fff;
}
.user-name {
  margin-top: 10px;
  font-size: 18px;
  font-weight: 600;
}
.user-phone {
  margin-top: 4px;
  font-size: 14px;
  opacity: 0.8;
}
.wallet-card {
  background: #fff;
  margin: 10px 16px;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.balance-label {
  font-size: 14px;
  color: #606266;
}
.balance-value {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
}
.menu-section {
  background: #fff;
  margin: 10px 16px;
  border-radius: 8px;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
}
.logout-section {
  padding: 20px 16px;
  text-align: center;
}
.logout-btn {
  width: 80%;
}
</style>