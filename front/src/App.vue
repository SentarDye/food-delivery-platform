<!-- 技术栈：Vue 3, Element Plus, Vue Router -->
<template>
  <el-container class="app-container">
    <el-main>
      <router-view />
    </el-main>
    <el-footer v-if="showBottomNav" height="50px" class="bottom-nav">
      <el-row justify="space-around" align="middle">
        <el-col :span="6">
          <router-link to="/home" class="nav-item">
            <i class="el-icon-house"></i>
            <span>首页</span>
          </router-link>
        </el-col>
        <el-col :span="6">
          <router-link to="/cart" class="nav-item">
            <i class="el-icon-shopping-cart"></i>
            <span>购物车</span>
          </router-link>
        </el-col>
        <el-col :span="6">
          <router-link to="/orders" class="nav-item">
            <i class="el-icon-document"></i>
            <span>订单</span>
          </router-link>
        </el-col>
        <el-col :span="6">
          <router-link to="/profile" class="nav-item">
            <i class="el-icon-user"></i>
            <span>我的</span>
          </router-link>
        </el-col>
      </el-row>
    </el-footer>
  </el-container>
</template>

<script setup lang="ts">
// 技术栈：Vue 3 Composition API
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const showBottomNav = computed(() => {
  // 不需要底部导航的页面：登录、注册、下单等
  const hideNavPaths = ['/login', '/register', '/checkout']
  return !hideNavPaths.includes(route.path)
})
</script>

<style>
.app-container {
  max-width: 480px;
  margin: 0 auto;
  min-height: 100vh;
}
.bottom-nav {
  background: #fff;
  border-top: 1px solid #eee;
  position: fixed;
  bottom: 0;
  width: 100%;
  max-width: 480px;
  left: 50%;
  transform: translateX(-50%);
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #666;
  text-decoration: none;
}
.nav-item.router-link-active {
  color: #409eff;
}
</style>
